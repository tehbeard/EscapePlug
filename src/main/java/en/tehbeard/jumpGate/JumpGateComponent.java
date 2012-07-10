package en.tehbeard.jumpGate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.tulonsae.mc.util.Log;

import en.tehbeard.areablock.ChunkCache;
import en.tehbeard.areablock.Cuboid;
import en.tehbeard.areablock.CuboidEntry;
import en.tehbeard.areablock.SessionStore;

import net.escapecraft.component.AbstractComponent;
import net.escapecraft.component.BukkitCommand;
import net.escapecraft.component.ComponentDescriptor;
import net.escapecraft.escapePlug.EscapePlug;

@ComponentDescriptor(name="JumpGates",slug="jumpgate",version="1.00")
@BukkitCommand(command="jumpgate")
public class JumpGateComponent extends AbstractComponent implements CommandExecutor, Listener {

    ChunkCache<JumpGate> cache = new ChunkCache<JumpGate>();

    Map<String,JumpGate> jumpgates = new HashMap<String, JumpGate>();

    SessionStore<JumpGateSession> sessions = new SessionStore<JumpGateSession>();

    @Override
    public boolean enable(Log log, EscapePlug plugin) {
        File f = new File(plugin.getDataFolder(),"jumpgates.yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(f);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (InvalidConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        for(String w: config.getStringList("worlds")){
            log.info("Loading world " + w);
            new WorldCreator(w).createWorld();

            //Bukkit.getWorld(w);
        }

        for(String key : config.getConfigurationSection("jumpgates").getKeys(false)){
            JumpGate jg = new JumpGate(config.getConfigurationSection("jumpgates").getConfigurationSection(key));
            jumpgates.put(key,jg);
            cache.addEntry(jg.getCuboid(), jg);
        }

        return true;
    }

    @Override
    public void disable() {
        // TODO Auto-generated method stub

    }

    public boolean onCommand(CommandSender sender, Command cmd, String cmdlbl,
            String[] args) {

        if(args.length==0){
            return false;
        }

        String subcmd = args[0];
        Player p = (Player) sender;
        if(subcmd.equalsIgnoreCase("toggle")){
            if(sessions.hasSession(p.getName())){
                sessions.remove(p.getName());
                sender.sendMessage("Off");
            }
            else
            {
                sessions.putSession(p.getName(),new JumpGateSession());
                sender.sendMessage("On");
            }
            return true;
        }
        
        

        if(!sessions.hasSession(p.getName())){sender.sendMessage("Not in jumpgate mode");return false;}

        if(subcmd.equalsIgnoreCase("debug")){
            if(sessions.hasSession(p.getName())){
                sessions.getSession(p.getName()).setDebugMode(!sessions.getSession(p.getName()).isDebugMode());
                sender.sendMessage("Debug mode: "  + (sessions.getSession(p.getName()).isDebugMode() ? "on" : "off"));
            }
            else
            {
                sender.sendMessage("No session found");
            }
            return true;
        }
        
        if(subcmd.equalsIgnoreCase("make")){
            if(args.length!=2){
                return false;
            }
            JumpGate jg = new JumpGate();
            jg.setName(args[1]);
            Cuboid c = new Cuboid();
            JumpGateSession s = sessions.getSession(p.getName());

            c.setCuboid(s.getV1(), s.getV2(), s.getWorld());
            jg.setCuboid(c);

            jg.setDestination(p.getLocation().clone());

            jumpgates.put(jg.getName(),jg);
            cache.addEntry(jg.getCuboid(), jg);
            sender.sendMessage("gate created");
            return true;
        }

        if(subcmd.equalsIgnoreCase("remove")){
            if(args.length!=2){
                return false;
            }
            
            if(!jumpgates.containsKey(args[1])){
                sender.sendMessage("gate not found");
                return true;
            }
            
            cache.remove(jumpgates.get(args[1]));
            jumpgates.remove(args[1]);
            sender.sendMessage("Gate removed");
            
        }

        return false;
    }

    
    public void onMove(PlayerMoveEvent event){
        if(event.isCancelled()){return;}
        for(CuboidEntry<JumpGate> entry  : cache.getEntries(event.getTo())){
            if(sessions.hasSession(event.getPlayer().getName())){
                if(sessions.getSession(event.getPlayer().getName()).isDebugMode()){
                    event.getPlayer().sendMessage(ChatColor.GOLD + entry.getEntry().getName());
                    return;
                }
            }
            event.setCancelled(true);
            event.getPlayer().teleport(entry.getEntry().getDestination());
            return;
        }
        
    }
}
