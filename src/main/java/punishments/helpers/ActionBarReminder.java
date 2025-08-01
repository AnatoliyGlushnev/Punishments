package punishments.helpers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import punishments.managers.PunishmentManager;

public class ActionBarReminder {
    private final Plugin plugin;
    private final PunishmentManager punishmentManager;
    private final String message;
    private final long intervalTicks;
    private volatile io.papermc.paper.threadedregions.scheduler.ScheduledTask task;

    public ActionBarReminder(Plugin plugin, PunishmentManager punishmentManager, String message, long intervalTicks) {
        this.plugin = plugin;
        this.punishmentManager = punishmentManager;
        this.message = message;
        this.intervalTicks = intervalTicks;
    }

    public void start() {
        task = org.bukkit.Bukkit.getGlobalRegionScheduler().runAtFixedRate(
            plugin,
            scheduledTask -> {
                for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
                    if (punishmentManager.isPunished(player.getName())) {
                        player.sendActionBar(message);
                        plugin.getLogger().info("[Punishments][DEBUG] ActionBar sent to: " + player.getName());
                        // Play sound if configured
                        org.bukkit.configuration.file.FileConfiguration config = punishments.Punishments.getInstance().getConfig();
                        if (config.getConfigurationSection("sounds.reminder") != null) {
                            String soundType = config.getString("sounds.reminder.type", "BLOCK_NOTE_BLOCK_BELL");
                            float volume = (float) config.getDouble("sounds.reminder.volume", 1.0);
                            float pitch = (float) config.getDouble("sounds.reminder.pitch", 1.0);
                            try {
                                player.playSound(player.getLocation(), org.bukkit.Sound.valueOf(soundType), volume, pitch);
                            } catch (IllegalArgumentException e) {
                                plugin.getLogger().warning("[Punishments] Unknown sound type in config: " + soundType);
                            }
                        }
                    }
                }
            },
            1L,
            intervalTicks
        );
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
