package me.jackz.jackzco3;

import de.Herbystar.TTA.TTA_Methods;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
            p.sendTitle("JackzCo is alive","");
            String motd = plugin.getJackzCo().getString("motd").replace("%player%",(p.getDisplayName() == null) ? p.getName() : p.getDisplayName()).replace("%username%",p.getName());
            System.out.println(motd);
            if(!motd.equals("")) {
                p.sendMessage(motd);
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
