package me.HKS.HNS;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/***
 * 
 * Plugin updater
 * 
 * @author HKS-HNS
 * 
 */
public class Updater {

    public Updater(PluginDescriptionFile pdf, File file) {
        String a = pdf.getVersion();
        try {
            URL jurl = new URL("https://hkshns.000webhostapp.com/Version.json");
            URLConnection request = jurl.openConnection();
            request.connect(); // Connect to the Website

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); // Read Json Stream
            JsonObject rootobj = root.getAsJsonObject();

            if (rootobj.get("Version").getAsFloat() > Float.valueOf(a)) { // Tests if it is the latest version
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                URL Downurl = new URL(rootobj.get("URL").getAsString()); // Download's the jar
                OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                URLConnection conn = Downurl.openConnection();
                InputStream in = conn.getInputStream();
                byte[] buffer = new byte[1024];
                int numRead;
                while ((numRead = in .read(buffer)) != -1) {
                    out.write(buffer, 0, numRead);
                }
                if ( in != null) {
                    in .close();
                }
                if (out != null) {
                    out.close();
                }
            } else { // If it is Up-to-date
                Bukkit.getLogger().info("The Plugin " + pdf.getName() + " is Up-to-date");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}