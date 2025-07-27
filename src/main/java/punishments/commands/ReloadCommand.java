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
        sender.sendMessage("§aКонфиг Punishments перезагружен!");
        // Перезапуск ActionBar-напоминание
        List<String> punished = Punishments.getInstance().getConfig().getStringList("punished_players");
        for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            if (punished.contains(player.getName())) {
                Punishments.getInstance().startFatigueEnforcer(player);
            }
        }
        return true;
    }
}
