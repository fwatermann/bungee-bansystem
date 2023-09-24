package de.fwatermann.bungeecord.bansystem.database.drivers;

import de.fwatermann.bungeecord.bansystem.BanSystem;
import de.fwatermann.bungeecord.bansystem.database.status.BanStatus;
import de.fwatermann.bungeecord.bansystem.database.status.IPBanStatus;
import de.fwatermann.bungeecord.bansystem.database.status.MuteStatus;
import de.fwatermann.bungeecord.bansystem.util.FileUtils;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLDatabase extends DatabaseDriver {

    private Connection connection;

    @Override
    public void init() {
        // Load config in Data dir
        File dbConfigFile = new File(BanSystem.getInstance().getDataFolder(), "mysql.yml");

        if (!dbConfigFile.exists()) {
            try {
                FileUtils.inputStreamToFile(
                        BanSystem.getInstance().getResourceAsStream("config/mysql.yml"),
                        dbConfigFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Configuration config =
                    YamlConfiguration.getProvider(YamlConfiguration.class).load(dbConfigFile);
            String host = config.getString("host");
            int port = config.getInt("port");
            String database = config.getString("database");
            String username = config.getString("username");
            String password = config.getString("password");
            boolean ssl = config.getBoolean("ssl");

            String url =
                    String.format("jdbc:mysql://%s:%d/%s?useSSL=%b", host, port, database, ssl);
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BanStatus getBanStatus(UUID uuid) {
        return null;
    }

    @Override
    public IPBanStatus getIPBanStatus(String ip) {
        return null;
    }

    @Override
    public MuteStatus getMuteStatus(UUID uuid) {
        return null;
    }

    @Override
    public String addBan(UUID uuid, String reason, long duration) {
        return null;
    }

    @Override
    public String addIPBan(String ip, String reason, long duration, boolean xban) {
        return null;
    }

    @Override
    public String addMute(UUID uuid, String reason, long duration) {
        return null;
    }
}
