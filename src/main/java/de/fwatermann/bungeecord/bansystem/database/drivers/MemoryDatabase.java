package de.fwatermann.bungeecord.bansystem.database.drivers;

import de.fwatermann.bungeecord.bansystem.database.BanStatus;
import de.fwatermann.bungeecord.bansystem.database.Database;
import de.fwatermann.bungeecord.bansystem.database.MuteStatus;

import java.util.HashMap;
import java.util.UUID;

public class MemoryDatabase extends Database {

    private HashMap<String, Ban> accountBans = new HashMap<>();
    private HashMap<String, Ban> ipBans = new HashMap<>();
    private HashMap<String, Mute> mutes = new HashMap<>();

    @Override
    public BanStatus getBanStatus(UUID uuid) {
        Ban ban = this.accountBans.get(uuid.toString());
        if (ban == null) {
            return BanStatus.notBanned();
        }
        return new BanStatus(
                ban.end == -1 || ban.end >= System.currentTimeMillis(),
                ban.reason,
                ban.start,
                ban.end,
                ban.id);
    }

    @Override
    public BanStatus getIPBanStatus(String ip) {
        Ban ban = this.ipBans.get(ip);
        if (ban == null) {
            return BanStatus.notBanned();
        }
        return new BanStatus(
                ban.end == -1 || ban.end >= System.currentTimeMillis(),
                ban.reason,
                ban.start,
                ban.end,
                ban.id);
    }

    @Override
    public MuteStatus getMuteStatus(UUID uuid) {
        Mute mute = this.mutes.get(uuid.toString());
        if (mute == null) {
            return MuteStatus.notMuted();
        }
        return new MuteStatus(
                mute.end == -1 || mute.end >= System.currentTimeMillis(),
                mute.reason,
                mute.start,
                mute.end,
                mute.id);
    }

    @Override
    public String addBan(UUID uuid, String reason, long duration) {
        Ban ban =
                new Ban(
                        UUID.randomUUID().toString(),
                        reason,
                        System.currentTimeMillis(),
                        duration == -1 ? -1 : System.currentTimeMillis() + duration);
        this.accountBans.put(uuid.toString(), ban);
        return ban.id;
    }

    @Override
    public String addIPBan(String ip, String reason, long duration) {
        Ban ban =
                new Ban(
                        UUID.randomUUID().toString(),
                        reason,
                        System.currentTimeMillis(),
                        duration == -1 ? -1 : System.currentTimeMillis() + duration);
        this.ipBans.put(ip, ban);
        return ban.id;
    }

    @Override
    public String addMute(UUID uuid, String reason, long duration) {
        Mute mute =
                new Mute(
                        UUID.randomUUID().toString(),
                        reason,
                        System.currentTimeMillis(),
                        duration == -1 ? -1 : System.currentTimeMillis() + duration);
        this.mutes.put(uuid.toString(), mute);
        return mute.id;
    }

    private record Ban(String id, String reason, long start, long end) {}

    private record Mute(String id, String reason, long start, long end) {}
}
