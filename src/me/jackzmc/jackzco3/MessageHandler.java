package me.jackzmc.jackzco3;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MessageHandler implements Listener {
    private Main plugin;

    public MessageHandler(Main plugin) {
        this.plugin = plugin;
    }
    public void chatEvent(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();
        if(msg.startsWith("jackzco")) {
            p.sendMessage(plugin.jackzco_prefix + " §7Thank you for wanting to use JackzCo Chat Commands! They are currently in development, sorry!");
        }
    }
}