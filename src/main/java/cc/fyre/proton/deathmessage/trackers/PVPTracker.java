package cc.fyre.proton.deathmessage.trackers;


import cc.fyre.proton.Proton;
import cc.fyre.proton.deathmessage.damage.PlayerDamage;
import cc.fyre.proton.deathmessage.event.CustomPlayerDamageEvent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PVPTracker implements Listener {

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {

        if (event.getCause() instanceof EntityDamageByEntityEvent) {

            final EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getCause();

            if (e.getDamager() instanceof Player) {
                Player damager = (Player) e.getDamager();
                Player damaged = event.getPlayer();

                event.setTrackerDamage(new PVPDamage(damaged.getUniqueId(), event.getDamage(), (Player)damager));
            }
        }
    }

    public static class PVPDamage extends PlayerDamage {

        private final String itemString;

        public PVPDamage(UUID damaged, double damage, Player damager) {
            super(damaged, damage, damager.getUniqueId());

            final ItemStack hand = damager.getItemInHand();

            if (hand.getType() == Material.AIR) {
                this.itemString = "their fists";
            } else if (hand.getItemMeta().hasDisplayName()) {
                this.itemString = ChatColor.stripColor(hand.getItemMeta().getDisplayName());
            } else {
                this.itemString = WordUtils.capitalizeFully(hand.getType().name().replace('_', ' '));
            }

        }

        public String getDeathMessage(UUID getFor) {
            return wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " was slain by " + wrapName(this.getDamager(), getFor) + ChatColor.YELLOW + (!Proton.getInstance().getDeathMessageHandler().getConfiguration().hideWeapons() ? " using " + ChatColor.RED + this.itemString.trim() : "") + ChatColor.YELLOW + ".";
        }

    }

}