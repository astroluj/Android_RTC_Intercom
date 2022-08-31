//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

public interface RefCounted {
    @CalledByNative
    void retain();

    @CalledByNative
    void release();
}
