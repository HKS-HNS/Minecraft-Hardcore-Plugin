package me.HKS.HNS.Listeners;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import me.HKS.HNS.Main;
import me.HKS.HNS.World.Create;

/***
 * 
 * The Hart of the Hardcore plugin
 * 
 * @author HKS-HNS
 * 
 */
public class Config implements Listener, CommandExecutor, TabCompleter {
    public File ConfigFile = new File("plugins/Hardcore", "config.yml");
    public FileConfiguration Config = YamlConfiguration.loadConfiguration(ConfigFile);

    int DefaultDeathCount = 5;
    int experienceLevels = 5;
    int DefaultFishCount = 10;
    Boolean AntiHack = false;
    public Config() {
        config();
    }

    void config() { // Refresh's the Config
        ConfigFile = new File("plugins/Hardcore", "config.yml");
        Config = YamlConfiguration.loadConfiguration(this.ConfigFile);
        if (!ConfigFile.exists()) {
            Config.set("Death.Count", Integer.valueOf(DefaultDeathCount));
            Config.set("Fish.Count", Integer.valueOf(DefaultFishCount));
            Config.set("Buy.Experiencelevels", Integer.valueOf(experienceLevels));
            Config.set("AntiHack.Range", AntiHack);
            Config.set("players.1e43497a-ce3e-4381-8850-8410a676c847.Deaths", Integer.valueOf(0));
            Config.set("players.1e43497a-ce3e-4381-8850-8410a676c847.MaxDeaths", Integer.valueOf(DefaultDeathCount));
        }
        DefaultDeathCount = Config.getInt("Death.Count");
        DefaultFishCount = Config.getInt("Fish.Count");
        experienceLevels = Config.getInt("Buy.Experiencelevels");
        AntiHack = Config.getBoolean("AntiHack.Range");
        try {
            Config.save(ConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        config();
        Player p = e.getPlayer();
        UUID PlayerUUID = p.getUniqueId();
        createPlayer(PlayerUUID);
        int deaths = Config.getInt("players." + PlayerUUID + ".Deaths");
        int Maxdeaths = Config.getInt("players." + PlayerUUID + ".MaxDeaths");
        if (deaths >= Maxdeaths) { // Set's the player to adventure mode if he dies too many times and sends him to another world
            Create.CreateWorld(e.getPlayer());
        } else if (p.getGameMode().equals(GameMode.ADVENTURE)) {

            p.setGameMode(GameMode.SURVIVAL);
        }
    }
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {

        if (e.getDamager() instanceof Player && e.getCause().equals(DamageCause.ENTITY_ATTACK) && AntiHack.equals(true)) {
            Entity whoWasHit = e.getEntity();
            Player Hitter = (Player) e.getDamager();

            double distance = Hitter.getLocation().distance(whoWasHit.getLocation());
            if (distance > 5) { // If a player hits from a range that is higher than 5 blocks of distance, he would be banned
                Bukkit.getBanList(BanList.Type.NAME).addBan(Hitter.getName(), "§4You had a range of §4§l" + distance, null, null);
                Hitter.kickPlayer("§4You had a range of §4§l" + distance);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!(e.getEntity() instanceof Player)) { // If the entity is a player, it counts the player deaths
            return;
        }

        UUID PlayerUUID = e.getEntity().getUniqueId();
        config();
        createPlayer(PlayerUUID);
        int deaths = Config.getInt("players." + PlayerUUID + ".Deaths") + 1;
        int Maxdeaths = Config.getInt("players." + PlayerUUID + ".MaxDeaths");
        if (deaths >= Maxdeaths) {}
        Config.set("players." + PlayerUUID + ".Deaths", Integer.valueOf(deaths));
        try {
            Config.save(ConfigFile);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        config();
        UUID PlayerUUID = e.getPlayer().getUniqueId();
        int deaths = Config.getInt("players." + PlayerUUID + ".Deaths");
        int Maxdeaths = Config.getInt("players." + PlayerUUID + ".MaxDeaths");
        if (deaths >= Maxdeaths) { // If a player reaches the limit of deaths, he must go fishing 
            Player p = e.getPlayer();
            Create.CreateWorld(p);
            String DeathWorld = "DeathPlayerWorld";
            e.getPlayer().getInventory().clear();
            if (Bukkit.getWorld(DeathWorld) != null) {
                Location spawn = Bukkit.getWorld(DeathWorld).getSpawnLocation();
                spawn.setY(3);
                e.setRespawnLocation(spawn);
            }
            p.setGameMode(GameMode.ADVENTURE);
            p.setExp(0);
            p.setLevel(0);
        }
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent e) {
        UUID PlayerUUID = e.getPlayer().getUniqueId();
        if (e.getAdvancement().getKey().getKey().toLowerCase().contains("adventure/kill_a_mob")) { // Add's a boring Advancement
            if (Config.get("players." + PlayerUUID + ".MaxDeaths") == null || Config.get("players." + PlayerUUID + ".Deaths") == null) {
                return;
            }
            int deaths = Config.getInt("players." + PlayerUUID + ".Deaths");
            int Maxdeaths = Config.getInt("players." + PlayerUUID + ".MaxDeaths");
            e.getPlayer().sendMessage("§4Be carefully because you have only§a " + (Maxdeaths - deaths) + "§4 Deaths Left");

        }

    }

    @EventHandler
    public void onFish(PlayerFishEvent e) {
        String DeathWorld = "DeathPlayerWorld";
        Player p = e.getPlayer();
        if (Bukkit.getWorld(DeathWorld) != null)
            if (p.getWorld().equals(Bukkit.getWorld(DeathWorld))) { // Replaces treasure from fishing when he is not in the normal world
                e.setExpToDrop(0);
                Set < Material > Fishs = new HashSet < Material > ();
                Fishs.add(Material.COD);
                Fishs.add(Material.TROPICAL_FISH);
                Fishs.add(Material.SALMON);
                Fishs.add(Material.PUFFERFISH);
                if (e.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
                    e.setCancelled(true);
                    if (Fishs.contains(((Item) e.getCaught()).getItemStack().getType())) {
                        p.getInventory().addItem(((Item) e.getCaught()).getItemStack());
                    } else {
                        p.getInventory().addItem(new ItemStack[] {
                            new ItemStack(Material.COD)
                        });
                    }
                }

            }
    }

    @EventHandler
    public void OnChestOpen(PlayerInteractEvent e) { // Creates a Chest for the Fishing
        if (e.hasBlock()) {
            if (e.getClickedBlock().getType() == Material.CHEST) {
                String DeathWorld = "DeathPlayerWorld";
                if (Bukkit.getWorld(DeathWorld) != null)
                    if (e.getClickedBlock().getWorld().equals(Bukkit.getWorld(DeathWorld))) {
                        Inventory inv = Bukkit.createInventory(null, 27);
                        ItemStack Frod = new ItemStack(Material.FISHING_ROD);
                        ItemMeta itemMeta = Frod.getItemMeta();
                        itemMeta.setUnbreakable(true);
                        Frod.setItemMeta(itemMeta);
                        for (int i = 0; i < inv.getSize(); i++) {
                            inv.setItem(i, Frod);
                        }
                        e.setCancelled(true);
                        e.getPlayer().openInventory(inv);
                    }
            }
        }
    }

    @EventHandler
    public void OnDropEvent(PlayerDropItemEvent e) {
        String DeathWorld = "DeathPlayerWorld";
        if (Bukkit.getWorld(DeathWorld) != null)
            if (e.getPlayer().getWorld().equals(Bukkit.getWorld(DeathWorld))) {

                ItemStack Frod = new ItemStack(Material.FISHING_ROD);
                ItemMeta itemMeta = Frod.getItemMeta();
                itemMeta.setUnbreakable(true);
                Frod.setItemMeta(itemMeta);
                if (e.getItemDrop().getItemStack().equals(Frod)) {
                    e.getItemDrop().remove();
                } else {
                    e.setCancelled(true);
                }
            }
    }

    @Override
    public List < String > onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) { // Add's AutoTabComplete
            Player p = (Player) sender;
            List < String > commands = new ArrayList < > ();
            if (p.getWorld().getName().equalsIgnoreCase("DeathPlayerWorld")) {
                commands.add("BuyFree");
            } else {
                commands.add("Buy");
            }

            commands.add("Deathsleft");
            return commands;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("buy")) {
            List < String > commands = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9");
            return commands;
        }
        return null;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("Hardcore")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§4You aren't a Player");
                return true;
            } else if (args.length < 1) {
                return true;
            }
            Player p = (Player) sender;
            UUID PlayerUUID = p.getUniqueId();
            config();
            if (Config.get("players." + PlayerUUID + ".MaxDeaths") == null || Config.get("players." + PlayerUUID + ".Deaths") == null) {
                commandsend(p, "Somthing bad happened pleas rejoin. ⅽ"); // Say's the player he must rejoin if he isn't saved in the config
                return true;
            }
            int deaths = Config.getInt("players." + PlayerUUID + ".Deaths");
            int Maxdeaths = Config.getInt("players." + PlayerUUID + ".MaxDeaths");

            if (args[0].equalsIgnoreCase("buy") && deaths < Maxdeaths) { // Add's lives if the player buys them
                if (args.length >= 2 && isNumeric(args[1])) {
                    if (p.getLevel() >= (experienceLevels * Integer.valueOf(args[1])) && Integer.valueOf(args[1]) >= 1) {
                        Config.set("players." + PlayerUUID + ".MaxDeaths", (Maxdeaths + Integer.valueOf(args[1])));
                        p.setLevel(p.getLevel() - (experienceLevels * Integer.valueOf(args[1])));
                        try {
                            Config.save(ConfigFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    } else {
                        commandsend(p, "§7You dont have enough Exp Your levels: §a" + p.getLevel() + "§7 and §a" + (experienceLevels * Integer.valueOf(args[1])) + "§7 Levels are requiret.");
                    }
                } else {
                    commandsend(p, "§7Hardcore buy <count>  |  How many levels you want to buy", "§7One Live costs 5xp levels");

                    // sender.sendMessage("§7Hardcore buy <count>  |  How many levels you want to buy");
                }
            } else if (args[0].equalsIgnoreCase("Deathsleft")) { // Sends the lives to the player if the command is executed
                if (Maxdeaths > deaths) {
                    commandsend(p, "§7 You have " + String.valueOf(Maxdeaths - deaths) + " Deaths Left");

                } else {
                    commandsend(p, "§7 Sorry, you have 0 Lives Left and you can't buy it anymore :(");

                }

            } else if (args[0].equalsIgnoreCase("BuyFree") && deaths >= Maxdeaths) { // Sends the player to the normal world if he has enough fishes fished
                config();
                int Fishcount = Config.getInt("Fish.Count");

                int cod = (getAmount(p, Material.COD) + getAmount(p, Material.TROPICAL_FISH) + getAmount(p, Material.SALMON) + getAmount(p, Material.PUFFERFISH));

                if (Fishcount <= cod) {
                    p.setGameMode(GameMode.SURVIVAL);
                    p.setExp(0);
                    p.setLevel(0);
                    if (Bukkit.getWorld("DeathPlayerWorld") != null)
                        for (Player pl: Bukkit.getWorld("DeathPlayerWorld").getPlayers()) {

                            if (!pl.equals(p)) {
                                pl.showPlayer(Main.getInstance(), p);
                                p.showPlayer(Main.getInstance(), pl);
                            }
                        }
                    if (p.getBedSpawnLocation() != null) {
                        p.teleport(p.getBedSpawnLocation());

                    } else {

                        World MainWorld = Bukkit.getServer().getWorlds().get(0);
                        int SRange = MainWorld.getGameRuleValue(GameRule.SPAWN_RADIUS);
                        Random random = new Random();
                        Location randomLoc = MainWorld.getSpawnLocation();

                        try {
                            if (random.nextBoolean()) {
                                randomLoc.setX(MainWorld.getSpawnLocation().getBlockX() + random.nextInt(SRange));
                                randomLoc.setZ(MainWorld.getSpawnLocation().getBlockZ() + random.nextInt(SRange));
                            } else {
                                randomLoc.setX(MainWorld.getSpawnLocation().getBlockX() - random.nextInt(SRange));
                                randomLoc.setZ(MainWorld.getSpawnLocation().getBlockZ() - random.nextInt(SRange));
                            }
                            randomLoc.setWorld(MainWorld);
                            randomLoc.setY(MainWorld.getHighestBlockYAt(randomLoc));
                        } catch (Exception e) {

                        }

                        p.teleport(randomLoc);

                    }
                    p.getInventory().clear();
                    Config.set("players." + PlayerUUID + ".Deaths", Integer.valueOf(0));
                    Config.set("players." + PlayerUUID + ".MaxDeaths", Integer.valueOf(1));
                    try {
                        Config.save(ConfigFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Create.DelWorld(p);
                } else {

                    commandsend(p, "§7 to buy you Free you must pay with " + Fishcount + " fishs",
                        "§7 You only have " + cod + " fishs fished you must have " + (Fishcount - cod) + " more.");
                }
            }
        }

        return false;
    }

    public void createPlayer(UUID PlayerUUID) { // creates a new player if he dont exists in the config
        if (Config.get("players." + PlayerUUID + ".MaxDeaths") == null) {
            Config.set("players." + PlayerUUID + ".MaxDeaths", Integer.valueOf(DefaultDeathCount));
        }
        if (Config.get("players." + PlayerUUID + ".Deaths") == null) {
            Config.set("players." + PlayerUUID + ".Deaths", Integer.valueOf(0));
        }
        try {
            Config.save(ConfigFile);
        } catch (IOException e1) {
            e1.printStackTrace();

        }
    }
    public static int getAmount(Player player, Material Material) { // Count's how many items of a specific type is in the inventory from the player
        PlayerInventory inventory = player.getInventory();
        ItemStack[] items = inventory.getContents();
        int has = 0;
        for (ItemStack item: items) {
            if ((item != null) && (item.getType() == Material) && (item.getAmount() > 0)) {
                has += item.getAmount();
            }
        }
        return has;
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {

            int test = Integer.parseInt(strNum);
            if (test <= 0) {
                return false;
            }

        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public void commandsend(Player p, String...Message) {
        p.sendMessage("§1|---------------------§4§lHardcore§1---------------------|");
        p.sendMessage(Message);
        p.sendMessage("§1|---------------------§4§lHardcore§1---------------------|");
    }

}