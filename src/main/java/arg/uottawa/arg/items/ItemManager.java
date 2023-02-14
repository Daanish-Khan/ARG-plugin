package arg.uottawa.arg.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemManager {

    public static ItemStack createStick() {
        ItemStack item = new ItemStack(Material.STICK, 1);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("Selection Stick");
        List<String> lore = new ArrayList<>();
        lore.add("Selects ender portal.");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createDisc(int audioNum) {
        ItemStack item = new ItemStack(Material.matchMaterial("music_disc_11"));
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(audioNum);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "audio" + audioNum);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createPuzzle1Book() {
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) item.getItemMeta();

        List<String> pages = new ArrayList<String>();
        pages.add("59584a6e4c6e5676644852686432466c63334276636e527a4c6d4e683d3d");
        pages.add("54456c545645564f494652504946644951565167534555675530465a55773d3d");

        meta.setTitle("████");
        meta.setAuthor("████");
        meta.setPages(pages);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createPuzzle4Books(int num) {
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) item.getItemMeta();

        List<String> pages = new ArrayList<String>();
        //https://dl.dropboxusercontent.com/s/k8uw6z16utg8egq/key4.mc?dl=0
        if (num == 1) {
            pages.add("https://dl.dropboxusercontent.com");
        } else if (num == 2) {
            pages.add("s/k8uw6z16utg8egq");
        } else {
            pages.add("key4.mc?dl=0");
        }

        meta.setTitle("████");
        meta.setAuthor("████");
        meta.setPages(pages);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createPuzzle7Book() {
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) item.getItemMeta();

        List<String> pages = new ArrayList<String>();
        pages.add("141 160 151 56 165 157 164 164 141 167 141 145 163 160 157 162 164 163 56 143 141 57 160 165 172 172 154 145 77 160 150 162 141 163 145 75");
        pages.add("141 143 150 145 151 166 145 155 145 156 164 40 156 157 40 163 160 141 143 145 163 40 141 154 154 40 154 157 167 145 162 143 141 163 145");

        meta.setTitle("████");
        meta.setAuthor("████");
        meta.setPages(pages);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createPuzzle8Book() {
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) item.getItemMeta();

        List<String> pages = new ArrayList<String>();
        pages.add("Date 12██: \n" +
                "\n" +
                "Test-273A: Redstone lamp responds to eye, producing s████t by powering it with a torch would.\n" +
                "\n" +
                "Test-273B: Piston also responds to eye, ████ fully and retr████ing when the eye has been moved away.\n");

        pages.add("Date ██3█:\n" +
                "\n" +
                "Test-89██: Power originating from eye does not fade after a set dista██e when connecting the eye to a re████ne line.\n" +
                "\n" +
                "Test-8988: Attaching ████ to a chest all████s for multi████sional storage. \n");

        pages.add("████: Using ████ chest as a power source is possible. More tests needed.\n" +
                "\n" +
                "████-89██: Redstone line extended by 1███0 blocks. Measured level at eye: 10█L. Measured level at end of line: 10P█.\n");

        pages.add("Date ████:\n" +
                "\n" +
                "████: R████ted use of the eye seems to be depleting the power output. Re████ding immed████ c█ase of use.\n");

        meta.setTitle("████");
        meta.setAuthor("████");
        meta.setPages(pages);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createPuzzle10Drop() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Fragment of ███");

        List<String> lore = new ArrayList<>();
        lore.add("The Original was shattered into 9");
        lore.add("pieces in the age of old.");
        lore.add("");
        lore.add("You hold only a fragment of what");
        lore.add("it used to be...");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;

    }

    public static ItemStack createLeftEye() {
        ItemStack item = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Left Eye of █████");

        List<String> lore = new ArrayList<>();
        lore.add("The left eye of a dragon.");
        lore.add("You can feel its power");
        lore.add("flowing through you.");
        lore.add("");
        lore.add("4652454557494C4C");

        meta.setLore(lore);
        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createRightEye() {
        ItemStack item = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Right Eye of █████");

        List<String> lore = new ArrayList<>();
        lore.add("A mythical dragon, reduced to ashes.");
        lore.add("All but its eye.");
        lore.add("");
        lore.add("592 63 -709");

        meta.setLore(lore);
        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createDragonHead() {
        ItemStack item = new ItemStack(Material.DRAGON_HEAD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Head of █████");

        List<String> lore = new ArrayList<>();
        lore.add("The head of an elder dragon.");
        lore.add("Upon touching it, you can");
        lore.add("feel the ever-lasting grief");
        lore.add("of a mother whose lost their child.");
        lore.add("");
        lore.add("LOOSENEDCHAINS");

        meta.setLore(lore);
        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }

}
