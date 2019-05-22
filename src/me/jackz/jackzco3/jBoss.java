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
import org.apache.commons.lang.WordUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class jBoss implements CommandExecutor {
	private final Main plugin;
	private String prefix;
	jBoss(Main plugin) {
		prefix = Main.jackzco_prefix;
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(sender instanceof Player) {
			Player p =(Player) sender;
			if(args.length == 0) {
				return false;
			}
			switch(args[0].toLowerCase()) {
				case "create": {
					if(p.isOp()) {
						//usage: /jboss <entity> <health> [custom name]
						if(args.length < 3) {
							p.sendMessage("§cInvalid Arguments. Usage: /jboss create <entity> <health> [custom name]");
						}else {
							EntityType ent = getEntityByName(args[1]);
							if (Util.isInteger(args[2])) {
								int hp = Integer.parseInt(args[2]);
								if(ent == null) {
									p.sendMessage("§cUnknown entity named §e" + args[1]);
								}else if(hp <= 0) {
									p.sendMessage("§cInvalid health, must be a whole number > 0");
								}else {
									String customName = (args[3] != null) ? args[3].replaceAll("_", " ") : WordUtils.capitalize(ent.name().replaceAll("_", ""));
									LivingEntity spawned_entity = (LivingEntity) p.getWorld().spawnEntity(p.getTargetBlock(null,10).getLocation(),ent);
									spawned_entity.setCustomName(customName);
									spawned_entity.setCustomNameVisible(true);
									AttributeInstance att_maxhp = spawned_entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
									att_maxhp.setBaseValue(hp);
									spawned_entity.setHealth(hp);
									p.sendMessage("§aSuccessfully created boss named §e\"" + customName + "\"");
									BossBar bar = p.getServer().getBossBar(NamespacedKey.minecraft("jboss"));
									if(bar == null) {
										bar = p.getServer().createBossBar("§3jBoss", BarColor.BLUE, BarStyle.SOLID);
										for(Player pl : p.getServer().getOnlinePlayers()) {
											bar.addPlayer(pl);
										}
									}
									bar.setTitle(String.format("%s [%d HP]",customName,hp));
									bar.setProgress(1);
								}
							} else {
								p.sendMessage("§cInvalid health, must be a whole number > 0");
							}
						}
					}else{
						p.sendMessage("§cYou must be an OP to use this.");
					}
					break;
				} default:
					p.sendMessage("§7Commands:\n§ehelp\ncreate - §7/jboss create <entity> <health> [custom name]");
			}

		}else{
			sender.sendMessage("You must be a player to use this");

		}
		return true;
	}

	private EntityType getEntityByName(String name) {
		for (EntityType type : EntityType.values()) {
			if(type.name().equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}
}
