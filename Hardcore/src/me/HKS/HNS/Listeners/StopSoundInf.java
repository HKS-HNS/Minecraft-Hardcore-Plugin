package me.HKS.HNS.Listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.netty.channel.Channel;

public interface StopSoundInf{
    List < UUID > playersFishing = new ArrayList < > ();
    List < UUID > playerThrown = new ArrayList < > ();
    List < UUID > playerReelin = new ArrayList < > ();
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e);
    
    @EventHandler
    public void onLeave(PlayerQuitEvent e);
    
    @EventHandler
    public void onSwitchSlot(PlayerItemHeldEvent e);
    
    @EventHandler
    public void onFish(PlayerFishEvent e);
    
    void removePlayer(Player p);
    void injectPlayer(Player p);
    
    Channel getChannel(Player p);

    //public Object getSoundEffect(String name) throws Exception;
    public Class<?> getNMSClass(String name) throws ClassNotFoundException;
    public Class<?> getCraftBukkitClass(String name) throws ClassNotFoundException;
    public String getServerVersion();
}
