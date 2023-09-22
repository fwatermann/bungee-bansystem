package de.fwatermann.bungeecord.bansystem.database.drivers;

import de.fwatermann.bungeecord.bansystem.database.BanStatus;
import de.fwatermann.bungeecord.bansystem.database.Database;
import de.fwatermann.bungeecord.bansystem.database.MuteStatus;

import java.util.HashMap;
import java.util.UUID;

public class MemoryDatabase extends Database {

    private HashMap<String, Ban> bans = new HashMap<>();
    private HashMap<String, Mute> mutes = new HashMap<>();

    @Override
    public BanStatus getBanStatus(UUID uuid) {
        Ban ban = this.bans.get(uuid.toString());
        if (ban == null) {
            return BanStatus.notBanned();
        }
        return new BanStatus(
                ban.end >= System.currentTimeMillis(), ban.reason, ban.start, ban.end, ban.id);
    }

    @Override
    public MuteStatus getMuteStatus(UUID uuid) {
        Mute mute = this.mutes.get(uuid.toString());
        if (mute == null) {
            return MuteStatus.notMuted();
        }
        return new MuteStatus(
                mute.end >= System.currentTimeMillis(), mute.reason, mute.start, mute.end, mute.id);
    }

    @Override
    public void ban(UUID uuid, String reason, long duration) {
        this.bans.put(
                uuid.toString(),
                new Ban(
                        UUID.randomUUID().toString(),
                        reason,
                        System.currentTimeMillis(),
                        duration == -1 ? -1 : System.currentTimeMillis() + duration));
    }

    @Override
    public void mute(UUID uuid, String reason, long duration) {
        this.mutes.put(
                uuid.toString(),
                new Mute(
                        UUID.randomUUID().toString(),
                        reason,
                        System.currentTimeMillis(),
                        duration == -1 ? -1 : System.currentTimeMillis() + duration));
    }

    private record Ban(String id, String reason, long start, long end) {}

    private record Mute(String id, String reason, long start, long end) {}
}
