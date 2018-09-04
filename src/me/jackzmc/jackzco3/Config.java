package me.jackzmc.jackzco3;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Config {

    /*public void Config(Main plugin) {
        this.plugin = plugin; // Store the plugin in situations where you need it.
    }*/
    public void config() {

    }
    public void setupConfig() {
        File configFile = new File (Main.getPlugin().getDataFolder(), "jackzco" + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        PluginDescriptionFile pdf = Main.getPlugin().getDescription();
        config.addDefault("motd", "Hello %player%, Welcome to the server!");
        config.addDefault("jackzco.prefix", "§3JackzCo§6>");
        config.addDefault("regions",new ArrayList<String>());

        config.set("version", pdf.getVersion());
        config.addDefault("versions.jackzco", "0.1");
        config.addDefault("versions.jphone","0.1");

        config.options().copyDefaults(true);
        try {
            config.save(configFile);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    public FileConfiguration getJackzCo() {
        File playerFile = new File (Main.getPlugin().getDataFolder(), "jackzco" + ".yml");
        return YamlConfiguration.loadConfiguration(playerFile);
    }
}
