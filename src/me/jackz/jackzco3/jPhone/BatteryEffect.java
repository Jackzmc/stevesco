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
import me.jackz.jackzco3.lib.LocationStore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BatteryEffect implements Runnable {
	private final Main plugin;
	BatteryEffect(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		int radius = 5;
		for(Player p : Bukkit.getOnlinePlayers()) {
			for (int x = radius; x >= -radius; x--) {
				for (int y = radius; y >= -radius; y--) {
					for (int z = radius; z >= -radius; z--) {
						Block bk = p.getLocation().getBlock().getRelative(x, y, z);
						if (bk.getType().equals(Material.PISTON_BASE)) {
							if (new LocationStore(plugin).getBoolean(bk.getLocation())) {
								Location loc = new me.jackz.jackzco3.lib.Util().getCenterLocation(bk.getLocation());
								p.spawnParticle(Particle.ENCHANTMENT_TABLE, loc.add(0, 3, 0), 10, 0.5, 5, 0.5);
							}
						}
					}
				}
			}
		}
	}
}