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

package me.jackz.jackzco3.lib;

import me.jackz.jackzco3.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.simple.JSONObject;

import java.io.FileWriter;

public class jTower {
	private String name;
	private Location location;
	public jTower(String name, Location location) {
		this.name = name;
		this.location = location;
	}
	public jTower(JSONObject obj, World world) {
		this.name = (String) obj.get("name");
		this.location = new Location(world, (Double) obj.get("locX"),  (Double) obj.get("locY"),  (Double) obj.get("locZ"));
	}

	@SuppressWarnings("unchecked")
	public void toJSON(Main plugin) {
		try (FileWriter file = new FileWriter(plugin.getDataFolder().toString() + "/towers/" + name + ".tower")){
			JSONObject obj = new JSONObject();
			obj.put("name", name);
			obj.put("locX", location.getX());
			obj.put("locY", location.getY());
			obj.put("locZ", location.getZ());
			file.write(obj.toJSONString());
		}catch(Exception e) {
			plugin.getLogger().warning("Writing to JSON failed.");
			plugin.getLogger().warning(e.toString());
		}
	}

	public String getQualityTerm(Double distance) {
		if (distance < 100) return "§2Excellent";
		if (distance < 250) return "§aGreat";
		if (distance < 400) return "§eOK";
		if (distance < 550) return "§cPoor";
		if (distance < 600) return "§cHorrible";
		return "§4Unreachable";
	}
}
