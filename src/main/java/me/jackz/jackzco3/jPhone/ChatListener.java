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

import de.tr7zw.nbtapi.NBTItem;
import me.jackz.jackzco3.Main;
import me.jackz.jackzco3.lib.InventoryStore;
import me.jackz.jackzco3.lib.Util;
import me.jackz.jackzco3.lib.jTower;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
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
	private final LinkedHashMap<String, String> commandsMap = new LinkedHashMap<String, String>() {
		{
			put("help", "get jphone help");
			put("version", "check the version of terminal");
			put("light", "turn on your flashlight");
			put("claim", "claim the device as yours");
			put("glow", "highlight players, entities, monsters");
			put("dangers", "highlight dangers (legacy)");
			put("state", "turn your phone off");
			put("lookup", "Search a database of users by UUID");
			put("text", "text any player that uses jService or jPhone");
			put("keychain", "View and manage your keychain space");
			put("jcloud", "manage your jCloud account");
			put("date/time", "View the current date/time");
			put("settings", "View or manage your phone's settings");
			put("exit", "exit terminal mode");
		}
	};
	private List<TextComponent> commands = new ArrayList<>();

	ChatListener(Main plugin,jPhoneMain jphone) {
		this.plugin = plugin;
		this.jphone = jphone;
		for (Map.Entry<String, String> commandEntry : commandsMap.entrySet()) {
			TextComponent tc = new TextComponent(commandEntry.getKey());
			tc.setColor(ChatColor.YELLOW);
			tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandEntry.getKey()));
			tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to run the command: \n§e" + commandEntry.getKey()).create()));
			tc.addExtra( "§7 " + commandEntry.getValue());
			commands.add(tc);
		}
	}
	@SuppressWarnings("SpellCheckingInspection")
	@EventHandler
	public void jPhoneChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		ItemStack itm = p.getInventory().getItemInMainHand();
		if(itm.hasItemMeta()) {
			NBTItem nbt = new NBTItem(itm);
			if(!nbt.hasKey("jphone") || !nbt.hasNBTData()) return;
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
					    int currentPage = 1;
					    if(args.length >= 2) {
						    if(Util.isInteger(args[1])) {
					            currentPage = Integer.parseInt(args[1]);
					            if(currentPage <= 0) {
					                currentPage = 1;
                                }
                            }
                        }
						final int MAX_RESULTS_PER_PAGE = 10;
						int AVAILABLE_PAGES = (int)Math.ceil((float)commands.size() / (float)MAX_RESULTS_PER_PAGE);
						if(currentPage > AVAILABLE_PAGES) {
							currentPage = AVAILABLE_PAGES;
						}
						List<TextComponent> cmdList;
						if(commands.size() >= MAX_RESULTS_PER_PAGE) {
							int startPageIndex = (currentPage-1) * 10;
							int endPageIndex = startPageIndex + (MAX_RESULTS_PER_PAGE);
							if(endPageIndex > commands.size()) endPageIndex = commands.size() - 1;
							cmdList = commands.subList(startPageIndex, endPageIndex);
						}else{
							cmdList = commands;
						}
                        TextComponent pageComponent = new TextComponent("§3Current Commands: §7(Page §e" + (currentPage) + "/" +AVAILABLE_PAGES + "§7) ");
						pageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7There are a total of §e" + commands.size() + "§7 commands.").create()));
						TextComponent leftArrow = new TextComponent("§9[Previous] ");
						leftArrow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "commands " + (currentPage-1)));
						leftArrow.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to go to the previous page").create()));
                        TextComponent rightArrow = new TextComponent("§9[Next]");
                        rightArrow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "commands " + (currentPage+1)));
                        rightArrow.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to go to the next page").create()));

                        if(currentPage > 1) pageComponent.addExtra(leftArrow);
                        if(currentPage < AVAILABLE_PAGES) pageComponent.addExtra(rightArrow);
						for (TextComponent textComponent : cmdList) {
							pageComponent.addExtra("\n");
							pageComponent.addExtra(textComponent);
						}
						p.spigot().sendMessage(pageComponent);
						break;
					case "keychain":
						final int max_size = 27;
						float size = new InventoryStore(plugin,"keychain_" + p.getName(), max_size).getFillSize();
						double percent = (size/(float) max_size) * 100;
						p.sendMessage(MessageFormat.format("§7Your keychain is §e{0,number,#.##%% §7full. (§e{1,number}/{2,number} slots used§7)", percent, size, max_size));
						break;
					case "help":
						p.sendMessage("§7Hi, terminal is currently in alpha and missing features.");
						p.sendMessage("§7Current Version is: §e" + plugin.getJackzCo().getString("versions.terminal"));
						p.sendMessage("§7Type §ecommands §7to view commands");
						break;
					case "time": {
						final DateFormat dateFormat = new SimpleDateFormat("h:mm a");
						p.sendMessage("§7The time is §e" + dateFormat.format(new Date()));
						break;
					} case "date": {
						final DateFormat dateFormat = new SimpleDateFormat("EEEE, MMMMMM d yyyy ");
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
							HashMap<jTower,Double> towers = jphone.getSortedTowers(p.getLocation());
							for (jTower tower : towers.keySet()) {
								double dist = towers.get(tower);
								if(dist > 1000) {
									p.sendMessage("§7Tower §e" + tower.name + "§7 is §e" + jPhoneMain.getTowerQuality(dist));
									continue;
								}else if(dist > 600) {
									p.sendMessage("§7Tower §e" + tower.name + "§7 (§cPoor§7) is §e" + Math.round(randomizeDouble(dist,p.getLocation())) + "§7 blocks away");
									continue;
								}
								p.sendMessage("§7Tower §e" + tower.name + "§7 is §e" + Math.round(dist) + "§7 blocks away.");
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
						final Inventory trash = Bukkit.createInventory(null, 9 * 3, "jPhone Portable Trash");
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
					}
					case "glow":
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
								Bukkit.getScheduler().runTask(plugin, () -> {
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
								});
								break;
							}  else if (args[1].equalsIgnoreCase("dangers")) {
								Bukkit.getScheduler().runTask(plugin, () -> {
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
								});
								break;
							} else if (args[1].equalsIgnoreCase("all")) {
								Bukkit.getScheduler().runTask(plugin, () -> {
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
								});
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
							ItemMeta meta = itm.getItemMeta();
							ItemStack newItem = nbt.getItem();
							meta.setLore(new ArrayList<>(Collections.singletonList("§cPhone is switched off.")));
							newItem.setItemMeta(meta);
							p.getInventory().setItemInMainHand(newItem);
							p.sendMessage("§7Phone has been switched off.");
						} else {
							nbt.setBoolean("state", true);
							p.sendMessage("§7Phone has been turned on.");
							p.getInventory().setItemInMainHand(nbt.getItem());
						}

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
						TextComponent base = new TextComponent("§cUnknown command was specified.");
						TextComponent help = new TextComponent(" §e[Help]");
						TextComponent commands = new TextComponent(" §9[View Commands]");

						help.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "help"));
						help.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to view help information").create()));

						commands.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "commands"));
						commands.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to view all jPhone Terminal commands.").create()));

						base.addExtra(help);
						base.addExtra(commands);

						p.spigot().sendMessage(base);
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
