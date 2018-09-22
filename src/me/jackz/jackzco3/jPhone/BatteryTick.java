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

import de.tr7zw.itemnbtapi.ItemNBTAPI;
import de.tr7zw.itemnbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BatteryTick implements Runnable {
	public void run() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			//check player's inventory
			for (int i = 0; i < p.getInventory().getSize(); i++) {
				ItemStack item = p.getInventory().getItem(i);
				if (item == null || !item.getType().equals(Material.TRIPWIRE_HOOK)) continue;
				ItemMeta meta = item.getItemMeta();
				if (meta.getDisplayName() == null) continue;
				if (meta.getDisplayName().equals("§fjLight") || meta.getDisplayName().equals("§3jPhone")) { //check if tripwire hook is jPhone || jLight

					NBTItem nbt = ItemNBTAPI.getNBTItem(item);
					if (nbt.getBoolean("state")) { //If phone on
						if (nbt.getInteger("battery") == null) {
							nbt.setInteger("battery", 100);
						}
						if (nbt.getInteger("battery") == 3) {
							p.sendMessage("§cShutting down phone in slot §e" + (i + 1) + "§7 due to low battery");
							nbt.setBoolean("state", false);
						} else if (nbt.getInteger("battery") == 0) {
							nbt.setInteger("battery", -1);
							nbt.setBoolean("state", false);
							p.sendMessage("§cThe jPhone in slot " + (i + 1) + " has died");
						} else if (Math.random() >= 0.90) {
							nbt.setInteger("battery", (nbt.getInteger("battery") - 1));
						}
						p.getInventory().setItem(i, nbt.getItem());
					}
				}
			}

			//check for nearby blocks

		}
	}
}
