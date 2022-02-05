package cc.fyre.proton.combatlogger;

import cc.fyre.proton.Proton;
import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.ChatColor;

import java.util.UUID;

public interface CombatLoggerConfiguration {

    CombatLoggerConfiguration DEFAULT_CONFIGURATION = user -> Proton.getInstance().getDeathMessageHandler().getConfiguration() != null ? Proton.getInstance().getDeathMessageHandler().getConfiguration().formatPlayerName(user) + ChatColor.GRAY + " (Combat-Logger)" : ChatColor.RED + UUIDUtils.name(user) + ChatColor.GRAY + " (Combat-Logger)";

    String formatPlayerName(UUID uuid);

    default String formatPlayerName(UUID user,UUID formatFor) {
        return this.formatPlayerName(user);
    }

}
