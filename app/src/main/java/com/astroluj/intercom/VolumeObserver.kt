package com.astroluj.intercom

import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Handler
import android.provider.Settings
import org.webrtc.voiceengine.WebRtcAudioEffects
import org.webrtc.voiceengine.WebRtcAudioManager
import org.webrtc.voiceengine.WebRtcAudioRecord
import org.webrtc.voiceengine.WebRtcAudioTrack

/**
 * internal class
 * 내부에서 쓰이는 볼륨 이벤트 클래스
 */
internal class VolumeObserver (private val context: Context,
                               private val handler: Handler,
                               private val limitVolumeRate: Float,
                               private val isSpeakerMode: Boolean = false,
                               private val streamType: Int = AudioManager.STREAM_VOICE_CALL): ContentObserver(handler) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val limitVolume = (audioManager.getStreamMaxVolume(streamType) * limitVolumeRate).toInt()

    private var originVolume = 0
    private var originMode = AudioManager.MODE_NORMAL
    private var originSpeaker = false
    private var audioEffects: WebRtcAudioEffects? = null

    // 옵저버 등록 이닛
    fun init() {
        originMode = audioManager.mode
        originSpeaker = audioManager.isSpeakerphoneOn

        audioManager.isSpeakerphoneOn = isSpeakerMode
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        // AES, NS
        // if (audioEffects == null) audioEffects = RTCUtils.createAudioEffect(audioManager.generateAudioSessionId())

        val currentVolume = audioManager.getStreamVolume(streamType)
        // 처음 최대 볼륨이 제한 볼륨보다 크면 변경
        if (limitVolume < currentVolume) {
            originVolume = currentVolume
            audioManager.setStreamVolume(streamType, limitVolume, 0)
        }

        context.contentResolver.registerContentObserver(Settings.System.CONTENT_URI, true, this)
    }
    
    // 옵저버 해제
    fun release () {
        // 제한을 두었던 최대 볼륨에서 원래 볼륨으로 되돌리기
        if (originVolume > 0) audioManager.setStreamVolume(streamType, originVolume, 0)
        this.context.contentResolver.unregisterContentObserver(this)
        this.audioEffects?.release()

        audioManager.isSpeakerphoneOn = originSpeaker
        audioManager.mode = originMode
    }

    override fun deliverSelfNotifications(): Boolean {
        return false
    }

    override fun onChange(selfChange: Boolean) {
        // 볼륨 변화 감지하여 최대 크기를 넘으면 강제로 줄이기
        val currentVolume = audioManager.getStreamVolume(streamType)
        if (limitVolume < currentVolume) {
            audioManager.setStreamVolume(streamType, limitVolume, 0)
        }
    }
}