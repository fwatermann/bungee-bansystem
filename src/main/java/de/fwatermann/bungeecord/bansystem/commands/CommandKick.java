package de.fwatermann.bungeecord.bansystem.commands;

import de.fwatermann.bungeecord.bansystem.Permissions;
import de.fwatermann.bungeecord.bansystem.Translations;
import de.fwatermann.bungeecord.bansystem.translation.Translation;
import de.fwatermann.bungeecord.bansystem.util.MessageGenerator;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;

public class CommandKick extends Command {

    public CommandKick() {
        super("kick", Permissions.KICK);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permissions.KICK)) {
            sender.sendMessage(Translation.component(Translations.NO_PERMISSIONS, sender));
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(Translation.component(Translations.COMMAND_KICK_USAGE, sender));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(
                    Translation.component(Translations.PLAYER_NOT_FOUND, sender, args[0]));
            return;
        }

        if (args.length == 1) {
            target.disconnect(
                    MessageGenerator.kickMessage(
                            target, Translation.text(Translations.KICK_DEFAULT_REASON, target)));
            sender.sendMessage(
                    Translation.component(Translations.KICK_SUCCESS, sender, target.getName()));
        } else {
            String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            target.disconnect(MessageGenerator.kickMessage(target, reason));
            sender.sendMessage(
                    Translation.component(Translations.KICK_SUCCESS, sender, target.getName()));
        }
    }
}
