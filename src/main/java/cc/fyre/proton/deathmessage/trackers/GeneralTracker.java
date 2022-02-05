package cc.fyre.proton.deathmessage.trackers;

import cc.fyre.proton.deathmessage.damage.Damage;
import cc.fyre.proton.deathmessage.event.CustomPlayerDamageEvent;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class GeneralTracker implements Listener {

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        switch (event.getCause().getCause()) {
            case SUFFOCATION:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getUniqueId(), event.getDamage(), "suffocated"));
                break;
            case DROWNING:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getUniqueId(), event.getDamage(), "drowned"));
                break;
            case STARVATION:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getUniqueId(), event.getDamage(), "starved to death"));
                break;
            case LIGHTNING:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getUniqueId(), event.getDamage(), "was struck by lightning"));
                break;
            case POISON:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getUniqueId(), event.getDamage(), "was poisoned"));
                break;
            case WITHER:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getUniqueId(), event.getDamage(), "withered away"));
                break;
            default:
                break;
        }
    }

    public static class GeneralDamage extends Damage {

        private String message;

        public GeneralDamage(UUID damaged,double damage,String message) {
            super(damaged, damage);
            this.message = message;
        }

        public String getDeathMessage(UUID getFor) {
            return wrapName(this.getDamaged(), getFor) + " " + ChatColor.YELLOW + this.message + ".";
        }

    }

}