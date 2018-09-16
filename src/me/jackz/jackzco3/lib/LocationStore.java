package me.jackz.jackzco3.lib;

import me.jackz.jackzco3.Main;
import org.bukkit.Location;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;

class BooleanValues {
    Location loc;
    boolean bool;
}
public class LocationStore {
    private final Main plugin;
    /*
        store a new one:
        new LocationStore(plugin)
        .getString(Location)
        .getBoolean(Location)
        .setBoolean(Location,boolean)
        .setString(Location,String)
     */
    public LocationStore(Main plugin) {
        this.plugin = plugin;
    }

    public boolean getBoolean(Location loc) {
        try(FileReader file = new FileReader(plugin.getDataFolder().toString() + "/location.store")){
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(file);
            Object output = obj.get(String.format("%s,%s,%s",loc.getX(),loc.getY(),loc.getZ()));
            if(output == null) {
                return false;
            }
            return Boolean.parseBoolean(output.toString());
        }catch(Exception ex) {
            plugin.getLogger().log(Level.WARNING,"getBoolean",ex);
            return false;
        }
    }
    public String getString(Location loc) {
        JSONParser parser = new JSONParser();
        try(FileReader file = new FileReader(plugin.getDataFolder().toString() + "/location.store")){
            JSONObject obj = (JSONObject) parser.parse(file);
            Object output = obj.get(String.format("%s,%s,%s",loc.getX(),loc.getY(),loc.getZ()));
            if(output == null) {
                return null;
            }
            return output.toString();
        }catch(Exception ex) {
            plugin.getLogger().log(Level.WARNING,"getString",ex);
            return null;
        }
    }
    public void deleteValue(Location loc) {
        try{
            File file = new File(plugin.getDataFolder().toString() + "/location.store");
            JSONObject obj;
            if(!file.exists()) {
                //throw new FileNotFoundException();
                obj = new JSONObject();
            }else{
                JSONParser parser = new JSONParser();
                obj = (JSONObject) parser.parse(new FileReader(file));
            }
            obj.remove(String.format("%s,%s,%s",loc.getX(),loc.getY(),loc.getZ()));
            FileWriter writer = new FileWriter(file);
            writer.write(obj.toJSONString());
            writer.flush();
            writer.close();
        }catch(Exception e) {
            plugin.getLogger().warning(e.toString());
        }
    }
    @SuppressWarnings("unchecked")
    public void setString(Location loc,String value) {
        try{
            File file = new File(plugin.getDataFolder().toString() + "/location.store");
            JSONObject obj;
            if(!file.exists()) {
                //throw new FileNotFoundException();
                obj = new JSONObject();
            }else{
                JSONParser parser = new JSONParser();
                obj = (JSONObject) parser.parse(new FileReader(file));
            }
            obj.put(String.format("%s,%s,%s",loc.getX(),loc.getY(),loc.getZ()), value);
            FileWriter writer = new FileWriter(file);
            writer.write(obj.toJSONString());
            writer.flush();
            writer.close();
        }catch(Exception e) {
            plugin.getLogger().warning(e.toString());
        }
    }
    @SuppressWarnings("unchecked")
    public void setBoolean(Location loc,boolean value) {
        try{
            File file = new File(plugin.getDataFolder().toString() + "/location.store");
            JSONObject obj;
            if(!file.exists()) {
                //throw new FileNotFoundException();
                obj = new JSONObject();

                //missing location.store
            }else{
                plugin.getLogger().info("parsing");
                JSONParser parser = new JSONParser();
                obj = (JSONObject) parser.parse(new FileReader(file));
            }
            obj.put(String.format("%s,%s,%s",loc.getX(),loc.getY(),loc.getZ()), value);
            FileWriter writer = new FileWriter(file);
            writer.write(obj.toJSONString());
            writer.flush();
            writer.close();
        }catch(Exception e) {
            plugin.getLogger().warning(e.toString() + " at " + e.getStackTrace()[0]);
        }
    }

}
