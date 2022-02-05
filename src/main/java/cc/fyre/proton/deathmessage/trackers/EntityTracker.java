package cc.fyre.proton.deathmessage.trackers;

import cc.fyre.proton.deathmessage.damage.MobDamage;
import cc.fyre.proton.deathmessage.event.CustomPlayerDamageEvent;
import cc.fyre.proton.util.EntityUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

public class EntityTracker implements Listener {

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause() instanceof EntityDamageByEntityEvent) {

            final EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getCause();

            if (!(e.getDamager() instanceof Player) && !(e.getDamager() instanceof Arrow)) {
                event.setTrackerDamage(new EntityDamage(event.getPlayer().getUniqueId(), event.getDamage(), e.getDamager()));
            }
        }
    }

    public static class EntityDamage extends MobDamage {

        public EntityDamage(UUID damaged, double damage, Entity entity) {
            super(damaged, damage, entity.getType());
        }

        @Override
        public String getDeathMessage(UUID getFor) {
            return wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " was slain by a " + ChatColor.RED + EntityUtils.getName(this.getMobType()) + ChatColor.YELLOW + ".";
        }

    }

}