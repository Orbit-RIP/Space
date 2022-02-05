package cc.fyre.proton.deathmessage.damage;

import lombok.Getter;

import java.util.UUID;

public abstract class PlayerDamage extends Damage {

    @Getter private final UUID damager;

    public PlayerDamage(UUID damaged, double damage, UUID damager) {
        super(damaged, damage);
        this.damager = damager;
    }

}
