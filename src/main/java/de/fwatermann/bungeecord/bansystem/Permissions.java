package de.fwatermann.bungeecord.bansystem;

public class Permissions {

    private static final String PREFIX = "bansystem.";

    private static String permission(String permission) {
        return PREFIX + permission;
    }

    public static final String BANSYSTEM_STAFF = permission("staff");

    // ### KICK ###

    public static final String KICK = permission("kick");
    public static final String KICK_ALL = permission("kick.all");
    public static final String KICK_SERVER = permission("kick.server");
    public static final String KICK_IP = permission("kick.ip");
    public static final String KICK_STAFF = permission("kick.staff");
}
