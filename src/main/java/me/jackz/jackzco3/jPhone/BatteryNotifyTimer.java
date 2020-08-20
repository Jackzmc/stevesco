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
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BatteryNotifyTimer implements Runnable {
	public void run() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.isSneaking()) {
				ItemStack item = player.getInventory().getItemInMainHand();
				if(item.getType() != Material.AIR) {
					NBTItem nbt = new NBTItem(item);
					if (nbt.hasKey("jphone")) {
						int battery = nbt.getInteger("battery");
						String color = (battery > 85) ? "§a" : (battery < 20) ? "§c" : "§e";
						//todo: check if TTA
						player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7Battery: " + color + battery + "%"));
					}
				}
			}
		}
	}
}
