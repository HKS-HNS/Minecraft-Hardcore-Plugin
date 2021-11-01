package me.HKS.HNS.Listeners;

import java.lang.reflect.Field;
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

public class StopSound_v1_14_v1_16 implements Listener, StopSoundInf {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        injectPlayer(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        removePlayer(e.getPlayer());
    }
    @EventHandler
    public void onSwitchSlot(PlayerItemHeldEvent e) {
        Player p = e.getPlayer();
        ItemStack Frod = new ItemStack(Material.FISHING_ROD);
        ItemMeta itemMeta = Frod.getItemMeta();
        itemMeta.setUnbreakable(true);
        Frod.setItemMeta(itemMeta);
        if (p.getWorld().getName().equalsIgnoreCase("DeathPlayerWorld")) {
            if (p.getInventory().getItem(e.getPreviousSlot()) != null)
                if (p.getInventory().getItem(e.getPreviousSlot()).equals(Frod)) {
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
        Channel channel = getChannel(p);
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(p.getName());
            return null;
        });
    }

    public void injectPlayer(Player p) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                super.channelRead(channelHandlerContext, packet);
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise promise) throws Exception {
                if (packet.getClass().isAssignableFrom(getNMSClass("PacketPlayOutNamedSoundEffect"))) {
                    Object packetSoundNamed = packet;
                    if (p.getWorld().getName().equalsIgnoreCase("DeathPlayerWorld")) {
                        try {

                            Field fila = packetSoundNamed.getClass().getDeclaredField("a");
                            fila.setAccessible(true);
                            Object a = fila.get(packetSoundNamed);
                            UUID PlayerUUID = p.getUniqueId();
                            if (a.equals(getSoundEffect("ENTITY_FISHING_BOBBER_RETRIEVE")) || a.equals(getSoundEffect("ENTITY_FISHING_BOBBER_SPLASH")) || a.equals(getSoundEffect("ENTITY_FISHING_BOBBER_THROW")) || a.equals(getSoundEffect("ENTITY_GENERIC_SPLASH"))) {

                                if (playerReelin.contains(PlayerUUID)) {
                                    playerReelin.remove(PlayerUUID);
                                    playersFishing.remove(PlayerUUID);
                                    if (!a.equals(getSoundEffect("ENTITY_FISHING_BOBBER_RETRIEVE"))) {

                                        return;
                                    }

                                } else if (playerThrown.contains(PlayerUUID)) {

                                    playerThrown.remove(PlayerUUID);
                                    if (!a.equals(getSoundEffect("ENTITY_FISHING_BOBBER_THROW"))) {
                                        return;
                                    }
                                } else if (playersFishing.contains(PlayerUUID)) {
                                    if (!a.equals(getSoundEffect("ENTITY_GENERIC_SPLASH")) && !a.equals(getSoundEffect("ENTITY_FISHING_BOBBER_SPLASH"))) {
                                        return;
                                    }
                                } else {
                                    return;
                                }

                            }
                            fila.setAccessible(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                    }

                }
                super.write(channelHandlerContext, packet, promise);
            }
        };

        ChannelPipeline pipeline = getChannel(p).pipeline();
        pipeline.addBefore("packet_handler", p.getName(), channelDuplexHandler);
    }

    public Channel getChannel(Player p) {

        try {
            Class < ? > craftplayer = getCraftBukkitClass("entity.CraftPlayer");
            Method getHandle = craftplayer.getMethod("getHandle");
            Object nms = getHandle.invoke(p);
            Object b = nms.getClass().getField("playerConnection").get(nms);
            Object a = b.getClass().getField("networkManager").get(b);
            Object chan = a.getClass().getField("channel").get(a);
            return (Channel) chan;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public Object getSoundEffect(String name) throws Exception {
        Class < ? > SoundEffects = getNMSClass("SoundEffects");
        return SoundEffects.getField(name.toUpperCase()).get(SoundEffects);
    }
    public Class < ? > getNMSClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + getServerVersion() + "." + name);
    }
    public Class < ? > getCraftBukkitClass(String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + getServerVersion() + "." + name);
    }
    public String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    }

}