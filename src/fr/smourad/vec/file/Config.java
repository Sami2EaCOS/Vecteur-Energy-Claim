package fr.smourad.vec.file;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	private YamlConfiguration config;
	private File configFile;
	
	public Config(File configfile) {
		this.config = new YamlConfiguration();
		this.configFile = configfile;
	}
	
	public boolean load() {
		try {
			if (!configFile.exists()) {
				configFile.createNewFile();
				
				loadDefaults();
			}
			config.load(configFile);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private void loadDefaults() {
	
		config.addDefault("Block.start", 100);
		config.addDefault("Block.price", 2);
		
		config.options().copyDefaults(true);
		save();
	}
	
	public boolean save() {
		try {
			config.save(configFile);
		} catch (Exception e) {
			
		}
		return true;
	}
	
	public void setInt(String path, int price) {
		config.set(path, price);
		save();
	}
	
	public int getInt(String path) {
		return config.getInt(path);
	}
}
