package me.jackz.jackzco3;

import de.tr7zw.itemnbtapi.ItemNBTAPI;
import de.tr7zw.itemnbtapi.NBTItem;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.inventivetalent.glow.GlowAPI;

import java.util.*;

public class jPhone implements Listener,CommandExecutor {
    private final Main plugin;
    private static Inventory keychain = Bukkit.createInventory(null, 9, "Inventory");
    private static Inventory appswitcher = Bukkit.createInventory(null, 36, "§4jPhone App Switcher");
    String phoneName = "§3jPhone";

    jPhone(Main plugin) {
        this.plugin = plugin; // Store the plugin in situations where you need it.
    }

    Random random = new Random();
    private double randomnum() {
        return random.nextBoolean() ? random.nextDouble() : -random.nextDouble();
    }
    @EventHandler
    public void PhoneClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        //below is the stupid way to stop double activation
        if(e.getAction() == Action.PHYSICAL) return;
        if(e.getHand().equals(EquipmentSlot.HAND)) {
            //spacing so i dont get confused. rightclick
            if(p.getInventory().getItemInMainHand().getType().equals(Material.TRIPWIRE_HOOK) && p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(phoneName)) {
                e.setCancelled(true);
                //cancel event, then set the item in hand to itself, fixing ghosting
                p.getInventory().setItemInMainHand(p.getInventory().getItemInMainHand());
                ItemStack item =  p.getInventory().getItemInMainHand();
                NBTItem nbti = ItemNBTAPI.getNBTItem(item);
                if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if(p.isSneaking()) {
                        //p.sendMessage("§cjPhone App Switcher not ready yet");

                        //plugin.appswitcher.setItem(1,  new ItemStack(Material.BOOK_AND_QUILL,1));
                        //plugin.appswitcher.setItem(3,  new ItemStack(Material.TORCH,1));
                        plugin.createDisplay(p,Material.BOOK_AND_QUILL,appswitcher,10,"&9Settings","&7Configure your phone");
                        plugin.createDisplay(p,Material.SIGN,appswitcher,12,"&9Terminal","&7Open the console/terminal");
                        plugin.createDisplay(p,Material.TORCH,appswitcher,14,"&9Flashlight","&7Illuminate the world!|&7(Left click to turn off)");
                        p.openInventory(appswitcher);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.2F, 5);

                        //gui ? Gui.
                    }else{
                        Integer battery = nbti.getInteger("battery");

                        p.sendMessage(" ");
                        if(battery == -1) {
                            p.sendMessage("§ajPhoneOS Version §e" + plugin.getJackzCo().getString("versions.jphone") + ChatColor.GOLD + " | " + ChatColor.GREEN + "Battery " + ChatColor.RED + "Dead");
                        }else{
                            p.sendMessage("§ajPhoneOS Version §e" + plugin.getJackzCo().getString("versions.jphone") + ChatColor.GOLD + " | " + ChatColor.GREEN + "Battery " + ChatColor.YELLOW + battery + "%");
                        }
                        p.sendMessage("§7Check your data by /jackzco jcloud info");


                        if(!(nbti.hasKey("owner"))) {

                            //hover: "Go to App Switcher->Settings->Owner to claim"
                            TextComponent msg = new TextComponent("§cThis device is not claimed. ");
                            TextComponent msg_hover = new TextComponent("§c[Hover to learn how to]");
                            //message.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "http://spigotmc.org" ) );
                            msg_hover.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("To claim this device go to \n§eapp switcher §rthen §esettings §rthen\n§eowner§r to claim.").create() ) );

                            msg.addExtra(msg_hover);
                            p.spigot().sendMessage(msg);
                            //Key "owner" not set
                        }else{
                            //is claimed
                            TextComponent msg = new TextComponent("§9Notice §7This device is claimed. Hover for details");
                            msg.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§9Device claimed by\n§7" + nbti.getString("owner")).create() ) );

                            p.spigot().sendMessage(msg);
                        }
                        p.sendMessage(" ");
                    }
                }else if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if(p.isSneaking()) {
                        p.sendMessage("§cjPhone KeyChain is not ready yet");

                        p.openInventory(keychain);
                    }else{

                        p.sendMessage("§cCould not locate any nearby towers");
                    }
                }
            }else if(p.getInventory().getItemInMainHand().getType() == Material.NETHER_STAR) {
	            if (p.isSneaking()) {
		            if(!plugin.checkRegion(p.getLocation(),"horsechaos")) {
		            	p.sendMessage("§cYou must be in the horsechaos region");
		            	return;
		            }
	            	if(e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
			            for (int i = 0; i < 50; i++) {
				            double rnd = Math.random();
				            Material mt;
				            if (rnd < .25) {
					            mt = Material.WOOL;
				            } else if (rnd >= .25 && rnd < .5) {
					            mt = Material.STAINED_CLAY;
				            } else if (rnd >= .5 && rnd < .75) {
					            mt = Material.CONCRETE;
				            } else {
					            mt = Material.CONCRETE_POWDER;
				            }
				            @SuppressWarnings("deprecation")
				            FallingBlock fb = p.getWorld().spawnFallingBlock(p.getEyeLocation(), mt, (byte) random.nextInt(16));
				            fb.setHurtEntities(false);
				            fb.setDropItem(false);
				            GlowAPI.setGlowing(fb, GlowAPI.Color.WHITE, p);
				            Vector v = p.getEyeLocation().getDirection();
				            v = v.add(new Vector(random.nextInt((1 - -1) + 1) + -1,0,random.nextInt((1 - -1) + 1) + -1));
				            fb.setVelocity(v.multiply(2));

			            }
	            		return;
		            }
		            Block targetbk = p.getTargetBlock(null, 100);
		            Location targetloc;
		            if (targetbk == null) {
			            p.sendMessage("§cCan't find block, must be within 100 blocks");
			            return;
		            }else if(targetbk.getType().equals(Material.GLASS)) {
		            	p.sendMessage("§cGlass is blacklisted, try some other block.");
		            	return;
		            }
		            targetloc = targetbk.getLocation();
		            if(!plugin.checkRegion(targetloc,"horsechaos")) {
			            p.sendMessage("§cYou must be in the horsechaos region");
			            return;
		            }
		            p.getWorld().createExplosion(targetloc, 0);

		            for (int i = 0; i < 50; i++) {
			            double rnd = Math.random();
			            Material mt;
			            if (rnd < .25) {
				            mt = Material.WOOL;
			            } else if (rnd >= .25 && rnd < .5) {
				            mt = Material.STAINED_CLAY;
			            } else if (rnd >= .5 && rnd < .75) {
				            mt = Material.CONCRETE;
			            } else {
				            mt = Material.CONCRETE_POWDER;
			            }
			            @SuppressWarnings("deprecation")
			            FallingBlock fb = p.getWorld().spawnFallingBlock(targetloc, mt, (byte) random.nextInt(16));
			            fb.setHurtEntities(false);
			            fb.setDropItem(false);
			            GlowAPI.setGlowing(fb, GlowAPI.Color.WHITE, p);
			            Vector v = new Vector(randomnum(), randomnum(), randomnum());
			            fb.setVelocity(v.multiply(2));
		            }
	            } else {
		            if(!plugin.checkRegion(p.getLocation(),"horsechaos")) {
			            p.sendMessage("§cYou must be in the horsechaos region");
			            return;
		            }
		            final Horse entity = (Horse) p.getWorld().spawnEntity(p.getEyeLocation(),EntityType.HORSE);
		            entity.setStyle(Horse.Style.NONE);
		            e.setCancelled(true);
		            entity.setVelocity(p.getEyeLocation().getDirection().multiply((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) ? 5 : 2));
		            Bukkit.getScheduler().runTaskLater(plugin, () -> {
			            entity.getWorld().createExplosion(entity.getLocation(), 0);
			            entity.remove();
			            for (int i = 0; i < 100; i++) {
				            double rnd = Math.random();
				            Material mt;
				            if (rnd < .25) {
					            mt = Material.WOOL;
				            } else if (rnd >= .25 && rnd < .5) {
					            mt = Material.STAINED_CLAY;
				            } else if (rnd >= .5 && rnd < .75) {
					            mt = Material.CONCRETE;
				            } else {
					            mt = Material.CONCRETE_POWDER;
				            }
				            @SuppressWarnings("deprecation")
				            FallingBlock fb = entity.getWorld().spawnFallingBlock(entity.getLocation(), mt, (byte) random.nextInt(16));
				            fb.setHurtEntities(false);
				            fb.setDropItem(false);
				            GlowAPI.setGlowing(fb, GlowAPI.Color.WHITE, p);
				            Vector v = new Vector(randomnum(), randomnum(), randomnum());
				            fb.setVelocity(v.multiply(2));
			            }
		            }, 15);
	            }
            }else if(p.getInventory().getItemInMainHand().getType().equals(Material.TORCH) && p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§fjLight") ) {
            	e.setCancelled(true);
                if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                    ItemStack phone = p.getInventory().getItemInMainHand();
                    ItemMeta phoneMeta = phone.getItemMeta();
                    phone.setType(Material.TRIPWIRE_HOOK);
                    phoneMeta.setDisplayName(phoneName);
                    phone.setItemMeta(phoneMeta);
                    p.getInventory().setItemInMainHand(phone);
                }

            }
            //below is the stupid way to stop offhand placement. I don't know if two setcancels will fuck it up but i hope not
        }else if(e.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if(p.getInventory().getItemInMainHand().getType() == Material.TRIPWIRE_HOOK && p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(phoneName)) {
                e.setCancelled(true);
            }
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            ItemStack phone =  p.getInventory().getItemInMainHand();
            NBTItem nbti = ItemNBTAPI.getNBTItem(phone);
            if(args.length == 0) {
                sender.sendMessage("§cNo arguments specified. Try /jphone help");
                return true;
            }
			switch(args[0].toLowerCase()) {
				case "help":
					ItemStack helpbook = new ItemStack(Material.WRITTEN_BOOK);
					BookMeta helpMeta = (BookMeta) helpbook.getItemMeta();
					helpMeta.setTitle("jPhone Terminal Help");
					helpMeta.setAuthor("JackzCo");
					List<String> pages = new ArrayList<>(Arrays.asList(
							"Thank you for using the jPhone!\n§cNotice: Help is not complete\n\n§6claim §7- claims the phone as yours\n§6trash §7 -portable trash collection\n§6dangers §7- scans for any nearby dangers\n§6glow §7- highlights certain parameters",
							"§6charge §7- charges your phone with battery\n§6state §7- turn on/off your phone\n§6lookup §7- Lookup the username of the owner of phone",
							"§3Notes after purchase:\n\nIf you bought this device from any retail store, you will need to claim the device as yours. This is as simple as typing \n§6/jphone claim"
					));

					helpMeta.setPages(pages);
					helpbook.setItemMeta(helpMeta);
					p.getInventory().addItem(helpbook);
					break;
				case "own":
				case "claim":
					if (nbti.hasKey("owner")) {
						p.sendMessage("§cThis device is claimed by: §e" + nbti.getString("owner"));
					} else {
						nbti.setString("owner", p.getUniqueId().toString());
						p.sendMessage("§7Claimed device as §e" + p.getUniqueId().toString());
						p.getInventory().setItemInMainHand(nbti.getItem());
					}
					break;
				case "trash":
					Inventory trash = Bukkit.createInventory(null, 9 * 3, "jPhone Portable Trash");
					p.openInventory(trash);
					break;
				case "dangers":
					if(plugin.getServer().getPluginManager().getPlugin("GlowAPI") == null) {
						p.sendMessage("§cThis feature is disabled, missing plugin §eGlowAPI");
						return true;
					}
	                boolean pvpenabled = p.getWorld().getPVP();
	                List<Entity> entities = new ArrayList<>();
	                for(Entity ent : p.getNearbyEntities(50, 50, 50)) {
	                    if(ent instanceof Monster) {
	                        entities.add(ent);
	                        GlowAPI.setGlowing(ent, GlowAPI.Color.DARK_RED,p);
	                    }else if(ent instanceof Player && pvpenabled) {
	                        //if ent is player and PVP is enabled for that world
		                    entities.add(ent);
		                    GlowAPI.setGlowing(ent, GlowAPI.Color.RED,p);
	                    }

	                }
					Bukkit.getScheduler().runTaskLater(plugin, () -> {
						for(Entity e: entities) {
							if(GlowAPI.isGlowing(e,p)) {
								GlowAPI.setGlowing(e,false,p);
							}
						}
					}, (30 * 20L));
	                p.sendMessage("§cFound " + entities.size() + " dangers");
					break;
				case "glow":
					if(plugin.getServer().getPluginManager().getPlugin("GlowAPI") == null) {
						p.sendMessage("§cThis feature is disabled, missing plugin §eGlowAPI");
						return true;
					}
	                if(args.length > 1) {
	                    if(args[1].equalsIgnoreCase("players")) {

	                        int count = 0;
	                        for(Player player : Bukkit.getOnlinePlayers()){
	                            if(!(GlowAPI.isGlowing(player, p))) {
	                                GlowAPI.setGlowing(player, GlowAPI.Color.WHITE, p);
	                                count += 1;
	                            }
	                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
	                                if(GlowAPI.isGlowing(player, p)) {
	                                    GlowAPI.setGlowing(player, false, p);
	                                }
	                            }, (30*20L));

	                        }
	                        p.sendMessage("§7Made §e" + count + "§7 players glow for §e30§7 seconds" );
	                    }else if(args[1].equalsIgnoreCase("entities")) {
	                        int count = 0;

	                        for(Entity ent : p.getNearbyEntities(50, 50, 50)) {
	                            if(!(ent instanceof Player)) {

	                                if(!(GlowAPI.isGlowing(ent, p))) {

	                                    GlowAPI.setGlowing(ent, GlowAPI.Color.WHITE, p);
	                                    count += 1;
	                                }

	                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
	                                    if(GlowAPI.isGlowing(ent, p)) {
	                                        GlowAPI.setGlowing(ent, false, p);
	                                    }

	                                }, (30*20L));
	                            }


	                        }
	                        p.sendMessage("§7Made §e" + count + "§7 entities glow for §e30§7 seconds" );
	                    }else if(args[1].equalsIgnoreCase("all")) {
	                        int count = 0;
	                        for(Entity ent : p.getNearbyEntities(50, 50, 50)) {
	                            if(!(GlowAPI.isGlowing(ent, p))) {
	                                GlowAPI.setGlowing(ent, GlowAPI.Color.WHITE, p);
	                                count += 1;
	                            }

	                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
	                                if(GlowAPI.isGlowing(ent, p)) {
	                                    GlowAPI.setGlowing(ent, false, p);
	                                }
	                            }, (30*20L));

	                        }
	                        p.sendMessage("§7Made §e" + count + "§7 entities/players glow for §e30§7 seconds" );
	                    }else{
	                        TextComponent msg = new TextComponent("§cPlease choose an option: §e");
	                        TextComponent msg_2 = new TextComponent("[Players]");
	                        TextComponent msg_3 = new TextComponent(" [Entities]");
	                        TextComponent msg_4 = new TextComponent(" [All]");
	                        msg_2.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/jphone glow players" ) );
	                        msg_3.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/jphone glow entities" ) );
	                        msg_4.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/jphone glow all" ) );

	                        msg.addExtra(msg_2);
	                        msg.addExtra(msg_3);
	                        msg.addExtra(msg_4);
	                        p.spigot().sendMessage(msg);
	                    }
	                }else{
	                    TextComponent msg = new TextComponent("§cPlease choose an option: §e");
	                    TextComponent msg_2 = new TextComponent("[Players]");
	                    TextComponent msg_3 = new TextComponent(" [Entities]");
	                    TextComponent msg_4 = new TextComponent(" [All]");
	                    msg_2.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/jphone glow players" ) );
	                    msg_3.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/jphone glow entities" ) );
	                    msg_4.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/jphone glow all" ) );

	                    msg.addExtra(msg_2);
	                    msg.addExtra(msg_3);
	                    msg.addExtra(msg_4);
	                    p.spigot().sendMessage(msg);
	                    //Key "owner" not set
	                }
	                return true;
				case "charge":
					nbti.setInteger("battery", 100);
					p.getInventory().setItemInMainHand(nbti.getItem());
					p.playSound(p.getLocation(),Sound.BLOCK_NOTE_PLING,1,1);
					p.sendMessage("§aDING! Charged your phone!");
					break;
				case "state":
					if (nbti.getBoolean("state")) {
						nbti.setBoolean("state", false);
						p.sendMessage("Phone now off");
					} else {
						nbti.setBoolean("state", true);
						p.sendMessage("phone now on");
					}
					p.getInventory().setItemInMainHand(nbti.getItem());
				case "exit":
					nbti.setBoolean("terminal", false);
					p.sendMessage("§7Exited §terminal mode");
					p.getInventory().setItemInMainHand(nbti.getItem());
				case "lookup":
					p.sendMessage("§7Looking up player from UUID...");
					try {
						UUID uuid = UUID.fromString(args[1]);
						p.sendMessage("§7Player: §e" + Bukkit.getOfflinePlayer(uuid).getName());
					}catch(IllegalArgumentException e) {
						p.sendMessage("§cPlayer was not found, or invalid UUID");
					}
					break;
				default:
					/*TextComponent msg = new TextComponent("§cThe specified commmand doesn't exist");
					msg.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "jphone glow players" ) );
					msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§9Command: §e" + args[0] + "," + args[1] + "," + args[2]).create()));
					p.spigot().sendMessage(msg);*/
					p.sendMessage("§cThe specified commmand doesn't exist");
			}
            return true;
        }
        return false;
    }

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked(); // The player that clicked the item
		ItemStack clicked = event.getCurrentItem(); // The item that was clicked
		Inventory inventory = event.getInventory(); // The inventory that was clicked in
		if(inventory.getName().equals(appswitcher.getName())) {
			//If name of inventory is same as app switcher
			event.setCancelled(true);
			if(!(p.getInventory().getItemInMainHand().getType().equals(Material.TRIPWIRE_HOOK))) {
				p.sendMessage("§You must have your phone being held");
				return;
			}
			ItemStack item =  p.getInventory().getItemInMainHand();
			NBTItem nbti = ItemNBTAPI.getNBTItem(item);
			switch(clicked.getType()) {
				case TORCH:
					ItemStack CurrentPhone = nbti.getItem();
					ItemMeta PhoneMeta = CurrentPhone.getItemMeta();
					PhoneMeta.setDisplayName("§fjLight");
					CurrentPhone.setItemMeta(PhoneMeta);
					CurrentPhone.setType(Material.TORCH);
					p.getInventory().setItemInMainHand(CurrentPhone);
					break;
				case BOOK_AND_QUILL:
					p.sendMessage("§7Type /jphone claim to claim");
					break;
				case SIGN:
					if(nbti.getBoolean("terminal")) {
						//ON to OFF
						nbti.setBoolean("terminal", false);
						p.sendMessage("§7Exited §eterminal mode");
					}else if(!(nbti.getBoolean("terminal"))) {
						//OFF to ON
						nbti.setBoolean("terminal", true);
						p.sendMessage("§7Entered §eterminal mode. §7Type §e'help'§7 for help");
					}
					p.getInventory().setItemInMainHand(nbti.getItem());
					break;
				default:
					p.sendMessage("That item is not configured correctly.");
			}
			p.closeInventory(); //close it
		}
	}

	@EventHandler
	public void jPhoneChat(AsyncPlayerChatEvent e) {
    	Player p = e.getPlayer();
    	ItemStack itm = p.getInventory().getItemInMainHand();
    	if(itm != null) {
    		NBTItem nbt = ItemNBTAPI.getNBTItem(itm);
    		if(nbt.getBoolean("terminal")) {
    			e.setCancelled(true);
			    p.sendMessage(" ");
    			p.sendMessage("§a>" + e.getMessage());
    			String[] args = e.getMessage().split(" ");
    			switch(args[0].toLowerCase()) {
				    case "version":
				    	p.sendMessage("§7The current version of terminal is §e" + plugin.getJackzCo().getString("versions.terminal"));
				    	break;
				    case "light":
				    case "jlight":
					    ItemStack CurrentPhone = nbt.getItem();
					    ItemMeta PhoneMeta = CurrentPhone.getItemMeta();
					    PhoneMeta.setDisplayName("§fjLight");
					    CurrentPhone.setItemMeta(PhoneMeta);
					    CurrentPhone.setType(Material.TORCH);
					    p.getInventory().setItemInMainHand(CurrentPhone);
				    	break;
				    case "commands":
				    	p.sendMessage("§3Current Commands:\n§ehelp §7general help\n§eversion §7check the version of terminal\n§elight §7turn on your flashlight\n§eexit §7exits terminal");
				    	break;
				    case "help":
				    	p.sendMessage("§7Hi, terminal is currently in alpha and missing features.");
				    	p.sendMessage("§7Current Version is: §e" + plugin.getJackzCo().getString("versions.terminal"));
				    	p.sendMessage("§7Type §ecommands §7to view commands");
				    	break;
				    case "exit":
				    	nbt.setBoolean("terminal",false);
					    p.sendMessage("§7Exited §eterminal mode");
					    p.getInventory().setItemInMainHand(nbt.getItem());
					    break;
				    default:
				    	p.sendMessage("§cUnknown command was specified. §7Type §ehelp for help");

			    }

		    }
	    }
	}
}