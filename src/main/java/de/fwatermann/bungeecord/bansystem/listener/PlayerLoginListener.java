package de.fwatermann.bungeecord.bansystem.listener;

import de.fwatermann.bungeecord.bansystem.database.BanStatus;
import de.fwatermann.bungeecord.bansystem.database.Database;
import de.fwatermann.bungeecord.bansystem.translation.Translation;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PlayerLoginListener implements Listener {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        BanStatus status = Database.getBanStatus(event.getPlayer().getUniqueId());
        if (status.banned()) {
            event.getPlayer()
                    .disconnect(
                            this.generateBanMessage(
                                    event.getPlayer().getLocale(),
                                    status.reason(),
                                    status.banId(),
                                    status.since(),
                                    status.until()));
        }
    }

    private BaseComponent[] generateBanMessage(
            Locale locale, String pReason, String banId, long pSince, long pUntil) {

        long msUntilUnban = pUntil - System.currentTimeMillis();

        long days = msUntilUnban / 1000 / 60 / 60 / 24;
        long hours = (msUntilUnban - days * 1000 * 60 * 60 * 24) / 1000 / 60 / 60;
        long minutes =
                (msUntilUnban - days * 1000 * 60 * 60 * 24 - hours * 1000 * 60 * 60) / 1000 / 60;
        long seconds =
                (msUntilUnban
                                - days * 1000 * 60 * 60 * 24
                                - hours * 1000 * 60 * 60
                                - minutes * 1000 * 60)
                        / 1000;

        BaseComponent[] time;
        if(pUntil == -1) {
            time = Translation.component("login.ban.duration.permanent", locale);
        } else {
            ComponentBuilder builder = new ComponentBuilder();
            if(days > 0) {
                builder.append(Translation.component("login.ban.duration.days", locale, days));
                builder.append(" ");
            }
            if(hours > 0) {
                builder.append(Translation.component("login.ban.duration.hours", locale, hours));
                builder.append(" ");
            }
            if(minutes > 0) {
                builder.append(Translation.component("login.ban.duration.minutes", locale, minutes));
                builder.append(" ");
            }
            if(seconds > 0) {
                builder.append(Translation.component("login.ban.duration.seconds", locale, seconds));
            }
            time = builder.create();
        }

        return new ComponentBuilder()
                .append(Translation.component("login.ban.spacer", locale)).append("\n\n")
                .append(Translation.component("login.ban.headline", locale)).append("\n\n")
                .append(Translation.component("login.ban.reason", locale, pReason)).append("\n\n")
                .append(Translation.component("login.ban.duration", locale)).append(" ").append(time).append("\n\n")
                .append(Translation.component("login.ban.spacer", locale)).append("\n")
                .append(Translation.component("login.ban.banId", locale, banId))
                .create();
    }
}
