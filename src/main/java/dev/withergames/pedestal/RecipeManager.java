package dev.withergames.pedestal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeManager {
    private final Map<String, PedestalRecipe> recipes = new HashMap<>();

    public RecipeManager() {
        recipes.put("diamond_sword", new PedestalRecipe(
                new ItemStack(Material.DIAMOND_SWORD),
                List.of(
                        new ItemStack(Material.DIAMOND, 2),
                        new ItemStack(Material.STICK, 1)
                )
        ));

        recipes.put("golden_apple", new PedestalRecipe(
                new ItemStack(Material.GOLDEN_APPLE),
                List.of(
                        new ItemStack(Material.APPLE, 1),
                        new ItemStack(Material.GOLD_INGOT, 8)
                )
        ));
    }

    public PedestalRecipe getRecipe(String id) {
        return recipes.get(id);
    }

    public record PedestalRecipe(ItemStack result, List<ItemStack> ingredients) {

        public ItemStack result() {
            return result.clone();
        }

            public Component getRecipeText() {
                TextComponent.Builder text = Component.text();
                text.append(result.displayName().color(NamedTextColor.GOLD));
                for (ItemStack ingredient : ingredients) {
                    text.append(Component.text(ingredient.getAmount() + "x " + ingredient.getType().name().replace("_", " "), NamedTextColor.GREEN));
                }
                return text.build();
            }
        }
}
