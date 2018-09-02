package me.jackzmc.jackzco3;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class JCommand {
    public String name;
    public String description;
    //public List<Args> = new ArrayList<Args>;
    public boolean run(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage("test");
        return true;
    }

    public JCommand(String cmdName, String cmdDesc) {
        this.name = cmdName;
        this.description = cmdDesc;
    }
}
