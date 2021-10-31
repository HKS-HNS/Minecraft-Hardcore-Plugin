package me.HKS.HNS.Listeners;

import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.game.PacketPlayOutNamedSoundEffect;
import net.minecraft.sounds.SoundEffects;

public class StopSound_v1_17 implements Listener, StopSoundInf {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        injectPlayer(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        removePlayer(e.getPlayer());
    }
    
    @EventHandler
    public void onSwitchSlot(PlayerItemHeldEvent e ) {
    	Player p = e.getPlayer();
    	ItemStack Frod = new ItemStack(Material.FISHING_ROD);
        ItemMeta itemMeta = Frod.getItemMeta();
        itemMeta.setUnbreakable(true);
        Frod.setItemMeta(itemMeta);
    	if (p.getWorld().getName().equalsIgnoreCase("DeathPlayerWorld")) {
    		if (p.getInventory().getItem(e.getPreviousSlot()) != null)
           if (p.getInventory().getItem(e.getPreviousSlot()).equals(Frod) ) {
        	   if (playersFishing.contains(p.getUniqueId())) 
        	   playersFishing.remove(p.getUniqueId());
           }

    	}
    }
    
    @EventHandler
    public void onFish(PlayerFishEvent e) {
        if (e.getPlayer().getWorld().getName().equalsIgnoreCase("DeathPlayerWorld")) {
            State fishst = e.getState();
            UUID PlayerUUID = e.getPlayer().getUniqueId();
            if (fishst.equals(PlayerFishEvent.State.FISHING)) {
            	if (!playersFishing.contains(PlayerUUID)) 
                playersFishing.add(PlayerUUID);
            	if (!playerThrown.contains(PlayerUUID)) 
                playerThrown.add(PlayerUUID);
            } else if (fishst.equals(PlayerFishEvent.State.IN_GROUND) || fishst.equals(PlayerFishEvent.State.REEL_IN)) {
            	if (!playerReelin.contains(PlayerUUID)) 
            	playerReelin.add(PlayerUUID);

            }

        }
    } 
    
    public void removePlayer(Player p) {
    	
    	try {
    		
    	Channel channel = getChannel(p);
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(p.getName());
            return null;
        });
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void injectPlayer(Player p) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                super.channelRead(channelHandlerContext, packet);
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise promise) throws Exception {
                if (packet instanceof PacketPlayOutNamedSoundEffect) {
                    PacketPlayOutNamedSoundEffect packetSoundNamed = (PacketPlayOutNamedSoundEffect) packet;
                    if (packetSoundNamed.b().equals(SoundEffects.ge) || packetSoundNamed.b().equals(SoundEffects.gf) || packetSoundNamed.b().equals(SoundEffects.gg) || packetSoundNamed.b().equals(SoundEffects.gN)) {
                        UUID PlayerUUID = p.getUniqueId();
                        if (playerReelin.contains(PlayerUUID)) {
                            playerReelin.remove(PlayerUUID);
                            playersFishing.remove(PlayerUUID);
                            if (!packetSoundNamed.b().equals(SoundEffects.ge)) {
                                return;
                            }

                        } else if (playerThrown.contains(PlayerUUID)) {

                            playerThrown.remove(PlayerUUID);
                            if (!packetSoundNamed.b().equals(SoundEffects.gg)) {
                                return;
                            }
                        } else if (playersFishing.contains(PlayerUUID)) {
                            if (!packetSoundNamed.b().equals(SoundEffects.gN) && !packetSoundNamed.b().equals(SoundEffects.gf)) {
                                return;
                            }
                        } else {
                            return;
                        }
                        // ge 394 ENTITY_FISHING_BOBBER_RETRIEVE 
                        // gf 395 ENTITY_FISHING_BOBBER_SPLASH
                        // gg 396 ENTITY_FISHING_BOBBER_THROW
                        // gN 377 generic.splash	
                        
                    }
                }
                super.write(channelHandlerContext, packet, promise);
            }
        };

        
        ChannelPipeline pipeline = getChannel(p).pipeline();
        pipeline.addBefore("packet_handler", p.getName(), channelDuplexHandler);
    };

    public Channel getChannel(Player p)  {
    	
    	try {  
    		
    		
    		Class<?> craftplayer = getCraftBukkitClass("entity.CraftPlayer"); 
    		Method getHandle = craftplayer.getMethod("getHandle");
    		Object nms = getHandle.invoke(p);
    		Object b = nms.getClass().getField("b").get(nms);
    		Object a = b.getClass().getField("a").get(b);
    		Object chan = a.getClass().getField("k").get(a);
    		return (Channel) chan;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    	
    }
    
    public Class<?> getNMSClass(String name) throws ClassNotFoundException {
      return Class.forName("net.minecraft.server." + getServerVersion() + "." + name);
    }

    public Class<?> getCraftBukkitClass(String name) throws ClassNotFoundException {
      return Class.forName("org.bukkit.craftbukkit." + getServerVersion() + "." + name);
    }

    public String getServerVersion() {
      return  Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    }

}