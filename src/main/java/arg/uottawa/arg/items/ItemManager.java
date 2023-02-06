package arg.uottawa.arg.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
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

}
