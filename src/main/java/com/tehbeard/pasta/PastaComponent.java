package com.tehbeard.pasta;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_5_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.tulonsae.mc.util.Log;

import com.tehbeard.pasta.schematic.SchVector;
import com.tehbeard.pasta.schematic.SchematicFile;
import com.tehbeard.pasta.schematic.SchematicWorker;
import com.tehbeard.pasta.schematic.worker.NoAirPastedWorker;
import com.tehbeard.pasta.schematic.worker.OffsetWorker;
import com.tehbeard.pasta.schematic.worker.OriginWorker;

import en.tehbeard.areablock.ArgumentPack;

import net.escapecraft.component.AbstractComponent;
import net.escapecraft.component.BukkitCommand;
import net.escapecraft.component.ComponentDescriptor;
import net.escapecraft.escapePlug.EscapePlug;
import net.minecraft.server.v1_5_R3.World;

@ComponentDescriptor(name="Pasta paste",slug="pasta",version="1.00")
@BukkitCommand(command="pasta")
public class PastaComponent extends AbstractComponent implements CommandExecutor{

    Plugin plugin;
    @Override
    public boolean enable(Log log, EscapePlug plugin) {
        this.plugin = plugin;
        plugin.getComponentManager().registerCommands(this);
        return true;
    }

    @Override
    public void disable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdlbl,
            String[] args) {
        //Generate ArgumentPack from command
        ArgumentPack ap = new ArgumentPack(new String[]{"a","o"}, new String[]{"w"}, args); 

        SchVector vector = new SchVector(0,0,0);
        //Get the world we are working with, Try grab the player's world, else look for the -w flag
        World w = null;
        if(sender instanceof Player){
            w = ((CraftWorld)((Player)sender).getWorld()).getHandle();
            Location l = ((Player) sender).getLocation();
            vector = new SchVector(l.getBlockX(),l.getBlockY(),l.getBlockZ());
        }
        if(ap.getOption("w") != null){
            w = ((CraftWorld)Bukkit.getWorld(ap.getOption("w"))).getHandle();
        }
        //check for null world and stop w/o error
        if(w==null){
            sender.sendMessage(ChatColor.RED + "No world specified for pasting, no paste performed");
            return false;
        }

        
        
        SchematicFile f;
        try {
            //Get the schematic loaded
            //TODO: Cache file?
            for(int i = 0;i<ap.size();i++){
                System.out.println("" + i + " : [" + ap.get(i) + "]");
            }
            f = new SchematicFile(new File(plugin.getDataFolder(),"../WorldEdit/Schematics/" + ap.get(0) + ".schematic"));
            
            //Construct worker
            SchematicWorker worker = new SchematicWorker().loadSchematic(f);//.loadWorkers(new OffsetWorker());
            

            
            //Apply flag option
            if(ap.getFlag("o")){
                System.out.println("Loading at origin");
                worker.loadWorkers(new OriginWorker());
                vector = new SchVector(0,0,0);
            }
            else
            {
                worker.loadWorkers(new OffsetWorker());
            }
            
            if(ap.getFlag("a")){
                worker.loadWorkers(new NoAirPastedWorker());
            }

            worker.paste(w, vector);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

}
