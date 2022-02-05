package cc.fyre.proton.deathmessage.damage;

import cc.fyre.proton.Proton;
import lombok.Getter;
import cc.fyre.proton.deathmessage.DeathMessageConfiguration;

import java.util.UUID;


public abstract class Damage {

    @Getter private final UUID damaged;
    @Getter private final double damage;
    @Getter private final long time;

    public Damage(UUID damaged, double damage) {
        this.damaged = damaged;
        this.damage = damage;
        this.time = System.currentTimeMillis();
    }

    public static String wrapName(UUID player, UUID wrapFor) {
        final DeathMessageConfiguration configuration = Proton.getInstance().getDeathMessageHandler().getConfiguration();
        return configuration.formatPlayerName(player, wrapFor);
    }

    public static String wrapName(UUID player) {
        final DeathMessageConfiguration configuration = Proton.getInstance().getDeathMessageHandler().getConfiguration();
        return configuration.formatPlayerName(player);
    }

    public abstract String getDeathMessage(UUID uuid);

    public long getTimeAgoMillis() {
        return System.currentTimeMillis() - this.time;
    }


}
