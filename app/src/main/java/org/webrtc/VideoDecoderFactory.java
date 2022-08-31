//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

import androidx.annotation.Nullable;

public interface VideoDecoderFactory {
    /** @deprecated */
    @Deprecated
    @Nullable
    default VideoDecoder createDecoder(String codecType) {
        throw new UnsupportedOperationException("Deprecated and not implemented.");
    }

    @Nullable
    @CalledByNative
    default VideoDecoder createDecoder(VideoCodecInfo info) {
        return this.createDecoder(info.getName());
    }

    @CalledByNative
    default VideoCodecInfo[] getSupportedCodecs() {
        return new VideoCodecInfo[0];
    }
}
