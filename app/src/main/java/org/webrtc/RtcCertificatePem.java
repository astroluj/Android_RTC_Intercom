//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

import org.webrtc.PeerConnection.KeyType;

public class RtcCertificatePem {
    public final String privateKey;
    public final String certificate;
    private static final long DEFAULT_EXPIRY = 2592000L;

    @CalledByNative
    public RtcCertificatePem(String privateKey, String certificate) {
        this.privateKey = privateKey;
        this.certificate = certificate;
    }

    @CalledByNative
    String getPrivateKey() {
        return this.privateKey;
    }

    @CalledByNative
    String getCertificate() {
        return this.certificate;
    }

    public static RtcCertificatePem generateCertificate() {
        return nativeGenerateCertificate(KeyType.ECDSA, 2592000L);
    }

    public static RtcCertificatePem generateCertificate(KeyType keyType) {
        return nativeGenerateCertificate(keyType, 2592000L);
    }

    public static RtcCertificatePem generateCertificate(long expires) {
        return nativeGenerateCertificate(KeyType.ECDSA, expires);
    }

    public static RtcCertificatePem generateCertificate(KeyType keyType, long expires) {
        return nativeGenerateCertificate(keyType, expires);
    }

    private static native RtcCertificatePem nativeGenerateCertificate(KeyType var0, long var1);
}
