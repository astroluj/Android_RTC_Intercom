//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

import android.graphics.Matrix;
import java.nio.ByteBuffer;

public class VideoFrame implements RefCounted {
    private final VideoFrame.Buffer buffer;
    private final int rotation;
    private final long timestampNs;

    @CalledByNative
    public VideoFrame(VideoFrame.Buffer buffer, int rotation, long timestampNs) {
        if (buffer == null) {
            throw new IllegalArgumentException("buffer not allowed to be null");
        } else if (rotation % 90 != 0) {
            throw new IllegalArgumentException("rotation must be a multiple of 90");
        } else {
            this.buffer = buffer;
            this.rotation = rotation;
            this.timestampNs = timestampNs;
        }
    }

    @CalledByNative
    public VideoFrame.Buffer getBuffer() {
        return this.buffer;
    }

    @CalledByNative
    public int getRotation() {
        return this.rotation;
    }

    @CalledByNative
    public long getTimestampNs() {
        return this.timestampNs;
    }

    public int getRotatedWidth() {
        return this.rotation % 180 == 0 ? this.buffer.getWidth() : this.buffer.getHeight();
    }

    public int getRotatedHeight() {
        return this.rotation % 180 == 0 ? this.buffer.getHeight() : this.buffer.getWidth();
    }

    public void retain() {
        this.buffer.retain();
    }

    @CalledByNative
    public void release() {
        this.buffer.release();
    }

    public interface TextureBuffer extends VideoFrame.Buffer {
        VideoFrame.TextureBuffer.Type getType();

        int getTextureId();

        Matrix getTransformMatrix();

        public static enum Type {
            OES(36197),
            RGB(3553);

            private final int glTarget;

            private Type(int glTarget) {
                this.glTarget = glTarget;
            }

            public int getGlTarget() {
                return this.glTarget;
            }
        }
    }

    public interface I420Buffer extends VideoFrame.Buffer {
        @CalledByNative("I420Buffer")
        ByteBuffer getDataY();

        @CalledByNative("I420Buffer")
        ByteBuffer getDataU();

        @CalledByNative("I420Buffer")
        ByteBuffer getDataV();

        @CalledByNative("I420Buffer")
        int getStrideY();

        @CalledByNative("I420Buffer")
        int getStrideU();

        @CalledByNative("I420Buffer")
        int getStrideV();
    }

    public interface Buffer extends RefCounted {
        @CalledByNative("Buffer")
        int getWidth();

        @CalledByNative("Buffer")
        int getHeight();

        @CalledByNative("Buffer")
        VideoFrame.I420Buffer toI420();

        @CalledByNative("Buffer")
        void retain();

        @CalledByNative("Buffer")
        void release();

        @CalledByNative("Buffer")
        VideoFrame.Buffer cropAndScale(int var1, int var2, int var3, int var4, int var5, int var6);
    }
}
