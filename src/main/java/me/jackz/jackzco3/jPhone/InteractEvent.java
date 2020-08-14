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
import me.jackz.jackzco3.lib.LocationStore;
import me.jackz.jackzco3.lib.Util;
import me.jackz.jackzco3.lib.jTower;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class InteractEvent implements Listener {


	private final Main plugin;
	private jPhoneMain jphone;
	InteractEvent(Main plugin,jPhoneMain jphone) {
		this.plugin = plugin;
		this.jphone = jphone;
	}

	private Random random = new Random();
	private double randomnum() {
		return random.nextBoolean() ? random.nextDouble() : -random.nextDouble();
	}
	@EventHandler
	public void PhoneClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		//below is the stupid way to stop double activation
		if (e.getAction() == Action.PHYSICAL) return;
		ItemStack item = p.getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		if(e.getHand() == null) return;
		if(e.getHand().equals(EquipmentSlot.OFF_HAND)) {

		}
		if (item.getType().equals(Material.TRIPWIRE_HOOK) && meta != null) {
			if(e.getHand().equals(EquipmentSlot.OFF_HAND)) {
				//todo: fix?
				ItemStack offhand = p.getInventory().getItemInOffHand();
				ItemMeta offhand_meta = offhand.getItemMeta();
				if(offhand_meta != null && meta.getDisplayName().equals(jPhoneMain.phoneName)) {
					plugin.getLogger().info("OFFHAND PLACEMENT");
					e.setUseItemInHand(Event.Result.DENY);
					e.setCancelled(true);
					return;
				}
			}
			//todo: convert .contains(phoneName) to jPhoneMain.IsJPhone
			if (e.getHand().equals(EquipmentSlot.HAND) && meta.getDisplayName().contains(jPhoneMain.phoneName)) {
				//spacing so i dont get confused. rightclick
				if (item.getType().equals(Material.TRIPWIRE_HOOK)) {
					e.setCancelled(true);
					//cancel event, then set the item in hand to itself, fixing ghosting
					//p.getInventory().setItemInMainHand(p.getInventory().getItemInMainHand());
					NBTItem nbti = new NBTItem(item);
					if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.PISTON) {
						String verify = new LocationStore(plugin).getString(e.getClickedBlock().getLocation());
						if (verify != null) {
							if(!verify.equals(jPhoneMain.JCHARGER_VERIFY)) p.sendMessage("§7Notice: This is not an official §3jCharger§7 phone.");
							if (nbti.getInteger("battery") == 100) {
								p.sendMessage("§7Your phone is already at §e100%");
								return;
							}
							//charge phone
							Location centerPos = Util.getCenterLocation(e.getClickedBlock().getLocation().add(0, 3, 0));
							p.getWorld().spawnParticle(Particle.SPELL_INSTANT, centerPos, 40, 0.5, 3, 0.5);
							p.sendMessage("§7Charging...");
							item.addUnsafeEnchantment(Enchantment.QUICK_CHARGE, 1);
							Item drop = p.getWorld().dropItem(centerPos, item);
							drop.setPickupDelay(60000);
							for (int i = 0; i < 59; i++) {
								plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
									p.getWorld().spawnParticle(Particle.SPELL_INSTANT, centerPos, 20, 0.0, 3, 0.0);
								}, i);
							}
							plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
								if (p.getInventory().getItemInMainHand().getType() != Material.TRIPWIRE_HOOK) { //TODO?: add to the above loop?
									p.sendMessage("§7Charging aborted - you must hold your phone.");
									return;
								}
								drop.remove();

								p.playSound(e.getClickedBlock().getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
								nbti.setInteger("battery", 100);
								p.sendMessage("§aYour phone has been charged!");
								item.removeEnchantment(Enchantment.QUICK_CHARGE);
								p.getInventory().setItemInMainHand(nbti.getItem());
							}, 60L);
							return;
						}

					}

					if (!nbti.getBoolean("state")) {
						if (p.isSneaking()) {
							if (nbti.getInteger("battery") < 5) {
								p.sendMessage("§7Battery is too low to start");
							} else {
								nbti.setBoolean("state", true);
								p.sendMessage("§7Turned on phone");
							}
							p.getInventory().setItemInMainHand(nbti.getItem());
							return;
						}
						TextComponent toggle_btn = new TextComponent("§a[Click Here]");
						toggle_btn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§7Turns on your phone").create()));
						toggle_btn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/jphone togglephone"));
						ComponentBuilder cb = new ComponentBuilder("§cPhone is turned off. Shift + RightClick or ")
								.append(toggle_btn);
						p.spigot().sendMessage(cb.create());
						return;
					}
					if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						if (p.isSneaking()) {
							p.openInventory(jPhoneMain.getAppSwitcher(p));
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.2F, 5);
						} else {
							Integer battery = nbti.getInteger("battery");
							p.sendMessage(" ");
							String provider = (nbti.hasKey("provider")) ? nbti.getString("provider") : "jService";
							if (battery == -1) {
								p.sendMessage("§ajPhoneOS V§e" + Main.JOS_VERSION + "§6 | §aProvider §e" + provider + " §6| §aBattery §cDead");
							} else {
								TextComponent battery_msg = new TextComponent("§aBattery §e" + battery + "%");
								battery_msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§6Estimated Hours Left:\n§7n/a\n§6Last Charge:\n§7n/a\n\n§cClick to turn off phone").create()));
								battery_msg.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND,"/jphone togglephone"));

								TextComponent provider_msg = new TextComponent("§aProvider §e" + provider);
								provider_msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§6Coverage:\n§7Undetermined\n§6Data Plan:\n§70 GB/∞ GB Used").create()));

								TextComponent version_msg = new TextComponent("§a§ljPhone OS §r§ev" + Main.JOS_VERSION);
								version_msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§6Terminal Version:\n").append(Main.TERMINAL_VERSION).color(ChatColor.YELLOW).create()));

								ComponentBuilder msg = new ComponentBuilder(version_msg)
										.append("§6 | ").append(provider_msg).color(ChatColor.YELLOW)
										.append("§6 | ").append(battery_msg);
								p.spigot().sendMessage(msg.create());
							}
							if (!(nbti.hasKey("owner"))) {
								//hover: "Go to App Switcher->Settings->Owner to claim"
								TextComponent msg = new TextComponent("§cThis device is not claimed. ");
								TextComponent msg_hover = new TextComponent("§c[Hover to learn how to]");
								//message.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "http://spigotmc.org" ) );
								msg_hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("To claim this device go to \n§eapp switcher §rthen §esettings §rthen\n§eowner§r to claim.").create()));
								msg.addExtra(msg_hover);
								p.spigot().sendMessage(msg);
								//Key "owner" not set
							} else {
								//is claimed
								TextComponent msg = new TextComponent("§9This device is claimed. §7Hover for details");
								String name = plugin.getServer().getOfflinePlayer(UUID.fromString(nbti.getString("owner"))).getName();
                                ComponentBuilder hover_text = new ComponentBuilder(String.format("§9Device claimed by\n§7%s §e(%s)",nbti.getString("owner"),name));
                                hover_text.append("\n§7Device ID: §e" + nbti.getString("ID"));
								msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover_text.create()));
								p.spigot().sendMessage(msg);
							}
							p.sendMessage(" ");
						}
					} else if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
						InventoryStore store = new InventoryStore(plugin, "keychain_" + p.getName(), 9 * 3);
						if (p.isSneaking()) {
							if (!jphone.isInTowerRange(p.getLocation())) {
								p.sendMessage("§cCannot access your keychain, please get in range of a tower.");
								return;
							}
							Inventory inv = store.loadInv();
							p.openInventory(inv);
						} else {
							//https://gist.github.com/Caeden117/92223ecd39b61bd3310aee64e0dfd0d0
							HashMap<jTower, Double> towers = jphone.getSortedTowers(p.getLocation());
							if (towers.isEmpty()) {
								p.sendMessage("§cCould not locate any nearby towers");
								return;
							}
							List<String> msgs = new ArrayList<>();
							int accessible = 0;
							for (jTower tower : towers.keySet()) {
								Double distances = towers.get(tower);
								if (distances <= 600) {
									msgs.add("§7Tower §e" + tower.name + "§7:§e " + jPhoneMain.getTowerQuality(distances) + " §7(" + Math.round(distances) + " blocks)");
									accessible++;
								}
							}
							String provider = (nbti.hasKey("provider")) ? nbti.getString("provider") : "jService";
							msgs.add(0, "§e" + accessible + "§7/§e" + towers.size() + "§7 towers are shown for provider: §e" + provider);
							p.sendMessage(msgs.toArray(new String[0]));
						}
					}
				} else if (p.getInventory().getItemInMainHand().getType() == Material.PISTON && e.getAction() == Action.RIGHT_CLICK_AIR) {
					if (meta != null && meta.getDisplayName().equals("§fjCharger")) {
						e.setCancelled(true);
						p.sendMessage("§7Please right click on a gold block to setup the §ejCharger");
						return;
					}
				} else if (Util.checkItem(item, Material.BONE, "§3jWrench")) {
					e.setCancelled(true);
					if (p.isSneaking()) {
						ItemMeta phoneMeta = item.getItemMeta();
						item.setType(Material.TRIPWIRE_HOOK);
						phoneMeta.setDisplayName(jPhoneMain.phoneName); //check if 2X
						item.setItemMeta(phoneMeta);
						p.getInventory().setItemInMainHand(item);
						return;
					}
					List<Material> allowedBlocks = new ArrayList<>(Arrays.asList(
							Material.PISTON,
							Material.STICKY_PISTON,
							Material.IRON_DOOR,
							Material.DISPENSER,
							Material.CHEST,
							Material.OAK_DOOR,
							Material.DROPPER
					));
					if (e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
						Block b = e.getClickedBlock();
						if (!allowedBlocks.contains(b.getType())) {
							p.sendMessage("§cInvalid block");
							//p.sendMessage("§cValid blocks: §e" + String.join(",",allowedBlocks::toString));
							return;
						}
						try {
							/*BlockFace[] directions = {BlockFace.NORTH,BlockFace.EAST,BlockFace.SOUTH,BlockFace.WEST,BlockFace.UP,BlockFace.DOWN};
							Directional dir = (Directional) b;
							BlockFace direction = dir.getFacing();
							dir.setFacingDirection(BlockFace.EAST_NORTH_EAST);*/
							/*if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
								int data = b.getData();
								b.setData((byte) ((data == 5) ? 0 : ++data));
							} else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
								int data = b.getData();
								b.setData((byte) ((data == 0) ? 5 : --data));
							}*/
							p.sendMessage("§cSorry, but the jWrench has been disabled until further notice.");

						} catch (IllegalArgumentException ex) {
							plugin.getLogger().warning("Wrench failure: " + ex.toString());
							p.sendMessage("§7Uh oh! Something went wrong. §c" + ex.toString());
						}

					} else {
						p.sendMessage("§cPlease left/right click a block");
					}
				} else if (item.getType().equals(Material.TORCH) && meta.getDisplayName().equals("jLight")) {
					e.setCancelled(true);
					if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
						ItemStack phone = p.getInventory().getItemInMainHand();
						ItemMeta phoneMeta = phone.getItemMeta();
						phone.setType(Material.TRIPWIRE_HOOK);
						if (phoneMeta != null) {
							phoneMeta.setDisplayName(jPhoneMain.phoneName); //check if 2X
						}
						phone.setItemMeta(phoneMeta);
						p.getInventory().setItemInMainHand(phone);
					}

				}
				//below is the stupid way to stop offhand placement. I don't know if two setcancels will fuck it up but i hope not
			}
		}

	}

}
