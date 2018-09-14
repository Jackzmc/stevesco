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

package me.jackz.jackzco3.jPhone;

import de.tr7zw.itemnbtapi.ItemNBTAPI;
import de.tr7zw.itemnbtapi.NBTItem;
import me.jackz.jackzco3.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.inventivetalent.glow.GlowAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Command  implements CommandExecutor {
	private Main plugin;
	private jPhoneMain jphone;
	Command(Main plugin,jPhoneMain jphone) {
		this.plugin = plugin;
		this.jphone = jphone;
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			ItemStack phone =  p.getInventory().getItemInMainHand();
			NBTItem nbti = ItemNBTAPI.getNBTItem(phone);
			if(args.length == 0) {
				sender.sendMessage("§cNo arguments specified. Try /jphone help");
				return true;
			}
			switch(args[0].toLowerCase()) {
				case "help":
					ItemStack helpbook = new ItemStack(Material.WRITTEN_BOOK);
					BookMeta helpMeta = (BookMeta) helpbook.getItemMeta();
					helpMeta.setTitle("jPhoneMain Terminal Help");
					helpMeta.setAuthor("JackzCo");
					List<String> pages = new ArrayList<>(Arrays.asList(
							"Thank you for using the jPhoneMain!\n§cNotice: Help is not complete\n\n§6claim §7- claims the phone as yours\n§6trash §7 -portable trash collection\n§6dangers §7- scans for any nearby dangers\n§6glow §7- highlights certain parameters",
							"§6charge §7- charges your phone with battery\n§6state §7- turn on/off your phone\n§6lookup §7- Lookup the username of the owner of phone",
							"§3Notes after purchase:\n\nIf you bought this device from any retail store, you will need to claim the device as yours. This is as simple as typing \n§6/jphone claim"
					));

					helpMeta.setPages(pages);
					helpbook.setItemMeta(helpMeta);
					p.getInventory().addItem(helpbook);
					break;
				case "own":
				case "claim":
					if (nbti.hasKey("owner")) {
						p.sendMessage("§cThis device is claimed by: §e" + nbti.getString("owner"));
					} else {
						nbti.setString("owner", p.getUniqueId().toString());
						p.sendMessage("§7Claimed device as §e" + p.getUniqueId().toString());
						p.getInventory().setItemInMainHand(nbti.getItem());
					}
					break;
				case "trash":
					Inventory trash = Bukkit.createInventory(null, 9 * 3, "jPhoneMain Portable Trash");
					p.openInventory(trash);
					break;
				case "dangers":
					if(plugin.getServer().getPluginManager().getPlugin("GlowAPI") == null) {
						p.sendMessage("§cThis feature is disabled, missing plugin §eGlowAPI");
						return true;
					}
					boolean pvpenabled = p.getWorld().getPVP();
					List<Entity> entities = new ArrayList<>();
					for(Entity ent : p.getNearbyEntities(50, 50, 50)) {
						if(ent instanceof Monster) {
							entities.add(ent);
							GlowAPI.setGlowing(ent, GlowAPI.Color.DARK_RED,p);
						}else if(ent instanceof Player && pvpenabled) {
							//if ent is player and PVP is enabled for that world
							entities.add(ent);
							GlowAPI.setGlowing(ent, GlowAPI.Color.RED,p);
						}

					}
					Bukkit.getScheduler().runTaskLater(plugin, () -> {
						for(Entity e: entities) {
							if(GlowAPI.isGlowing(e,p)) {
								GlowAPI.setGlowing(e,false,p);
							}
						}
					}, (30 * 20L));
					p.sendMessage("§cFound " + entities.size() + " dangers");
					break;
				case "glow":
					if(plugin.getServer().getPluginManager().getPlugin("GlowAPI") == null) {
						p.sendMessage("§cThis feature is disabled, missing plugin §eGlowAPI");
						return true;
					}
					if(args.length > 1) {
						if(args[1].equalsIgnoreCase("players")) {

							int count = 0;
							for(Player player : Bukkit.getOnlinePlayers()){
								if(!(GlowAPI.isGlowing(player, p))) {
									GlowAPI.setGlowing(player, GlowAPI.Color.WHITE, p);
									count += 1;
								}
								Bukkit.getScheduler().runTaskLater(plugin, () -> {
									if(GlowAPI.isGlowing(player, p)) {
										GlowAPI.setGlowing(player, false, p);
									}
								}, (30*20L));

							}
							p.sendMessage("§7Made §e" + count + "§7 players glow for §e30§7 seconds" );
						}else if(args[1].equalsIgnoreCase("entities")) {
							int count = 0;

							for(Entity ent : p.getNearbyEntities(50, 50, 50)) {
								if(!(ent instanceof Player)) {

									if(!(GlowAPI.isGlowing(ent, p))) {

										GlowAPI.setGlowing(ent, GlowAPI.Color.WHITE, p);
										count += 1;
									}

									Bukkit.getScheduler().runTaskLater(plugin, () -> {
										if(GlowAPI.isGlowing(ent, p)) {
											GlowAPI.setGlowing(ent, false, p);
										}

									}, (30*20L));
								}


							}
							p.sendMessage("§7Made §e" + count + "§7 entities glow for §e30§7 seconds" );
						}else if(args[1].equalsIgnoreCase("all")) {
							int count = 0;
							for(Entity ent : p.getNearbyEntities(50, 50, 50)) {
								if(!(GlowAPI.isGlowing(ent, p))) {
									GlowAPI.setGlowing(ent, GlowAPI.Color.WHITE, p);
									count += 1;
								}

								Bukkit.getScheduler().runTaskLater(plugin, () -> {
									if(GlowAPI.isGlowing(ent, p)) {
										GlowAPI.setGlowing(ent, false, p);
									}
								}, (30*20L));

							}
							p.sendMessage("§7Made §e" + count + "§7 entities/players glow for §e30§7 seconds" );
						}else{
							TextComponent msg = new TextComponent("§cPlease choose an option: §e");
							TextComponent msg_2 = new TextComponent("[Players]");
							TextComponent msg_3 = new TextComponent(" [Entities]");
							TextComponent msg_4 = new TextComponent(" [All]");
							msg_2.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/jphone glow players" ) );
							msg_3.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/jphone glow entities" ) );
							msg_4.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/jphone glow all" ) );

							msg.addExtra(msg_2);
							msg.addExtra(msg_3);
							msg.addExtra(msg_4);
							p.spigot().sendMessage(msg);
						}
					}else{
						TextComponent msg = new TextComponent("§cPlease choose an option: §e");
						TextComponent msg_2 = new TextComponent("[Players]");
						TextComponent msg_3 = new TextComponent(" [Entities]");
						TextComponent msg_4 = new TextComponent(" [All]");
						msg_2.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/jphone glow players" ) );
						msg_3.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/jphone glow entities" ) );
						msg_4.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/jphone glow all" ) );

						msg.addExtra(msg_2);
						msg.addExtra(msg_3);
						msg.addExtra(msg_4);
						p.spigot().sendMessage(msg);
						//Key "owner" not set
					}
					return true;
				case "charge":
					nbti.setInteger("battery", 100);
					p.getInventory().setItemInMainHand(nbti.getItem());
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING,1,1);
					p.sendMessage("§aDING! Charged your phone!");
					break;
				case "state":
					if (nbti.getBoolean("state")) {
						nbti.setBoolean("state", false);
						p.sendMessage("Phone now off");
					} else {
						nbti.setBoolean("state", true);
						p.sendMessage("phone now on");
					}
					p.getInventory().setItemInMainHand(nbti.getItem());
				case "exit":
					nbti.setBoolean("terminal", false);
					p.sendMessage("§7Exited §terminal mode");
					p.getInventory().setItemInMainHand(nbti.getItem());
				case "lookup":
					p.sendMessage("§7Looking up player from UUID...");
					try {
						UUID uuid = UUID.fromString(args[1]);
						p.sendMessage("§7Player: §e" + Bukkit.getOfflinePlayer(uuid).getName());
					}catch(IllegalArgumentException e) {
						p.sendMessage("§cPlayer was not found, or invalid UUID");
					}
					break;
				default:
					/*TextComponent msg = new TextComponent("§cThe specified commmand doesn't exist");
					msg.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "jphone glow players" ) );
					msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§9Command: §e" + args[0] + "," + args[1] + "," + args[2]).create()));
					p.spigot().sendMessage(msg);*/
					p.sendMessage("§cThe specified commmand doesn't exist");
			}
			return true;
		}
		return false;
	}
}
