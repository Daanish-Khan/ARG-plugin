package arg.uottawa.arg;

import arg.uottawa.arg.commands.CommandManager;
import arg.uottawa.arg.events.EventManager;
import arg.uottawa.arg.events.KeyFoundEvent;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import com.google.auth.oauth2.GoogleCredentials;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.checkerframework.checker.units.qual.K;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;

public final class ARG extends JavaPlugin implements Listener {

    public Firestore db;
    public static int[][] portal =  new int[12][3];
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

        // Init events
        getLogger().log(Level.INFO, "Initializing events...");
        getServer().getPluginManager().registerEvents(new EventManager(), this);
        getLogger().log(Level.INFO, "Events created!");

        getLogger().log(Level.INFO, "Initializing listeners...");
        initListeners(db);
        getLogger().log(Level.INFO, "Listeners started!");

        // Command Registration
        getLogger().log(Level.INFO, "Registering commands...");
        getCommand("argstick").setExecutor(new CommandManager());

        // Config setup
        String portalLoc = getConfig().getString("portal");

        // Read portal location from config
        if (portalLoc != null && !portalLoc.equals("")) {

            Scanner sc = new Scanner(portalLoc).useDelimiter("[,|]");
            for (int r = 0; r < 12; r++) {
                for (int c = 0; c < 3; c++) {
                    portal[r][c] = Integer.parseInt(sc.next());
                }
            }

        } else {
            // Create default config
            getConfig().options().copyDefaults();
            saveDefaultConfig();
        }

        getLogger().log(Level.INFO, "Started!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().log(Level.INFO, "Disabled!");
    }

    private void placeEyes(int num) {
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
                            // Makes first snapshot is loaded before making any changes
                            if (check) {

                                // Fill portal
                                placeEyes(Math.toIntExact((Long) value.get("event")));

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
