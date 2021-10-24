package me.HKS.HNS.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import me.HKS.HNS.Main;

public class StopSound {


	public StopSound() {
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		manager.addPacketListener(new PacketAdapter(Main.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
			 @Override
			public void onPacketSending(PacketEvent e) {	
				 if (e.getPacketType() == PacketType.Play.Server.NAMED_SOUND_EFFECT) {
					 String DeathWorld = "DeathPlayerWorld";
		              Player p = e.getPlayer();
		             
		              if (Bukkit.getWorld(DeathWorld) != null)
		              if (p.getWorld().equals(Bukkit.getWorld(DeathWorld))) { 
		            	  Bukkit.getLogger().info( e.getPacket()+" " + p.getDisplayName());
		            	  e.setCancelled(true);
                }}
		     
		    }
		});
	}
	
	
	
	
}
