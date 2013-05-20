package com.tehbeard.pasta.schematic;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Material;

import net.minecraft.server.v1_5_R3.NBTTagCompound;

import com.tehbeard.pasta.schematic.data.RotationHandler;
import com.tehbeard.pasta.schematic.data.SchematicDataHandler;
import com.tehbeard.pasta.schematic.extensions.ClassCatalogue;
import com.tehbeard.pasta.schematic.extensions.SchExtension;
import com.tehbeard.pasta.schematic.extensions.SchematicExtension;
import com.tehbeard.pasta.schematic.extensions.WorldEditVectorExtension;

/**
 * Main entry point for LibSchematic.
 * This class acts as the FML mod class, 
 * it handles holding data handlers for mod blocks 
 * and schematic extensions to access extra information 
 * embedded in a schematic
 * @author James
 *
 */
public class SchematicDataRegistry {

    //BEGIN FORGE MOD SECTION
    public static final boolean DEBUG_MODE = true;

    private static Logger logger = Logger.getLogger("Minecraft");

    public static Logger logger(){
        return logger;
    }

    static{
        logger.setFilter(null);
        logger.setLevel(Level.ALL);
    }
    //END FORGE MOD SECTION


    //data handlers for blocks
    public static final SchematicDataHandler[] dataHandlers = new SchematicDataHandler[4096];

    //Initialise vanilla block data
    static{
        dataHandlers[Material.CHEST.getId()] = RotationHandler.CONTAINER_PISTON;
        dataHandlers[Material.TRAPPED_CHEST.getId()] = RotationHandler.CONTAINER_PISTON;
        dataHandlers[Material.ENDER_CHEST.getId()] = RotationHandler.CONTAINER_PISTON;
        dataHandlers[Material.FURNACE.getId()] = RotationHandler.CONTAINER_PISTON;
        dataHandlers[Material.BURNING_FURNACE.getId()] = RotationHandler.CONTAINER_PISTON;
        dataHandlers[Material.PISTON_STICKY_BASE.getId()] = RotationHandler.CONTAINER_PISTON;
        dataHandlers[Material.PISTON_BASE.getId()] = RotationHandler.CONTAINER_PISTON;
        dataHandlers[Material.PISTON_EXTENSION.getId()] = RotationHandler.CONTAINER_PISTON;
        dataHandlers[Material.PISTON_MOVING_PIECE.getId()] = RotationHandler.CONTAINER_PISTON;
        dataHandlers[Material.LADDER.getId()] = RotationHandler.CONTAINER_PISTON;
        dataHandlers[Material.WALL_SIGN.getId()] = RotationHandler.CONTAINER_PISTON;
        dataHandlers[Material.DISPENSER.getId()] = RotationHandler.CONTAINER_PISTON;
        dataHandlers[Material.HOPPER.getId()] = RotationHandler.CONTAINER_PISTON;
        dataHandlers[Material.DROPPER.getId()] = RotationHandler.CONTAINER_PISTON;

        dataHandlers[Material.REDSTONE_TORCH_OFF.getId()] = RotationHandler.WALL_MOUNTED;
        dataHandlers[Material.REDSTONE_TORCH_ON.getId()] = RotationHandler.WALL_MOUNTED;
        dataHandlers[Material.STONE_BUTTON.getId()] = RotationHandler.WALL_MOUNTED;
        dataHandlers[Material.WOOD_BUTTON.getId()] = RotationHandler.WALL_MOUNTED;
        dataHandlers[Material.LEVER.getId()] = RotationHandler.WALL_MOUNTED;
        dataHandlers[Material.TORCH.getId()] = RotationHandler.WALL_MOUNTED;


        dataHandlers[Material.REDSTONE_COMPARATOR_OFF.getId()] = RotationHandler.REPEATER;
        dataHandlers[Material.REDSTONE_COMPARATOR_ON.getId()] = RotationHandler.REPEATER;
        dataHandlers[Material.DIODE_BLOCK_OFF.getId()] = RotationHandler.REPEATER;
        dataHandlers[Material.DIODE_BLOCK_ON.getId()] = RotationHandler.REPEATER;

        dataHandlers[Material.RAILS.getId()] = RotationHandler.RAIL;
        dataHandlers[Material.POWERED_RAIL.getId()] = RotationHandler.RAIL;
        dataHandlers[Material.DETECTOR_RAIL.getId()] = RotationHandler.RAIL;
        dataHandlers[Material.ACTIVATOR_RAIL.getId()] = RotationHandler.RAIL;
        
        dataHandlers[Material.BRICK_STAIRS.getId()] = RotationHandler.STAIRS;
        dataHandlers[Material.COBBLESTONE_STAIRS.getId()] = RotationHandler.STAIRS;
        dataHandlers[Material.WOOD_STAIRS.getId()] = RotationHandler.STAIRS;
        dataHandlers[Material.SPRUCE_WOOD_STAIRS.getId()] = RotationHandler.STAIRS;
        dataHandlers[Material.BIRCH_WOOD_STAIRS.getId()] = RotationHandler.STAIRS;
        dataHandlers[Material.JUNGLE_WOOD_STAIRS.getId()] = RotationHandler.STAIRS;
        dataHandlers[Material.SMOOTH_STAIRS.getId()] = RotationHandler.STAIRS;
        dataHandlers[Material.SANDSTONE_STAIRS.getId()] = RotationHandler.STAIRS;
        dataHandlers[Material.QUARTZ_STAIRS.getId()] = RotationHandler.STAIRS;
        dataHandlers[Material.NETHER_BRICK_STAIRS.getId()] = RotationHandler.STAIRS;
        

        dataHandlers[Material.VINE.getId()] = RotationHandler.VINES;

        dataHandlers[Material.TRAP_DOOR.getId()] = RotationHandler.TRAPDOOR;

        dataHandlers[Material.TRIPWIRE_HOOK.getId()] = RotationHandler.HOOK;

        dataHandlers[Material.FENCE_GATE.getId()] = RotationHandler.FENCEGATE;

        dataHandlers[Material.ANVIL.getId()] = RotationHandler.ANVIL;

        dataHandlers[Material.BED_BLOCK.getId()] = RotationHandler.BED;

        dataHandlers[Material.SIGN_POST.getId()] = RotationHandler.SIGN_POST;

        dataHandlers[Material.WOODEN_DOOR.getId()] = RotationHandler.DOOR;
        dataHandlers[Material.IRON_DOOR_BLOCK.getId()] = RotationHandler.DOOR;
        
        dataHandlers[Material.QUARTZ_BLOCK.getId()] = RotationHandler.QUARTZ;
        
        dataHandlers[Material.LOG.getId()] = RotationHandler.WOOD;
    }

    public static final ClassCatalogue<SchematicExtension> schematicExtensions = new ClassCatalogue<SchematicExtension>();

    static{
        schematicExtensions.addProduct(WorldEditVectorExtension.class);
    }

    /**
     * Add a data handler for a block, to be used by mods.
     * Data handlers provide a way for mods to suggest how to handle a block in certain situations
     * such as rotation of a block, changing the owner.
     * @param blockId
     * @param handler
     */
    public static void setHandler(int blockId, SchematicDataHandler handler){
        if(blockId < 0 || blockId >= 4096){
            throw new IllegalArgumentException("INVALID BLOCKID (0-4095) " + blockId + " SUPPLIED");
        }
        dataHandlers[blockId] = handler;
        //logger.info("Added schematic data handler for " + Block.blocksList[blockId].getUnlocalizedName() + "[" + blockId + "]");
    }

    /**
     * return data handler for block, it may implement one or several interfaces for 
     * @param blockId
     * @return
     */
    public static SchematicDataHandler getHandler(int blockId){
        if(blockId < 0 || blockId >= 4096){
            throw new IllegalArgumentException("INVALID BLOCKID (0-4095) " + blockId + " SUPPLIED");
        }
        return dataHandlers[blockId];

    }

    /**
     * Returns a list of extension objects for schematic
     * @param tag
     * @param file
     * @return
     */
    public static List<SchematicExtension> getExtensions(NBTTagCompound tag,SchematicFile file){
        List<SchematicExtension> l = new ArrayList<SchematicExtension>();

        for(String exTagFull : schematicExtensions.getTags()){
            logger.fine("Checking for " + exTagFull);
            if(nbtContainsPath(tag,exTagFull)){
                try {
                    Class<? extends SchematicExtension> c = schematicExtensions.get(exTagFull);
                    logger.fine("Loading extension handler [" + c.getAnnotation(SchExtension.class).name() + "]");
                    SchematicExtension ext = c.newInstance();
                    ext.onLoad(tag, file);
                    l.add(ext);
                } catch (InstantiationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

        return l;
    }

    private static boolean nbtContainsPath(NBTTagCompound tag,String exTagFull){
        NBTTagCompound base = tag;
        String[] parts = exTagFull.split(".");

        if(parts.length == 0){
            return base.hasKey(exTagFull);
        }

        for(int i = 0; i<parts.length-1;i++){
            String part = parts[i];
            if(base.hasKey(part)){
                base = base.getCompound(parts[i]);
            }
            else
            {
                return false; 
            }
        }
        return base.hasKey(parts[parts.length-1]);
    }
}
