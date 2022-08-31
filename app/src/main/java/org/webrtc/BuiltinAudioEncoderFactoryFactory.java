//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

public class BuiltinAudioEncoderFactoryFactory implements AudioEncoderFactoryFactory {
    public BuiltinAudioEncoderFactoryFactory() {
    }

    public long createNativeAudioEncoderFactory() {
        return nativeCreateBuiltinAudioEncoderFactory();
    }

    private static native long nativeCreateBuiltinAudioEncoderFactory();
}
