package arg.uottawa.arg.events;

import arg.uottawa.arg.ARG;
import arg.uottawa.arg.commands.CommandManager;
import arg.uottawa.arg.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class EventManager implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {

        // Right click event for portal selection
        if (CommandManager.inCommand) {

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.END_PORTAL_FRAME) {
                if (event.getItem() != null) {
                    // Checking if right clicked with argstick
                    if (Objects.equals(event.getItem().getItemMeta(), ItemManager.createStick().getItemMeta())) {

                        Player p = event.getPlayer();

                        // Selection process
                        if (CommandManager.frameCount <= 12) {
                            p.sendMessage(ChatColor.AQUA + "" + (CommandManager.frameCount + 1) + ChatColor.YELLOW + " frame(s) selected.");

                            // Saves each position of frame block
                            ARG.portal[CommandManager.frameCount] = new int[] {
                                    event.getClickedBlock().getLocation().getBlockX(),
                                    event.getClickedBlock().getLocation().getBlockY(),
                                    event.getClickedBlock().getLocation().getBlockZ()
                            };
                            CommandManager.frameCount++;

                            // Allows save only when 12 frames have been selected
                            if (CommandManager.frameCount == 12) {
                                CommandManager.saveReady = true;
                                p.sendMessage(ChatColor.YELLOW + "Frame selected! Use " + ChatColor.AQUA + "/argstick set " + ChatColor.YELLOW + "to save the positions.");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "Frame already selected!");
                        }

                    }
                }
            }
        }

    }

    @EventHandler
    public void onKeyFoundEvent(KeyFoundEvent event) {

        // If a key has been found, then update the portal
        for (int i = 0; i < event.getEventNum() - 1; i++) {
            Block b = Bukkit.getWorlds().get(0).getBlockAt(ARG.portal[i][0], ARG.portal[i][1], ARG.portal[i][2]);
            EndPortalFrame f = (EndPortalFrame) b.getBlockData();
            f.setEye(true);
            b.setBlockData(f);
        }
    }

}
