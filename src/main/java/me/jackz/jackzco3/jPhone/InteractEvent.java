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
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
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

	@EventHandler
	public void PhoneClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		//below is the stupid way to stop double activation
		if (event.getAction() == Action.PHYSICAL) return;
		ItemStack item = player.getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		if(event.getHand() == null) return;
		if (item.getType().equals(Material.TRIPWIRE_HOOK) && meta != null) {
			if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
				//todo: fix?
				NBTItem nbt = new NBTItem(item);
				if (nbt.hasKey("jphone")) {
					plugin.getLogger().info("OFFHAND PLACEMENT");
					event.setUseItemInHand(Event.Result.DENY);
					event.setCancelled(true);
				}
				return;
			}
		}
		//todo: convert .contains(phoneName) to jPhoneMain.IsJPhone
		if (event.getHand().equals(EquipmentSlot.HAND) && jPhoneMain.isPhone(item)) {
			//spacing so i dont get confused. rightclick
			if (item.getType().equals(Material.TRIPWIRE_HOOK)) {
				event.setCancelled(true);
				//cancel event, then set the item in hand to itself, fixing ghosting
				//p.getInventory().setItemInMainHand(p.getInventory().getItemInMainHand());
				NBTItem nbti = new NBTItem(item);
				if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.PISTON) {
					String verify = new LocationStore(plugin).getString(event.getClickedBlock().getLocation());
					if (verify != null) {
						if(!verify.equals(jPhoneMain.JCHARGER_VERIFY)) player.sendMessage("§7Notice: This is not an official §3jCharger§7 phone.");
						if (nbti.getInteger("battery") == 100) {
							player.sendMessage("§7Your phone is already at §e100%");
							return;
						}
						//charge phone
						Location centerPos = Util.getCenterLocation(event.getClickedBlock().getLocation().add(0, 3, 0));
						player.getWorld().spawnParticle(Particle.SPELL_INSTANT, centerPos, 40, 0.5, 3, 0.5);
						player.sendMessage("§7Charging...");
						item.addUnsafeEnchantment(Enchantment.QUICK_CHARGE, 1);
						Item drop = player.getWorld().dropItem(centerPos, item);
						drop.setPickupDelay(60000);
						for (int i = 0; i < 59; i++) {
							plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
								player.getWorld().spawnParticle(Particle.SPELL_INSTANT, centerPos, 20, 0.0, 3, 0.0);
							}, i);
						}
						plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
							if (player.getInventory().getItemInMainHand().getType() != Material.TRIPWIRE_HOOK) { //TODO?: add to the above loop?
								player.sendMessage("§7Charging aborted - you must hold your phone.");
								return;
							}
							drop.remove();

							player.playSound(event.getClickedBlock().getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
							nbti.setInteger("battery", 100);
							player.sendMessage("§aYour phone has been charged!");
							item.removeEnchantment(Enchantment.QUICK_CHARGE);
							player.getInventory().setItemInMainHand(nbti.getItem());
						}, 60L);
						return;
					}

				}

				if (!nbti.getBoolean("state")) {
					if (player.isSneaking()) {
						if (nbti.getInteger("battery") < 5) {
							player.sendMessage("§7Battery is too low to start");
						} else {
							nbti.setBoolean("state", true);
							player.sendMessage("§7Turned on phone");
						}
						player.getInventory().setItemInMainHand(nbti.getItem());
						return;
					}
					TextComponent toggle_btn = new TextComponent("§a[Click Here]");
					toggle_btn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§7Turns on your phone").create()));
					toggle_btn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/jphone togglephone"));
					ComponentBuilder cb = new ComponentBuilder("§cPhone is turned off. Shift + RightClick or ")
							.append(toggle_btn);
					player.spigot().sendMessage(cb.create());
					return;
				}
				if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (player.isSneaking()) {
						player.openInventory(jPhoneMain.getAppSwitcher(player));
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.2F, 5);
					} else {
						Integer battery = nbti.getInteger("battery");
						player.sendMessage(" ");
						String provider = (nbti.hasKey("provider")) ? nbti.getString("provider") : "jService";
						if (battery == -1) {
							player.sendMessage("§ajPhoneOS V§e" + Main.JOS_VERSION + "§6 | §aProvider §e" + provider + " §6| §aBattery §cDead");
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
							player.spigot().sendMessage(msg.create());
						}
						if (!(nbti.hasKey("owner"))) {
							//hover: "Go to App Switcher->Settings->Owner to claim"
							TextComponent msg = new TextComponent("§cThis device is not claimed. ");
							TextComponent msg_hover = new TextComponent("§c[Hover to learn how to]");
							//message.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "http://spigotmc.org" ) );
							msg_hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("To claim this device go to \n§eapp switcher §rthen §esettings §rthen\n§eowner§r to claim.").create()));
							msg.addExtra(msg_hover);
							player.spigot().sendMessage(msg);
							//Key "owner" not set
						} else {
							//is claimed
							TextComponent msg = new TextComponent("§9This device is claimed. §7Hover for details");
							String name = plugin.getServer().getOfflinePlayer(UUID.fromString(nbti.getString("owner"))).getName();
							ComponentBuilder hover_text = new ComponentBuilder(String.format("§9Device claimed by\n§7%s §e(%s)",nbti.getString("owner"),name));
							hover_text.append("\n§7Device ID: §e" + nbti.getString("ID"));
							hover_text.append("\n§7Model: " + nbti.getString("model"));
							msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover_text.create()));
							player.spigot().sendMessage(msg);
						}
						player.sendMessage(" ");
					}
				} else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
					InventoryStore store = new InventoryStore(plugin, "keychain_" + player.getName(), 9 * 3);
					if (player.isSneaking()) {
						if (!jphone.isInTowerRange(player.getLocation())) {
							player.sendMessage("§cCannot access your keychain, please get in range of a tower.");
							return;
						}
						Inventory inv = store.loadInv();
						player.openInventory(inv);
					} else {
						//https://gist.github.com/Caeden117/92223ecd39b61bd3310aee64e0dfd0d0
						HashMap<jTower, Double> towers = jphone.getSortedTowers(player.getLocation());
						if (towers.isEmpty()) {
							player.sendMessage("§cCould not locate any nearby towers");
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
						player.sendMessage(msgs.toArray(new String[0]));
					}
				}
			} else if (player.getInventory().getItemInMainHand().getType() == Material.PISTON && event.getAction() == Action.RIGHT_CLICK_AIR) {
				if (meta.getDisplayName().equals("§fjCharger")) {
					event.setCancelled(true);
					player.sendMessage("§7Please right click on a gold block to setup the §ejCharger");
				}
			} else if (Util.checkItem(item, Material.BONE, "§3jWrench")) {
				event.setCancelled(true);
				if (player.isSneaking()) {
					ItemMeta phoneMeta = item.getItemMeta();
					item.setType(Material.TRIPWIRE_HOOK);
					phoneMeta.setDisplayName(jPhoneMain.phoneName); //check if 2X
					item.setItemMeta(phoneMeta);
					player.getInventory().setItemInMainHand(item);
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
				if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
					Block block = event.getClickedBlock();
					BlockData blockData = block.getBlockData();
					if(blockData instanceof Directional) {
						try {
							Directional directional = (Directional) blockData;
							if(directional.getFaces().contains(BlockFace.UP)) {
								switch (directional.getFacing()) {
									case UP: directional.setFacing(BlockFace.DOWN);
									case DOWN: directional.setFacing(BlockFace.NORTH);
									case NORTH: directional.setFacing(BlockFace.EAST);
									case EAST: directional.setFacing(BlockFace.SOUTH);
									case SOUTH: directional.setFacing(BlockFace.WEST);
									case WEST: directional.setFacing(BlockFace.UP);
								}
							}else{
								switch (directional.getFacing()) {
									case NORTH: directional.setFacing(BlockFace.EAST);
									case EAST: directional.setFacing(BlockFace.SOUTH);
									case SOUTH: directional.setFacing(BlockFace.WEST);
									case WEST: directional.setFacing(BlockFace.NORTH);
								}
							}
							block.setBlockData(directional);
						} catch (IllegalArgumentException ex) {
							plugin.getLogger().warning("Wrench failure: " + ex.toString());
							player.sendMessage("§7Uh oh! Something went wrong. §c" + ex.toString());
						}
					}else{
						player.sendMessage("§cThat block is not supported.");
					}

				} else {
					player.sendMessage("§cPlease left/right click a block");
				}
			} else if (item.getType().equals(Material.TORCH)) {
				event.setCancelled(true);
				if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
					ItemStack phone = player.getInventory().getItemInMainHand();
					ItemMeta phoneMeta = phone.getItemMeta();
					phone.setType(Material.TRIPWIRE_HOOK);
					if (phoneMeta != null) {
						phoneMeta.setDisplayName(jPhoneMain.phoneName); //check if 2X
					}
					phone.setItemMeta(phoneMeta);
					player.getInventory().setItemInMainHand(phone);
				}

			}
		}


	}

}
