package de.fwatermann.bungeecord.bansystem.database;

public record MuteStatus(boolean muted, String reason, long since, long until, String muteId) {}
