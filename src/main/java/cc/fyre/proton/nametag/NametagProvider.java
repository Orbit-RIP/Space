package cc.fyre.proton.nametag;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.beans.ConstructorProperties;

public abstract class NametagProvider {
    private final String name;
    private final int weight;

    public abstract NametagInfo fetchNametag(Player var1, Player var2);

    public static NametagInfo createNametag(String prefix, String suffix) {
        return FrozenNametagHandler.getOrCreate(prefix, suffix);
    }

    @ConstructorProperties(value={"name", "weight"})
    public NametagProvider(String name, int weight) {
        this.name=name;
        this.weight=weight;
    }

    public String getName() {
        return this.name;
    }

    public int getWeight() {
        return this.weight;
    }

    protected static final class DefaultNametagProvider
            extends NametagProvider {
        public DefaultNametagProvider() {
            super("Default Provider", 0);
        }

        @Override
        public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
            return DefaultNametagProvider.createNametag("", "");
        }
    }
}

