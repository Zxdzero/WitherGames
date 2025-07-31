package dev.withergames.pedestal;

import dev.withergames.withergames;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PedestalManager implements Listener {

    private withergames plugin = withergames.getPlugin();
    private final RecipeManager recipeManager = new RecipeManager();
    private final NamespacedKey pedestalKey =  new NamespacedKey(plugin, "pedestal_id");
    private final NamespacedKey itemTypeKey = new NamespacedKey(plugin, "item_type");
    private final NamespacedKey textDisplayKey = new NamespacedKey(plugin, "text_display_uuid");
    private final NamespacedKey interactionKey = new NamespacedKey(plugin, "interaction_uuid");
    private final NamespacedKey itemDisplayKey = new NamespacedKey(plugin, "item_display_uuid");
    private final NamespacedKey pedestalBaseKey = new NamespacedKey(plugin, "pedestal_base_uuid");


    public void placePedestal(Location location, String id) {
        RecipeManager.PedestalRecipe recipe = recipeManager.getRecipe(id);
        if (recipe == null) {
            return; // No recipe found
        }

        UUID pedestalId = UUID.randomUUID();

        // Create pedestal base (armor stand with custom model)
        ArmorStand base = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        base.setVisible(false);
        base.setGravity(false);
        base.setSmall(true);
        base.addScoreboardTag("pedestal");
        base.getPersistentDataContainer().set(pedestalKey, PersistentDataType.STRING, pedestalId.toString());
        base.getPersistentDataContainer().set(itemTypeKey, PersistentDataType.STRING, id);

        // Create spinning item display
        Location itemLoc = location.clone().add(0, 1.2, 0);
        ItemDisplay itemDisplay = (ItemDisplay) location.getWorld().spawnEntity(itemLoc, EntityType.ITEM_DISPLAY);
        itemDisplay.setItemStack(recipe.result());
        itemDisplay.setGravity(false);

        // Create hologram text
        Location textLoc = location.clone().add(0, 2.5, 0);
        TextDisplay textDisplay = (TextDisplay) location.getWorld().spawnEntity(textLoc, EntityType.TEXT_DISPLAY);
        textDisplay.text(recipe.getRecipeText());
        textDisplay.setBillboard(TextDisplay.Billboard.CENTER);
        textDisplay.setGravity(false);

        // Create interaction entity
        Location interactionLoc = location.clone().add(0, 1, 0);
        Interaction interaction = (Interaction) location.getWorld().spawnEntity(interactionLoc, EntityType.INTERACTION);
        interaction.setInteractionWidth(1.5f);
        interaction.setInteractionHeight(2.0f);
        interaction.getPersistentDataContainer().set(pedestalBaseKey, PersistentDataType.STRING, base.getUniqueId().toString());

        // Store entity UUIDs in pedestal base NBT
        base.getPersistentDataContainer().set(textDisplayKey, PersistentDataType.STRING, textDisplay.getUniqueId().toString());
        base.getPersistentDataContainer().set(interactionKey, PersistentDataType.STRING, interaction.getUniqueId().toString());
        base.getPersistentDataContainer().set(itemDisplayKey, PersistentDataType.STRING, itemDisplay.getUniqueId().toString());
    }

    public void refillPedestal(ArmorStand base) {
        String pedestalIdStr = base.getPersistentDataContainer().get(pedestalKey, PersistentDataType.STRING);
        String itemTypeStr = base.getPersistentDataContainer().get(itemTypeKey, PersistentDataType.STRING);

        if (pedestalIdStr == null || itemTypeStr == null) return;

        // Check if pedestal already has entities (is already filled)
        String textDisplayIdStr = base.getPersistentDataContainer().get(textDisplayKey, PersistentDataType.STRING);
        if (textDisplayIdStr != null) {
            // Already filled, don't refill
            return;
        }

        try {
            Material itemType = Material.valueOf(itemTypeStr);
            RecipeManager.PedestalRecipe recipe = recipeManager.getRecipe(itemType);
            if (recipe == null) return;

            Location location = base.getLocation();

            // Create spinning item display
            Location itemLoc = location.clone().add(0, 1.2, 0);
            ItemDisplay itemDisplay = (ItemDisplay) location.getWorld().spawnEntity(itemLoc, EntityType.ITEM_DISPLAY);
            itemDisplay.setItemStack(recipe.result());
            itemDisplay.setGravity(false);

            // Create hologram text
            Location textLoc = location.clone().add(0, 2.5, 0);
            TextDisplay textDisplay = (TextDisplay) location.getWorld().spawnEntity(textLoc, EntityType.TEXT_DISPLAY);
            textDisplay.text(recipe.getRecipeText());
            textDisplay.setBillboard(TextDisplay.Billboard.CENTER);
            textDisplay.setGravity(false);

            // Create interaction entity
            Location interactionLoc = location.clone().add(0, 1, 0);
            Interaction interaction = (Interaction) location.getWorld().spawnEntity(interactionLoc, EntityType.INTERACTION);
            interaction.setInteractionWidth(1.5f);
            interaction.setInteractionHeight(2.0f);
            interaction.getPersistentDataContainer().set(pedestalBaseKey, PersistentDataType.STRING, base.getUniqueId().toString());

            // Store entity UUIDs in pedestal base NBT
            base.getPersistentDataContainer().set(textDisplayKey, PersistentDataType.STRING, textDisplay.getUniqueId().toString());
            base.getPersistentDataContainer().set(interactionKey, PersistentDataType.STRING, interaction.getUniqueId().toString());
            base.getPersistentDataContainer().set(itemDisplayKey, PersistentDataType.STRING, itemDisplay.getUniqueId().toString());

        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid item type stored in pedestal: " + itemTypeStr);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Interaction interaction)) return;

        String pedestalBaseIdStr = interaction.getPersistentDataContainer().get(pedestalBaseKey, PersistentDataType.STRING);
        if (pedestalBaseIdStr == null) return;

        UUID pedestalBaseId = UUID.fromString(pedestalBaseIdStr);
        ArmorStand base = (ArmorStand) Bukkit.getEntity(pedestalBaseId);
        if (base == null) return;

        String itemTypeStr = base.getPersistentDataContainer().get(itemTypeKey, PersistentDataType.STRING);
        if (itemTypeStr == null) return;

        Material itemType = Material.valueOf(itemTypeStr);
        Player player = event.getPlayer();
        RecipeManager.PedestalRecipe recipe = recipeManager.getRecipe(itemType);
        if (recipe == null) return;

        // Check if player has all ingredients
        if (hasAllIngredients(player, recipe)) {
            // Remove ingredients from inventory
            removeIngredients(player, recipe);

            // Give result item
            player.getInventory().addItem(recipe.result());

            // Get and remove entities using stored UUIDs
            String textDisplayIdStr = base.getPersistentDataContainer().get(textDisplayKey, PersistentDataType.STRING);
            String interactionIdStr = base.getPersistentDataContainer().get(interactionKey, PersistentDataType.STRING);
            String itemDisplayIdStr = base.getPersistentDataContainer().get(itemDisplayKey, PersistentDataType.STRING);

            if (textDisplayIdStr != null) {
                TextDisplay textDisplay = (TextDisplay) Bukkit.getEntity(UUID.fromString(textDisplayIdStr));
                if (textDisplay != null) textDisplay.remove();
            }

            if (interactionIdStr != null) {
                Interaction interactionEntity = (Interaction) Bukkit.getEntity(UUID.fromString(interactionIdStr));
                if (interactionEntity != null) interactionEntity.remove();
            }

            if (itemDisplayIdStr != null) {
                ItemDisplay itemDisplay = (ItemDisplay) Bukkit.getEntity(UUID.fromString(itemDisplayIdStr));
                if (itemDisplay != null) itemDisplay.remove();
            }

            // Clear NBT data from pedestal base
            base.getPersistentDataContainer().remove(textDisplayKey);
            base.getPersistentDataContainer().remove(interactionKey);
            base.getPersistentDataContainer().remove(itemDisplayKey);

            player.sendMessage("§aCrafting successful!");
        } else {
            player.sendMessage("§cYou don't have all the required ingredients!");
        }
    }
    private boolean hasAllIngredients(Player player, RecipeManager.PedestalRecipe recipe) {
        Map<Material, Integer> required = new HashMap<>();
        for (ItemStack ingredient : recipe.ingredients()) {
            required.merge(ingredient.getType(), ingredient.getAmount(), Integer::sum);
        }

        Map<Material, Integer> available = new HashMap<>();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                available.merge(item.getType(), item.getAmount(), Integer::sum);
            }
        }

        for (Map.Entry<Material, Integer> entry : required.entrySet()) {
            if (available.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    private void removeIngredients(Player player, RecipeManager.PedestalRecipe recipe) {
        Map<Material, Integer> toRemove = new HashMap<>();
        for (ItemStack ingredient : recipe.ingredients()) {
            toRemove.merge(ingredient.getType(), ingredient.getAmount(), Integer::sum);
        }

        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null || item.getType() == Material.AIR) continue;

            Integer needed = toRemove.get(item.getType());
            if (needed != null && needed > 0) {
                int toTake = Math.min(needed, item.getAmount());
                item.setAmount(item.getAmount() - toTake);
                toRemove.put(item.getType(), needed - toTake);

                if (item.getAmount() == 0) {
                    contents[i] = null;
                }
            }
        }
        player.getInventory().setContents(contents);
    }


}
