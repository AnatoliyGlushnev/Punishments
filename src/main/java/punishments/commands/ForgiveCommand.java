package punishments.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import punishments.Punishments;

public class ForgiveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("снять_штраф")) {
            if (args.length != 1) {
                sender.sendMessage("§cИспользование: /снять_штраф [ник]");
                return true;
            }
            String playerName = args[0];
            java.util.List<String> punished = Punishments.getInstance().getConfig().getStringList("punished_players");
            if (!punished.contains(playerName)) {
                sender.sendMessage("§eНа игроке " + playerName + " нет штрафа.");
                return true;
            }
            Player target = Bukkit.getPlayer(playerName);
            if (target != null) {
                Punishments.removePunished(target);
                target.removePotionEffect(PotionEffectType.getByName("MINING_FATIGUE"));
                sender.sendMessage("§aШтраф снят с игрока " + target.getName() + ".");
                target.sendMessage("§aС вас снят эффект усталости!");
            } else {
                // оффлайн-игрок
                punished.remove(playerName);
                Punishments.getInstance().getConfig().set("punished_players", punished);
                Punishments.getInstance().getConfig().set("punished_reasons." + playerName, null);
                Punishments.getInstance().saveConfig();
                sender.sendMessage("§aШтраф снят с оффлайн-игрока " + playerName + ".");
            }
        }
        return true;
    }
}
