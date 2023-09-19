package de.fwatermann.bungeecord.bansystem.listener;

import de.fwatermann.bungeecord.bansystem.database.Database;
import de.fwatermann.bungeecord.bansystem.database.MuteStatus;
import de.fwatermann.bungeecord.bansystem.util.Constants;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onChat(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer pp)) return;
        if (event.getMessage().startsWith("/")) return;
        MuteStatus muteStatus = Database.getMuteStatus(pp.getUniqueId());
        if (muteStatus.muted()) {
            event.setCancelled(true);
            pp.sendMessage(
                    ChatMessageType.SYSTEM,
                    new ComponentBuilder().append(Constants.CHAT_PREFIX_MUTE).append("").create());
        }
    }
}
