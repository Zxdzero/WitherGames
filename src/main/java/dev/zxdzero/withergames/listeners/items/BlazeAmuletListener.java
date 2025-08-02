package dev.zxdzero.withergames.listeners.items;

import dev.zxdzero.withergames.withergames;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class BlazeAmuletListener implements Listener {

    private boolean isBlaze(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective("blaze_ticks");
        assert objective != null;
        return objective.getScore(player.getName()).getScore() > 0;
    }

    @EventHandler
    public void onPlayerMelee(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && isBlaze(player) && event.getEntity() instanceof LivingEntity target) {
            target.setFireTicks(80);
            player.getWorld().spawnParticle(
                    Particle.FLAME,
                    player.getEyeLocation().add(player.getLocation().getDirection().multiply(0.5)).add(0, -0.3, 0),
                    10,
                    0.1, 0.1, 0.1,
                    0.01
            );
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile proj = event.getEntity();

        if ( (proj instanceof Arrow ||  proj instanceof WindCharge charge) && proj.getShooter() instanceof Player player && isBlaze(player)) {
            proj.setFireTicks(100);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!proj.isValid() || proj.isOnGround()) {
                        cancel();
                        return;
                    }
                    proj.getWorld().spawnParticle(
                            Particle.FLAME,
                            proj.getLocation(),
                            2,
                            0.02, 0.02, 0.02,
                            0.001
                    );
                }
            }.runTaskTimer(withergames.getPlugin(), 0L, 1L); // every tick
        }
    }

    @EventHandler
    public void onFire(EntityDamageEvent e) {
        DamageType type = e.getDamageSource().getDamageType();
        if (e.getEntity() instanceof Player player && isBlaze(player) && (type == DamageType.ON_FIRE || type == DamageType.IN_FIRE) ) {
            e.setCancelled(true);
        }
    }
}
