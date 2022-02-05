package cc.fyre.proton.autoreboot.task;

import cc.fyre.proton.Proton;
import cc.fyre.proton.util.TimeUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerRebootTask extends BukkitRunnable {

    @Getter private int secondsRemaining;
    @Getter private boolean wasWhitelisted;

    public ServerRebootTask(long time) {
        this.secondsRemaining = (int)(time / 1000);
        this.wasWhitelisted = Proton.getInstance().getServer().hasWhitelist();
    }

    public void run() {

        if (this.secondsRemaining == 300) {
            Proton.getInstance().getServer().setWhitelist(true);
        } else if (this.secondsRemaining == 0) {
            Proton.getInstance().getServer().setWhitelist(this.wasWhitelisted);

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.chat("/hub");
            }

            Proton.getInstance().getServer().shutdown();
        }

        switch(this.secondsRemaining) {
            case 5:
            case 10:
            case 15:
            case 30:
            case 60:
            case 120:
            case 180:
            case 240:
            case 300:
            case 600:
                Proton.getInstance().getServer().broadcastMessage(ChatColor.RED + "⚠ " + ChatColor.DARK_RED.toString() + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RED + " ⚠");
                Proton.getInstance().getServer().broadcastMessage(ChatColor.RED + "Server rebooting in " + TimeUtils.formatIntoDetailedString(this.secondsRemaining) + ".");
                Proton.getInstance().getServer().broadcastMessage(ChatColor.RED + "⚠ " + ChatColor.DARK_RED.toString() + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RED + " ⚠");
            default:
                --this.secondsRemaining;
        }
    }

    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        Proton.getInstance().getServer().setWhitelist(this.wasWhitelisted);
    }
}
