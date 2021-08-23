package me.HKS.HNS.Screen;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import me.HKS.HNS.Listeners.Config;

public class Actionbar {
    private ScheduledExecutorService exectorService = Executors.newScheduledThreadPool(0);
    public File ConfigFile = new File("plugins/Hardcore", "config.yml");
    public FileConfiguration Confi = YamlConfiguration.loadConfiguration(ConfigFile);

    int DefaultDeathCount = 5;
    int DefaultFishCount = 10;

    public void Start() {
        exectorService.scheduleAtFixedRate(() -> {
            config();
            for (Player p: Bukkit.getOnlinePlayers()) {
                UUID PlayerUUID = p.getUniqueId();
                if (Confi.get("players." + PlayerUUID + ".MaxDeaths") == null || Confi.get("players." + PlayerUUID + ".Deaths") == null) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§4§lPleas Rejoin Tanks Somthing bad happened. ⅽ"));
                } else {
                    //TODO: Machen das man wenn man genug fischhe hat richtig angezeigt wird
                    int deaths = Confi.getInt("players." + PlayerUUID + ".Deaths");
                    int Maxdeaths = Confi.getInt("players." + PlayerUUID + ".MaxDeaths");
                    if (deaths >= Maxdeaths) {
                        int cod = (Config.getAmount(p, Material.COD) + Config.getAmount(p, Material.TROPICAL_FISH) + Config.getAmount(p, Material.SALMON) + Config.getAmount(p, Material.PUFFERFISH));
                        int Fishs = Confi.getInt("Fish.Count");

                        if (cod < Fishs) {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7You have only §4" + cod + "§7 fishes fished From §a" + Fishs));

                        } else {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7You have enough fishes fished §a" + cod + "§7/§a" + Fishs + "§7 pleas Type §b§n/hardcore BuyFree"));

                        }
                    } else {
                        if ((Maxdeaths - deaths) <= 1) {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7You have only §4" + (Maxdeaths - deaths) + "§7 Lives Left"));

                        } else if ((Maxdeaths - deaths) <= 3) {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7You have only §c" + (Maxdeaths - deaths) + "§7 Lives Left"));

                        } else if ((Maxdeaths - deaths) > 3) {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7You have §a" + (Maxdeaths - deaths) + "§7 Lives Left"));

                        }

                    }

                }
            }

        }, 0, 700, TimeUnit.MILLISECONDS);
    }

    public void Stoptimer() {
        exectorService.shutdownNow();
    }

    public void Deltimer() {
        exectorService.shutdownNow();
        sendActionBar("");
    }

    private void config() {
        ConfigFile = new File("plugins/Hardcore", "config.yml");
        Confi = YamlConfiguration.loadConfiguration(this.ConfigFile);
        if (ConfigFile.exists()) {
            DefaultDeathCount = Confi.getInt("Death.Count");
            DefaultFishCount = Confi.getInt("Fish.Count");
            try {
                Confi.save(ConfigFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendActionBar(String Message) {
        for (Player p: Bukkit.getOnlinePlayers()) {

            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Message));

        }
    }
}