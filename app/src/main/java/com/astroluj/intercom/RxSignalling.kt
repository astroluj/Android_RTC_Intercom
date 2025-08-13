package com.astroluj.intercom

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.net.ConnectException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

/**
 * 내부 망을 통해서 통신을 할 때
 */
abstract class RxSignalling {
    private val serverTimeOut = 3000
    private val socketTimeOut = 10000

    private var disposables: CompositeDisposable? = null

    // 시그날링으로 주고 받을 데이터를 받을 서버 생성
    fun startSignalling(myPort: Int) {
        val observable = Observable.create<String> { emitter ->
            // 서버를 생성
            ServerSocket().use { serverSocket ->
                serverSocket.soTimeout = serverTimeOut
                serverSocket.reuseAddress = true
                serverSocket.bind(InetSocketAddress(myPort))

                while (!emitter.isDisposed) {
                    try {
                        val socket = serverSocket.accept()
                        socket.use {
                            // 연결 하려는 소켓 연결
                            it.getInputStream().use { input ->
                                val text = input.bufferedReader().readText()
                                // 시그날 발행
                                emitter.onNext(text)
                            }
                        }
                    } catch (e: Exception) {}
                }
            }
        }

        val disposable = observable
            // 내부 스케줄러
            .subscribeOn(Schedulers.io())
            // ui 스케줄러
            .observeOn(AndroidSchedulers.mainThread())
            // 구독 결과
            .subscribe(this::onRxReceive, this::onRxError)

        if (this.disposables == null) this.disposables = CompositeDisposable()
        disposables?.add(disposable)
    }

    fun sendPacket(packet: JSONObject, partnerIP: String, partnerPort: Int) {
        val action = Completable.fromAction {
            Socket().use {
                it.soTimeout = socketTimeOut
                it.connect(InetSocketAddress(partnerIP, partnerPort), socketTimeOut)
                it.getOutputStream().write(packet.toString().toByteArray())
            }
        }

        val disposable = action.retry { error -> error is ConnectException }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, this::onRxError)

        if (this.disposables == null) this.disposables = CompositeDisposable()
        disposables?.add(disposable)
    }

    open fun stop() {
        this.disposables?.clear()
    }

    open fun release () {
        this.stop()
        this.disposables?.dispose()
        this.disposables = null
    }

    abstract fun onRxReceive(json: String)
    abstract fun onRxError(error: Throwable)
}