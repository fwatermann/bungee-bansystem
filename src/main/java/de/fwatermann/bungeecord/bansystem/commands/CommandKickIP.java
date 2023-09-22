package de.fwatermann.bungeecord.bansystem.commands;

import de.fwatermann.bungeecord.bansystem.Permissions;
import de.fwatermann.bungeecord.bansystem.Translations;
import de.fwatermann.bungeecord.bansystem.translation.Translation;
import de.fwatermann.bungeecord.bansystem.util.IPUtils;
import de.fwatermann.bungeecord.bansystem.util.MessageGenerator;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class CommandKickIP extends Command {

    private static final Pattern IPv4_Regex =
            Pattern.compile(
                    "^(((25[0-4])|(2[0-4][0-9])|(1[0-9]{0,2})|([1-9][0-9]?))\\.)(((25[0-4])|(2[0-4][0-9])|(1[0-9]{0,2})|([0-9]{1,2}))\\.){0,2}((25[0-4])|(2[0-4][0-9])|(1[0-9]{0,2})|([1-9][0-9]?))$");
    private static final Pattern IPv6_Regex =
            Pattern.compile(
                    "(^([0-9a-fA-F]{1,4}:){1,7}[0-9a-fA-F]{1,4}$)|(^([0-9a-fA-F]{0,4}:){0,7}:$)|(^([0-9a-fA-F]{1,4}:){1}(:[0-9a-fA-F]{1,4}){1,6}$)|(^([0-9a-fA-F]{1,4}:){2}(:[0-9a-fA-F]{1,4}){1,5}$)|(^([0-9a-fA-F]{1,4}:){3}(:[0-9a-fA-F]{1,4}){1,4}$)|(^([0-9a-fA-F]{1,4}:){4}(:[0-9a-fA-F]{1,4}){1,3}$)|(^([0-9a-fA-F]{1,4}:){5}(:[0-9a-fA-F]{1,4}){1,2}$)|(^([0-9a-fA-F]{1,4}:){6}(:[0-9a-fA-F]{1,4}){1,1}$)");

    public CommandKickIP() {
        super("kickip");
    }

    private static String ipv6(String in) {
        return in.split("%", 2)[0];
    }

    /**
     * Check if the input matches the player's IP address
     *
     * @param input The input (can be an IP address or an IP range in CIDR notation)
     * @param address The player's IP address
     * @return Whether the input matches the player's IP address
     */
    private static boolean ipMatch(String input, InetSocketAddress address) {
        byte[] playerIp = address.getAddress().getAddress();
        byte[] inputIP = getRawIPByString(input.split("/", 2)[0]);

        if (playerIp.length != inputIP.length) { // Check if both IPs are the same version
            System.out.printf(
                    Locale.GERMAN,
                    "playerIp: %s inputIp: %s\n",
                    Arrays.toString(playerIp),
                    Arrays.toString(inputIP));
            return false;
        }

        if (!input.contains("/")) { // Check if it is a single IP
            return Arrays.equals(playerIp, inputIP);
        }
        int index;
        if ((index = input.lastIndexOf("/"))
                > input.length() - 2) { // Check if there is a number after the slash
            System.out.println("No number after slash");
            return false;
        }

        BigInteger netMask =
                getNetMask(inputIP.length * 8, Integer.parseInt(input.substring(index + 1)));
        BigInteger maskIP = byteArrayToBigInt(inputIP).and(netMask);

        System.out.printf(Locale.GERMAN, "netMask: %d maskIp: %d\n", netMask, maskIP);
        System.out.printf(
                Locale.GERMAN,
                "inputIp: %s netMask: %s maskIp: %s playerIp: %s\n",
                rawIpToString(inputIP),
                rawIpToString(netMask.toByteArray()),
                rawIpToString(maskIP.toByteArray()),
                rawIpToString(playerIp));

        int[] result = new int[playerIp.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (playerIp[i] & netMask.toByteArray()[i]) ^ maskIP.toByteArray()[i];
        }
        System.out.println(Arrays.toString(result));
        for (int i = 0; i < result.length; i++) {
            if (result[i] != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Convert a byte array to a BigInteger
     *
     * @param bytes The byte array
     * @return The BigInteger
     */
    private static BigInteger byteArrayToBigInt(byte[] bytes) {
        return new BigInteger(bytes);
    }

    /**
     * Generate a byte array from a string representation of an IP address
     *
     * @param ip The IP address as a string
     * @return The IP address as a byte array
     */
    private static byte[] getRawIPByString(String ip) {
        byte[] result;
        if (ip.contains(":")) { // IPv6
            result = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            String[] split = ip.split(":", 8);
            for (int i = 0; i < split.length; i++) {
                if (split[i].isEmpty()) {
                    int skip = 8 - split.length;
                    i += skip;
                    continue;
                }
                String hex = split[i];
                short value = Short.parseShort(hex, 16);
                result[i * 2] = (byte) (value >> 8);
                result[i * 2 + 1] = (byte) (value & 0xFF);
            }
        } else { // IPv4
            result = new byte[] {0, 0, 0, 0};
            String[] split = ip.split("\\.", 4);
            for (int i = 0; i < split.length - 1; i++) {
                result[i] = Byte.parseByte(split[i]);
            }
            result[3] = Byte.parseByte(split[split.length - 1]);
        }
        return result;
    }

    private static BigInteger getNetMask(int ipBitLength, int cidr) {
        BigInteger i =
                BigInteger.ONE
                        .shiftLeft(cidr)
                        .subtract(BigInteger.ONE)
                        .shiftLeft(ipBitLength - cidr);
        System.out.println(
                "NetMask: "
                        + i.toString(2)
                        + " "
                        + Arrays.toString(i.toByteArray())
                        + " ("
                        + i.bitLength()
                        + " cidr: "
                        + cidr
                        + " bits: "
                        + ipBitLength
                        + ")");
        return i;
    }

    private static String rawIpToString(byte[] rawIp) {
        if (rawIp.length == 4) {
            return String.format(
                    Locale.GERMAN,
                    "%d.%d.%d.%d",
                    rawIp[0] & 0xFF,
                    rawIp[1] & 0xFF,
                    rawIp[2] & 0xFF,
                    rawIp[3] & 0xFF);
        } else if (rawIp.length == 16) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rawIp.length; i += 2) {
                sb.append(String.format(Locale.GERMAN, "%02x%02x", rawIp[i], rawIp[i + 1]));
                if (i < rawIp.length - 2) {
                    sb.append(":");
                }
            }
            return sb.toString();
        } else {
            throw new IllegalArgumentException("Invalid IP address length (" + rawIp.length + ")");
        }
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permissions.KICK_IP) || !sender.hasPermission(Permissions.KICK)) {
            sender.sendMessage(Translation.component(Translations.NO_PERMISSIONS, sender));
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(Translation.component(Translations.KICKIP_COMMAND_USAGE, sender));
            return;
        }

        String reason;
        if (args.length > 1) {
            reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        } else {
            reason = null;
        }

        try {
            IPUtils.Network network;
            boolean isNetwork = false;
            if (args[0].contains("/")) { // Check if CIDR notation
                network = IPUtils.Network.byCIDR(args[0]);
                isNetwork = true;
            } else {
                network = IPUtils.Network.byIP(args[0]);
            }

            int count = 0;
            for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
                if (IPUtils.isIPinNetwork(network, pp.getAddress().getAddress().getAddress())) {
                    if (isNetwork) {
                        pp.disconnect(
                                MessageGenerator.kickMessage(
                                        pp,
                                        reason == null
                                                ? Translation.text(
                                                        Translations.KICKIP_DEFAULT_REASON_NETWORK,
                                                        pp,
                                                        args[0])
                                                : reason));
                    } else {
                        pp.disconnect(
                                MessageGenerator.kickMessage(
                                        pp,
                                        reason == null
                                                ? Translation.text(
                                                        Translations.KICKIP_DEFAULT_REASON_IP,
                                                        pp,
                                                        args[0])
                                                : reason));
                    }
                    count++;
                }
            }
            sender.sendMessage(
                    Translation.component(
                            Translations.KICKIP_COMMAND_SUCCESS, sender, count, args[0]));
        } catch (IPUtils.InvalidIPAddressException ex) {
            sender.sendMessage(
                    Translation.component(Translations.KICKIP_COMMAND_INVALID_IP, sender, args[0]));
            ProxyServer.getInstance().getLogger().log(Level.WARNING, "IP address error", ex);
        }
    }
}
