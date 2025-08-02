package dev.withergames.items;

import dev.withergames.withergames;
import dev.zxdzero.registries.ItemMenuRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class ItemsMenuManager implements Listener {

    private static final withergames plugin = withergames.getPlugin();

    public static void registerMenus() {
        // Register the main WitherGames items menu (the original menu functionality)
        ItemMenuRegistry.registerItemMenu(
                plugin,
                "withergames_items",
                new ItemStack(Material.NETHER_STAR),
                Component.text("Withergames Items", NamedTextColor.GOLD),
                ItemsMenuManager::createWitherGamesItemsMenu
        );
    }

    public void unregisterMenus() {
        ItemMenuRegistry.unregisterMenusFromPlugin(plugin);
    }

    private static Inventory createWitherGamesItemsMenu() {
        Inventory itemsMenu = Bukkit.createInventory(null, 9, Component.text("Withergames Items"));

        ItemStack amuletsButton = new ItemStack(Material.AMETHYST_BLOCK);
        ItemMeta amuletMeta = amuletsButton.getItemMeta();
        amuletMeta.displayName(Component.text("Amulets", NamedTextColor.LIGHT_PURPLE));
        amuletsButton.setItemMeta(amuletMeta);

        ItemStack factionsButton = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta factionMeta = factionsButton.getItemMeta();
        factionMeta.displayName(Component.text("Faction Weapons", NamedTextColor.RED));
        factionsButton.setItemMeta(factionMeta);

        ItemStack legendaryButton = new ItemStack(Material.CROSSBOW);
        ItemMeta legendaryMeta = legendaryButton.getItemMeta();
        legendaryMeta.displayName(Component.text("Legendary Weapons", NamedTextColor.YELLOW));
        legendaryButton.setItemMeta(legendaryMeta);

        itemsMenu.setItem(2, amuletsButton);
        itemsMenu.setItem(4, factionsButton);
        itemsMenu.setItem(6, legendaryButton);

        return itemsMenu;
    }
    private Inventory createAmuletsMenu() {
        Inventory inv = Bukkit.createInventory(null, 9, Component.text("Amulets"));
        inv.setItem(0, Amulets.lifeAmulet());
        inv.setItem(1, Amulets.blazeAmulet());
        inv.setItem(2, Amulets.frostAmulet());
        inv.setItem(3, Amulets.voidAmulet());
        return inv;
    }

    private Inventory createFactionsMenu() {
        Inventory inv = Bukkit.createInventory(null, 9, Component.text("Faction Weapons"));
        inv.setItem(0, FactionWeapons.dartGun());
        inv.setItem(1, FactionWeapons.glacialScythe());
        inv.setItem(2, FactionWeapons.feather());
        inv.setItem(3, FactionWeapons.ghostKnife());
        return inv;
    }

    private Inventory createLegendaryMenu() {
        Inventory inv = Bukkit.createInventory(null, 9, Component.text("Legendary Weapons"));
        inv.setItem(0, LegendaryWeapons.wonderPickaxe());
        inv.setItem(1, LegendaryWeapons.soulCanister());
        return inv;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getOriginalTitle();

        System.out.println(title);

        // Handle the main WitherGames Items menu (replicates original /item command behavior)
        if (title.equals("Withergames Items")) {
            event.setCancelled(true); // Prevent taking the buttons

            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.AMETHYST_BLOCK) {
                player.openInventory(createAmuletsMenu());
            } else if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.DIAMOND_SWORD) {
                player.openInventory(createFactionsMenu());
            } else if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.CROSSBOW) {
                player.openInventory(createLegendaryMenu());
            }
        }
    }
}