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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MoveHandler implements Listener {
	private Main plugin;
	private Map<String,Boolean> scanImmune = new HashMap<>();
	private Map<String,Boolean> scanTimeImmune = new HashMap<>();

	MoveHandler(Main plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("SwitchStatementWithTooFewBranches")
	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(!Main.checkRegion(p.getLocation(),"stevesco")) return; //check if in whitelisted region
		Block underneathPlayer = p.getWorld().getBlockAt(p.getLocation().subtract(0,1,0));
		if(underneathPlayer.getType().equals(Material.AIR)) return;
		Boolean isImmune = scanImmune.get(p.getUniqueId().toString());
		Boolean isTimeImmune = scanTimeImmune.get(p.getUniqueId().toString());
		if(isImmune == null || isTimeImmune == null) {
			scanImmune.put(p.getUniqueId().toString(),false);
			scanTimeImmune.put(p.getUniqueId().toString(),false); //split this?
			isTimeImmune = false;
			isImmune = false;
		}
		switch(underneathPlayer.getType()) {
			case POLISHED_ANDESITE:
				//noinspection
				if(!isImmune && !isTimeImmune) {
					scanImmune.put(p.getUniqueId().toString(),true);
					plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
						scanTimeImmune.put(p.getUniqueId().toString(), false);
						//isTimeImmune = false;
					},600L);
					//noinspection
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 20));
					p.sendTitle("Scanning...","By §3JackzCo SuperUltra Security Scanner 3027™",0,40,0);
					List<String> items = plugin.getJackzCo().getStringList("scanner.disallowed");
					List<ItemStack> illegalItems = new ArrayList<>();
					for(ItemStack item  : p.getInventory()) {
						if(item == null) continue;
						for(String blacklisted : items) {
							if(item.getType().toString().equalsIgnoreCase(blacklisted)) {
								illegalItems.add(item);
							}
						}
					}
					if(illegalItems.size() > 0) {
						String itemList = illegalItems.stream().map(itm -> itm.getType().toString()).collect(Collectors.joining(", "));
						p.sendMessage(Main.jackzco_prefix + "§7Detected illegal items: §e" + itemList);
						plugin.getServer().broadcastMessage("§3JackzCo Security §7Player §e" + p.getName() + " §7has illegal items: §e" + itemList);
						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40*20, 40));
						//todo: lockdown
					}else{
						scanTimeImmune.put(p.getUniqueId().toString(),true);
					}
				}
				break;
			default:
				//if diff block
				if(isImmune) {
					//if immune, undo
					scanImmune.put(p.getUniqueId().toString(),false);
				}
		}
	}
}
