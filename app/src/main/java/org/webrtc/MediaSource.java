//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

public class MediaSource {
    private final RefCountDelegate refCountDelegate;
    private long nativeSource;

    public MediaSource(long nativeSource) {
        this.refCountDelegate = new RefCountDelegate(() -> {
            JniCommon.nativeReleaseRef(nativeSource);
        });
        this.nativeSource = nativeSource;
    }

    public MediaSource.State state() {
        this.checkMediaSourceExists();
        return nativeGetState(this.nativeSource);
    }

    public void dispose() {
        this.checkMediaSourceExists();
        this.refCountDelegate.release();
        this.nativeSource = 0L;
    }

    protected long getNativeMediaSource() {
        this.checkMediaSourceExists();
        return this.nativeSource;
    }

    void runWithReference(Runnable runnable) {
        if (this.refCountDelegate.safeRetain()) {
            try {
                runnable.run();
            } finally {
                this.refCountDelegate.release();
            }
        }

    }

    private void checkMediaSourceExists() {
        if (this.nativeSource == 0L) {
            throw new IllegalStateException("MediaSource has been disposed.");
        }
    }

    private static native MediaSource.State nativeGetState(long var0);

    public static enum State {
        INITIALIZING,
        LIVE,
        ENDED,
        MUTED;

        private State() {
        }

        @CalledByNative("State")
        static MediaSource.State fromNativeIndex(int nativeIndex) {
            return values()[nativeIndex];
        }
    }
}
