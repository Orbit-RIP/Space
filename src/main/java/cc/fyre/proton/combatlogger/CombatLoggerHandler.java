package cc.fyre.proton.combatlogger;

import cc.fyre.proton.Proton;
import cc.fyre.proton.combatlogger.listener.CombatLoggerListener;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatLoggerHandler {

    @Getter private final Map<UUID,CombatLogger> combatLoggerMap = new HashMap<>();

    @Getter @Setter private CombatLoggerConfiguration configuration;

    public CombatLoggerHandler() {
        this.configuration = CombatLoggerConfiguration.DEFAULT_CONFIGURATION;
        Proton.getInstance().getServer().getPluginManager().registerEvents(new CombatLoggerListener(),Proton.getInstance().getInstance());
    }

}
