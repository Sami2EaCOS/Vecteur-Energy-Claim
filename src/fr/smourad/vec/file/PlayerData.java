package fr.smourad.vec.file;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.smourad.vec.Claim;

public class PlayerData {

	public YamlConfiguration Playerdata;
	public File Playerdatafile;
	private String playername;
	
	public PlayerData(File Playerdatafile, String playername) {
		this.Playerdata = new YamlConfiguration();
		this.Playerdatafile = Playerdatafile;
		this.playername = playername;
	}
	
	public boolean load() {
		try {
			if (!Playerdatafile.exists()) {
				Playerdatafile.createNewFile();
				loadDefaults();
			}
			Playerdata.load(Playerdatafile);
			return true;
		} catch (Exception e) {
			
			return false;
		}
	}
	
	public boolean save() {
		try {
			Playerdata.save(Playerdatafile);
		} catch (Exception e) {
			
		}
		return true;
	}
	
	private void loadDefaults() {
		Playerdata.addDefault("Player.name", playername);
		Playerdata.addDefault("Player.blocks", Claim.config.getInt("Block.start"));
		
		Playerdata.options().copyDefaults(true);
		save();
	}
	
	public void setString(String path, String nick) {
		Playerdata.set(path, nick);
		save();
	}
	
	
	public String getString(String path) {
		return Playerdata.getString(path);
	}	
	
	public void setInt(String path, int stats) {
		Playerdata.set(path, stats);
		save();
	}
	
	public int getInt(String path) {
		return Playerdata.getInt(path);
	}
}
