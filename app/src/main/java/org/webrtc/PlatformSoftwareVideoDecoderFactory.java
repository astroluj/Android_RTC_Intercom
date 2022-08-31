//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

import android.media.MediaCodecInfo;
import androidx.annotation.Nullable;
import org.webrtc.EglBase.Context;

public class PlatformSoftwareVideoDecoderFactory extends MediaCodecVideoDecoderFactory {
    private static final Predicate<MediaCodecInfo> defaultAllowedPredicate = new Predicate<MediaCodecInfo>() {
        public boolean test(MediaCodecInfo arg) {
            return MediaCodecUtils.isSoftwareOnly(arg);
        }
    };

    public PlatformSoftwareVideoDecoderFactory(@Nullable Context sharedContext) {
        super(sharedContext, defaultAllowedPredicate);
    }
}
