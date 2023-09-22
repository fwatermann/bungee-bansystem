package de.fwatermann.bungeecord.bansystem.util;

import de.fwatermann.bungeecord.bansystem.Translations;
import de.fwatermann.bungeecord.bansystem.translation.Translation;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Locale;

public class MessageGenerator {

    public static BaseComponent[] banMessage(
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
        if (pUntil == -1L) {
            time = Translation.component("login.ban.duration.permanent", locale);
        } else {
            ComponentBuilder builder = new ComponentBuilder();
            if (days > 0) {
                builder.append(Translation.component("login.ban.duration.days", locale, days));
                builder.append(" ");
            }
            if (hours > 0) {
                builder.append(Translation.component("login.ban.duration.hours", locale, hours));
                builder.append(" ");
            }
            if (minutes > 0) {
                builder.append(
                        Translation.component("login.ban.duration.minutes", locale, minutes));
                builder.append(" ");
            }
            if (seconds > 0) {
                builder.append(
                        Translation.component("login.ban.duration.seconds", locale, seconds));
            }
            time = builder.create();
            if (time.length == 0) {
                time =
                        new ComponentBuilder()
                                .append(
                                        Translation.component(
                                                "login.ban.duration.seconds", locale, 0))
                                .create();
            }
        }

        System.out.printf("pUntil: %d time.length: %d%n", pUntil, time.length);

        return new ComponentBuilder()
                .append(Translation.component("login.ban.spacer", locale))
                .append("\n\n")
                .append(Translation.component("login.ban.headline", locale))
                .append("\n\n")
                .append(Translation.component("login.ban.reason", locale, pReason))
                .append("\n\n")
                .append(Translation.component("login.ban.duration", locale))
                .append(" ")
                .append(time)
                .append("\n\n")
                .append(Translation.component("login.ban.spacer", locale))
                .append("\n")
                .append(Translation.component("login.ban.banId", locale, banId))
                .create();
    }

    public static BaseComponent[] banMessage(
            ProxiedPlayer pp, String reason, String banId, long since, long until) {
        return banMessage(pp.getLocale(), reason, banId, since, until);
    }

    public static BaseComponent[] kickMessage(Locale locale, String reason) {
        return new ComponentBuilder()
                .append(Translation.component(Translations.KICK_PLAYER_SPACER, locale))
                .append("\n\n")
                .append(Translation.component(Translations.KICK_PLAYER_HEADLINE, locale))
                .append("\n\n")
                .append(Translation.component(Translations.KICK_PLAYER_REASON, locale, reason))
                .append("\n\n")
                .append(Translation.component(Translations.KICK_PLAYER_SPACER, locale))
                .create();
    }

    public static BaseComponent[] kickMessage(ProxiedPlayer pp, String reason) {
        return kickMessage(pp.getLocale(), reason);
    }
}
