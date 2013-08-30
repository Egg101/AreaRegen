package io.github.Egg101.AreaRegen;

import io.github.Egg101.AreaRegen.TerrainManager;

import java.io.File;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;


public class AreaRegen extends JavaPlugin implements Listener{
	Logger log;
    public static Permission perms = null;
    Location l1;
    Location l2;
    Plugin plugin;
    Server s;
    
    @Override
    public void onEnable(){
    	// Set up main plugin stuff
		log = this.getLogger();
		log.info("[AreaRegen] Enabled");
		
		getServer().getPluginManager().registerEvents(this, this);
		plugin = getServer().getPluginManager().getPlugin("AreaRegen");
    	
	    File file = new File(this.getDataFolder(), "config.yml");
	    if (!file.exists()) {
			log.info("[AreaRegen] Creating config.yml...");
	        this.saveDefaultConfig();
			log.info("[AreaRegen] Successfully created config.yml!");
	    }
        setupPermissions();
        
        // END SETTING MAIN PLUGIN THINGS
        
        // Set up TerrainManager (wrapper for WorldEdit)
        final WorldEditPlugin wep = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (wep == null) {
          // then don't try to use TerrainManager!
        }
        long time = getConfig().getLong("interval_mins")*60*20;

        l1 = new Location(getServer().getWorld(getConfig().getString("worldname")),
        				  getConfig().getDouble("pos.x"),
        				  getConfig().getDouble("pos.y"),
        				  getConfig().getDouble("pos.z"));
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
        {
            public void run()
            {
            	if(getConfig().getBoolean("enabled") != false){
	                TerrainManager tm = new TerrainManager(wep, getServer().getWorld(getConfig().getString("worldname")), log);
	                File schematic = new File(plugin.getDataFolder(), getConfig().getString("schematicname"));
	            	try {
	        		  // reload at the given location
	        		  tm.loadSchematic(schematic);
	        		} catch (Exception e) {
	        			log.info("[AreaRegen] Error with loading schematic");
	        		}
            	}
            }
        }, (time/2), time);
    }
    
    @Override
    public void onDisable() {
		log.info("[AreaRegen] Disabled");
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		Player player = (Player) sender;
        final WorldEditPlugin wep = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
		
    	if(cmd.getName().equalsIgnoreCase("arearegen")){
    		if (args.length == 1 && args[0].equalsIgnoreCase("reload")){
    			if(perms.has(player, "autoregen.admin")) {
    				this.reloadConfig();
    				player.sendMessage(ChatColor.BLUE + "Loaded config.yml from plugins/AreaRegen/");
    			}
    		} // end /arearegen reload

    		// /arearegen pos1
    		/*if (args.length == 1 && args[0].equalsIgnoreCase("setpos")){
    			if(perms.has(player, "autoregen.admin")) {
    				Location playerLoc = player.getLocation();
    				log.info(String.valueOf(playerLoc.getX()));
					this.getConfig().set("pos.x", playerLoc.getX());
					this.getConfig().set("pos.y", playerLoc.getY());
					this.getConfig().set("pos.z", playerLoc.getZ());
					
			        l1.setX(playerLoc.getX());
			        l1.setY(playerLoc.getX());
			        l1.setZ(playerLoc.getX());
			        
    				player.sendMessage(ChatColor.BLUE + "Set position successfully.");
					this.saveConfig();
    			}
    		}*/
    		// /arearegen pos1
    		if (args.length == 1 && args[0].equalsIgnoreCase("loadnow")){
    			if(perms.has(player, "autoregen.admin")) {
    				log.info("1");
    		        TerrainManager tm = new TerrainManager(wep, getServer().getWorld(getConfig().getString("worldname")),log);
    				log.info("2");
    		        File schematic = new File(plugin.getDataFolder(), getConfig().getString("schematicname"));
    				log.info("3");
                	try {
              		  // reload at the given location
              		  tm.loadSchematic(schematic);
            			player.sendMessage(ChatColor.BLUE + "Loaded schematic.");
      				log.info("4a");
              		} catch (Exception e) {
              			log.info("[AreaRegen] Error with loading schematic");
        				log.info("4b");
              		}
    				log.info("5");
    				this.reloadConfig();
    			}
    		}
    		
    	} // end /arearegen
    	return true;
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
}
 