//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

public interface SdpObserver {
    @CalledByNative
    void onCreateSuccess(SessionDescription var1);

    @CalledByNative
    void onSetSuccess();

    @CalledByNative
    void onCreateFailure(String var1);

    @CalledByNative
    void onSetFailure(String var1);
}
