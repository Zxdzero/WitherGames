package dev.zxdzero.withergames.items;

import dev.zxdzero.withergames.withergames;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public class FactionWeapons {

    private static boolean checkCooldown(Player player, Integer newCooldown) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective("faction_weapon_cooldown");
        int cooldown = 0;
        if (objective != null) {
            cooldown = objective.getScore(player.getName()).getScore();
        }

        if (cooldown == 0) {
            objective.getScore(player.getName()).setScore(newCooldown);
            player.setCooldown(Material.PITCHER_POD, newCooldown);
        } else {
            player.sendMessage(Component.text("You must wait another " + cooldown/20 + " seconds to use this ability again!", NamedTextColor.RED));
        }
        return cooldown == 0;
    }

    private static boolean checkCooldown(Player player) { return checkCooldown(player, 1200); }

    public static void registerBehavior() {

        // Glacial Scythe
        ItemActionRegistry.register(2002, (player, item) -> {
            if (checkCooldown(player)) {
                player.getWorld().spawnParticle(Particle.SNOWFLAKE, player.getLocation(), 80, 2, 1.5, 2, 0.1);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1.0f);
                player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1f, 0.6f);

                for (Entity nearby : player.getNearbyEntities(6, 4, 6)) {
                    if (nearby instanceof LivingEntity target && !target.equals(player)) {
                        target.damage(6, player);
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 2)); // Slowness III
                    }
                }

                // Freeze terrain
                int radius = 3;
                Block centerBlock = player.getLocation().getBlock();
                World world = player.getWorld();

                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dy = -radius; dy <= radius; dy++) {
                        for (int dz = -radius; dz <= radius; dz++) {
                            // Check if the current offset is within the sphere
                            if (dx * dx + dy * dy + dz * dz <= radius * radius) {
                                Block block = world.getBlockAt(centerBlock.getX() + dx, centerBlock.getY() + dy, centerBlock.getZ() + dz);
                                Material original = block.getType();

                                if (original == Material.GRASS_BLOCK || original == Material.DIRT) {
                                    block.setType(Material.ICE);
                                    Bukkit.getScheduler().runTaskLater(withergames.getPlugin(), () -> {
                                        if (block.getType() == Material.ICE) block.setType(original);
                                    }, 20 * 10); // revert in 10s
                                } else if (original == Material.WATER) {
                                    block.setType(Material.FROSTED_ICE);
//                                    Bukkit.getScheduler().runTaskLater(withergames.getPlugin(), () -> {
//                                        if (block.getType() == Material.FROSTED_ICE) block.setType(Material.WATER);
//                                    }, 20 * 10);
                                }
                            }
                        }
                    }
                }
            }
        });

        // Feather of Torment
        ItemActionRegistry.register(2003, (player, item) -> {
            if (checkCooldown(player)) {

                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BREEZE_IDLE_AIR, 0.6f, 2.0f);
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_WOOL_BREAK, 0.4f, 1.5f);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.2f, 2.0f);

                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.SPEED,
                        200,
                        4,
                        false,
                        false,
                        true
                ));
                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.WEAKNESS,
                        200,
                        4,
                        false,
                        false,
                        true
                ));

                new BukkitRunnable() {
                    int ticks = 0;
                    final int duration = 8;

                    @Override
                    public void run() {
                        if (ticks++ > duration) {
                            cancel();
                            return;
                        }

                        for (int i = 0; i < 12; i++) {
                            double offsetX = (Math.random() - 0.5) * 1.5;
                            double offsetY = Math.random() * 1.2;
                            double offsetZ = (Math.random() - 0.5) * 1.5;
                            Location pLoc = player.getLocation().clone().add(offsetX, offsetY, offsetZ);

                            // Puff of wind
                            player.getWorld().spawnParticle(Particle.CLOUD, pLoc, 0, 0, 0.01, 0, 0.01);

                            // Light "feather" burst
                            if (Math.random() < 0.4) {
                                player.getWorld().spawnParticle(Particle.FALLING_DUST, pLoc, 1, 0, 0, 0, 0.01, Material.WHITE_WOOL.createBlockData());
                            }

                            // Occasionally add sweep effect
                            if (Math.random() < 0.15) {
                                player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, pLoc, 0);
                            }
                        }
                    }
                }.runTaskTimer(withergames.getPlugin(), 0L, 1L);
            }
        });

        // Ghost Knife
        ItemActionRegistry.register(2004, (player, item) -> {
            if (checkCooldown(player, 900)) {
                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.INVISIBILITY,
                        200,
                        0,
                        false,
                        false,
                        true
                ));

                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 0.5f, 1.4f);
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SOUL_SAND_BREAK, 0.4f, 0.8f);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PHANTOM_AMBIENT, 0.3f, 1.0f);

                new BukkitRunnable() {
                    int ticks = 0;
                    final int duration = 10;

                    @Override
                    public void run() {
                        if (ticks++ > duration) {
                            // Add subtle soul effect at the end
                            player.getWorld().spawnParticle(Particle.SOUL, player.getLocation().clone().add(0, 1, 0), 2, 0.2, 0.1, 0.2, 0.01);
                            cancel();
                            return;
                        }

                        for (int i = 0; i < 10; i++) {
                            double offsetX = (Math.random() - 0.5) * 1.2;
                            double offsetY = Math.random() * 1.0;
                            double offsetZ = (Math.random() - 0.5) * 1.2;
                            Location pLoc = player.getLocation().clone().add(offsetX, offsetY, offsetZ);

                            player.getWorld().spawnParticle(Particle.SMOKE, pLoc, 0, 0, 0.01, 0, 0.005);
                            player.getWorld().spawnParticle(Particle.ASH, pLoc, 0, 0, 0.01, 0, 0.005);
                            if (Math.random() < 0.05) {
                                player.getWorld().spawnParticle(Particle.BLOCK, pLoc, 1, 0, 0, 0, 0.02, Material.SOUL_SAND.createBlockData());
                            }
                        }
                    }
                }.runTaskTimer(withergames.getPlugin(), 0L, 1L);
            }
        });
    }

    public static ItemStack dartGun() {
        ItemStack gun = new ItemStack(Material.CROSSBOW);
        ItemMeta meta = gun.getItemMeta();
        meta.displayName(Component.text("Dart Gun").decoration(TextDecoration.ITALIC, false));
        meta.setCustomModelData(2001);
        meta.lore(withergames.loreBuilder(List.of(
                "5 rapid fire poison darts"
        )));
        gun.setItemMeta(meta);

        return gun;
    }

    public static ItemStack glacialScythe() {
        ItemStack scythe = new ItemStack(Material.PITCHER_POD);
        ItemMeta meta = scythe.getItemMeta();
        meta.displayName(Component.text("Glacial Scythe").decoration(TextDecoration.ITALIC, false));
        meta.setCustomModelData(2002);
        meta = withergames.weaponBuilder(meta, 7, 1.6);
        meta.lore(withergames.loreBuilder(List.of(
                "Sweeping attack",
                "Makes the ground slick"
        )));
        scythe.setItemMeta(meta);
        return scythe;
    }

    public static ItemStack feather() {
        ItemStack feather = new ItemStack(Material.PITCHER_POD);
        ItemMeta meta = feather.getItemMeta();
        meta.displayName(Component.text("Feather of Torment").decoration(TextDecoration.ITALIC, false));
        meta.setCustomModelData(2003);
        meta.lore(withergames.loreBuilder(List.of(
                "Speed 5 for quick getaway",
                "Weakness 5"
        )));
        feather.setItemMeta(meta);

        return feather;
    }

    public static ItemStack ghostKnife() {
        ItemStack knife = new ItemStack(Material.PITCHER_POD);
        ItemMeta meta = knife.getItemMeta();
        meta.displayName(Component.text("Ghost Knife").decoration(TextDecoration.ITALIC, false));
        meta.setCustomModelData(2004);
        meta = withergames.weaponBuilder(meta, 6, 2.0);
        meta.lore(withergames.loreBuilder(List.of(
                "Temporary invisibility"
        )));
        knife.setItemMeta(meta);

        return knife;
    }
}
