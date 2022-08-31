//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

public interface CapturerObserver {
    void onCapturerStarted(boolean var1);

    void onCapturerStopped();

    void onFrameCaptured(VideoFrame var1);
}
