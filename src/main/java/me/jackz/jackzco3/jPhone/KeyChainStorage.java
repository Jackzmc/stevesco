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

import me.jackz.jackzco3.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class KeyChainStorage {
	private final Main plugin;
	private final File file;
	private final File jsonMap;
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
	public KeyChainStorage(Main plugin) {
		file = new File(plugin.getDataFolder() + "/data/keychains.list");
		jsonMap = new File(plugin.getDataFolder() + "/data/keychain.map");
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
				//saveMap(); //stored on onDisable now
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
	/*
	map:
	{
		"uuid":"string loc"
	}
	 */
	/*private JSONObject getMap2(Player p) {
		if(!file.exists())  {
			saveObject(new JSONObject());
			return null;
		}else{
			try{
				JSONParser parser = new JSONParser();
				FileReader freader = new FileReader(file);
				JSONObject obj = (JSONObject) parser.parse(freader);
				return obj;
			}catch (Exception e) {
				plugin.getLogger().warning("KeyChain#getMap failed: " + e.toString());
				return null;
			}
		}
	}*/
	public Map<String,Location> loadMap(World world) {
		if(!jsonMap.exists())  {
			plugin.getLogger().warning("jsonmap doesnt exist, creating blank");
			Map<String,Location> map = new HashMap<>();
			saveMap(map);
			return map;
		}else{
			try{
				JSONParser parser = new JSONParser();
				FileReader freader = new FileReader(jsonMap);
				JSONObject obj = (JSONObject) parser.parse(freader);
				Map<String,Location> map = new HashMap<>();
				if(obj.size() > 0) {
					for (Object o : obj.keySet()) {
						String key = (String) o;
						String value = obj.get(key).toString();
						double[] ldoubles = Arrays.stream(value.split(",")).mapToDouble(Double::parseDouble).toArray();
						Location loc = new Location(world, ldoubles[0], ldoubles[1], ldoubles[2]);
						map.put(key, loc);
					}
				}
				return map;
				//should be printed below?
			}catch (Exception e) {
				plugin.getLogger().warning("KeyChain#loadMap failed: " + e.toString());
				return null;
			}
		}
	}
	@SuppressWarnings("unchecked")
	public void saveMap(Map<String,Location> map) {
		try {
			FileWriter fw = new FileWriter(jsonMap);
			JSONObject root = new JSONObject();
			if(map.size() > 0) {
				for (Object o : map.keySet()) {
					String playerID = (String) o; //UUID
					Location loc = map.get(playerID);
					root.put(playerID, String.format("%f,%f,%f", loc.getX(), loc.getY(), loc.getZ()));
				}
			}
			fw.write(root.toJSONString());
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			plugin.getLogger().log(Level.WARNING,"KeyChain#saveMap",e);
		}
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
		if(!file.exists())  {
			saveArray(new JSONArray());
			return new JSONArray();
		}else{
			try{
				JSONParser parser = new JSONParser();
				FileReader freader = new FileReader(file);
				return (JSONArray) parser.parse(freader);
				//should be printed below?
			}catch (Exception e) {
				plugin.getLogger().warning("KeyChain#getStorage failed: " + e.toString());
				return null;
			}
		}
	}
}
