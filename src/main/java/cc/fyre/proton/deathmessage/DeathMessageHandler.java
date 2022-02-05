package cc.fyre.proton.deathmessage;

import cc.fyre.proton.Proton;
import cc.fyre.proton.deathmessage.damage.Damage;
import cc.fyre.proton.deathmessage.listener.DamageListener;
import cc.fyre.proton.deathmessage.listener.DeathListener;
import cc.fyre.proton.deathmessage.listener.DisconnectListener;
import cc.fyre.proton.deathmessage.trackers.*;
import lombok.Getter;
import lombok.Setter;

import net.minecraft.util.com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.*;

public class DeathMessageHandler {

    @Getter @Setter private DeathMessageConfiguration configuration;
    @Getter private Map<UUID,List<Damage>> damage;

    public DeathMessageHandler() {

        this.configuration = DeathMessageConfiguration.DEFAULT_CONFIGURATION;
        this.damage = new HashMap<>();

        final PluginManager pluginManager = Proton.getInstance().getServer().getPluginManager();

        pluginManager.registerEvents(new DamageListener(),Proton.getInstance());
        pluginManager.registerEvents(new DeathListener(),Proton.getInstance());
        pluginManager.registerEvents(new DisconnectListener(),Proton.getInstance());
        pluginManager.registerEvents(new GeneralTracker(),Proton.getInstance());
        pluginManager.registerEvents(new PVPTracker(),Proton.getInstance());
        pluginManager.registerEvents(new EntityTracker(),Proton.getInstance());
        pluginManager.registerEvents(new FallTracker(),Proton.getInstance());
        pluginManager.registerEvents(new ArrowTracker(),Proton.getInstance());
        pluginManager.registerEvents(new VoidTracker(),Proton.getInstance());
        pluginManager.registerEvents(new BurnTracker(),Proton.getInstance());
    }

    public List<Damage> getDamage(Player player) {
        return (List)(damage.containsKey(player.getUniqueId()) ? damage.get(player.getUniqueId()) : ImmutableList.of());
    }

    public void addDamage(Player player, Damage addedDamage) {

        damage.putIfAbsent(player.getUniqueId(), new ArrayList<>());

        final List damageList = damage.get(player.getUniqueId());

        while(damageList.size() > 30) {
            damageList.remove(0);
        }

        damageList.add(addedDamage);
    }

    public void clearDamage(Player player) {
        damage.remove(player.getUniqueId());
    }

}
