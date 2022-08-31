//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

import org.webrtc.VideoFrame.Buffer;
import org.webrtc.VideoProcessor.FrameAdaptationParameters;

class NativeCapturerObserver implements CapturerObserver {
    private final NativeAndroidVideoTrackSource nativeAndroidVideoTrackSource;

    @CalledByNative
    public NativeCapturerObserver(long nativeSource) {
        this.nativeAndroidVideoTrackSource = new NativeAndroidVideoTrackSource(nativeSource);
    }

    public void onCapturerStarted(boolean success) {
        this.nativeAndroidVideoTrackSource.setState(success);
    }

    public void onCapturerStopped() {
        this.nativeAndroidVideoTrackSource.setState(false);
    }

    public void onFrameCaptured(VideoFrame frame) {
        FrameAdaptationParameters parameters = this.nativeAndroidVideoTrackSource.adaptFrame(frame);
        if (parameters != null) {
            Buffer adaptedBuffer = frame.getBuffer().cropAndScale(parameters.cropX, parameters.cropY, parameters.cropWidth, parameters.cropHeight, parameters.scaleWidth, parameters.scaleHeight);
            this.nativeAndroidVideoTrackSource.onFrameCaptured(new VideoFrame(adaptedBuffer, frame.getRotation(), parameters.timestampNs));
            adaptedBuffer.release();
        }
    }
}
