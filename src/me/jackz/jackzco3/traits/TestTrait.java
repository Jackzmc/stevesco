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

package me.jackz.jackzco3.traits;

import me.jackz.jackzco3.Main;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

@TraitName("testtrait")
public class TestTrait extends Trait {
	public TestTrait() {
		super("testtrait");
		plugin = JavaPlugin.getPlugin(Main.class);
	}

	Main plugin = null;
	boolean SomeSetting = false;
	@Persist("mysettingname") boolean automaticallyPersistedSetting = false;
	// Here you should load up any values you have previously saved (optional).
	// This does NOT get called when applying the trait for the first time, only loading onto an existing npc at server start.
	// This is called AFTER onAttach so you can load defaults in onAttach and they will be overridden here.
	// This is called BEFORE onSpawn, npc.getBukkitEntity() will return null.
	public void load(DataKey key) {
		SomeSetting = key.getBoolean("SomeSetting", false);
	}

	// Save settings for this NPC (optional). These values will be persisted to the Citizens saves file
	public void save(DataKey key) {
		key.setBoolean("SomeSetting",SomeSetting);
	}

	// An example event handler. All traits will be registered automatically as Bukkit Listeners.
	@EventHandler
	public void click(net.citizensnpcs.api.event.NPCClickEvent event){
		//Handle a click on a NPC. The event has a getNPC() method.
		//Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!
		Player p = event.getClicker();
		p.sendMessage("§3" + event.getNPC().getFullName() + "§7: Dont you ever click on me again! ");
	}

	// Called every tick
	@Override
	public void run() {
	}

	//Run code when your trait is attached to a NPC.
	//This is called BEFORE onSpawn, so npc.getBukkitEntity() will return null
	//This would be a good place to load configurable defaults for new NPCs.
	@Override
	public void onAttach() {
		plugin.getServer().getLogger().info(npc.getName() + " has been assigned MyTrait!");
	}

	// Run code when the NPC is despawned. This is called before the entity actually despawns so npc.getBukkitEntity() is still valid.
	@Override
	public void onDespawn() {
		plugin.getServer().getLogger().info("onDespawn " + npc.isSpawned() + " : " + npc.getName());
		if (npc.isSpawned()) {
			Entity ent = npc.getEntity();
			ent.setGlowing(false);
		}
	}

	//Run code when the NPC is spawned. Note that npc.getBukkitEntity() will be null until this method is called.
	//This is called AFTER onAttach and AFTER Load when the server is started.
	@Override
	public void onSpawn() {
		if (npc.isSpawned()) {

			npc.getNavigator().setTarget(Bukkit.getServer().getOnlinePlayers().iterator().next().getLocation());
		}

	}

	//run code when the NPC is removed. Use this to tear down any repeating tasks.
	@Override
	public void onRemove() {
	}

}