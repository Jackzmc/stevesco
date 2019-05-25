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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.jackz.jackzco3.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class jTowerManager {
    static Main plugin;
    static Gson gson;
    final File tower_data;
//todo: case insensitive, collosion check
    public HashMap<String, jTower> towers = new HashMap<>();
    public jTowerManager(Main plugin) throws IOException {
        jTowerManager.plugin = plugin;
        gson = new Gson();
        tower_data = new File(plugin.getDataFolder() + "/data/towers.json");
        this.load();
        plugin.getLogger().info("[jTowerManager] Loaded " + towers.size() + " towers");
    }
    public jTower get(String name) {
        return towers.get(name);
    }
    public void add(jTower tower) throws IOException {
        this.add(tower,false);
    }
    public void add(jTower tower, boolean save) throws IOException {
        towers.put(tower.name,tower);
        if(save) this.save();
    }
    public void remove(String name,boolean save) {
        if(towers.get(name) == null) return;
        towers.remove(name);
        if(save) this.save();
    }
    public void remove(jTower tower, boolean save) {
        remove(tower.name,save);
    }
    public void save()  {
        Iterator iterator = towers.values().iterator();
        JsonArray array  = new JsonArray();
        while (iterator.hasNext()) {
            jTower tower = (jTower) iterator.next();
            JsonObject obj = new JsonObject();
            obj.addProperty("name",tower.name);
            obj.addProperty("x",tower.location.getX());
            obj.addProperty("y",tower.location.getY());
            obj.addProperty("z",tower.location.getZ());
            array.add(obj);
        }
        FileWriter filewriter;
        try {
            filewriter = new FileWriter(tower_data);
            String json = (array.size() > 0) ? gson.toJson(array) : "[]";
            filewriter.write(json);
            filewriter.flush();
            filewriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void load() throws IOException {
        if(tower_data.exists() && tower_data.isFile() && tower_data.length() > 0) {
            JsonParser parser = new JsonParser();
            //JSONParser parser = new JSONParser();
            JsonArray array = (JsonArray) parser.parse(new FileReader(tower_data));
            Iterator iterator = array.iterator();
            while(iterator.hasNext()) {
                JsonObject obj = (JsonObject) iterator.next();

                String name = obj.get("name").getAsString();
                double x = obj.get("x").getAsDouble();
                double y = obj.get("y").getAsDouble();
                double z = obj.get("z").getAsDouble();
                String world = obj.has("world")?obj.get("world").getAsString() : "world";
                Location loc = new Location(Bukkit.getWorld(world),x,y,z);
                jTower tower = new jTower(name,loc);
                towers.put(name,tower);
            }
            plugin.getLogger().info(towers.keySet().toString());
        }else{
            //throw new DataFileNotFoundException("towers.store");
            plugin.getLogger().info("[jTowerManager] towers.json not found, creating new file");
            FileWriter filewriter = new FileWriter(tower_data);
            filewriter.write("[]");
            filewriter.flush();
            filewriter.close();
        }
    }
}

