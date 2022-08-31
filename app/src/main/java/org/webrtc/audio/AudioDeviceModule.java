//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc.audio;

public interface AudioDeviceModule {
    long getNativeAudioDeviceModulePointer();

    void release();

    void setSpeakerMute(boolean var1);

    void setMicrophoneMute(boolean var1);
}
