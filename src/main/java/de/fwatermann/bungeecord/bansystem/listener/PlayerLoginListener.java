package de.fwatermann.bungeecord.bansystem.listener;

import de.fwatermann.bungeecord.bansystem.database.Database;
import de.fwatermann.bungeecord.bansystem.database.status.BanStatus;
import de.fwatermann.bungeecord.bansystem.database.status.IPBanStatus;
import de.fwatermann.bungeecord.bansystem.util.MessageGenerator;

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerLoginListener implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        BanStatus statusPlayer = Database.getBanStatus(event.getPlayer().getUniqueId());
        if (statusPlayer.banned()) {
            event.getPlayer()
                    .disconnect(
                            MessageGenerator.banMessage(
                                    event.getPlayer().getLocale(),
                                    statusPlayer.reason(),
                                    statusPlayer.banId(),
                                    statusPlayer.start(),
                                    statusPlayer.end()));
            return;
        }

        IPBanStatus statusIP =
                Database.getIPBanStatus(
                        event.getPlayer().getAddress().getAddress().getHostAddress());
        if (statusIP.banned()) {
            if (statusIP.xban()) {
                // Ban account
                Database.addBan(
                        event.getPlayer().getUniqueId(),
                        statusIP.reason(),
                        Math.max(statusIP.end() - System.currentTimeMillis(), 0));
            }
            event.getPlayer()
                    .disconnect(
                            MessageGenerator.banMessage(
                                    event.getPlayer().getLocale(),
                                    statusIP.reason(),
                                    statusIP.banId(),
                                    statusIP.start(),
                                    statusIP.end()));
        }
    }
}
