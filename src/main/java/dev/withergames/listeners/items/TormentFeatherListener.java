package dev.withergames.listeners.items;

import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class TormentFeatherListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getDamageSource().getDamageType() == DamageType.FALL &&
                e.getEntity() instanceof Player player && player.getInventory().getItemInMainHand().getItemMeta().hasCustomModelData() && player.getInventory().getItemInMainHand().getItemMeta().getCustomModelData() == 2003) {
            e.setCancelled(true);
        }
    }
}
