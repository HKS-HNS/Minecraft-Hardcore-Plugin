package me.HKS.HNS.World;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.libs.org.codehaus.plexus.util.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.HKS.HNS.Main;

/***
 * 
 * To Create the Player for each Player
 * 
 * @author HKS-HNS
 * 
 */
public class Create {
    public static HashMap < UUID, Location > Respawn = new HashMap < UUID, Location > ();

    public static void CreateWorld(Player p) {
        String DeathWorld = "DeathPlayerWorld";
        if (!Bukkit.getServer().getWorlds().contains(Bukkit.getWorld(DeathWorld)) || !Bukkit.getWorld(DeathWorld).getWorldFolder().exists()) {
            //Creates the World if not exists
            World world = Bukkit.createWorld(new WorldCreator(DeathWorld).type(WorldType.FLAT).generateStructures(false));
            world.setPVP(false);
            world.setKeepSpawnInMemory(false);
            world.setDifficulty(Difficulty.PEACEFUL);
            world.setGameRule(GameRule.FREEZE_DAMAGE, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
            world.setGameRule(GameRule.DISABLE_RAIDS, false);
            world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
            world.setGameRule(GameRule.SPAWN_RADIUS, 5);
            world.getWorldBorder().setSize(15);
            Bukkit.getServer().getWorlds().add(world);
            Location spawn = world.getSpawnLocation();
            spawn.setY(1);

            for (Player pl: world.getPlayers()) {

                if (!pl.equals(p)) {
                    pl.hidePlayer(Main.getInstance(), p);
                    p.hidePlayer(Main.getInstance(), pl);
                }
            }
            Respawn.put(p.getUniqueId(), spawn);
            int x;
            for (x = spawn.getBlockX() - 7; x <= spawn.getBlockX() + 7; x++) {
                for (int y = spawn.getBlockY(); y <= spawn.getBlockY() + 10; y++) {
                    for (int z = spawn.getBlockZ() - 7; z <= spawn.getBlockZ() + 7; z++) {
                        world.getBlockAt(x, y, z).setType(Material.BEDROCK);
                    }
                }
            }

            for (x = spawn.getBlockX() - 6; x <= spawn.getBlockX() + 6; x++) {
                for (int y = spawn.getBlockY() + 2; y <= spawn.getBlockY() + 10; y++) {
                    for (int z = spawn.getBlockZ() - 6; z <= spawn.getBlockZ() + 6; z++) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                    }
                }
            }

            for (x = spawn.getBlockX() - 7; x <= spawn.getBlockX() + 7; x++) {
                for (int y = spawn.getBlockY() + 10; y <= spawn.getBlockY() + 10; y++) {
                    for (int z = spawn.getBlockZ() - 7; z <= spawn.getBlockZ() + 7; z++) {
                        world.getBlockAt(x, y, z).setType(Material.BARRIER);
                    }
                }
            }

            spawn.add(0, 1, 0);
            Location chestLocation = new Location(world, spawn.getBlockX(), spawn.getBlockY() + 1, spawn.getBlockZ() - 2);
            chestLocation.getBlock().setType(Material.CHEST);
            if (chestLocation.getBlock().getState() instanceof Chest) {
                Chest chest = (Chest) chestLocation.getBlock().getState();
                Directional d = (Directional) chestLocation.getBlock().getBlockData();
                d.setFacing(BlockFace.SOUTH);
                chest.setBlockData(d);
                Inventory inv = chest.getBlockInventory();
                ItemStack Frod = new ItemStack(Material.FISHING_ROD);
                ItemMeta itemMeta = Frod.getItemMeta();
                itemMeta.setUnbreakable(true);
                Frod.setItemMeta(itemMeta);
                chest.getInventory().clear();
                for (int i = 0; i < inv.getSize(); i++) {
                    inv.setItem(i, Frod);
                }

            }
            for (x = spawn.getBlockX() - 1; x <= spawn.getBlockX() + 1; x++) {
                for (int y = spawn.getBlockY(); y <= spawn.getBlockY(); y++) {
                    for (int z = spawn.getBlockZ() + 1; z <= spawn.getBlockZ() + 3; z++) {
                        world.getBlockAt(x, y, z).setType(Material.WATER);
                    }
                }
            }

            p.setGameMode(GameMode.ADVENTURE);
            p.setExp(0);
            p.setLevel(0);

        }
        World world = Bukkit.getWorld(DeathWorld);
        Location spawn = world.getSpawnLocation();
        spawn.setY(2);
        Location chestLocation = new Location(world, spawn.getBlockX(), spawn.getBlockY() + 1, spawn.getBlockZ() - 2);
        chestLocation.getBlock().setType(Material.CHEST);
        if (chestLocation.getBlock().getState() instanceof Chest) {
            Chest chest = (Chest) chestLocation.getBlock().getState();
            Directional d = (Directional) chestLocation.getBlock().getBlockData();
            d.setFacing(BlockFace.SOUTH);
            chest.setBlockData(d);
            Inventory inv = chest.getBlockInventory();
            ItemStack Frod = new ItemStack(Material.FISHING_ROD);
            ItemMeta itemMeta = Frod.getItemMeta();
            itemMeta.setUnbreakable(true);
            Frod.setItemMeta(itemMeta);
            chest.getInventory().clear();
            chest.update();
            for (int i = 0; i < inv.getSize(); i++) {
                inv.setItem(i, Frod);
            }
            spawn.setY(2);
            world.setPVP(false);
            world.setKeepSpawnInMemory(false);
            world.setDifficulty(Difficulty.PEACEFUL);
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
            world.setGameRule(GameRule.FREEZE_DAMAGE, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
            world.setGameRule(GameRule.DISABLE_RAIDS, false);
            world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
            world.setGameRule(GameRule.SPAWN_RADIUS, 5);
            int x;
            for (x = spawn.getBlockX() - 1; x <= spawn.getBlockX() + 1; x++) {
                for (int y = spawn.getBlockY(); y <= spawn.getBlockY(); y++) {
                    for (int z = spawn.getBlockZ() + 1; z <= spawn.getBlockZ() + 3; z++) {
                        world.getBlockAt(x, y, z).setType(Material.WATER);
                    }
                }
            }
        }

        for (Player pl: world.getPlayers()) {

            if (!pl.equals(p)) {
                pl.hidePlayer(Main.getInstance(), p);
                p.hidePlayer(Main.getInstance(), pl);

            }
        }
        p.teleport(spawn.add(0, 1, 0));
        p.dropItem(false);
    }

    public static void DelWorld(Player p) {
        String DeathWorld = "DeathPlayerWorld";
        if (Bukkit.getWorld(DeathWorld) == null)
            return;
        if (Bukkit.getWorld(DeathWorld).getPlayers().size() >= 1)
            return;
        World delete = Bukkit.getWorld(DeathWorld);

        delete.getWorldFolder().delete();
        File deleteFolder = delete.getWorldFolder();

        Bukkit.unloadWorld(delete, false);
        Bukkit.getServer().getWorlds().remove(delete);
        try {
            FileUtils.deleteDirectory(deleteFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}