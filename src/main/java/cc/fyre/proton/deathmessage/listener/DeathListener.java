package cc.fyre.proton.deathmessage.listener;

import cc.fyre.proton.Proton;
import cc.fyre.proton.deathmessage.damage.Damage;
import cc.fyre.proton.deathmessage.damage.UnknownDamage;
import cc.fyre.proton.deathmessage.DeathMessageConfiguration;
import cc.fyre.proton.deathmessage.damage.PlayerDamage;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DeathListener implements Listener {

    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void onPlayerDeathEarly(PlayerDeathEvent event) {

        final List<Damage> record = Proton.getInstance().getDeathMessageHandler().getDamage(event.getEntity());

        if (record.isEmpty()) {
            return;
        }

        final Damage deathCause = record.get(record.size() - 1);

        if (deathCause instanceof PlayerDamage && deathCause.getTimeAgoMillis() < TimeUnit.MINUTES.toMillis(1L)) {

            final UUID killerUuid = ((PlayerDamage)deathCause).getDamager();
            final Player killerPlayer = Proton.getInstance().getServer().getPlayer(killerUuid);

            if (killerPlayer != null) {
                ((CraftPlayer)event.getEntity()).getHandle().killer = ((CraftPlayer)killerPlayer).getHandle();
            }
        }

    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onPlayerDeathLate(PlayerDeathEvent event) {

        final List<Damage> record = Proton.getInstance().getDeathMessageHandler().getDamage(event.getEntity());

        Damage deathCause;

        if (record != null && !record.isEmpty()) {
            deathCause = record.get(record.size() - 1);
        } else {
            deathCause = new UnknownDamage(event.getEntity().getUniqueId(), 1.0D);
        }

        event.setDeathMessage(null);

        final DeathMessageConfiguration configuration = Proton.getInstance().getDeathMessageHandler().getConfiguration();
        final UUID diedUuid = event.getEntity().getUniqueId();
        final UUID killerUuid = event.getEntity().getKiller() == null ? null : event.getEntity().getKiller().getUniqueId();

        for (Player loopPlayer : Proton.getInstance().getServer().getOnlinePlayers()) {

            final boolean showDeathMessage = configuration.shouldShowDeathMessage(loopPlayer.getUniqueId(), diedUuid, killerUuid);

            if (showDeathMessage) {
                final String deathMessage = deathCause.getDeathMessage(event.getEntity().getUniqueId());
                loopPlayer.sendMessage(deathMessage);
            }

        }

        Proton.getInstance().getDeathMessageHandler().clearDamage(event.getEntity());
    }

}
