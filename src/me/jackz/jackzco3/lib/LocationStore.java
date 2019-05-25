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
import org.bukkit.Location;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;

public class LocationStore {
    private final Main plugin;
    private File file;
    /*
        store a new one:
        new LocationStore(plugin)
        .getString(Location)
        .getBoolean(Location)
        .setBoolean(Location,boolean)
        .setString(Location,String)
     */
    public LocationStore(Main plugin) {
        this.plugin = plugin;
	    file = new File(plugin.getDataFolder().toString() + "/data/location.store");
    }
    public LocationStore(Main plugin, String filename) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder().toString() + "/data/" + filename);
    }

    public boolean getBoolean(Location loc) {
        try(FileReader fr = new FileReader(file)){
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(fr);
            Object output = obj.get(String.format("%s,%s,%s",loc.getX(),loc.getY(),loc.getZ()));
            if(output == null) {
                return false;
            }
            return Boolean.parseBoolean(output.toString());
        }catch(Exception ex) {
            plugin.getLogger().log(Level.WARNING,"getBoolean",ex);
            return false;
        }
    }
    public String getString(Location loc) {
        JSONParser parser = new JSONParser();
        try(FileReader fr = new FileReader(file)){
            JSONObject obj = (JSONObject) parser.parse(fr);
            Object output = obj.get(String.format("%s,%s,%s",loc.getX(),loc.getY(),loc.getZ()));
            if(output == null) {
                return null;
            }
            return output.toString();
        }catch(Exception ex) {
            plugin.getLogger().log(Level.WARNING,"getString",ex);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public void setString(Location loc,String value) {
        try{
            JSONObject obj = getObject();
            if(obj == null) return; //todo: error
            obj.put(String.format("%s,%s,%s",loc.getX(),loc.getY(),loc.getZ()), value);
            FileWriter writer = new FileWriter(file);
            writer.write(obj.toJSONString());
            writer.flush();
            writer.close();
        }catch(Exception e) {
            plugin.getLogger().warning(e.toString());
        }
    }
    @SuppressWarnings("unchecked")
    public void setBoolean(Location loc,boolean value) {
        setString(loc, String.valueOf(value));
    }

	public void deleteValue(Location loc) {
		try{
			JSONObject obj = getObject();
			if(obj == null) return; //todo: error
			obj.remove(String.format("%s,%s,%s",loc.getX(),loc.getY(),loc.getZ()));
			FileWriter writer = new FileWriter(file);
			writer.write(obj.toJSONString());
			writer.flush();
			writer.close();
		}catch(Exception e) {
			plugin.getLogger().warning(e.toString());
		}
	}
    @SuppressWarnings("unchecked")
	private JSONObject getObject() {
        try {
            JSONObject obj;
            if(!file.exists()) {
                //throw new FileNotFoundException();
                obj = new JSONObject();
            }else{
                JSONParser parser = new JSONParser();
                obj = (JSONObject) parser.parse(new FileReader(file));
            }
            return obj;
        }catch(Exception e) {
            plugin.getLogger().warning(e.toString());
            return null; //todo: error
        }
    }
	/*
	getReader
	saveWrite
	 */
}
