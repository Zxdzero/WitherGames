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
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PedestalManager implements Listener {

    private static withergames plugin = withergames.getPlugin();
    public static final NamespacedKey pedestalKey =  new NamespacedKey(plugin, "pedestal_id");
    private final NamespacedKey itemIdKey = new NamespacedKey(plugin, "item_type");
    private final NamespacedKey textDisplayKey = new NamespacedKey(plugin, "text_display_uuid");
    private final NamespacedKey interactionKey = new NamespacedKey(plugin, "interaction_uuid");
    private final NamespacedKey itemDisplayKey = new NamespacedKey(plugin, "item_display_uuid");
    private final NamespacedKey pedestalBaseKey = new NamespacedKey(plugin, "pedestal_base_uuid");


    public void placePedestal(Location location, String id) {
        RecipeManager.PedestalRecipe recipe = RecipeManager.getRecipe(id);
        if (recipe == null) {
            throw new IllegalArgumentException("Invalid pedestal id");
        }

        UUID pedestalId = UUID.randomUUID();


        BlockDisplay base = (BlockDisplay) location.getWorld().spawnEntity(location, EntityType.BLOCK_DISPLAY);
        base.setGravity(false);

        Transformation transform = new Transformation(
                new Vector3f(-0.25f, 0, -0.25f),           // translation (offset)
                new AxisAngle4f(0, 0, 0, 1),     // left rotation (no rotation)
                new Vector3f(0.5f, 1.0f, 0.5f),  // scale
                new AxisAngle4f(0, 0, 0, 1)      // right rotation (no rotation)
        );
        base.setTransformation(transform);
        base.setRotation(0,0);
        base.setBlock(Material.QUARTZ_PILLAR.createBlockData());

        base.addScoreboardTag("pedestal");
        base.getPersistentDataContainer().set(pedestalKey, PersistentDataType.STRING, pedestalId.toString());
        base.getPersistentDataContainer().set(itemIdKey, PersistentDataType.STRING, id);

        // Create spinning item display
        Location itemLoc = location.clone().add(0, 1.8, 0);
        ItemDisplay itemDisplay = (ItemDisplay) location.getWorld().spawnEntity(itemLoc, EntityType.ITEM_DISPLAY);
        itemDisplay.setItemStack(recipe.result());
        itemDisplay.setGravity(false);
//        itemDisplay.setRotation( withergames.random.nextInt(360),0);
        itemDisplay.setBillboard(TextDisplay.Billboard.CENTER);
        itemDisplay.setDisplayWidth(1.5f);
        itemDisplay.setDisplayHeight(1.5f);
        itemDisplay.addScoreboardTag("spin");

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

    public boolean refillPedestal(BlockDisplay base) {
        String pedestalIdStr = base.getPersistentDataContainer().get(pedestalKey, PersistentDataType.STRING);
        String itemType = base.getPersistentDataContainer().get(itemIdKey, PersistentDataType.STRING);

        if (pedestalIdStr == null || itemType == null) return false;

        // Check if pedestal already has entities (is already filled)
        String textDisplayIdStr = base.getPersistentDataContainer().get(textDisplayKey, PersistentDataType.STRING);
        if (textDisplayIdStr != null) {
            // Already filled, don't refill
            return false;
        }

        try {
            RecipeManager.PedestalRecipe recipe = RecipeManager.getRecipe(itemType);
            if (recipe == null) return false;

            Location location = base.getLocation();

            // Create spinning item display
            Location itemLoc = location.clone().add(0, 1.8, 0);
            ItemDisplay itemDisplay = (ItemDisplay) location.getWorld().spawnEntity(itemLoc, EntityType.ITEM_DISPLAY);
            itemDisplay.setItemStack(recipe.result());
            itemDisplay.setGravity(false);
//            itemDisplay.setRotation( withergames.random.nextInt(360),0);
            itemDisplay.setBillboard(TextDisplay.Billboard.CENTER);
            itemDisplay.setDisplayWidth(1.5f);
            itemDisplay.setDisplayHeight(1.5f);
            itemDisplay.addScoreboardTag("spin");

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

            return true;

        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid item type stored in pedestal: " + itemType);
            return false;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Interaction interaction)) return;

        String pedestalBaseIdStr = interaction.getPersistentDataContainer().get(pedestalBaseKey, PersistentDataType.STRING);
        if (pedestalBaseIdStr == null) return;

        UUID pedestalBaseId = UUID.fromString(pedestalBaseIdStr);
        BlockDisplay base = (BlockDisplay) Bukkit.getEntity(pedestalBaseId);
        if (base == null) return;

        String itemType = base.getPersistentDataContainer().get(itemIdKey, PersistentDataType.STRING);
        if (itemType == null) return;

        Player player = event.getPlayer();
        RecipeManager.PedestalRecipe recipe = RecipeManager.getRecipe(itemType);
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

    public boolean removePedestal(BlockDisplay base) {
        // Verify this is actually a pedestal base
        String pedestalIdStr = base.getPersistentDataContainer().get(pedestalKey, PersistentDataType.STRING);
        if (pedestalIdStr == null) {
            return false; // Not a pedestal
        }

        // Get and remove associated entities using stored UUIDs
        String textDisplayIdStr = base.getPersistentDataContainer().get(textDisplayKey, PersistentDataType.STRING);
        String interactionIdStr = base.getPersistentDataContainer().get(interactionKey, PersistentDataType.STRING);
        String itemDisplayIdStr = base.getPersistentDataContainer().get(itemDisplayKey, PersistentDataType.STRING);

        // Remove text display
        if (textDisplayIdStr != null) {
            TextDisplay textDisplay = (TextDisplay) Bukkit.getEntity(UUID.fromString(textDisplayIdStr));
            if (textDisplay != null) {
                textDisplay.remove();
            }
        }

        // Remove interaction entity
        if (interactionIdStr != null) {
            Interaction interaction = (Interaction) Bukkit.getEntity(UUID.fromString(interactionIdStr));
            if (interaction != null) {
                interaction.remove();
            }
        }

        // Remove item display
        if (itemDisplayIdStr != null) {
            ItemDisplay itemDisplay = (ItemDisplay) Bukkit.getEntity(UUID.fromString(itemDisplayIdStr));
            if (itemDisplay != null) {
                itemDisplay.remove();
            }
        }

        // Finally remove the base itself
        base.remove();

        return true; // Successfully removed
    }

    // Overloaded method to remove pedestal by location (finds nearest pedestal base)
    public boolean removePedestal(Location location, double radius) {
        BlockDisplay closestPedestal = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
            if (entity instanceof BlockDisplay stand) {
                String pedestalIdStr = stand.getPersistentDataContainer().get(pedestalKey, PersistentDataType.STRING);
                if (pedestalIdStr != null) {
                    double distance = location.distanceSquared(stand.getLocation());
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestPedestal = stand;
                    }
                }
            }
        }

        if (closestPedestal != null) {
            return removePedestal(closestPedestal);
        }

        return false; // No pedestal found in radius
    }
}
