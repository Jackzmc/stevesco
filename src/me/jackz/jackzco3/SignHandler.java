package me.jackz.jackzco3;

import me.jackz.jackzco3.lib.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SignHandler implements Listener {
	private final Main plugin;

	SignHandler(Main plugin) { this.plugin = plugin; }
	private static Inventory jstore = Bukkit.createInventory(null, 36, "§cJackzCo Store");

	@EventHandler
	public void onSignClick(PlayerInteractEvent e) {
		Block block = e.getClickedBlock();
		Player p = e.getPlayer();
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getHand() != EquipmentSlot.HAND) return; //check if rightclicking a block with main hand
		if(block == null) return; //make sure block not null

		if(block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
			Sign sign = (Sign) block.getState();
			String[] lines = sign.getLines();
			if(lines[0].startsWith("[jGate")) {
				p.sendMessage(String.join(",",sign.getLines()));
			}else if(lines[0].contains("[jStore]")) {
				getStore(p);

			}
		}

	}
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(e.getClickedInventory().getName().equals(jstore.getName())) {
			ItemStack item = e.getCurrentItem();
			e.setCancelled(true);
			if(item == null) {
				//do nothing?
				return;
			}
			switch(e.getCurrentItem().getType()) {
				case TRIPWIRE_HOOK:
					switch(e.getCurrentItem().getItemMeta().getDisplayName()) { //get NBT?
						case "§6jPhone X":
							break;
						case "§6jPhone XL":
							break;
						default:
							p.sendMessage("§cInvalid phone");
					}
					break;
				default:
					p.sendMessage("§cSorry, that item is not setup");
					p.closeInventory();
			}
		}
	}
	Inventory getStore(Player p) {
		Util util = new Util();
		util.createDisplay(p,Material.TRIPWIRE_HOOK,jstore,1,"§fjPhone 2","§a$Unknown");
		util.createDisplay(p,Material.TRIPWIRE_HOOK,jstore,3,"§fjPhone 2X:","§a$Unknown+$100");
		util.createDisplay(p,Material.PISTON_BASE,jstore,5,"§fjCharger","§7Ultra fast charger");
		p.openInventory(jstore);
		return jstore;
	}
}
