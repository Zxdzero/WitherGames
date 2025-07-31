package dev.withergames.listeners.items;

import dev.withergames.items.LegendaryWeapons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WonderPickaxeListener implements Listener {
    private final Random random = new Random();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();

        // Check if tool exists and has meta
        if (tool.getType() == Material.AIR || !tool.hasItemMeta()) {
            return;
        }

        ItemMeta meta = tool.getItemMeta();

        // Check if tool has custom model data 3001
        if (!meta.hasCustomModelData() || meta.getCustomModelData() != 3001) {
            return;
        }

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

        // Get current counter value (default to 0 if not present)
        int counter = dataContainer.getOrDefault(LegendaryWeapons.wonderKey, PersistentDataType.INTEGER, 0);

        // Increment counter
        counter++;

        // Check if counter reached 10
        if (counter >= 10) {
            // Apply random enchantment if under 20 total enchant levels
            int totalLevels = meta.getEnchants().values().stream().mapToInt(Integer::intValue).sum();
            if (totalLevels < 20) {
                Component name = applyRandomEnchant(meta);
                if (name != null) {
                    player.sendMessage(Component.text()
                            .append(Component.text("Gained "))
                            .append(name.color(NamedTextColor.GREEN))
                            .append(Component.text("!"))
                            .build());
                }
            }
            // Reset counter to 0
            counter = 0;
        }

        // Save updated counter
        dataContainer.set(LegendaryWeapons.wonderKey, PersistentDataType.INTEGER, counter);

        // Apply meta back to tool
        tool.setItemMeta(meta);
    }
    private Component applyRandomEnchant(ItemMeta meta) {
        List<Enchantment> availableEnchants = getAvailableEnchants(meta);

        if (availableEnchants.isEmpty()) {
            return null; // No available enchantments
        }

        // Select random enchantment
        Enchantment randomEnchant = availableEnchants.get(random.nextInt(availableEnchants.size()));

        // Get current level (0 if not present) and add 1, max 10
        int currentLevel = meta.getEnchantLevel(randomEnchant);
        int newLevel = Math.min(currentLevel + 1, 10);

        // Apply enchantment
        meta.addEnchant(randomEnchant, newLevel, true);

        return randomEnchant.displayName(newLevel);
    }

    private List<Enchantment> getAvailableEnchants(ItemMeta meta) {
        List<Enchantment> availableEnchants = new ArrayList<>();

        // Calculate total enchant levels currently on the item
        int totalLevels = meta.getEnchants().values().stream().mapToInt(Integer::intValue).sum();

        // If already at 20 levels, no more enchants can be added
        if (totalLevels >= 20) {
            return availableEnchants;
        }

        Enchantment[] allEnchants = {
                Enchantment.PROTECTION,
                Enchantment.FIRE_PROTECTION,
                Enchantment.FEATHER_FALLING,
                Enchantment.BLAST_PROTECTION,
                Enchantment.PROJECTILE_PROTECTION,
                Enchantment.RESPIRATION,
                Enchantment.AQUA_AFFINITY,
                Enchantment.THORNS,
                Enchantment.DEPTH_STRIDER,
                Enchantment.FROST_WALKER,
                Enchantment.SHARPNESS,
                Enchantment.SMITE,
                Enchantment.BANE_OF_ARTHROPODS,
                Enchantment.KNOCKBACK,
                Enchantment.FIRE_ASPECT,
                Enchantment.LOOTING,
                Enchantment.SWEEPING_EDGE,
                Enchantment.EFFICIENCY,
                Enchantment.SILK_TOUCH,
                Enchantment.UNBREAKING,
                Enchantment.FORTUNE,
                Enchantment.POWER,
                Enchantment.PUNCH,
                Enchantment.FLAME,
                Enchantment.INFINITY,
                Enchantment.LUCK_OF_THE_SEA,
                Enchantment.LURE,
                Enchantment.LOYALTY,
                Enchantment.IMPALING,
                Enchantment.RIPTIDE,
                Enchantment.CHANNELING,
                Enchantment.MULTISHOT,
                Enchantment.QUICK_CHARGE,
                Enchantment.PIERCING,
                Enchantment.MENDING,
                Enchantment.SOUL_SPEED,
                Enchantment.SWIFT_SNEAK,
                Enchantment.WIND_BURST
        };

        for (Enchantment enchant : allEnchants) {
            int currentLevel = meta.getEnchantLevel(enchant);

            // Can add/upgrade if under level 10 and won't exceed total cap
            if (currentLevel < 10) {
                // Skip conflicting enchantments
                if (hasConflictingEnchants(enchant, meta)) {
                    continue;
                }
                availableEnchants.add(enchant);
            }
        }

        return availableEnchants;
    }

    private boolean hasConflictingEnchants(Enchantment enchant, ItemMeta meta) {
        if (enchant == Enchantment.SILK_TOUCH && meta.hasEnchant(Enchantment.FORTUNE)) {
            return true;
        }
        return enchant == Enchantment.FORTUNE && meta.hasEnchant(Enchantment.SILK_TOUCH);
    }
}
