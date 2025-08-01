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
import punishments.commands.GiveFatigueCommand;
import punishments.commands.ForgiveCommand;
import punishments.commands.ReloadCommand;
import punishments.commands.ListPunishedCommand;
import punishments.listeners.FatigueListener;

public class Punishments extends JavaPlugin {

    public String getJoinPunishedMessage() {
        return getConfig().getString("messages.join_punished", "§c[ШТРАФ] На вас наложен штраф! Для снятия обратитесь к судье.");
    }
    public String getEffectRestoredMessage() {
        return getConfig().getString("messages.effect_restored", "§c[ШТРАФ] Эффект усталости возвращён после входа!");
    }
    public String getEffectRestoredDeathMessage() {
        return getConfig().getString("messages.effect_restored_death", "§c[ШТРАФ] Эффект усталости возвращён после смерти!");
    }
    private static Punishments instance;

    private punishments.helpers.ActionBarReminder actionBarReminder;

    @Override
    public void onEnable() {
        instance = this;
        System.out.println("[Punishments][DEBUG] onEnable called, registering listener");
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new FatigueListener(this), this);
        getCommand("штраф").setExecutor(new GiveFatigueCommand());
        getCommand("снять_штраф").setExecutor(new ForgiveCommand());
        getCommand("punishmentsreload").setExecutor(new ReloadCommand());
        getCommand("штрафы").setExecutor(new ListPunishedCommand());


        boolean abEnabled = getConfig().getBoolean("actionbar_reminder.enabled", true);
        String abMessage = getConfig().getString("actionbar_reminder.message", "§cУ вас штраф, обратитесь к судье");
        int abMinutes = getConfig().getInt("actionbar_reminder.interval_minutes", 30);
        long abTicks = abMinutes * 60L * 20L;
        if (abEnabled) {
            actionBarReminder = new punishments.helpers.ActionBarReminder(
                this,
                new punishments.managers.PunishmentManager(),
                abMessage,
                abTicks
            );
            actionBarReminder.start();
            getLogger().info("[Punishments] ActionBarReminder enabled: interval=" + abMinutes + "min");
        }


        getLogger().info("Punishments plugin enabled!");
    }

    public static Punishments getInstance() {
        return instance;
    }

public void reloadActionBarReminder() {
    if (actionBarReminder != null) {
        actionBarReminder.stop();
        actionBarReminder = null;
    }
    boolean abEnabled = getConfig().getBoolean("actionbar_reminder.enabled", true);
    String abMessage = getConfig().getString("actionbar_reminder.message", "§cУ вас штраф, обратитесь к судье");
    int abMinutes = getConfig().getInt("actionbar_reminder.interval_minutes", 30);
    long abTicks = abMinutes * 60L * 20L;
    getLogger().info("[Punishments][DEBUG] reloadActionBarReminder: interval_minutes=" + abMinutes + ", abTicks=" + abTicks);
    if (abEnabled) {
        actionBarReminder = new punishments.helpers.ActionBarReminder(
            this,
            new punishments.managers.PunishmentManager(),
            abMessage,
            abTicks
        );
        actionBarReminder.start();
        getLogger().info("[Punishments] ActionBarReminder reloaded: interval=" + abMinutes + "min");
    }
}
}
