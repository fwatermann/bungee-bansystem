package de.fwatermann.bungeecord.bansystem.database;

import de.fwatermann.bungeecord.bansystem.BanSystem;
import de.fwatermann.bungeecord.bansystem.database.drivers.DatabaseDriver;
import de.fwatermann.bungeecord.bansystem.database.status.BanStatus;
import de.fwatermann.bungeecord.bansystem.database.status.IPBanStatus;
import de.fwatermann.bungeecord.bansystem.database.status.MuteStatus;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.logging.Level;

public class Database {

    private static final Cache<UUID, BanStatus> cache_ban = new Cache<>();
    private static final Cache<String, IPBanStatus> cache_ipban = new Cache<>();
    private static final Cache<UUID, MuteStatus> cache_mute = new Cache<>();
    private static final Cache<UUID, String> cache_name = new Cache<>();
    private static final Cache<String, UUID> cache_uuid =
            new Cache<>(60000, 1000, String::toLowerCase);

    private static final DatabaseDriver driver;

    static {
        String driverName = BanSystem.getInstance().config.getString("database.driver");
        if (driverName == null) {
            BanSystem.getInstance()
                    .getLogger()
                    .log(Level.WARNING, "No database driver specified, using memory driver!");
            driverName = "memory";
        }

        switch (driverName.toLowerCase()) {
            case "memory" -> driver =
                    new de.fwatermann.bungeecord.bansystem.database.drivers.MemoryDatabase();
            case "mysql" -> driver =
                    new de.fwatermann.bungeecord.bansystem.database.drivers.MySQLDatabase();
            default -> {
                try {
                    Class<?> clazz = Class.forName(driverName);
                    if (!DatabaseDriver.class.isAssignableFrom(clazz)) {
                        throw new RuntimeException("Driver class must implement DatabaseDriver!");
                    }
                    driver = (DatabaseDriver) clazz.getConstructors()[0].newInstance();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Database driver class not found!", e);
                } catch (InstantiationException
                        | IllegalAccessException
                        | InvocationTargetException e) {
                    throw new RuntimeException("Failed to instantiate database driver!", e);
                }
            }
        }
        driver.init();
    }

    /**
     * Get ban status of a player by its UUID.
     *
     * @param uuid UUID of the player
     * @return BanStatus object
     */
    public static BanStatus getBanStatus(UUID uuid) {
        return cache_ban.lookup(uuid, () -> driver.getBanStatus(uuid));
    }

    /**
     * Get ban status of an IP.
     *
     * @param ip IP to check
     * @return BanStatus object
     */
    public static IPBanStatus getIPBanStatus(String ip) {
        return cache_ipban.lookup(ip, () -> driver.getIPBanStatus(ip));
    }

    /**
     * Get mute status of a player by its UUID.
     *
     * @param uuid UUID of the player
     * @return MuteStatus object
     */
    public static MuteStatus getMuteStatus(UUID uuid) {
        return cache_mute.lookup(uuid, () -> driver.getMuteStatus(uuid));
    }

    /**
     * Ban a player.
     *
     * @param uuid UUID of the player
     * @param reason Reason for the ban
     * @param duration Duration of the ban in milliseconds, -1 for permanent.
     * @return BanStatus object
     */
    public static BanStatus addBan(UUID uuid, String reason, long duration) {
        String id = driver.addBan(uuid, reason, duration);
        BanStatus status =
                new BanStatus(
                        reason,
                        System.currentTimeMillis(),
                        duration == -1 ? -1 : System.currentTimeMillis() + duration,
                        id);
        cache_ban.put(uuid, status);
        return status;
    }

    /**
     * Ban an IP. This does not work with network addresses.
     *
     * @param ip IP to ban
     * @param reason Reason for the ban
     * @param duration Duration of the ban in milliseconds, -1 for permanent.
     * @return IPBanStatus object
     */
    public static IPBanStatus addIPBan(String ip, String reason, long duration, boolean xban) {
        String id = driver.addIPBan(ip, reason, duration, xban);
        IPBanStatus status =
                new IPBanStatus(
                        reason,
                        System.currentTimeMillis(),
                        duration == -1 ? -1 : System.currentTimeMillis() + duration,
                        id,
                        xban);
        cache_ipban.put(ip, status);
        return status;
    }

    /**
     * Mute a player.
     *
     * @param uuid UUID of the player
     * @param reason Reason for the mute
     * @param duration Duration of the mute in milliseconds, -1 for permanent.
     * @return ID of the mute
     */
    public static MuteStatus addMute(UUID uuid, String reason, long duration) {
        String id = driver.addMute(uuid, reason, duration);
        MuteStatus status =
                new MuteStatus(
                        reason,
                        System.currentTimeMillis(),
                        duration == -1 ? -1 : System.currentTimeMillis() + duration,
                        id);
        cache_mute.put(uuid, status);
        return status;
    }

    /**
     * Update player database entry.
     *
     * @param uuid UUID of the player
     * @param name Name of the player
     */
    public static void updatePlayer(UUID uuid, String name) {
        cache_uuid.put(name.toLowerCase(), uuid);
        cache_name.put(uuid, name);
        driver.updatePlayerEntry(uuid, name);
    }

    /**
     * Get the name of a player by its UUID.
     *
     * @param uuid UUID of the player
     * @return Name of the player
     */
    public static String getNameByUUID(UUID uuid) {
        return cache_name.lookup(uuid, () -> driver.getNameByUUID(uuid));
    }

    /**
     * Get the UUID of a player by its name.
     *
     * @param name Name of the player
     * @return UUID of the player
     */
    public static UUID getUUIDByName(String name) {
        return cache_uuid.lookup(name, () -> driver.getUUIDByName(name));
    }
}
