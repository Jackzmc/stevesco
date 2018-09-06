package me.jackz.jackzco3.lib;

import com.sk89q.worldguard.util.jsonsimple.JSONArray;
import com.sk89q.worldguard.util.jsonsimple.JSONObject;
import jdk.nashorn.internal.parser.JSONParser;
import me.jackz.jackzco3.Main;
import org.bukkit.Location;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class LocVarLib {
    private final Main plugin;
    public LocVarLib(Main plugin) {
        this.plugin = plugin;
    }
    public JSONObject getSettings(Location loc) throws ParseException, FileNotFoundException, IOException {
         JSONParser parser = new JSONParser();
        return parser.parse(new FileReader("employees.json"));
    }
    public void setSettings(Location loc,JSONObject obj) {

    }
}
