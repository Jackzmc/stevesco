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
import me.jackz.jackzco3.lib.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryClick implements Listener {
	private final Main plugin;
	private final Util util;
	private jPhoneMain jphone;
	InventoryClick(Main plugin,jPhoneMain jphone) {
		this.plugin = plugin;
		this.jphone = jphone;
		this.util = new Util();
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) { //move to own class?
		Player p = (Player) event.getWhoClicked(); // The player that clicked the item
		ItemStack clicked = event.getCurrentItem(); // The item that was clicked
		Inventory inventory = event.getInventory(); // The inventory that was clicked in
		if(inventory.getName().equals(jPhoneMain.appswitcher.getName())) {
			//If name of inventory is same as app switcher
			event.setCancelled(true);
			if(!(p.getInventory().getItemInMainHand().getType().equals(Material.TRIPWIRE_HOOK))) {
				p.sendMessage("§You must have your phone being held");
				return;
			}
			if(clicked == null) return;
			ItemStack item =  p.getInventory().getItemInMainHand();
			NBTItem nbti = ItemNBTAPI.getNBTItem(item);
			switch(clicked.getType()) {
				case AIR:
					break;
				case TORCH:
					ItemStack CurrentPhone = nbti.getItem();
					ItemMeta PhoneMeta = CurrentPhone.getItemMeta();
					PhoneMeta.setDisplayName("§fjLight");
					CurrentPhone.setItemMeta(PhoneMeta);
					CurrentPhone.setType(Material.TORCH);
					p.getInventory().setItemInMainHand(CurrentPhone);
					break;
				case BOOK_AND_QUILL:
					p.sendMessage("§7Type /jphone claim to claim");
					break;
				case SIGN:
					if(nbti.getBoolean("terminal")) {
						//ON to OFF
						nbti.setBoolean("terminal", false);
						p.sendMessage("§7Exited §eterminal mode");
						p.getInventory().setItemInMainHand(nbti.getItem());
					}else if(!nbti.getBoolean("terminal")) {
						//OFF to ON
						nbti.setBoolean("terminal", true);
						p.sendMessage("§7Entered §eterminal mode. §7Type §e'help'§7 for help");
						p.getInventory().setItemInMainHand(nbti.getItem());
					}

					break;
				case REDSTONE_LAMP_OFF:
					nbti.setBoolean("state",false);
					p.getInventory().setItemInMainHand(nbti.getItem());
					p.sendMessage("§7Phone has been switched off.");
					break;
				case NOTE_BLOCK:
					util.createDisplay(p,Material.WRITTEN_BOOK,jPhoneMain.stunes,0,"§9INFO");
					plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
						p.openInventory(jPhoneMain.stunes);
					},1L);
					//play a tune
					break;
				default:
					p.sendMessage("That item is not configured correctly.");
			}
			p.closeInventory(); //close it
		}else if(inventory.getName().equals(jPhoneMain.stunes.getName())) {
			event.setCancelled(true);
			switch(clicked.getType()) {
				default:
					p.sendMessage("§7The §3Steves Tunes §7player is currently §cin development");
			}
		}
	}
}