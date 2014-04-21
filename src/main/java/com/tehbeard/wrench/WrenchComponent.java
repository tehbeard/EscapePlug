package com.tehbeard.wrench;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import net.escapecraft.component.AbstractComponent;
import net.escapecraft.component.ComponentDescriptor;
import net.escapecraft.escapeplug.EscapePlug;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredListener;

import com.tehbeard.areablock.SessionStore;

@ComponentDescriptor(name="Wrench",slug="wrench",version="1.00")
public class WrenchComponent extends AbstractComponent implements Listener {

    private ItemStack breakBlockWith;

    public enum WrenchState{
        NONE("Do nothing"),
        ROTATE("Rotate machinery"),
        BREAK("Break machinery");

        private String msg;

        private WrenchState(String msg){
            this.msg = msg;
        }

        public String getMsg(){
            return this.msg;
        }
    }
    //Patch to initialise session
    private SessionStore<WrenchState> state = new SessionStore<WrenchState>(){
        public WrenchState getSession(String player) {
            if(!super.hasSession(player)){
                putSession(player, WrenchState.NONE);
            }
            return super.getSession(player);
        };

    };

    private Material wrenchObject = Material.GOLD_PICKAXE;//Object to check in hand for wrench

    private Set<Material> wrenchableObjects = new HashSet<Material>(); // List of blocks that can be picked up

    private Map<Material,Integer[]> rotationMap = new HashMap<Material, Integer[]>();//List of blocks and metadata to rotate between

    private String protectionPlugin = null;

    @Override
    public boolean enable(EscapePlug plugin) {
        breakBlockWith = new ItemStack(Material.DIAMOND_PICKAXE);
        breakBlockWith.addEnchantment(Enchantment.SILK_TOUCH, 1);

        //Load breakable objects and wrench object
        ConfigurationSection cfg = plugin.getConfig().getConfigurationSection("wrench");
        wrenchObject = Material.getMaterial(cfg.getInt("wrench",wrenchObject.getId()));
        for(int i : cfg.getIntegerList("breakable")){
            wrenchableObjects.add(Material.getMaterial(i));
        }
        
        protectionPlugin = cfg.getString("protectionPlugin",null);

        ConfigurationSection rotations = cfg.getConfigurationSection("rotate");

        for(String k : rotations.getKeys(false)){
            List<Integer> v = rotations.getIntegerList(k);
            Integer[] b = new Integer[v.size()];
            for(int i =0;i<b.length;i++){
                b[i] = v.get(i);
            }
            rotationMap.put(
                    Material.getMaterial(Integer.parseInt(k)), 
                    b);
        }



        Bukkit.getPluginManager().registerEvents(this,plugin);
        Bukkit.getPluginManager().registerEvents(state,plugin);

        return true;
    }

    @Override
    public void disable() {

    }

    public boolean canBreak(Material m){
        return wrenchableObjects.contains(m);
    }

    public int nextRotation(Material m, int current){
        if(!rotationMap.containsKey(m)){return -1;}

        Integer[] map = rotationMap.get(m);
        Object res = nextOf(map, current);
        if(res == null){
            return -1;
        }
        return (Integer)res;
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onWrench(PlayerInteractEvent event){
        //Fail if
        //Not sneaking
        //Wrench not active and not clicking air
        //or not holding a wrench
        if(
                !event.getPlayer().isSneaking() ||
                (state.getSession(event.getPlayer().getName()) == WrenchState.NONE && event.getAction() != Action.RIGHT_CLICK_AIR)  ||
                event.getMaterial() != wrenchObject
                ) {
            return;
        }

        //Cycle wrench state
        if(event.getAction() == Action.RIGHT_CLICK_AIR){
            WrenchState newState = (WrenchState)nextOf(WrenchState.values(), state.getSession(event.getPlayer().getName()));
            event.getPlayer().sendMessage("Wrench will now: " + newState.getMsg());
            state.putSession(event.getPlayer().getName(), newState);
            return;
        }

        if(!checkPermission(event.getClickedBlock(), event.getPlayer())){
            event.getPlayer().sendMessage(ChatColor.RED + "Cannot use wrench here");
            return;
        }

        WrenchState mode = state.getSession(event.getPlayer().getName());
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && mode != WrenchState.NONE){

            //Try instamine the block
            if(mode == WrenchState.BREAK){
                //Check on list of breakble blocks, if so break it.
                if(canBreak(event.getClickedBlock().getType())){
                    //Break it and fix the breaking itemstack
                    event.setCancelled(true);
                    event.getClickedBlock().breakNaturally(breakBlockWith); // Hack to ensure broken properly
                    breakBlockWith.setDurability((short) 5);//reset hack
                }else{
                    event.getPlayer().sendMessage(ChatColor.GOLD + "Can't wrench that!");
                }
                return;
            }

            if(mode == WrenchState.ROTATE){
                //Get next rotation
                int nextValue = nextRotation(event.getClickedBlock().getType(), event.getClickedBlock().getData());
                if(nextValue != -1){
                    //rotate the block
                    event.setCancelled(true);
                    event.getClickedBlock().setData((byte) nextValue);
                    
                }else{
                    event.getPlayer().sendMessage(ChatColor.GOLD + "Can't rotate that!");
                    
                }
                return;
            }
        }
    }

    private Object nextOf(Object[] arr, Object entry){
        for(int i = 0;i<arr.length;i++){
            if(arr[i] == entry){
                return arr[((i+1) < arr.length ? (i+1) : 0)];
            }
        }
        return null;
    }

    /**
     * This code pulls the listeners for the {@link BlockBreakEvent}, and finds those registered for a specific plugin
     * We then call all of those listeners (usually one), and check the isCancelled() result
     * @param b
     * @param p
     * @return
     */
    private boolean checkPermission(Block b, Player p){
        BlockBreakEvent ev = new BlockBreakEvent(b, p);
        for(RegisteredListener handler : BlockBreakEvent.getHandlerList().getRegisteredListeners()){
            if(handler.getPlugin().getName().equalsIgnoreCase(protectionPlugin)){
                try {
                    handler.callEvent(ev);
                } catch (EventException e) {
                    Logger.getGlobal().severe("Failed to query protection plugin " + protectionPlugin);
                    e.printStackTrace();
                }
            }
        }
        return !ev.isCancelled();

    }
}
