package arg.uottawa.arg.advancements;

import arg.uottawa.arg.ARG;
import arg.uottawa.arg.items.ItemManager;
import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.UUID;

public class FallAdvancement extends BaseAdvancement {

    private ArrayList<UUID> hasRespawned = new ArrayList<UUID>();
    public FallAdvancement(String key, AdvancementDisplay display, Advancement parent) {
        super(key, display, parent);

        registerEvent(EntityDamageEvent.class, e-> {
            if (e.getEntity() instanceof Player && ARG.currentEvent == 7) {
                Player p = (Player) e.getEntity();
                if (isVisible(p) && e.getCause() == EntityDamageEvent.DamageCause.FALL && e.getFinalDamage() >= 20 ) {
                    grant(p);
                }
            }
        });
    }

    public boolean hasRespawned(Player p) {
        return this.hasRespawned.contains(p.getUniqueId());
    }

    public void addRespawned(Player p) {
        this.hasRespawned.add(p.getUniqueId());
    }

}
