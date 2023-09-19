package de.fwatermann.bungeecord.bansystem;

import de.fwatermann.bungeecord.bansystem.commands.CommandKick;
import de.fwatermann.bungeecord.bansystem.listener.PlayerChatListener;
import de.fwatermann.bungeecord.bansystem.listener.PlayerLoginListener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.util.Locale;

public final class BanSystem extends Plugin {

    private static BanSystem INSTANCE;

    public static BanSystem getInstance() {
        return INSTANCE;
    }

    public BanSystem() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        Locale.setDefault(Locale.US);

        PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();

        pluginManager.registerListener(this, new PlayerLoginListener());
        pluginManager.registerListener(this, new PlayerChatListener());

        pluginManager.registerCommand(this, new CommandKick());

        Locale locale = Locale.getDefault();
        System.out.println(
                "Default: "
                        + locale.getLanguage().toLowerCase()
                        + "_"
                        + locale.getCountry().toUpperCase());
        System.out.println(
                "English (US): "
                        + Locale.US.getLanguage().toLowerCase()
                        + "_"
                        + Locale.US.getCountry().toUpperCase());
        System.out.println(
                "Deutsch (DE): "
                        + Locale.GERMANY.getLanguage().toLowerCase()
                        + "_"
                        + Locale.GERMANY.getCountry().toUpperCase());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
