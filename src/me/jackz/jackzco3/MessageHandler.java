package me.jackz.jackzco3;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MessageHandler implements Listener {
    private Main plugin;

    public MessageHandler(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void chatEvent(AsyncPlayerChatEvent e) {
        e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
        Player p = e.getPlayer();
        String msg = e.getMessage();
        if(msg.startsWith("jackzco")) {
            e.setCancelled(true);
            p.sendMessage(plugin.jackzco_prefix + " §7Thank you for wanting to use JackzCo Chat Commands! They are currently in development, sorry!");
        }
    }
}
