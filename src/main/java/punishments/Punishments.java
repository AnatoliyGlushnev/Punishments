package punishments;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;

import punishments.commands.GiveFatigueCommand;
import punishments.commands.ForgiveCommand;
import punishments.commands.ReloadCommand;
import punishments.commands.ListPunishedCommand;
import punishments.listeners.FatigueListener;

public class Punishments extends JavaPlugin {

    public String getJoinPunishedMessage() {
        return getConfig().getString("messages.join_punished", "§cНа вас наложен штраф! Для снятия обратитесь к судье.");
    }
    public String getEffectRestoredMessage() {
        return getConfig().getString("messages.effect_restored", "§cЭффект усталости возвращён после входа!");
    }
    public String getEffectRestoredDeathMessage() {
        return getConfig().getString("messages.effect_restored_death", "§cЭффект усталости возвращён после смерти!");
    }
    private static Punishments instance;

    @Override
    public void onEnable() {
        instance = this;
        System.out.println("[Punishments][DEBUG] onEnable called, registering listener");
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new FatigueListener(), this);
        getCommand("штраф").setExecutor(new GiveFatigueCommand());
        getCommand("снять_штраф").setExecutor(new ForgiveCommand());
        getCommand("punishmentsreload").setExecutor(new ReloadCommand());
        getCommand("штрафы").setExecutor(new ListPunishedCommand());
        getLogger().info("Punishments plugin enabled!");
    }

    public void startFatigueEnforcer(Player player) { 
        // Эффект усталости (каждые 2 тика)
        player.getScheduler().runAtFixedRate(
            this,
            task -> {
                if (!player.isOnline()) {
                    task.cancel();
                    return;
                }
                if (isPunished(player)) {
                    boolean hasEffect = player.getPotionEffect(PotionEffectType.getByName("MINING_FATIGUE")) != null;
                    if (!hasEffect) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.getByName("MINING_FATIGUE"), Integer.MAX_VALUE, 4, false, false, true));
                        System.out.println("[Punishments][DEBUG] [PlayerScheduler] Effect restored for " + player.getName());
                        player.sendMessage("§cЭффект усталости восстановлен автоматически!");
                    }
                }
            },
            () -> {},
            20L,
            20L
        );
        // ActionBar-напоминание (настраивается через config.yml)
        long actionBarInterval = getActionBarIntervalTicks();
        String actionBarMsg = "§l§c⚠ " + getActionBarMessage();
        player.getScheduler().runAtFixedRate(
            this,
            task -> {
                if (!player.isOnline()) {
                    task.cancel();
                    return;
                }
                if (isPunished(player)) {
                    // звук
                    try {
                        Sound s = Sound.valueOf(getConfig().getString("sounds.reminder.type", "BLOCK_NOTE_BLOCK_BELL"));
                        player.playSound(player.getLocation(), s,
                            (float) getConfig().getDouble("sounds.reminder.volume", 1.0),
                            (float) getConfig().getDouble("sounds.reminder.pitch", 0.5));
                    } catch (IllegalArgumentException ex) {
                        getLogger().warning("[Punishments] Некорректное имя звука в config.yml: " + getConfig().getString("sounds.reminder.type") + ". Используйте значения из org.bukkit.Sound");
                    }
                    player.sendActionBar(Component.text(actionBarMsg));
                    if (getConfig().getBoolean("sounds.reminder.repeat", false)) {
                        try {
                            Sound s = Sound.valueOf(getConfig().getString("sounds.reminder.type", "BLOCK_NOTE_BLOCK_BELL"));
                            player.playSound(player.getLocation(), s,
                                (float) getConfig().getDouble("sounds.reminder.volume", 1.0),
                                (float) getConfig().getDouble("sounds.reminder.pitch", 0.5));
                        } catch (IllegalArgumentException ex) {
                            getLogger().warning("[Punishments] Некорректное имя звука в config.yml: " + getConfig().getString("sounds.reminder.type") + ". Используйте значения из org.bukkit.Sound");
                        }
                    }
                    for (int i = 1; i < 5; i++) {
                        final int delayTicks = i * 20;
                        player.getScheduler().runDelayed(
                            this,
                            t -> {
                                if (player.isOnline() && isPunished(player)) {
                                    player.sendActionBar(Component.text(actionBarMsg));
                                    if (getConfig().getBoolean("sounds.reminder.repeat", false)) {
                                        try {
                                            Sound s = Sound.valueOf(getConfig().getString("sounds.reminder.type", "BLOCK_NOTE_BLOCK_BELL"));
                                            player.playSound(player.getLocation(), s,
                                                (float) getConfig().getDouble("sounds.reminder.volume", 1.0),
                                                (float) getConfig().getDouble("sounds.reminder.pitch", 0.5));
                                        } catch (IllegalArgumentException ex) {
                                            getLogger().warning("[Punishments] Некорректное имя звука в config.yml");
                                        }
                                    }
                                }
                            },
                            () -> {},
                            delayTicks
                        );
                    }
                }
            },
            () -> {},
            actionBarInterval,
            actionBarInterval 
        );
    }

    public String getActionBarMessage() {
        return getConfig().getString("messages.actionbar_reminder", "§cШтраф - обратитесь к судье");
    }

    public long getActionBarIntervalTicks() {
        int minutes = getConfig().getInt("actionbar_interval_minutes", 15);
        if (minutes < 1) minutes = 1;
        return minutes * 1200L; // 1 минута = 1200 тиков
    }

    public static Punishments getInstance() {
        return instance;
    }

    public static boolean isPunished(Player player) {
        List<String> punished = getInstance().getConfig().getStringList("punished_players");
        return punished.contains(player.getName());
    }

    public static void addPunished(Player player) {
        FileConfiguration config = getInstance().getConfig();
        List<String> punished = config.getStringList("punished_players");
        String name = player.getName();
        if (!punished.contains(name)) {
            punished.add(name);
            config.set("punished_players", punished);
            getInstance().saveConfig();
        }
    }

    public static void setPunishmentReason(Player player, String reason) {
        FileConfiguration config = getInstance().getConfig();
        config.set("punished_reasons." + player.getName(), reason);
        getInstance().saveConfig();
    }

    public static String getPunishmentReason(Player player) {
        FileConfiguration config = getInstance().getConfig();
        return config.getString("punished_reasons." + player.getName(), "Не указана");
    }

    public static void removePunishmentReason(Player player) {
        FileConfiguration config = getInstance().getConfig();
        config.set("punished_reasons." + player.getName(), null);
        getInstance().saveConfig();
    }

    public static void removePunished(Player player) {
        FileConfiguration config = getInstance().getConfig();
        List<String> punished = config.getStringList("punished_players");
        punished.remove(player.getName());
        config.set("punished_players", punished);
        getInstance().saveConfig();
    }
}
