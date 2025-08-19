package com.astroluj.intercom

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.Camera1Enumerator
import org.webrtc.CapturerObserver
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.NV21Buffer
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SessionDescription
import org.webrtc.Size
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoCapturer
import org.webrtc.VideoFrame
import org.webrtc.VideoTrack
import java.util.concurrent.TimeUnit

abstract class RTCIntercom (private val context: Context, private val fps: Int = 20, private val size: Size = Size(640, 480)) {

    companion object {
        const val KEY_TYPE = "type"
        val KEY_OFFER = SessionDescription.Type.OFFER.canonicalForm()
        val KEY_ANSWER = SessionDescription.Type.ANSWER.canonicalForm()
        const val KEY_CANDIDATE = "candidate"
        const val KEY_SDP = "sdp"
        const val KEY_SDP_INDEX = "sdpMLineIndex"
        const val KEY_SDP_MID = "sdpMid"
        const val DEFAULT_PORT = 6001
    }

    var isRunning = false
        private set

    /*
     * video config
     */
    // 비디오 사용시 리소스
    private var localCapture: VideoCapturer? = null
    // 내 화면
    private var localView: SurfaceViewRenderer? = null
    private var localVideoTrack: VideoTrack? = null
    // 상대방 화면
    private var remoteView: SurfaceViewRenderer? = null
    private var remoteVideoTrack: VideoTrack? = null
    // 데이터 관련
    private var captureObserver: CapturerObserver? = null

    // 연결자
    private var peerConnection: PeerConnection? = null

    // 구글 스턴 서버
    private var googleStunServer: String = ""
    // Turn server
    private var customTurnServerURL: String = ""
    private var customTurnServerID: String = ""
    private var customTurnServerPW: String = ""


    // rtc session protocol
    private val sessionObserver by lazy {
        object: SessionObserver() {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)
                // {"type:"offer or answer", "sdp":"!@#$!@#%$", "sdpMLineIndex": "!@#", "sdpMid": int}
                // 파트너와 주고받을 데이터
                try {
                    val packet = JSONObject()
                        .put(KEY_SDP, sessionDescription.description)
                        .put(KEY_TYPE, sessionDescription.type.canonicalForm())
                    // 로컬 설정
                    peerConnection?.setLocalDescription(this, sessionDescription)
                    // 파트너에게 전송
                    onPacketSignalling(packet.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                    onError(e)
                }
            }
        }
    }
    // rtc peer connection
    private val peerObserver by lazy {
        object: PeerObserver() {
            override fun onIceCandidate(iceCandidate: IceCandidate) {
                // {"type:"candidate", "candidate":"candidate:!@@$#%$@#", "sdpMLineIndex": "!@#", "sdpMid": int}
                try {
                    val packet = JSONObject()
                    // 파트너와 주고받을 데이터
                    packet.put(KEY_CANDIDATE, iceCandidate.sdp)
                    packet.put(KEY_SDP_INDEX, iceCandidate.sdpMLineIndex)
                    packet.put(KEY_SDP_MID, iceCandidate.sdpMid)
                    packet.put(KEY_TYPE, KEY_CANDIDATE)

                    // 파트너에게 전송
                    onPacketSignalling(packet.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                    onError(e)
                }
            }

            override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {
                when (state) {
                    PeerConnection.IceConnectionState.DISCONNECTED,
                    PeerConnection.IceConnectionState.CLOSED -> {
                        // 연결 끊김
                        onDisconnected()
                    }
                    PeerConnection.IceConnectionState.CONNECTED -> {
                        // 연결 됨
                        onConnected()
                    }
                    else -> {}
                }
            }
        }
    }

    // volume change observer
    private var volumeObserver: VolumeObserver? = null

    /**
     * 연결 시작
     * @param isUsedVideo 영상 통신 사용 여부 (default 미사용)
     * @param isUsedAudio 음성 통신 사용 여부 (default 미사용)
     * @param localView org.webrtc.SurfaceViewRenderer 내 화면
     * @param remoteView org.webrtc.SurfaceViewRenderer 받은 화면
     * @param limitVolumeRate 볼륨 최대치 0 ~ 1f
     * @param isSpeakerMode 스피커 모드 여부
     * @param streamType 오디오 스트림 타입
     * @param googleStunServer 구글 Stun 서버 사용 여부 (default "")
     * @param customTurnServerURL Turn 서버 URL (default "")
     * @param customTurnServerID Turn 서버 접속 아이디 (default "")
     * @param customTurnServerPW Turn 서버 접속 암호 (default "")
     */
    fun start(
        isUsedVideo: Boolean = false,
        isUsedAudio: Boolean = false,
        localView: SurfaceViewRenderer? = null,
        remoteView: SurfaceViewRenderer? = null,
        limitVolumeRate: Float = 1f, isSpeakerMode: Boolean = false, streamType: Int = AudioManager.STREAM_VOICE_CALL,
        googleStunServer: String = "",
        customTurnServerURL: String = "", customTurnServerID: String = "", customTurnServerPW: String = ""
    ) {
        this.isRunning = true
        this.localView = localView
        this.remoteView = remoteView
        this.googleStunServer = googleStunServer
        this.customTurnServerURL = customTurnServerURL
        this.customTurnServerID = customTurnServerID
        this.customTurnServerPW = customTurnServerPW

        // 설정 된 옵션에 맞게 준비

        // 비디오 뷰 설정
        val eglBase = EglBase.create()
        // camera setting
        // 따로 실행중인 카메라가 없는 경우
        this.localView?.let {
            // 카메라 사용 가능 판단하기
            val camera1Enumerator = Camera1Enumerator(false)
            val cameraNames = camera1Enumerator.deviceNames
            val cameraName = cameraNames.singleOrNull { camera1Enumerator.isFrontFacing(it) } ?: cameraNames.firstOrNull()
            if (cameraName == null) {
                onError(Throwable("Devise is not supported camera."))
                return
            }
            this.localCapture = camera1Enumerator.createCapturer(cameraName, null)
            it.init(eglBase.eglBaseContext, null)
        }
        this.remoteView?.init(eglBase.eglBaseContext, null)

        // 볼륨 변화 이벤트 리스너
        if (isUsedAudio) {
            this.volumeObserver = VolumeObserver(this.context,
                handler = Handler(Looper.myLooper() ?: Looper.getMainLooper()),
                limitVolumeRate = limitVolumeRate,
                isSpeakerMode = isSpeakerMode,
                streamType = streamType)
            this.volumeObserver?.init()
        }

        // 초기화
        val initializationOptions = PeerConnectionFactory.InitializationOptions
            .builder(this.context)
            .setEnableInternalTracer(false)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)
        val peerConnectionBuilder = PeerConnectionFactory.builder().setOptions(PeerConnectionFactory.Options())

        // 팩토리 생성
        if (isUsedVideo) {
            // 비디오 설정 적용
            peerConnectionBuilder
                .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
                .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true))
        }
        if (isUsedAudio) {
            // 오디오 설정 적용
            peerConnectionBuilder.setAudioDeviceModule(RTCUtils.createLegacyAudioDevice(this.context))
        }
        val peerConnectionFactory = peerConnectionBuilder.createPeerConnectionFactory()

        // ice server 설정 (기본 구글 턴서버 사용 여부에 따라 추가)
        val iceServers = mutableListOf<PeerConnection.IceServer>()
        if (this.googleStunServer.isNotEmpty()) {
            // google stun server connect
            iceServers.add(
                PeerConnection.IceServer
                    .builder(this.googleStunServer)
                    .createIceServer()
            )
        }
        if (this.customTurnServerURL.isNotEmpty()) {
            // custom turn server connect
            iceServers.add(
                PeerConnection.IceServer
                    .builder(this.customTurnServerURL)
                    .setUsername(this.customTurnServerID)
                    .setPassword(this.customTurnServerPW)
                    .createIceServer()
            )
        }
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN

        // PeerConnection 생성
        this.peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, this.peerObserver)

        val mediaStreamLabels = listOf("ARDAMS")
        // 카메라 영상 접근
        this.localCapture?.let {
            // video setting
            val helper = SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext)
            val videoSource = peerConnectionFactory.createVideoSource(it.isScreencast)
            this.captureObserver = videoSource.capturerObserver
            it.initialize(helper, this.context, videoSource.capturerObserver)
            it.startCapture(size.width, size.height, fps)
            this.localVideoTrack = peerConnectionFactory.createVideoTrack("video", videoSource)
            localView?.let { sink -> this.localVideoTrack?.addSink(sink) }
            this.peerConnection?.addTrack(this.localVideoTrack, mediaStreamLabels)
        } ?: run {
            // 따로 실행중인 카메라에서 데이터 받는 경우
            val videoSource = peerConnectionFactory.createVideoSource(false)
            this.captureObserver = videoSource.capturerObserver
            this.localVideoTrack = peerConnectionFactory.createVideoTrack("video", videoSource)
            this.localView?.let { sink -> this.localVideoTrack?.addSink(sink) }
            this.peerConnection?.addTrack(this.localVideoTrack, mediaStreamLabels)
        }

        // 상대방 화면
        remoteView?.let { sink ->
            peerConnection?.let { peerConn ->
                for (transceiver in peerConn.transceivers) {
                    val track = transceiver.receiver.track()
                    if (track is VideoTrack) {
                        this.remoteVideoTrack = track
                        break
                    }
                }

                remoteVideoTrack?.addSink(sink) ?: run {
                    onError(Throwable("상대방 카메라 화면을 가져올 수 없습니다"))
                    return
                }
            }
        }

        if (isUsedAudio) {
            // 오디오 정보 세팅
            val audioConstraints = MediaConstraints()
            audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googEchoCancellation", "true"))
            audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
            audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googHighpassFilter", "true"))
            audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression", "true"))
            val audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
            val audioTrack = peerConnectionFactory.createAudioTrack("audio", audioSource)
            peerConnection?.addTrack(audioTrack, mediaStreamLabels)
        }
    }

    /**
     * 파트너에게 통신 콜
     */
    fun callCreateOffer() {
        // 파트너에게 통신 하자고 연락
        peerConnection?.createOffer(sessionObserver, MediaConstraints())
    }

    /**
     * 카메라 프레임 전달
     */
    fun onUpdateFrame(nv21: ByteArray, width: Int, height: Int) {
        if (this.isRunning) {
            val timestampNS = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime())
            val buffer = NV21Buffer(nv21, width, height, null)

            val videoFrame = VideoFrame(buffer, 0, timestampNS)
            this.captureObserver?.onFrameCaptured(videoFrame)
            videoFrame.release()
        }
    }

    /**
     * RTC 통신 정지
     */
    fun stop() {
        isRunning = false
        this.volumeObserver?.release()
        this.volumeObserver = null
        this.localView?.let {
            this.localVideoTrack?.removeSink(it)
            it.release()
        }
        this.remoteView?.let {
            this.remoteVideoTrack?.removeSink(it)
            it.release()
        }
        this.localCapture?.stopCapture()
        this.captureObserver?.onCapturerStopped()
        this.peerConnection?.dispose()
        this.peerConnection = null
    }

    /**
     * RTC 리소스 해제
     */
    fun release () {
        this.stop()
        this.localCapture?.dispose()
        this.localCapture = null
        /*if (!isRelease) {
            // 여러번 PeerConnection.close()함수가 호출 되면 데드락이 걸려서 방지하기 위한 해결책
            isRelease = true
            this.peerConnection?.dispose()
            this.peerConnection = null
        }
        isRelease = true*/
    }

    /**
     * 파트너에게 받은 통신 json 형식의 데이터
     */
    fun onSignallingReceive(json: String) {
        try {
            val packet = JSONObject(json)
            when(packet.getString(KEY_TYPE)) {
                KEY_ANSWER -> {
                    // 수신자로부터 answer를 받으면 remote 등록
                    peerConnection?.setRemoteDescription(this.sessionObserver, SessionDescription(SessionDescription.Type.ANSWER, packet.getString(KEY_SDP)))
                }
                KEY_OFFER -> {
                    // 발신자로부터 offer를 받으면 Peerconnection 생성
                    peerConnection?.let {
                        // 발신자면 answer 수신자면 offer
                        it.setRemoteDescription(this.sessionObserver, SessionDescription(SessionDescription.Type.OFFER, packet.getString(KEY_SDP)))
                        it.createAnswer(this.sessionObserver, MediaConstraints())
                    }
                }
                KEY_CANDIDATE -> {
                    peerConnection?.let {
                        try {
                            // {"type:"candidate", "candidate":"candidate:!@@$#%$@#", "sdpMLineIndex": "!@#", "sdpMid": int}
                            val sdp = packet.getString(KEY_CANDIDATE)
                            val sdpMLineIndex = try { packet.getInt(KEY_SDP_INDEX) } catch (e: JSONException) { 0 }
                            val sdpMid = try{ packet.getString(KEY_SDP_MID) } catch (e: JSONException) { "" }

                            val iceCandidate = IceCandidate(sdpMid, sdpMLineIndex, sdp)
                            it.addIceCandidate(iceCandidate)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            onError(e)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 상대방에게 보낼 json 형식의 데이터
     * @param jsonStr 통신 연결에 필요한 json 형식의 스트링 데이터
     */
    abstract fun onPacketSignalling (jsonStr: String)

    /**
     * 정상 연결 된 경우 이벤트
     */
    abstract fun onConnected ()

    /**
     * 연결 해제 된 경우 이벤트
     */
    abstract fun onDisconnected ()

    /**
     * 종료, 오류, 비정상 종료 등의 이벤트
     */
    abstract fun onError(e: Throwable? = null)
}