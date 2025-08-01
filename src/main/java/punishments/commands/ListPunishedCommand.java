package punishments.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import punishments.Punishments;
import java.util.List;
// /штрафы - список всех оштрафованных игроков
public class ListPunishedCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        punishments.managers.PunishmentManager pm = new punishments.managers.PunishmentManager();
        List<String> punished = pm.getAllPunished();
        if (punished.isEmpty()) {
            sender.sendMessage("§eСписок оштрафованных пуст.");
            return true;
        }
        sender.sendMessage("§6Штрафы:");
        for (String name : punished) {
            String reason = pm.getPunishmentReason(name);
            sender.sendMessage("§c- " + name + " §7(Причина: " + reason + ")");
        }
        return true;
    }
}
