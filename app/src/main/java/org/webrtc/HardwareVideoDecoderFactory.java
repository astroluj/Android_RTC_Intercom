//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

import android.media.MediaCodecInfo;
import androidx.annotation.Nullable;
import org.webrtc.EglBase.Context;

public class HardwareVideoDecoderFactory extends MediaCodecVideoDecoderFactory {
    private static final Predicate<MediaCodecInfo> defaultAllowedPredicate = new Predicate<MediaCodecInfo>() {
        public boolean test(MediaCodecInfo arg) {
            return MediaCodecUtils.isHardwareAccelerated(arg);
        }
    };

    /** @deprecated */
    @Deprecated
    public HardwareVideoDecoderFactory() {
        this((Context)null);
    }

    public HardwareVideoDecoderFactory(@Nullable Context sharedContext) {
        this(sharedContext, (Predicate)null);
    }

    public HardwareVideoDecoderFactory(@Nullable Context sharedContext, @Nullable Predicate<MediaCodecInfo> codecAllowedPredicate) {
        super(sharedContext, codecAllowedPredicate == null ? defaultAllowedPredicate : codecAllowedPredicate.and(defaultAllowedPredicate));
    }
}
