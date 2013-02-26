package com.tehbeard.bungeejump;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import net.escapecraft.escapePlug.EscapePlug;
import net.escapecraft.component.AbstractComponent;
import net.escapecraft.component.BukkitCommand;
import net.escapecraft.component.ComponentDescriptor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.tulonsae.mc.util.Log;



/**
 * Manages loading and saving of kit data
 * @author james
 *
 */

@ComponentDescriptor(name="BungeeJump server jump",slug="bungeejump",version="1.0")
@BukkitCommand(command="bungeejump")
public class BungeeJumpComponent extends AbstractComponent implements CommandExecutor{


	private Plugin plugin;
	@Override
	public boolean enable(Log log, EscapePlug plugin) {
		this.plugin = plugin;
		Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
		plugin.getComponentManager().registerCommands(this);
		return true;
	}

	@Override
	public void disable() {

	}

	public boolean onCommand(CommandSender sender, Command cmd, String cmdlbl,
			String[] args) {
		if(sender instanceof Player){
			Player p = (Player)sender;
			if(args.length == 1){
				if(p.hasPermission("escapeplug.bungeejump." + args[0])){
					bungeeJumpToServer(p,args[0]);
				}
			}
		}
		return true;
	}


	private void bungeeJumpToServer(Player player,String serverId){
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF("Connect");
			out.writeUTF(serverId); // Target Server
		} catch (IOException e) {
			// Can never happen
		}
		player.sendPluginMessage(this.plugin, "BungeeCord", b.toByteArray());

		try {
			b.close();
			out.close();
		} catch (IOException e) {
			//tidy up
		}
	}



}