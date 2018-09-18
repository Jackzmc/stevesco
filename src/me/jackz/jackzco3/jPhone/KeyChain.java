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
import me.jackz.jackzco3.lib.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

class KeychainEvents implements Listener {
	private final Main plugin;
	KeychainEvents(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getHand() == EquipmentSlot.HAND) {
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(new Util().checkItem(e.getItem(), Material.BLAZE_ROD,"§6jKeychain Creator")) {
					//todo: check if inventory
					//todo: convert name
					e.setCancelled(true);
					boolean status = new KeychainStorage(plugin,p).addKeyChain(e.getClickedBlock().getLocation());
					if(status) {
						p.sendMessage("§aSuccessfully added to storage");
					}else{
						p.sendMessage("§cFailed to add storage.");
					}
				}
			}
		}
	}
}

class KeychainStorage {
	private final Main plugin;
	private final File file;
	//private Map<String,Location> keychainMap = new HashMap<>(); //store in file instead of hashmap? | or in main.java
	/*
	each player gets registered a new storage

	grab idMap (below) on init
	get the location by accessing empty_keychains.store:
		by: new LocationStore(blah,blah).getString('empty-keychains.store'), convert string -> location
		[
			{name:k001,loc:string}
			{name:k002,loc:string}
			{name:k003,loc:string}
		]
	delete object once claimed
	get first element emptykeychainsarray[0]

	store map keychainMap in JSON :
	{
		"playerUUID":"location (x,y,z only)"
	}
	 */
	KeychainStorage(Main plugin, Player p) {
		file = new File(plugin.getDataFolder() + "/data/keychains.list");
		this.plugin = plugin;
	}
	public Location getKeychain(Player p) {
		if(plugin.keychainMap.containsKey(p.getUniqueId().toString())) {
			return plugin.keychainMap.get(p.getUniqueId().toString());
		}else{
			JSONArray keychains = getStorage();
			if(keychains != null && keychains.size() > 0) {
				String strLocation = keychains.get(0).toString();
				double[] ldoubles = Arrays.stream(strLocation.split(",")).mapToDouble(Double::parseDouble).toArray();
				Location loc = new Location(p.getWorld(),ldoubles[0],ldoubles[1],ldoubles[2]);
				keychains.remove(0);
				saveArray(keychains);
				plugin.keychainMap.put(p.getUniqueId().toString(),loc);
				return loc;
			}else{
				return null;
			}

		}
		//use that as chest
		//maybe add a check if it IS a location
		//new LocationStore(plugin,"keychain").get
		//fetch from file, based on player
		//check ID based off player (store map?)
		//then get location from ID (map)
	}
	private Map<String,Location> getMap() {
		return null;
	}
	private void storeMap() {

	}

	@SuppressWarnings("unchecked")
	boolean addKeyChain(Location loc) {
		try {
			JSONArray keychain = getStorage();
			if(keychain == null) return false; //storage error?
			keychain.add(String.format("%f,%f,%f",loc.getX(),loc.getY(),loc.getZ()));
			saveArray(keychain);
			return true;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean isAdded(Location loc) {
		return false; //TODO: todo this
	}
	private void saveArray(JSONArray array) {
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(array.toJSONString());
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public JSONArray getStorage() {
		JSONArray keychainstore;
		if(!file.exists())  {
			createDefaultObject();
			keychainstore = new JSONArray();
		}else{
			try{
				JSONParser parser = new JSONParser();
				FileReader freader = new FileReader(file);
				keychainstore = (JSONArray) parser.parse(freader);
				//should be printed below?
			}catch (Exception e) {
				plugin.getLogger().warning("KeyChain#getStorage failed: " + e.toString());
				return null;
			}
		}
		return keychainstore;
	}
	private void createDefaultObject() {
		saveArray(new JSONArray());
	}
}
