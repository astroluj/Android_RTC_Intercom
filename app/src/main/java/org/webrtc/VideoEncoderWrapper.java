//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

import androidx.annotation.Nullable;
import org.webrtc.VideoEncoder.Callback;
import org.webrtc.VideoEncoder.ScalingSettings;

class VideoEncoderWrapper {
    VideoEncoderWrapper() {
    }

    @CalledByNative
    static boolean getScalingSettingsOn(ScalingSettings scalingSettings) {
        return scalingSettings.on;
    }

    @Nullable
    @CalledByNative
    static Integer getScalingSettingsLow(ScalingSettings scalingSettings) {
        return scalingSettings.low;
    }

    @Nullable
    @CalledByNative
    static Integer getScalingSettingsHigh(ScalingSettings scalingSettings) {
        return scalingSettings.high;
    }

    @CalledByNative
    static Callback createEncoderCallback(long nativeEncoder) {
        return (frame, info) -> {
            nativeOnEncodedFrame(nativeEncoder, frame);
        };
    }

    private static native void nativeOnEncodedFrame(long var0, EncodedImage var2);
}
