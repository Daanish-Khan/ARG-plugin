package arg.uottawa.arg.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

}
