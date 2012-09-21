package net.escapecraft.escapePlug;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.tulonsae.mc.util.Log;

import en.tehbeard.areablock.ArgumentPack;

import net.escapecraft.component.AbstractComponent;
import net.escapecraft.component.ComponentDescriptor;
import net.escapecraft.component.ComponentManager;

@ComponentDescriptor(name="Component Admin",slug="admin",version="1.00")
public class AdminComponent extends AbstractComponent implements CommandExecutor {

    ComponentManager compMan;
    @Override
    public boolean enable(Log log, EscapePlug plugin) {
        compMan = plugin.getComponentManager();
        return false;
    }

    @Override
    public void disable() {
        // TODO Auto-generated method stub

    }

    public boolean onCommand(CommandSender sender, Command cmd, String lbl,
            String[] args) {

        ArgumentPack pack = new ArgumentPack(new String[] {"e","d","c","l","r"}, new String[] {}, args);


        //enable components
        if(pack.getFlag("e")){
            if(pack.size()==0){
                sender.sendMessage(ChatColor.RED+ "MUST SUPPLY COMPONENTS TO ENSABLE");
                return true;
            }
            for(int i = 0;i < pack.size();i++){
                if(compMan.startComponent(pack.get(i), true)){
                    sender.sendMessage(ChatColor.GOLD + "Component " + pack.get(i) + " enabled"); 
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "ERROR: Component " + pack.get(i) + " was not enabled");
                }
            }
            return true;
        }


        //disable components
        if(pack.getFlag("d")){
            if(pack.size()==0){
                sender.sendMessage(ChatColor.RED+ "MUST SUPPLY COMPONENTS TO DISABLE");
                return true;
            }
            for(int i = 0;i < pack.size();i++){
                if(pack.get(i).equals("admin")){continue;}//do not unload admin component
                compMan.disableComponent(pack.get(i));
                sender.sendMessage(ChatColor.GOLD + "Disabling Component " + pack.get(i)); 
            }
            return true;
        }

        //reload configs, does not disable plugins
        if(pack.getFlag("c")){
            if(pack.size()==0){
                sender.sendMessage(ChatColor.RED+ "MUST SUPPLY COMPONENTS TO RELOAD CONFIG");
                return true;
            }
            for(int i = 0;i < pack.size();i++){
                AbstractComponent comp = compMan.getActiveInstance(pack.get(i));
                if(comp!=null){
                    sender.sendMessage(ChatColor.GOLD + "Reloading Component " + pack.get(i) +"'s config");
                    comp.reloadConfig();
                }

            }
            return true;
        }

        //list components
        if(pack.getFlag("l")){
            for(String l :compMan.listComponentsPretty()){
                sender.sendMessage(ChatColor.GOLD + l);
            }
            return true;
        }

        //reload plugin
        if(pack.getFlag("r")){
            if(pack.size()==0){
                sender.sendMessage(ChatColor.RED+ "MUST SUPPLY COMPONENTS TO RELOAD CONFIG");
                return true;
            }
            for(int i = 0;i < pack.size();i++){
                sender.sendMessage(ChatColor.GOLD + "Disabling Component " + pack.get(i));
                compMan.disableComponent(pack.get(i));
                if(compMan.startComponent(pack.get(i), true)){
                    sender.sendMessage(ChatColor.GOLD + "Component " + pack.get(i) + " enabled"); 
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "ERROR: Component " + pack.get(i) + " was not enabled");
                }

            }
            return true;
        }

        return false;
    }

}
