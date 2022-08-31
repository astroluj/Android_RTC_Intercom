//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraManager;
import androidx.annotation.Nullable;
import org.webrtc.CameraSession.CreateSessionCallback;
import org.webrtc.CameraSession.Events;
import org.webrtc.CameraVideoCapturer.CameraEventsHandler;

@TargetApi(21)
public class Camera2Capturer extends CameraCapturer {
    private final Context context;
    @Nullable
    private final CameraManager cameraManager;

    public Camera2Capturer(Context context, String cameraName, CameraEventsHandler eventsHandler) {
        super(cameraName, eventsHandler, new Camera2Enumerator(context));
        this.context = context;
        this.cameraManager = (CameraManager)context.getSystemService("camera");
    }

    protected void createCameraSession(CreateSessionCallback createSessionCallback, Events events, Context applicationContext, SurfaceTextureHelper surfaceTextureHelper, String cameraName, int width, int height, int framerate) {
        Camera2Session.create(createSessionCallback, events, applicationContext, this.cameraManager, surfaceTextureHelper, cameraName, width, height, framerate);
    }
}
