package com.tehbeard.pasta.schematic.worker;

import com.tehbeard.pasta.schematic.SchVector;
import com.tehbeard.pasta.schematic.SchematicWorker;
import com.tehbeard.pasta.schematic.extensions.WorldEditVectorExtension;

/**
 * Offsets a schematic using WorldEdit's offset vector
 * @author James
 *
 */
public class OriginWorker extends ISchematicWorker {

    @Override
    public SchVector modifyOffsetVector(SchVector vector,
            SchematicWorker worker) {
        WorldEditVectorExtension ve = worker.getSchematic().getExtension(WorldEditVectorExtension.class);
        if(ve==null){return vector;}
        
        vector.add(ve.getOffset());
        return vector;
    }
    
}
