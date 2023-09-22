package de.fwatermann.bungeecord.bansystem.commands.kick;

import de.fwatermann.bungeecord.bansystem.Permissions;
import de.fwatermann.bungeecord.bansystem.Translations;
import de.fwatermann.bungeecord.bansystem.translation.Translation;
import de.fwatermann.bungeecord.bansystem.util.MessageGenerator;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

public class CommandKickAll extends Command {

    public CommandKickAll() {
        super("kickall");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permissions.KICK)
                || !sender.hasPermission(Permissions.KICK_ALL)) {
            sender.sendMessage(Translation.component(Translations.NO_PERMISSIONS, sender));
            return;
        }
        if (args.length == 0) {
            ProxyServer.getInstance()
                    .getPlayers()
                    .forEach(
                            pp -> {
                                pp.disconnect(
                                        MessageGenerator.kickMessage(
                                                pp,
                                                Translation.text(
                                                        Translations.KICKALL_DEFAULT_REASON, pp)));
                            });
            sender.sendMessage(Translation.component(Translations.KICKALL_COMMAND_SUCCESS, sender));
            return;
        }
        String reason = String.join(" ", args);
        ProxyServer.getInstance()
                .getPlayers()
                .forEach(
                        pp -> {
                            pp.disconnect(MessageGenerator.kickMessage(pp, reason));
                        });
        sender.sendMessage(Translation.component(Translations.KICK_SUCCESS, sender, "all players"));
    }
}
