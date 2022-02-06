package cc.fyre.proton.scoreboard.construct;

import com.google.common.base.Preconditions;
import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class TitleGetter {

    private String defaultTitle;

    public TitleGetter(String defaultTitle) {
        this.defaultTitle = ChatColor.translateAlternateColorCodes('&', defaultTitle);
    }

    public String getTitle(Player player) {
        return defaultTitle;
    }


    public TitleGetter forStaticString(final String staticString) {
        Preconditions.checkNotNull((Object)staticString);
        return new TitleGetter(){

            @Override
            public String getTitle(Player player) {
                return staticString;
            }
        };
    }

}