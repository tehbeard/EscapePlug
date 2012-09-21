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
        plugin.getCommand("escapeplug").setExecutor(this);
        return true;
    }

    @Override
    public void disable() {
        // TODO Auto-generated method stub

    }

    public boolean onCommand(CommandSender sender, Command cmd, String lbl,
            String[] args) {

        ArgumentPack pack = new ArgumentPack(new String[]{"l"}, new String[] {"e","d","c","r"}, args);


        //enable components

        if(pack.getOption("e")!=null){

            String component = pack.getOption("e");
            if(compMan.startComponent(component, true)){
                sender.sendMessage(ChatColor.GOLD + "Component " + component + " enabled"); 
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "ERROR: Component " + component + " was not enabled");
            }
            return true;
        }


        //disable components
        if(pack.getOption("d")!=null){
            String component = pack.getOption("d");

            if(component.equals("admin")){return true;}//do not unload admin component
            compMan.disableComponent(component);
            sender.sendMessage(ChatColor.GOLD + "Disabling Component " + component); 

            return true;
        }

        //reload configs, does not disable plugins
        if(pack.getOption("c")!=null){
            String component = pack.getOption("c");


            AbstractComponent comp = compMan.getActiveInstance(component);
            if(comp!=null){
                sender.sendMessage(ChatColor.GOLD + "Reloading Component " + component +"'s config");
                comp.reloadConfig();
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
        if(pack.getOption("r")!=null){
            String component = pack.getOption("r");
            sender.sendMessage(ChatColor.GOLD + "Disabling Component " + component);
            compMan.disableComponent(component);
            if(compMan.startComponent(component, true)){
                sender.sendMessage(ChatColor.GOLD + "Component " + component + " enabled"); 
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "ERROR: Component " + component + " was not enabled");
            }


            return true;
        }

        return false;
    }

}
