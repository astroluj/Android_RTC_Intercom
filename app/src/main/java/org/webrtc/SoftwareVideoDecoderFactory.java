//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SoftwareVideoDecoderFactory implements VideoDecoderFactory {
    public SoftwareVideoDecoderFactory() {
    }

    /** @deprecated */
    @Deprecated
    @Nullable
    public VideoDecoder createDecoder(String codecType) {
        return this.createDecoder(new VideoCodecInfo(codecType, new HashMap()));
    }

    @Nullable
    public VideoDecoder createDecoder(VideoCodecInfo codecType) {
        if (codecType.getName().equalsIgnoreCase("VP8")) {
            return new LibvpxVp8Decoder();
        } else {
            return codecType.getName().equalsIgnoreCase("VP9") && LibvpxVp9Decoder.nativeIsSupported() ? new LibvpxVp9Decoder() : null;
        }
    }

    public VideoCodecInfo[] getSupportedCodecs() {
        return supportedCodecs();
    }

    static VideoCodecInfo[] supportedCodecs() {
        List<VideoCodecInfo> codecs = new ArrayList();
        codecs.add(new VideoCodecInfo("VP8", new HashMap()));
        if (LibvpxVp9Decoder.nativeIsSupported()) {
            codecs.add(new VideoCodecInfo("VP9", new HashMap()));
        }

        return (VideoCodecInfo[])codecs.toArray(new VideoCodecInfo[codecs.size()]);
    }
}
