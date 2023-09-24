package de.fwatermann.bungeecord.bansystem.database.status;

public class MuteStatus {

    private final String reason;
    private final String muteId;
    private final long start, end;

    /**
     * Create a new MuteStatus object.
     *
     * @param reason Reason for the mute
     * @param start Start time of the mute
     * @param end End time of the mute
     * @param muteId ID of the mute
     */
    public MuteStatus(String reason, long start, long end, String muteId) {
        this.reason = reason;
        this.muteId = muteId;
        this.start = start;
        this.end = end;
    }

    /**
     * Get MuteStatus object that represents a not muted player.
     *
     * @return MuteStatus object
     */
    public static MuteStatus notMuted() {
        return new MuteStatus(null, 0L, 0L, null);
    }

    /**
     * Get the reason for the mute.
     *
     * @return Reason for the mute
     */
    public String reason() {
        return this.reason;
    }

    /**
     * Get the ID of the mute.
     *
     * @return ID of the mute
     */
    public String muteId() {
        return this.muteId;
    }

    /**
     * Get the start time of the mute.
     *
     * @return Start time of the mute
     */
    public long start() {
        return this.start;
    }

    /**
     * Get the end time of the mute.
     *
     * @return End time of the mute
     */
    public long end() {
        return this.end;
    }

    public boolean muted() {
        return this.end >= System.currentTimeMillis() || this.end == -1;
    }
}
