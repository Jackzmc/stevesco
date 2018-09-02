package me.jackzmc.jackzco3;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;



public class Main extends JavaPlugin {
    private static Plugin plugin;
    String latest_version = "Unknown";
    static String jackzco_prefix = "§3JackzCo§6>§r ";

    @Override
    public void onEnable() {
        PluginDescriptionFile pdf = this.getDescription();
        //register commands
        this.getCommand("jackzco").setExecutor(new jCMD(this));
        getServer().getPluginManager().registerEvents(new DoorControl(this), this);

        //register event listeners and lots of shit yeah
        getServer().getPluginManager().registerEvents(new JoinEvent(this), this);
        getServer().getPluginManager().registerEvents(new MainListener(), this);
        /*getServer().getPluginManager().registerEvents(new OnDeath(this), this);

        getServer().getPluginManager().registerEvents(new CreeperExplosion(this), this);
        getServer().getPluginManager().registerEvents(new jPhone(this), this);
        getServer().getPluginManager().registerEvents(new MiscListener(this), this);*/


        final FileConfiguration config = this.getConfig();
        config.addDefault("motd", "Hello %player%, Welcome to the server!");
        config.set("version", pdf.getVersion());
        latest_version = pdf.getVersion();
        config.options().copyDefaults(true);
        saveConfig();
        getJackzCo();
        plugin = this;
    }
    @Override
    public void onDisable() {

    }
    public static Plugin getPlugin() {
        return plugin;
    }
    public FileConfiguration getJackzCo() {
        File playerFile = new File (plugin.getDataFolder(), "jackzco" + ".yml");
        FileConfiguration jdata = YamlConfiguration.loadConfiguration(playerFile);
        return jdata;
    }
    public void createJackzCo() {
        File playerFile = new File (plugin.getDataFolder(), "jackzco" + ".yml");
        FileConfiguration jdata = YamlConfiguration.loadConfiguration(playerFile);
        jdata.addDefault("versions.jackzco", "0.1");
        jdata.addDefault("versions.jphone","0.1");
        jdata.addDefault("jackzco.prefix", "§3JackzCo§6>");
        jdata.options().copyDefaults(true);
        try {
            jdata.save(playerFile);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    /*this is a simple /jump command*/

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        if (command.getName().equalsIgnoreCase("j") || command.getName().equalsIgnoreCase("jump")) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                float pitch = player.getLocation().getPitch();
                float yaw = player.getLocation().getYaw();
                if(isSafeLocation(player.getTargetBlock((Set<Material>) null, 100).getLocation())) {
                    //true
                    Location tploc = player.getTargetBlock((Set<Material>) null, 100).getLocation();
                    tploc.setPitch(pitch);
                    tploc.setYaw(yaw);
                    int x = tploc.getBlockX();
                    int z = tploc.getBlockZ();
                    tploc.setY(tploc.getWorld().getHighestBlockYAt(x, z));
                    player.teleport(tploc);
                }else{
                    sender.sendMessage("§dThere is no block to jump to");
                }

            }else{
                sender.sendMessage("§cYou must be in game to use this command");
            }

            return true;
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



    public final class MainListener implements Listener {


        @EventHandler
        public void onChange(SignChangeEvent e) {
            //On sign creation
            // Player p = e.getPlayer();
            e.setLine(0,ChatColor.translateAlternateColorCodes('&', e.getLine(0)));
            e.setLine(1,ChatColor.translateAlternateColorCodes('&', e.getLine(1)));
            e.setLine(2,ChatColor.translateAlternateColorCodes('&', e.getLine(2)));
            e.setLine(3,ChatColor.translateAlternateColorCodes('&', e.getLine(3)));
        }

        @EventHandler
        public void onInteract(PlayerInteractEvent e) {
            Player p = e.getPlayer();
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                BlockState bs = e.getClickedBlock().getState();
                if ((bs instanceof Sign)) {
                    Sign sign = (Sign)bs;
                    if (sign.getLine(0).contains("§4§lJackzCo") || sign.getLine(0).contains("§4§l§n")) {
                        String[] options = {"Balance","Withdraw","Deposit","Test1","Test2","Test3"};
                        System.out.println(options[0]);
                        //String current = options[0];
            	 /* String current = "";
            	 int id = -1;
               if(sign.getLine(1).contains(">")) {
            	   current = sign.getLine(1).replace(">", "");
            	   id = 1;
            	   //Above is the name of the value (without > selection id)
            	   //then
               }else if(sign.getLine(2).contains(">")) {
            	  current = sign.getLine(2).replace(">", "");
            	   id = 2;
               }else if(sign.getLine(3).contains(">")) {
            	    current = sign.getLine(3).replace(">", "");
            	   id = 3;
               }else{
            	   p.sendMessage("This sign has nothing selected and therefore invalid");
               }
               int index = -1;
               for (int i=0;i<options.length;i++) {
                   if (options[i].equals(current)) {
                       index = i;
                       break;
                   }
               }
                //Index -> The index of the selected option in array
               index = index + 1;
               sign.setLine(id,options[index]);*/
                        // sign.setLine(id-1, arg1);
                        //ID = selected line number, current = NAME of selected item
                        bs.update();

                    }
                }
            }else if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
                BlockState bs = e.getClickedBlock().getState();
                if ((bs instanceof Sign)) {
                    Sign sign = (Sign)bs;
                    if (sign.getLine(0).contains("§4§lJackzCo") || sign.getLine(0).contains("§4§l§nJackzCo")) {
                        e.setCancelled(true);
                        if(sign.getLine(1).contains(">")) {
                            sign.setLine(1,"Balance");
                            sign.setLine(2, "Withdraw");
                            sign.setLine(3, ">Deposit");
                        }else if(sign.getLine(2).contains(">")) {
                            sign.setLine(1,">Balance");
                            sign.setLine(2, "Withdraw");
                            sign.setLine(3, "Deposit");
                        }else if(sign.getLine(3).contains(">")) {
                            sign.setLine(1,"Balance");
                            sign.setLine(2, ">Withdraw");
                            sign.setLine(3, "Deposit");
                        }else{
                            p.sendMessage("This sign has nothing selected and therefore invalid");
                        }
                        bs.update();

                    }
                }
            }
        }
    	/*NOTE BROADCAST:
    	 Bukkit.broadcastMessage("test");

    	 */

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
}




