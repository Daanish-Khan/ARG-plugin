package arg.uottawa.arg.events;

import arg.uottawa.arg.ARG;
import arg.uottawa.arg.commands.CommandManager;
import arg.uottawa.arg.items.ItemManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.Objects;

public class EventManager implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {

        // Right click event for portal selection
        if (CommandManager.inCommand[0]) {

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.END_PORTAL_FRAME) {
                if (event.getItem() != null) {
                    // Checking if right clicked with argstick
                    if (Objects.equals(event.getItem().getItemMeta(), ItemManager.createStick().getItemMeta())) {

                        Player p = event.getPlayer();

                        // Selection process
                        if (CommandManager.frameCount <= 12) {
                            p.sendMessage(ChatColor.AQUA + "" + (CommandManager.frameCount + 1) + ChatColor.YELLOW + " frame(s) selected.");

                            // Saves each position of frame block
                            ARG.portal[CommandManager.frameCount] = new int[]{
                                    event.getClickedBlock().getLocation().getBlockX(),
                                    event.getClickedBlock().getLocation().getBlockY(),
                                    event.getClickedBlock().getLocation().getBlockZ()
                            };
                            CommandManager.frameCount++;

                            // Allows save only when 12 frames have been selected
                            if (CommandManager.frameCount == 12) {
                                CommandManager.saveReady = true;
                                p.sendMessage(ChatColor.YELLOW + "Frame selected! Use " + ChatColor.AQUA + "/argstick portalSelect set " + ChatColor.YELLOW + "to save the positions.");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "Frame already selected!");
                        }

                    }
                }
            }
        } else if (CommandManager.inCommand[1]) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.CHEST) {
                if (event.getItem() != null) {
                    // Checking if right clicked with argstick
                    if (Objects.equals(event.getItem().getItemMeta(), ItemManager.createStick().getItemMeta())) {
                        ARG.chest = new int[] {
                                event.getClickedBlock().getLocation().getBlockX(),
                                event.getClickedBlock().getLocation().getBlockY(),
                                event.getClickedBlock().getLocation().getBlockZ()
                        };

                        String chestStr = "";

                        // Create location sting for config using | as xyz seperator and
                        for (int i = 0; i < ARG.chest.length; i++) {
                            chestStr = chestStr + ARG.chest[i];
                            if (i != 2) {
                                chestStr = chestStr + "|";
                            }
                        }

                        // Save config
                        Bukkit.getServer().getPluginManager().getPlugin("ARG").getConfig().set("chest", chestStr);
                        Bukkit.getServer().getPluginManager().getPlugin("ARG").saveConfig();

                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Chest location saved!");
                        event.getPlayer().getInventory().removeItem(ItemManager.createStick());
                        CommandManager.inCommand[1] = false;
                    }
                }
            }
        }

    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (Objects.equals(event.getItem().getItemMeta(), ItemManager.createStick().getItemMeta())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getClickedInventory() != null) {
            if (event.getClickedInventory().getHolder() instanceof Chest) {
                if (event.getClickedInventory().getLocation().equals(new Location(Bukkit.getWorlds().get(0), ARG.chest[0], ARG.chest[1], ARG.chest[2]))) {
                    if (event.getCurrentItem().equals(ItemManager.createDisc(ARG.currentEvent))) {
                        event.getWhoClicked().getInventory().addItem(ItemManager.createDisc(ARG.currentEvent));
                        event.setCancelled(true);
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

        // Update audio diary
        if (event.getEventNum() != 8) {
            Chest c = (Chest) Bukkit.getWorlds().get(0).getBlockAt(ARG.chest[0], ARG.chest[1], ARG.chest[2]).getState();
            c.getBlockInventory().clear();

            c.getBlockInventory().setItem(13, ItemManager.createDisc(ARG.currentEvent));

        }
    }

    @EventHandler
    public void onPlayerEnterPortalEvent(PlayerPortalEvent event) {
        if (!event.getTo().getWorld().getName().contains("nether") && !event.getFrom().getWorld().getName().contains("nether") && !event.getFrom().getWorld().getName().contains("binder")) {
            event.setTo(new Location(Bukkit.getWorld("em_binder_of_worlds"), 43, 65, 0));
        }
    }

    @EventHandler
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        if (event.getPlayer().getWorld().getName().contains("binder")) {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.MUSIC_DRAGON, (float) 0.4, 1);
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (event.getPlayer().getWorld().getName().contains("binder")) {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.MUSIC_DRAGON, (float) 0.3, 1);
        }
    }

    @EventHandler
    public void onEntitySpawnEvent(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.ENDER_SIGNAL && Bukkit.getWorlds().get(0).getName().equals(event.getLocation().getWorld().getName())) {
            ((EnderSignal) event.getEntity()).setTargetLocation(new Location(event.getLocation().getWorld(), ARG.portal[0][0], ARG.portal[0][1], ARG.portal[0][2]));
        }
    }

    @EventHandler void onPortalCreateEvent(PortalCreateEvent event) {
        if (event.getReason().equals(PortalCreateEvent.CreateReason.END_PLATFORM)) {
            event.setCancelled(true);
        }
    }

}
