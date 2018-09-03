package me.jackzmc.jackzco3;


import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class DoorControlCmd implements CommandExecutor {
    private final Main plugin;
    public DoorControlCmd(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("must be a player sorry");
            return true;
        }
        Player player = (Player) sender;

        ItemStack idCard = new ItemStack(Material.PAPER);
        ItemMeta idCardMeta = idCard.getItemMeta();
        idCardMeta.setDisplayName("§6ID Card");
        List<String> lore = new ArrayList<>();
        lore.add("§7UUID " + player.getUniqueId());
        lore.add("§7Level 1");

        idCardMeta.setLore(lore);
        idCard.setItemMeta(idCardMeta);
        player.getInventory().addItem(idCard);

        return true;
    }
}
