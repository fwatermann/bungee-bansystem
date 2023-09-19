package de.fwatermann.bungeecord.bansystem.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Constants {

    public static final BaseComponent[] CHAT_PREFIX_MUTE =
            new ComponentBuilder()
                    .append("[")
                    .color(ChatColor.YELLOW)
                    .append("Mute")
                    .color(ChatColor.RED)
                    .append("] ")
                    .color(ChatColor.YELLOW)
                    .append("")
                    .color(ChatColor.RED)
                    .create();
    public static final BaseComponent[] CHAT_PREFIX_BAN =
            new ComponentBuilder()
                    .append("[")
                    .color(ChatColor.YELLOW)
                    .append("Ban")
                    .color(ChatColor.RED)
                    .append("] ")
                    .color(ChatColor.YELLOW)
                    .append("")
                    .color(ChatColor.RED)
                    .create();
}
