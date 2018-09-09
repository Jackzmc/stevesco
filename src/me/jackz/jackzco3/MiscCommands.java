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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

class MiscCommands {
	boolean onCommand(Main plugin,CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("setname")) {
			if(sender instanceof Player) {
				try {
					Player p = (Player) sender;
					ItemStack item = p.getInventory().getItemInMainHand();
					if(item == null) {
						p.sendMessage("§cYou must have an item in your primary hand!");
						return true;
					}else{

						String msg = args[0].replace("_"," ");
						msg = msg.trim().replaceAll("(&([a-f0-9]))", "\u00A7$2");

						ItemMeta itemMeta = item.getItemMeta();
						itemMeta.setDisplayName(msg);
						item.setItemMeta(itemMeta);

					}
				}catch(Exception err) {
					sender.sendMessage("Failed: §c" + err.toString());
					plugin.getLogger().log(Level.INFO,"setname!",err);
				}
			}else{
				sender.sendMessage("You must be a player");
			}
			return true;
		} else if (command.getName().equalsIgnoreCase("uuid")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("§7Must be a player");
				return true;
			}
			Player p = (Player) sender;
			sender.sendMessage("Your UUID is §e" + p.getUniqueId());
			return true;
		}else if(command.getName().equalsIgnoreCase("getlogs")) {
			try {

				String log = plugin.getDataFolder().toPath() + "/../../logs/latest.log";
				FileInputStream in = new FileInputStream(log);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));

				List<String> lines = new LinkedList<>();
				for (String tmp; (tmp = br.readLine()) != null; )
					if (lines.add(tmp) && lines.size() > 5)
						lines.remove(0);

				for (String line : lines) {
					sender.sendMessage(line);
				}
			} catch (Exception ex) {
				sender.sendMessage("Failed to get file: " + ex.toString());
				plugin.getLogger().log(Level.INFO, "getlogs!", ex);
			}

			return true;
		}else if(command.getName().equalsIgnoreCase("getname")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				ItemStack itm = p.getInventory().getItemInMainHand();
				if(itm == null) {
					p.sendMessage("§cYou must have an item in your hand!");
				}else{
					p.sendMessage("Item is: §e" + itm.getType().toString());
				}
			}
			return true;
		}else if(command.getName().equalsIgnoreCase("test")) {
			sender.sendMessage(plugin.getJackzCo().getString("motd"));
			if(sender instanceof Player) {
				Player p = (Player) sender;
				sender.sendMessage("In JackzCo Region: " + plugin.isJackzCoRegion(p.getLocation()));
			}

			return true;
		}
		return false;
	}
}
