package punishments.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import punishments.Punishments;

public class GiveFatigueCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§cИспользование: /штраф [ник] [причина]");
            return true;
        }
        String playerName = args[0];
        String reason = args.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)) : "Не указана";
        Player target = Bukkit.getPlayer(playerName);
        if (target != null) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.getByName("MINING_FATIGUE"), Integer.MAX_VALUE, 4, false, false, true));
            Punishments.addPunished(target);
            Punishments.setPunishmentReason(target, reason);
            target.sendMessage("§cВам выдан эффект усталости! Причина: " + reason);
            sender.sendMessage("§aШтраф выдан игроку " + target.getName() + ". Причина: " + reason);
            Punishments.getInstance().startFatigueEnforcer(target);
            return true;
        } else {
            // оффлайн-игрок
            java.util.List<String> punished = Punishments.getInstance().getConfig().getStringList("punished_players");
            if (!punished.contains(playerName)) {
                punished.add(playerName);
                Punishments.getInstance().getConfig().set("punished_players", punished);
            }
            Punishments.getInstance().getConfig().set("punished_reasons." + playerName, reason);
            Punishments.getInstance().saveConfig();
            sender.sendMessage("§aШтраф выдан оффлайн-игроку " + playerName + ". Причина: " + reason);
            return true;
        }
    }
}
