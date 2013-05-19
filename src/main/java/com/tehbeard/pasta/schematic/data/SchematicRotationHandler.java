package com.tehbeard.pasta.schematic.data;

import com.tehbeard.pasta.schematic.SchematicFile;

/**
 * This handler implements rotating a blocks metadata
 * @author James
 *
 */
public interface SchematicRotationHandler extends SchematicDataHandler {

    /**
     * Returns the metadata to for a rotated schematic, you are provided the schematic and xyz to use for multi block/tile entity related rotations
     *  
     * @param schematic schematic object being used
     * @param x coord in schematic of block
     * @param y coord in schematic of block
     * @param z coord in schematic of block
     * @param blockId use if registering one class for multiple block ids
     * @param metadata metadata before rotation
     * @param rotations number of rotations 90 degrees clockwise made
     * @return new metadata for rotation
     */
    public int rotateData(SchematicFile schematic,int x,int y, int z,int blockId, int metadata,int rotations);
}
