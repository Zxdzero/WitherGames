package dev.zxdzero.withergames.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ItemActionRegistry {
    private static final Map<CustomModelDataComponent, BiConsumer<Player, ItemStack>> actions = new HashMap<>();

    public static void register(CustomModelDataComponent customModelData, BiConsumer<Player, ItemStack> action) {
        actions.put(customModelData, action);
    }

    public static void register(ItemStack item, BiConsumer<Player, ItemStack> action) {
        actions.put(item.getItemMeta().getCustomModelDataComponent(), action);
    }

    public static BiConsumer<Player, ItemStack> getAction(CustomModelDataComponent customModelData) {
        return actions.get(customModelData);
    }
}
