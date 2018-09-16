/*
 * Copyright (C) 2018 Jackson Bixby
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
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockEvent implements Listener {
	private final Main plugin;
	private final Util util;
	private jPhoneMain jphone;
	BlockEvent(Main plugin, jPhoneMain jphone) {
		this.plugin = plugin;
		this.jphone = jphone;
		this.util = new Util();
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(e.getBlockPlaced().getType().equals(Material.PISTON_BASE)) {
			ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
			if(meta == null || meta.getDisplayName() == null || !meta.getDisplayName().equals("§fjCharger")){
				return;
			}
			if(e.getBlockAgainst().getType().equals(Material.GOLD_BLOCK)) {
				e.getBlockPlaced().setData((byte) 1);
				p.getWorld().playSound(e.getBlockPlaced().getLocation(), Sound.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS,1,1);
				p.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE,util.getCenterLocation(e.getBlockPlaced().getLocation()),400,0.5,20,0.5);
				new LocationStore(plugin).setBoolean(e.getBlockPlaced().getLocation(),true);
				p.sendMessage("§aSuccessflly added jCharger");
			}else{
				e.setCancelled(true);
				p.sendMessage("§7Must be placed on a gold block");
			}
		}
	}
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(e.getBlock().getType().equals(Material.PISTON_BASE)) {
			boolean isCharger = new LocationStore(plugin).getBoolean(e.getBlock().getLocation());
			if(isCharger) {
				if(p.isSneaking()) {
					new LocationStore(plugin).deleteValue(e.getBlock().getLocation()); //todo: make .deleteValue()
					p.getInventory().addItem(util.getNamedItem(Material.PISTON_BASE,"§fjCharger"));
					p.sendMessage("§cSuccessfully removed jCharger");
				}else{
					e.setCancelled(true);
					p.sendMessage("§7Sneak & break to pick up the jCharger.");
				}
			}
		}
	}
}
