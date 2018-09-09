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

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.jackz.jackzco3.lib.Config;
import me.jackz.jackzco3.lib.jTower;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends JavaPlugin {
    private static Plugin plugin;

    String latest_version = "0.0.0";
    static String jackzco_prefix = "§3JackzCo§6>§r ";

     static Inventory keychain = Bukkit.createInventory(null, 9, "Inventory");
     static Inventory appswitcher = Bukkit.createInventory(null, 36, "§4jPhone App Switcher");

    FileConfiguration config;

    @Override
    public void onEnable() {
        plugin = this;
        latest_version = this.getDescription().getVersion();
        this.getCommand("jackzco").setExecutor(new jCommandLoader(this));
        this.getCommand("getid").setExecutor(new DoorControlCmd(this));
        this.getCommand("jphone").setExecutor(new jPhone(this));

        //this.getCommand("jphone").setExecutor(new jPhone(this));
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
        config = new Config().setupConfig(this);
        loadTowers();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return new MiscCommands().onCommand(this,sender,command,label,args);
    }

    WorldGuardPlugin getWorldGuard() {
        Plugin plugin =  getServer().getPluginManager().getPlugin("WorldGuard");
        // WorldGuard may not be loaded
        if (!(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) plugin;
    }
    boolean isJackzCoRegion(Location loc) {

        List<String> jackzco_regions = getJackzCo().getStringList("regions");
        plugin.getLogger().info("[isJackzCoRegion] Checking location. Regions: " + jackzco_regions.toString());
        return checkRegion(loc,jackzco_regions);
    }
    boolean checkRegion(Location loc, List<String> regions) {
        if(getWorldGuard() != null) {
            RegionContainer container = getWorldGuard().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(loc);
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

    boolean checkRegion(Location loc, String region_name) { //should simplify but eh
        if(getWorldGuard() != null) {
            RegionContainer container = getWorldGuard().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(loc);
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

    FileConfiguration getJackzCo() {
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
                } catch (Exception e) {

                }
            }
            return;
        }
        getServer().getLogger().warning("No /towers folder found");
    }

    static void createDisplay(Player p, Material material, Inventory inv, int Slot, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        name = name.replaceAll("%name%", p.getName()); //convert vars
        name = name.replaceAll("%display name%", p.getCustomName());
        lore = lore.replaceAll("%name%", p.getName());
        lore = lore.replaceAll("%display name%", p.getCustomName());

        name = ChatColor.translateAlternateColorCodes('&', name); //convert & to color code
        lore = ChatColor.translateAlternateColorCodes('&', lore);
        meta.setDisplayName(name);

        ArrayList<String> Lore = new ArrayList<>(Arrays.asList(lore.split("\\|")));

        //Lore.add(lore);
        meta.setLore(Lore);
        item.setItemMeta(meta);

        inv.setItem(Slot, item);

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




