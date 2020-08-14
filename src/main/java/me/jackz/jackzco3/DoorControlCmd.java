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
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class DoorControlCmd implements CommandExecutor {
    private final Main plugin;
    DoorControlCmd(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("must be a player sorry");
            return true;
        }
        Player player = (Player) sender;
        Number level = 1;
        if(args.length > 0) {
            if(Util.isInteger(args[0])) {
                level = Integer.parseInt(args[0]);
            }else {
                player.sendMessage("§cInvalid Syntax. §e/getid [level]");
            }
            return true;
        }
        ItemStack idCard = new ItemStack(Material.PAPER);
        ItemMeta idCardMeta = idCard.getItemMeta();
        idCardMeta.setDisplayName("§6ID Card");
        List<String> lore = new ArrayList<>();
        lore.add("§7UUID " + player.getUniqueId());
        lore.add("§7Level " + level.toString());

        idCardMeta.setLore(lore);
        idCard.setItemMeta(idCardMeta);
        player.getInventory().addItem(idCard);

        List<String> validRegions = plugin.getJackzCo().getStringList("regions");
        player.sendMessage("§7This §3Steves Co §7ID Card will only work for these regions: §e" + String.join(",",validRegions) );

        return true;
    }
}
