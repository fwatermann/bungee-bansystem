package de.fwatermann.bungeecord.bansystem.database;

public record BanStatus(boolean banned, String reason, long since, long until, String banId) {}
