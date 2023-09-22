package de.fwatermann.bungeecord.bansystem.commands.kick;

import de.fwatermann.bungeecord.bansystem.Permissions;
import de.fwatermann.bungeecord.bansystem.Translations;
import de.fwatermann.bungeecord.bansystem.translation.Translation;
import de.fwatermann.bungeecord.bansystem.util.MessageGenerator;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandKickServer extends Command {

    public CommandKickServer() {
        super("kickserver");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permissions.COMMAND_KICK_SERVER)
                || !sender.hasPermission(Permissions.COMMAND_KICK)) {
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
                        Translation.component(
                                Translations.KICKSERVER_COMMAND_ERROR_NOT_A_PLAYER, sender));
                return;
            }
            server = pp.getServer().getInfo();
        } else {
            server = ProxyServer.getInstance().getServerInfo(args[0]);
        }
        if (server == null) {
            sender.sendMessage(
                    Translation.component(
                            Translations.KICKSERVER_COMMAND_ERROR_SERVER_NOT_FOUND,
                            sender,
                            args[0]));
            return;
        }

        String reason = null;
        if (args.length > 1) {
            reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        }

        boolean kickStaff = sender.hasPermission(Permissions.KICK_STAFF);
        ArrayList<String> staff = new ArrayList<>();
        for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
            if (!kickStaff && pp.hasPermission(Permissions.STAFF)) {
                staff.add(pp.getName());
                continue;
            }
            pp.disconnect(
                    MessageGenerator.kickMessage(
                            pp,
                            reason == null
                                    ? Translation.text(Translations.KICKSERVER_DEFAULT_REASON, pp)
                                    : reason));
        }
        if (staff.size() > 0) {
            sender.sendMessage(
                    Translation.component(
                            Translations.KICKSERVER_COMMAND_NOTICE_SKIP_STAFF,
                            sender,
                            String.join(", ", staff)));
        }
        sender.sendMessage(
                Translation.component(
                        Translations.KICKSERVER_COMMAND_SUCCESS, sender, server.getName()));
    }
}
