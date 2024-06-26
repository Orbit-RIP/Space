package cc.fyre.proton.menu;

import cc.fyre.proton.Proton;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class Menu {

    static {
        Proton.getInstance().getServer().getPluginManager().registerEvents(new ButtonListener(), Proton.getInstance());
        currentlyOpenedMenus = new HashMap<>();
        checkTasks = new HashMap<>();
    }

    private static Method openInventoryMethod;
    private String staticTitle = null;
    @Getter private ConcurrentHashMap<Integer, Button> buttons = new ConcurrentHashMap<>();

    @Getter @Setter private boolean autoUpdate = false;
    @Getter @Setter private boolean updateAfterClick = true;
    @Getter @Setter private boolean placeholder = false;
    @Getter @Setter private boolean noncancellingInventory = false;

    @Getter private static Map<UUID,Menu> currentlyOpenedMenus;
    @Getter private static Map<UUID,BukkitRunnable> checkTasks;

    private Inventory createInventory(Player player) {

        final Inventory inventory = Proton.getInstance().getServer().createInventory(player, size(player), getTitle(player));

        for (Map.Entry<Integer, Button> buttonEntry : getButtons(player).entrySet()) {

            this.buttons.put(buttonEntry.getKey(), buttonEntry.getValue());

            final ItemStack item = createItemStack(player, buttonEntry.getValue());

            inventory.setItem(buttonEntry.getKey(), item);
        }

        if (this.isPlaceholder()) {

            final Button placeholder = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15);

            for (int index = 0; index < this.size(player); index++) {

                if (this.getButtons(player).get(index) == null) {
                    this.buttons.put(index, placeholder);
                    inventory.setItem(index, placeholder.getButtonItem(player));
                }

            }

        }

        return inventory;
    }

    private static Method getOpenInventoryMethod() {
        if (openInventoryMethod == null) {
            try {
                openInventoryMethod = CraftHumanEntity.class.getDeclaredMethod("openCustomInventory", Inventory.class, EntityPlayer.class, Integer.TYPE);
                openInventoryMethod.setAccessible(true);
            } catch (NoSuchMethodException var1) {
                var1.printStackTrace();
            }
        }

        return openInventoryMethod;
    }

    private ItemStack createItemStack(Player player, Button button) {
        ItemStack item = button.getButtonItem(player);

        if (item.getType() != Material.SKULL_ITEM) {

            ItemMeta meta = item.getItemMeta();

            if (meta != null && meta.hasDisplayName()) {
                meta.setDisplayName(meta.getDisplayName() + "§k§e§r§e§m");
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    public Menu() {
    }

    public Menu(String staticTitle) {
        this.staticTitle = (String) Preconditions.checkNotNull((Object)staticTitle);
    }

    public void openMenu(Player player) {

        final EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        final Inventory inventory = this.createInventory(player);

        try {
            getOpenInventoryMethod().invoke(player,inventory,entityPlayer,0);
            this.update(player);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    private void update(Player player) {
        cancelCheck(player);
        currentlyOpenedMenus.put(player.getUniqueId(),this);

        this.onOpen(player);

        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {

                if (!player.isOnline()) {
                    Menu.cancelCheck(player);
                    Menu.currentlyOpenedMenus.remove(player.getUniqueId());
                }

                if (isAutoUpdate()) {
                    player.getOpenInventory().getTopInventory().setContents(createInventory(player).getContents());
                }

            }
        };

        runnable.runTaskTimer(Proton.getInstance(), 10L, 10L);
        checkTasks.put(player.getUniqueId(), runnable);
    }


    public static void cancelCheck(Player player) {
        if (checkTasks.containsKey(player.getUniqueId())) {
            checkTasks.remove(player.getUniqueId()).cancel();
        }

    }

    public int size(Player player) {
        int highest = 0;

        for (int buttonValue : getButtons(player).keySet()) {
            if (buttonValue > highest) {
                highest = buttonValue;
            }
        }

        return (int) (Math.ceil((highest + 1) / 9D) * 9D);
    }

    public int getSlot(int x, int y) {
        return ((9 * y) + x);
    }


    public String getTitle(Player player) {
        return this.staticTitle;
    }

    public abstract Map<Integer, Button> getButtons(Player player);

    public void onOpen(Player player) {}

    public void onClose(Player player) {}

}