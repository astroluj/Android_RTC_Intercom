package com.astroluj.intercom

import org.webrtc.*

internal open class PeerObserver: PeerConnection.Observer {
    override fun onIceCandidate(iceCandidate: IceCandidate) {}
    override fun onDataChannel(dataChannel: DataChannel) {}
    override fun onIceConnectionReceivingChange(receivingChange: Boolean) {}
    override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {}
    override fun onIceGatheringChange(state: PeerConnection.IceGatheringState) {}
    override fun onAddStream(mediaStream: MediaStream) {}
    override fun onSignalingChange(signalingState: PeerConnection.SignalingState) {}
    override fun onIceCandidatesRemoved(list: Array<out IceCandidate>) {}
    override fun onRemoveStream(mediaStream: MediaStream) {}
    override fun onRenegotiationNeeded() {}
    override fun onAddTrack(receiver: RtpReceiver?, mediaStreams: Array<out MediaStream>) {}
}