package de.fwatermann.bungeecord.bansystem.util;

import java.util.Arrays;

public class IPUtils {

    /**
     * A Network object, representing a network address and a network mask
     *
     * @param network The network address
     * @param networkMask The network mask
     */
    public record Network(byte[] network, byte[] networkMask) {
        /**
         * Create a Network object by a string
         *
         * @param notation The CIDR notation
         * @return The Network object
         */
        public static Network byCIDR(String notation) throws InvalidIPAddressException {
            String[] parts = notation.split("/", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid CIDR notation: " + notation);
            }
            byte[] network = getRawIP(parts[0]);
            int mask = Integer.parseInt(parts[1]);
            if (mask < 0 || mask > network.length * 8) {
                throw new IllegalArgumentException("Invalid CIDR notation: " + notation);
            }
            byte[] networkMask = new byte[network.length];
            Arrays.fill(networkMask, (byte) 0);
            for (int i = 0; i < mask; i++) {
                int index = i / 8;
                int bit = i % 8;
                networkMask[index] |= 1 << (7 - bit);
            }
            return new Network(network, networkMask);
        }

        /**
         * Create a Network object by an IP address. The subnet will be /32 for IPv4 and /128 for
         * IPv6
         *
         * @param ip The IP address
         * @return The Network object
         */
        public static Network byIP(String ip) throws InvalidIPAddressException {
            byte[] network = getRawIP(ip);
            byte[] networkMask = new byte[network.length];
            Arrays.fill(networkMask, (byte) 0xFF);
            return new Network(network, networkMask);
        }
    }

    /**
     * Get the raw ip bytes from a string
     *
     * @param ip The ip string, can be IPv4, IPv6 or IPv4 mapped IPv6
     * @return The raw ip bytes
     */
    public static byte[] getRawIP(String ip) throws InvalidIPAddressException {

        boolean ipv6 = ip.contains(":");
        boolean ipv4 = ip.contains(".");

        if (ipv6 && !ipv4) { // IPv6
            byte[] bytes = new byte[16];
            Arrays.fill(bytes, (byte) 0);
            String[] parts = ip.split(":", 8);
            int index = 0;
            for (int i = 0; i < parts.length; i++, index++) {
                if (parts[i].isEmpty()) { // Skip
                    int skip = 8 - parts.length;
                    index += skip;
                    continue;
                }
                String hex = parts[i];
                int value = Integer.parseInt(hex, 16);
                bytes[index * 2] = (byte) (value >> 8);
                bytes[index * 2 + 1] = (byte) (value & 0xFF);
            }
            return bytes;
        } else if (!ipv6 && ipv4) { // IPv4
            byte[] bytes = new byte[4];
            Arrays.fill(bytes, (byte) 0);
            String[] parts = ip.split("\\.", 4);
            if (parts.length < 4 && parts.length >= 2) {
                for (int i = 0; i < parts.length - 1; i++) {
                    bytes[i] = (byte) Integer.parseInt(parts[i]);
                }
                bytes[3] = (byte) Integer.parseInt(parts[parts.length - 1]);
            } else if (parts.length == 4) {
                for (int i = 0; i < parts.length; i++) {
                    bytes[i] = (byte) Integer.parseInt(parts[i]);
                }
            } else {
                throw new InvalidIPAddressException("Invalid IPv4 address: " + ip);
            }
            return bytes;
        } else if (ipv6 && ipv4) { // IPv4 mapped IPv6
            byte[] bytes = new byte[16];
            Arrays.fill(bytes, (byte) 0);
            int splitIndex = ip.lastIndexOf("FFFF:");
            if (splitIndex == -1) {
                throw new InvalidIPAddressException("Invalid IPv4 mapped IPv6 address: " + ip);
            }
            String[] parts = ip.substring(splitIndex + 5).split("\\.", 4);
            for (int i = 0; i < parts.length; i++) {
                bytes[i + 12] = (byte) Integer.parseInt(parts[i]);
            }
            return bytes;
        } else {
            throw new InvalidIPAddressException("Invalid IP address: " + ip);
        }
    }

    /**
     * Get the IP address from raw bytes
     *
     * @param bytes The raw bytes, can be bytes of IPv4 or IPv6
     * @param shorten Whether to shorten the IPv6 address
     * @return The IP address
     */
    public static String getIPbyRawBytes(byte[] bytes, boolean shorten)
            throws InvalidIPAddressException {
        if (bytes.length == 16) { // IPv6
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                int value = (((bytes[i * 2] & 0xFF) << 8) | (bytes[i * 2 + 1] & 0xFF));
                builder.append(Integer.toString(value, 16));
                if (i < 7) {
                    builder.append(":");
                }
            }
            String ip = builder.toString();
            if (shorten) {
                ip = ip.replaceFirst("((0000:)|(0:)|(0$))+", ":");
            }
            return ip;
        } else if (bytes.length == 4) { // IPv4
            return String.format(
                    "%d.%d.%d.%d",
                    bytes[0] & 0xFF, bytes[1] & 0xFF, bytes[2] & 0xFF, bytes[3] & 0xFF);
        } else {
            throw new InvalidIPAddressException("Invalid IP address: " + Arrays.toString(bytes));
        }
    }

    /**
     * Check if an IP address is in a network
     *
     * @param network CIDR noted network
     * @param hostIp The IP address to check
     * @return Whether the IP address is in the network
     */
    public static boolean isIPinNetwork(Network network, byte[] hostIp)
            throws InvalidIPAddressException {
        if (network.network.length != hostIp.length) {
            throw new InvalidIPAddressException(
                    "Invalid IP address length: "
                            + hostIp.length
                            + " (expected: "
                            + network.network.length
                            + ")");
        }

        byte[] maskedIP = new byte[hostIp.length];
        for (int i = 0; i < hostIp.length; i++) {
            maskedIP[i] = (byte) (hostIp[i] & network.networkMask[i]);
        }
        byte[] maskedNetwork = new byte[network.network.length];
        for (int i = 0; i < network.network.length; i++) {
            maskedNetwork[i] = (byte) (network.network[i] & network.networkMask[i]);
        }

        return Arrays.equals(maskedIP, maskedNetwork);
    }

    /**
     * Check if an IP address is in a network
     *
     * @param cidr Network in CIDR notation
     * @param ip The IP address to check
     * @return Whether the IP address is in the network
     * @throws InvalidIPAddressException If the IP address is invalid
     */
    public static boolean isIPInNetwork(String cidr, String ip) throws InvalidIPAddressException {
        return isIPinNetwork(Network.byCIDR(cidr), getRawIP(ip));
    }

    public static class InvalidIPAddressException extends Exception {
        public InvalidIPAddressException(String message) {
            super(message);
        }
    }
}
