//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

public class AudioSource extends MediaSource {
    public AudioSource(long nativeSource) {
        super(nativeSource);
    }

    long getNativeAudioSource() {
        return this.getNativeMediaSource();
    }
}
