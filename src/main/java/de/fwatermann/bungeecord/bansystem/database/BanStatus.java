package de.fwatermann.bungeecord.bansystem.database;

public record BanStatus(boolean banned, String reason, long since, long until, String banId) {

    public static BanStatus notBanned() {
        return new BanStatus(false, null, 0L, 0L, null);
    }
}
