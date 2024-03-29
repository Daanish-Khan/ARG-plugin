package arg.uottawa.arg.events;

import arg.uottawa.arg.ARG;
import arg.uottawa.arg.advancements.FallAdvancement;
import arg.uottawa.arg.commands.CommandManager;
import arg.uottawa.arg.items.ItemManager;
import com.fren_gor.ultimateAdvancementAPI.events.PlayerLoadingCompletedEvent;
import com.fren_gor.ultimateAdvancementAPI.util.AdvancementKey;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class EventManager implements Listener {


    private boolean state = false;
    private ArrayList<UUID> puzzle8 = new ArrayList<UUID>();
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
        } else {
            if (event.getClickedBlock() != null) {
                if (event.getClickedBlock().isBlockPowered() && event.getClickedBlock().getType() == Material.ENDER_CHEST && ARG.currentEvent == 8 && !puzzle8.contains(event.getPlayer().getUniqueId())) {
                    event.getPlayer().getEnderChest().addItem(ItemManager.createDisc(8));
                    puzzle8.add(event.getPlayer().getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getClickedInventory() != null) {
            if (event.getClickedInventory().getHolder() instanceof Chest) {
                if (event.getClickedInventory().getLocation().equals(new Location(Bukkit.getWorlds().get(0), ARG.chest[0], ARG.chest[1], ARG.chest[2]))) {
                    if (!event.getAction().toString().contains("PICKUP") && !event.getAction().toString().contains("SWAP")) {
                        event.setCancelled(true);
                    } else {
                        if (event.getCurrentItem().equals(ItemManager.createDisc(ARG.currentEvent))) {
                            event.getWhoClicked().getInventory().addItem(ItemManager.createDisc(ARG.currentEvent));
                            event.setCancelled(true);
                        } else if (event.getCurrentItem().equals(ItemManager.createPuzzle8Book())) {
                            event.getWhoClicked().getInventory().addItem(ItemManager.createPuzzle8Book());
                            event.setCancelled(true);
                        }
                    }
                } else if (event.getClickedInventory().getLocation().equals(ARG.chestLoc)) {
                    if (!event.getAction().toString().contains("PICKUP") && !event.getAction().toString().contains("SWAP")) {
                        event.setCancelled(true);
                    } else {
                        event.getWhoClicked().getInventory().addItem(event.getCurrentItem());
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
        Chest c = (Chest) Bukkit.getWorlds().get(0).getBlockAt(ARG.chest[0], ARG.chest[1], ARG.chest[2]).getState();
        c.getBlockInventory().clear();
        if (event.getEventNum() == 8) {
            c.getBlockInventory().setItem(13, ItemManager.createPuzzle8Book());
        } else {
            c.getBlockInventory().setItem(13, ItemManager.createDisc(ARG.currentEvent));
        }

        // ------------ ARG PUZZLE STUFF ----------------

        World w = Bukkit.getWorlds().get(0);

        // Reset each puzzle element

        // 1
        w.getBlockAt(ARG.chest[0], ARG.chest[1], ARG.chest[2] - 1).setType(Material.AIR);

        // 4
        w.getBlockAt(-366, 66, -107).setType(Material.AIR);
        w.getBlockAt(-728, 65, 909).setType(Material.AIR);
        w.getBlockAt(-231, 135, -819).setType(Material.AIR);

        // 6

        // V1
        w.getBlockAt(-204, 64, -379).setType(Material.AIR);
        w.getBlockAt(-204, 63, -379).setType(Material.AIR);

        w.getBlockAt(-204, 63, -378).setType(Material.AIR);

        w.getBlockAt(-202, 63, -379).setType(Material.AIR);
        w.getBlockAt(-201, 63, -379).setType(Material.AIR);
        w.getBlockAt(-200, 63, -379).setType(Material.AIR);

        w.getBlockAt(-194, 63, -379).setType(Material.AIR);
        w.getBlockAt(-193, 63, -379).setType(Material.AIR);
        w.getBlockAt(-192, 63, -379).setType(Material.AIR);

        w.getBlockAt(-192, 63, -378).setType(Material.AIR);

        // V2
        w.getBlockAt(94, 66, -639).setType(Material.AIR);
        w.getBlockAt(94, 65, -639).setType(Material.AIR);

        w.getBlockAt(94, 65, -640).setType(Material.AIR);

        w.getBlockAt(92, 65, -639).setType(Material.AIR);
        w.getBlockAt(91, 65, -639).setType(Material.AIR);
        w.getBlockAt(90, 65, -639).setType(Material.AIR);

        w.getBlockAt(83, 65, -639).setType(Material.AIR);
        w.getBlockAt(82, 65, -639).setType(Material.AIR);
        w.getBlockAt(81, 65, -639).setType(Material.AIR);

        // V3

        w.getBlockAt(-430, 78, -837).setType(Material.AIR);
        w.getBlockAt(-430, 77, -837).setType(Material.AIR);

        w.getBlockAt(-430, 77, -836).setType(Material.AIR);

        w.getBlockAt(-428, 77, -837).setType(Material.AIR);
        w.getBlockAt(-427, 77, -837).setType(Material.AIR);
        w.getBlockAt(-426, 77, -837).setType(Material.AIR);

        w.getBlockAt(-421, 77, -837).setType(Material.AIR);
        w.getBlockAt(-420, 77, -837).setType(Material.AIR);
        w.getBlockAt(-419, 77, -837).setType(Material.AIR);

        // v4
        w.getBlockAt(-155, 64, 230).setType(Material.AIR);
        w.getBlockAt(-155, 63, 230).setType(Material.AIR);

        w.getBlockAt(-154, 63, 230).setType(Material.AIR);

        w.getBlockAt(-155, 63, 228).setType(Material.AIR);
        w.getBlockAt(-155, 63, 227).setType(Material.AIR);
        w.getBlockAt(-155, 63, 226).setType(Material.AIR);

        w.getBlockAt(-155, 63, 222).setType(Material.AIR);
        w.getBlockAt(-155, 63, 221).setType(Material.AIR);

        switch(event.getEventNum()) {
            case 1:
                w.getBlockAt(ARG.chest[0], ARG.chest[1], ARG.chest[2] - 1).setType(Material.LECTERN);
                ((Lectern) w.getBlockAt(ARG.chest[0], ARG.chest[1], ARG.chest[2] - 1).getState())
                        .getInventory().setItem(0, ItemManager.createPuzzle1Book());
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                w.getBlockAt(-366, 66, -107).setType(Material.LECTERN);
                w.getBlockAt(-728, 65, 909).setType(Material.LECTERN);
                w.getBlockAt(-231, 135, -819).setType(Material.LECTERN);
                ((Lectern) w.getBlockAt(-366, 66, -107).getState()).getInventory()
                        .setItem(0, ItemManager.createPuzzle4Books(1));
                ((Lectern) w.getBlockAt(-728, 65, 909).getState()).getInventory()
                        .setItem(0, ItemManager.createPuzzle4Books(2));
                ((Lectern) w.getBlockAt(-231, 135, -819).getState()).getInventory()
                        .setItem(0, ItemManager.createPuzzle4Books(3));
                break;
            case 5:
                break;
            case 6:

                // Village 1
                w.getBlockAt(-204, 64, -379).setType(Material.OBSIDIAN);
                w.getBlockAt(-204, 63, -379).setType(Material.OBSIDIAN);

                w.spawn(new Location(w, -204, 64, -378), ItemFrame.class, itemFrame -> {
                    itemFrame.setItem(new ItemStack(Material.ENDER_EYE));
                });

                w.getBlockAt(-204, 63, -378).setType(Material.OAK_SIGN);
                Sign s = (Sign) w.getBlockAt(-204, 63, -378).getState();
                s.setLine(0, "1");
                s.setLine(1, "81 65 -640");
                s.setLine(3, ChatColor.RED + "You" + ChatColor.WHITE + "Tube");
                s.setGlowingText(true);
                s.setColor(DyeColor.BLACK);
                s.update();

                w.getBlockAt(-202, 63, -379).setType(Material.END_STONE);
                w.getBlockAt(-201, 63, -379).setType(Material.YELLOW_CONCRETE);
                w.getBlockAt(-200, 63, -379).setType(Material.END_STONE);

                w.getBlockAt(-194, 63, -379).setType(Material.NETHERRACK);
                w.getBlockAt(-193, 63, -379).setType(Material.YELLOW_CONCRETE);
                w.getBlockAt(-192, 63, -379).setType(Material.IRON_ORE);

                w.spawn(new Location(w, -202, 63, -378), ItemFrame.class, itemFrame -> {
                    itemFrame.setRotation(Rotation.CLOCKWISE_135);
                    itemFrame.setItem(new ItemStack(Material.POLISHED_ANDESITE_STAIRS));
                });

                w.spawn(new Location(w, -193, 63, -378), ItemFrame.class, itemFrame -> {
                    itemFrame.setRotation(Rotation.CLOCKWISE_135);
                    itemFrame.setItem(new ItemStack(Material.POLISHED_ANDESITE_STAIRS));
                });
                w.spawn(new Location(w, -192, 63, -378), ItemFrame.class, itemFrame -> {
                    itemFrame.setRotation(Rotation.CLOCKWISE_135);
                    itemFrame.setItem(new ItemStack(Material.POLISHED_ANDESITE_STAIRS));
                });

                // Village 2
                w.getBlockAt(94, 66, -639).setType(Material.OBSIDIAN);
                w.getBlockAt(94, 65, -639).setType(Material.OBSIDIAN);

                w.spawn(new Location(w, 94, 66, -640), ItemFrame.class, itemFrame -> {
                    itemFrame.setItem(new ItemStack(Material.ENDER_EYE));
                });

                w.getBlockAt(94, 65, -640).setType(Material.OAK_SIGN);

                Sign s2 = (Sign) w.getBlockAt(94, 65, -640).getState();
                s2.setLine(0, "2");
                s2.setLine(1, "-430 79 -837");
                s2.setLine(3, ChatColor.RED + "You" + ChatColor.WHITE + "Tube");
                s2.setGlowingText(true);
                s2.setColor(DyeColor.BLACK);
                Rotatable dir = (Rotatable) s2.getBlockData();
                dir.setRotation(BlockFace.NORTH);
                s2.setBlockData(dir);
                s2.update();

                w.getBlockAt(92, 65, -639).setType(Material.END_STONE);
                w.getBlockAt(91, 65, -639).setType(Material.YELLOW_CONCRETE);
                w.getBlockAt(90, 65, -639).setType(Material.END_STONE);

                w.getBlockAt(83, 65, -639).setType(Material.YELLOW_CONCRETE);
                w.getBlockAt(82, 65, -639).setType(Material.YELLOW_CONCRETE);
                w.getBlockAt(81, 65, -639).setType(Material.NETHERRACK);

                w.spawn(new Location(w, 92, 65, -640), ItemFrame.class, itemFrame -> {
                    itemFrame.setRotation(Rotation.CLOCKWISE_135);
                    itemFrame.setItem(new ItemStack(Material.POLISHED_ANDESITE_STAIRS));
                });

                w.spawn(new Location(w, 83, 65, -640), ItemFrame.class, itemFrame -> {
                    itemFrame.setRotation(Rotation.CLOCKWISE_135);
                    itemFrame.setItem(new ItemStack(Material.POLISHED_ANDESITE_STAIRS));
                });

                // Village 3
                w.getBlockAt(-430, 78, -837).setType(Material.OBSIDIAN);
                w.getBlockAt(-430, 77, -837).setType(Material.OBSIDIAN);

                w.spawn(new Location(w, -430, 78, -836), ItemFrame.class, itemFrame -> {
                    itemFrame.setItem(new ItemStack(Material.ENDER_EYE));
                });

                w.getBlockAt(-430, 77, -836).setType(Material.OAK_SIGN);
                Sign s3 = (Sign) w.getBlockAt(-430, 77, -836).getState();
                s3.setLine(0, "3");
                s3.setLine(1, "-155 65 230");
                s3.setLine(3, ChatColor.RED + "You" + ChatColor.WHITE + "Tube");
                s3.setGlowingText(true);
                s3.setColor(DyeColor.BLACK);
                s3.update();

                w.getBlockAt(-428, 77, -837).setType(Material.END_STONE);
                w.getBlockAt(-427, 77, -837).setType(Material.YELLOW_CONCRETE);
                w.getBlockAt(-426, 77, -837).setType(Material.END_STONE);

                w.getBlockAt(-421, 77, -837).setType(Material.POLISHED_ANDESITE);
                w.getBlockAt(-420, 77, -837).setType(Material.ZOMBIE_HEAD);
                w.getBlockAt(-419, 77, -837).setType(Material.NETHERRACK);

                w.spawn(new Location(w, -428, 77, -836), ItemFrame.class, itemFrame -> {
                    itemFrame.setRotation(Rotation.CLOCKWISE_135);
                    itemFrame.setItem(new ItemStack(Material.POLISHED_ANDESITE_STAIRS));
                });

                w.spawn(new Location(w, -421, 77, -836), ItemFrame.class, itemFrame -> {
                    itemFrame.setRotation(Rotation.CLOCKWISE_135);
                    itemFrame.setItem(new ItemStack(Material.POLISHED_ANDESITE_STAIRS));
                });

                w.spawn(new Location(w, -420, 77, -836), ItemFrame.class, itemFrame -> {
                    itemFrame.setRotation(Rotation.CLOCKWISE_135);
                    itemFrame.setItem(new ItemStack(Material.POLISHED_ANDESITE_STAIRS));
                });
                w.spawn(new Location(w, -419, 77, -836), ItemFrame.class, itemFrame -> {
                    itemFrame.setRotation(Rotation.CLOCKWISE_135);
                    itemFrame.setItem(new ItemStack(Material.POLISHED_ANDESITE_STAIRS));
                });

                // Village 4

                w.getBlockAt(-155, 64, 230).setType(Material.OBSIDIAN);
                w.getBlockAt(-155, 63, 230).setType(Material.OBSIDIAN);

                w.spawn(new Location(w, -154, 64, 230), ItemFrame.class, itemFrame -> {
                    itemFrame.setItem(new ItemStack(Material.ENDER_EYE));
                });

                w.getBlockAt(-154, 63, 230).setType(Material.OAK_SIGN);
                Sign s4 = (Sign) w.getBlockAt(-154, 63, 230).getState();
                s4.setLine(0, "4");
                s4.setLine(1, "-204 65 -375");
                s4.setLine(3, ChatColor.RED + "You" + ChatColor.WHITE + "Tube");
                s4.setGlowingText(true);
                s4.setColor(DyeColor.BLACK);
                Rotatable dir2 = (Rotatable) s4.getBlockData();
                dir2.setRotation(BlockFace.EAST);
                s4.setBlockData(dir2);
                s4.update();

                w.getBlockAt(-155, 63, 228).setType(Material.END_STONE);
                w.getBlockAt(-155, 63, 227).setType(Material.YELLOW_CONCRETE);
                w.getBlockAt(-155, 63, 226).setType(Material.END_STONE);

                w.getBlockAt(-155, 63, 222).setType(Material.REINFORCED_DEEPSLATE);
                w.getBlockAt(-155, 63, 221).setType(Material.YELLOW_CONCRETE);

                w.spawn(new Location(w, -154, 63, 228), ItemFrame.class, itemFrame -> {
                    itemFrame.setRotation(Rotation.CLOCKWISE_135);
                    itemFrame.setItem(new ItemStack(Material.POLISHED_ANDESITE_STAIRS));
                });

                w.spawn(new Location(w, -154, 63, 222), ItemFrame.class, itemFrame -> {
                    itemFrame.setRotation(Rotation.CLOCKWISE_135);
                    itemFrame.setItem(new ItemStack(Material.POLISHED_ANDESITE_STAIRS));
                });

                w.spawn(new Location(w, -154, 63, 221), ItemFrame.class, itemFrame -> {
                    itemFrame.setRotation(Rotation.CLOCKWISE_135);
                    itemFrame.setItem(new ItemStack(Material.POLISHED_ANDESITE_STAIRS));
                });

                break;
            case 7:
                ARG.advTab.getEventManager().register(ARG.advTab, PlayerLoadingCompletedEvent.class, e -> {
                    ARG.advTab.showTab(e.getPlayer());
                    ARG.advTab.grantRootAdvancement(e.getPlayer());
                });
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                ShapedRecipe leftEyeRecipe = new ShapedRecipe(new NamespacedKey(Bukkit.getPluginManager().getPlugin("ARG"), "lefteye"), ItemManager.createLeftEye());
                leftEyeRecipe.shape("XXX", "XXX", "XXX");
                leftEyeRecipe.setIngredient('X', new RecipeChoice.ExactChoice(ItemManager.createPuzzle10Drop()));
                Bukkit.addRecipe(leftEyeRecipe);
                break;
            case 11:
                ShapedRecipe rightEyeRecipe = new ShapedRecipe(new NamespacedKey(Bukkit.getPluginManager().getPlugin("ARG"), "righteye"), ItemManager.createRightEye());
                rightEyeRecipe.shape("BBB", "OGO", "BBB");
                rightEyeRecipe.setIngredient('B', Material.BLAZE_POWDER);
                rightEyeRecipe.setIngredient('O', Material.OBSIDIAN);
                rightEyeRecipe.setIngredient('G', Material.GLASS);
                Bukkit.addRecipe(rightEyeRecipe);

                ShapedRecipe headRecipe = new ShapedRecipe(new NamespacedKey(Bukkit.getPluginManager().getPlugin("ARG"), "head"), ItemManager.createDragonHead());
                headRecipe.shape("OOO", "LOR", "OOO");
                headRecipe.setIngredient('O', Material.OBSIDIAN);
                headRecipe.setIngredient('L', new RecipeChoice.ExactChoice(ItemManager.createLeftEye()));
                headRecipe.setIngredient('R', new RecipeChoice.ExactChoice(ItemManager.createRightEye()));
                Bukkit.addRecipe(headRecipe);

                w.spawn(new Location(w, 592, 66, -706), GlowItemFrame.class, itemFrame -> {
                    itemFrame.setItem(new ItemStack(Material.OBSIDIAN));
                });
                w.spawn(new Location(w, 591, 66, -706), GlowItemFrame.class, itemFrame -> {
                    itemFrame.setItem(new ItemStack(Material.OBSIDIAN));
                });
                w.spawn(new Location(w, 590, 66, -706), GlowItemFrame.class, itemFrame -> {
                    itemFrame.setItem(new ItemStack(Material.OBSIDIAN));
                });

                w.spawn(new Location(w, 592, 65, -706), GlowItemFrame.class, itemFrame -> {
                    itemFrame.setItem(new ItemStack(Material.ENDER_EYE));
                });
                w.spawn(new Location(w, 591, 65, -706), GlowItemFrame.class, itemFrame -> {
                    itemFrame.setItem(new ItemStack(Material.OBSIDIAN));
                });
                w.spawn(new Location(w, 590, 65, -706), GlowItemFrame.class, itemFrame -> {
                    itemFrame.setItem(new ItemStack(Material.ENDER_EYE));
                });

                w.spawn(new Location(w, 592, 64, -706), GlowItemFrame.class, itemFrame -> {
                    itemFrame.setItem(new ItemStack(Material.OBSIDIAN));
                });
                w.spawn(new Location(w, 591, 64, -706), GlowItemFrame.class, itemFrame -> {
                    itemFrame.setItem(new ItemStack(Material.OBSIDIAN));
                });
                w.spawn(new Location(w, 590, 64, -706), GlowItemFrame.class, itemFrame -> {
                    itemFrame.setItem(new ItemStack(Material.OBSIDIAN));
                });
                break;
            case 12:
                ShapedRecipe QRRecipe = new ShapedRecipe(new NamespacedKey(Bukkit.getPluginManager().getPlugin("ARG"), "QR"), ItemManager.createMap());
                QRRecipe.shape("LR");
                QRRecipe.setIngredient('L', new RecipeChoice.ExactChoice(ItemManager.createWitherDrop()));
                QRRecipe.setIngredient('R', new RecipeChoice.ExactChoice(ItemManager.createWardenDrop()));
                Bukkit.addRecipe(QRRecipe);

                break;
        }
    }

    @EventHandler
    public void onPlayerEnterPortalEvent(PlayerPortalEvent event) {

        if (!event.getTo().getWorld().getName().contains("nether") && !event.getFrom().getWorld().getName().contains("nether") && !event.getFrom().getWorld().getName().contains("binder")) {
            if (!ARG.startEvent) {
                if (ARG.currentEvent != 13) {
                    event.getPlayer().sendMessage(ChatColor.RED + "Due to a rogue wormhole, you have been cast out of His dimension...");
                    event.setTo(Bukkit.getWorlds().get(0).getSpawnLocation());
                } else {
                    event.setTo(new Location(Bukkit.getWorld("em_binder_of_worlds"), 43, 65, 0));
                }
            } else {
                event.setTo(new Location(Bukkit.getWorld("em_binder_of_worlds"), 43, 65, 0));
            }
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

    @EventHandler
    public void onPrepareItemEnchantEvent (PrepareItemEnchantEvent event) {

            if (event.getItem().getType() == Material.ENDER_EYE && event.getItem().getEnchantments().size() == 0 && ARG.currentEvent == 5) {
                event.setCancelled(false);
                event.getOffers()[0] = new EnchantmentOffer(Enchantment.PROTECTION_ENVIRONMENTAL, 1, 2);
                event.getOffers()[1] = new EnchantmentOffer(Enchantment.LUCK, 6, 5);
                event.getOffers()[2] = new EnchantmentOffer(Enchantment.DAMAGE_UNDEAD, 4, 6);

                if (event.getInventory().getContents()[1] != null) {
                    if (event.getInventory().getContents()[1].getType() == Material.LAPIS_LAZULI) {
                        event.getInventory().getContents()[1].setAmount(event.getInventory().getContents()[1].getAmount() - 1);
                        event.getItem().addUnsafeEnchantment(Enchantment.LUCK, 1);

                        ItemMeta meta = event.getItem().getItemMeta();
                        List<String> lore = new ArrayList<>();
                        lore.add("4c4f4f4b20464f522054484520455945");
                        lore.add("");
                        lore.add("01010101 01000011 01010101"); // UCU
                        lore.add("01010100 01000101 01010010");
                        lore.add("01001101 01001001 01001110");
                        lore.add("01010101 01010011"); // TERMINUS

                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        meta.setLore(lore);
                        meta.setDisplayName("███A███E█████S████");
                        event.getItem().setItemMeta(meta);

                        event.getEnchanter().playSound(event.getEnchanter().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
                        event.getEnchanter().sendMessage(ChatColor.LIGHT_PURPLE + "The enchantment table reacts with the Eye...");
                    }
                }

            }

    }

    @EventHandler
    public void onRespawnEvent(PlayerRespawnEvent event) {
        if (ARG.currentEvent == 7) {
            if (ARG.advTab.getAdvancement(AdvancementKey.fromString("argadvancement:fall")).isGranted(event.getPlayer())) {
                if (!((FallAdvancement) ARG.advTab.getAdvancement(AdvancementKey.fromString("argadvancement:fall"))).hasRespawned(event.getPlayer())) {
                    event.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "You find a mysterious book where you fell...");
                    event.getPlayer().playSound(event.getRespawnLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2, 1);
                    event.getPlayer().getInventory().addItem(ItemManager.createPuzzle7Book());
                    ((FallAdvancement) ARG.advTab.getAdvancement(AdvancementKey.fromString("argadvancement:fall"))).addRespawned(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onMobDeathEvent(EntityDeathEvent event) {

        // Pain
        if ((event.getEntity() instanceof Blaze
            || event.getEntity() instanceof Creeper
            || event.getEntity() instanceof Drowned
            || event.getEntity() instanceof Husk
            || event.getEntity() instanceof MagmaCube
            || event.getEntity() instanceof Phantom
            || event.getEntity() instanceof PiglinAbstract
            || event.getEntity() instanceof Pillager
            || event.getEntity() instanceof Ravager
            || event.getEntity() instanceof Skeleton
            || event.getEntity() instanceof Slime
            || event.getEntity() instanceof Spider
            || event.getEntity() instanceof CaveSpider
            || event.getEntity() instanceof Stray
            || event.getEntity() instanceof Vex
            || event.getEntity() instanceof Vindicator
            || event.getEntity() instanceof Witch
            || event.getEntity() instanceof WitherSkeleton
            || event.getEntity() instanceof Zoglin
            || event.getEntity() instanceof Zombie
            || event.getEntity() instanceof ZombieVillager) && ARG.currentEvent == 10 && event.getEntity().getKiller() != null) {
            event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), ItemManager.createPuzzle10Drop());
        }

        if (event.getEntity() instanceof Wither && ARG.currentEvent == 12) {
            event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), ItemManager.createWitherDrop());
        }

        if (event.getEntity() instanceof Warden && ARG.currentEvent == 12) {
            event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), ItemManager.createWardenDrop());
        }
    }

    @EventHandler
    public void prepareItemCraftEvent(PrepareItemCraftEvent event) {
        if (event.getInventory().containsAtLeast(ItemManager.createPuzzle10Drop(), 1) && event.getRecipe() != null) {
            if (event.getRecipe().getResult().getType() == Material.BEACON) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
            }
        }
    }

    @EventHandler
    public void onBlockPhysicsEvent(BlockPhysicsEvent event) {
        if (event.getSourceBlock().getType() != Material.ENDER_CHEST || !event.getSourceBlock().isBlockPowered() || ARG.currentEvent != 8 || event.getChangedType() != Material.REDSTONE_WIRE) {
            return;
        }

        Bukkit.getWorlds().get(0).playSound(event.getSourceBlock().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);

    }

}
