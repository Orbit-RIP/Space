package cc.fyre.proton.nametag;

import cc.fyre.proton.nametag.NametagUpdate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class NametagThread extends Thread {
    private static final Map<NametagUpdate, Boolean> pendingUpdates=new ConcurrentHashMap<>();

    public NametagThread() {
        super("qLib - Nametag Thread");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            for (NametagUpdate pendingUpdate : NametagThread.pendingUpdates.keySet()) {
                try {
                    cc.fyre.proton.nametag.FrozenNametagHandler.applyUpdate(pendingUpdate);
                }
                catch (Exception ignored) {
                }
            }
            try {
                Thread.sleep(cc.fyre.proton.nametag.FrozenNametagHandler.getUpdateInterval() * 50L);
            }
            catch (InterruptedException ignored) {

            }
        }
    }

    public static Map<NametagUpdate, Boolean> getPendingUpdates() {
        return pendingUpdates;
    }
}

