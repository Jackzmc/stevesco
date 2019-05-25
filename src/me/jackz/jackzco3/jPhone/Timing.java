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

import de.Herbystar.TTA.TTA_Methods;
import de.tr7zw.itemnbtapi.ItemNBTAPI;
import de.tr7zw.itemnbtapi.NBTItem;
import me.jackz.jackzco3.lib.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Timing implements Runnable {
	public void run() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			ItemStack item = p.getInventory().getItemInMainHand();
			if(Util.checkItem(item,Material.TRIPWIRE_HOOK,"jPhone")) {
				NBTItem nbt = ItemNBTAPI.getNBTItem(item);
				if(!nbt.hasKey("jphone")) continue;
				int battery = nbt.getInteger("battery");
				String color = (battery > 85) ? "§a" : (battery < 20) ? "§c" : "§e";
				//todo: check if TTA
				TTA_Methods.sendActionBar(p,"§7Battery: " + color + battery + "%");

			}
		}
	}
}
