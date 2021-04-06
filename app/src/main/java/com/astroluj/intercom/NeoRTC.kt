package com.astroluj.intercom

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import org.webrtc.SessionDescription.Type.ANSWER
import org.webrtc.SessionDescription.Type.OFFER

abstract class NeoRTC (private val context: Context) {

    companion object {
        const val KEY_ANSWER = "answer"
        const val KEY_OFFER = "offer"
        const val KEY_OFFER_SDP = "sdp"
        const val KEY_OFFER_SDP_INDEX = "sdpMLineIndex"
        const val KEY_OFFER_SDP_MID = "sdpMid"
    }

    var isRunning = false
        private set

    // 상대방 아이피 내부망이 아닌 경우 포트포워딩이 되어야 함
    var partnerIP: String = ""
    /*
     * audio config
     */
    // 볼륨 최대치 0 ~ 1f
    var limitVolumeRate = 1f
    // 스피커 모드 여부
    var isSpeakerMode: Boolean = false
    // 오디오 스트림 타입
    var streamType: Int = AudioManager.STREAM_VOICE_CALL

    /*
     * video config
     */
    // 내 화면
    var localView: SurfaceViewRenderer? = null
    // 상대방 화면
    var remoteView: SurfaceViewRenderer? = null
    // 동영상 사이즈
    var size: Size = Size(640, 480)
    // 동영상 프레임
    var fps: Int = 20

    // 비디오 사용시 리소스
    private var localCapture: VideoCapturer? = null
    // 연결자
    private var peerConnection: PeerConnection? = null

    // 중복 dispose 막기 위한 플래그
    private var isRelease = false
    // 발신자 여부
    private var isInitiator: Boolean = false
    // 구글 스턴 서버 사용 여부
    private var isUseGoogleStunServer: Boolean = false

    // rtc session protocol
    private val sessionObserver by lazy {
        object: SessionObserver() {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)
                // p2p 연결
                peerConnection?.setLocalDescription(this, sessionDescription)

                // 파트너와 주고받을 데이터
                val packet = JSONObject()
                packet.put(KEY_ANSWER, sessionDescription.description)

                // 파트너에게 전송
                onPacketSignalling(packet.toString(), partnerIP)
            }
        }
    }
    // rtc peer connection
    private val peerObserver by lazy {
        object: PeerObserver() {
            override fun onIceCandidate(iceCandidate: IceCandidate) {
                val iceCandidateJson = JSONObject()
                iceCandidateJson.put(KEY_OFFER_SDP, iceCandidate.sdp)
                iceCandidateJson.put(KEY_OFFER_SDP_INDEX, iceCandidate.sdpMLineIndex)
                iceCandidateJson.put(KEY_OFFER_SDP_MID, iceCandidate.sdpMid)

                // 파트너와 주고받을 데이터
                val packet = JSONObject()
                packet.put(KEY_OFFER, iceCandidateJson)

                // 파트너에게 전송
                onPacketSignalling(packet.toString(), partnerIP)
            }

            override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {
                when (state) {
                    PeerConnection.IceConnectionState.DISCONNECTED,
                    PeerConnection.IceConnectionState.CLOSED -> {
                        // 연결 끊김
                        onError(Throwable("Ice state DISCONNECTED"))
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
     * @param isInitiator 발신자 여부 null 또는 false 이면 수신자임
     * @param isUseGoogleStunServer 구글 스턴 서버 사용 여부
     */
    fun start(isInitiator: Boolean = false, isUseGoogleStunServer: Boolean = false) {
        this.isRelease = false
        this.isRunning = true
        this.isInitiator = isInitiator
        this.isUseGoogleStunServer = isUseGoogleStunServer

        // 볼륨 변화 이벤트 리스너
        this.volumeObserver = VolumeObserver(this.context,
            handler = Handler(Looper.myLooper() ?: Looper.getMainLooper()),
            limitVolumeRate = limitVolumeRate,
            isSpeakerMode = isSpeakerMode,
            streamType = streamType)
        this.volumeObserver?.init()
        if (this.isInitiator) {
            // 발신자 이면 먼저 연결 준비
            startWebRTC()
            peerConnection?.createOffer(sessionObserver, MediaConstraints())
        }
    }

    /**
     * RTC 리소스 해제
     */
    fun release () {
        isRunning = false
        this.volumeObserver?.release()
        this.volumeObserver = null
        this.localCapture?.dispose()
        this.localCapture = null
        if (!isRelease) {
            // 여러번 PeerConnection.close()함수가 호출 되면 데드락이 걸려서 방지하기 위한 해결책
            isRelease = true
            this.peerConnection?.dispose()
            this.peerConnection = null
        }
        isRelease = true
    }

    /**
     * 파트너에게 받은 통신 json 형식의 데이터
     */
    fun onSignallingReceive(json: String) {
        try {
            val packet = JSONObject(json)

            if (!packet.isNull(KEY_ANSWER)) {
                // 발신자로부터 통신 시작데이터를 받으면 통신 시작작
               if (!isInitiator) startWebRTC()

                val description = packet.getString(KEY_ANSWER)
                peerConnection?.let {
                    // 발신자면 answer 수신자면 offer
                    it.setRemoteDescription(this.sessionObserver, SessionDescription(if (this.isInitiator) ANSWER else OFFER, description))
                    it.createAnswer(this.sessionObserver, MediaConstraints())
                }
            } else if (!packet.isNull(KEY_OFFER)) {
                val iceCandidateJson = packet.getJSONObject(KEY_OFFER)
                val sdp = iceCandidateJson.getString(KEY_OFFER_SDP)
                val sdpMLineIndex = iceCandidateJson.getInt(KEY_OFFER_SDP_INDEX)
                val sdpMid = iceCandidateJson.getString(KEY_OFFER_SDP_MID)

                val iceCandidate = IceCandidate(sdpMid, sdpMLineIndex, sdp)
                peerConnection?.addIceCandidate(iceCandidate)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    // Web RTC 관련 설정
    private fun startWebRTC() {
        // peer connection 생성
        val initializationOptions = PeerConnectionFactory.InitializationOptions
                .builder(this.context)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)
        val peerConnectionBuilder = PeerConnectionFactory.builder().setOptions(PeerConnectionFactory.Options())

        // camera setting
        val eglBase = EglBase.create()
        if (this.localView != null && this.remoteView != null) {
            // 카메라 사용 가능 판단하기
            val camera1Enumerator = Camera1Enumerator(false)
            val cameraNames = camera1Enumerator.deviceNames
            val cameraName = cameraNames.singleOrNull { camera1Enumerator.isFrontFacing(it) } ?: cameraNames.firstOrNull()
            if (cameraName == null) {
                onError(Throwable("Devise is not supported camera."))
                return
            }

            this.localCapture = camera1Enumerator.createCapturer(cameraName, null)
            this.localView?.init(eglBase.eglBaseContext, null)
            this.remoteView?.init(eglBase.eglBaseContext, null)

            // 비디오 설정 적용
            peerConnectionBuilder
                    .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
                    .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true))
        }

        // connection stream setting
        val peerConnectionFactory = peerConnectionBuilder
                .setOptions(PeerConnectionFactory.Options())
                .setAudioDeviceModule(RTCUtils.createLegacyAudioDevice(this.context))
                .createPeerConnectionFactory()

        // ice server add (기본 구글 턴서버 사용여부에 따라 추가)
        val iceServers = mutableListOf<PeerConnection.IceServer>()
        if (isUseGoogleStunServer) iceServers.add(PeerConnection.IceServer.builder("stun:stun2.l.google.com:19302").createIceServer())

        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        this.peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, this.peerObserver)

        val mediaStreamLabels = listOf("ARDAMS")
        // 카메라 영상 접근
        this.localCapture?.let {
            // video setting
            val helper = SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext)
            val videoSource = peerConnectionFactory.createVideoSource(it.isScreencast)
            it.initialize(helper, this.context, videoSource.capturerObserver)
            it.startCapture(size.width, size.height, fps)
            val videoTrack = peerConnectionFactory.createVideoTrack("video", videoSource)
            videoTrack.addSink(localView)
            this.peerConnection?.addTrack(videoTrack, mediaStreamLabels)

            var remoteVideoTrack: VideoTrack? = null
            peerConnection?.let { peerConn ->
                for (transceiver in peerConn.transceivers) {
                    val track = transceiver.receiver.track()
                    if (track is VideoTrack) {
                        remoteVideoTrack = track
                        break
                    }
                }
                remoteVideoTrack?.addSink(remoteView) ?: run {
                    onError(Throwable("상대방 카메라 화면을 가져올 수 없습니다"))
                    return
                }
            }
        }

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

    /**
     * 상대방에게 보낼 json 형식의 데이터
     * @param jsonStr 통신 연결에 필요한 json 형식의 스트링 데이터
     * @param partnerIP 상대방 아이피
     */
    abstract fun onPacketSignalling (jsonStr: String, partnerIP: String = "")

    /**
     * 정상 연결 된 경우 이벤트
     */
    abstract fun onConnected ()

    /**
     * 종료, 오류, 비정상 종료 등의 이벤트
     */
    abstract fun onError(e: Throwable? = null)
}