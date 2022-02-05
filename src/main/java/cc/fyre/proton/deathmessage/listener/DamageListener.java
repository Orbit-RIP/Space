package cc.fyre.proton.deathmessage.listener;

import cc.fyre.proton.Proton;
import cc.fyre.proton.deathmessage.damage.UnknownDamage;
import cc.fyre.proton.deathmessage.event.CustomPlayerDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void onEntityDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player)event.getEntity();
        final CustomPlayerDamageEvent customEvent = new CustomPlayerDamageEvent(event);

        customEvent.setTrackerDamage(new UnknownDamage(player.getUniqueId(), customEvent.getDamage()));

        Proton.getInstance().getServer().getPluginManager().callEvent(customEvent);

        Proton.getInstance().getDeathMessageHandler().addDamage(player, customEvent.getTrackerDamage());

    }

}
