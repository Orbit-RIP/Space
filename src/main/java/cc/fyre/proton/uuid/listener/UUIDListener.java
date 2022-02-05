package cc.fyre.proton.uuid.listener;

import cc.fyre.proton.Proton;
import cc.fyre.proton.uuid.UUIDCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public final class UUIDListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {

        if (Proton.getInstance().getUuidCache().cached(event.getUniqueId())) {
            Proton.getInstance().getUuidCache().update(event.getUniqueId(),event.getName());
        } else {
            Proton.getInstance().getUuidCache().updateAll(event.getUniqueId(),event.getName());
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (!event.getPlayer().isOp() || UUIDCache.MONITOR_CACHE.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }

        UUIDCache.MONITOR_CACHE.put(event.getPlayer().getUniqueId(),true);
    }

}