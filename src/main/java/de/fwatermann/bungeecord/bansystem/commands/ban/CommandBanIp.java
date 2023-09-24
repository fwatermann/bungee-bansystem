package de.fwatermann.bungeecord.bansystem.commands.ban;

import de.fwatermann.bungeecord.bansystem.Permissions;
import de.fwatermann.bungeecord.bansystem.Translations;
import de.fwatermann.bungeecord.bansystem.database.Database;
import de.fwatermann.bungeecord.bansystem.database.status.IPBanStatus;
import de.fwatermann.bungeecord.bansystem.translation.Translation;
import de.fwatermann.bungeecord.bansystem.util.IPUtils;
import de.fwatermann.bungeecord.bansystem.util.MessageGenerator;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandBanIp extends Command {
    public CommandBanIp() {
        super("banip");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permissions.COMMAND_BAN_IP)
                || !sender.hasPermission(Permissions.COMMAND_BAN_IP)) {
            sender.sendMessage(Translation.component(Translations.NO_PERMISSIONS, sender));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(Translation.component(Translations.BANIP_COMMAND_USAGE, sender));
            return;
        }

        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));
        ;
        arguments.remove(0);

        long duration = 0L;
        if (arguments.contains("-t")) {
            int index = arguments.indexOf("-t");
            if (arguments.size() < index + 2) {
                sender.sendMessage(
                        Translation.component(Translations.BAN_COMMAND_USAGE_TIME, sender));
                return;
            }
            String time = arguments.get(index + 1);
            try {
                duration = CommandBan.parseTime(time, 12 * 60 * 60 * 1000L);
                if (duration > 12 * 60 * 60 * 1000L) {
                    sender.sendMessage(
                            Translation.component(
                                    Translations.BANIP_COMMAND_ERROR_TIME_TOO_LONG, sender));
                    return;
                }
            } catch (NumberFormatException ex) {
                sender.sendMessage(
                        Translation.component(Translations.BAN_COMMAND_USAGE_TIME, sender));
                return;
            }
            arguments.remove(index);
            arguments.remove(index);
        } else {
            duration = 12 * 60 * 60 * 1000L;
        }

        String reason = String.join(" ", arguments);

        String target = args[0];
        ProxiedPlayer targetPlayer;
        if ((targetPlayer = ProxyServer.getInstance().getPlayer(target)) != null) {
            String ipAddress = targetPlayer.getAddress().getAddress().getHostAddress();
            if (!sender.hasPermission(Permissions.BAN_STAFF)
                    && targetPlayer.hasPermission(Permissions.STAFF)) {
                sender.sendMessage(Translation.component(Translations.BAN_ERROR_STAFF, sender));
                return;
            }
            IPBanStatus ipban = Database.addIPBan(ipAddress, reason, duration, false);
            targetPlayer.disconnect(
                    MessageGenerator.banMessage(
                            targetPlayer,
                            reason,
                            ipban.banId(),
                            System.currentTimeMillis(),
                            System.currentTimeMillis() + duration));
            return;
        }

        try {
            IPBanStatus ipban = Database.addIPBan(target, reason, duration, false);
            IPUtils.Network network =
                    target.contains("/")
                            ? IPUtils.Network.byCIDR(target)
                            : IPUtils.Network.byIP(target);
            for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
                if (IPUtils.isIPinNetwork(network, pp.getAddress().getAddress().getAddress())) {
                    pp.disconnect(
                            MessageGenerator.banMessage(
                                    pp,
                                    reason,
                                    ipban.banId(),
                                    System.currentTimeMillis(),
                                    System.currentTimeMillis() + duration));
                }
            }
        } catch (IPUtils.InvalidIPAddressException e) {
            if (target.contains("/")) {
                sender.sendMessage(
                        Translation.component(
                                Translations.BANIP_COMMAND_ERROR_INVALID_CIDR, sender, target));
            } else {
                sender.sendMessage(
                        Translation.component(
                                Translations.BANIP_COMMAND_ERROR_INVALID_IP, sender, target));
            }
        }
    }
}
