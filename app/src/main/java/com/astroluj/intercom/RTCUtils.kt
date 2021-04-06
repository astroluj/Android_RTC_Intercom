package com.astroluj.intercom

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import org.webrtc.audio.AudioDeviceModule
import org.webrtc.audio.JavaAudioDeviceModule
import org.webrtc.voiceengine.*

class RTCUtils {
    companion object {
        // 디바이스 오디오 설정
        @JvmStatic fun createLegacyAudioDevice(context: Context, rateHz: Int = 16000): AudioDeviceModule {
            // echo canceller
            if (WebRtcAudioUtils.isAcousticEchoCancelerSupported()) WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true)
            if (WebRtcAudioUtils.isNoiseSuppressorSupported()) WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true)
            if (WebRtcAudioUtils.isAutomaticGainControlSupported()) WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(true)
            if (WebRtcAudioUtils.isDefaultSampleRateOverridden()) WebRtcAudioUtils.setDefaultSampleRateHz(rateHz)

            return JavaAudioDeviceModule.builder(context)
                .setUseHardwareAcousticEchoCanceler (true)
                .setUseHardwareNoiseSuppressor (true)
                .createAudioDeviceModule()
        }

        // 디바이스 오디오 이펙트 설정
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @JvmStatic fun createAudioEffect (audioSessionId: Int): WebRtcAudioEffects {
            val effect = WebRtcAudioEffects.create()
            effect.enable(audioSessionId)
            effect.setAEC(WebRtcAudioEffects.isAcousticEchoCancelerSupported())
            effect.setNS(WebRtcAudioEffects.isNoiseSuppressorSupported())

            return effect
        }
    }
}