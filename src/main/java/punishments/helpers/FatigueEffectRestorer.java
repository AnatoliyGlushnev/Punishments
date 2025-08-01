package punishments.helpers;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import punishments.Punishments;

public class FatigueEffectRestorer {
    public static void restoreFatigueIfNeeded(Player player) {
        if (player.isOnline() && player.getPotionEffect(PotionEffectType.getByName("MINING_FATIGUE")) == null) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.getByName("MINING_FATIGUE"), Integer.MAX_VALUE, 4, false, false, true));
        }
    }
}
