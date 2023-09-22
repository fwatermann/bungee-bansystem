package de.fwatermann.bungeecord.bansystem;

import de.fwatermann.bungeecord.bansystem.commands.kick.CommandKick;
import de.fwatermann.bungeecord.bansystem.commands.kick.CommandKickAll;
import de.fwatermann.bungeecord.bansystem.commands.kick.CommandKickIP;
import de.fwatermann.bungeecord.bansystem.commands.kick.CommandKickServer;
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
        pluginManager.registerCommand(this, new CommandKickAll());
        pluginManager.registerCommand(this, new CommandKickServer());
        pluginManager.registerCommand(this, new CommandKickIP());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
