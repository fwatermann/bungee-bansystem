package de.fwatermann.bungeecord.bansystem;

public class Permissions {

    private static final String PREFIX = "bansystem.";

    private static String permission(String permission) {
        return PREFIX + permission;
    }

    public static final String STAFF = permission("staff");

    // ### KICK ###

    public static final String COMMAND_KICK = permission("kick");
    public static final String COMMAND_KICK_ALL = permission("kick.all");
    public static final String COMMAND_KICK_SERVER = permission("kick.server");
    public static final String COMMAND_KICK_IP = permission("kick.ip");
    public static final String KICK_STAFF = permission("kick.staff");
    public static final String COMMAND_BAN = permission("ban");
    public static final String COMMAND_BAN_IP = permission("ban.ip");
    public static final String COMMAND_UNBAN = permission("unban");
    public static final String COMMAND_UNBAN_IP = permission("unban.ip");
    public static final String BAN_STAFF = permission("ban.staff");
}
