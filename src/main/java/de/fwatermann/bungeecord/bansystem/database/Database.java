package de.fwatermann.bungeecord.bansystem.database;

import java.util.UUID;

/** This class is an abstraction layer for the database. */
public class Database {

    /**
     * Get ban status of a player by its UUID.
     *
     * @param uuid UUID of the player
     * @return BanStatus object
     */
    public static BanStatus getBanStatus(UUID uuid) {
        return new BanStatus(true, "illegal client modification", 0L, 1726263860000L, "a1b2c3d4e5");
    }

    /**
     * Get mute status of a player by its UUID.
     *
     * @param uuid UUID of the player
     * @return MuteStatus object
     */
    public static MuteStatus getMuteStatus(UUID uuid) {
        return new MuteStatus(
                true, "Insulting other players in chat", 0L, 1726263860000L, "a1b2c3d4e5");
    }
}
