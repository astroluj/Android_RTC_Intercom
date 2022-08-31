//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

public class BuiltinAudioDecoderFactoryFactory implements AudioDecoderFactoryFactory {
    public BuiltinAudioDecoderFactoryFactory() {
    }

    public long createNativeAudioDecoderFactory() {
        return nativeCreateBuiltinAudioDecoderFactory();
    }

    private static native long nativeCreateBuiltinAudioDecoderFactory();
}
