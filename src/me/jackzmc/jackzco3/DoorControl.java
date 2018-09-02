package me.jackzmc.jackzco3;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class DoorControl extends JavaPlugin implements Listener {
    private final Main plugin;
    public DoorControl(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("must be a player srry");
        }
        Player player = (Player) sender;
        if(command.getName().equals(("getid"))) {
            ItemStack idCard = new ItemStack(Material.PAPER);
            ItemMeta idCardMeta = idCard.getItemMeta();
            idCardMeta.setDisplayName("§6ID Card");
            List<String> lore = new ArrayList<String>();
            lore.add("§7UUID|" + player.getUniqueId());
            lore.add("§7Level|1");

            idCardMeta.setLore(lore);
            idCard.setItemMeta(idCardMeta);
            player.getInventory().addItem(idCard);
        }
        return true;
    }

    @EventHandler
    void DoorClick(PlayerInteractEvent e) {
        Block clickedBlock = e.getClickedBlock();
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(clickedBlock.getType() == Material.IRON_DOOR_BLOCK) {
                BlockState state = clickedBlock.getState();
                MaterialData data = state.getData();
                if(data instanceof Door) {
                    Door door = (Door) data;
                }
            }
        }

    }
}
