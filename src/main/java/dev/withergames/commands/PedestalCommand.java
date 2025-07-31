package dev.withergames.commands;

import dev.withergames.pedestal.PedestalManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PedestalCommand implements CommandExecutor {
    private final PedestalManager pedestalManager;

    public PedestalCommand(PedestalManager pedestalManager) {
        this.pedestalManager = pedestalManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /pedestal <place|refill|remove> [item_type]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "place":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /pedestal place <item_type>");
                    return true;
                }

                try {
                    pedestalManager.placePedestal(player.getLocation(), args[1]);
                    player.sendMessage("§aPedestal placed for " + args[1]);
                } catch (IllegalArgumentException e) {
                    player.sendMessage("§cInvalid item type: " + args[1]);
                }
                break;

            case "refill":
                // Find nearby pedestal bases
                for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
                    if (entity instanceof ArmorStand stand) {
                        pedestalManager.refillPedestal(stand);
                        player.sendMessage("§aPedestal refilled!");
                        return true;
                    }
                }
                player.sendMessage("§cNo pedestal found nearby!");
                break;

            case "remove":
                if (pedestalManager.removePedestal(player.getLocation(), 5.0)) {
                    player.sendMessage("§aPedestal removed!");
                } else {
                    player.sendMessage("§cNo pedestal found nearby!");
                }
                break;

            default:
                player.sendMessage("§cUsage: /pedestal <place|refill|remove> [item_type]");
        }

        return true;
    }
}
