package me.jackzmc.jackzco3;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class Wand implements Listener {
    private final Main plugin;

    public Wand(Main plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    void noLitter(PlayerDropItemEvent e) {
        ItemStack item = e.getItemDrop().getItemStack();
        if(item.getType().equals(Material.INK_SACK) & item.getDurability() == (byte) 4) {
            Player p = e.getPlayer();
            e.setCancelled(true);
            p.sendMessage("dude, no littering u fuck");
            p.getInventory().addItem(item);
        }
    }

    @EventHandler
    void WandUse(PlayerInteractEvent e) {
        try {
            if(e.getAction() == Action.RIGHT_CLICK_AIR && e.getHand() == EquipmentSlot.HAND) {
                Player p = e.getPlayer();
                ItemStack item = p.getInventory().getItemInMainHand();
                if(item.getType().equals(Material.SPECTRAL_ARROW)) {
                    e.setCancelled(true);
                    Integer wandSlot = p.getInventory().getHeldItemSlot();
                    wandSlot = (wandSlot >= 8) ? 0 : ++wandSlot;
                    ItemStack buildBlock = p.getInventory().getItem(wandSlot);
                    if(buildBlock != null && buildBlock.getType().isBlock()) {
                        p.sendMessage("attempt");
                        Block originb = p.getTargetBlock(null, 5);
                        Location block0 = new Location(originb.getWorld(),originb.getX(),originb.getY() + 1,originb.getZ());
                        Location block1 = new Location(originb.getWorld(),originb.getX(),originb.getY() + 1,originb.getZ() + 1);
                        originb.getWorld().getBlockAt(block0).setType(buildBlock.getType());
                        originb.getWorld().getBlockAt(block0).setType(buildBlock.getType());
                    }else{
                        p.sendMessage("§cThe item to your right slot is not a block!");
                    }
                }
            }
        }catch(Exception ex) {
            Player p = e.getPlayer();
            p.sendMessage("Error: §c" + ex.toString());
        }
    }
}
