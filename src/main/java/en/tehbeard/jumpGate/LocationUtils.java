package en.tehbeard.jumpGate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;


public class LocationUtils {


    public static String toText(Location location){
        return location.getWorld().getName() + ":" +
                location.getX() + ":" +
                location.getY() + ":" +
                location.getZ() + ":" +
                location.getPitch() + ":" +
                location.getYaw();
    }

    public static Location fromText(String location){
        String[] p = location.split("\\:");



        World world = Bukkit.getWorld(p[0]);
        double x    = Double.parseDouble(p[1]);
        double y    = Double.parseDouble(p[2]);
        double z    = Double.parseDouble(p[3]);
        float yaw   = Float.parseFloat(p[4]);
        float pitch = Float.parseFloat(p[4]);
        
        return new Location(world, x, y, z, yaw, pitch);

    }
}
