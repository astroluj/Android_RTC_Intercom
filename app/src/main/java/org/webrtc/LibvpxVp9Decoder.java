//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

public class LibvpxVp9Decoder extends WrappedNativeVideoDecoder {
    public LibvpxVp9Decoder() {
    }

    public long createNativeVideoDecoder() {
        return nativeCreateDecoder();
    }

    static native long nativeCreateDecoder();

    static native boolean nativeIsSupported();
}
