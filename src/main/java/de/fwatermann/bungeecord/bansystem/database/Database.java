package de.fwatermann.bungeecord.bansystem.database;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/** This class is an abstraction layer for the database. */
public abstract class Database {

    private static Database instance;

    public static Database getInstance() {
        return instance;
    }

    public static void setDriver(Class<? extends Database> driver) {
        try {
            Database.instance = (Database) driver.getConstructors()[0].newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get ban status of a player by its UUID.
     *
     * @param uuid UUID of the player
     * @return BanStatus object
     */
    public abstract BanStatus getBanStatus(UUID uuid);

    /**
     * Get ban status of an IP.
     *
     * @param ip IP to check
     * @return BanStatus object
     */
    public abstract BanStatus getIPBanStatus(String ip);

    /**
     * Get mute status of a player by its UUID.
     *
     * @param uuid UUID of the player
     * @return MuteStatus object
     */
    public abstract MuteStatus getMuteStatus(UUID uuid);

    /**
     * Ban a player.
     *
     * @param uuid UUID of the player
     * @param reason Reason for the ban
     * @param duration Duration of the ban in milliseconds, -1 for permanent.
     * @return ID of the ban
     */
    public abstract String addBan(UUID uuid, String reason, long duration);

    /**
     * Ban an IP. This does not work with network addresses.
     *
     * @param ip IP to ban
     * @param reason Reason for the ban
     * @param duration Duration of the ban in milliseconds, -1 for permanent.
     * @return ID of the ban
     */
    public abstract String addIPBan(String ip, String reason, long duration);

    /**
     * Mute a player.
     *
     * @param uuid UUID of the player
     * @param reason Reason for the mute
     * @param duration Duration of the mute in milliseconds, -1 for permanent.
     * @return ID of the mute
     */
    public abstract String addMute(UUID uuid, String reason, long duration);
}
