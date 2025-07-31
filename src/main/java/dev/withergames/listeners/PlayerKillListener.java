package dev.withergames.listeners;

import dev.withergames.withergames;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class PlayerKillListener implements Listener {

    @EventHandler
    public void onPlayerKillEvent(PlayerDeathEvent e) {
        if (e.getDamageSource().getCausingEntity() instanceof Player player && !(player.getUniqueId() == e.getPlayer().getUniqueId())) {
            withergames.modifyHearts(player, 2);
        }

        withergames.modifyHearts(e.getPlayer(), -2);
    }
}
