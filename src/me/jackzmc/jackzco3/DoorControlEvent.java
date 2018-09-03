package me.jackzmc.jackzco3;

import de.Herbystar.TTA.TTA_Methods;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public class DoorControlEvent implements Listener {
    private final Main plugin;
    public DoorControlEvent(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    void DoorClick(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand() == EquipmentSlot.HAND) {
            Block clickedBlock = e.getClickedBlock();
            Player p = e.getPlayer();
            try {
                if(clickedBlock.getType() == Material.IRON_DOOR || clickedBlock.getType() == Material.IRON_DOOR_BLOCK) {
                    Boolean isMatch = false;
                    PlayerInventory inv = p.getInventory();
                    for(ItemStack item : inv.getContents()) {
                        if(item != null && item.getType().equals(Material.PAPER)) {

                            e.setCancelled(true);
                            if(item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("§6ID Card")) {
                                isMatch = true;
                                BlockState blockState = clickedBlock.getState();
                                if(((Door) blockState.getData()).isTopHalf()){
                                    blockState = clickedBlock.getRelative(BlockFace.DOWN).getState();
                                }

                                Openable openable = (Openable) blockState.getData();
                                Boolean isDoorOpen = openable.isOpen();
                                if(isDoorOpen) { //is currently opened
                                    openable.setOpen(false);
                                    p.playSound(p.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1, 1);
                                }else{ //is currently closed
                                    openable.setOpen(true);
                                    p.playSound(p.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1, 1);
                                }
                                blockState.setData((MaterialData) openable);
                                blockState.update();
                                if(isDoorOpen) {
                                    Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                                        public void run() {
                                            BlockState closedBS = clickedBlock.getState();
                                            Openable closed_openable = (Openable) closedBS.getData();
                                            if (closed_openable.isOpen()) {

                                                if (((Door) closedBS.getData()).isTopHalf()) {
                                                    closedBS = clickedBlock.getRelative(BlockFace.DOWN).getState();
                                                }
                                                closed_openable.setOpen(false);
                                                closedBS.setData((MaterialData) closed_openable);
                                                closedBS.update();
                                                p.playSound(p.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1, 1);
                                            }

                                        }
                                    }, 60L);
                                }
                                break;
                            }

                        }
                    }
                    if(!isMatch) {
                        TTA_Methods.sendActionBar(p, "§cYou need an ID Card!");
                    }
                    //failed iron block, no paper

                }
            }catch(Exception ex) {
                p.sendMessage("§6[DoorControl] §rError occurred: §c" + ex.toString());
                plugin.getLogger().log(Level.INFO,"DoorControl FUCKED UP JACKZ!!!!",ex);
            }

        }

    }
}