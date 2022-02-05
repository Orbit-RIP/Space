package cc.fyre.proton.deathmessage.listener;

import cc.fyre.proton.Proton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class DisconnectListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Proton.getInstance().getDeathMessageHandler().clearDamage(event.getPlayer());
    }

}
