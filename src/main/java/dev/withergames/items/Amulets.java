package dev.withergames.items;

import dev.withergames.withergames;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Amulets {

    private static boolean checkCooldown(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective("amulet_cooldown");
        int cooldown = 0;
        if (objective != null) {
            cooldown = objective.getScore(player.getName()).getScore();
        }

        if (cooldown == 0) {
            objective.getScore(player.getName()).setScore(1200);
            player.setCooldown(Material.AMETHYST_SHARD, 1200);
        } else {
            player.sendMessage(Component.text("You must wait another " + cooldown/20 + " seconds to use another amulet!", NamedTextColor.RED));
        }
        return cooldown == 0;
    }

    public static void registerBehavior() {

        // Life Amulet
        ItemActionRegistry.register(1001, (player, item) -> {
            if (checkCooldown(player)) {
                Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_ABSORPTION)).setBaseValue(12);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 6.0f, 1.0f);
                new BukkitRunnable() {
                    int ticks = 0;
                    final int duration = 8;

                    @Override
                    public void run() {
                        if (ticks++ > duration) {
                            // Final flash of protective energy
                            player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation().clone().add(0, 1, 0), 5, 0.3, 0.3, 0.3, 0.05);
                            cancel();
                            return;
                        }

                        for (int i = 0; i < 8; i++) {
                            double angle = Math.random() * 2 * Math.PI;
                            double radius = 0.6 + Math.random() * 0.2;
                            double x = Math.cos(angle) * radius;
                            double z = Math.sin(angle) * radius;
                            double y = 1.0 + Math.random() * 0.3;

                            Location pLoc = player.getLocation().clone().add(x, y, z);

                            // Golden dust swirl
                            Particle.DustTransition dust = new Particle.DustTransition(
                                    Color.fromRGB(255, 205, 85), // soft gold
                                    Color.WHITE,
                                    1.0f
                            );
                            player.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, pLoc, 1, 0, 0, 0, 0, dust);

                            // Soft sparkles
                            if (Math.random() < 0.15) {
                                player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, pLoc, 0);
                            }
                        }
                    }
                }.runTaskTimer(withergames.getPlugin(), 0L, 1L);

                player.setAbsorptionAmount(player.getAbsorptionAmount() + 12);
            }
        });

        //Blaze Amulet
        ItemActionRegistry.register(1002, (player, item) -> {
            if (checkCooldown(player)) {
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
                Objective objective = scoreboard.getObjective("blaze_ticks");
                if (objective == null) {
                    player.sendMessage(Component.text("Server error, please contact an admin!", NamedTextColor.RED));
                }
                player.getWorld().spawnParticle(
                        Particle.FLAME,
                        player.getLocation().add(0, 1, 0), // slightly above ground
                        100, // count
                        0.5, 1, 0.5, // x, y, z offset
                        0.05 // speed
                );
                player.getWorld().spawnParticle(
                        Particle.LARGE_SMOKE,
                        player.getLocation().add(0, 1, 0),
                        40,
                        0.3, 0.8, 0.3,
                        0.01
                );
                player.getWorld().playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1.0f, 1.0f);
                objective.getScore(player.getName()).setScore(1200);
            }
        });

        //Frost Amulet
        ItemActionRegistry.register(1003, (player, item) -> {
            if (checkCooldown(player)) {
                final int totalShots = 10;
                final int delayBetweenShots = 5;
                final UUID playerId = player.getUniqueId();

                new BukkitRunnable() {
                    int shotsFired = 0;

                    @Override
                    public void run() {
                        Player player = Bukkit.getPlayer(playerId);
                        if (player == null || !player.isOnline()) {
                            cancel();
                            return;
                        }

                        Location eyeLoc = player.getEyeLocation();
                        Vector forward = eyeLoc.getDirection().normalize();

                        Vector right = forward.clone().crossProduct(new Vector(0, 1, 0)).normalize();
                        Vector up = right.clone().crossProduct(forward).normalize();

                        World world = player.getWorld();

                        int rows = 2;
                        int cols = 10;
                        double spacing = 0.6;

                        double baseVelocity = 0.7;
                        double minVelocityFactor = 0.8; // Top row gets 40% of base speed
                        double maxVelocityFactor = 1.0; // Bottom row gets 100% of base speed

                        for (int row = 0; row < rows; row++) {
                            double rowFactor = maxVelocityFactor - ((double) row / (rows - 1)) * (maxVelocityFactor - minVelocityFactor);

                            for (int col = 0; col < cols; col++) {
                                Vector offset = right.clone().multiply((col - (cols - 1) / 2.0) * spacing)
                                        .add(up.clone().multiply((row - (rows - 1) / 2.0) * spacing));

                                Location spawnLoc = eyeLoc.clone().add(forward).add(offset);

                                FallingBlock fallingBlock = (FallingBlock) world.spawnEntity(spawnLoc, EntityType.FALLING_BLOCK);
                                fallingBlock.setBlockData(Material.POWDER_SNOW.createBlockData());
                                fallingBlock.setDropItem(false);
                                fallingBlock.setHurtEntities(false);

                                Vector velocity = forward.clone().multiply(baseVelocity * rowFactor);
                                fallingBlock.setVelocity(velocity);
                            }
                        }
                        shotsFired++;
                        player.playSound(player.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1f, 0.5f);
                        player.getWorld().spawnParticle(Particle.SNOWFLAKE, player.getLocation(), 40, 2, 1.5, 2, 0.1);

                        if (shotsFired >= totalShots) {
                            cancel();
                        }
                    }
                }.runTaskTimer(withergames.getPlugin(), 0, delayBetweenShots);
            }
        });

        //Void Amulet
        ItemActionRegistry.register(1004, ((player, itemStack) -> {
            if (checkCooldown(player)) {
                player.getWorld().spawnParticle(
                        Particle.DRAGON_BREATH, // purple-ish particle like totem effect
                        player.getLocation().add(0, 1, 0), // around player's head
                        50,  // count (number of particles)
                        0.5, 0.5, 0.5, // spread in x, y, z directions
                        0.1  // speed
                );
                Location location = player.getLocation().add(0, 1, 0);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + player.getName() + " at @s run function withergames:void_ring");
                player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.0f);
            }
        }));
    }


    public static ItemStack lifeAmulet() {
        ItemStack amulet = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = amulet.getItemMeta();
        meta.displayName(Component.text("Life Amulet").decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        meta.setCustomModelData(1001);
        meta.lore(withergames.loreBuilder(List.of(
                "6 absorption hearts",
                "Regen for 5 seconds"
        )));
        amulet.setItemMeta(meta);

        return amulet;
    }

    public static ItemStack blazeAmulet() {
        ItemStack amulet = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = amulet.getItemMeta();
        meta.displayName(Component.text("Blaze Amulet").decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        meta.setCustomModelData(1002);
        meta.lore(withergames.loreBuilder(List.of(
                "When right clicked | Gives user 60 seconds of blaze powers.",
                "Flaming attacks",
                "Fire resistance"
        )));
        amulet.setItemMeta(meta);

        return amulet;
    }

    public static ItemStack frostAmulet() {
        ItemStack amulet = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = amulet.getItemMeta();
        meta.displayName(Component.text("Frost Amulet").decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        meta.setCustomModelData(1003);
        meta.lore(withergames.loreBuilder(List.of(
                "Spew forth powder snow"
        )));
        amulet.setItemMeta(meta);

        return amulet;
    }

    public static ItemStack voidAmulet() {
        ItemStack amulet = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = amulet.getItemMeta();
        meta.displayName(Component.text("Void Amulet").decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        meta.setCustomModelData(1004);
        meta.lore(withergames.loreBuilder(List.of(
                "Ring of void damage",
                "12x12 area"
        )));
        amulet.setItemMeta(meta);

        return amulet;
    }


}