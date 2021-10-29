package me.HKS.HNS;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import me.HKS.HNS.Listeners.Config;
import me.HKS.HNS.Listeners.StopSound_v1_14_R1;
import me.HKS.HNS.Listeners.StopSound_v1_15_R1;
import me.HKS.HNS.Listeners.StopSound_v1_16_R3;
import me.HKS.HNS.Listeners.StopSound_v1_17_R1;
import me.HKS.HNS.Screen.Actionbar;

/***
 * 
 * Main Class
 * 
 * @author HKS-HNS
 * 
 */

public class Main extends JavaPlugin {
    public static Main Instance;
    public String sversion;
    
	public  Listener StopSound;
    Actionbar Ac = new Actionbar(); // Set's the Actionbar
    
    @Override
    public void onEnable() { 
    	
    	if (!setupManager()) {
    	getLogger().info("Failed to setup Hardcore! Running non-compatible version!");
    	Bukkit.getPluginManager().disablePlugin(this);
    	return;
    	}
        // ge 394 ENTITY_FISHING_BOBBER_RETRIEVE 1.17
        // gf 395 ENTITY_FISHING_BOBBER_SPLASH 1.17
        // gg 396 ENTITY_FISHING_BOBBER_THROW 1.17
        // gN 377 generic.splash 1.17
    	
        Instance = this;
        PluginDescriptionFile pdf = this.getDescription();;
        this.getServer().getPluginManager().registerEvents((Listener) new Config(), (Plugin) this);
        this.getCommand("Hardcore").setExecutor((CommandExecutor) new Config());
        this.getCommand("Hardcore").setTabCompleter((TabCompleter) new Config());
        Ac.Start(); 
        new Updater(pdf, this.getFile());
    }

    @Override
    public void onDisable() {
        Ac.DelAC();
    }

    private boolean setupManager() {
    	sversion = "N/A";
		try {
			sversion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		
		if(sversion.equals("v1_14_R1"))
			StopSound = new StopSound_v1_14_R1();
		else if (sversion.equals("v1_15_R1"))
			StopSound = new StopSound_v1_15_R1();
		else if (sversion.equals("v1_16_R3"))
			StopSound = new StopSound_v1_16_R3();
		else if (sversion.equals("v1_17_R1"))
			StopSound = new StopSound_v1_17_R1();
		
		if (StopSound != null){
	        this.getServer().getPluginManager().registerEvents((Listener) StopSound, (Plugin) this);
	        return true;
		} 
		
		 return false;
    	
    }
    
    public static Main getInstance() {
        return Instance;

    }

}