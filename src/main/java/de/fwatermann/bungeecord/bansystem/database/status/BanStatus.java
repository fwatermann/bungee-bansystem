package de.fwatermann.bungeecord.bansystem.database.status;

public class BanStatus {

    private final String reason;
    private final String banId;
    private final long start, end;

    /**
     * Create a new BanStatus object.
     *
     * @param reason Reason for the ban
     * @param since Start time of the ban
     * @param until End time of the ban. If -1 the ban is permanent.
     * @param banId ID of the ban
     */
    public BanStatus(String reason, long since, long until, String banId) {
        this.reason = reason;
        this.start = since;
        this.end = until;
        this.banId = banId;
    }

    /**
     * Get BanStatus object that represents a not banned player.
     *
     * @return BanStatus object
     */
    public static BanStatus notBanned() {
        return new BanStatus(null, 0L, 0L, null);
    }

    /**
     * Get the reason for the ban.
     *
     * @return Reason for the ban
     */
    public String reason() {
        return this.reason;
    }

    /**
     * Get the ID of the ban.
     *
     * @return ID of the ban
     */
    public String banId() {
        return this.banId;
    }

    /**
     * Get the start time of the ban.
     *
     * @return Start time of the ban
     */
    public long start() {
        return this.start;
    }

    /**
     * Get the end time of the ban.
     *
     * @return End time of the ban
     */
    public long end() {
        return this.end;
    }

    /**
     * Check if the ban is permanent.
     *
     * @return True if the ban is permanent
     */
    public boolean banned() {
        return this.end >= System.currentTimeMillis() || this.end == -1;
    }
}
