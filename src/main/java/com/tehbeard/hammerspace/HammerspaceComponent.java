package com.tehbeard.hammerspace;

import java.util.List;

import org.bukkit.World;
import org.tulonsae.mc.util.Log;

import net.escapecraft.component.AbstractComponent;
import net.escapecraft.component.ComponentDescriptor;
import net.escapecraft.escapePlug.EscapePlug;
/**
 * Provides personal worlds to players
 * @author James
 *
 */
@ComponentDescriptor(name="Personal Hammerspace",slug="hammerspace",version="0.1")
public class HammerspaceComponent extends AbstractComponent {
    
    private int worldLimit = 5;
    
    List<World> loaded;

    @Override
    public boolean enable(Log log, EscapePlug plugin) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void disable() {
        // TODO Auto-generated method stub
        
    }
    
    
    /**
     * Create a personal Hammerspace world for a player
     * @param player player to create for, silently fails if world exists
     */
    public void createSpace(String player){
        //check not exists
        //copy default world folder
    }
    
    /**
     * Player has hammerspace already
     * @param player
     * @return
     */
    public boolean hasSpace(String player){
        return false;
    }
    
    
    public boolean loadSpace(String player){
        if(loaded.size() == worldLimit){return false;}
        
        //load world
        //loaded.add(world);
        
        return false;
    }
    
    public boolean unloadSpace(String player){
        return false;
        
    }

}
