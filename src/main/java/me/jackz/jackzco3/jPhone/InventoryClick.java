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
import me.jackz.jackzco3.Main;
import me.jackz.jackzco3.lib.InventoryStore;
import me.jackz.jackzco3.lib.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class InventoryClick implements Listener {
	private final Main plugin;

	InventoryClick(Main plugin,jPhoneMain jphone) {
		this.plugin = plugin;
	}

	@SuppressWarnings("SpellCheckingInspection")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) { //move to own class?
		InventoryView view = event.getView();
		Player p = (Player) event.getWhoClicked(); // The player that clicked the item
		ItemStack clicked = event.getCurrentItem(); // The item that was clicked
		Inventory inventory = event.getInventory(); // The inventory that was clicked in
		if(view.getTitle().equals("§9jPhone App Switcher")) { //todo: fix?
			//If name of inventory is same as app switcher
			event.setCancelled(true);
			if(!(p.getInventory().getItemInMainHand().getType().equals(Material.TRIPWIRE_HOOK))) {
				p.sendMessage("§cYou must have your phone being held");
				return;
			}
			if(clicked == null) return;
			ItemStack item =  p.getInventory().getItemInMainHand();
			NBTItem nbti = new NBTItem(item);
			switch(clicked.getType()) {
				case AIR:
					break;
				case TORCH: {
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName("§fjLight");
					item.setItemMeta(meta);
					item.setType(Material.TORCH);
					p.getInventory().setItemInMainHand(item);
					break;
				} case WRITABLE_BOOK:
					Util.createDisplay(p,Material.WRITTEN_BOOK, jPhoneMain.settings,0,"§9INFO");
					plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.openInventory(jPhoneMain.settings),1L);
					//p.sendMessage("§7Type /jphone claim to claim");
					break;
				case OAK_SIGN:
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
				case BONE: {
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName("§3jWrench");
					item.setItemMeta(meta);
					item.setType(Material.BONE);
					p.getInventory().setItemInMainHand(item);
					break;
				} case REDSTONE_LAMP:
					nbti.setBoolean("state",false);
					ItemMeta meta = item.getItemMeta();
					ItemStack newItem = nbti.getItem();
					meta.setLore(new ArrayList<>(Collections.singletonList("§cPhone is switched off.")));
					newItem.setItemMeta(meta);
					p.sendMessage("§7Phone has been switched off.");
					p.getInventory().setItemInMainHand(newItem);

					break;
				case GOLD_NUGGET:
					Bukkit.getScheduler().runTaskLater(plugin, () -> {
						Inventory inv = new InventoryStore(plugin,"keychain_" + p.getName(),9*3).loadInv();
						p.openInventory(inv);
					},1L);
					break;
				case NOTE_BLOCK:
					Util.createDisplay(p,Material.WRITTEN_BOOK,jPhoneMain.stunes,0,"§9INFO");
					Util.createDisplay(p,Material.MUSIC_DISC_MELLOHI,jPhoneMain.stunes,2,"§9Legacy Player");
					plugin.getServer().getScheduler().runTaskLater(plugin, () -> p.openInventory(jPhoneMain.stunes),1L);
					//play a tune
					break;
				default:
					p.sendMessage("That item is not configured correctly.");
			}
			p.closeInventory(); //close it
		}else if(view.getTitle().equals("§9Steves Tunes Player")) {
			event.setCancelled(true);
			switch(clicked.getType()) {
				case MUSIC_DISC_MELLOHI:
					p.performCommand("music");
					break;
				default:
					p.sendMessage("§7The §3Steves Tunes §7player is currently §cin development");
			}
		}else if(view.getTitle().startsWith("keychain")) {
			String[] phonenames = {
					"§3jPhone",
					jPhoneMain.phoneName,
					"§fjLight",
					"§3jPhone Development Phone",
					"§3jPhone XL"
			};
			ItemMeta meta = clicked.getItemMeta();
			if(meta.hasDisplayName()) {
				for (String phonename : phonenames) {
					if(meta.getDisplayName().startsWith(phonename)) {
						event.setCancelled(true);
					}
				}

			}
		}else if(view.getTitle().startsWith("§9jPhone")) {
			event.setCancelled(true);
			if(clicked != null) {
				switch (clicked.getType()) {
					case WRITTEN_BOOK: {
						p.sendMessage("§cSettings is currently in development. Check back later!");
						break;
					}
					default:
						p.sendMessage("§cUnknown item. ");
				}
			}
		}
	}
	@EventHandler
	public void onInvClose(InventoryCloseEvent e) {
		Inventory inv = e.getInventory();
		InventoryView view = e.getView();
		if(view.getTitle().startsWith("keychain")) {
			try {
				new InventoryStore(plugin,view.getTitle(),9*3).saveInv(inv);
			} catch (IOException e1) {
				e.getPlayer().sendMessage("§7Failed to save your KeyChain: §e" + e1.toString());
			}
		}
	}
}
