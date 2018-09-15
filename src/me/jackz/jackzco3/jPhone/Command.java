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
import me.jackz.jackzco3.Main;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Command implements CommandExecutor {
	private Main plugin;
	private jPhoneMain jphone;
	Command(Main plugin,jPhoneMain jphone) {
		this.plugin = plugin;
		this.jphone = jphone;
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			ItemStack phone =  p.getInventory().getItemInMainHand();
			NBTItem nbti = ItemNBTAPI.getNBTItem(phone);
			if(args.length == 0) {
				sender.sendMessage("§cNo arguments specified. Try /jphone help");
				return true;
			}
			switch(args[0].toLowerCase()) {
				case "help":
					p.sendMessage("§7This is now a terminal-based command.");
					break;
				case "own":
				case "claim":
					p.sendMessage("§7You must now claim the device by using your terminal");
					break;
				case "trash":
					Inventory trash = Bukkit.createInventory(null, 9 * 3, "jPhoneMain Portable Trash");
					p.openInventory(trash);
					break;
				case "dangers":
					p.sendMessage("§7This is now a terminal-based command.");
					break;
				case "glow":
					p.sendMessage("§7This is now a terminal-based command.");
					break;
				case "charge":
					nbti.setInteger("battery", 100);
					p.getInventory().setItemInMainHand(nbti.getItem());
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING,1,1);
					p.sendMessage("§aDING! Charged your phone!");
					break;
				case "state":
					p.sendMessage("§7This is now a terminal-based command");
					break;
				case "_terminal":
					p.sendMessage("§7Sorry, this feature is currently being revamped");
					break;
				case "exit":
					nbti.setBoolean("terminal", false);
					p.sendMessage("§7Exited §terminal mode");
					p.getInventory().setItemInMainHand(nbti.getItem());
				case "lookup":
					p.sendMessage("§7Looking up player from UUID...");
					try {
						UUID uuid = UUID.fromString(args[1]);
						p.sendMessage("§7Player: §e" + Bukkit.getOfflinePlayer(uuid).getName());
					}catch(IllegalArgumentException e) {
						p.sendMessage("§cPlayer was not found, or invalid UUID");
					}
					break;
				default:
					p.sendMessage("§cThe specified commmand doesn't exist");
			}
			return true;
		}
		return false;
	}
}
