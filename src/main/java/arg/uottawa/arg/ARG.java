package arg.uottawa.arg;

import arg.uottawa.arg.advancements.FallAdvancement;
import arg.uottawa.arg.commands.CommandManager;
import arg.uottawa.arg.events.EventManager;
import arg.uottawa.arg.events.KeyFoundEvent;
import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import com.google.auth.oauth2.GoogleCredentials;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;

public final class ARG extends JavaPlugin implements Listener {

    public Firestore db;
    public static int[][] portal =  new int[12][3];
    public static int[] chest = new int[3];
    public static int currentEvent = 0;
    private UltimateAdvancementAPI api;
    private RootAdvancement root;
    public static AdvancementTab advTab;
    @Override
    public void onEnable() {
        // Plugin startup logic

        getLogger().log(Level.INFO, "Initializing database...");
        try {

            // Authenticate credentials
            InputStream serviceAcc = getClass().getResourceAsStream("/creds.json");
            GoogleCredentials cred = GoogleCredentials.fromStream(serviceAcc);
            FirebaseOptions options = FirebaseOptions.builder().setCredentials(cred).build();

            // Init firebase
            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "An error occured!");
            getLogger().log(Level.SEVERE, e.toString());
        }

        // Grab db
        db = FirestoreClient.getFirestore();
        getLogger().log(Level.INFO, "Database initialized!");

        getLogger().log(Level.INFO, "Initializing listeners...");
        initListeners(db);
        getLogger().log(Level.INFO, "Listeners started!");

        // Command Registration
        getLogger().log(Level.INFO, "Registering commands...");
        getCommand("argstick").setExecutor(new CommandManager());
        getCommand("startEvent").setExecutor(new CommandManager());

        // Config setup
        String portalLoc = getConfig().getString("portal");
        String chestLoc = getConfig().getString("chest");

        // Read portal location from config
        if (portalLoc != null && !portalLoc.equals("")) {

            Scanner sc = new Scanner(portalLoc).useDelimiter("[,|]");
            for (int r = 0; r < 12; r++) {
                for (int c = 0; c < 3; c++) {
                    portal[r][c] = Integer.parseInt(sc.next());
                }
            }

        }

        if (chestLoc != null && !chestLoc.equals("")) {
            String[] strArr = chestLoc.split("\\|");
            for (int i = 0; i < strArr.length; i++) {
                chest[i] = Integer.parseInt(strArr[i]);
            }
        }

        if (!(portalLoc != null && !portalLoc.equals("")) && !(chestLoc != null && !chestLoc.equals(""))) {
            // Create default config
            getConfig().options().copyDefaults();
            saveDefaultConfig();
        }

        // Add advancements
        api = UltimateAdvancementAPI.getInstance(this);
        advTab = api.createAdvancementTab("argadvancement");
        AdvancementDisplay rootDisplay = new AdvancementDisplay(Material.ENDER_EYE, "...", AdvancementFrameType.TASK, true, true, 0, 0, "It beckons...");
        root = new RootAdvancement(advTab, "root", rootDisplay, "textures/block/end_stone_bricks.png");
        FallAdvancement fa = new FallAdvancement("fall", new AdvancementDisplay(Material.ELYTRA, "The taller they stand...", AdvancementFrameType.CHALLENGE, true, true, 1.5f, 0, "The harder they fall."), root);
        advTab.registerAdvancements(root, fa);

        // Init events
        getLogger().log(Level.INFO, "Initializing events...");
        getServer().getPluginManager().registerEvents(new EventManager(), this);
        getLogger().log(Level.INFO, "Events created!");

        getLogger().log(Level.INFO, "Started!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().log(Level.INFO, "Disabled!");
    }

    private void sendKeyEvent(int num) {
        // Create event and run task synchronously since db listener is async
        KeyFoundEvent kfe = new KeyFoundEvent(num);
        Bukkit.getScheduler().runTask(this, () -> {
            try {Bukkit.getPluginManager().callEvent(kfe);} catch (Exception e) { System.out.println(e.toString()); };
        });
    }
    private void initListeners(Firestore db) {

        // Event Change Listener
        DocumentReference docRef = db.collection("events").document("CURRENTEVENT");
        docRef.addSnapshotListener(
                new EventListener<DocumentSnapshot>() {

                    private boolean check = false;
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirestoreException error) {
                        if (error != null) {
                            getLogger().log(Level.SEVERE, "Event Listener failed!");
                            getLogger().log(Level.SEVERE, error.toString());
                            return;
                        }
                        // Check if there has been a change in the document
                        if (value != null && value.exists()) {

                            currentEvent = Math.toIntExact((Long) value.get("event"));

                            // Fill portal and set audio diary
                            sendKeyEvent(Math.toIntExact((Long) value.get("event")));

                            // Makes first snapshot is loaded before announcing any changes
                            if (check) {

                                // Check if ARG has been solved
                                if ((Long) value.get("event") == 13) {
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        p.playSound(p.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 0.3f, 1);
                                    }
                                    Bukkit.broadcastMessage(ChatColor.RED + "The portal has been filled. Fernyiges awaits...");
                                } else {

                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        p.playSound(p.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 1, 1);
                                    }
                                    Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "An eye has been found...");
                                }
                            } else {
                                check = true;
                            }

                        }
                    }
                }
        );
    }

}
