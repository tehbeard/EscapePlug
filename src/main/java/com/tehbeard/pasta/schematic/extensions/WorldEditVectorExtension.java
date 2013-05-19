package com.tehbeard.pasta.schematic.extensions;

import net.minecraft.server.v1_5_R3.NBTTagCompound;

import com.tehbeard.pasta.schematic.SchVector;
import com.tehbeard.pasta.schematic.SchematicFile;

/**
 * Adds access to WorldEdit's offset and origin vectors.
 * These vectors can be used by a SchematicWorker to place a schematic in a more intuative place.
 * (e.g. Paste a house so that the front door is infront of the user).
 * @author James
 *
 */
@SchExtension(checkPath = "WEOriginX",name="WorldEdit Vector")
public class WorldEditVectorExtension implements SchematicExtension{

    //Original location in world
    private SchVector origin;

    //Offset vector
    private SchVector offset;
    
    @Override
    public void onLoad(NBTTagCompound tag,SchematicFile file) {
        origin = new SchVector(
                tag.getInt("WEOriginX"),
                tag.getInt("WEOriginY"),
                tag.getInt("WEOriginZ"));

        offset = new SchVector(
                tag.getInt("WEOffsetX"),
                tag.getInt("WEOffsetY"),
                tag.getInt("WEOffsetZ")
                );
    }

    @Override
    public void onSave(NBTTagCompound tag,SchematicFile file) {
        
    }
    
    /**
     * Gets the schematics origin (WorldEdit generated schematics)
     * @return
     */
    public SchVector getOrigin() {
        return origin;
    }

    /**
     * Gets the schematics offset (WorldEdit generated schematics)
     * @return
     */
    public SchVector getOffset() {
        return offset;
    }

}
