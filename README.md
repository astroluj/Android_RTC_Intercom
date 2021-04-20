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
val rxSignalling: RxSignalling by lazy {
    object : RxSignalling() {
        override fun onRxError(error: Throwable) {
            error.printStackTrace()
            //Toast.makeText(applicationContext, e.message.toString(), Toast.LENGTH_LONG).show()
            this.release()
            neoRTC.release()
        }

        override fun onRxReceive(json: String) {
            neoRTC.onSignallingReceive(json)
        }

    }
}
val neoRTC: NeoRTC by lazy {
    object : NeoRTC(applicationContext) {
        override fun onError(e: Throwable?) {
            e?.printStackTrace()
            rxSignalling.release()
            this.release()
        }

        override fun onPacketSignalling(jsonStr: String, partnerIP: String) {
            rxSignalling.sendPacket(JSONObject(jsonStr), partnerIP)
        }

        override fun onConnected () {
        }
    }
}

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_webrtc)

    neoRTC.isSpeakerMode = false
    neoRTC.partnerIP = intent.getStringExtra("partnerIp")!!
    neoRTC.start(intent.getBooleanExtra("isInitiator", false))
    rxSignalling.startSignalling()
}

override fun onDestroy() {
    super.onDestroy()
    rxSignalling.release()
    neoRTC.release()
}
...
/***********************************Intercom***********************************/
</code></pre>
<p><p>

<h2>Dependency<br></h2>
Project build.gradle
<code><pre>
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
</pre></code>
Application build.gradle
<code><pre>
dependencies {
	implementation 'com.github.astroluj:Android_RTC_NSIntercom:1.1.1'
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



