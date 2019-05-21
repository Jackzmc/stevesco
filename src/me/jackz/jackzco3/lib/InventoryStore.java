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

package me.jackz.jackzco3.lib;

import me.jackz.jackzco3.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class InventoryStore {
	private Main plugin;
	private String name;
	private int slots;
	public InventoryStore(Main plugin, String name, int slots) {
		this.name = name;
		this.slots = slots;
		this.plugin = plugin;
	}

	public int getFillSize(Inventory inv) {
		return inv.getSize();
	}
	public int getFillSize() {
		File invFile = new File (plugin.getDataFolder(), "inventories/" + name + ".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(invFile);
		Set<String> keys = config.getKeys(false);
		return keys.size();

	}
	public Inventory loadInv() {
		Inventory inv = Bukkit.createInventory(null,slots,name);
		inv.clear();
		File invFile = new File (plugin.getDataFolder(), "inventories/" + name + ".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(invFile);
		for(String keys : config.getKeys(false)) {
			int slot = Integer.parseInt(keys);
			ItemStack item = config.getItemStack(keys);
			inv.setItem(slot,item);
		}
		return inv;
	}
	public void saveInv(Inventory inventory) throws IOException {
		File invFile = new File (plugin.getDataFolder(), "inventories/" + name + ".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(invFile);
		for(int i = 0 ; i < inventory.getSize() ; i++) {
			if(inventory.getItem(i) != null) {
				config.set(Integer.toString(i), inventory.getItem(i));
			}else{
				config.set(Integer.toString(i),null);
			}
		}
		config.save(invFile);
	}
}
