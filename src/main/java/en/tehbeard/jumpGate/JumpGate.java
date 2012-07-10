package en.tehbeard.jumpGate;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import en.tehbeard.areablock.Cuboid;

/**
 * Represents a jump gate
 * A jump gate consists of an entrance cuboid and an exit vector.
 * Vectors can be same/diff world
 * @author James
 *
 */
public class JumpGate {

    private Location destination;
    private Cuboid cuboid;
   
    private String name;
    
    public JumpGate(){

    }
    
    public JumpGate(ConfigurationSection section){
        name = section.getName();
        cuboid = new Cuboid();
        cuboid.setCuboid(section.getString("portal"));
        destination = LocationUtils.fromText(section.getString("exit"));
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public void setCuboid(Cuboid cuboid) {
        this.cuboid = cuboid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void save(ConfigurationSection section){
        section.set(name +".portal", cuboid.toString());
        section.set(name +".exit", LocationUtils.toText(destination));
    }
    
}
