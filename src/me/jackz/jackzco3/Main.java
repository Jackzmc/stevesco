package me.jackz.jackzco3;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.jackz.jackzco3.lib.Config;
import me.jackz.jackzco3.lib.LocVarLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    private static Plugin plugin;

    String latest_version = "0.0.0";
    static public String jackzco_prefix = "§3JackzCo§6>§r ";

    public static Inventory keychain = Bukkit.createInventory(null, 9, "Inventory");
    public static Inventory appswitcher = Bukkit.createInventory(null, 36, "§4jPhone App Switcher");


    @Override
    public void onEnable() {
        latest_version = this.getDescription().getVersion();
        this.getCommand("jackzco").setExecutor(new jCommandLoader(this));
        this.getCommand("getid").setExecutor(new DoorControlCmd(this));
        //this.getCommand("jphone").setExecutor(new jPhone(this));
        ;
        registerEvents(this,
                new JoinEvent(this),
                new MainListener(this),
                new DoorControlEvent(this),
                new Wand(this),
                new MessageHandler(this)
        );
        new LocVarLib(this);
        new Config().setupConfig(this);
        plugin = this;
    }

    @Override
    public void onDisable() {

    }

    public WorldGuardPlugin getWorldGuard() {
        Plugin plugin =  getServer().getPluginManager().getPlugin("WorldGuard");
        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) plugin;
    }
    public boolean isJackzCoRegion(Location loc) {

        List<String> jackzco_regions = plugin.getConfig().getStringList("regions");
        plugin.getLogger().info("[isJackzCoRegion] Checking location. Regions: " + jackzco_regions.toString());
        return checkRegion(loc,jackzco_regions);
    }
    public boolean checkRegion(Location loc, List<String> regions) {
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
        public boolean checkRegion(Location loc, String region_name) { //should simplify but eh
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
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
       if(command.getName().equalsIgnoreCase("setname")) {
            if(sender instanceof Player) {
                try {
                    Player p = (Player) sender;
                    ItemStack item = p.getInventory().getItemInMainHand();
                    if(item == null) {
                        p.sendMessage("§cYou must have an item in your primary hand!");
                        return true;
                    }else{

                        String msg = args[0].replace("_"," ");
                        msg = msg.trim().replaceAll("(&([a-f0-9]))", "\u00A7$2");

                        ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.setDisplayName(msg);
                        item.setItemMeta(itemMeta);

                    }
                }catch(Exception err) {
                    sender.sendMessage("Failed: §c" + err.toString());
                    getLogger().log(Level.INFO,"setname!",err);
                }
            }else{
                sender.sendMessage("You must be a player");
            }
            return true;
       } else if (command.getName().equalsIgnoreCase("uuid")) {
            Player p = (Player) sender;
            sender.sendMessage("Your UUID is §e" + p.getUniqueId());
            return true;
       }else if(command.getName().equalsIgnoreCase("getlogs")) {
           try {

               String log = getDataFolder().toPath() + "/../../logs/latest.log";
               FileInputStream in = new FileInputStream(log);
               BufferedReader br = new BufferedReader(new InputStreamReader(in));

               List<String> lines = new LinkedList<>();
               for(String tmp; (tmp = br.readLine()) != null;)
                   if (lines.add(tmp) && lines.size() > 5)
                       lines.remove(0);

               for(String line : lines) {
                   sender.sendMessage(line);
               }
           }catch(Exception ex) {
               sender.sendMessage("Failed to get file: " + ex.toString());
               getLogger().log(Level.INFO,"getlogs!",ex);
           }

           return true;
       }
        return false;
    }



    public static void createDisplay(Player p, Material material, Inventory inv, int Slot, String name, String lore) {
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

    public static boolean isSafeLocation(Location location) {
        Block feet = location.getBlock();
        if (!feet.getType().isTransparent() && !feet.getLocation().add(0, 1, 0).getBlock().getType().isTransparent()) {
            return false; // not transparent (will suffocate)
        }
        Block head = feet.getRelative(BlockFace.UP);
        if (!head.getType().isTransparent()) {
            return false; // not transparent (will suffocate)
        }
        Block ground = feet.getRelative(BlockFace.DOWN);
        if (!ground.getType().isSolid()) {
            return false; // not solid
        }
        return true;
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




