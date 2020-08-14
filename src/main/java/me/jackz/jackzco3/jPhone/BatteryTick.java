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

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

public class BatteryTick implements Runnable {
	private final Calendar calt = Calendar.getInstance();
	private final SimpleDateFormat sdft = new SimpleDateFormat("h:mm a");

	public void run() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			//check player's inventory
			for (int i = 0; i < p.getInventory().getSize(); i++) {
				ItemStack item = p.getInventory().getItem(i);
				if (item == null || !item.getType().equals(Material.TRIPWIRE_HOOK)) continue;
				ItemMeta meta = item.getItemMeta();
				String dpname = (meta != null && meta.hasDisplayName()) ? meta.getDisplayName() : null;
				if(dpname == null) continue;
				//if (dpname.contains("jLight") || dpname.contains("jPhone") || dpname.contains("jWrench")) { //check if tripwire hook is jPhone || jLight
				NBTItem nbt = new NBTItem(item);
				if(!nbt.hasKey("jphone")) continue;
				if (nbt.getBoolean("state")) { //If phone on
					Integer battery = nbt.getInteger("battery");
					if (battery == null) {
						nbt.setInteger("battery", 100);
						battery = 100;
					}
					meta.setLore(new ArrayList<>(Arrays.asList(
							"§9Time: §7" +  sdft.format(calt.getTime()),
							"§9Battery: §7" + battery + "%",
							"",
							"§7§oNo new notifications"
					)));

					if (battery == 3) {
						p.sendMessage("§cShutting down phone in slot §e" + (i + 1) + "§c due to low battery");
						meta.setLore(new ArrayList<>(Collections.singletonList("§cPhone is switched off.")));
						nbt.setBoolean("state", false);
					} else if (battery == 0) {
						nbt.setInteger("battery", -1);
						nbt.setBoolean("state", false);
						meta.setLore(new ArrayList<>(Collections.singletonList("§cPhone is dead, use a jCharger to recharge.")));
						p.sendMessage("§cThe jPhone in slot " + (i + 1) + " has died");
					} else if (Math.random() >= 0.80) {
						nbt.setInteger("battery", battery - 1);
					}
					item = nbt.getItem();
					item.setItemMeta(meta);
					p.getInventory().setItem(i, item);
				}
				//}
			}

			//check for nearby blocks

		}
	}
}
