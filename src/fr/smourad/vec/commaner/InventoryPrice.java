package fr.smourad.vec.commaner;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.smourad.vec.Claim;
import fr.smourad.vec.file.PlayerData;

public class InventoryPrice implements Listener, CommandExecutor {

	public String inv_name = "Vente de blocs";
	
	public Claim plugin;
	
	public InventoryPrice(Claim plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Inventory inv =  e.getInventory();
		
		if (!(inv.getName().equals(inv_name))) {
			return;
		}
		
		ItemStack clicked = e.getCurrentItem();
		Player p = (Player) e.getWhoClicked();
		
		plugin.playerDataLoad(p.getName(), p);
		PlayerData playerfile = Claim.playerDataMap.get(p);
		
		if (plugin.setupEconomy()) {
			if (clicked.getType().equals(Material.IRON_INGOT)) {
				if ((Claim.economy.getBalance(p)-Claim.config.getInt("Block.price")*10) >= 0) {
					Claim.economy.withdrawPlayer(p, Claim.config.getInt("Block.price")*10);
					playerfile.setInt("Player.blocks", playerfile.getInt("Player.blocks")+10);
					p.sendMessage(ChatColor.GREEN + "Vous venez d'acheter 10 blocs de claim! Vous avez maintenant " + playerfile.getInt("Player.blocks") + " blocs.");
					p.closeInventory();
				} else {
					p.sendMessage(ChatColor.RED + "Vous n'avez plus assez d'argent pour cela.");
				}
			}
			
			if (clicked.getType().equals(Material.GOLD_INGOT)) {
				if ((Claim.economy.getBalance(p)-Claim.config.getInt("Block.price")*100) >= 0) {
					Claim.economy.withdrawPlayer(p, Claim.config.getInt("Block.price")*100);
					playerfile.setInt("Player.blocks", playerfile.getInt("Player.blocks")+100);
					p.sendMessage(ChatColor.GREEN + "Vous venez d'acheter 100 blocs de claim! Vous avez maintenant " + playerfile.getInt("Player.blocks") + " blocs.");
					p.closeInventory();
				} else {
					p.sendMessage(ChatColor.RED + "Vous n'avez plus assez d'argent pour cela.");
				}
			}
			
			if (clicked.getType().equals(Material.DIAMOND)) {
				if ((Claim.economy.getBalance(p)-Claim.config.getInt("Block.price")*1000) >= 0) {
					Claim.economy.withdrawPlayer(p, Claim.config.getInt("Block.price")*1000);
					playerfile.setInt("Player.blocks", playerfile.getInt("Player.blocks")+1000);
					p.sendMessage(ChatColor.GREEN + "Vous venez d'acheter 1000 blocs de claim! Vous avez maintenant " + playerfile.getInt("Player.blocks") + " blocs.");
					p.closeInventory();
				} else {
					p.sendMessage(ChatColor.RED + "Vous n'avez plus assez d'argent pour cela.");
				}
			}
		}
		
		e.setCancelled(true);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("blocks")) {
			if (!(sender instanceof Player)) {
				return true;
			}
			
			Player p = (Player) sender;
			
			plugin.playerDataLoad(p.getName(), p);
			PlayerData playerfile = Claim.playerDataMap.get(p);
			
			Inventory price = Bukkit.getServer().createInventory(null, 9*1, inv_name);
			
			ArrayList<String> lore_t = new ArrayList<String>();
			if (plugin.setupEconomy()) {
				lore_t.add("Argent actuel: " + Claim.economy.getBalance(p));
			}
			lore_t.add("Stock de blocs actuels: " + playerfile.getInt("Player.blocks"));
			lore_t.add("Prix: " + (Claim.config.getInt("Block.price")*10));
			
			ItemStack ten = new ItemStack(Material.IRON_INGOT, 1); 
			ItemMeta ten_m = ten.getItemMeta();
			
			ten_m.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "10 blocs");
			ten_m.setLore(lore_t);
			ten.setItemMeta(ten_m);
			
			price.setItem(0, ten);
			
			ArrayList<String> lore_h = new ArrayList<String>();
			if (plugin.setupEconomy()) {
				lore_h.add("Argent actuel: " + Claim.economy.getBalance(p));
			}
			lore_h.add("Stock de blocs actuels: " + playerfile.getInt("Player.blocks"));
			lore_h.add("Prix: " + (Claim.config.getInt("Block.price")*100));
			
			ItemStack hundred = new ItemStack(Material.GOLD_INGOT, 1); 
			ItemMeta hundred_m = ten.getItemMeta();
			
			hundred_m.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "100 blocs");
			hundred_m.setLore(lore_h);
			hundred.setItemMeta(hundred_m);
			
			price.setItem(1, hundred);
			
			ArrayList<String> lore_th = new ArrayList<String>();
			if (plugin.setupEconomy()) {
				lore_th.add("Argent actuel: " + Claim.economy.getBalance(p));
			}
			lore_th.add("Stock de blocs actuels: " + playerfile.getInt("Player.blocks"));
			lore_th.add("Prix: " + (Claim.config.getInt("Block.price")*1000));
			
			ItemStack thousand = new ItemStack(Material.DIAMOND, 1); 
			ItemMeta thousand_m = ten.getItemMeta();
			
			thousand_m.setDisplayName(ChatColor.RESET + "" + ChatColor.GRAY + "1000 blocs");
			thousand_m.setLore(lore_th);
			thousand.setItemMeta(thousand_m);
			
			price.setItem(2, thousand);
			
			p.openInventory(price);
			return true;
		}
		return false;
	}

}
