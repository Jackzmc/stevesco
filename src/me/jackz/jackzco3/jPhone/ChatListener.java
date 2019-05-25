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

package me.jackz.jackzco3.jPhone;

import de.tr7zw.itemnbtapi.ItemNBTAPI;
import de.tr7zw.itemnbtapi.NBTItem;
import me.jackz.jackzco3.Main;
import me.jackz.jackzco3.lib.InventoryStore;
import me.jackz.jackzco3.lib.Util;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.inventivetalent.glow.GlowAPI;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatListener implements Listener {
	private Main plugin;
	private jPhoneMain jphone;
	ChatListener(Main plugin,jPhoneMain jphone) {
		this.plugin = plugin;
		this.jphone = jphone;
	}
	@SuppressWarnings("SpellCheckingInspection")
	@EventHandler
	public void jPhoneChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		ItemStack itm = p.getInventory().getItemInMainHand();
		if(itm.hasItemMeta()) {
			NBTItem nbt = ItemNBTAPI.getNBTItem(itm);
			if(!nbt.hasKey("jackzco") || !nbt.hasNBTData()) return;
			if(nbt.getBoolean("terminal") && !nbt.getBoolean("state")) { //check if terminal mode on, and its off
				p.sendMessage("§7Cannot connect to phone: §cPhone is offline");
				return;
			}
			if(nbt.getBoolean("terminal")) {
				e.setCancelled(true);
				p.sendMessage(" ");
				p.sendMessage("§a>" + e.getMessage());
				String[] args = e.getMessage().split(" ");
				switch(args[0].toLowerCase()) {
					case "version":
						p.sendMessage("§7The current version of terminal is §e" + Main.TERMINAL_VERSION);
						break;
					case "light":
					case "jlight":
						ItemStack CurrentPhone = nbt.getItem();
						ItemMeta PhoneMeta = CurrentPhone.getItemMeta();
						PhoneMeta.setDisplayName("§fjLight");
						CurrentPhone.setItemMeta(PhoneMeta);
						CurrentPhone.setType(Material.TORCH);
						p.getInventory().setItemInMainHand(CurrentPhone);
						break;
					case "commands":
					    int pageno = 1;
					    if(args.length >= 2) {
						    if(Util.isInteger(args[1])) {
					            pageno = Integer.parseInt(args[1]);
					            if(pageno <= 0) {
					                pageno = 1;
                                }
                            }
                        }
					    //TODO: pagination (split by 10 or 15s)
						List<String> cmds = new ArrayList<>(Arrays.asList( //make clickable names
								"§ehelp §7get jphone help",
								"§eversion §7check the version of terminal",
								"§elight §7turn on your flashlight",
								"§eclaim §7claim the device as yours",
								"§eglow §7highlight player, entities, monsters",
								"§edangers §7highlights dangers (legacy",
								"§estate §7turn on/off phone",
								"§elookup §7lookup a player by UUID",
								"§etext §7text any player that has a jPhone",
								"§ekeychain §7View your used/free space on your keychain",
								"§ejcloud §7manage your jCloud account",
								"§edate/time §7view the current time or date",
								"§esettings §7view/Set your phone's settings",
								"§eping §7ping all known towers",
								"§eexit §7exit terminal mode"
						));
						final int pageResults = 10;
						List<List<String>> commands = new ArrayList<>();
						if(cmds.size() >= pageResults) {
							for (int i = 0; i < cmds.size(); i++)
								if (i % pageResults == 0) {
									//index of:

									int endsize = (i+(pageResults - 1)) > cmds.size() ? cmds.size() : i+(pageResults - 1);
									List<String> list = new ArrayList<>(cmds.subList(i, endsize));
									commands.add(list);
								}
						}else{
							commands.add(cmds);
						}
                        if(pageno > commands.size()) {
                            pageno = commands.size();
                        }
						p.sendMessage("§3Current Commands: §7(Page §e" + (pageno) + "/" + commands.size() + "§7)\n" + String.join("\n",commands.get(pageno-1)));
						break;
					case "keychain":
						int max_size = 9*3;
						float size = new InventoryStore(plugin,"keychain_" + p.getName(),max_size).getFillSize();
						double percent = (size/(float)max_size) * 100;
						p.sendMessage(MessageFormat.format("§7Your keychain is §e{0}% §7full. (§e{1}/{2}§7)",percent,size,max_size));
						break;
					case "help":
						p.sendMessage("§7Hi, terminal is currently in alpha and missing features.");
						p.sendMessage("§7Current Version is: §e" + plugin.getJackzCo().getString("versions.terminal"));
						p.sendMessage("§7Type §ecommands §7to view commands");
						break;
					case "time": {
						DateFormat dateFormat = new SimpleDateFormat("h:mm a");
						p.sendMessage("§7The time is §e" + dateFormat.format(new Date()));
						break;
					} case "date": {
						DateFormat dateFormat = new SimpleDateFormat("EEEE, MMMMMM d yyyy ");
						p.sendMessage("§7Today is §e" + dateFormat.format(new Date()));
						break;
					} case "claim":
					case "own":
						if (nbt.hasKey("owner")) {
							p.sendMessage("§cThis device is claimed by: §e" + nbt.getString("owner")  + (nbt.getString("owner").equals(p.getUniqueId().toString()) ? " §7(You)":""));
						} else {
							nbt.setString("owner", p.getUniqueId().toString());
							p.sendMessage("§7Claimed device as §e" + p.getUniqueId().toString());
							p.getInventory().setItemInMainHand(nbt.getItem());
						}
						break;
					case "ping":
					case "towers":
						p.sendMessage("§7Pinging towers...");
						Bukkit.getScheduler().runTaskLater(plugin, () -> {
							HashMap<String,Double> towers = jphone.getSortedTowers(p.getLocation());
							for (String tower : towers.keySet()) {
								double dist = towers.get(tower);
								if(dist > 1000) {
									p.sendMessage("§7Tower §e" + tower + "§7 is §e" + jPhoneMain.getTowerQuality(dist));
									continue;
								}else if(dist > 600) {
									p.sendMessage("§7Tower §e" + tower + "§7 (§cPoor§7) is §e" + Math.round(randomizeDouble(dist,p.getLocation())) + "§7 blocks away");
									continue;
								}
								p.sendMessage("§7Tower §e" + tower + "§7 is §e" + Math.round(dist) + "§7 blocks away.");
							}
						},40L);
						break;
					case "text":
						//text 'player' 'message' [3 args]
						if(args.length < 3) {
							p.sendMessage("§cSyntax for texting: §etext <player> <message>");
							return;
						}

						Player rec = Bukkit.getPlayer(args[1]);
						if(rec == null) {
							p.sendMessage("§cPlayer §e" + args[1] + " §cwas not found online");
							return;
						}else if(rec == p) {
							p.sendMessage("§cYou can't text yourself!");
							return;
						}
						if(!jphone.isInTowerRange(p.getLocation())) {
							p.sendMessage("§7Failed to send message: §cNot in range of a tower.");
							return;
						}else if(!jphone.isInTowerRange(rec.getLocation())) {
							p.sendMessage("§7cReceiver is out of range of any towers.");
							return;
						}
						//loop rec's inventory to check for phone:
						for (int i = 0; i < p.getInventory().getSize(); i++) {
							ItemStack item = p.getInventory().getItem(i);
							if (item == null || !item.getType().equals(Material.TRIPWIRE_HOOK)) continue;
							ItemMeta meta = item.getItemMeta();
							if (meta == null || !meta.hasDisplayName()) continue;
							if (meta.getDisplayName().equals("§fjLight") || meta.getDisplayName().equals("§3jPhone")) {
								String outMsg = String.join(" ",args).replace(String.format("%s %s",args[0],args[1]),"");
								rec.sendMessage("§ajText>§3" + p.getName() + ":§7" + outMsg);
								rec.playSound(rec.getLocation(),Sound.BLOCK_NOTE_BLOCK_BELL,1,1); //todo: customizable
								p.playSound(p.getLocation(),Sound.BLOCK_NOTE_BLOCK_FLUTE,1,1);
								return;
							}
						}
						p.sendMessage("§cPlayer does not have a jPhone. Cannot receive texts");
						break;
					case "charge":
						nbt.setInteger("battery", 100);
						p.getInventory().setItemInMainHand(nbt.getItem());
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,1,1);
						p.sendMessage("§aYour phone has been charged using the power of BlockChain(TM)");
						break;
					case "trash":
						Inventory trash = Bukkit.createInventory(null, 9 * 3, "jPhone Portable Trash");
						p.openInventory(trash);
						break;
					case "dangers": {
						if (plugin.getServer().getPluginManager().getPlugin("GlowAPI") == null) {
							p.sendMessage("§cThis feature is disabled, missing plugin §eGlowAPI");
							return;
						}
						List<Entity> entities = new ArrayList<>();
						for (Entity ent : p.getNearbyEntities(50, 50, 50)) {
							if (ent instanceof Monster) {
								entities.add(ent);
								GlowAPI.setGlowing(ent, GlowAPI.Color.DARK_RED, p);
							} else if (ent instanceof Player && p.getWorld().getPVP()) {
								//if ent is player and PVP is enabled for that world
								entities.add(ent);
								GlowAPI.setGlowing(ent, GlowAPI.Color.RED, p);
							}

						}
						Bukkit.getScheduler().runTaskLater(plugin, () -> {
							for (Entity ent : entities) {
								if (GlowAPI.isGlowing(ent, p)) {
									GlowAPI.setGlowing(ent, false, p);
								}
							}
						}, (30 * 20L));
						p.sendMessage("§cFound " + entities.size() + " dangers");
						break;
					} case "glow":
					case "highlight": {
						if (plugin.getServer().getPluginManager().getPlugin("GlowAPI") == null) {
							p.sendMessage("§cThis feature is disabled, missing plugin §eGlowAPI");
							return;
						}
						//todo: merge duplicates into method to toggle glow status for X ticks
						if (args.length > 1) {
							if (args[1].equalsIgnoreCase("players")) {
								int count = 0;
								for (Player player : Bukkit.getOnlinePlayers()) {
									if (!(GlowAPI.isGlowing(player, p))) {
										GlowAPI.setGlowing(player, GlowAPI.Color.WHITE, p);
										count += 1;
									}
									Bukkit.getScheduler().runTaskLater(plugin, () -> {
										if (GlowAPI.isGlowing(player, p)) GlowAPI.setGlowing(player, false, p);
									}, (30 * 20L));
								}
								p.sendMessage("§7Made §e" + count + "§7 players glow for §e30§7 seconds");
								break;
							} else if (args[1].equalsIgnoreCase("entities")) {
								int count = 0;
								for (Entity ent : p.getNearbyEntities(50, 50, 50)) {
									if (!(ent instanceof Player)) {
										if (!(GlowAPI.isGlowing(ent, p))) {

											GlowAPI.setGlowing(ent, GlowAPI.Color.WHITE, p);
											count += 1;
										}
										Bukkit.getScheduler().runTaskLater(plugin, () -> {
											if (GlowAPI.isGlowing(ent, p)) GlowAPI.setGlowing(ent, false, p);
										}, (30 * 20L));
									}
								}
								p.sendMessage("§7Made §e" + count + "§7 entities glow for §e30§7 seconds");
								break;
							}  else if (args[1].equalsIgnoreCase("dangers")) {
								int count = 0;
								for (Entity ent : p.getNearbyEntities(50, 50, 50)) {
									if (ent instanceof Monster) {
										if (!(GlowAPI.isGlowing(ent, p))) {
											GlowAPI.setGlowing(ent, GlowAPI.Color.WHITE, p);
											count += 1;
										}
										Bukkit.getScheduler().runTaskLater(plugin, () -> {
											if (GlowAPI.isGlowing(ent, p)) GlowAPI.setGlowing(ent, false, p);
										}, (30 * 20L));
									}
								}
								p.sendMessage("§7Made §e" + count + "§7 entities glow for §e30§7 seconds");
								break;
							} else if (args[1].equalsIgnoreCase("all")) {
								int count = 0;
								for (Entity ent : p.getNearbyEntities(50, 50, 50)) {
									if (!(GlowAPI.isGlowing(ent, p))) {
										GlowAPI.setGlowing(ent, GlowAPI.Color.WHITE, p);
										count += 1;
									}
									Bukkit.getScheduler().runTaskLater(plugin, () -> {
										if (GlowAPI.isGlowing(ent, p)) GlowAPI.setGlowing(ent, false, p);
									}, (30 * 20L));
								}
								p.sendMessage("§7Made §e" + count + "§7 entities/players glow for §e30§7 seconds");
								break;
							}
						} else {
							TextComponent msg = new TextComponent("§cPlease choose an option: §e");
							TextComponent msg_2 = new TextComponent("[Players]");
							TextComponent msg_3 = new TextComponent(" [Entities]");
							TextComponent msg_4 = new TextComponent(" [All]"); //i hope i can simplify all of this
							msg_2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"glow players"));
							msg_3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "glow entities"));
							msg_4.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "glow all"));

							msg.addExtra(msg_2);
							msg.addExtra(msg_3);
							msg.addExtra(msg_4);
							p.spigot().sendMessage(msg);
							//Key "owner" not set
						}
						break;
					}case "state":
						if (nbt.getBoolean("state")) {
							nbt.setBoolean("state", false);
							p.sendMessage("§7Phone has been switched off.");
						} else {
							nbt.setBoolean("state", true);
							p.sendMessage("§7Phone has been turned on.");
						}
						p.getInventory().setItemInMainHand(nbt.getItem());
						break;
					case "settings":
						BaseComponent message = new TextComponent("§3jPhoneOS Settings\n");
						if(!nbt.hasKey("owner")) {
							BaseComponent ownermsg = new TextComponent("§cThis phone is not claimed, click to claim.\n");
							ownermsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"claim"));
							message.addExtra(ownermsg);
							message.addExtra("\n");
						}
						if(!nbt.hasKey("txtsound")) nbt.setString("txtsound","bell");
						BaseComponent txt_sound = new TextComponent("§9Text Sound: §e" + nbt.getString("txtsound"));
						message.addExtra(txt_sound);
						message.addExtra("\n§7No other settings found.\n");
						p.spigot().sendMessage(message);
						break;
					case "debug":
						p.sendMessage("§cjPhone Data Dump. jPhone Terminal OS V" + Main.TERMINAL_VERSION);
						Set<String> keys = nbt.getKeys();
						while(keys.iterator().hasNext()){
							String key = keys.iterator().next();
							String value = nbt.getString(key);
							p.sendMessage(String.format("§c%s: §e%s",key,value));
						}
					case "lookup":
						p.sendMessage("§7Looking up player from UUID...");
						try {
							UUID uuid = UUID.fromString(args[1]);
							p.sendMessage("§7Player: §e" + Bukkit.getOfflinePlayer(uuid).getName());
						}catch(IllegalArgumentException ex) {
							p.sendMessage("§cPlayer was not found, or invalid UUID");
						}
						break;
					case "jcloud":
						switch(args[1].toLowerCase()) {
							default:
								p.sendMessage("§7Failed to fetch data from jCloud API: §c501 Not Implemented");
								break;
						}
						break;
					case "exit":
						nbt.setBoolean("terminal",false);
						p.sendMessage("§7Exited §eterminal mode");
						p.getInventory().setItemInMainHand(nbt.getItem());
						break;
					default:
						p.sendMessage("§cUnknown command was specified. §7Type §ehelp for help");

				}

			}
		}
	}
	private double randomizeDouble(Double d, Location loc) {
		int seed = (int)loc.getX() + (int)loc.getY() + (int)loc.getZ();
		return randomizeDouble(d,seed);
	}
	private double randomizeDouble(Double d, int seed) {
		Random random = new Random(seed);
		//300 blocks -> down to 250 to 350
		double range = ((d + 50) - (d - 50)) + 1;
		return (int)(Math.random() * range) + (d - 50);
	}
}
