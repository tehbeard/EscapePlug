package net.escapecraft.component;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.escapecraft.escapePlug.EscapePlug;

import org.bukkit.command.CommandExecutor;
import org.tulonsae.mc.util.Log;


public class ComponentManager{

    private EscapePlug plugin;
    private Log log;
    public ComponentManager(EscapePlug plugin,Log log){
        this.plugin = plugin;
        this.log = log;
    }

    private Set<AbstractComponent> activeComponents = new HashSet<AbstractComponent>();

    private HashMap<String,Class<? extends AbstractComponent>> components = new HashMap<String, Class<? extends AbstractComponent>>();

    /**
     * add a component to this manager
     * @param component
     */
    public void addComponent(Class<? extends AbstractComponent> component){
        ComponentDescriptor cd = component.getAnnotation(ComponentDescriptor.class);
        if(cd!=null)	{
            components.put(cd.slug(), component);
        }

    }

    public String[] listComponentsPretty(){
        String[] out = new String[activeComponents.size()];

        int i =0;
        for(AbstractComponent component:activeComponents){
            ComponentDescriptor cd = component.getClass().getAnnotation(ComponentDescriptor.class);
            out[i] = cd.name() + " [" + cd.slug() + "] Version: " + cd.version();
            i++;
        }
        return out;
    }

    /**
     * Start an individual component
     * @param slug
     */
    public boolean startComponent(String slug,boolean override){
        Class<? extends AbstractComponent> component = components.get(slug);
        if(component !=null){
            ComponentDescriptor cd = component.getAnnotation(ComponentDescriptor.class);
            if(cd!=null){
                if(plugin.getConfig().getBoolean("plugin." +cd.slug() + ".enabled", true) || override){
                    try {
                        log.info("Enabling " + cd.name() + " " + cd.version());
                        Log compLog = new Log("EscapePlug",cd.name());
                        return enableComponent(compLog,component.newInstance());
                    } catch (Exception e) {
                        log.info("Exception occured during enabling component " + slug);
                        e.printStackTrace();
                    } 
                }
            }
        }
        return false;
    }

    /**
     * Attempt to start all components that have been loaded
     */
    public void startupComponents(){
        for(String slug:components.keySet()){
            startComponent(slug,false);
        }
    }




    private boolean enableComponent(Log log,AbstractComponent component){
        if(component.enable(log,plugin)){
            activeComponents.add(component);
            return true;
        }
        return false;
    }

    /**
     * Returns an instance of this component that is active, or null if not loaded
     * @param slug
     * @return
     */
    public AbstractComponent getActiveInstance(String slug){
        Iterator<AbstractComponent> it = activeComponents.iterator();
        AbstractComponent component;
        while(it.hasNext()){
            component = it.next();
            ComponentDescriptor cd = component.getClass().getAnnotation(ComponentDescriptor.class);
            if(cd!=null){
                if(cd.slug().equals(slug)){
                    return component;
                }
            }
        }
        return null;
    }


    /**
     * Reloads all configs
     * @param slug
     * @return
     */
    public void reloadAllComponentConfig(){
        Iterator<AbstractComponent> it = activeComponents.iterator();
        AbstractComponent component;
        while(it.hasNext()){
            component = it.next();
            component.reloadConfig();
        }

    }


    /**
     * Disable an active component
     * @param slug component to disable
     */


    public void disableComponent(String slug){
        Iterator<AbstractComponent> it = activeComponents.iterator();
        AbstractComponent component;
        while(it.hasNext()){
            component = it.next();
            ComponentDescriptor cd = component.getClass().getAnnotation(ComponentDescriptor.class);
            if(cd!=null){
                if(cd.slug().equals(slug)){
                    component.disable();
                    it.remove();
                }
            }
        }
    }


    /**
     * Register a command executor
     * @param executor
     */
    public void registerCommands(CommandExecutor executor){
        for(Annotation a: executor.getClass().getAnnotations()){

            if(a instanceof BukkitCommand){
                BukkitCommand bc = (BukkitCommand)a;
                for(String comm : bc.command()){
                    plugin.getCommand(comm).setExecutor(executor);
                }
            }
        }
    }

    public void disableComponents(){
        log.info("Shutting down");
        for(AbstractComponent comp : activeComponents){
            comp.disable();
        }
    }


}
