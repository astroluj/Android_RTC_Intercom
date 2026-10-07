package com.astroluj.intercom

import android.content.Context
import org.webrtc.audio.AudioDeviceModule
import org.webrtc.audio.JavaAudioDeviceModule

class RTCUtils {
    companion object {
        // 디바이스 오디오 설정
        @JvmStatic fun createLegacyAudioDevice(context: Context, rateHz: Int = 16000): AudioDeviceModule {

            return JavaAudioDeviceModule.builder(context)
                .setUseHardwareAcousticEchoCanceler (true)
                .setUseHardwareNoiseSuppressor (true)
                .createAudioDeviceModule()
        }
    }
}