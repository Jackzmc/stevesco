package me.jackzmc.jackzco3;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.*;


public class jCMD implements CommandExecutor {
    private final Main plugin;
    String prefix = "§3[JackzCo]§r";
    /*ArrayList<JCommand> cmds = new ArrayList<JCommand>();
    JCommand cmd = new JCommand("test","blah");*/


    public jCMD(Main plugin) {
        this.plugin = plugin; // Store the plugin in situations where you need it.
        prefix = plugin.jackzco_prefix;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("[JackzCo] You must be a player.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("§aJackzCo §eV" + plugin.latest_version + "§a - §e/jackzco help§a for help");
            return true;
        }
        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("help")) {
            generateHelp(sender);
        } else if (args[0].equalsIgnoreCase("date")) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy");
            sender.sendMessage(prefix + "§7Today is §e" + sdf.format(cal.getTime()));
        } else if (args[0].equalsIgnoreCase("time")) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
            sender.sendMessage(prefix + "§7Time: §e" + sdf.format(cal.getTime()));
        } else if (args[0].equalsIgnoreCase("reload")) {
            Bukkit.dispatchCommand(sender, "plugman reload JackzCo3");
        } else if (args[0].equalsIgnoreCase("version")) {
            sender.sendMessage(prefix + "Currently running on version §e" + plugin.latest_version + "!");
        } else if (args[0].equalsIgnoreCase("jCloud")) {
            if (args.length >= 2) {
                if (args[1].equalsIgnoreCase("help")) {
                    player.sendMessage("§aWhat is jCloud? §7I don't know, what is it?");
                } else if (args[1].equalsIgnoreCase("url") || args[1].equalsIgnoreCase("link")) {
                    player.sendMessage("https://jackzco.online/jCloud/" + player.getUniqueId());
                } else if (args[1].equalsIgnoreCase("info") || args[1].equalsIgnoreCase("details")) {
                    player.sendMessage("§cFailed - In Development");
                } else {
                    player.sendMessage(ChatColor.RED + "jCloud Commands: [help] [url] [info]");
                }
            } else {
                player.sendMessage(ChatColor.RED + "jCloud Commands: [help] [url] [info]");
            }
        } else if (args[0].equalsIgnoreCase("getphone")) {
            ItemStack newPhone = new ItemStack(Material.TRIPWIRE_HOOK);
            ItemMeta newPhoneMeta = newPhone.getItemMeta();
            newPhoneMeta.setDisplayName("§3jPhone");
            newPhone.setItemMeta(newPhoneMeta);
            player.getInventory().addItem(newPhone);
        } else {
            generateHelp(sender);
        }
        return true;
    }
    private void generateHelp(CommandSender sender) {
        sender.sendMessage("§aJackzCo §eV" + plugin.latest_version + " §e" + "?" + " commands");
        /*for (String cmd : cmds) {
           sender.sendMessage("§7" + cmd);
        }*/
    }
}



