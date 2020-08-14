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

import me.jackz.jackzco3.lib.RandomString;
import me.jackz.jackzco3.lib.Util;
import me.jackz.jackzco3.lib.jTower;
import me.jackz.jackzco3.lib.jTowerManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Iterator;

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
			case "tower": {
				jTowerManager manager = plugin.getTowerManager();
				if(args.length < 3) {
					p.sendMessage("§cTry: add, list, remove");
					return;
				}
				if(args[2].equalsIgnoreCase("add")) {
					String id;
					if(args.length < 4) {
						id = new RandomString(8).nextString();
						//id = UUID.randomUUID().toString();
						p.sendMessage("§7Using §e" + id + " §7as ID of tower.");
					}else{
						id = (Util.isInteger(args[3]))? String.format("%03d", Integer.parseInt(args[3])) : args[3].toLowerCase();
					}
					//args[0], args[1], args[2]
					jTower twr = new jTower(id,p.getLocation());
					try {
						manager.add(twr,true);
						p.sendMessage("§ajTower §e" + id + " §acreated");
					} catch (IOException e) {
						p.sendMessage("§cFailed to add tower");
					}
				}else if(args[2].equalsIgnoreCase("list")) {

					Iterator iterator = manager.towers.values().iterator();
					while(iterator.hasNext()) {
						jTower tower = (jTower) iterator.next();
						p.sendMessage(String.format("§e%s§a: §e%f %f %f",tower.name,tower.location.getX(),tower.location.getY(),tower.location.getZ()));
					}
				}else if(args[2].equalsIgnoreCase("delete") || args[2].equalsIgnoreCase("remove")) {
					if(args[3] == null) {
						p.sendMessage("§cPlease enter a tower ID");
						return;
					}
					if(manager.towers.get(args[3]) == null) {
						p.sendMessage("§cThat tower does not exist");
					}else {
						manager.remove(args[3],true);
						p.sendMessage("§aRemoved tower successfully");
					}
				}else{
					p.sendMessage("§cunknown option. try add, delete, list");
				}
				break;
			}
			case "keychain":
				p.getInventory().addItem(Util.getCustomItem(Material.BLAZE_ROD,"§6jKeychain Creator"));
				break;
			default:
				p.sendMessage("§cUnknown admin option, you fuck");
		}
	}
}
