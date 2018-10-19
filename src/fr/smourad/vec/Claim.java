package fr.smourad.vec;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import fr.smourad.vec.commands.AdminZone;
import fr.smourad.vec.commands.Zone;
import fr.smourad.vec.commaner.InventoryPrice;
import fr.smourad.vec.file.Config;
import fr.smourad.vec.file.PlayerData;
import fr.smourad.vec.hashmap.LocationEmergency;
import fr.smourad.vec.listener.BlockExplode;
import fr.smourad.vec.listener.PlayerInteract;
import fr.smourad.vec.listener.PlayerItemHeld;
import net.milkbowl.vault.economy.Economy;

public class Claim extends JavaPlugin {
	
	public static Config config = null;
	public static HashMap<Player, PlayerData> playerDataMap = new HashMap<Player, PlayerData>();
	public static Economy economy = null;
	
	
	public static HashMap<Player, LocationEmergency> selectionHashmap = new HashMap<Player, LocationEmergency>(); 
	
	@Override
	public void onEnable() {
		loadConfig();
		loadCommands();
		loadListeners();
		loadCommaner();
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public void loadCommands() {
		this.getCommand("zone").setExecutor(new Zone(this));
		this.getCommand("adminzone").setExecutor(new AdminZone(this));
	}
	
	public void loadListeners() {
		this.getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerItemHeld(), this);
		this.getServer().getPluginManager().registerEvents(new BlockExplode(this), this);
	}
	
	public void loadCommaner() {
		InventoryPrice ip = new InventoryPrice(this);
		
		this.getCommand("blocks").setExecutor(ip);
		this.getServer().getPluginManager().registerEvents(ip, this);
	}
	
	public WorldGuardPlugin getWorldGuard() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	 
	    // WorldGuard may not be loaded
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
	        return null; // Maybe you want throw an exception instead
	    }
	 
	    return (WorldGuardPlugin) plugin;
	}
	
	public WorldEditPlugin getWorldEdit() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");
	 
	    // WorldGuard may not be loaded
	    if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
	        return null; // Maybe you want throw an exception instead
	    }
	 
	    return (WorldEditPlugin) plugin;
	}
	
	public void loadConfig() {
		File dir = this.getDataFolder();
		if(!dir.exists()) dir.mkdir();
		
		File file = new File(dir, "config.yml");
		config = new Config(file);
		
		if (!config.load()) {
			this.getServer().getPluginManager().disablePlugin(this);
			throw new IllegalStateException("The config was not loaded correctly!");
		}
	}
	
	public void playerDataLoad(String playername, Player p) {
		File dir = new File(Bukkit.getPluginManager().getPlugin("VecteurEnergy_Claim").getDataFolder() + "/" + "Playerdata");
		if (!dir.exists()) dir.mkdir();
		
		File file = new File(dir, playername + ".yml");
		PlayerData playerdata = new PlayerData(file, playername);
		
		if (!playerdata.load()) {
			throw new IllegalStateException("The player data file for player " + playername + " was not loaded correctly.");
		} else {			
			playerDataMap.put(p, playerdata);
		}
	}
	
	public boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
		
		return (economy != null);
	}
}
