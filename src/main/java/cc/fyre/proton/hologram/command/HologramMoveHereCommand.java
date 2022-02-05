package cc.fyre.proton.hologram.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.hologram.construct.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class HologramMoveHereCommand {

    @Command(
            names = {"hologram movehere","holo movehere","hologram move","holo move"},
            permission = "proton.command.hologram.movehere"
    )
    public static void execute(Player player,@Parameter(name = "hologram")Hologram hologram) {
        hologram.move(player.getLocation());

        player.sendMessage(ChatColor.GOLD + "Moved hologram with id " + ChatColor.WHITE + hologram.id() + ChatColor.GOLD + ".");
    }

}
