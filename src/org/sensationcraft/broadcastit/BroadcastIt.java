package org.sensationcraft.broadcastit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BroadcastIt extends JavaPlugin{

	private MessageSelector selector;
	private BukkitTask broadcaster;
	private String prefix;

	@Override
	public void onEnable(){
		this.saveDefaultConfig();
		final List<Message> messages = this.parseConfig(this.getConfig().getStringList("Messages"));
		final boolean random = this.getConfig().getBoolean("Random Broadcasting");
		final boolean weighted = this.getConfig().getBoolean("Weighted Broadcasting");
		final long interval = this.getConfig().getLong("Interval", 3000L);
		this.prefix = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("Prefix"));
		this.selector = new MessageSelector(messages, random, weighted);
		this.broadcaster = new Broadcast().runTaskTimer(this, interval, interval);
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args){
		if(!sender.hasPermission("broadcastit.reload")){
			sender.sendMessage(ChatColor.RED+"You don't have permission to do that!");
			return false;
		}
		if((args.length != 1) || !args[0].equalsIgnoreCase("reload")){
			sender.sendMessage(ChatColor.RED+"/broadcastit reload");
			return false;
		}
		this.reloadConfig();
		final List<Message> messages = this.parseConfig(this.getConfig().getStringList("Messages"));
		final boolean random = this.getConfig().getBoolean("Random Broadcasting");
		final boolean weighted = this.getConfig().getBoolean("Weighted Broadcasting");
		final long interval = this.getConfig().getLong("Interval", 3000L);
		this.prefix = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("Prefix"));
		this.selector = new MessageSelector(messages, random, weighted);
		this.broadcaster.cancel();
		this.broadcaster = new Broadcast().runTaskTimer(this, interval, interval);
		sender.sendMessage(ChatColor.GOLD+"BroadcastIt reloaded.");
		return true;
	}

	private List<Message> parseConfig(final List<String> strings){
		final List<Message> messages = new ArrayList<Message>();
		if(strings.isEmpty()){
			this.getLogger().warning("No messages were found!");
			return messages;
		}
		for(final String string:strings){
			final String[] split = string.split(";");
			if(split.length == 1)
				messages.add(new Message(ChatColor.translateAlternateColorCodes('&', string)));
			else if((split.length == 2) && split[1].matches("[0-9]*.[0-9]*"))
				messages.add(new Message(ChatColor.translateAlternateColorCodes('&', split[0]), Double.parseDouble(split[1])));
			else
				this.getLogger().warning("Failed to read message \""+string+"\"!");
		}
		return messages;
	}

	private class Broadcast extends BukkitRunnable{

		@Override
		public void run() {
			final String message = BroadcastIt.this.prefix.concat(BroadcastIt.this.selector.getMessage().getMessage());
			for(final Player player:Bukkit.getOnlinePlayers())
				if(!player.hasPermission("broadcastit.exempt"))
					player.sendMessage(message);
		}
	}
}
