package de.fwatermann.bungeecord.bansystem;

import de.fwatermann.bungeecord.bansystem.listener.PlayerChatListener;
import de.fwatermann.bungeecord.bansystem.listener.PlayerLoginListener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

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
        ProxyServer.getInstance()
                .getPluginManager()
                .registerListener(this, new PlayerLoginListener());
        ProxyServer.getInstance()
                .getPluginManager()
                .registerListener(this, new PlayerChatListener());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
