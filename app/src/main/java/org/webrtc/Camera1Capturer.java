//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

import android.content.Context;
import org.webrtc.CameraSession.CreateSessionCallback;
import org.webrtc.CameraSession.Events;
import org.webrtc.CameraVideoCapturer.CameraEventsHandler;

public class Camera1Capturer extends CameraCapturer {
    private final boolean captureToTexture;

    public Camera1Capturer(String cameraName, CameraEventsHandler eventsHandler, boolean captureToTexture) {
        super(cameraName, eventsHandler, new Camera1Enumerator(captureToTexture));
        this.captureToTexture = captureToTexture;
    }

    protected void createCameraSession(CreateSessionCallback createSessionCallback, Events events, Context applicationContext, SurfaceTextureHelper surfaceTextureHelper, String cameraName, int width, int height, int framerate) {
        Camera1Session.create(createSessionCallback, events, this.captureToTexture, applicationContext, surfaceTextureHelper, Camera1Enumerator.getCameraIndex(cameraName), width, height, framerate);
    }
}
