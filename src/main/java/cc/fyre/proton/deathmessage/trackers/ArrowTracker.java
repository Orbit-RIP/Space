package cc.fyre.proton.deathmessage.trackers;

import cc.fyre.proton.Proton;
import cc.fyre.proton.deathmessage.damage.Damage;
import cc.fyre.proton.deathmessage.damage.MobDamage;
import cc.fyre.proton.deathmessage.damage.PlayerDamage;
import cc.fyre.proton.deathmessage.event.CustomPlayerDamageEvent;
import lombok.Getter;

import cc.fyre.proton.util.EntityUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.UUID;

public class ArrowTracker implements Listener {

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            event.getProjectile().setMetadata("ShotFromDistance", new FixedMetadataValue(Proton.getInstance(), event.getProjectile().getLocation()));
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event.getCause();

            if (entityDamageByEntityEvent.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) entityDamageByEntityEvent.getDamager();

                if (arrow.getShooter() instanceof Player) {
                    /*LOGIC TEST
                    Location l = arrow.getLocation();
                    Location l2 = event.getPlayer().getLocation();
                    if( l.getBlockX() != l2.getBlockX() || l.getBlockZ() != l2.getBlockZ() ) {
                        entityDamageByEntityEvent.setCancelled(true);
                        System.out.println("Cancelled arrow hit! Hit through a wall!");
                        return;
                    }
                    LOGIC TEST - FAILED WILL TODO: INVESTIGATE FURTHER */
                    Player shooter = (Player) arrow.getShooter();

                    for (MetadataValue value : arrow.getMetadata("ShotFromDistance")) {
                        Location shotFrom = (Location) value.value();
                        double distance = shotFrom.distance(event.getPlayer().getLocation());
                        event.setTrackerDamage(new ArrowDamageByPlayer(event.getPlayer().getUniqueId(), event.getDamage(), shooter.getUniqueId(), distance));
                    }
                } else if (arrow.getShooter() instanceof Entity) {
                    event.setTrackerDamage(new ArrowDamageByMob(event.getPlayer().getUniqueId(), event.getDamage(), (Entity) arrow.getShooter()));
                } else {
                    event.setTrackerDamage(new ArrowDamage(event.getPlayer().getUniqueId(), event.getDamage()));
                }
            }
        }
    }

    public static class ArrowDamage extends Damage {

        public ArrowDamage(UUID damaged,double damage) {
            super(damaged, damage);
        }

        public String getDeathMessage(UUID getFor) {
            return wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " was shot.";
        }
    }

    public static class ArrowDamageByPlayer extends PlayerDamage {

        @Getter private double distance;

        public ArrowDamageByPlayer(UUID damaged, double damage, UUID damager, double distance) {
            super(damaged, damage, damager);
            this.distance = distance;
        }

        public String getDeathMessage(UUID getFor) {
            return wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " was shot by " + wrapName(this.getDamager(), getFor) + ChatColor.YELLOW + " from " + ChatColor.BLUE + (int)this.distance + " blocks" + ChatColor.YELLOW + ".";
        }
    }

    public static class ArrowDamageByMob extends MobDamage {

        public ArrowDamageByMob(UUID damaged, double damage, Entity damager) {
            super(damaged, damage, damager.getType());
        }

        public String getDeathMessage(UUID getFor) {
            return wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " was shot by a " + ChatColor.RED + EntityUtils.getName(this.getMobType()) + ChatColor.YELLOW + ".";
        }
    }

}