package arg.uottawa.arg.commands;

import arg.uottawa.arg.ARG;
import arg.uottawa.arg.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        if (command.getName().equalsIgnoreCase("startEvent")) {

            if (ARG.currentEvent == 13) {
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
