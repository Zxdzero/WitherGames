package dev.withergames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Objects;

public final class EntityPotionEffectListener implements Listener {

    @EventHandler
    public void onStatusEffect(EntityPotionEffectEvent e) {
        if ((e.getEntity() instanceof Player player) && !(e.getNewEffect() == null)) {

            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Objective objective = scoreboard.getObjective("worthiness");
            if (objective == null) {
                Bukkit.getLogger().warning("Objective 'worthiness' does not exist!");
                return;
            }

            if (objective.getScore(player.getName()).getScore() >= 25) {
                String team = Objects.requireNonNull(scoreboard.getEntryTeam(player.getName())).getName();
                PotionEffectType effect = e.getNewEffect().getType();
                if (    (Objects.equals(team, "bogged") && effect == PotionEffectType.POISON) ||
                        (Objects.equals(team, "stray") && effect == PotionEffectType.SLOWNESS) ||
                        (Objects.equals(team, "skeleton") && effect == PotionEffectType.WEAKNESS) ||
                        (Objects.equals(team, "wither_skeleton") && effect == PotionEffectType.WITHER)) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
