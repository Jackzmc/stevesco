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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class jPhoneMain implements Listener {
	private final Main plugin;
	static Inventory keychain = Bukkit.createInventory(null, 9, "jPhone Keychain");
	static Inventory appswitcher = Bukkit.createInventory(null, 45, "§9jPhone App Switcher");
	static Inventory stunes = Bukkit.createInventory(null,54,"§9Steves Tunes Player");

	static String phoneName = "§3jPhone";

	public jPhoneMain(Main plugin) {
		this.plugin = plugin; // Store the plugin in situations where you need it.
		plugin.getServer().getPluginManager().registerEvents(new ChatListener(plugin,this),plugin);
		plugin.getServer().getPluginManager().registerEvents(new InteractEvent(plugin,this),plugin);
		plugin.getServer().getPluginManager().registerEvents(new InventoryClick(plugin,this),plugin);
		plugin.getServer().getPluginManager().registerEvents(new BlockEvent(plugin,this),plugin);
		plugin.getServer().getPluginManager().registerEvents(new KeyChainEvents(plugin),plugin);
		plugin.getCommand("jphone").setExecutor(new Command(plugin,this));
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new BatteryEffect(plugin),0L,10L);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new BatteryTick(),0L,30 * 20);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,new Timing(),0L,5L);
		//plugin.getServer().getScheduler().runTaskTimer(plugin, new BatteryTick(), 0L,200L);
	}

	public ItemStack givePhone(Player p, String name, boolean locked) {
		NBTItem nbt = ItemNBTAPI.getNBTItem(new ItemStack(Material.TRIPWIRE_HOOK));
		nbt.setInteger("battery",100);
		if(!locked) nbt.setString("owner",p.getUniqueId().toString());
		nbt.setBoolean("terminal",false);
		nbt.setBoolean("firstuse",true);
		nbt.setBoolean("locked",locked);
		nbt.setBoolean("state",true);
		ItemStack item = nbt.getItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}

	Inventory getAppSwitcher(Player p) {
		me.jackz.jackzco3.lib.Util util = new me.jackz.jackzco3.lib.Util();
		util.createDisplay(p, Material.BOOK_AND_QUILL, jPhoneMain.appswitcher, 10, "&9Settings", "&7Configure your phone");
		util.createDisplay(p, Material.SIGN, jPhoneMain.appswitcher, 12, "&9Terminal", "&7Open the console/terminal");
		util.createDisplay(p, Material.TORCH, jPhoneMain.appswitcher, 14, "&9Flashlight", "&7Illuminate the world!|&7(Left click to turn off)");
		util.createDisplay(p, Material.REDSTONE_LAMP_OFF, jPhoneMain.appswitcher, 16, "§9Power off", "§7Turn the phone off");
		util.createDisplay(p, Material.NOTE_BLOCK, jPhoneMain.appswitcher, 28, "§9Steves Tunes", "§7Your music, the way you want");
		util.createDisplay(p, Material.BONE,jPhoneMain.appswitcher,30,"§9Wrench","§7Rotate inventories, and blocks.");
		util.createDisplay(p, Material.GOLD_NUGGET,jPhoneMain.appswitcher,32,"§9jKeychain","§7Your private, secure, remote storage");
		return appswitcher;
	}
}
