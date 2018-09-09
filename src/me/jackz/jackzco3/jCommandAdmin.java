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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class jCommandAdmin {
	jCommandAdmin(Main plugin, CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Only available to players");
			return;
		}
		Player p = (Player) sender;
		if(args[1].equalsIgnoreCase("tower")) {
			if(args.length < 3) {
				p.sendMessage("§7Please enter an ID for the tower");
				return;
			}
			//args[0], args[1], args[2]
			if(Util.isInteger(args[2])) {
				String id = String.format("%03d", Integer.parseInt(args[2]));
				jTower twr = new jTower(id,p.getLocation());
				twr.toJSON(plugin);
				p.sendMessage("§ajTower #" + id + " created");
			}else{
				p.sendMessage("§cNot a valid ID");
			}
		}
	}
}
