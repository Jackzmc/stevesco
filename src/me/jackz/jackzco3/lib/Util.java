package me.jackz.jackzco3.lib;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Util {
    public Util() {

    }

    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }
    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
    public static boolean isSafeLocation(Location location) {
        Block feet = location.getBlock();
        if (!feet.getType().isTransparent() && !feet.getLocation().add(0, 1, 0).getBlock().getType().isTransparent()) {
            return false; // not transparent (will suffocate)
        }
        Block head = feet.getRelative(BlockFace.UP);
        if (!head.getType().isTransparent()) {
            return false; // not transparent (will suffocate)
        }
        Block ground = feet.getRelative(BlockFace.DOWN);
        return ground.getType().isSolid();
    }
    public void createDisplay(Player p, Material material, Inventory inv, int slot, String name) {
        createDisplay(p,material,inv,slot,name,"");
    }
    public void createDisplay(Player p, Material material, Inventory inv, int Slot, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        name = name.replaceAll("%name%", p.getName()); //convert vars
        name = name.replaceAll("%display name%", p.getCustomName());
        lore = lore.replaceAll("%name%", p.getName());
        lore = lore.replaceAll("%display name%", p.getCustomName());

        name = ChatColor.translateAlternateColorCodes('&', name); //convert & to color code
        lore = ChatColor.translateAlternateColorCodes('&', lore);
        meta.setDisplayName(name);

        ArrayList<String> Lore = new ArrayList<>(Arrays.asList(lore.split("\\|")));

        //Lore.add(lore);
        meta.setLore(Lore);
        item.setItemMeta(meta);
        inv.setItem(Slot, item);
    }
    public ItemStack getCustomItem(Material mt, String name) {
        return getCustomItem(mt,name,new ArrayList<>());
    }
    public ItemStack getCustomItem(Material mt, String name, List<String> lore) {
        ItemStack item = new ItemStack(mt);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public Location getCenterLocation(Location loc) {
        return loc.add((loc.getX() > 0 ? 0.5 : -0.5), 0.0, (loc.getZ() > 0 ? 0.5 : -0.5));
    }
    public boolean checkItem(ItemStack input, String name) {
        ItemMeta meta = input.getItemMeta();
        if(meta == null || meta.getDisplayName() == null) {
            return false;
        }else{
            return meta.getDisplayName().equals(name);
        }
    }
    public boolean checkItem(ItemStack input, Material mt, String name) {
        if(input == null || input.getType() != mt) return false;
        return checkItem(input,name);
    }
    public ItemStack getIntroBook() {
        String[] pages = {
                "Thank you for choosing jPhone! To get started you will want to claim the phone. Shift+rightclick, holding the phone to open your appswitcher. You will want to turn on terminal mode. Terminal allows you to run commands to do certain tasks. ",
                "You will want to type in chat with terminal mode 'help' to get started You can view a list of commands in terminal mode by typing 'ccommands [page #]'. Try texting someone or claiming your device!"
        };
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setAuthor("JackzCo");
        meta.setTitle("jPhone Docs");
        meta.addPage(pages);
        book.setItemMeta(meta);
        return book;
    }
}
