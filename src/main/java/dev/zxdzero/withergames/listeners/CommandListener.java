package dev.zxdzero.withergames.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommandListener implements Listener {

    private final List<String> messages = Arrays.asList(
            "AH UP BUP BUP BUP",
            "no.",
            "stop. bad.",
            "bro",
            "well that was a close one"
    );

    private final Random random = new Random();

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage().toLowerCase().trim();


        if (msg.equals("/kill @e") || msg.equals("kill @e")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text(messages.get(random.nextInt(messages.size())), NamedTextColor.RED));
        }
    }


}
