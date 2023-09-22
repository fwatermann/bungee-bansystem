package de.fwatermann.bungeecord.bansystem.database;

public record MuteStatus(boolean muted, String reason, long since, long until, String muteId) {

    public static MuteStatus notMuted() {
        return new MuteStatus(false, null, 0L, 0L, null);
    }
}
