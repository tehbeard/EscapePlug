package com.tehbeard.pasta.schematic.worker;

import com.tehbeard.pasta.schematic.SchVector;
import com.tehbeard.pasta.schematic.SchematicDataRegistry;
import com.tehbeard.pasta.schematic.SchematicWorker;
import com.tehbeard.pasta.schematic.data.SchematicDataHandler;
import com.tehbeard.pasta.schematic.data.SchematicRotationHandler;

/**
 * Rotates a schematic
 * @author James
 *
 */
public class RotateWorker extends ISchematicWorker{

    private int rotations;
    
    public RotateWorker(int rotations){
        this.rotations = rotations;
    }
    
    @Override
    public SchVector modifyOffsetVector(SchVector vector, SchematicWorker worker) {
        vector.rotateVector(rotations);
        return vector;
    }
    
    @Override
    public int[] modifyBlock(int[] block,int sx,int sy,int sz,SchematicWorker worker) {
        int b_id = block[0];
        int b_meta = block[1];
        SchematicDataHandler handler = SchematicDataRegistry.dataHandlers[b_id];

        if(handler instanceof SchematicRotationHandler){
            int old_meta = b_meta;

            b_meta = ((SchematicRotationHandler)handler).rotateData(worker.getSchematic(), sx, sy, sz, b_id, b_meta, rotations);

            SchematicDataRegistry.logger().info("Transformer found for " + b_id + " : " + old_meta + " -> " + b_meta);
        }
        return new int[]{b_id,b_meta};
    }
}
