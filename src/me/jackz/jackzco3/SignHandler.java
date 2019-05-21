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

import me.jackz.jackzco3.jPhone.jPhoneMain;
import me.jackz.jackzco3.lib.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SignHandler implements Listener {
	private final Main plugin;

	SignHandler(Main plugin) { this.plugin = plugin; }
	private static Inventory jstore = Bukkit.createInventory(null, 36, "§cJackzCo Store");

	@EventHandler
	public void onSignClick(PlayerInteractEvent e) {
		Block block = e.getClickedBlock();
		Player p = e.getPlayer();
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getHand() != EquipmentSlot.HAND) return; //check if rightclicking a block with main hand
		if(block == null) return; //make sure block not null

		if(block.getType() == Material.OAK_SIGN || block.getType() == Material.OAK_WALL_SIGN) { //todo: use tag.signs
			Sign sign = (Sign) block.getState();
			String[] lines = sign.getLines();
			if(lines[0].startsWith("[jGate")) {
				p.sendMessage(String.join(",",sign.getLines()));
			}else if(lines[0].contains("[jStore]")) {
				getStore(p);

			}
		}

	}
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		InventoryView view = e.getView();
		if(e.getClickedInventory() != null && view.getTitle().equals("§cJackzCo Store")) {
			Player p = (Player) e.getWhoClicked();
			ItemStack item = e.getCurrentItem();
			e.setCancelled(true);
			if(item == null) {
				//do nothing?
				return;
			}
			jPhoneMain jphone = new jPhoneMain(plugin);
			switch(e.getCurrentItem().getType()) {
				case TRIPWIRE_HOOK:
					switch(e.getCurrentItem().getItemMeta().getDisplayName()) { //get NBT?
						case "§fjPhone 2": {
							p.getInventory().addItem(jphone.givePhone(p,"§3jPhone 2",false));
							p.getInventory().addItem(new me.jackz.jackzco3.lib.Util().getIntroBook());
							break;
						} case "§fjPhone 2X": {
							p.getInventory().addItem(jphone.givePhone(p,"§3jPhone 2X",false));
							p.getInventory().addItem(new me.jackz.jackzco3.lib.Util().getIntroBook());
							break;
						} default:
							p.sendMessage(e.getCurrentItem().getItemMeta().getDisplayName());
							p.sendMessage("§cInvalid phone");
					}
					break;
				case PISTON:
					ItemStack jcharger = new ItemStack(Material.PISTON);
					ItemMeta meta = jcharger.getItemMeta();
					meta.setDisplayName("§fjCharger");
					jcharger.setItemMeta(meta);

					p.getInventory().addItem(jcharger);
					p.sendMessage("§aThank you for buying the jCharger!");
					p.sendMessage("§7Place the piston on a gold block to set it up.");
					break;
				case AIR:
					//dont do anything
					break;
				default:
					p.sendMessage("§cSorry, that item is not setup");
					p.closeInventory();
			}
		}
	}
	Inventory getStore(Player p) {
		Util util = new Util();
		util.createDisplay(p,Material.TRIPWIRE_HOOK,jstore,9+1,"§fjPhone 2","§a$Unknown");
		util.createDisplay(p,Material.TRIPWIRE_HOOK,jstore,9+3,"§fjPhone 2X","§a$Unknown+$100");
		util.createDisplay(p,Material.PISTON,jstore,9+5,"§fjCharger","§7Ultra fast charger");
		p.openInventory(jstore);
		return jstore;
	}
}
