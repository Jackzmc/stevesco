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

import me.jackz.jackzco3.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class jPhoneMain implements Listener {
	final Main plugin;
	static Inventory keychain = Bukkit.createInventory(null, 9, "Inventory");
	static Inventory appswitcher = Bukkit.createInventory(null, 36, "§4jPhone App Switcher");

	String phoneName = "§3jPhone";

	public jPhoneMain(Main plugin) {

		this.plugin = plugin; // Store the plugin in situations where you need it.
		plugin.getServer().getPluginManager().registerEvents(new ChatListener(plugin,this),plugin);
		plugin.getServer().getPluginManager().registerEvents(new InteractEvent(plugin,this),plugin);
		plugin.getCommand("jphone").setExecutor(new Command(plugin,this));
	}


}
