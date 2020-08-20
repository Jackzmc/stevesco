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

import me.jackz.jackzco3.jPhone.jPhoneMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class jCommandLoader implements CommandExecutor {
    private final Main plugin;
    private String prefix;
    jCommandLoader(Main plugin) {
        prefix = Main.JACKZCO_PREFIX;
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if(args.length == 0) {
            sender.sendMessage("§aJackzCo §eV" + Main.LATEST_VERSION + "§a - §e/jackzco help§a for help");
            return true;
        }
        switch(args[0].toLowerCase()) {
            case "date":
                Calendar cald = Calendar.getInstance();
                SimpleDateFormat sdfd = new SimpleDateFormat("EEEE, MMMM d, yyyy");
                sender.sendMessage(prefix + "§7Today is §e" + sdfd.format(cald.getTime()));
                break;
            case "time":
                Calendar calt = Calendar.getInstance();
                SimpleDateFormat sdft = new SimpleDateFormat("hh:mm:ss a");
                sender.sendMessage(prefix + "§7Time: §e" + sdft.format(calt.getTime()));
                break;
            case "reload":
                Bukkit.dispatchCommand(sender, "plugman reload JackzCo3");
                break;
            case "config":
                plugin.reloadConfig();
                break;
            case "version":
                sender.sendMessage(prefix + "Currently running on version §e" + Main.LATEST_VERSION + "!");
                break;
            case "jcloud":
                if (args.length >= 2) {
                    if (args[1].equalsIgnoreCase("help")) {
                        sender.sendMessage("§aWhat is jCloud? §7I don't know, what is it?");
                    } else if (args[1].equalsIgnoreCase("url") || args[1].equalsIgnoreCase("link")) {
                        if(sender instanceof Player) {
                            sender.sendMessage("https://jackzco.online/jCloud/" + ((Player) sender).getPlayer().getUniqueId());
                        }else{
                            sender.sendMessage("https://jackzco.online/jCloud/[UUID]");
                        }

                    } else if (args[1].equalsIgnoreCase("info") || args[1].equalsIgnoreCase("details")) {
                        sender.sendMessage("§cFailed - In Development");
                    } else {
                        sender.sendMessage(ChatColor.RED + "jCloud Commands: [help] [url] [info]");
                    }
                } else {
                   sender.sendMessage(ChatColor.RED + "jCloud Commands: [help] [url] [info]");
                }
                break;
            case "admin":
                new jCommandAdmin(plugin,sender,cmd,commandLabel,args);
                return true;
            case "beta": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("[JackzCo] You must be a player to use this");
                    return true;
                }
                Player p = (Player) sender;
                ItemStack newPhone = new ItemStack(Material.TRIPWIRE_HOOK);
                ItemMeta newPhoneMeta = newPhone.getItemMeta();
                newPhoneMeta.setDisplayName("§3jPhone Beta Phone");
                newPhoneMeta.setLore(Arrays.asList("§cBeta Test #001", "§eBugs May Occur"));
                newPhone.setItemMeta(newPhoneMeta);
                p.getInventory().addItem(newPhone);
                p.sendMessage("§7Thank you for being apart of the §3jPhone Beta Program");
                p.sendMessage("§7We have provided you a documentation book to get started with the phone. Thank you for choosing JackzCo!");
                p.getInventory().addItem(jPhoneMain.getIntroBook());
                break;
            } case "dev": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("[JackzCo] You must be a player to use this");
                    return true;
                }
                Player p = (Player) sender;
                if (!p.isOp()) {
                    p.sendMessage("§cYou must be an OP to get the jPhone Test Phone.");
                    return true;
                }
                new jPhoneMain(plugin);
                p.getInventory().addItem(jPhoneMain.givePhone(p,"§3jPhone 2", "2", false));
                p.getInventory().addItem(jPhoneMain.getIntroBook());
                ItemStack newPhone = new ItemStack(Material.TRIPWIRE_HOOK);
                ItemMeta newPhoneMeta = newPhone.getItemMeta();
                newPhoneMeta.setDisplayName("§3jPhone Development Phone");
                newPhoneMeta.setLore(Arrays.asList("§cNOT §7to be shared with the public", "§4IF CAUGHT SHARING PHONE, ", "§4YOU WILL BE SUED & TERMINATED"));
                newPhone.setItemMeta(newPhoneMeta);
                p.getInventory().addItem(newPhone);
                break;
            } default:
                sender.sendMessage("§aJackzCo §eV" + Main.LATEST_VERSION + " §e" + "?" + " commands");
                break;
        }
        return true;
        //TODO: add commands
    }
    private void jRespond(CommandSender s,String msg) {
        s.sendMessage(prefix + msg);
    }
}
