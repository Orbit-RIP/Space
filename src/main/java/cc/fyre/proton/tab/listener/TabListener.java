package cc.fyre.proton.tab.listener;

import cc.fyre.proton.Proton;
import cc.fyre.proton.tab.construct.TabLayout;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class TabListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                Proton.getInstance().getTabHandler().addPlayer(event.getPlayer());
            }
        }.runTaskLater(Proton.getInstance(),10L);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Proton.getInstance().getTabHandler().removePlayer(event.getPlayer());
        TabLayout.remove(event.getPlayer());
    }

}
