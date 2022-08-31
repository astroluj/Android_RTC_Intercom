//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SoftwareVideoEncoderFactory implements VideoEncoderFactory {
    public SoftwareVideoEncoderFactory() {
    }

    @Nullable
    public VideoEncoder createEncoder(VideoCodecInfo info) {
        if (info.name.equalsIgnoreCase("VP8")) {
            return new LibvpxVp8Encoder();
        } else {
            return info.name.equalsIgnoreCase("VP9") && LibvpxVp9Encoder.nativeIsSupported() ? new LibvpxVp9Encoder() : null;
        }
    }

    public VideoCodecInfo[] getSupportedCodecs() {
        return supportedCodecs();
    }

    static VideoCodecInfo[] supportedCodecs() {
        List<VideoCodecInfo> codecs = new ArrayList();
        codecs.add(new VideoCodecInfo("VP8", new HashMap()));
        if (LibvpxVp9Encoder.nativeIsSupported()) {
            codecs.add(new VideoCodecInfo("VP9", new HashMap()));
        }

        return (VideoCodecInfo[])codecs.toArray(new VideoCodecInfo[codecs.size()]);
    }
}
