package me.HKS.HNS;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.HKS.HNS.Listeners.Config;
import me.HKS.HNS.Screen.Actionbar;



public class Main extends JavaPlugin{
	Actionbar Ac = new Actionbar(); // Set's the Actionbar
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents((Listener)new Config(), (Plugin)this);
		this.getCommand("Hardcore").setExecutor((CommandExecutor) new Config());
		this.getCommand("Hardcore").setTabCompleter((TabCompleter) new Config());
		Ac.Start(); 
	}
	
	@Override
	public void onDisable() {
		Ac.DelAC(); 
	} 
}
