package cc.fyre.proton.nametag;

import cc.fyre.proton.Proton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

final class NametagListener
        implements Listener {
    NametagListener() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (FrozenNametagHandler.isInitiated()) {
            event.getPlayer().setMetadata("qLibNametag-LoggedIn", new FixedMetadataValue(Proton.getInstance(), true));
            FrozenNametagHandler.initiatePlayer(event.getPlayer());
            FrozenNametagHandler.reloadPlayer(event.getPlayer());
            FrozenNametagHandler.reloadOthersFor(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().removeMetadata("qLibNametag-LoggedIn", Proton.getInstance());
        FrozenNametagHandler.getTeamMap().remove(event.getPlayer().getName());
    }
}

