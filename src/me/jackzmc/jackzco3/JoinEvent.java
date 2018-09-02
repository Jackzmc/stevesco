package me.jackzmc.jackzco3;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {
    private final Main plugin;

    public JoinEvent(Main plugin) {
        this.plugin = plugin; // Store the plugin in situations where you need it.
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        FileConfiguration config = plugin.getConfig();
        String motd = config.getString("motd");
        motd = motd.replace("%player%",p.getDisplayName());
        motd = motd.replace("%username",p.getName());
        System.out.println(motd);
        if(motd != "") {
            p.sendMessage(motd);
        }
    }
}
