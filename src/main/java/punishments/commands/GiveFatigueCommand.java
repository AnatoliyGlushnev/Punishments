package punishments.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import punishments.Punishments;
// /штрраф [ник] + [причина](в противном случае будет не указана причина) - выдача штрафа логично
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
        punishments.managers.PunishmentManager pm = new punishments.managers.PunishmentManager();
        if (target != null) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.getByName("MINING_FATIGUE"), Integer.MAX_VALUE, 4, false, false, true));
            pm.addPunished(target.getName());
            pm.setPunishmentReason(target.getName(), reason);
            target.sendMessage("§c[ШТРАФ] Вам выдан эффект усталости! Причина: " + reason);
            sender.sendMessage("§aШтраф выдан игроку " + target.getName() + ". Причина: " + reason);
            return true;
        } else {
            pm.addPunished(playerName);
            pm.setPunishmentReason(playerName, reason);
            sender.sendMessage("§aШтраф выдан оффлайн-игроку " + playerName + ". Причина: " + reason);
            return true;
        }
    }
}
