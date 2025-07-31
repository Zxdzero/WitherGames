package dev.withergames.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ResetCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        String countdown = args[0].toLowerCase();

        if (countdown.equals("amulet") && commandSender instanceof Player player) {
            player.setCooldown(Material.AMETHYST_SHARD, 0);

            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Objective objective = scoreboard.getObjective("amulet_cooldown");
            assert objective != null;
            objective.getScore(player.getName()).setScore(0);
        } else if (countdown.equals("faction_weapon") && commandSender instanceof Player player) {
            player.setCooldown(Material.PITCHER_POD, 0);

            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Objective objective = scoreboard.getObjective("faction_weapon_cooldown");
            assert objective != null;
            objective.getScore(player.getName()).setScore(0);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("amulet", "faction_weapon");
        }
        return Collections.emptyList();
    }
}
