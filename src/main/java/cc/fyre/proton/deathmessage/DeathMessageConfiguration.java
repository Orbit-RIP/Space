package cc.fyre.proton.deathmessage;

import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.ChatColor;

import java.util.UUID;

public interface DeathMessageConfiguration {

    DeathMessageConfiguration DEFAULT_CONFIGURATION = new DeathMessageConfiguration() {

        @Override
        public boolean shouldShowDeathMessage(UUID checkFor, UUID died, UUID killer) {
            return true;
        }

        @Override
        public String formatPlayerName(UUID player) {
            return ChatColor.RED + UUIDUtils.name(player);
        }

    };

    boolean shouldShowDeathMessage(UUID checkFor,UUID died,UUID killer);

    String formatPlayerName(UUID uuid);

    default String formatPlayerName(UUID player,UUID formatFor) {
        return this.formatPlayerName(player);
    }

    default boolean hideWeapons() {
        return false;
    }

}
