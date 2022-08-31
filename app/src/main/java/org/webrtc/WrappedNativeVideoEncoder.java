//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

import org.webrtc.VideoEncoder.BitrateAllocation;
import org.webrtc.VideoEncoder.Callback;
import org.webrtc.VideoEncoder.EncodeInfo;
import org.webrtc.VideoEncoder.ScalingSettings;
import org.webrtc.VideoEncoder.Settings;

public abstract class WrappedNativeVideoEncoder implements VideoEncoder {
    public WrappedNativeVideoEncoder() {
    }

    public abstract long createNativeVideoEncoder();

    public abstract boolean isHardwareEncoder();

    public final VideoCodecStatus initEncode(Settings settings, Callback encodeCallback) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public final VideoCodecStatus release() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public final VideoCodecStatus encode(VideoFrame frame, EncodeInfo info) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public final VideoCodecStatus setRateAllocation(BitrateAllocation allocation, int framerate) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public final ScalingSettings getScalingSettings() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public final String getImplementationName() {
        throw new UnsupportedOperationException("Not implemented.");
    }
}
