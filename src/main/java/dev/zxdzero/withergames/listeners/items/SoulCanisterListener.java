package dev.zxdzero.withergames.listeners.items;

import dev.zxdzero.withergames.items.LegendaryWeapons;
import dev.zxdzero.withergames.withergames;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SoulCanisterListener implements Listener {
    private static final int CUSTOM_MODEL_DATA = 3002;
    private static final int MAX_KILL_COUNT = 5;
    private static final double INSTANT_KILL_RANGE = 5.0;

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) return;

        ItemStack soulCanister = findInInventory(killer);
        if (soulCanister == null) return;

        incrementKillCount(soulCanister, killer);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (!isSoulCanister(item)) return;

        int killCount = 0;
        if (isSoulCanister(item)) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                killCount = container.getOrDefault(LegendaryWeapons.soulKey, PersistentDataType.INTEGER, 0);

                if (killCount < MAX_KILL_COUNT) return;

                player.getWorld().playSound(player.getLocation(), Sound.PARTICLE_SOUL_ESCAPE, 5f, 1.0f);
                player.getWorld().playSound(player.getLocation(), Sound.PARTICLE_SOUL_ESCAPE, 5f, 1.0f);
                player.getWorld().playSound(player.getLocation(), Sound.PARTICLE_SOUL_ESCAPE, 5f, 1.5f);
                player.getWorld().playSound(player.getLocation(), Sound.PARTICLE_SOUL_ESCAPE, 5f, 1.5f);
                player.getWorld().playSound(player.getLocation(), Sound.PARTICLE_SOUL_ESCAPE, 5f, 1.8f);
                player.getWorld().playSound(player.getLocation(), Sound.PARTICLE_SOUL_ESCAPE, 5f, 1.8f);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 0.5f);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 0.6f, 0.3f);
                player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 0.3f, 1.4f);

                Location startLocation = player.getEyeLocation();
                Vector direction = startLocation.getDirection().normalize();
                World world = player.getWorld();

// Create the particle beam and check for entities along the path
                List<LivingEntity> hitEntities = new ArrayList<>();
                double stepSize = 0.1; // Distance between particle spawns
                int maxSteps = (int) (INSTANT_KILL_RANGE / stepSize);

                for (int i = 0; i < maxSteps; i++) {
                    // Calculate current position along the beam
                    Location currentLocation = startLocation.clone().add(direction.clone().multiply(i * stepSize));

                    // Check if we hit a block (stop the beam)
                    if (currentLocation.getBlock().getType().isSolid()) {
                        break;
                    }

                    // Spawn purple dust particles at this location
                    world.spawnParticle(
                            Particle.DUST_COLOR_TRANSITION,
                            currentLocation,
                            1, // particle count
                            0, 0, 0, // offset
                            0, // extra speed
                            new Particle.DustTransition(
                                    Color.PURPLE,
                                    Color.fromRGB(128, 0, 128), // darker purple
                                    2.0f // size
                            )
                    );

                    // Check for entities at this location
                    Collection<Entity> nearbyEntities = world.getNearbyEntities(currentLocation, 0.5, 0.5, 0.5);
                    for (Entity entity : nearbyEntities) {
                        if (entity instanceof LivingEntity target && !entity.equals(player) && !hitEntities.contains(target)) {
                            hitEntities.add(target);
                        }
                    }
                }

// Execute instant kill on all hit entities
                for (LivingEntity target : hitEntities) {
                    executeInstantKill(player, target);
                }

// Update item state regardless of whether we hit anything
                container.set(LegendaryWeapons.soulKey, PersistentDataType.INTEGER, 0);
                updateItemDisplay(meta, 0);
                applyStatusEffects(player, 0);
                item.setItemMeta(meta);
                event.setCancelled(true);
            }
        }


    }

    private static ItemStack findInInventory(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (isSoulCanister(mainHand)) {
            return mainHand;
        }

        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (isSoulCanister(offHand)) {
            return offHand;
        }

        for (ItemStack item : player.getInventory().getContents()) {
            if (isSoulCanister(item)) {
                return item;
            }
        }

        return null;
    }

    private static boolean isSoulCanister(ItemStack item) {
        if (item == null || item.getType() != Material.TURTLE_SCUTE) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        return meta.hasCustomModelData() && meta.getCustomModelData() == CUSTOM_MODEL_DATA;
    }

    private void incrementKillCount(ItemStack item, Player player) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        int currentCount = container.getOrDefault(LegendaryWeapons.soulKey, PersistentDataType.INTEGER, 0);

        if (currentCount >= MAX_KILL_COUNT) return;

        int newCount = currentCount + 1;
        container.set(LegendaryWeapons.soulKey, PersistentDataType.INTEGER, newCount);

        // Update item display name and lore
        updateItemDisplay(meta, newCount);
        item.setItemMeta(meta);

        // Apply status effects based on kill count
        applyStatusEffects(player, newCount);

        // Visual and audio feedback
        player.getWorld().spawnParticle(Particle.SOUL, player.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.5f, 1.8f);
        player.playSound(player.getLocation(), Sound.ENTITY_ALLAY_DEATH, 0.4f, 2.0f);
        player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_RETURN, 0.3f, 1.5f);
        player.getWorld().playSound(player.getLocation(), Sound.PARTICLE_SOUL_ESCAPE, 5f, 1.0f);

        if (newCount == MAX_KILL_COUNT) {
//            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.5f, 0.9f);
            player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.2f, 2.0f);

        }
    }

    public static void updateItemDisplay(ItemMeta meta, int killCount) {
        //ChatColor.DARK_RED + "Soul Canister " + ChatColor.GRAY + "(" + killCount + "/" + MAX_KILL_COUNT + ")"
        meta.displayName(Component.text()
                .append(Component.text("Soul Canister").decoration(TextDecoration.BOLD, true))
                .append(Component.text(" (" + killCount + "/" + MAX_KILL_COUNT + ")", NamedTextColor.GRAY))
                .build());

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text()
                .append(Component.text("Souls Harvested: ", NamedTextColor.GRAY))
                .append(Component.text(killCount, NamedTextColor.RED))
                .build());
        lore.add(Component.text(""));

        // Add current effect description
        switch (killCount) {
            case 1:
                lore.add(Component.text( "◆ Speed I", NamedTextColor.BLUE));
                break;
            case 2:
                lore.add(Component.text( "◆ Speed I", NamedTextColor.BLUE));
                lore.add(Component.text( "◆ Invisibility", NamedTextColor.BLUE));
                break;
            case 3:
                lore.add(Component.text( "◆ Speed II", NamedTextColor.BLUE));
                lore.add(Component.text( "◆ Invisibility", NamedTextColor.BLUE));
                break;
            case 4:
                lore.add(Component.text( "◆ Speed II", NamedTextColor.BLUE));
                lore.add(Component.text( "◆ Invisibility", NamedTextColor.BLUE));
                lore.add(Component.text( "◆ Strength I", NamedTextColor.BLUE));
                break;
            case 5:
                lore.add(Component.text( "◆ Speed II", NamedTextColor.BLUE));
                lore.add(Component.text( "◆ Invisibility", NamedTextColor.BLUE));
                lore.add(Component.text( "◆ Strength I", NamedTextColor.BLUE));
                lore.add(Component.text(""));
                lore.add(Component.text( "Right-click to release souls", NamedTextColor.DARK_PURPLE));
                break;
        }

        meta.lore(lore);
    }

    private static void applyStatusEffects(Player player, int killCount) {
        switch (killCount) {
            case 5:
            case 4:
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 15, 0));
            case 3:
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15, 1));
            case 2:
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 15, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15, 0));
                break;
            case 1:
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15, 0));
                break;
        }
    }

    private void executeInstantKill(Player player, LivingEntity target) {
        playEffects(player, target);
        target.damage(target.getHealth() + 100, player);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 20 || target.isDead()) { // Run for 1 second or until dead
                    cancel();
                    return;
                }

                // Continuous soul particles
                target.getWorld().spawnParticle(Particle.SOUL, target.getLocation().add(0, 1, 0), 5, 0.3, 0.5, 0.3, 0.1);

                ticks++;
            }
        }.runTaskTimer(withergames.getPlugin(), 0L, 1L);
    }

    private void playEffects(Player player, LivingEntity target) {
        // Sound effects reminiscent of Minecraft Dungeons Harvester
        player.getWorld().playSound(target.getLocation(), Sound.PARTICLE_SOUL_ESCAPE, 5f, 1.0f);
        player.getWorld().playSound(target.getLocation(), Sound.PARTICLE_SOUL_ESCAPE, 5f, 1.0f);
        player.getWorld().playSound(target.getLocation(), Sound.PARTICLE_SOUL_ESCAPE, 5f, 1.5f);
        player.getWorld().playSound(target.getLocation(), Sound.PARTICLE_SOUL_ESCAPE, 5f, 1.5f);
        player.getWorld().playSound(target.getLocation(), Sound.PARTICLE_SOUL_ESCAPE, 5f, 1.8f);
        player.getWorld().playSound(target.getLocation(), Sound.PARTICLE_SOUL_ESCAPE, 5f, 1.8f);
        player.getWorld().playSound(target.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 0.5f);
        player.getWorld().playSound(target.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 0.6f, 0.3f);
        player.getWorld().playSound(target.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 0.3f, 1.4f);

        // Dramatic particle effects
        target.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, target.getLocation().add(0, 1, 0), 20, 0.5, 1.0, 0.5, 0.1);
        target.getWorld().spawnParticle(Particle.LARGE_SMOKE, target.getLocation().add(0, 1, 0), 15, 0.3, 0.5, 0.3, 0.05);
        target.getWorld().spawnParticle(Particle.ENCHANT, target.getLocation().add(0, 1, 0), 30, 1.0, 1.0, 1.0, 0.5);

        // Dark magic circle effect
        new BukkitRunnable() {
            double angle = 0;
            int duration = 0;

            @Override
            public void run() {
                if (duration >= 10) {
                    cancel();
                    return;
                }

                for (int i = 0; i < 8; i++) {
                    double x = Math.cos(angle + (i * Math.PI / 4)) * 2;
                    double z = Math.sin(angle + (i * Math.PI / 4)) * 2;

                    target.getWorld().spawnParticle(Particle.WITCH,
                            target.getLocation().add(x, 0.1, z), 1, 0, 0, 0, 0);
                }

                angle += Math.PI / 8;
                duration++;
            }
        }.runTaskTimer(withergames.getPlugin(), 0L, 2L);
    }

    @EventHandler
    private void onPlayerRespawn(PlayerRespawnEvent e) {
        ItemStack soulCanister = findInInventory(e.getPlayer());
        if (soulCanister != null ) {
            ItemMeta meta = soulCanister.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                applyStatusEffects(e.getPlayer(), container.getOrDefault(LegendaryWeapons.soulKey, PersistentDataType.INTEGER, 0));
            }
        }
    }

    public static void startStatusEffectUpdater() {
        Bukkit.getScheduler().runTaskTimer(withergames.getPlugin(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ItemStack soulCanister = findInInventory(player);
                if (soulCanister != null ) {
                    ItemMeta meta = soulCanister.getItemMeta();
                    if (meta != null) {
                        PersistentDataContainer container = meta.getPersistentDataContainer();
                        applyStatusEffects(player, container.getOrDefault(LegendaryWeapons.soulKey, PersistentDataType.INTEGER, 0));
                    }
                }
            }
        }, 0L, 10L);
    }
}