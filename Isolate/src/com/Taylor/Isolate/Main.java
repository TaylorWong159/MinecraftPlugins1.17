package com.Taylor.Isolate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	
	private boolean enabled = false;
	private HashMap<Player, WorldSet> players;
	
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@EventHandler
	public void onPortal(PlayerPortalEvent e) {
		if (enabled && players.containsKey(e.getPlayer())) {
			Location loc = e.getTo();
			loc.setWorld(players.get(e.getPlayer()).getWorld(loc.getWorld().getEnvironment()));
			e.setTo(loc);
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		ArrayList<String> res = new ArrayList<String>();
		if (args.length == 1) {
			res.add("disable");
			res.add("enable");
		}
		return res;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("isolate")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("enable")) {
					enabled = true;
					long seed = (long) Math.floor(Math.random() * Long.MAX_VALUE);
					players = new HashMap<Player, WorldSet>();
					
					for (Player player : Bukkit.getOnlinePlayers()) {
						String key = player.getName();
						String world = "gen_world_" + key, nether = "gen_nether_" + key, end = "gen_end_" + key;
						player.sendMessage(ChatColor.BLUE + "Generating Isolated Worlds");
						World normalWorld = new WorldCreator(world).seed(seed).environment(Environment.NORMAL).createWorld();
						player.sendMessage(ChatColor.BLUE + "Overworld Generated Successfully");
						World netherWorld = new WorldCreator(nether).seed(seed).environment(Environment.NETHER).createWorld();
						player.sendMessage(ChatColor.BLUE + "Nether Generated Successfully");
						World endWorld = new WorldCreator(end).seed(seed).environment(Environment.THE_END).createWorld();
						player.sendMessage(ChatColor.BLUE + "End Generated Successfully");
						players.put(player, new WorldSet(normalWorld, netherWorld, endWorld));
					}
					for (Player player : players.keySet()) {
						player.sendMessage(ChatColor.BLUE + "Teleporting to new World");
						player.teleport(players.get(player).normal.getSpawnLocation());
					}
					sender.sendMessage(ChatColor.GREEN + "World Generation Successful");
					return true;
				} if (args[0].equalsIgnoreCase("disable")) {
					enabled = false;
					for (Player player : players.keySet()) {
						player.sendMessage(ChatColor.BLUE + "Isolation disabled\nChanging dimensions will now lead to normal world");
					}
					return true;
				}
			}
			sender.sendMessage(ChatColor.RED + "improper usage: /isolate <enable/disable>");
			return false;
		}
		return false;
	}
}
