package me.jackz.jackzco3.lib;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LocVarLib {
    //private final Main plugin;
    private Properties properties = new Properties();
    private Map<String,Location> db = new HashMap<>();

    /*public LocVarLib(Main plugin) {
        this.plugin = plugin;
        //load db from file & initalize var

        File dbfile = new File(plugin.getDataFolder().toPath() + "/locvar.db");
        properties.load(new FileInputStream(dbfile));

        for (String key : properties.stringPropertyNames()) {
            db.put(key, properties.getProperty(key.toString()));
        }
    }
    public JSONObject getJSONSettings(Location loc) throws ParseException, FileNotFoundException, IOException {
         JSONParser parser = new JSONParser();
         return parser.parse(new FileReader("employees.json"));
    }
    public void setJSONSettings(Location loc,JSONObject obj) {

    }

    public void saveSettings() {
        for (Map.Entry<String,Location> entry : db.entrySet()) {
            properties.put(entry.getKey(), entry.getValue());
        }

        properties.store(new FileOutputStream("data.properties"), null);
    }
    public Map getSettings() {
        return db;
    }*/
}
