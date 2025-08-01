package punishments.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import punishments.Punishments;
import java.util.List;

public class PunishmentManager {
    private final FileConfiguration config;

    public PunishmentManager() {
        this.config = Punishments.getInstance().getConfig();
    }

    public boolean isPunished(String playerName) {
        List<String> punished = config.getStringList("punished_players");
        return punished.contains(playerName);
    }

    public void addPunished(String playerName) {
        List<String> punished = config.getStringList("punished_players");
        if (!punished.contains(playerName)) {
            punished.add(playerName);
            config.set("punished_players", punished);
            Punishments.getInstance().saveConfig();
        }
    }

    public void removePunished(String playerName) {
        List<String> punished = config.getStringList("punished_players");
        punished.remove(playerName);
        config.set("punished_players", punished);
        Punishments.getInstance().saveConfig();
    }

    public void setPunishmentReason(String playerName, String reason) {
        config.set("punished_reasons." + playerName, reason);
        Punishments.getInstance().saveConfig();
    }

    public String getPunishmentReason(String playerName) {
        return config.getString("punished_reasons." + playerName, "Не указана");
    }

    public void removePunishmentReason(String playerName) {
        config.set("punished_reasons." + playerName, null);
        Punishments.getInstance().saveConfig();
    }

    public List<String> getAllPunished() {
        return config.getStringList("punished_players");
    }
}
