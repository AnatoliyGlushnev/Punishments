package punishments.helpers;

import org.bukkit.entity.Player;
import punishments.Punishments;
import punishments.managers.PunishmentManager;

public class PunishedPlayerMessenger {
    //Отправляет игроку сообщение о штрафе с подстановкой причины
    public static void sendPunishedJoinMessage(Player player) {
        String joinMsg = Punishments.getInstance().getJoinPunishedMessage();
        PunishmentManager pm = new PunishmentManager();
        String reason = pm.getPunishmentReason(player.getName());
        joinMsg = joinMsg.replace("{reason}", reason);
        player.sendMessage(joinMsg);
    }
}
