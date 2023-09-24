package de.fwatermann.bungeecord.bansystem.commands.ban;

import de.fwatermann.bungeecord.bansystem.Permissions;
import de.fwatermann.bungeecord.bansystem.Translations;
import de.fwatermann.bungeecord.bansystem.database.Database;
import de.fwatermann.bungeecord.bansystem.database.status.BanStatus;
import de.fwatermann.bungeecord.bansystem.translation.Translation;
import de.fwatermann.bungeecord.bansystem.util.MessageGenerator;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandBan extends Command {

    private static Pattern regex_Time = Pattern.compile("([0-9]+)?(s|mo|h|d|w|m|y)");

    public CommandBan() {
        super("ban");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permissions.COMMAND_BAN)) {
            sender.sendMessage(Translation.component(Translations.NO_PERMISSIONS, sender));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(Translation.component(Translations.BAN_COMMAND_USAGE, sender));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(
                    Translation.component(Translations.PLAYER_NOT_FOUND, sender, args[0]));
            return;
        }

        if (target.hasPermission(Permissions.STAFF)
                && !sender.hasPermission(Permissions.BAN_STAFF)) {
            sender.sendMessage(Translation.component(Translations.BAN_ERROR_STAFF, sender));
            return;
        }

        long duration = 0L;
        boolean banIp = false;
        boolean xban = false;
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));
        arguments.remove(0);

        if (arguments.contains("-t")) {
            if (arguments.contains("-perma")) {
                sender.sendMessage(
                        Translation.component(
                                Translations.BAN_COMMAND_USAGE_TIME_COMB_PERMA, sender));
                return;
            }
            int index = arguments.indexOf("-t");
            if (arguments.size() < index + 2) {
                sender.sendMessage(
                        Translation.component(Translations.BAN_COMMAND_USAGE_TIME, sender));
                return;
            }
            String timeStr = arguments.get(index + 1);
            duration = parseTime(timeStr, 0);

            arguments.remove(index);
            arguments.remove(index);
        }

        if (arguments.contains("-perma")) {
            duration = -1;
            arguments.remove("-perma");
        }

        if (arguments.contains("-ip")) {
            if (arguments.contains("-xip")) {
                sender.sendMessage(
                        Translation.component(Translations.BAN_COMMAND_USAGE_IP_COMB_XIP, sender));
                return;
            }
            banIp = true;
            arguments.remove("-ip");
        }

        if (arguments.contains("-xip")) {
            xban = true;
            arguments.remove("-xip");
        }

        if (duration == 0) {
            duration = 1000L * 60L * 60L * 24L * 30L; // 30 days
        }

        String reason = String.join(" ", arguments);

        if (reason.isEmpty()) {
            sender.sendMessage(Translation.component(Translations.BAN_COMMAND_USAGE, sender));
            return;
        }

        BanStatus ban = Database.addBan(target.getUniqueId(), reason, duration);

        target.disconnect(
                MessageGenerator.banMessage(
                        target, reason, ban.banId(), System.currentTimeMillis(), ban.end()));

        if (banIp) {
            Database.addIPBan(
                    target.getAddress().getAddress().getHostAddress(), reason, duration, xban);
            for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
                if (pp.getAddress()
                        .getAddress()
                        .getHostAddress()
                        .equals(target.getAddress().getAddress().getHostAddress())) {
                    pp.disconnect(
                            MessageGenerator.banMessage(
                                    pp,
                                    reason,
                                    ban.banId(),
                                    System.currentTimeMillis(),
                                    ban.end()));
                }
            }
        }

        sender.sendMessage(
                Translation.component(Translations.BAN_COMMAND_SUCCESS, sender, target.getName()));
    }

    /**
     * Parse time string to duration in milliseconds.
     *
     * @param timeStr Time string
     * @param defaultTime Default time in milliseconds
     * @return Duration in milliseconds (-1 if invalid)
     */
    public static long parseTime(String timeStr, long defaultTime) throws NumberFormatException {
        long duration = 0;
        Matcher matcher = regex_Time.matcher(timeStr);
        while (matcher.find()) {
            String unit = matcher.group(2);
            int amount = Integer.parseInt(matcher.group(1));
            switch (unit) {
                case "s" -> // Second
                duration += amount * 1000L;
                case "m" -> // Minute
                duration += amount * 1000L * 60L;
                case "h" -> // Hour
                duration += amount * 1000L * 60L * 60L;
                case "d" -> // Day
                duration += amount * 1000L * 60L * 60L * 24L;
                case "w" -> // Week
                duration += amount * 1000L * 60L * 60L * 24L * 7L;
                case "mo" -> // Month (30 days)
                duration += amount * 1000L * 60L * 60L * 24L * 30L;
                case "y" -> // Year (365 days)
                duration += amount * 1000L * 60L * 60L * 24L * 365L;
                default -> {
                    throw new NumberFormatException("Invalid time unit: " + unit);
                }
            }
        }
        return duration;
    }
}
