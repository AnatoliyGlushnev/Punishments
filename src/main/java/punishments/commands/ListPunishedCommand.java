package punishments.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import punishments.Punishments;
import java.util.List;

public class ListPunishedCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<String> punished = Punishments.getInstance().getConfig().getStringList("punished_players");
        if (punished.isEmpty()) {
            sender.sendMessage("§eСписок оштрафованных пуст.");
            return true;
        }
        sender.sendMessage("§6Штрафы:");
        for (String name : punished) {
            String reason = Punishments.getInstance().getConfig().getString("punished_reasons." + name, "Не указана");
            sender.sendMessage("§c- " + name + " §7(Причина: " + reason + ")");
        }
        return true;
    }
}
