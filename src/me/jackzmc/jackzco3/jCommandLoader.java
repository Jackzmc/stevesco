package me.jackzmc.jackzco3;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class jCommandLoader implements CommandExecutor {
    private final Main plugin;
    public jCommandLoader(Main plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        switch(args[0].toLowerCase()) {
            case "help":
                break;

        }
        return true;
        //TODO: add commands
    }
}
