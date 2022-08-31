//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

import androidx.annotation.Nullable;
import java.util.List;

public interface NetworkChangeDetector {
    NetworkChangeDetector.ConnectionType getCurrentConnectionType();

    boolean supportNetworkCallback();

    @Nullable
    List<NetworkChangeDetector.NetworkInformation> getActiveNetworkList();

    void destroy();

    public interface Observer {
        void onConnectionTypeChanged(NetworkChangeDetector.ConnectionType var1);

        void onNetworkConnect(NetworkChangeDetector.NetworkInformation var1);

        void onNetworkDisconnect(long var1);

        void onNetworkPreference(List<NetworkChangeDetector.ConnectionType> var1, int var2);
    }

    public static class NetworkInformation {
        public final String name;
        public final NetworkChangeDetector.ConnectionType type;
        public final NetworkChangeDetector.ConnectionType underlyingTypeForVpn;
        public final long handle;
        public final NetworkChangeDetector.IPAddress[] ipAddresses;

        public NetworkInformation(String name, NetworkChangeDetector.ConnectionType type, NetworkChangeDetector.ConnectionType underlyingTypeForVpn, long handle, NetworkChangeDetector.IPAddress[] addresses) {
            this.name = name;
            this.type = type;
            this.underlyingTypeForVpn = underlyingTypeForVpn;
            this.handle = handle;
            this.ipAddresses = addresses;
        }

        @CalledByNative("NetworkInformation")
        private NetworkChangeDetector.IPAddress[] getIpAddresses() {
            return this.ipAddresses;
        }

        @CalledByNative("NetworkInformation")
        private NetworkChangeDetector.ConnectionType getConnectionType() {
            return this.type;
        }

        @CalledByNative("NetworkInformation")
        private NetworkChangeDetector.ConnectionType getUnderlyingConnectionTypeForVpn() {
            return this.underlyingTypeForVpn;
        }

        @CalledByNative("NetworkInformation")
        private long getHandle() {
            return this.handle;
        }

        @CalledByNative("NetworkInformation")
        private String getName() {
            return this.name;
        }
    }

    public static class IPAddress {
        public final byte[] address;

        public IPAddress(byte[] address) {
            this.address = address;
        }

        @CalledByNative("IPAddress")
        private byte[] getAddress() {
            return this.address;
        }
    }

    public static enum ConnectionType {
        CONNECTION_UNKNOWN,
        CONNECTION_ETHERNET,
        CONNECTION_WIFI,
        CONNECTION_5G,
        CONNECTION_4G,
        CONNECTION_3G,
        CONNECTION_2G,
        CONNECTION_UNKNOWN_CELLULAR,
        CONNECTION_BLUETOOTH,
        CONNECTION_VPN,
        CONNECTION_NONE;

        private ConnectionType() {
        }
    }
}
