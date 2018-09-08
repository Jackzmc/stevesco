package me.jackz.jackzco3.lib;

import me.jackz.jackzco3.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Config {
    public FileConfiguration setupConfig(Main plugin) { //set the config in memory, and set defaults
        File configFile = new File (plugin.getDataFolder(), "jackzco.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.addDefault("motd", "Hello %player%, Welcome to the server!");
        config.addDefault("jackzco.prefix", "§3JackzCo§6>");
        config.addDefault("regions",new ArrayList<String>());
        config.addDefault("scanner.disallowed",new ArrayList<String>());

        config.set("version",plugin.getDescription().getVersion());
        config.addDefault("versions.jackzco", "0.1");
        config.addDefault("versions.jphone","0.1");

        config.options().copyDefaults(true);
        try {
            config.save(configFile);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return config;
    }
}
