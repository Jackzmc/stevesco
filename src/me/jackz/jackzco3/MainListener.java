package me.jackz.jackzco3;

import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public final class MainListener implements Listener {
    private final Main plugin;
    public MainListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChange(SignChangeEvent e) {
        //On sign creation
        // Player p = e.getPlayer();
        e.setLine(0, ChatColor.translateAlternateColorCodes('&', e.getLine(0)));
        e.setLine(1,ChatColor.translateAlternateColorCodes('&', e.getLine(1)));
        e.setLine(2,ChatColor.translateAlternateColorCodes('&', e.getLine(2)));
        e.setLine(3,ChatColor.translateAlternateColorCodes('&', e.getLine(3)));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            BlockState bs = e.getClickedBlock().getState();
            if ((bs instanceof Sign)) {
                Sign sign = (Sign)bs;
                if (sign.getLine(0).contains("§4§lJackzCo") || sign.getLine(0).contains("§4§l§n")) {
                    String[] options = {"Balance","Withdraw","Deposit","Test1","Test2","Test3"};
                    System.out.println(options[0]);
                    //String current = options[0];
            	 /* String current = "";
            	 int id = -1;
               if(sign.getLine(1).contains(">")) {
            	   current = sign.getLine(1).replace(">", "");
            	   id = 1;
            	   //Above is the name of the value (without > selection id)
            	   //then
               }else if(sign.getLine(2).contains(">")) {
            	  current = sign.getLine(2).replace(">", "");
            	   id = 2;
               }else if(sign.getLine(3).contains(">")) {
            	    current = sign.getLine(3).replace(">", "");
            	   id = 3;
               }else{
            	   p.sendMessage("This sign has nothing selected and therefore invalid");
               }
               int index = -1;
               for (int i=0;i<options.length;i++) {
                   if (options[i].equals(current)) {
                       index = i;
                       break;
                   }
               }
                //Index -> The index of the selected option in array
               index = index + 1;
               sign.setLine(id,options[index]);*/
                    // sign.setLine(id-1, arg1);
                    //ID = selected line number, current = NAME of selected item
                    bs.update();

                }
            }
        }else if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
            BlockState bs = e.getClickedBlock().getState();
            if ((bs instanceof Sign)) {
                Sign sign = (Sign)bs;
                if (sign.getLine(0).contains("§4§lJackzCo") || sign.getLine(0).contains("§4§l§nJackzCo")) {
                    e.setCancelled(true);
                    if(sign.getLine(1).contains(">")) {
                        sign.setLine(1,"Balance");
                        sign.setLine(2, "Withdraw");
                        sign.setLine(3, ">Deposit");
                    }else if(sign.getLine(2).contains(">")) {
                        sign.setLine(1,">Balance");
                        sign.setLine(2, "Withdraw");
                        sign.setLine(3, "Deposit");
                    }else if(sign.getLine(3).contains(">")) {
                        sign.setLine(1,"Balance");
                        sign.setLine(2, ">Withdraw");
                        sign.setLine(3, "Deposit");
                    }else{
                        p.sendMessage("This sign has nothing selected and therefore invalid");
                    }
                    bs.update();

                }
            }
        }
    }
    	/*NOTE BROADCAST:
    	 Bukkit.broadcastMessage("test");

    	 */
}