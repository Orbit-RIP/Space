package cc.fyre.proton.deathmessage.trackers;

import cc.fyre.proton.Proton;
import cc.fyre.proton.deathmessage.damage.Damage;
import cc.fyre.proton.deathmessage.damage.PlayerDamage;
import cc.fyre.proton.deathmessage.event.CustomPlayerDamageEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class VoidTracker implements Listener {

    @EventHandler(priority=EventPriority.LOW)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {

        if (event.getCause().getCause() != EntityDamageEvent.DamageCause.VOID) {
            return;
        }

        final List<Damage> record = Proton.getInstance().getDeathMessageHandler().getDamage(event.getPlayer());

        Damage knocker = null;
        long knockerTime = 0L;

        if (record != null) {

            for (Damage damage : record) {
                if (damage instanceof VoidDamage || damage instanceof VoidDamageByPlayer) {
                    continue;
                }

                if (damage instanceof PlayerDamage && (knocker == null || damage.getTime() > knockerTime)) {
                    knocker = damage;
                    knockerTime = damage.getTime();
                }
            }

        }

        if (knocker != null && knockerTime + TimeUnit.MINUTES.toMillis(1) > System.currentTimeMillis() ) {
            event.setTrackerDamage(new VoidDamageByPlayer(event.getPlayer().getUniqueId(), event.getDamage(),((PlayerDamage) knocker).getDamager()));
        } else {
            event.setTrackerDamage(new VoidDamage(event.getPlayer().getUniqueId(),event.getDamage()));
        }

    }

    public static class VoidDamage extends Damage {

        public VoidDamage(UUID damaged,double damage) {
            super(damaged,damage);
        }

        @Override
        public String getDeathMessage(UUID getFor) {
            return wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " fell into the void.";
        }
    }

    public static class VoidDamageByPlayer extends PlayerDamage {

        public VoidDamageByPlayer(UUID damaged, double damage, UUID damager) {
            super(damaged, damage, damager);
        }

        public String getDeathMessage(UUID getFor) {
            return wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " fell into the void thanks to " + wrapName(this.getDamager(), getFor) + ChatColor.YELLOW + ".";
        }

    }

}