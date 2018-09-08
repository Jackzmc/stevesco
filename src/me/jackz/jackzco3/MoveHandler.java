package me.jackz.jackzco3;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveHandler implements Listener {
	private Main plugin;
	private Map<String,Boolean> scanImmune = new HashMap<>();

	public MoveHandler(Main plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent e) {

		Player p = e.getPlayer();
		Location from = e.getFrom();
		Location to = e.getTo();
		if(!plugin.checkRegion(p.getLocation(),"stevesco")) return; //check if in whitelisted region
		Block underneathPlayer = p.getWorld().getBlockAt(p.getLocation().subtract(0,1,0));
		if(underneathPlayer == null) return;
		Boolean isImmune = scanImmune.get(p.getUniqueId().toString());
		if(isImmune == null) {
			scanImmune.put(p.getUniqueId().toString(),false);
			isImmune = false;
		}
		switch(underneathPlayer.getType()) {
			case STONE:
				if(!isImmune && underneathPlayer.getData() == (byte) 6) {
					scanImmune.put(p.getUniqueId().toString(),true);
					p.sendTitle("Scanning...","By §3JackzCo SuperUltra Security Scanner 3027™");
					List<String> items = plugin.getJackzCo().getStringList("scanner.disallowed");
					for(ItemStack item  : p.getInventory()) {
						for(String blacklisted : items) {
							if(item.getType().toString().equalsIgnoreCase(blacklisted)) {
								p.sendMessage("Illegal item: §e" + blacklisted);
							}
						}
					}

				}
				break;
			default:
				//if diff block
				if(isImmune) {
					//if immune, undo
					scanImmune.put(p.getUniqueId().toString(),false);
				}
		}
	}
}
