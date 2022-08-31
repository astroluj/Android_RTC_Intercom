//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

class BaseBitrateAdjuster implements BitrateAdjuster {
    protected int targetBitrateBps;
    protected int targetFps;

    BaseBitrateAdjuster() {
    }

    public void setTargets(int targetBitrateBps, int targetFps) {
        this.targetBitrateBps = targetBitrateBps;
        this.targetFps = targetFps;
    }

    public void reportEncodedFrame(int size) {
    }

    public int getAdjustedBitrateBps() {
        return this.targetBitrateBps;
    }

    public int getCodecConfigFramerate() {
        return this.targetFps;
    }
}
