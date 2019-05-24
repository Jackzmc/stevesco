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

import de.tr7zw.itemnbtapi.ItemNBTAPI;
import de.tr7zw.itemnbtapi.NBTItem;
import me.jackz.jackzco3.Main;
import me.jackz.jackzco3.lib.Util;
import me.jackz.jackzco3.lib.jTower;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import static java.util.stream.Collectors.toMap;

public class jPhoneMain implements Listener {
	private final Main plugin;
	static Inventory keychain = Bukkit.createInventory(null, 9, "jPhone Keychain");
	static Inventory appswitcher = Bukkit.createInventory(null, 45, "§9jPhone App Switcher");
	static Inventory stunes = Bukkit.createInventory(null,54,"§9Steves Tunes Player");
	public static final String JCHARGER_VERIFY = "VphPyIZn";

	static String phoneName = "§3jPhone";

	 public jPhoneMain(Main plugin) {
		this.plugin = plugin; // Store the plugin in situations where you need it.
		 plugin.getLogger().info("[jPhoneMain] Loading events, commands, and managers...");
		plugin.getServer().getPluginManager().registerEvents(new ChatListener(plugin,this),plugin);
		plugin.getServer().getPluginManager().registerEvents(new InteractEvent(plugin,this),plugin);
		plugin.getServer().getPluginManager().registerEvents(new InventoryClick(plugin,this),plugin);
		plugin.getServer().getPluginManager().registerEvents(new BlockEvent(plugin,this),plugin);
		//plugin.getServer().getPluginManager().registerEvents(new KeyChainEvents(plugin),plugin);
		plugin.getCommand("jphone").setExecutor(new Command(plugin,this));
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new BatteryEffect(plugin),0L,10L);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new BatteryTick(),0L,30 * 20);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,new Timing(),0L,5L);
		 plugin.getLogger().info("[jphoneMain] Loaded events, commands managers successfully.");
	}

	static public ItemStack givePhone(Player p, String name, boolean locked) {
		NBTItem nbt = ItemNBTAPI.getNBTItem(new ItemStack(Material.TRIPWIRE_HOOK));
		nbt.setInteger("battery",100);
		if(!locked) nbt.setString("owner",p.getUniqueId().toString());
		nbt.setBoolean("terminal",false);
		nbt.setBoolean("firstuse",true);
		nbt.setBoolean("locked",locked);
		nbt.setBoolean("jphone",true);
		nbt.setBoolean("state",true);
		nbt.setString("provider","jService");
		nbt.setString("txtsound","bell");
		ItemStack item = nbt.getItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}

	static Inventory getAppSwitcher(Player p) {
		me.jackz.jackzco3.lib.Util util = new me.jackz.jackzco3.lib.Util();
		Util.createDisplay(p, Material.WRITABLE_BOOK, jPhoneMain.appswitcher, 10, "&9Settings", "&7Configure your phone");
		Util.createDisplay(p, Material.OAK_SIGN, jPhoneMain.appswitcher, 12, "&9Terminal", "&7Open the console/terminal");
		Util.createDisplay(p, Material.TORCH, jPhoneMain.appswitcher, 14, "&9Flashlight", "&7Illuminate the world!|&7(Left click to turn off)");
		Util.createDisplay(p, Material.REDSTONE_LAMP, jPhoneMain.appswitcher, 16, "§9Power off", "§7Turn the phone off");
		Util.createDisplay(p, Material.NOTE_BLOCK, jPhoneMain.appswitcher, 28, "§9Steves Tunes", "§7Your music, the way you want");
		Util.createDisplay(p, Material.BONE,jPhoneMain.appswitcher,30,"§9Wrench","§7Rotate inventories, and blocks.");
		Util.createDisplay(p, Material.GOLD_NUGGET,jPhoneMain.appswitcher,32,"§9jKeychain","§7Your private, secure, remote storage");
		return appswitcher;
	}

	HashMap<String,Double> getTowers(Location fromLocation) throws NullPointerException{
		HashMap<String, Double> map = new HashMap<>();
		File towers = new File(plugin.getDataFolder().toString() + "/towers/");
		File[] phoneLists = towers.listFiles();
		if (phoneLists != null) {
			for (File phoneList : phoneLists) {
				try {
					if (phoneList.isFile()) {
						if (!phoneList.getName().contains("tower")) continue;
						JSONParser parser = new JSONParser();
						JSONObject obj = (JSONObject) parser.parse(new FileReader(phoneList));
						jTower tower = new jTower(obj, fromLocation.getWorld());
						map.put(tower.name, fromLocation.distance(tower.location));
					}
				} catch (Exception e) {
					Bukkit.getLogger().log(Level.WARNING,"getTowers issue reading",e);
				}
			}
			return map;
		}else{
			return null;
		}
	}

	HashMap<String,Double> getSortedTowers(Location loc) {
		HashMap<String, Double> map = getTowers(loc);
		return map.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
	}
	boolean isInTowerRange(Location loc) {
		HashMap<String, Double> map = getSortedTowers(loc);
		double range = map.entrySet().iterator().next().getValue();
		return (range < 650);
	}
	static String getTowerQuality(Double distance) {
		if (distance < 25)  return "§3NASA Quality";
		if (distance < 150) return "§2Excellent";
		if (distance < 250) return "§aDecent";
		if (distance < 350) return "§aGreat";
		if (distance < 500) return "§eOK";
		if (distance < 575) return "§cPoor";
		if (distance < 650) return "§cHorrible";
		return "§4Unreachable";
	}
	public static ItemStack getIntroBook() {
		String[] pages = {
				"Thank you for choosing jPhone! To get started you will want to claim the phone. Shift+rightclick, holding the phone to open your appswitcher. You will want to turn on terminal mode. Terminal allows you to run commands to do certain tasks. ",
				"You will want to type in chat with terminal mode 'help' to get started You can view a list of commands in terminal mode by typing 'ccommands [page #]'. Try texting someone or claiming your device!"
		};
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();
		meta.setAuthor("StevesCo");
		meta.setTitle("jPhone Docs");
		meta.addPage(pages);
		book.setItemMeta(meta);
		return book;
	}
	public static ItemStack getWarrantyBook() {
		String[] pages = {
				"Thank you for choosing jPhone! Warranty Information is disclosed in here ",
				"You will want to type in chat with terminal mode 'help' to get started You can view a list of commands in terminal mode by typing 'ccommands [page #]'. Try texting someone or claiming your device!"
		};
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();
		meta.setAuthor("StevesCo");
		meta.setTitle("jPhone Warranty Information");
		meta.addPage(pages);
		book.setItemMeta(meta);
		return book;
	}
}
