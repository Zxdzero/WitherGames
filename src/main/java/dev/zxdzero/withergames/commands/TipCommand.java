package dev.zxdzero.withergames.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TipCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective("worthiness");

        if (objective.getScore(commandSender.getName()).getScore() >= 75) {
            PotionEffect potionEffect = switch (Objects.requireNonNull(scoreboard.getEntryTeam(commandSender.getName())).getName()) {
                case "bogged" -> new PotionEffect(PotionEffectType.POISON, 200, 0);
                case "stray" -> new PotionEffect(PotionEffectType.SLOWNESS, 200, 0);
                case "skeleton" -> new PotionEffect(PotionEffectType.WEAKNESS, 200, 0);
                case "wither_skeleton" -> new PotionEffect(PotionEffectType.WITHER, 200, 0);
                default -> null;
            };
            convertAllArrowsToTipped((Player) commandSender, potionEffect);
        } else {
            commandSender.sendMessage("§cYou must be at least 75% worthy!");
        }

        return true;
    }

    public static void convertAllArrowsToTipped(Player player, PotionEffect potionEffect) {
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            if (item == null || item.getType() != Material.ARROW) continue;

            // Create tipped arrow with custom slowness effect
            ItemStack tippedArrow = new ItemStack(Material.TIPPED_ARROW, item.getAmount());
            PotionMeta meta = (PotionMeta) tippedArrow.getItemMeta();
            meta.addCustomEffect(potionEffect, true); // 10s duration
            meta.displayName(Component.text("Tipped Arrow").decoration(TextDecoration.ITALIC, false));
            tippedArrow.setItemMeta(meta);

            contents[i] = tippedArrow;
        }

        player.getInventory().setContents(contents);
        player.updateInventory();
    }
}