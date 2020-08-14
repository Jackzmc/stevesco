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

import me.jackz.jackzco3.lib.Util;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

class MiscCommands {
	Main plugin;
	MiscCommands(Main plugin) {
		this.plugin = plugin;
	}
	boolean onCommand(Main plugin,CommandSender sender, Command command, String label, String[] args) {
		String cmd = command.getName().toLowerCase();
		switch (cmd) {
			case "worlds":
				if(sender instanceof Player) {
					Player p = (Player) sender;
					TextComponent base = new TextComponent("§6===== Worlds =====\n");
					for(World world : plugin.getServer().getWorlds()) {
						TextComponent world_tc = new TextComponent("§e" + world.getName());
						TextComponent teleport = new TextComponent(" §9[Teleport]");
						teleport.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder( "Teleport to world" ).create()));
						teleport.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/world " + world.getName()));
						world_tc.addExtra(teleport);
						base.addExtra(world_tc);
						base.addExtra("\n");
					}
					p.spigot().sendMessage(base);
				}else{
					sender.sendMessage("You must be a player to use this");
				}
				return true;
			case "openchant":
			case "opchant":
				if (sender instanceof Player) {
					Player p = (Player) sender;
					if (args.length < 2) {
						p.sendMessage("§cUsage: /openchant <enchant> <level>");
					} else {
						if (p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
							p.sendMessage("§cPlease hold an item in your hand to enchant");
						} else {
							Enchantment enchantment = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(args[0]));
							if (enchantment == null) {
								p.sendMessage("§cThe enchantment §e" + args[0] + "§c was not found.");
							} else {
								int level = 0;
								if (Util.isInteger(args[1])) {
									level = Integer.parseInt(args[1]);
								} else {
									p.sendMessage("§cInvalid level. Usage: /openchant <enchant> <level>");
								}
								p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE,1,0);
								p.getInventory().getItemInMainHand().addUnsafeEnchantment(enchantment, level);
							}
						}
					}
					// /openchant
				} else {
					sender.sendMessage("You must be a player");
				}
				return true;
			case "setname":
				if (sender instanceof Player) {
					try {
						Player p = (Player) sender;
						ItemStack item = p.getInventory().getItemInMainHand();
						if (item == null) {
							p.sendMessage("§cYou must have an item in your primary hand!");
							return true;
						} else {

							String msg = args[0].replace("_", " ");
							msg = msg.trim().replaceAll("(&([a-f0-9]))", "\u00A7$2");

							ItemMeta itemMeta = item.getItemMeta();
							itemMeta.setDisplayName(msg);
							item.setItemMeta(itemMeta);

						}
					} catch (Exception err) {
						sender.sendMessage("Failed: §c" + err.toString());
						plugin.getLogger().log(Level.INFO, "setname!", err);
					}
				} else {
					sender.sendMessage("You must be a player");
				}
				return true;
			case "jstore": {
				if (!(sender instanceof Player)) {
					sender.sendMessage("§7Must be a player");
					return true;
				}
				Player p = (Player) sender;
				SignHandler.getStore(p);
				return true;
			}
			case "uuid": {
				if (!(sender instanceof Player)) {
					sender.sendMessage("§7Must be a player");
					return true;
				}
				Player p;
				if(args.length > 0) {
					p = plugin.getServer().getPlayer(args[0]);
					if(p == null) {
						try {
							p = plugin.getServer().getPlayer(UUID.fromString(args[0]));
						}catch(Exception ignored){ }
					}
				}else{
					p = (Player) sender;
				}
				if(p == null) {
					sender.sendMessage("§cThat user could not be found online.");
					return true;
				}
				sender.sendMessage("§e" + p.getName() + "'s§7 UUID is §e" + p.getUniqueId());
				return true;
			}
			case "getlogs":
				try {

					String log = plugin.getDataFolder().toPath() + "/../../logs/latest.log";
					FileInputStream in = new FileInputStream(log);
					BufferedReader br = new BufferedReader(new InputStreamReader(in));

					List<String> lines = new LinkedList<>();
					for (String tmp; (tmp = br.readLine()) != null; )
						if (lines.add(tmp) && lines.size() > 5)
							lines.remove(0);

					for (String line : lines) {
						sender.sendMessage(line);
					}
				} catch (Exception ex) {
					sender.sendMessage("Failed to get file: " + ex.toString());
					plugin.getLogger().log(Level.INFO, "getlogs!", ex);
				}

				return true;
			case "getname":
				if (sender instanceof Player) {
					Player p = (Player) sender;
					ItemStack itm = p.getInventory().getItemInMainHand();
					if (itm.getType().equals(Material.AIR)) {
						p.sendMessage("§cYou must have an item in your hand!");
					} else {
						p.sendMessage("Item is: §e" + itm.getType().toString());
					}
				}
				return true;
			case "test":
				sender.sendMessage(plugin.getJackzCo().getString("motd"));
				if (sender instanceof Player) {
					Player p = (Player) sender;
					sender.sendMessage("In JackzCo Region: " + plugin.isJackzCoRegion(p.getLocation()));
				}

				return true;
		}
		return false;
	}
}
