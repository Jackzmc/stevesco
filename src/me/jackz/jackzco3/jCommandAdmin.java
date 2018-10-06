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

import me.jackz.jackzco3.lib.Util;
import me.jackz.jackzco3.lib.jTower;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

class jCommandAdmin {
	jCommandAdmin(Main plugin, CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Only available to players");
			return;
		}
		Player p = (Player) sender;
		if(!p.getUniqueId().toString().equals("b0c16432-67a6-4e3d-b49a-61b323c49b03")) {
			p.sendMessage("§cYou are not a jackz, u fuk off m8");
			return;
		}
		if(args.length < 2) {
			p.sendMessage("put an option you fuck");
			return;
		}
		switch(args[1].toLowerCase()) {
			case "tower":
				String id;
				if(args.length < 3) {
					id = UUID.randomUUID().toString();
					p.sendMessage("§7Using §e" + id + "§7as ID of tower.");
					return;
				}
				//args[0], args[1], args[2]
				if(Util.isInteger(args[2])) {
					id = String.format("%03d", Integer.parseInt(args[2]));
				}else{
					id = args[2].toLowerCase();
				}
				jTower twr = new jTower(id,p.getLocation());
				twr.toJSON(plugin);
				p.sendMessage("§ajTower §e" + id + " §acreated");
				break;
			case "keychain":
				p.getInventory().addItem(new Util().getCustomItem(Material.BLAZE_ROD,"§6jKeychain Creator"));
				break;
			default:
				p.sendMessage("§cUnknown admin option, you fuck");
		}
	}
}
