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

package me.jackz.jackzco3.jPhone;

import me.jackz.jackzco3.Main;
import me.jackz.jackzco3.lib.LocationStore;
import me.jackz.jackzco3.lib.Util;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;

public class BlockEvent implements Listener {
	private final Main plugin;

	BlockEvent(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();

		if(e.getBlockPlaced().getType().equals(Material.PISTON)) {
			ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();

			if(meta == null || !meta.getDisplayName().contains("jCharger")) return;
			if(!meta.hasLore() && meta.getLore().size() > 0|| !meta.getLore().get(0).equals(jPhoneMain.JCHARGER_VERIFY)) return;
			if(e.getBlockAgainst().getType().equals(Material.GOLD_BLOCK)) {
				p.sendMessage("§cgold block placed against");
				Directional piston = (Directional) e.getBlockPlaced().getBlockData();
				//Piston piston = (Piston) e.getBlockPlaced();
				piston.setFacing(BlockFace.UP);
				p.getWorld().playSound(e.getBlockPlaced().getLocation(), Sound.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS,1,1);
				p.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, Util.getCenterLocation(e.getBlockPlaced().getLocation()),400,0.5,20,0.5);
				new LocationStore(plugin).setString(e.getBlockPlaced().getLocation(),jPhoneMain.JCHARGER_VERIFY);
				p.sendMessage("§aSuccessfully added jCharger");
			}else{
				e.setCancelled(true);
				p.sendMessage("§7Must be placed on a gold block");
			}
		}
	}
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(e.getBlock().getType().equals(Material.PISTON)) {
			String charger_verify = new LocationStore(plugin).getString(e.getBlock().getLocation());
			if(charger_verify.equals(jPhoneMain.JCHARGER_VERIFY)) {
				if(p.isSneaking()) {
					new LocationStore(plugin).deleteValue(e.getBlock().getLocation()); //todo: make .deleteValue()
					if(p.getGameMode() != GameMode.CREATIVE) {
						ItemStack itm = new ItemStack(Material.PISTON);
						ItemMeta meta = itm.getItemMeta();
						meta.setDisplayName("§fjCharger");
						meta.setLore(new ArrayList<>(
								Collections.singletonList(jPhoneMain.JCHARGER_VERIFY))
						);
						itm.setItemMeta(meta);
						p.getInventory().addItem(itm);
					}
					//p.getInventory().addItem(Util.getCustomItem(Material.PISTON,"§fjCharger"));
					p.sendMessage("§cSuccessfully removed jCharger");
				}else{
					e.setCancelled(true);
					p.sendMessage("§7Sneak & break to pick up the jCharger.");
				}
			}
		}
	}
}
