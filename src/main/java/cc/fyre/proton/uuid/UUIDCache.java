package cc.fyre.proton.uuid;

import cc.fyre.proton.Proton;
import cc.fyre.proton.uuid.impl.IUUIDCache;
import cc.fyre.proton.uuid.impl.redis.RedisUUIDCache;
import cc.fyre.proton.uuid.listener.UUIDListener;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public final class UUIDCache implements IUUIDCache {

    public static final UUID CONSOLE_UUID = UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670");
    public static final String UPDATE_PREFIX = ChatColor.BLUE + "[UUIDCache]";

    public static final  Map<UUID,Boolean> MONITOR_CACHE = new HashMap<>();

    @Getter private IUUIDCache impl;

    public UUIDCache() {

        try {
            this.impl = new RedisUUIDCache();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Proton.getInstance().getServer().getPluginManager().registerEvents(new UUIDListener(), Proton.getInstance());

        this.update(CONSOLE_UUID,"CONSOLE");
    }

    public UUID uuid(String name) {
        return this.impl.uuid(name);
    }

    public String name(UUID uuid) {
        return this.impl.name(uuid);
    }

    public boolean cached(UUID uuid) {
        return this.impl.cached(uuid);
    }

    public boolean cached(String name) {
        return this.impl.cached(name);
    }

    public void ensure(UUID uuid) {
        this.impl.ensure(uuid);
    }

    public void update(UUID uuid, String name) {
        this.impl.update(uuid, name);
    }

    public void updateAll(UUID uuid,String name) {
        this.impl.updateAll(uuid,name);
    }

    public void monitor(String message) {
        MONITOR_CACHE.keySet().forEach(uuid -> {

            final Player player = Proton.getInstance().getServer().getPlayer(uuid);

            if (player != null && player.isOnline() && MONITOR_CACHE.get(uuid)) {
                player.sendMessage(UPDATE_PREFIX + " " + ChatColor.GRAY + message);

            }

        });
    }
}