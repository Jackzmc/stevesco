package me.jackz.jackzco3;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MoveHandler implements Listener {
	private Main plugin;
	private Map<String,Boolean> scanImmune = new HashMap<>();
	private Map<String,Boolean> scanTimeImmune = new HashMap<>();

	MoveHandler(Main plugin) {
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
		Boolean isTimeImmune = scanTimeImmune.get(p.getUniqueId().toString());
		if(isImmune == null || isTimeImmune == null) {
			scanImmune.put(p.getUniqueId().toString(),false);
			scanTimeImmune.put(p.getUniqueId().toString(),false); //split this?
			isTimeImmune = false;
			isImmune = false;
		}
		switch(underneathPlayer.getType()) {
			case STONE:
				if(!isImmune && !isTimeImmune && underneathPlayer.getData() == (byte) 2) {
					scanImmune.put(p.getUniqueId().toString(),true);
					plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
						scanTimeImmune.put(p.getUniqueId().toString(), false);
						//isTimeImmune = false;
					},600L);
					scanTimeImmune.put(p.getUniqueId().toString(),true);
					p.sendTitle("Scanning...","By §3JackzCo SuperUltra Security Scanner 3027™");
					List<String> items = plugin.getJackzCo().getStringList("scanner.disallowed");
					List<ItemStack> illegalItems = new ArrayList<>();
					for(ItemStack item  : p.getInventory()) {
						if(item == null) continue;
						for(String blacklisted : items) {
							if(item.getType().toString().equalsIgnoreCase(blacklisted)) {
								illegalItems.add(item);
							}
						}
					}
					if(illegalItems.size() > 0) {
						p.sendMessage(plugin.jackzco_prefix + " §7Detected illegal items: §e" + illegalItems.stream().map(itm -> itm.getType().toString()).collect(Collectors.joining(", ")));
						//todo: lockdown
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
