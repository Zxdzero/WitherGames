package dev.zxdzero.withergames.listeners.items;

import dev.zxdzero.withergames.withergames;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class DartGunListener implements Listener {

    @EventHandler
    public void onCrossbowShoot(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player player && e.getBow().getType() == Material.CROSSBOW) {
            ItemMeta meta = e.getBow().getItemMeta();
            if (meta.hasCustomModelData() && meta.getCustomModelData() == 2001) {

                Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
                Objective objective = scoreboard.getObjective("faction_weapon_cooldown");
                objective.getScore(player.getName()).setScore(60);

                new BukkitRunnable() {
                    int shotsFired = 0;

                    @Override
                    public void run() {
                        if (shotsFired++ >= 5) {
                            cancel();
                            return;
                        }

                        Arrow arrow = player.launchProjectile(Arrow.class);
                        arrow.setVelocity(player.getLocation().getDirection().multiply(2.0));
                        arrow.setShooter(player);
                        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                        arrow.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 210, 0), true); // 2 sec poison

                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1f, 1.5f);
                    }
                }.runTaskTimer(withergames.getPlugin(), 0L, 10L);
                e.setCancelled(true);
                player.stopSound(Sound.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS);
            }
        }
    }
}
