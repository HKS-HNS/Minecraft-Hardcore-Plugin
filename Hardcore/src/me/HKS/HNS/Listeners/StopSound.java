package me.HKS.HNS.Listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.game.PacketPlayOutNamedSoundEffect;
import net.minecraft.sounds.SoundEffects;

public class StopSound implements Listener {

    private List < UUID > playersFishing = new ArrayList < > ();
    private List < UUID > playerThrown = new ArrayList < > ();
    private List < UUID > playerReelin = new ArrayList < > ();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        injectPlayer(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        removePlayer(e.getPlayer());
    }
    @EventHandler
    public void onFish(PlayerFishEvent e) {
        if (e.getPlayer().getWorld().getName().equalsIgnoreCase("DeathPlayerWorld")) {
            State fishst = e.getState();
            UUID PlayerUUID = e.getPlayer().getUniqueId();
            if (fishst.equals(PlayerFishEvent.State.FISHING)) {
                playersFishing.add(PlayerUUID);
                playerThrown.add(PlayerUUID);
            } else if (fishst.equals(PlayerFishEvent.State.IN_GROUND) || fishst.equals(PlayerFishEvent.State.REEL_IN)) {
                playerReelin.add(PlayerUUID);

            }

        }
    }

    private void removePlayer(Player p) {
        Channel channel = ((org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer) p).getHandle().b.a.k;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(p.getName());
            return null;
        });
    }

    private void injectPlayer(Player p) {
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

        ChannelPipeline pipeline = ((org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer) p).getHandle().b.a.k.pipeline();
        pipeline.addBefore("packet_handler", p.getName(), channelDuplexHandler);
    }

}