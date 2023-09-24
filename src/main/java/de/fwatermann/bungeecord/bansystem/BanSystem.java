package de.fwatermann.bungeecord.bansystem;

import de.fwatermann.bungeecord.bansystem.commands.ban.CommandBan;
import de.fwatermann.bungeecord.bansystem.commands.ban.CommandBanIp;
import de.fwatermann.bungeecord.bansystem.commands.kick.CommandKick;
import de.fwatermann.bungeecord.bansystem.commands.kick.CommandKickAll;
import de.fwatermann.bungeecord.bansystem.commands.kick.CommandKickIP;
import de.fwatermann.bungeecord.bansystem.commands.kick.CommandKickServer;
import de.fwatermann.bungeecord.bansystem.listener.PlayerChatListener;
import de.fwatermann.bungeecord.bansystem.listener.PlayerLoginListener;
import de.fwatermann.bungeecord.bansystem.util.FileUtils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public final class BanSystem extends Plugin {

    private static BanSystem INSTANCE;

    public static BanSystem getInstance() {
        return INSTANCE;
    }

    public BanSystem() {
        INSTANCE = this;
    }

    public Configuration config;

    @Override
    public void onEnable() {
        Locale.setDefault(Locale.US);

        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                FileUtils.inputStreamToFile(
                        this.getResourceAsStream("config/config.yml"), configFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            this.config =
                    ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();

        pluginManager.registerListener(this, new PlayerLoginListener());
        pluginManager.registerListener(this, new PlayerChatListener());

        pluginManager.registerCommand(this, new CommandKick());
        pluginManager.registerCommand(this, new CommandKickAll());
        pluginManager.registerCommand(this, new CommandKickServer());
        pluginManager.registerCommand(this, new CommandKickIP());

        pluginManager.registerCommand(this, new CommandBan());
        pluginManager.registerCommand(this, new CommandBanIp());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
