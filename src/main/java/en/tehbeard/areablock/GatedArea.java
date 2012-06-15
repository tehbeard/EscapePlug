package en.tehbeard.areablock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.tehbeard.utils.cuboid.*;

/**
 * Represents a gated area
 * A gated area consists of:
 * - A number of detection areas.
 * - A number of gates
 * - A player threshold
 * @author James
 *
 */
public class GatedArea {

    private List<Cuboid> detectAreas = new ArrayList<Cuboid>();
    
    private List<Gate> gates = new ArrayList<Gate>();
    
    int threshold = 0;
    
    
    public final List<Gate> getGates() {
        return gates;
    }

    boolean open = true;
    
    public GatedArea(int threshold){
        this.threshold = threshold;
    }
    
    public GatedArea(ConfigurationSection section){
        threshold = section.getInt("threshold");
        for(String s :section.getStringList("areas")){
            Cuboid c = new Cuboid();
            c.setCuboid(s);
            detectAreas.add(c);
        }
        
        for(String s :section.getStringList("gates")){
            
            gates.add(new Gate(s));
        }
    }
    
    /**
     * Returns true if areas have zero players in them
     * @return
     */
    public boolean isEmpty(){
        for(Player p : Bukkit.getOnlinePlayers()){
            for(Cuboid c : detectAreas){
                if(c.isInside(p.getLocation())){return false;}
            }
        }
        
        return true;
    }
    
    /**
     * Returns true if area has nessecary number of players
     * @return
     */
    public boolean isActive(){
        Set<String> ply = new HashSet<String>();
        for(Player p : Bukkit.getOnlinePlayers()){
            for(Cuboid c : detectAreas){
                if(c.isInside(p.getLocation())){ply.add(p.getName());}
            }
        }
        return ply.size() >= threshold;
    }
    
    
    public void updateArea(){
        
        if(open){

            if(isActive()){
                //if currently open and number of players needed are inside, close and set open to false.
                for(Gate g : gates){
                    g.close();
                }
                open = false;
                System.out.println("Closing area");
            }
        }
        else
        {
            if(isEmpty()){
                for(Gate g : gates){
                    g.open();
                }
                open = true;
                System.out.println("Opening area");
            }
        }
    }

    public final List<Cuboid> getDetectAreas() {
        return detectAreas;
    }

    public final int getThreshold() {
        return threshold;
    }

    public final boolean isOpen() {
        return open;
    }
    
    public ConfigurationSection toConfig(){
        ConfigurationSection config = new YamlConfiguration();
        config.set("threshold",threshold);
        
        List<String> areas = new ArrayList<String>();
        for(Cuboid c :detectAreas){
            areas.add(c.toString());
        }
        config.set("areas",areas);
        
        List<String> gates = new ArrayList<String>();
        for(Gate g :this.gates){
            gates.add(g.toString());
        }
        config.set("gates",gates);
        
        
        return config;
    }
}