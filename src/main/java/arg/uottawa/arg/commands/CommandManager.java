package arg.uottawa.arg.commands;

import arg.uottawa.arg.ARG;
import arg.uottawa.arg.items.ItemManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor {

    public static int frameCount = 0;
    public static boolean[] inCommand = new boolean[]{false, false};
    public static boolean saveReady = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Check if command was sent by player
        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;
        Location currentLoc = p.getLocation();

        if (command.getName().equalsIgnoreCase("startEvent")) {

            ARG.startEvent = true;


            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.playSound(pl.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1);
            }

            Bukkit.broadcastMessage(ChatColor.RED + "Fernyiges grows restless. As you struggle to solve the secrets " +
                "behind Fernyiges, the ender pearls break one by one...");
            for (int i = 0; i < ARG.currentEvent + 1; i++) {
                int finalI = i;

                Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("ARG"), new Runnable() {
                    @Override
                    public void run() {
                        if (finalI < ARG.currentEvent - 1) {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 0.5f, 1);
                            }
                            Block b = Bukkit.getWorlds().get(0).getBlockAt(ARG.portal[finalI][0], ARG.portal[finalI][1], ARG.portal[finalI][2]);
                            EndPortalFrame f = (EndPortalFrame) b.getBlockData();
                            p.spawnParticle(Particle.ITEM_CRACK, b.getLocation(), 100, new ItemStack(Material.ENDER_EYE));
                            p.spawnParticle(Particle.EXPLOSION_NORMAL, b.getLocation(), 50);
                            f.setEye(false);
                            b.setBlockData(f);
                        }

                        if (finalI == ARG.currentEvent) {
                            Bukkit.broadcastMessage(ChatColor.RED + "The power of the eyes seeps back into the portal," +
                                    " latent energy re-igniting the wormhole. Fernyiges awaits your doom...");
                            World w = Bukkit.getWorlds().get(0);
                            w.getBlockAt(-95, 56, -144).setType(Material.END_PORTAL);
                            w.getBlockAt(-95, 56, -143).setType(Material.END_PORTAL);
                            w.getBlockAt(-95, 56, -142).setType(Material.END_PORTAL);

                            w.getBlockAt(-96, 56, -144).setType(Material.END_PORTAL);
                            w.getBlockAt(-96, 56, -143).setType(Material.END_PORTAL);
                            w.getBlockAt(-96, 56, -142).setType(Material.END_PORTAL);

                            w.getBlockAt(-97, 56, -144).setType(Material.END_PORTAL);
                            w.getBlockAt(-97, 56, -143).setType(Material.END_PORTAL);
                            w.getBlockAt(-97, 56, -142).setType(Material.END_PORTAL);

                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.playSound(p.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 0.5f, 1);
                            }

                            Block chest = Bukkit.getWorlds().get(0).getBlockAt(currentLoc);
                            chest.setType(Material.CHEST);
                            Chest c = (Chest) chest.getState();
                            c.getBlockInventory().setItem(4, new ItemStack(Material.GOLDEN_APPLE, 64));

                            c.getBlockInventory().setItem(11, new ItemStack(Material.NETHERITE_HELMET));
                            c.getBlockInventory().setItem(12, new ItemStack(Material.NETHERITE_CHESTPLATE));
                            c.getBlockInventory().setItem(13, new ItemStack(Material.NETHERITE_LEGGINGS));
                            c.getBlockInventory().setItem(14, new ItemStack(Material.NETHERITE_BOOTS));
                            c.getBlockInventory().setItem(15, new ItemStack(Material.NETHERITE_SWORD));

                            ARG.chestLoc = chest.getLocation();
                            ItemStack item = new ItemStack(Material.BOW);
                            ItemMeta meta = item.getItemMeta();

                            meta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
                            item.setItemMeta(meta);

                            c.getBlockInventory().setItem(21, item);
                            c.getBlockInventory().setItem(22, new ItemStack(Material.ARROW));
                            c.getBlockInventory().setItem(23, new ItemStack(Material.SHIELD));

                        }
                    }

                }, (4 + i) * 20L);
            }




        }


        if (command.getName().equalsIgnoreCase("argstick")) {

            if (args.length == 0) {
                p.sendMessage(ChatColor.RED + "Incorrect usage!");
                return true;
            }

            // Start portal selection
            if (args[0].equalsIgnoreCase("portalSelect")) {

                if (args.length == 1) {
                    // Check if user was already in selection process
                    if (!inCommand[0] && !inCommand[1]) {
                        p.getInventory().addItem(ItemManager.createStick());
                        p.sendMessage(
                                ChatColor.YELLOW + "Right-click each portal frame counterclockwise. Use " + ChatColor.AQUA +
                                        "/argstick portalSelect cancel" + ChatColor.YELLOW + " to cancel, and " + ChatColor.AQUA +
                                        "/argstick portalSelect set " + ChatColor.YELLOW + "to save the selection."
                        );
                        frameCount = 0;
                        inCommand[0] = true;
                        saveReady = false;
                        ARG.portal = new int[12][3];
                    } else {
                        p.sendMessage(ChatColor.RED + "You are already in the selection process!");
                    }
                } else if (args[1].equalsIgnoreCase("cancel")) { // Resets selection process
                    if (inCommand[0]) {
                        inCommand[0] = false;
                        frameCount = 0;
                        saveReady = false;
                        ARG.portal = new int[12][3];
                        p.sendMessage(ChatColor.YELLOW + "Selection canceled.");
                        p.getInventory().remove(ItemManager.createStick());
                    } else {
                        p.sendMessage(ChatColor.RED + "There is no ongoing selection to cancel!");
                    }
                } else if (args[1].equalsIgnoreCase("set")) { // Saves portal selection
                    if (inCommand[0]) {
                        if (saveReady) {

                            String portalStr = "";

                            // Create location sting for config using | as xyz seperator and , as block seperator
                            for (int i = 0; i < ARG.portal.length; i++) {
                                for (int j = 0; j < ARG.portal[i].length; j++) {
                                    portalStr = portalStr + ARG.portal[i][j];
                                    if (j != 2) {
                                        portalStr = portalStr + "|";
                                    }
                                }
                                if (i != ARG.portal.length - 1) {
                                    portalStr = portalStr + ",";
                                }
                            }

                            // Save config
                            Bukkit.getServer().getPluginManager().getPlugin("ARG").getConfig().set("portal", portalStr);
                            Bukkit.getServer().getPluginManager().getPlugin("ARG").saveConfig();

                            inCommand[0] = false;
                            frameCount = 0;
                            saveReady = false;
                            p.getInventory().remove(ItemManager.createStick());
                            p.sendMessage(ChatColor.YELLOW + "Portal set!");
                        } else {
                            p.sendMessage(ChatColor.RED + "You have not selected every frame!");
                            p.sendMessage(ChatColor.RED + "You have " + ChatColor.YELLOW + (12 - frameCount) + ChatColor.RED + " frames left to select.");
                        }

                    } else {
                        p.sendMessage(ChatColor.RED + "You are not currently in selection mode!");
                        p.sendMessage(ChatColor.RED + "Use /argstick portalSelect to start selection.");
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "Incorrect usage.");
                }

            } else if (args[0].equalsIgnoreCase("chestSelect")) {

                if (args.length == 1) {

                    if (!inCommand[0] && !inCommand[1]) {
                        inCommand[1] = true;
                        p.getInventory().addItem(ItemManager.createStick());
                        p.sendMessage(ChatColor.YELLOW + "Right click the chest the discs will go in.");
                    } else {
                        p.sendMessage(ChatColor.RED + "You are in the middle of a command!");
                    }

                } else if (args[1].equalsIgnoreCase("cancel")) {
                    inCommand[1] = false;
                    p.getInventory().remove(ItemManager.createStick());
                    p.sendMessage(ChatColor.YELLOW + "Chest selection cancelled!");
                }

            } else {
                p.sendMessage(ChatColor.RED + "Incorrect Usage!");
            }

        }

        return true;
    }
}
