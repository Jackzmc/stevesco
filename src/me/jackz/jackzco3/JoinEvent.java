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

import de.Herbystar.TTA.TTA_Methods;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Iterator;
import java.util.logging.Level;

public class JoinEvent implements Listener {
    private final Main plugin;

    JoinEvent(Main plugin) {
        this.plugin = plugin; // Store the plugin in situations where you need it.
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        try {
            if(plugin.getJackzCo().getString("motd") == null) return;
            Player p = e.getPlayer();
            TTA_Methods.sendTablist(p, "§4Jackz Craft Testing Server", "die nerd");
            //noinspection deprecation
            p.sendTitle("JackzCo is alive","",0,40,0);
            String motd = plugin.getJackzCo().getString("motd").replace("%player%",(p.getDisplayName() == null) ? p.getName() : p.getDisplayName()).replace("%username%",p.getName());
            System.out.println(motd);
            if(!motd.equals("")) {
                p.sendMessage(motd);
            }
            Iterator<KeyedBossBar> bossbars = p.getServer().getBossBars();
            while(bossbars.hasNext()) {
               BossBar b = bossbars.next();
               if(b.getTitle().startsWith("§3")) {
                   b.removePlayer(p);
               }
            }
        }catch(Exception ex) {
            Player p = e.getPlayer();
            if(p.getUniqueId().toString().equals("b0c16432-67a6-4e3d-b49a-61b323c49b03")) {
                p.sendMessage("JoinEvent error: §c" + ex.toString());
            }
            plugin.getLogger().log(Level.INFO,"JoinEvent Error",ex);
        }

    }
}
