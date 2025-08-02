package dev.zxdzero.withergames.items;

import dev.zxdzero.withergames.listeners.items.SoulCanisterListener;
import dev.zxdzero.withergames.withergames;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class LegendaryWeapons {

    public static final NamespacedKey wonderKey = new NamespacedKey(withergames.getPlugin(), "wonder_pickaxe");
    public static final NamespacedKey soulKey = new NamespacedKey(withergames.getPlugin(), "soul_count");

    public static ItemStack wonderPickaxe() {
        ItemStack pickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta meta = pickaxe.getItemMeta();
        meta.displayName(Component.text("Wonder Pickaxe").decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        CustomModelDataComponent customModelData = meta.getCustomModelDataComponent();
        customModelData.setFloats(List.of(3001f));
        meta.setCustomModelDataComponent(customModelData);
        meta.lore(withergames.loreBuilder(List.of(
                "Gain a new enchantment every 10 blocks"
        )));
        meta.getPersistentDataContainer().set(wonderKey, PersistentDataType.INTEGER, 0);
        pickaxe.setItemMeta(meta);

        return pickaxe;
    }

    public static ItemStack soulCanister() {
        ItemStack canister = new ItemStack(Material.TURTLE_SCUTE);
        ItemMeta meta = canister.getItemMeta();
        meta.displayName(Component.text("Soul Canister").decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        CustomModelDataComponent customModelData = meta.getCustomModelDataComponent();
        customModelData.setFloats(List.of(3002f));
        meta.setCustomModelDataComponent(customModelData);
        SoulCanisterListener.updateItemDisplay(meta, 0);
//        meta.lore(withergames.loreBuilder(List.of(
//                "Gain a new enchantment every 10 blocks"
//        )));
        meta.getPersistentDataContainer().set(soulKey, PersistentDataType.INTEGER, 0);
        canister.setItemMeta(meta);

        return canister;
    }

    public static ItemStack megatonas() {
        ItemStack megatonis = new ItemStack(Material.MACE);
        ItemMeta meta = megatonis.getItemMeta();
        meta.displayName(Component.text("Soul Canister").decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        CustomModelDataComponent customModelData = meta.getCustomModelDataComponent();
        customModelData.setFloats(List.of(3003f));
        meta.setCustomModelDataComponent(customModelData);
        SoulCanisterListener.updateItemDisplay(meta, 0);
        meta = withergames.weaponBuilder(meta, 12, 1);
        meta.lore().add(Component.text(" - Right click to cleave surroundings", NamedTextColor.GOLD));
        meta.getPersistentDataContainer().set(soulKey, PersistentDataType.INTEGER, 0);
        megatonis.setItemMeta(meta);

        return megatonis;
    }
}
