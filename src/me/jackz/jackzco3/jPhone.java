package me.jackz.jackzco3;

import de.tr7zw.itemnbtapi.ItemNBTAPI;
import de.tr7zw.itemnbtapi.NBTItem;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class jPhone implements Listener,CommandExecutor {
    private final Main plugin;

    public jPhone(Main plugin) {
        this.plugin = plugin; // Store the plugin in situations where you need it.
    }

    Random random = new Random();
    double randomnum() {
        return random.nextBoolean() ? random.nextDouble() : -random.nextDouble();
    }
    @SuppressWarnings("deprecation")
    @EventHandler
    public void PhoneClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        //below is the stupid way to stop double activation
        if(e.getAction() == Action.PHYSICAL) return;
        if(e.getHand().equals(EquipmentSlot.HAND)) {
            //spacing so i dont get confused. rightclick
            if(p.getInventory().getItemInMainHand().getType().equals(Material.TRIPWIRE_HOOK) && p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("�fjPhone")) {
                e.setCancelled(true);
                //cancel event, then set the item in hand to itself, fixing ghosting
                p.getInventory().setItemInMainHand(p.getInventory().getItemInMainHand());
                ItemStack item =  p.getInventory().getItemInMainHand();
                NBTItem nbti = ItemNBTAPI.getNBTItem(item);
                if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if(p.isSneaking()) {
                        //p.sendMessage("�cjPhone App Switcher not ready yet");

                        //plugin.appswitcher.setItem(1,  new ItemStack(Material.BOOK_AND_QUILL,1));
                        //plugin.appswitcher.setItem(3,  new ItemStack(Material.TORCH,1));
                        plugin.createDisplay(p,Material.BOOK_AND_QUILL,plugin.appswitcher,10,"&9Settings","&7Configure your phone");
                        plugin.createDisplay(p,Material.SIGN,plugin.appswitcher,12,"&9Terminal","&7Open the console/terminal");
                        plugin.createDisplay(p,Material.TORCH,plugin.appswitcher,14,"&9Flashlight","&7Illuminate the world!|&7(Left click to turn off)");
                        p.openInventory(plugin.appswitcher);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 0.2F, 5);

                        //gui ? Gui.
                    }else{
                        Integer battery = nbti.getInteger("battery");

                        p.sendMessage(" ");
                        if(battery == -1) {
                            p.sendMessage("�ajPhoneOS Version �e" + plugin.getConfig().getString("versions.jphone") + ChatColor.GOLD + " | " + ChatColor.GREEN + "Battery " + ChatColor.RED + "Dead");
                        }else{
                            p.sendMessage("�ajPhoneOS Version �e" + plugin.getConfig().getString("versions.jphone") + ChatColor.GOLD + " | " + ChatColor.GREEN + "Battery " + ChatColor.YELLOW + battery + "%");
                        }
                        p.sendMessage("�7Check your data by /jackzco jcloud info");


                        if(!(nbti.hasKey("owner"))) {

                            //hover: "Go to App Switcher->Settings->Owner to claim"
                            TextComponent msg = new TextComponent("�cThis device is not claimed. ");
                            TextComponent msg_hover = new TextComponent("�c[Hover to learn how to]");
                            //message.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "http://spigotmc.org" ) );
                            msg_hover.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("To claim this device go to \n�eapp switcher �rthen �esettings �rthen\n�eowner�r to claim.").create() ) );

                            msg.addExtra(msg_hover);
                            p.spigot().sendMessage(msg);
                            //Key "owner" not set
                        }else{
                            //is claimed
                            TextComponent msg = new TextComponent("�9Notice �7This device is claimed. Hover for details");
                            msg.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("�9Device claimed by\n�7" + nbti.getString("owner")).create() ) );

                            p.spigot().sendMessage(msg);
                        }
                        p.sendMessage(" ");
                    }
                }else if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if(p.isSneaking()) {
                        p.sendMessage("�cjPhone KeyChain is not ready yet");

                        p.openInventory(plugin.keychain);
                    }else{
                        p.sendMessage("�cCould not locate any nearby towers");
                    }
                }
            }else if(p.getInventory().getItemInMainHand().getType() == Material.NETHER_STAR) {
                final Horse entity = (Horse) p.getWorld().spawnEntity(p.getEyeLocation(),EntityType.HORSE);
                entity.setStyle(Horse.Style.NONE);
                entity.setVelocity(p.getEyeLocation().getDirection().multiply(2));

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    entity.getWorld().createExplosion(entity.getLocation(),0);
                    entity.remove();
                    for(int i = 0; i< 100; i++) {
                        FallingBlock fb = entity.getWorld().spawnFallingBlock(entity.getLocation(), Material.WOOL, (byte) random.nextInt(16));
                        fb.setHurtEntities(false);
                        fb.setDropItem(false);
                        fb.setVelocity(new Vector(randomnum(), randomnum(), randomnum()).multiply(2));
                    }

                }, 20);
            }else if(p.getInventory().getItemInMainHand().getType().equals(Material.TORCH) && p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("�fjLight") ) {
                if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                    ItemStack phone = p.getInventory().getItemInMainHand();
                    ItemMeta phoneMeta = phone.getItemMeta();
                    phone.setType(Material.TRIPWIRE_HOOK);
                    phoneMeta.setDisplayName("�fjPhone");
                    phone.setItemMeta(phoneMeta);
                    p.getInventory().setItemInMainHand(phone);
                }


            }
            //below is the stupid way to stop offhand placement. I don't know if two setcancels will fuck it up but i hope not
        }else if(e.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if(p.getInventory().getItemInMainHand().getType() == Material.TRIPWIRE_HOOK && p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("�fjPhone")) {
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
                sender.sendMessage("�cPlease put an argument");
            }else{
                if(args[0].equalsIgnoreCase("help")) {
                    //p.sendMessage("�7Please wait...");
                    ItemStack helpbook = new ItemStack(Material.WRITTEN_BOOK);
                    BookMeta helpMeta = (BookMeta) helpbook.getItemMeta();
                    helpMeta.setTitle("jPhone Terminal Help");
                    helpMeta.setAuthor("JackzCo");
                    List<String> pages = new ArrayList<>();
                    pages.add("well this is supposed to help you but im lazy.... oops?"); // Page 1
                    pages.add("Hope you enjoy your stay/play!");

                    helpMeta.setPages(pages);
                    helpbook.setItemMeta(helpMeta);
                    p.getInventory().addItem(helpbook);
                }else if(args[0].equalsIgnoreCase("claim")) {
                    if(nbti.hasKey("owner")) {
                        p.sendMessage("�cThis device is claimed by: �e" + nbti.getString("owner") );
                    }else{
                        nbti.setString("owner", p.getUniqueId().toString());
                        p.sendMessage("�7Claimed device as �e" + p.getUniqueId().toString());
                        p.getInventory().setItemInMainHand(nbti.getItem());
                    }
                }else if(args[0].equalsIgnoreCase("trash")) {
                    Inventory trash = Bukkit.createInventory(null, 9*3, "Trash Can");
                    p.openInventory(trash);
                }else if(args[0].equalsIgnoreCase("dangers")) {
                    p.sendMessage("[disabled for now]");
                    //highlight monsters w/ red
                    /*int count = 0;
                    Boolean pvpenabled = p.getWorld().getPVP();
                    for(Entity ent : p.getNearbyEntities(50, 50, 50)) {

                        if(ent instanceof Monster) {
                            count += 1;
                            GlowAPI.setGlowing(ent, GlowAPI.Color.DARK_RED, p);
                        }else if(ent instanceof Player && pvpenabled) {
                            //if ent is player and PVP is enabled for that world
                            GlowAPI.setGlowing(ent, GlowAPI.Color.RED, p);
                        }
                        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                            @Override
                            public void run() {
                                if(GlowAPI.isGlowing(ent, p)) {
                                    GlowAPI.setGlowing(ent, false, p);
                                }

                            }
                        }, (30*20L));
                    }
                    p.sendMessage("�cFound " + count + " dangers");*/
                }else if(args[0].equalsIgnoreCase("glow")) {
                    p.sendMessage("removed");
                   /* if(args.length > 1) {
                        if(args[1].equalsIgnoreCase("players")) {

                            int count = 0;
                            for(Player player : Bukkit.getOnlinePlayers()){
                                if(!(GlowAPI.isGlowing(player, p))) {
                                    GlowAPI.setGlowing(player, GlowAPI.Color.WHITE, p);
                                    count += 1;
                                }
                                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        if(GlowAPI.isGlowing(player, p)) {
                                            GlowAPI.setGlowing(player, false, p);
                                        }
                                    }
                                }, (30*20L));

                            }
                            p.sendMessage("�7Made �e" + count + "�7 players glow for �e30�7 seconds" );
                        }else if(args[1].equalsIgnoreCase("entities")) {
                            int count = 0;

                            for(Entity ent : p.getNearbyEntities(50, 50, 50)) {
                                if(!(ent instanceof Player)) {

                                    if(!(GlowAPI.isGlowing(ent, p))) {

                                        GlowAPI.setGlowing(ent, GlowAPI.Color.WHITE, p);
                                        count += 1;
                                    }

                                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            if(GlowAPI.isGlowing(ent, p)) {
                                                GlowAPI.setGlowing(ent, false, p);
                                            }

                                        }
                                    }, (30*20L));
                                }


                            }
                            p.sendMessage("�7Made �e" + count + "�7 entities glow for �e30�7 seconds" );
                        }else if(args[1].equalsIgnoreCase("all")) {
                            int count = 0;
                            for(Entity ent : p.getNearbyEntities(50, 50, 50)) {
                                if(!(GlowAPI.isGlowing(ent, p))) {
                                    GlowAPI.setGlowing(ent, GlowAPI.Color.WHITE, p);
                                    count += 1;
                                }

                                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        if(GlowAPI.isGlowing(ent, p)) {
                                            GlowAPI.setGlowing(ent, false, p);
                                        }
                                    }
                                }, (30*20L));

                            }
                            p.sendMessage("�7Made �e" + count + "�7 entities/players glow for �e30�7 seconds" );
                        }else{
                            TextComponent msg = new TextComponent("�cPlease choose an option: �e");
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
                        TextComponent msg = new TextComponent("�cPlease choose an option: �e");
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
                    return true;*/
                }else if(args[0].equalsIgnoreCase("charge")) {
                    nbti.setInteger("battery",100);
                    p.getInventory().setItemInMainHand(nbti.getItem());
                    p.sendMessage("�aDING! Charged your phone!");
                }else if(args[0].equalsIgnoreCase("state")) {
                    if(nbti.getBoolean("state")) {
                        nbti.setBoolean("state", false);
                        p.sendMessage("Phone now off");
                    }else{
                        nbti.setBoolean("state", true);
                        p.sendMessage("phone now on");
                    }
                    p.getInventory().setItemInMainHand(nbti.getItem());
                }else if(args[0].equalsIgnoreCase("exit")) {
                    nbti.setBoolean("terminal", false);
                    p.sendMessage("�7Exited �terminal mode");
                    p.getInventory().setItemInMainHand(nbti.getItem());
                }else if(args[0].equalsIgnoreCase("lookup")) {
                    //UUID to player
                    p.sendMessage("�7Looking up player from UUID... �c(Feature not available)");
                    //p.sendMessage("�7Player: �e" + Bukkit.getOfflinePlayer(args[1]));
                }else{
					/*TextComponent msg = new TextComponent("�cThe specified commmand doesn't exist");
					msg.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "jphone glow players" ) );
					msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("�9Command: �e" + args[0] + "," + args[1] + "," + args[2]).create()));
					p.spigot().sendMessage(msg);*/
                    p.sendMessage("�cThe specified commmand doesn't exist");
                }
            }
            return true;
        }else{
            return false;
        }
    }
}