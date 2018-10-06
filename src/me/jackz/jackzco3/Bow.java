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

package me.jackz.jackzco3;

import de.Herbystar.TTA.TTA_Methods;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.Random;

public class Bow implements Listener {
	private Main plugin;
	Bow(Main plugin) { this.plugin = plugin; }

	private Random random = new Random();
	private double randomnum() {
		return random.nextBoolean() ? random.nextDouble() : -random.nextDouble();
	}
	private double randomSpread() {
		return random.nextBoolean() ? random.nextInt() : -random.nextInt();
	}
	@EventHandler
	public void onBowShoot(EntityShootBowEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			/*for(int i=0;i<1;i++) {
				Vector v = new Vector(randomSpread(), randomSpread(), randomSpread());
				Vector finalV= e.getProjectile().getLocation().getDirection().multiply(v);
				p.launchProjectile(Arrow.class,finalV);
			}*/

			if(random.nextFloat() < .1f) {
				TTA_Methods.sendActionBar(p,"Hey, why are you shooting?");
				e.setCancelled(true);
			}
 			/*Entity ent = p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
			e.getProjectile().setGlowing(true);
			e.getProjectile().addPassenger(ent);*/
		}
	}
}
