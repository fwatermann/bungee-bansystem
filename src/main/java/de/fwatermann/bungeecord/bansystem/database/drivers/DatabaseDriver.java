package de.fwatermann.bungeecord.bansystem.database.drivers;

import de.fwatermann.bungeecord.bansystem.database.status.BanStatus;
import de.fwatermann.bungeecord.bansystem.database.status.IPBanStatus;
import de.fwatermann.bungeecord.bansystem.database.status.MuteStatus;

import java.util.UUID;

public abstract class DatabaseDriver {

    /**
     * Initialize the database.
     *
     * <p>This methods should be overridden by the driver. (e.g. create tables)
     */
    public void init() {
        // Nothing to do here.
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
    public abstract IPBanStatus getIPBanStatus(String ip);

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
    public abstract String addIPBan(String ip, String reason, long duration, boolean xban);

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
