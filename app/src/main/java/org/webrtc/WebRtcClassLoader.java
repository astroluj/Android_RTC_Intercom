//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

class WebRtcClassLoader {
    WebRtcClassLoader() {
    }

    @CalledByNative
    static Object getClassLoader() {
        Object loader = WebRtcClassLoader.class.getClassLoader();
        if (loader == null) {
            throw new RuntimeException("Failed to get WebRTC class loader.");
        } else {
            return loader;
        }
    }
}
