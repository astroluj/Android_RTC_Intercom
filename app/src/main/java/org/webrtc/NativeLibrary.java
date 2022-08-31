//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

class NativeLibrary {
    private static String TAG = "NativeLibrary";
    private static Object lock = new Object();
    private static boolean libraryLoaded;

    NativeLibrary() {
    }

    static void initialize(NativeLibraryLoader loader, String libraryName) {
        synchronized(lock) {
            if (libraryLoaded) {
                Logging.d(TAG, "Native library has already been loaded.");
            } else {
                Logging.d(TAG, "Loading native library: " + libraryName);
                libraryLoaded = loader.load(libraryName);
            }
        }
    }

    static boolean isLoaded() {
        synchronized(lock) {
            return libraryLoaded;
        }
    }

    static class DefaultLoader implements NativeLibraryLoader {
        DefaultLoader() {
        }

        public boolean load(String name) {
            Logging.d(NativeLibrary.TAG, "Loading library: " + name);

            try {
                System.loadLibrary(name);
                return true;
            } catch (UnsatisfiedLinkError var3) {
                Logging.e(NativeLibrary.TAG, "Failed to load native library: " + name, var3);
                return false;
            }
        }
    }
}
