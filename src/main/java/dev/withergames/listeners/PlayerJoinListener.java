package dev.withergames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        Objective amuletCooldown = scoreboard.getObjective("amulet_cooldown");
        assert amuletCooldown != null;
        e.getPlayer().setCooldown(Material.AMETHYST_SHARD, amuletCooldown.getScore(e.getPlayer().getName()).getScore());

        Objective factionWeaponCooldown = scoreboard.getObjective("faction_weapon_cooldown");
        assert factionWeaponCooldown != null;
        e.getPlayer().setCooldown(Material.PITCHER_POD, factionWeaponCooldown.getScore(e.getPlayer().getName()).getScore());

    }
}
