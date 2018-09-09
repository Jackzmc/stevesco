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

package me.jackz.jackzco3;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

class PlayerInteractHandler implements Listener {
	private final Main plugin;

	PlayerInteractHandler(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getHand() == EquipmentSlot.HAND) {
			ItemStack hand = p.getInventory().getItemInMainHand();
			if(hand != null && hand.getType() == Material.STICK) {
				ItemMeta meta = hand.getItemMeta();

			}
		}
	}
}
