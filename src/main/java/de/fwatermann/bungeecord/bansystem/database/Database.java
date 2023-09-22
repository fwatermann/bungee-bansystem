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
     */
    public abstract void ban(UUID uuid, String reason, long duration);

    /**
     * Mute a player.
     *
     * @param uuid UUID of the player
     * @param reason Reason for the mute
     * @param duration Duration of the mute in milliseconds, -1 for permanent.
     */
    public abstract void mute(UUID uuid, String reason, long duration);
}
