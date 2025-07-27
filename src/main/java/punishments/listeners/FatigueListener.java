package punishments.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Player;
import punishments.Punishments;

public class FatigueListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (Punishments.isPunished(player)) {
            Punishments.getInstance().startFatigueEnforcer(player);
            String joinMsg = Punishments.getInstance().getJoinPunishedMessage();
            String reason = Punishments.getInstance().getConfig().getString("punished_reasons." + player.getName(), "Не указана");
            joinMsg = joinMsg.replace("{reason}", reason);
            player.sendMessage(joinMsg);
        }
    }
}
