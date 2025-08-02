package dev.zxdzero.withergames.commands;

import dev.zxdzero.withergames.withergames;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class HeartsCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /hearts <add|remove|set> <number> [player]", NamedTextColor.RED));
            return true;
        }

        String action = args[0].toLowerCase();
        int value;

        try {
            value = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Invalid number.", NamedTextColor.RED));
            return true;
        }

        Player target;
        if (args.length >= 3) {
            target = Bukkit.getPlayerExact(args[2]);
            if (target == null || !target.isOnline()) {
                sender.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Component.text("Console must specify a player.", NamedTextColor.RED));
                return true;
            }
            target = (Player) sender;
        }

        int amount;

        if (action.equals("add")) {
            amount = value * 2;
        } else if (action.equals("remove")) {
            amount = value * -2;
        } else if (action.equals("set")) {
            amount = (int) (value*2 - Objects.requireNonNull(target.getAttribute(Attribute.MAX_HEALTH)).getValue());
        } else {
            sender.sendMessage(Component.text("Unknown action: use 'add' or 'remove'", NamedTextColor.RED));
            return true;
        }

        withergames.modifyHearts(target, amount);
        sender.sendMessage(Component.text()
                .append(Component.text(action.equals("remove") ? "Removed " : "Added ", NamedTextColor.GREEN))
                .append(Component.text(action.equals("set") ? amount : value/2, NamedTextColor.GOLD))
                .append(Component.text(" heart" + (value > 1 ? "s" : "") + (action.equals("remove") ? " from " : " to "), NamedTextColor.GREEN))
                .append(Component.text(target.getName(), NamedTextColor.GOLD)));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("add", "remove", "set");
        } else if (args.length == 2) {
            return Collections.emptyList();
        } else if (args.length == 3) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}