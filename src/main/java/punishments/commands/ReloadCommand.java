package punishments.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import punishments.Punishments;
import java.util.List;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Punishments.getInstance().reloadConfig();
        Punishments.getInstance().reloadActionBarReminder();
        sender.sendMessage("§aКонфиг Punishments перезагружен!");

        punishments.managers.PunishmentManager pm = new punishments.managers.PunishmentManager();
        List<String> punished = pm.getAllPunished();
        for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
    if (punished.contains(player.getName())) {
        int level = Punishments.getInstance().getConfig().getInt("fatigue.effect_level", 4);
        if (level < 0) level = 0;
        if (level > 4) level = 4;
        player.removePotionEffect(org.bukkit.potion.PotionEffectType.getByName("MINING_FATIGUE"));
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(
            org.bukkit.potion.PotionEffectType.getByName("MINING_FATIGUE"),
            Integer.MAX_VALUE, level, false, false, true));
    }
}
        return true;
    }
}
