//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

public class LibvpxVp8Encoder extends WrappedNativeVideoEncoder {
    public LibvpxVp8Encoder() {
    }

    public long createNativeVideoEncoder() {
        return nativeCreateEncoder();
    }

    static native long nativeCreateEncoder();

    public boolean isHardwareEncoder() {
        return false;
    }
}
