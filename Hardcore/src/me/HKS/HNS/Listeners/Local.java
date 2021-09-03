package me.HKS.HNS.Listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/***
 * 
 * My first version of the plugin, but without config and autocomplete.
 * 
 * @author HKS-HNS
 * 
 * @deprecated
 * 
 */
public class Local implements Listener, CommandExecutor, TabCompleter {
    static HashMap < UUID, Integer > Deathcount = new HashMap < UUID, Integer > ();
    static HashMap < UUID, Integer > MaxDeath = new HashMap < UUID, Integer > ();
    int DefaultDeath = 5;

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent e) {
        UUID PlayerUUID = e.getPlayer().getUniqueId();
        if (e.getAdvancement().getKey().getKey().toLowerCase().contains("adventure/kill_a_mob")) {
            if (Deathcount.get(PlayerUUID) == null && MaxDeath.get(PlayerUUID) == null) {
                return;
            }
            e.getPlayer().sendMessage("§4Be carefully because you have only§a " + String.valueOf(MaxDeath.get(PlayerUUID) - Deathcount.get(PlayerUUID)) + "§4 Deaths Left");

        }

    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        CreatePlayerDeath(e.getPlayer().getUniqueId());
        if (Deathcount.get(e.getPlayer().getUniqueId()) >= MaxDeath.get(e.getPlayer().getUniqueId()) &&
            !e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
        }

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
            if (args[0].equalsIgnoreCase("buy")) {
                if (args.length >= 2 && isNumeric(args[1])) {
                    if (Deathcount.get(PlayerUUID) == null && MaxDeath.get(PlayerUUID) == null) {
                        commandsend(p, "Somthing bad happens pleas rejoin. ⅽ");
                        return true;
                    }
                    if (p.getLevel() >= (5 * Integer.valueOf(args[1]))) {
                        MaxDeath.put(PlayerUUID, MaxDeath.get(PlayerUUID) + Integer.valueOf(args[1]));
                        p.setLevel(p.getLevel() - (5 * Integer.valueOf(args[1])));
                        return true;
                    } else {
                        commandsend(p, "§7You dont have enough Exp Your levels: §a" + p.getLevel() + "§7 and §a" + (5 * Integer.valueOf(args[1])) + "§7 Levels are requiret.");
                    }
                } else {
                    commandsend(p, "§7Hardcore buy <count>  |  How many levels you want to buy", "§7One Death costs 5xp levels");

                    // sender.sendMessage("§7Hardcore buy <count>  |  How many levels you want to buy");
                }
            } else if (args[0].equalsIgnoreCase("Deathsleft")) {
                if (MaxDeath.get(PlayerUUID) != null && MaxDeath.get(PlayerUUID) != null) {
                    commandsend(p, "§7 You have " + String.valueOf(MaxDeath.get(PlayerUUID) - Deathcount.get(PlayerUUID)) + " Deaths Left");

                } else {
                    commandsend(p, "Somthing bad happens pleas rejoin. ⅽ");
                }
            }
        }

        return false;
    }

    @Override
    public List < String > onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List < String > commands = new ArrayList < > ();
            commands.add("Buy");
            commands.add("Deathsleft");
            return commands;
        } else if (args.length == 2) {
            List < String > commands = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9");
            return commands;
        }
        return null;
    }

    public void CreatePlayerDeath(UUID PlayerUUID) {
        if (MaxDeath.get(PlayerUUID) == null) {
            MaxDeath.put(PlayerUUID, DefaultDeath);

        }
        if (Deathcount.get(PlayerUUID) == null) {
            Deathcount.put(PlayerUUID, 0);

        }
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Integer.parseInt(strNum);

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