package punishments.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import punishments.Punishments;
// Снятие штрафа с игрока
public class ForgiveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("снять_штраф")) {
            if (args.length != 1) {
                sender.sendMessage("§cИспользование: /снять_штраф [ник]");
                return true;
            }
            String playerName = args[0];
            punishments.managers.PunishmentManager pm = new punishments.managers.PunishmentManager();
            java.util.List<String> punished = pm.getAllPunished();
            if (!punished.contains(playerName)) {
                sender.sendMessage("§eНа игроке " + playerName + " нет штрафа.");
                return true;
            }
            Player target = Bukkit.getPlayer(playerName);
            if (target != null) {
                pm.removePunished(target.getName());
                pm.removePunishmentReason(target.getName());
                target.removePotionEffect(PotionEffectType.getByName("MINING_FATIGUE"));
                // Удаление из punishedOnlinePlayers и остановка таймера
                punishments.listeners.FatigueListener.getInstance().removeOnlinePunished(target.getName());
                sender.sendMessage("§aШтраф снят с игрока " + target.getName() + ".");
                target.sendMessage("§a[ШТРАФ] С вас сняты эффекты. С вас снят штраф!");
            } else {
                pm.removePunished(playerName);
                pm.removePunishmentReason(playerName);
                sender.sendMessage("§aШтраф снят с оффлайн-игрока " + playerName + ".");
            }
        }
        return true;
    }
}
