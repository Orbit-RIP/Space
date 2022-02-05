package cc.fyre.proton.deathmessage.damage;


import org.bukkit.ChatColor;

import java.util.UUID;

public final class UnknownDamage extends Damage {

    public UnknownDamage(UUID damaged,double damage) {
        super(damaged, damage);
    }

    public String getDeathMessage(UUID getFor) {
        return wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " died.";
    }

}
