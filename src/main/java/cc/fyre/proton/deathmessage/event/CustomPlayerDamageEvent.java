package cc.fyre.proton.deathmessage.event;

import lombok.Getter;
import lombok.Setter;
import cc.fyre.proton.deathmessage.damage.Damage;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerEvent;

public class CustomPlayerDamageEvent extends PlayerEvent {

    @Getter private static final HandlerList handlerList = new HandlerList();
    @Getter private final EntityDamageEvent cause;
    @Getter @Setter private Damage trackerDamage;

    public CustomPlayerDamageEvent(EntityDamageEvent cause) {
        super((Player)cause.getEntity());
        this.cause = cause;
    }

    public double getDamage() {
        return this.cause.getDamage(EntityDamageEvent.DamageModifier.BASE);
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

}

