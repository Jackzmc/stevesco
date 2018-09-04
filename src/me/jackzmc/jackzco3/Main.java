package me.jackzmc.jackzco3;

import com.google.common.base.Joiner;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class Main extends JavaPlugin {
    private static Plugin plugin;

    String latest_version = "0.0.0";
    static public String jackzco_prefix = "§3JackzCo§6>§r ";

    public static Inventory keychain = Bukkit.createInventory(null, 9, "Inventory");
    public static Inventory appswitcher = Bukkit.createInventory(null, 36, "§4jPhone App Switcher");

    @Override
    public void onEnable() {
        latest_version = this.getDescription().getVersion();
        this.getCommand("jackzco").setExecutor(new jCMD(this));
        this.getCommand("getid").setExecutor(new DoorControlCmd(this));
        //this.getCommand("jphone").setExecutor(new jPhone(this));

        registerEvents(this,
                new JoinEvent(this),
                new MainListener(this),
                new DoorControlEvent(this),
                new Wand(this)
        );
        new Config().setupConfig();
        plugin = this;
    }

    @Override
    public void onDisable() {

    }


    /*this is a simple /jump command*/

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        if (command.getName().equalsIgnoreCase("j") || command.getName().equalsIgnoreCase("jump")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                float pitch = player.getLocation().getPitch();
                float yaw = player.getLocation().getYaw();
                if (isSafeLocation(player.getTargetBlock((Set<Material>) null, 100).getLocation())) {
                    //true
                    Location tploc = player.getTargetBlock((Set<Material>) null, 100).getLocation();
                    tploc.setPitch(pitch);
                    tploc.setYaw(yaw);
                    int x = tploc.getBlockX();
                    int z = tploc.getBlockZ();
                    tploc.setY(tploc.getWorld().getHighestBlockYAt(x, z));
                    player.teleport(tploc);
                } else {
                    sender.sendMessage("§dThere is no block to jump to");
                }

            } else {
                sender.sendMessage("§cYou must be in game to use this command");
            }

            return true;
        }else if(command.getName().equalsIgnoreCase("setname")) {
            if(sender instanceof Player) {
                try {
                    Player p = (Player) sender;
                    String msg = Joiner.on(" ").join(args);
                    msg = msg.replaceAll(args[0], "");
                    msg = msg.trim().replaceAll("(&([a-f0-9]))", "\u00A7$2");

                    ItemStack item = p.getInventory().getItemInMainHand();
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setDisplayName(msg);
                    item.setItemMeta(itemMeta);
                }catch(Exception err) {
                    sender.sendMessage("Failed: " + err.getMessage());
                }
            }

        }else if(command.getName().equalsIgnoreCase("fly")) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                if(player.hasPermission("essentials.fly")) {
                    if(player.getAllowFlight()) {
                        player.setAllowFlight(false);
                        player.sendMessage("§7Flight §edisabled");
                    }else{
                        player.setAllowFlight(true);
                        player.sendMessage("§7Flight §eenabled");
                    }
                }else{
                    player.sendMessage("§cYou don't have permission to fly!");
                }
            }else{
                sender.sendMessage("§cThis is a player command");
            }
        }else if(command.getName().equalsIgnoreCase("spawn")) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage("this is coming soon");
                return true;
            }else{
                return false;
            }
            //do ess spawn

        }
        return false;
    }

    public WorldGuardPlugin getWorldGuard() {
        Plugin plugin =  getServer().getPluginManager().getPlugin("WorldGuard");
        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) plugin;
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

        ArrayList<String> Lore = new ArrayList<String>(Arrays.asList(lore.split("\\|")));

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




