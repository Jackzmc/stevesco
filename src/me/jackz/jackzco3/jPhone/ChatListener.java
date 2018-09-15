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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChatListener implements Listener {
	private Main plugin;
	private jPhoneMain jphone;
	ChatListener(Main plugin,jPhoneMain jphone) {
		this.plugin = plugin;
		this.jphone = jphone;
	}
	@EventHandler
	public void jPhoneChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		ItemStack itm = p.getInventory().getItemInMainHand();
		if(itm != null) {
			NBTItem nbt = ItemNBTAPI.getNBTItem(itm);
			if(!nbt.hasNBTData()) {
				return;
			}
			if(nbt.getBoolean("terminal")) {
				e.setCancelled(true);
				p.sendMessage(" ");
				p.sendMessage("§a>" + e.getMessage());
				String[] args = e.getMessage().split(" ");
				switch(args[0].toLowerCase()) {
					case "version":
						p.sendMessage("§7The current version of terminal is §e" + plugin.getJackzCo().getString("versions.terminal"));
						break;
					case "light":
					case "jlight":
						ItemStack CurrentPhone = nbt.getItem();
						ItemMeta PhoneMeta = CurrentPhone.getItemMeta();
						PhoneMeta.setDisplayName("§fjLight");
						CurrentPhone.setItemMeta(PhoneMeta);
						CurrentPhone.setType(Material.TORCH);
						p.getInventory().setItemInMainHand(CurrentPhone);
						break;
					case "commands":
						p.sendMessage("§3Current Commands:\n§ehelp §7general help\n§eversion §7check the version of terminal\n§elight §7turn on your flashlight\n§eexit §7exits terminal");
						break;
					case "help":
						p.sendMessage("§7Hi, terminal is currently in alpha and missing features.");
						p.sendMessage("§7Current Version is: §e" + plugin.getJackzCo().getString("versions.terminal"));
						p.sendMessage("§7Type §ecommands §7to view commands");
						break;
					case "exit":
						nbt.setBoolean("terminal",false);
						p.sendMessage("§7Exited §eterminal mode");
						p.getInventory().setItemInMainHand(nbt.getItem());
						break;
					default:
						p.sendMessage("§cUnknown command was specified. §7Type §ehelp for help");

				}

			}
		}
	}
}
