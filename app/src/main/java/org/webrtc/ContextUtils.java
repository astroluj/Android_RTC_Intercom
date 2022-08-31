//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

import android.content.Context;

public class ContextUtils {
    private static final String TAG = "ContextUtils";
    private static Context applicationContext;

    public ContextUtils() {
    }

    public static void initialize(Context applicationContext) {
        if (applicationContext == null) {
            throw new IllegalArgumentException("Application context cannot be null for ContextUtils.initialize.");
        } else {
            ContextUtils.applicationContext = applicationContext;
        }
    }

    /** @deprecated */
    @Deprecated
    public static Context getApplicationContext() {
        return applicationContext;
    }
}
