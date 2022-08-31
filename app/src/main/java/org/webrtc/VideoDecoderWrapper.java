//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

import org.webrtc.VideoDecoder.Callback;

class VideoDecoderWrapper {
    VideoDecoderWrapper() {
    }

    @CalledByNative
    static Callback createDecoderCallback(long nativeDecoder) {
        return (frame, decodeTimeMs, qp) -> {
            nativeOnDecodedFrame(nativeDecoder, frame, decodeTimeMs, qp);
        };
    }

    private static native void nativeOnDecodedFrame(long var0, VideoFrame var2, Integer var3, Integer var4);
}
