package en.tehbeard.jumpGate;

import org.bukkit.util.Vector;

public class JumpGateSession {

    private Vector v1;
    private Vector v2;
    private String world;
    private boolean debugMode = false;
    
    public boolean isDebugMode() {
        return debugMode;
    }
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
    public String getWorld() {
        return world;
    }
    public void setWorld(String world) {
        this.world = world;
    }
    public Vector getV1() {
        return v1;
    }
    public void setV1(Vector v1) {
        this.v1 = v1;
    }
    public Vector getV2() {
        return v2;
    }
    public void setV2(Vector v2) {
        this.v2 = v2;
    }
    
    
}
