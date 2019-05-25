/*
 * Copyright (C) 2019 Jackson Bixby
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.jackz.jackzco3;

import de.Herbystar.TTA.TTA_Methods;
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
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

class DoorControlEvent implements Listener {
    private final Main plugin;
    DoorControlEvent(Main plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("SpellCheckingInspection")
    @EventHandler
    void DoorClick(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand() == EquipmentSlot.HAND) {
            if(e.getClickedBlock() == null) return;
            Block clickedBlock = e.getClickedBlock();
            Player p = e.getPlayer();
            try {

                if(clickedBlock.getType() == Material.IRON_DOOR) {

                    if(!Main.checkRegion(clickedBlock.getLocation(),"jackzco")) return;
                    boolean isMatch = false;
                    PlayerInventory inv = p.getInventory();
                    for(ItemStack item : inv.getContents()) {
                        if(item != null && item.getType().equals(Material.PAPER)) {
                            p.sendMessage("§cuser has ID");
                            e.setCancelled(true);

                            if(item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("§6ID Card")) {

                                isMatch = true;
                                p.sendMessage("§cdebug: door open attempt");
                                BlockState blockState = clickedBlock.getState();
                                if(((Door) blockState.getData()).isTopHalf()){
                                    blockState = clickedBlock.getRelative(BlockFace.DOWN).getState();
                                    p.sendMessage("§cdebug: " + ((Door) blockState.getData()).isTopHalf());
                                }
                                Openable openable = (Openable) blockState.getData();
                                boolean isDoorOpen = openable.isOpen();
                                if(isDoorOpen) { //is currently opened
                                    openable.setOpen(false);
                                    clickedBlock.getWorld().playSound(p.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1, 1);
                                }else{ //is currently closed
                                    openable.setOpen(true);
                                    clickedBlock.getWorld().playSound(p.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1, 1);
                                }
                                blockState.setData((MaterialData) openable);
                                blockState.update();
                                final BlockState closedBS = blockState;
                                if(!isDoorOpen) {
                                    new BukkitRunnable() {
                                        public void run() {
                                            Openable openable = (Openable) clickedBlock.getState().getData();
                                            if (openable.isOpen()) {
                                                openable.setOpen(false);
                                                closedBS.setData((MaterialData) openable);
                                                closedBS.update();
                                                clickedBlock.getWorld().playSound(p.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1, 1);
                                            }
                                        }
                                    }.runTaskLater(plugin,3000);
                                }
                                break;
                            }

                        }
                    }
                    if(!isMatch) {
                        if(plugin.getServer().getPluginManager().getPlugin("TTA") == null) return;
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