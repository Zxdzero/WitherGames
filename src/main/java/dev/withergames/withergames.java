package dev.withergames;

import dev.withergames.commands.*;
import dev.withergames.items.Amulets;
import dev.withergames.items.FactionWeapons;
import dev.withergames.items.ItemsMenuManager;
import dev.withergames.items.LegendaryWeapons;
import dev.withergames.listeners.*;
import dev.withergames.listeners.items.*;
import dev.zxdzero.registries.RecipeManager;
import dev.zxdzero.registries.RecipeManager.PedestalRecipe;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class withergames extends JavaPlugin {

    private Economy econ;

    private static withergames plugin;

    public static withergames getPlugin() {
        return plugin;
    }

    public static NamespacedKey damageKey;
    public static NamespacedKey speedKey;

    private boolean showWorthiness;

    public static Random random = new Random();

    @Override
    public void onEnable() {
        plugin = this;
        damageKey = new NamespacedKey(plugin, "attack_damage");
        speedKey = new NamespacedKey(plugin, "attack_speed");
        ItemsMenuManager itemsMenuManager = new ItemsMenuManager();

        getServer().getPluginManager().registerEvents(new PlayerKillListener(), this);
        getServer().getPluginManager().registerEvents(new EntityPotionEffectListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new BlazeAmuletListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new TormentFeatherListener(), this);
        getServer().getPluginManager().registerEvents(new CommandListener(), this);
        getServer().getPluginManager().registerEvents(new DartGunListener(), this);
        getServer().getPluginManager().registerEvents(new WonderPickaxeListener(), this);
        getServer().getPluginManager().registerEvents(new SoulCanisterListener(), this);
        getServer().getPluginManager().registerEvents(itemsMenuManager, this);

        getCommand("tip").setExecutor(new TipCommand());
        getCommand("hearts").setExecutor(new HeartsCommand());
        getCommand("reset").setExecutor(new ResetCommand());

        itemsMenuManager.registerMenus();

        RecipeManager.registerRecipe(this, "wonder_pickaxe", new RecipeManager.PedestalRecipe(
                LegendaryWeapons.wonderPickaxe(),
                List.of(
                        new ItemStack(Material.DIAMOND_BLOCK, 8),
                        new ItemStack(Material.PRISMARINE_SHARD, 16),
                        new ItemStack(Material.ENCHANTING_TABLE, 8),
                        new ItemStack(Material.NETHERITE_PICKAXE, 1)
                )
        ));
        RecipeManager.registerRecipe(this, "soul_canister", new PedestalRecipe(
                LegendaryWeapons.soulCanister(),
                List.of(
                        new ItemStack(Material.BLAZE_ROD, 32),
                        new ItemStack(Material.SOUL_LANTERN, 64),
                        new ItemStack(Material.NETHER_STAR, 1)
                )
        ));

        Amulets.registerBehavior();
        FactionWeapons.registerBehavior();

        SoulCanisterListener.startStatusEffectUpdater();

        saveDefaultConfig();
        showWorthiness = getConfig().getBoolean("show-worthiness");

        if (!setupEconomy()) {
            getLogger().severe("No Vault-compatible economy plugin found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Repeating task every second
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (showWorthiness) {

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getWorld().getName().contains("THINGWHY")) {
                        int worthiness = getPoints(player);
                        double balance = 0;
                        try {
                            balance = econ.getBalance(player);
                        } catch (Exception ignored) {
                        }

                        Component message = Component.text()
                                .append(Component.text("Coins: $", NamedTextColor.GREEN))
                                .append(Component.text(Math.round(balance), NamedTextColor.GREEN))
                                .append(Component.text(" | ", NamedTextColor.GRAY))
                                .append(Component.text("Worthiness: ", NamedTextColor.GOLD))
                                .append(Component.text(worthiness + "%", NamedTextColor.YELLOW))
                                .build();

                        player.sendActionBar(message);
                    }
                }
            }
        }, 0L, 20L); // 20 ticks = 1 second
    }

    private int getPoints(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        if (board.getObjective("worthiness") == null) return 0;
        return board.getObjective("worthiness").getScore(player.getName()).getScore();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return true;
    }

    public static void modifyHearts(Player target, double amount) {
        double attackerHealth = Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(attackerHealth + amount);
        if (amount > 0) {
            target.setHealth(target.getHealth() + amount);
        }
    }

    public static List<Component> loreBuilder(List<String> lore) {
        List<Component> list = new ArrayList<>();
        for (int i = 0; i < lore.size(); i++) {
            Component component = Component.text(" - " + lore.get(i), NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false);
            list.add(component);
        }

        return list;
    }

    public static ItemMeta weaponBuilder(ItemMeta meta, int attackDamage, double attackSpeed) {
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(
                withergames.damageKey,
                attackDamage,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
        ));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(
                withergames.speedKey,
                attackSpeed - 4.0,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
        ));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.lore(List.of(
                Component.text(""),
                Component.text("When in Main Hand:", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text(" " + attackDamage + " Attack Damage", NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC, false),
                Component.text(" " + attackSpeed +  " Attack Speed", NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC, false)
        ));
        return meta;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
