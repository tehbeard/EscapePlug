package com.tehbeard.pasta.schematic.worker;


import net.minecraft.server.v1_5_R3.World;

import com.tehbeard.pasta.schematic.SchematicWorker;

public class NoAirPastedWorker extends ISchematicWorker {

    @Override
    public boolean canPaste(World world, int x, int y, int z, int b_id,
            int b_meta, SchematicWorker worker) {
        return b_id != 0;
    }
}
