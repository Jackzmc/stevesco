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

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MessageHandler implements Listener {

    MessageHandler(Main plugin) {
    }
    @EventHandler
    public void chatEvent(AsyncPlayerChatEvent e) {
        e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage().replace(":tm:","™").replace(".\\","/")));
        Player p = e.getPlayer();
        String msg = e.getMessage();
        if(msg.startsWith("jackzco")) {
            e.setCancelled(true);
            p.sendMessage(Main.JACKZCO_PREFIX + " §7Thank you for wanting to use JackzCo Chat Commands! They are currently in development, sorry!");
        }
    }
}
