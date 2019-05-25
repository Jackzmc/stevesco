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

package me.jackz.jackzco3;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.jackz.jackzco3.jPhone.KeyChainStorage;
import me.jackz.jackzco3.jPhone.jPhoneMain;
import me.jackz.jackzco3.lib.Config;
import me.jackz.jackzco3.lib.jTower;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Main extends JavaPlugin {
    private static Plugin plugin;

    public static String LATEST_VERSION = "0.0.0";
    public static String JOS_VERSION = "2.4.0-beta";
    public static String TERMINAL_VERSION = "1.5.0-beta";
    static String jackzco_prefix = "§3JackzCo§6>§r ";
    public Map<String,Location> keychainMap = new HashMap<>();
    private FileConfiguration config;

    @Override
    public void onEnable() {
        keychainMap = new KeyChainStorage(this).loadMap(Bukkit.getWorld("overworld"));
        plugin = this;
        new jPhoneMain(this);
        LATEST_VERSION = this.getDescription().getVersion();
        getLogger().info("Loading Main Commands");
        this.getCommand("jackzco").setExecutor(new jCommandLoader(this));
        this.getCommand("getid").setExecutor(new DoorControlCmd(this));
        this.getCommand("jboss").setExecutor(new jBoss(this));
        //this.getCommand("jphone").setExecutor(new jPhoneMain(this));

        //this.getCommand("jphone").setExecutor(new jPhoneMain(this));
        getLogger().info("Loading Main Event Managers");
        registerEvents(this,
                new JoinEvent(this),
                new MainListener(this),
                new DoorControlEvent(this),
                new Wand(this),
                new MessageHandler(this),
		        new MoveHandler(this),
                new SignHandler(this),
                new PlayerInteractHandler(this)
        );
        //new LocVarLib(this);
        getLogger().info("Loading Main Misc");
        config = new Config().setupConfig(this);
        loadTowers();
        if(config.getBoolean("updatecheck.enabled")) {
	        getServer().getScheduler().runTaskTimer(this, this::checkForUpdates,0L,config.getInt("updatecheck.interval")*20L);
        }
    }

    @Override
    public void onDisable() {
        //CitizensAPI.getTraitFactory().deregisterTrait(TraitInfo.create(TestTrait.class));
       // new KeyChainStorage(this).saveMap(keychainMap); //hopefully saves map
		//getServer().getScheduler().cancelAllTasks();
        getServer().getScheduler().cancelTasks(this);
        getLogger().info("Successfully disabled all tasks");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkForUpdates() {
    	try {
		    File newJar = new File(getDataFolder() + "/jackzco.jar");
		    if(newJar.exists()) {
		    	File dest = new File(plugin.getDataFolder() + "../jackzco.jar");
		    	if(dest.exists()) {
		    		dest.delete();
			    }
			    newJar.renameTo(new File(plugin.getDataFolder() + "/../jackzco.jar"));
                getLogger().info("[UpdateChecker] Detected jackzco.jar, reloading...");
			    Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "plugman reload JackzCo3"), 20L);
			    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    getLogger().info("[UpdateChecker] Updating & reloading JackzCo");
                    Player jackz = getServer().getPlayer(UUID.fromString("b0c16432-67a6-4e3d-b49a-61b323c49b03"));
                    if(jackz != null) {
                        jackz.sendMessage(jackzco_prefix + "§7Auto updating & reloading jackzco");
                        jackz.playSound(jackz.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,1,1);
                    }
                },20L * 2);

		    }
	    }catch(Exception ex) {
    	    getLogger().warning("[UpdateChecker] Error while update checking: " + ex.toString());
	    }
    	//periodically check for an update
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return new MiscCommands(this).onCommand(this,sender,command,label,args);
    }

    static WorldGuard getWorldGuard() {
        WorldGuard wg = WorldGuard.getInstance();
        // WorldGuard may not be loaded
        // Maybe you want throw an exception instead
        return wg;
    }
    public boolean isJackzCoRegion(Location loc) {

        List<String> jackzco_regions = getJackzCo().getStringList("regions");
        plugin.getLogger().info("[isJackzCoRegion] Checking location. Regions: " + jackzco_regions.toString());
        return checkRegion(loc,jackzco_regions);
    }
    public static boolean checkRegion(Location loc, List<String> regions) {
        if(getWorldGuard() != null) {
            //RegionContainer container = getWorldGuard().getRegionContainer();
            RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(loc));
            for (ProtectedRegion region : set) {
                for(String rg_name : regions) {
                    if (region.getId().contains(rg_name)) {
                        return true;
                    }
                }
            }
        }else{
            return true;//if WG missing, just allow doors anywhere
        }
        return false;
    }

    public static boolean checkRegion(Location loc, String region_name) { //should simplify but eh
        if(getWorldGuard() != null) {
            RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(loc));
            for (ProtectedRegion region : set) {
                if (region.getId().contains(region_name)) {
                    return true;
                }
            }
        }else{
            return true; //if WG missing, just allow doors anywhere
        }
        return false;
    }

    public FileConfiguration getJackzCo() {
        return config;
    }
    private void loadTowers() {
        File[] towers = new File(getDataFolder() + "/towers").listFiles();
        if(towers != null) {
            for (File tower : towers) {
                try {
                    if (tower.isFile()) {
                        if (!tower.getName().endsWith(".tower")) continue;
                        JSONParser parser = new JSONParser();
                        JSONObject obj = (JSONObject) parser.parse(new FileReader(tower));
                        new jTower(obj, getServer().getWorld("world"));
                    }
                } catch (Exception ignored) {

                }
            }
            return;
        }
        getServer().getLogger().warning("No /towers folder found");
    }

    public static Plugin getPlugin() {
        return plugin;
    }
    private static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

}




