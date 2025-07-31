package dev.withergames.pedestal;

import dev.withergames.items.LegendaryWeapons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeManager {
    private static final Map<String, PedestalRecipe> recipes = new HashMap<>();

    public static void initRecipes() {
        recipes.put("wonder_pickaxe", new PedestalRecipe(
                LegendaryWeapons.wonderPickaxe(),
                List.of(
                        new ItemStack(Material.DIAMOND_BLOCK, 8),
                        new ItemStack(Material.PRISMARINE_SHARD, 18),
                        new ItemStack(Material.ENCHANTING_TABLE, 8),
                        new ItemStack(Material.NETHERITE_PICKAXE, 1)
                )
        ));

        recipes.put("soul_canister", new PedestalRecipe(
                LegendaryWeapons.soulCanister(),
                List.of(
                        new ItemStack(Material.BLAZE_ROD, 32),
                        new ItemStack(Material.SOUL_LANTERN, 64),
                        new ItemStack(Material.NETHER_STAR, 1)
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
