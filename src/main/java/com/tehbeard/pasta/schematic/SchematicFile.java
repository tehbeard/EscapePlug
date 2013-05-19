package com.tehbeard.pasta.schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_5_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_5_R3.NBTTagCompound;
import net.minecraft.server.v1_5_R3.NBTTagList;
import net.minecraft.server.v1_5_R3.TileEntity;

import com.tehbeard.pasta.schematic.extensions.SchematicExtension;

/**
 * SchematicFile provides a way to read .schematic files
 * SchematicFile acts as a container for SchematicExtensions, which add additional non-standard information to a schematic.
 * Feature supported:
 * <li>Extended Block Ids (WorldEdit feature)</li>
 * <li>Schematic origin and offset (WorldEdit feature)</li>
 * <li>Layers support (Own feature)</li>
 * <li>Support for mod block rotation</li>
 * 
 * Planned features:
 * <li>FML ModItemData dictionary support for automatic block id translation</li>
 * <li>No paste air blocks</li>
 * <li>layer selection support</li>
 * <li>Entity support</li>
 * @author James
 *
 */
public class SchematicFile {

    //schematic size
    private short width = 0;
    private short height = 0;
    private short length = 0;

    //block data
    private short[] blocks;
    private byte[] blockData;

    

    //Complex NBT objects
    private final List<NBTTagCompound> tileEntities = new ArrayList<NBTTagCompound>();
    private final List<NBTTagCompound> entities = new ArrayList<NBTTagCompound>();
    
    
    private List<SchematicExtension> extensions = new ArrayList<SchematicExtension>();

    /**
     * Constructs an empty schematic, Not much use right now, will be when copying is implemented
     * @param width
     * @param height
     * @param length
     */
    public SchematicFile(short width,short height,short length){
        this.width = width;
        this.height = height;
        this.length = length;

        resetArrays();

    }

    /**
     * Loads a schematic 
     * @param is InputStream to schematic
     * @throws IOException
     */
    public SchematicFile(InputStream is) throws IOException{

        loadSchematic(is);
    }

    /**
     * Load schematic from file
     * @param file
     * @throws IOException
     */
    public SchematicFile(File file) throws IOException{

        loadSchematic(new FileInputStream(file));
    }

    /**
     * Resets the arrays
     */
    private void resetArrays(){
        int size = width*height*length;
        blocks = new short[size];
        blockData = new byte[size];
    }

    /**
     * loads the schematic data into memory
     * @throws IOException 
     */
    public void loadSchematic(InputStream is) throws IOException{
        NBTTagCompound tag  = NBTCompressedStreamTools.a(is);

        if(!tag.getName().equalsIgnoreCase("schematic")){
            throw new IOException("File is not a valid schematic");
        }

        width = tag.getShort("Width");
        height = tag.getShort("Height");
        length = tag.getShort("Length");

        SchematicDataRegistry.logger().config("Schematic loaded, [" + width + ", " + height + ", " + length + "]");

        resetArrays();


        
        //read in block data; Vanilla lower byte array
        byte[] b_lower = tag.getByteArray("Blocks");
       

        byte[] addBlocks = new byte[0];
        //Check and load Additional blocks array
        if(tag.hasKey("AddBlocks")){
            SchematicDataRegistry.logger().config("Extended block data detected!");
            addBlocks = tag.getByteArray("AddBlocks");
        }
        
        
        for (int index = 0; index < b_lower.length; index++) {
            if ((index >> 1) >= addBlocks.length) { 
                blocks[index] = (short) (b_lower[index] & 0xFF);
            } else {
                if ((index & 1) == 0) {
                    blocks[index] = (short) (((addBlocks[index >> 1] & 0x0F) << 8) + (b_lower[index] & 0xFF));
                } else {
                    blocks[index] = (short) (((addBlocks[index >> 1] & 0xF0) << 4) + (b_lower[index] & 0xFF));
                }
            }
        }
        

        blockData = tag.getByteArray("Data");

        //load tileEntities
        NBTTagList tileEntityTag = tag.getList("TileEntities");

        for(int i =0;i<tileEntityTag.size();i++){
            tileEntities.add((NBTTagCompound) tileEntityTag.get(i));
        }
        NBTTagList entityTag = tag.getList("Entities");

        for(int i =0;i<entityTag.size();i++){
            entities.add((NBTTagCompound) tileEntityTag.get(i));
        }
        
        extensions = SchematicDataRegistry.getExtensions(tag, this);
    }

    public void saveSchematic(File file) throws IOException{
        throw new UnsupportedOperationException("Not implemented in this version");
        /*NBTTagCompound tag  = new NBTTagCompound("schematic");
        tag.setString("Materials", "Alpha");


        tag.setShort("Width",width);
        tag.setShort("Height",height);
        tag.setShort("Length",length);


        tag.setInteger("WEOriginX",origin.getX());
        tag.setInteger("WEOriginY",origin.getY());
        tag.setInteger("WEOriginZ",origin.getZ());

        tag.setInteger("WEOffsetX",offset.getX());
        tag.setInteger("WEOffsetY",offset.getY());
        tag.setInteger("WEOffsetZ",offset.getZ());


        tag.setByteArray("Layers",layers);
        tag.setByteArray("Blocks",blocks);
        tag.setByteArray("AddBlocks",addBlocks);
        tag.setByteArray("Data",blockData);

        //TODO: PARSE TILE ENTITIES


        //TODO: PARSE ENTITIES*/

    }

    /**
     * Get the block id at a coordinate, This value may be 0-4096 (Includes extended block support)
     * @param x
     * @param y
     * @param z
     * @return
     */
    public int getBlockId(int x,int y,int z){

        int index =  (y * width * length) + (z * width) + x;

        if(index < 0 || index >= blocks.length){
            return 0;
        }

        return blocks[index];
    }

    /**
     * Get the block data at a coordinate (0-15)
     * @param x
     * @param y
     * @param z
     * @return
     */
    public int getBlockData(int x,int y,int z){

        int index =  y * width *length + z * width + x;
        if(index < 0 || index >= blockData.length){
            return 0;
        }
        return blockData[index];
    }

    /**
     * Set the block id at a coordinate (May not work, not tested with extended block support)
     * @param x
     * @param y
     * @param z
     * @param block
     */
    public void setBlockId(int x,int y,int z,int block){

        int index =  (y * width * length) + (z * width) + x;
        if(index < 0 || index >= blocks.length){
            return;
        }
        blocks[index] = (short) block;
    }

    /**
     * Set the block data at a coordinate
     * @param x
     * @param y
     * @param z
     * @param data
     */
    public void setBlockData(int x,int y,int z,byte data){

        int index =  y * width *length + z * width + x;
        if(index < 0 || index >= blockData.length){
            return;
        }
        blockData[index] = data;
    }

    /**
     * Size along x axis
     * @return
     */
    public final short getWidth() {
        return width;
    }

    /**
     * Size along y axis
     * @return
     */
    public final short getHeight() {
        return height;
    }

    /**
     * Size along z axis
     * @return
     */
    public final short getLength() {
        return length;
    }

    /**
     * Get all tile entities in this schematic
     * @return
     */
    public final List<NBTTagCompound> getTileEntities() {
        return tileEntities;
    }
    

    /**
     * Grab tile entity at location
     * @param x
     * @param y
     * @param z
     * @return tag for tile entity, or null for none found
     */
    public final NBTTagCompound getTileEntityTagAt(int x,int y, int z){

        for(NBTTagCompound tileEntity:  getTileEntities()){

            if( 
                    tileEntity.getInt("x") == x &&
                    tileEntity.getInt("y") == y &&
                    tileEntity.getInt("z") == z 
                    ){
                return tileEntity;
            }
        }
        return null;
    }

    /**
     * Returns a tile entity object or null if none found
     * @param x
     * @param y
     * @param z
     * @return
     */
    public final TileEntity getTileEntityAt(int x,int y, int z){
        NBTTagCompound te = getTileEntityTagAt(x, y, z);
        if(te==null){return null;}
        return TileEntity.c(te);
    }

    /**
     * Return list of entity tags, not used currently
     * @return
     */
    public final List<NBTTagCompound> getEntities() {
        return entities;
    }



    public String toString(){
        return "Schematic {" +
                "[w: " + getWidth() + ", " +
                "l: " + getLength() + ", " +
                "h: " + getHeight() + "]\n}";

    }


  
    
    @SuppressWarnings("unchecked")
    public <T> T getExtension(Class<T> cl){
        for(SchematicExtension se : extensions){
            if(cl.isInstance(se)){
                return (T) se;
            }
        }
        
        return null;
        
    }
}

