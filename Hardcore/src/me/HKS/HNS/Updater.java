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

public class Updater {


    public Updater(PluginDescriptionFile pdf, File file) {
    	String a = pdf.getVersion();
    	try { 
    		 URL jurl = new URL("https://hkshns.000webhostapp.com/Version.json");
    	        URLConnection request = jurl.openConnection();
    	        request.connect();

    	        JsonParser jp = new JsonParser(); 
    	        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); 
    	        JsonObject rootobj = root.getAsJsonObject(); //May be
    	        
    	        if(rootobj.get("Version").getAsFloat() > Float.valueOf(a)) {
    	        	if(file.exists()) {
    	        		file.delete();
    	        	}
    	        	file.createNewFile();
    	            URL Downurl = new URL(rootobj.get("URL").getAsString());
    	            OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
    	            URLConnection conn = Downurl.openConnection();
    	            InputStream in = conn.getInputStream();
    	            byte[] buffer = new byte[1024];
    	            int numRead;
    	            while ((numRead = in.read(buffer)) != -1) {
    	                out.write(buffer, 0, numRead);
    	            }
    	            if (in != null) {
    	                in.close();
    	            }
    	            if (out != null) {
    	                out.close();
    	            }
    	        }else {
    	        	Bukkit.getLogger().info("The Plugin "+pdf.getName()+ " is Uptodate");

    	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
       
    }

}
 