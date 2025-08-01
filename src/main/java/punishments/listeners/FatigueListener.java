package punishments.listeners;

import org.bukkit.plugin.Plugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Player;
import punishments.Punishments;


public class FatigueListener implements Listener {
    private static FatigueListener instance;
    public static FatigueListener getInstance() { return instance; }
    public void removeOnlinePunished(String playerName) {
        punishedOnlinePlayers.remove(playerName);
        if (punishedOnlinePlayers.isEmpty() && fatigueRestoreTask != null) {
            fatigueRestoreTask.cancel();
            fatigueRestoreTask = null;
        }
    }
    private final java.util.Set<String> punishedOnlinePlayers = java.util.Collections.synchronizedSet(new java.util.HashSet<>());
    private long checkIntervalSeconds;
    private int fatigueEffectLevel;
    private final org.bukkit.plugin.Plugin plugin;
    private volatile io.papermc.paper.threadedregions.scheduler.ScheduledTask fatigueRestoreTask = null;


    public FatigueListener(org.bukkit.plugin.Plugin plugin) {
        instance = this;
        this.plugin = plugin;
        this.checkIntervalSeconds = Punishments.getInstance().getConfig().getInt("fatigue.interval_seconds", 10);
        if (checkIntervalSeconds < 1) checkIntervalSeconds = 1;
        this.fatigueEffectLevel = Punishments.getInstance().getConfig().getInt("fatigue.effect_level", 4);
        if (fatigueEffectLevel < 0) fatigueEffectLevel = 0;
        if (fatigueEffectLevel > 4) fatigueEffectLevel = 4;

        startGlobalFatigueRestoreTimer();
    }

    private void startGlobalFatigueRestoreTimer() {
    if (fatigueRestoreTask != null && !fatigueRestoreTask.isCancelled()) return;
    plugin.getLogger().info("[Punishments][DEBUG] Глобальный таймер восстановления Mining Fatigue ЗАПУЩЕН");
    fatigueRestoreTask = org.bukkit.Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task -> {
        synchronized (punishedOnlinePlayers) {
            if (punishedOnlinePlayers.isEmpty()) {
                plugin.getLogger().info("[Punishments][DEBUG] Глобальный таймер восстановления Mining Fatigue ОСТАНОВЛЕН (нет наказанных онлайн)");
                task.cancel();
                fatigueRestoreTask = null;
                return;
            }
            for (String name : punishedOnlinePlayers) {
                Player player = org.bukkit.Bukkit.getPlayerExact(name);
                if (player != null && player.isOnline() && player.getPotionEffect(org.bukkit.potion.PotionEffectType.getByName("MINING_FATIGUE")) == null) {
                    player.getScheduler().run(Punishments.getInstance(), scheduledTask -> {
    player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.getByName("MINING_FATIGUE"), Integer.MAX_VALUE, 4, false, false, true));
    player.sendMessage(Punishments.getInstance().getEffectRestoredDeathMessage());
    plugin.getLogger().info("[Punishments][DEBUG] Таймер: Fatigue восстановлен для " + player.getName());
}, () -> {});
                }
            }
        }
    }, 0L, checkIntervalSeconds, java.util.concurrent.TimeUnit.SECONDS); // 0L первый запуск, далее по checkIntervalSeconds из конфига
}
    private final java.util.Set<String> awaitingRespawn = new java.util.HashSet<>();
    @EventHandler
    public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent event) {
        Player player = event.getEntity();
        punishments.managers.PunishmentManager pm = new punishments.managers.PunishmentManager();
        if (pm.isPunished(player.getName())) {
            awaitingRespawn.add(player.getName());
            player.getServer().getLogger().info("[Punishments][DEBUG] PlayerDeathEvent: " + player.getName() + " ожидает восстановления эффекта после respawn");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        punishedOnlinePlayers.remove(event.getPlayer().getName());
        // штраф-онлайн нет в онлайне (вообще никого нет из штрафа) — остановить таймеры
        if (punishedOnlinePlayers.isEmpty()) {
            if (fatigueRestoreTask != null) {
                fatigueRestoreTask.cancel();
                fatigueRestoreTask = null;
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        punishments.managers.PunishmentManager pm = new punishments.managers.PunishmentManager();
        if (pm.isPunished(player.getName())) {
            punishedOnlinePlayers.add(player.getName());
            if (punishedOnlinePlayers.size() == 1) {
                startGlobalFatigueRestoreTimer();
            }
            player.getScheduler().runDelayed(punishments.Punishments.getInstance(), task -> {
                punishments.helpers.FatigueEffectRestorer.restoreFatigueIfNeeded(player);
            }, () -> {}, 20L);
            punishments.helpers.PunishedPlayerMessenger.sendPunishedJoinMessage(player);
        }
 
        if (awaitingRespawn.remove(player.getName())) {
            player.getServer().getLogger().info("[Punishments][DEBUG] Detected respawn for " + player.getName() + ", но глобальный таймер сам восстановит эффект, если потребуется.");
        }
    }

@EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        punishments.managers.PunishmentManager pm = new punishments.managers.PunishmentManager();
        if (pm.isPunished(player.getName())
                && event.getItem() != null
                && event.getItem().getType().toString().equals("MILK_BUCKET")) {
            event.setCancelled(true);
            player.sendMessage("§c[ШТРАФ] Вы не можете пить молоко, пока на вас штраф!");
        }
    }
}
