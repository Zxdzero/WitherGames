package dev.withergames.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ItemActionRegistry {
    private static final Map<Integer, BiConsumer<Player, ItemStack>> actions = new HashMap<>();

    public static void register(int customModelData, BiConsumer<Player, ItemStack> action) {
        actions.put(customModelData, action);
    }

    public static BiConsumer<Player, ItemStack> getAction(int customModelData) {
        return actions.get(customModelData);
    }
}
