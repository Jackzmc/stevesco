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

import de.tr7zw.itemnbtapi.ItemNBTAPI;
import de.tr7zw.itemnbtapi.NBTItem;
import me.jackz.jackzco3.Main;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Command implements CommandExecutor {
	private Main plugin;
	Command(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		if(args.length == 0) {
			sender.sendMessage("§7All jPhone commands have been moved to terminal mode.");
			return true;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player");
			return false;
		}
		Player p = (Player) sender;
		ItemStack item = p.getInventory().getItemInMainHand();
		if(!jPhoneMain.isPhone(item)) {
			p.sendMessage("§cYou must have a jphone in your hand");
			return true;
		}
		switch(args[0].toLowerCase()) {
			case "togglephone": {
				NBTItem nbt = ItemNBTAPI.getNBTItem(item);
				if (nbt.getBoolean("state")) {
					nbt.setBoolean("state", false);
					p.sendMessage("§7Phone has been switched off.");
				} else {
					nbt.setBoolean("state", true);
					p.sendMessage("§7Phone has been turned on.");
				}
				p.getInventory().setItemInMainHand(nbt.getItem());
				break;
			}
			default:
				sender.sendMessage("§7All jPhone commands have been moved to terminal mode.");
		}
		return true;
	}
}
