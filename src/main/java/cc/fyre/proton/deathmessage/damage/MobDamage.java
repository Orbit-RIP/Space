package cc.fyre.proton.deathmessage.damage;

import lombok.Getter;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public abstract class MobDamage extends Damage {

    @Getter private final EntityType mobType;

    public MobDamage(UUID damaged,double damage,EntityType mobType) {
        super(damaged, damage);
        this.mobType = mobType;
    }

}
