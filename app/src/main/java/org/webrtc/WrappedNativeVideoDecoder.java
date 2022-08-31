//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

import org.webrtc.VideoDecoder.Callback;
import org.webrtc.VideoDecoder.DecodeInfo;
import org.webrtc.VideoDecoder.Settings;

public abstract class WrappedNativeVideoDecoder implements VideoDecoder {
    public WrappedNativeVideoDecoder() {
    }

    public abstract long createNativeVideoDecoder();

    public final VideoCodecStatus initDecode(Settings settings, Callback decodeCallback) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public final VideoCodecStatus release() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public final VideoCodecStatus decode(EncodedImage frame, DecodeInfo info) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public final boolean getPrefersLateDecoding() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public final String getImplementationName() {
        throw new UnsupportedOperationException("Not implemented.");
    }
}
