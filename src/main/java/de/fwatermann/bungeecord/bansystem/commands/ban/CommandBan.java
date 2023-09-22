package de.fwatermann.bungeecord.bansystem.commands.ban;

import de.fwatermann.bungeecord.bansystem.Permissions;
import de.fwatermann.bungeecord.bansystem.Translations;
import de.fwatermann.bungeecord.bansystem.database.Database;
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
            sender.sendMessage(Translation.component(Translations.PLAYER_NOT_FOUND, sender));
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

            // Parse timeStr to duration
            Matcher matcher = regex_Time.matcher(timeStr);
            while (matcher.find()) {
                String unit = matcher.group(2);
                int amount = Integer.parseInt(matcher.group(1));
                switch (unit) {
                    case "s": // Second
                        duration += amount * 1000;
                        break;
                    case "m": // Minute
                        duration += amount * 1000 * 60;
                        break;
                    case "h": // Hour
                        duration += amount * 1000 * 60 * 60;
                        break;
                    case "d": // Day
                        duration += amount * 1000 * 60 * 60 * 24;
                        break;
                    case "w": // Week
                        duration += amount * 1000 * 60 * 60 * 24 * 7;
                        break;
                    case "mo": // Month (30 days)
                        duration += amount * 1000 * 60 * 60 * 24 * 30;
                        break;
                    case "y": // Year (365 days)
                        duration += amount * 1000 * 60 * 60 * 24 * 365;
                        break;
                    default:
                        sender.sendMessage(
                                Translation.component(Translations.BAN_COMMAND_USAGE_TIME, sender));
                        return;
                }
            }

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

        String reason = String.join(" ", arguments);
        String banId = Database.getInstance().addBan(target.getUniqueId(), reason, duration);

        target.disconnect(
                MessageGenerator.banMessage(
                        target,
                        reason,
                        banId,
                        System.currentTimeMillis(),
                        System.currentTimeMillis() + duration));

        if (banIp) {
            Database.getInstance()
                    .addIPBan(target.getAddress().getAddress().getHostAddress(), reason, duration);
        }

        // TODO: Add xban

    }
}
