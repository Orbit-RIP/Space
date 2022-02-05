package cc.fyre.proton.combatlogger.listener;

import cc.fyre.proton.Proton;
import cc.fyre.proton.combatlogger.CombatLogger;
import cc.fyre.proton.combatlogger.CombatLoggerConfiguration;
import cc.fyre.proton.deathmessage.DeathMessageConfiguration;
import cc.fyre.proton.deathmessage.damage.Damage;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class CombatLoggerListener implements Listener {
    public CombatLoggerListener() {
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onEntityDeath(EntityDeathEvent event) {

        if (!event.getEntity().hasMetadata(CombatLogger.COMBAT_LOGGER_METADATA)) {
            return;
        }

        final CombatLogger logger = Proton.getInstance().getCombatLoggerHandler().getCombatLoggerMap().get(event.getEntity().getUniqueId());

        if (logger == null) {
            return;
        }

        for (int i = 0; i < logger.getArmor().length; i++) {

            final ItemStack item = logger.getArmor()[i];

            event.getDrops().add(item);
        }

        for (int i = 0; i < logger.getInventory().length; i++) {

            final ItemStack item = logger.getInventory()[i];

            event.getDrops().add(item);
        }

        logger.getEventAdapter().onEntityDeath(logger, event);

        final CombatLoggerConfiguration configuration = Proton.getInstance().getCombatLoggerHandler().getConfiguration();
        final DeathMessageConfiguration dmConfig = Proton.getInstance().getDeathMessageHandler().getConfiguration();

        final Player killer = event.getEntity().getKiller();

        if (configuration != null && dmConfig != null) {

            new BukkitRunnable() {

                @Override
                public void run() {

                    for (Player loopPlayer : Proton.getInstance().getServer().getOnlinePlayers()) {

                        final String deathMessage = getCombatLoggerDeathMessage(logger.getPlayerUuid(),killer,loopPlayer.getUniqueId());
                        final boolean showDeathMessage = dmConfig.shouldShowDeathMessage(loopPlayer.getUniqueId(),logger.getPlayerUuid(),killer == null ? null : killer.getUniqueId());

                        if (showDeathMessage) {
                            loopPlayer.sendMessage(deathMessage);
                        }
                    }

                }
            }.runTaskAsynchronously(Proton.getInstance());

        }

        Player target = Proton.getInstance().getServer().getPlayer(logger.getPlayerUuid());

        if (target == null) {

            final MinecraftServer server = ((CraftServer)Proton.getInstance().getServer()).getServer();
            final EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(logger.getPlayerUuid(), logger.getPlayerName()), new PlayerInteractManager(server.getWorldServer(0)));

            target = entity.getBukkitEntity();

            if (target != null) {
                target.loadData();
            }
        }

        if (target != null) {
            target.getInventory().clear();
            target.getInventory().setArmorContents(null);
            target.saveData();
        }

        Proton.getInstance().getCombatLoggerHandler().getCombatLoggerMap().remove(event.getEntity().getUniqueId());
        Proton.getInstance().getCombatLoggerHandler().getCombatLoggerMap().remove(logger.getPlayerUuid());
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().hasMetadata(CombatLogger.COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onChunkUnload(ChunkUnloadEvent event) {

        for (Entity entity : event.getChunk().getEntities()) {

            if (entity.hasMetadata(CombatLogger.COMBAT_LOGGER_METADATA) && !entity.isDead()) {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onEntityPortal(EntityPortalEvent event) {
        if (event.getEntity().hasMetadata(CombatLogger.COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onPlayerJoin(PlayerJoinEvent event) {

        final CombatLogger logger = Proton.getInstance().getCombatLoggerHandler().getCombatLoggerMap().get(event.getPlayer().getUniqueId());

        if (logger != null && logger.getSpawnedEntity() != null && logger.getSpawnedEntity().isValid() && !logger.getSpawnedEntity().isDead()) {

            final UUID entityId = logger.getSpawnedEntity().getUniqueId();

            logger.getSpawnedEntity().remove();
            Proton.getInstance().getCombatLoggerHandler().getCombatLoggerMap().remove(entityId);
            Proton.getInstance().getCombatLoggerHandler().getCombatLoggerMap().remove(event.getPlayer().getUniqueId());
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity().hasMetadata(CombatLogger.COMBAT_LOGGER_METADATA)) {

            final CombatLogger logger = Proton.getInstance().getCombatLoggerHandler().getCombatLoggerMap().get(event.getEntity().getUniqueId());

            if (logger != null) {
                logger.getEventAdapter().onEntityDamageByEntity(logger, event);
            }
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onEntityPressurePlate(EntityInteractEvent event) {
        boolean pressurePlate = event.getBlock().getType() == Material.STONE_PLATE || event.getBlock().getType() == Material.GOLD_PLATE || event.getBlock().getType() == Material.IRON_PLATE || event.getBlock().getType() == Material.WOOD_PLATE;
        if (pressurePlate && event.getEntity().hasMetadata(CombatLogger.COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true);
        }

    }

    private String getCombatLoggerDeathMessage(UUID player,Player killer,UUID getFor) {
        if (killer == null) {
            return this.wrapLogger(player, getFor) + ChatColor.YELLOW + " died.";
        } else {
            ItemStack hand = killer.getItemInHand();
            String itemString;
            if (hand.getType() == Material.AIR) {
                itemString = "their fists";
            } else if (hand.getItemMeta().hasDisplayName()) {
                itemString = ChatColor.stripColor(hand.getItemMeta().getDisplayName());
            } else {
                itemString = WordUtils.capitalizeFully(hand.getType().name().replace('_', ' '));
            }

            return this.wrapLogger(player, getFor) + ChatColor.YELLOW + " was slain by " + Damage.wrapName(killer.getUniqueId(), getFor) + ChatColor.YELLOW + " using " + ChatColor.RED + itemString.trim() + ChatColor.YELLOW + ".";
        }
    }

    private String wrapLogger(UUID player, UUID wrapFor) {
        return Proton.getInstance().getCombatLoggerHandler().getConfiguration().formatPlayerName(player, wrapFor);
    }
}
