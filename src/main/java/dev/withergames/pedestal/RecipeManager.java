package dev.withergames.pedestal;

import dev.withergames.items.Amulets;
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
    private static final Map<String, PedestalRecipe> recipes = new HashMap<>();

    public static void initRecipes() {
        recipes.put("diamond_sword", new PedestalRecipe(
                new ItemStack(Material.DIAMOND_SWORD),
                List.of(
                        new ItemStack(Material.DIAMOND, 2),
                        new ItemStack(Material.STICK, 1)
                )
        ));

        recipes.put("life_amulet", new PedestalRecipe(
                Amulets.lifeAmulet(),
                List.of(
                        new ItemStack(Material.APPLE, 1),
                        new ItemStack(Material.GOLD_INGOT, 8)
                )
        ));
    }

    public static PedestalRecipe getRecipe(String id) {
        return recipes.get(id);
    }

    public static List<String> getAllRecipes() {
        return recipes.keySet().stream().toList();
    }

    public record PedestalRecipe(ItemStack result, List<ItemStack> ingredients) {

        public ItemStack result() {
            return result.clone();
        }

            public Component getRecipeText() {
                TextComponent.Builder text = Component.text();
                text.append(result.displayName().color(NamedTextColor.GOLD));
                for (ItemStack ingredient : ingredients) {
                    text.append(Component.text("\n"));
                    text.append(Component.text(ingredient.getAmount() + "x " + ingredient.getType().name().replace("_", " "), NamedTextColor.GREEN));
                }
                return text.build();
            }
        }
}
