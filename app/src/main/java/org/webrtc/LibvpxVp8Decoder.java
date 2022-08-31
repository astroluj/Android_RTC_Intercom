//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

public class LibvpxVp8Decoder extends WrappedNativeVideoDecoder {
    public LibvpxVp8Decoder() {
    }

    public long createNativeVideoDecoder() {
        return nativeCreateDecoder();
    }

    static native long nativeCreateDecoder();
}
