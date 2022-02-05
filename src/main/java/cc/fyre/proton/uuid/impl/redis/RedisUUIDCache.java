package cc.fyre.proton.uuid.impl.redis;

import cc.fyre.proton.Proton;
import cc.fyre.proton.redis.IRedisCommand;
import cc.fyre.proton.util.UUIDUtils;
import cc.fyre.proton.uuid.impl.IUUIDCache;
import lombok.Getter;
import cc.fyre.proton.pidgin.packet.handler.IncomingPacketHandler;
import cc.fyre.proton.pidgin.packet.listener.PacketListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class RedisUUIDCache implements IUUIDCache, PacketListener {

    @Getter private static Map<UUID,String> uuidToName = new ConcurrentHashMap<>();
    @Getter private static Map<String,UUID> nameToUuid = new ConcurrentHashMap<>();
    private final IRedisCommand iRedisCommand = Proton.getInstance().getIRedisCommand();

    public RedisUUIDCache() {

        Proton.getInstance().getPidginHandler().registerPacket(RedisUUIDUpdatePacket.class);
        Proton.getInstance().getPidginHandler().registerListener(this);

        iRedisCommand.runBackboneRedisCommand(redis -> {

            final Map<String, String> cache = redis.hgetAll("UUIDCache");

            for (Map.Entry<String, String> cacheEntry : cache.entrySet()) {

                final UUID uuid = UUID.fromString(cacheEntry.getKey());
                final String name = cacheEntry.getValue();

                uuidToName.put(uuid, name);
                nameToUuid.put(name.toLowerCase(), uuid);
            }

            return null;
        });
    }

    public UUID uuid(String name) {

        if (nameToUuid.containsKey(name.toLowerCase())) {
            return nameToUuid.get(name.toLowerCase());
        }

        return null;
    }

    public String name(UUID uuid) {
        return uuidToName.get(uuid);
    }

    @Override
    public boolean cached(UUID uuid) {
        return uuidToName.containsKey(uuid);
    }

    @Override
    public boolean cached(String name) {
        return nameToUuid.containsKey(name.toLowerCase());
    }

    public void ensure(UUID uuid) {
        if (String.valueOf(name(uuid)).equals("null")) {
            Proton.getInstance().getLogger().warning(uuid + " didn't have a cached name.");
        }
    }

    public void update(UUID uuid,String name) {
        uuidToName.put(uuid,name);

        for (Map.Entry<String, UUID> entry : (new HashMap<>(nameToUuid)).entrySet()) {

            if (entry.getValue().equals(uuid)) {
                nameToUuid.remove(entry.getKey());
            }

        }

        nameToUuid.put(name.toLowerCase(),uuid);
    }

    public void updateAll(UUID uuid,String name) {
        this.update(uuid,name);

        Proton.getInstance().getUuidCache().monitor("UUID & name has been updated in the local cache. (" + UUIDUtils.formatPretty(uuid) + ")");

        Proton.getInstance().getServer().getScheduler().runTaskAsynchronously(Proton.getInstance(),() -> iRedisCommand.runBackboneRedisCommand(redis -> {
            Proton.getInstance().getUuidCache().monitor("UUID & name has been updated in the redis cache. (" + UUIDUtils.formatPretty(uuid) + ")");
            redis.hset("UUIDCache",uuid.toString(),name);
            return null;
        }));

        Proton.getInstance().getPidginHandler().sendPacket(new RedisUUIDUpdatePacket(uuid,name));
    }

    @IncomingPacketHandler
    public void onRedisUUIDUpdate(RedisUUIDUpdatePacket packet) {
        this.update(packet.uuid(),packet.name());
        Proton.getInstance().getUuidCache().monitor("UUID & name has been updated across network. (" +UUIDUtils.formatPretty(packet.uuid()) + ")");
    }

}