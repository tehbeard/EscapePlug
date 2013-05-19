package com.tehbeard.pasta.schematic.extensions;

import net.minecraft.server.v1_5_R3.NBTTagCompound;

import com.tehbeard.pasta.schematic.SchematicFile;


/**
 * Represents an extension to the Schematic file format
 * extensions add additional data ontop of the standard schematic format.
 * 
 * @author James
 *
 */
public interface SchematicExtension {

    /**
     * Called after loading the base data
     * @param tag
     */
    public void onLoad(NBTTagCompound tag,SchematicFile file);
    
    
    /**
     * NOT IMPLEMENTED YET
     * @param tag
     */
    public void onSave(NBTTagCompound tag,SchematicFile file);
    
    
}
