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
import me.jackz.jackzco3.lib.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

class KeyChainEvents implements Listener {
	private final Main plugin;
	KeyChainEvents(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getHand() == EquipmentSlot.HAND) {
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(new Util().checkItem(e.getItem(), Material.BLAZE_ROD,"§6jKeychain Creator")) {
					//todo: check if inventory
					//todo: convert name
					e.setCancelled(true);
					boolean status = new KeyChainStorage(plugin).addKeyChain(e.getClickedBlock().getLocation());
					if(status) {
						p.sendMessage("§aSuccessfully added to storage");
					}else{
						p.sendMessage("§cFailed to add storage.");
					}
				}
			}
		}
	}
}