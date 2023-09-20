package de.fwatermann.bungeecord.bansystem.commands;

import de.fwatermann.bungeecord.bansystem.Permissions;
import de.fwatermann.bungeecord.bansystem.Translations;
import de.fwatermann.bungeecord.bansystem.translation.Translation;
import de.fwatermann.bungeecord.bansystem.util.MessageGenerator;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;

public class CommandKickServer extends Command {

    public CommandKickServer() {
        super("kickserver");
    }

    private static void kickAllFromServer(ServerInfo server, String reason) {

        server.getPlayers()
                .forEach(
                        pp -> {
                            pp.disconnect(
                                    MessageGenerator.kickMessage(
                                            pp,
                                            reason == null
                                                    ? Translation.text(
                                                            Translations.KICKSERVER_DEFAULT_REASON,
                                                            pp)
                                                    : reason));
                        });
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permissions.KICK_SERVER)
                || !sender.hasPermission(Permissions.KICK)) {
            sender.sendMessage(Translation.component(Translations.NO_PERMISSIONS, sender));
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(
                    Translation.component(Translations.KICKSERVER_COMMAND_USAGE, sender));
            return;
        }

        ServerInfo server;
        if (args[0].equalsIgnoreCase("this")) {
            if (!(sender instanceof ProxiedPlayer pp)) {
                sender.sendMessage(
                        Translation.component(Translations.KICKSERVER_NOT_A_PLAYER, sender));
                return;
            }
            server = pp.getServer().getInfo();
        } else {
            server = ProxyServer.getInstance().getServerInfo(args[0]);
        }
        if (server == null) {
            sender.sendMessage(
                    Translation.component(
                            Translations.KICKSERVER_SERVER_NOT_FOUND, sender, args[0]));
            return;
        }

        if (args.length == 1) {
            kickAllFromServer(server, null);
            sender.sendMessage(
                    Translation.component(
                            Translations.KICKSERVER_SUCCESS, sender, server.getName()));
            return;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        kickAllFromServer(server, reason);
        sender.sendMessage(
                Translation.component(Translations.KICKSERVER_SUCCESS, sender, server.getName()));
    }
}
