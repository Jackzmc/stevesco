/*
 * Copyright (C) 2019 Jackson Bixby
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
        config.addDefault("updatecheck.enabled",true);
        config.addDefault("updatecheck.interval",30);

        config.set("versions.jackzco", "DEVELOPMENT BUILD");
        config.set("versions.jphone","2.3.0-beta");
        config.set("versions.terminal","1.5.0-beta");
        config.set("versions.main",plugin.getDescription().getVersion());

        config.options().copyDefaults(true);
        try {
            config.save(configFile);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return config;
    }
}
