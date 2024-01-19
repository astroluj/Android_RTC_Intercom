# WebRTC이용하여 인터컴 기능 사용

# WebRTC 1.0.+(3) version <b>
  https://webrtc.org/
# Android kotlin<br>
- UDP RTP Video/Audio communication


<p><p>
<h2> example<br></h2>
<pre><code>
/***********************************Intercom***********************************/<br>
...
...

    // rtp communication signalling 
    // 기본으로 제공하는 signalling 서버 사용시
    // implementation "io.reactivex.rxjava2:rxandroid:version"
    private val rxSignalling: RxSignalling by lazy {
        object : RxSignalling() {
            override fun onRxReceive(json: String) {
                // 데이터 교환
                rtcIntercom.onSignallingReceive(json)
            }
            
            override fun onRxError(error: Throwable) {
                error.printStackTrace()
                this.release()
                rtcIntercom.onError(error)
            }
        }
    }
    
    private val rtcIntercom: RTCIntercom by lazy {
        object: RTCIntercom(applicationContext) {
            override fun onConnected(partnerIP: String, partnerPort: Int) {
                // signalling 종료
                rxSignalling.release()
            }
            
            override fun onDisconnected(partnerIP: String, partnerPort: Int) {
            }
            
            override fun onError(e: Throwable?) {
                this.release()
                rxSignalling.release()
            }
            
            override fun onPacketSignalling(jsonStr: String, partnerIP: String, partnerPort: Int) {
                // signalling 교환 요청
                rxSignalling.sendPacket(JSONObject(jsonStr), partnerIP, partnerPort)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) { 
        super.onCreate(savedInstanceState)
        ...
    
        rtcIntercom.isSpeakerMode = false
        rtcIntercom.limitVolumeRate = 0 ~ 1f
        rtcIntercom.streamType = AudioManager.STREAM_VOICE_CALL
        rtcIntercom.partnerIP = intent.getStringExtra("partnerIp")!!
        rtcIntercom.isSpeakerMode = false
        rtcIntercom.partnerIP = DefaultIp
        rtcIntercom.partnerPort = DefaultPort
        rtcIntercom.isRunning
        ...
    }
    
        Running
        ...
        rtcIntercom.start(
            // 발신자 여부 true or false,
            isUsedVideo = true or false, 
            isUsedAudio = false or false,
            googleStunServer = "" or google stun server url,
            customTurnServerURL = "" or url,
            customTurnServerID = "" or user name,
            customTurnServerPW = "" or password,
            localView = localLayout(org.webrtc.SurfaceViewRenderer) or null,
            remoteView = remoteLayout(org.webrtc.SurfaceViewRenderer) or null)
        rxSignalling.startSignalling(rtcIntercom.myPort)
    
        // WebRTC에서 제공하는 Rendering을 사용하지 않고 custom frame 전송
        rtcIntercom.onUpdateFrame(NV21 byte array data, width, height)
        ...
    
    override fun onDestroy() {
        super.onDestroy()
        ...
        rtcIntercom.release()
        rxSignalling.release()
        ...
    }
...
/***********************************Intercom***********************************/
</code></pre>
<p><p>

<h2>Dependency<br></h2>
Project build.gradle
<code><pre>
repositories {
    maven { url 'https://jitpack.io' }
}

</pre></code>
Application build.gradle
<code><pre>
dependencies {
	implementation 'com.github.astroluj:android_rtc_intercom:1.3.2'
}
</pre></code>

<h2>License</h2><br>
<p style="font-size:x-large">
License<br>
<a href="http://www.apache.org/licenses/LICENSE-2.0">
	Apache 2.0 License
</a>
<br>
<a href="https://opensource.org/licenses/BSD-3-Clause">
	BSD 3-Clause License
</a>
</p>



