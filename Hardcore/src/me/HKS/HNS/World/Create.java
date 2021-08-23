package me.HKS.HNS.World;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.libs.org.codehaus.plexus.util.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Create {

    public static void CreateWorld(Player p) {
        String worlds = p.getUniqueId().toString();
        if (!Bukkit.getServer().getWorlds().contains(Bukkit.getWorld(worlds)) || Bukkit.getWorld(worlds).getWorldFolder().exists()) {
            World world = Bukkit.createWorld(new WorldCreator(worlds).type(WorldType.FLAT).generateStructures(false));
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
            world.setGameRule(GameRule.SPAWN_RADIUS, 5);
            world.getWorldBorder().setSize(50);
            Bukkit.getServer().getWorlds().add(world);
            Location spawn = world.getSpawnLocation();
            Location chestLocation = new Location(world, spawn.getBlockX(), world.getHighestBlockYAt(spawn) + 1, spawn.getBlockZ() - 2);
            chestLocation.getBlock().setType(Material.CHEST);
            if (chestLocation.getBlock().getState() instanceof Chest) {
                Chest chest = (Chest) chestLocation.getBlock().getState();
                Inventory inv = chest.getBlockInventory();
                ItemStack Frod = new ItemStack(Material.FISHING_ROD);
                ItemMeta itemMeta = Frod.getItemMeta();
                itemMeta.setUnbreakable(true);
                Frod.setItemMeta(itemMeta);
                chest.getInventory().clear();
                for (int i = 0; i < inv.getSize(); i++) {
                    inv.setItem(i, Frod);
                }
                world.getBlockAt(spawn.getBlockX(), world.getHighestBlockYAt(spawn), spawn.getBlockZ() + 1).setType(Material.WATER);
                world.getBlockAt(spawn.getBlockX() + 1, world.getHighestBlockYAt(spawn), spawn.getBlockZ() + 1).setType(Material.WATER);
                world.getBlockAt(spawn.getBlockX(), world.getHighestBlockYAt(spawn), spawn.getBlockZ() + 2).setType(Material.WATER);
                world.getBlockAt(spawn.getBlockX(), world.getHighestBlockYAt(spawn), spawn.getBlockZ() + 3).setType(Material.WATER);
                world.getBlockAt(spawn.getBlockX() + 1, world.getHighestBlockYAt(spawn), spawn.getBlockZ() + 2).setType(Material.WATER);
                world.getBlockAt(spawn.getBlockX() + 1, world.getHighestBlockYAt(spawn), spawn.getBlockZ() + 3).setType(Material.WATER);
                world.getBlockAt(spawn.getBlockX() - 1, world.getHighestBlockYAt(spawn), spawn.getBlockZ() + 1).setType(Material.WATER);
                world.getBlockAt(spawn.getBlockX() - 1, world.getHighestBlockYAt(spawn), spawn.getBlockZ() + 2).setType(Material.WATER);
                world.getBlockAt(spawn.getBlockX() - 1, world.getHighestBlockYAt(spawn), spawn.getBlockZ() + 3).setType(Material.WATER);

            }
            p.teleport(spawn);
            p.setGameMode(GameMode.ADVENTURE);
            p.setExp(0);
            p.setLevel(0);

        } else {

            World world = Bukkit.getWorld(worlds);
            Location spawn = world.getSpawnLocation();
            Location chestLocation = new Location(world, spawn.getBlockX(), world.getHighestBlockYAt(spawn) + 1, spawn.getBlockZ() - 2);
            chestLocation.getBlock().setType(Material.CHEST);
            if (chestLocation.getBlock().getState() instanceof Chest) {
                Chest chest = (Chest) chestLocation.getBlock().getState();
                Inventory inv = chest.getBlockInventory();
                ItemStack Frod = new ItemStack(Material.FISHING_ROD);
                ItemMeta itemMeta = Frod.getItemMeta();
                itemMeta.setUnbreakable(true);
                Frod.setItemMeta(itemMeta);
                chest.getInventory().clear();
                for (int i = 0; i < inv.getSize(); i++) {
                    inv.setItem(i, Frod);
                }
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
                world.setGameRule(GameRule.SPAWN_RADIUS, 5);
                p.teleport(spawn);
            }
        }
    }

    public static void DelWorld(Player p) {
        World delete = Bukkit.getWorld(p.getUniqueId().toString());
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