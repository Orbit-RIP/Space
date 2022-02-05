package cc.fyre.proton.command.defaults;

import cc.fyre.proton.command.Command;

import cc.fyre.proton.uuid.UUIDCache;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class UUIDMonitorCommand {

    @Command(
            names = {"uuidmonitor","uuidcache"},
            permission = "proton.command.uuidmonitor",
            hidden = true
    )
    public static void execute(Player player) {

        if (!UUIDCache.MONITOR_CACHE.containsKey(player.getUniqueId())) {
            UUIDCache.MONITOR_CACHE.put(player.getUniqueId(),true);
        } else {
            UUIDCache.MONITOR_CACHE.put(player.getUniqueId(),!UUIDCache.MONITOR_CACHE.get(player.getUniqueId()));
        }

        player.sendMessage(ChatColor.GOLD + "UUID Monitor: " + (UUIDCache.MONITOR_CACHE.get(player.getUniqueId()) ? ChatColor.GREEN + "Enabled":ChatColor.RED + "Disabled"));
    }

}
