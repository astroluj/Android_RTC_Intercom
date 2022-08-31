//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

enum VideoCodecMimeType {
    VP8("video/x-vnd.on2.vp8"),
    VP9("video/x-vnd.on2.vp9"),
    H264("video/avc");

    private final String mimeType;

    private VideoCodecMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    String mimeType() {
        return this.mimeType;
    }
}
