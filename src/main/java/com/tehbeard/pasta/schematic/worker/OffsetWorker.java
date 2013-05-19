package com.tehbeard.pasta.schematic.worker;

import com.tehbeard.pasta.schematic.SchVector;
import com.tehbeard.pasta.schematic.SchematicWorker;
import com.tehbeard.pasta.schematic.extensions.WorldEditVectorExtension;

/**
 * Uses WorldEdit's origin
 * @author James
 *
 */
public class OffsetWorker extends ISchematicWorker {

    @Override
    public SchVector modifyOffsetVector(SchVector vector,
            SchematicWorker worker) {
        WorldEditVectorExtension ve = worker.getSchematic().getExtension(WorldEditVectorExtension.class);
        if(ve==null){return vector;}
        
        vector.add(ve.getOrigin());
        return vector;
    }
    
}
