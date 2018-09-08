package me.jackz.jackzco3;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveHandler implements Listener {
	private Main plugin ;
	public MoveHandler(Main plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location from = e.getFrom();
		Location to = e.getTo() ;

		Block underneathPlayer = p.getWorld().getBlockAt(p.getLocation().subtract(0,1,0));
		if(underneathPlayer == null) return;
		switch(underneathPlayer.getType()) {
			case STONE:
				if(underneathPlayer.getData() == (byte) 6) {
					p.sendTitle("Scanning...","By §3JackzCo SuperUltra Security Scanner 3027™");
				}
		}
	}
}
