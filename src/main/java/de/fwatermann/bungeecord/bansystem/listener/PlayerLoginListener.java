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
        BanStatus statusPlayer =
                Database.getInstance().getBanStatus(event.getPlayer().getUniqueId());
        if (statusPlayer.banned()) {
            event.getPlayer()
                    .disconnect(
                            MessageGenerator.banMessage(
                                    event.getPlayer().getLocale(),
                                    statusPlayer.reason(),
                                    statusPlayer.banId(),
                                    statusPlayer.since(),
                                    statusPlayer.until()));
        }

        BanStatus statusIP =
                Database.getInstance()
                        .getIPBanStatus(
                                event.getPlayer().getAddress().getAddress().getHostAddress());
        if (statusIP.banned()) {
            event.getPlayer()
                    .disconnect(
                            MessageGenerator.banMessage(
                                    event.getPlayer().getLocale(),
                                    statusIP.reason(),
                                    statusIP.banId(),
                                    statusIP.since(),
                                    statusIP.until()));
        }
    }
}
