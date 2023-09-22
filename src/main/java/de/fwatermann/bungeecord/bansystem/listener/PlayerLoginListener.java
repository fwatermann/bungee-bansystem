package de.fwatermann.bungeecord.bansystem.listener;

import de.fwatermann.bungeecord.bansystem.database.BanStatus;
import de.fwatermann.bungeecord.bansystem.database.Database;
import de.fwatermann.bungeecord.bansystem.util.MessageGenerator;

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerLoginListener implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        BanStatus status = Database.getInstance().getBanStatus(event.getPlayer().getUniqueId());
        if (status.banned()) {
            event.getPlayer()
                    .disconnect(
                            MessageGenerator.banMessage(
                                    event.getPlayer().getLocale(),
                                    status.reason(),
                                    status.banId(),
                                    status.since(),
                                    status.until()));
        }
    }
}
