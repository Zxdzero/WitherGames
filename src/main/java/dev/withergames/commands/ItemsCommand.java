package dev.withergames.commands;

import dev.withergames.items.Amulets;
import dev.withergames.items.FactionWeapons;
import dev.withergames.items.LegendaryWeapons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class ItemsCommand implements CommandExecutor, Listener {

    private Inventory itemsMenu;

    private Inventory amuletsMenu() {
        Inventory inv = Bukkit.createInventory(null, 9, Component.text("Amulets"));
        inv.setItem(0, Amulets.lifeAmulet());
        inv.setItem(1, Amulets.blazeAmulet());
        inv.setItem(2, Amulets.frostAmulet());
        inv.setItem(3, Amulets.voidAmulet());
        return inv;
    }

    private Inventory factionsMenu() {
        Inventory inv = Bukkit.createInventory(null, 9, Component.text("Faction Weapons"));
        inv.setItem(0, FactionWeapons.dartGun());
        inv.setItem(1, FactionWeapons.glacialScythe());
        inv.setItem(2, FactionWeapons.feather());
        inv.setItem(3, FactionWeapons.ghostKnife());
        return inv;
    }

    private Inventory legendaryMenu() {
        Inventory inv = Bukkit.createInventory(null, 9, Component.text("Legendary Weapons"));
        inv.setItem(0, LegendaryWeapons.wonderPickaxe());
        inv.setItem(1, LegendaryWeapons.soulCanister());
        return inv;
    }

    public ItemsCommand() {
        itemsMenu = Bukkit.createInventory(null, 9, Component.text("Items Menu"));

        ItemStack amuletsButton = new ItemStack(Material.AMETHYST_BLOCK);
        ItemMeta amuletMeta = amuletsButton.getItemMeta();
        amuletMeta.displayName(Component.text("Amulets", NamedTextColor.LIGHT_PURPLE));
        amuletsButton.setItemMeta(amuletMeta);

        ItemStack factionsButton = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta factionMeta = amuletsButton.getItemMeta();
        factionMeta.displayName(Component.text("Faction Weapons", NamedTextColor.RED));
        factionsButton.setItemMeta(factionMeta);

        ItemStack legendaryButton = new ItemStack(Material.CROSSBOW);
        ItemMeta legendaryMeta = amuletsButton.getItemMeta();
        legendaryMeta.displayName(Component.text("Legendary Weapons", NamedTextColor.YELLOW));
        legendaryButton.setItemMeta(legendaryMeta);

        itemsMenu.setItem(2, amuletsButton);
        itemsMenu.setItem(4, factionsButton);
        itemsMenu.setItem(6, legendaryButton);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player player) {
//            player.getInventory().addItem(Amulets.lifeAmulet());
            player.openInventory(itemsMenu);
        }
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Main Menu
        if (event.getView().title().equals(Component.text("Items Menu"))) {
            event.setCancelled(true); // Prevent taking the button

            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.AMETHYST_BLOCK) {
                player.openInventory(amuletsMenu());
            } else if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.DIAMOND_SWORD) {
                player.openInventory(factionsMenu());
            } else if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.CROSSBOW) {
                player.openInventory(legendaryMenu());
            }
        }
    }
}
