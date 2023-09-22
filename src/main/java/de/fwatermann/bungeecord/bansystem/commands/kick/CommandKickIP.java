package de.fwatermann.bungeecord.bansystem.commands.kick;

import de.fwatermann.bungeecord.bansystem.Permissions;
import de.fwatermann.bungeecord.bansystem.Translations;
import de.fwatermann.bungeecord.bansystem.translation.Translation;
import de.fwatermann.bungeecord.bansystem.util.IPUtils;
import de.fwatermann.bungeecord.bansystem.util.MessageGenerator;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.logging.Level;

public class CommandKickIP extends Command {

    public CommandKickIP() {
        super("kickip");
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
