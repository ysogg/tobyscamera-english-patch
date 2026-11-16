package tcenglish.plugin;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import tcenglish.Translations;

/**
 * Companion plugin for the English agent.
 *
 * TobysCamera reads some of its own lore back (film lines by prefix, the
 * "Negative" marker by equality). Items created before the patch still carry
 * the Chinese text, so this converts it to the matching English text whenever
 * such an item turns up in a player inventory, an opened container, or a pickup.
 */
public final class EnglishPatchPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        if (!"active".equals(System.getProperty("tcenglish.agent"))) {
            getLogger().severe("The English agent is NOT loaded, so TobysCamera text is still Chinese.");
            getLogger().severe("Add  -javaagent:plugins/" + getFile().getName()
                    + "  to the java command in run.bat (before -jar) and restart the server.");
            getLogger().severe("Existing-item conversion is disabled until then.");
            return;
        }
        getServer().getPluginManager().registerEvents(this, this);
        for (Player p : getServer().getOnlinePlayers()) migratePlayer(p);
        getLogger().info("English agent active (" + System.getProperty("tcenglish.replaced", "0")
                + " strings translated so far; more are translated as TobysCamera loads them).");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        migratePlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onOpen(InventoryOpenEvent event) {
        migrate(event.getInventory());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        Item entity = event.getItem();
        ItemStack stack = entity.getItemStack();
        if (migrate(stack)) entity.setItemStack(stack);
    }

    private void migratePlayer(Player player) {
        migrate(player.getInventory());
        migrate(player.getEnderChest());
    }

    private void migrate(Inventory inventory) {
        ItemStack[] contents = inventory.getContents();
        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack stack = contents[slot];
            if (migrate(stack)) inventory.setItem(slot, stack);
        }
    }

    /** Translates Chinese TobysCamera name/lore on one item. Returns true if changed. */
    static boolean migrate(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.hasItemMeta()) return false;
        ItemMeta meta = stack.getItemMeta();
        boolean changed = false;

        if (meta.hasDisplayName()) {
            Component before = meta.displayName();
            Component after = translate(before);
            if (after != before) { meta.displayName(after); changed = true; }
        }
        if (meta.hasItemName()) {
            Component before = meta.itemName();
            Component after = translate(before);
            if (after != before) { meta.itemName(after); changed = true; }
        }
        List<Component> lore = meta.lore();
        if (lore != null && !lore.isEmpty()) {
            List<Component> updated = new ArrayList<>(lore.size());
            boolean loreChanged = false;
            for (Component line : lore) {
                Component t = translate(line);
                loreChanged |= t != line;
                updated.add(t);
            }
            if (loreChanged) { meta.lore(updated); changed = true; }
        }
        if (changed) stack.setItemMeta(meta);
        return changed;
    }

    /** Returns the same instance when nothing was translated; keeps all styling. */
    private static Component translate(Component component) {
        if (component == null) return null;
        Component out = component;
        if (component instanceof TextComponent text) {
            String english = Translations.translateItemLine(text.content());
            if (english != null) out = text.content(english);
        }
        List<Component> children = out.children();
        if (!children.isEmpty()) {
            List<Component> updated = new ArrayList<>(children.size());
            boolean any = false;
            for (Component child : children) {
                Component t = translate(child);
                any |= t != child;
                updated.add(t);
            }
            if (any) out = out.children(updated);
        }
        return out;
    }
}
