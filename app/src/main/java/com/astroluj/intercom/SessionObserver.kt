package com.astroluj.intercom

import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

open class SessionObserver: SdpObserver {
    override fun onCreateSuccess(sessionDescription: SessionDescription) {}
    override fun onSetSuccess() {}
    override fun onCreateFailure(description: String) {}
    override fun onSetFailure(description: String) {}
}