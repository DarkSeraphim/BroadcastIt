package net.darkseraphim.broadcast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author DarkSeraphim
 */
public class Main extends JavaPlugin
{
        
    private String[] messages;
    
    private String prefix;
    
    private File messages_txt;
    
    private BukkitTask task = null;
    
    private int counter = -1;
    
    @Override
    public void onEnable()
    {
        saveDefaultConfig();
        this.messages_txt = new File(getDataFolder(), "messages.txt");
        if(!this.messages_txt.exists())
        {
            boolean exists = false;
            FileOutputStream out = null;
            try
            {
                if(!this.messages_txt.createNewFile())
                    throw new IOException("Failed to create /plugins/BroadcastIt/messages.txt");
                exists = true;
                out = new FileOutputStream(this.messages_txt);
                out.write("Each new line represents a message".getBytes());
                out.write("If you want messages to be split, use \\n".getBytes());
                out.write("You can add colours and formats by using the usual &(0-9 or a-f or k-o)".getBytes());
            }
            catch(IOException ex)
            {
                if(!exists)
                {
                    getLogger().log(Level.WARNING, "Failed to create /plugins/BroadcastIt/messages.txt");
                    getLogger().log(Level.WARNING, "Please create the file yourself to add messages");
                }
            }
            finally
            {
                if(out != null)
                {
                    try
                    {
                        out.close();
                    }catch(IOException ex){/* shh, It's ok */}
                }
            }
            getLogger().info("Please customize messages.txt as you like");
        }
        else
        {
            loadMessages();
            this.prefix = getConfig().getString("prefix", "&4[&aBroadcast&4]&7: &a");
                this.prefix = ChatColor.translateAlternateColorCodes('&', this.prefix);
                if(this.task != null)
                    this.task.cancel();
                this.task = new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        magic();
                    }
                }.runTaskTimer(this, getConfig().getLong("interval", 0)/20L, getConfig().getLong("interval", 1200)/20L);
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(args.length > 0)
        {
            if(args[0].equalsIgnoreCase("reload"))
            {
                reloadConfig();
                loadMessages();
                this.prefix = getConfig().getString("prefix", "&4[&aBroadcast&4]&7: &a");
                this.prefix = ChatColor.translateAlternateColorCodes('&', this.prefix);
                if(this.task != null)
                    this.task.cancel();
                this.task = new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        magic();
                    }
                }.runTaskTimer(this, getConfig().getLong("interval", 0)/20L, getConfig().getLong("interval", 1200)/20L);
            }
        }
        return true;
    }
    
    private void loadMessages()
    {
        FileReader fr = null;
        BufferedReader reader = null;
        try
        {
            fr = new FileReader(this.messages_txt);
            reader = new BufferedReader(fr);
            String line;
            List<String> msgs = new ArrayList<String>();
            while((line = reader.readLine()) != null)
                msgs.add(ChatColor.translateAlternateColorCodes('&', line.replace("\\n", "\n")));
            this.messages = msgs.toArray(new String[msgs.size()]);
        }
        catch(IOException ex)
        {
            getLogger().severe("Failed to load /plugins/BroadcastIt/messages.txt");
            this.messages = new String[0];
        }
        finally
        {
            try
            {
                if(fr != null)
                    fr.close();
                if(reader != null)
                    reader.close();
            }
            catch(IOException ex)
            {
                // Shh, it's ok...
            }
        }
    }
    
    private void magic()
    {
        if(this.messages.length < 1)
            return;
        this.counter++;
        this.counter %= this.messages.length;
        String message = String.format("%s%s", this.prefix, this.messages[this.counter]);
        for(Player player : Bukkit.getOnlinePlayers())
            player.sendMessage(message);
    }
}
