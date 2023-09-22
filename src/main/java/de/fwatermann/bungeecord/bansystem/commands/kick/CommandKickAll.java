package de.fwatermann.bungeecord.bansystem.commands.kick;

import de.fwatermann.bungeecord.bansystem.Permissions;
import de.fwatermann.bungeecord.bansystem.Translations;
import de.fwatermann.bungeecord.bansystem.translation.Translation;
import de.fwatermann.bungeecord.bansystem.util.MessageGenerator;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;

public class CommandKickAll extends Command {

    public CommandKickAll() {
        super("kickall");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permissions.COMMAND_KICK)
                || !sender.hasPermission(Permissions.COMMAND_KICK_ALL)) {
            sender.sendMessage(Translation.component(Translations.NO_PERMISSIONS, sender));
            return;
        }

        boolean kickStaff = sender.hasPermission(Permissions.KICK_STAFF);

        String reason = null;
        if (args.length > 0) {
            reason = String.join(" ", args);
        }

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
                                    ? Translation.text(Translations.KICKALL_DEFAULT_REASON, pp)
                                    : reason));
        }
        if (staff.size() > 0) {
            sender.sendMessage(
                    Translation.component(
                            Translations.KICKALL_COMMAND_NOTICE_SKIP_STAFF,
                            sender,
                            String.join(", ", staff)));
        }
        sender.sendMessage(Translation.component(Translations.KICKALL_COMMAND_SUCCESS, sender));
    }
}
