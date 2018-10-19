package fr.smourad.vec.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import fr.smourad.vec.Claim;
import net.minecraft.util.com.google.common.collect.Lists;

public class AdminZone implements CommandExecutor, TabExecutor {

	public Claim plugin;
	
	private ArrayList<String> allCmd = new ArrayList<String>();
	
	public AdminZone(Claim plugin) {
		this.plugin = plugin;
		allCmd.add("get");
		allCmd.add("delete");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Tu ne peux pas utiliser les commandes admin!");
			return true;
		}
		
		Player p = (Player) sender;
		
		if (!(p.isOp())) {
			p.sendMessage(ChatColor.RED + "Tu n'as pas la permission d'utiliser cette commande.");
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("adminzone")) {
			if (args.length == 0) {
				p.sendMessage(ChatColor.RED + "Il manque des arguments à ta commande.");
				return true;
			}
			
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("get")) {			
					CuboidSelection sel = new CuboidSelection(
							p.getWorld(), 
							p.getLocation(), 
							p.getLocation()
					);
					
					ProtectedCuboidRegion region = new ProtectedCuboidRegion(
							"HereOrNot", 
							new BlockVector(sel.getNativeMinimumPoint()), 
							new BlockVector(sel.getNativeMaximumPoint())
					);
					
					Map<String, ProtectedRegion> mapRg = plugin.getWorldGuard().getRegionManager(p.getWorld()).getRegions();
					List<ProtectedRegion> otherRegions = Lists.newArrayList(mapRg.values());
					List<ProtectedRegion> regionHere = region.getIntersectingRegions(otherRegions);
					
					if (regionHere.size() > 0) {
						ProtectedRegion rg = regionHere.get(0);
						String lastPlayerName = "";
						for (UUID id : rg.getOwners().getUniqueIds()) {
							lastPlayerName += Bukkit.getPlayer(id).getName();
						}
						DefaultDomain owners = new DefaultDomain();
						owners.addPlayer(plugin.getWorldGuard().wrapPlayer(p));
						rg.setOwners(owners);
						
						DefaultDomain members = new DefaultDomain();
						rg.setMembers(members);
						
						p.sendMessage(ChatColor.LIGHT_PURPLE + "Vous venez de prendre possession d'une zone appartenant à " + lastPlayerName + ".");
						return true;
					}
					p.sendMessage(ChatColor.RED + "Vous devez vous trouver dans une zone");
					return true;
				}

				if (args[0].equalsIgnoreCase("delete")) {
					CuboidSelection sel = new CuboidSelection(
							p.getWorld(), 
							p.getLocation(), 
							p.getLocation()
					);
					
					ProtectedCuboidRegion region = new ProtectedCuboidRegion(
							"HereOrNot", 
							new BlockVector(sel.getNativeMinimumPoint()), 
							new BlockVector(sel.getNativeMaximumPoint())
					);
					
					Map<String, ProtectedRegion> mapRg = plugin.getWorldGuard().getRegionManager(p.getWorld()).getRegions();
					List<ProtectedRegion> otherRegions = Lists.newArrayList(mapRg.values());
					List<ProtectedRegion> regionHere = region.getIntersectingRegions(otherRegions);
					
					if (regionHere.size() > 0) {
						ProtectedRegion rg = regionHere.get(0);
						String lastPlayerName = "";
						for (UUID id : rg.getOwners().getUniqueIds()) {
							lastPlayerName += Bukkit.getPlayer(id).getName();
						}
						plugin.getWorldGuard().getRegionManager(p.getWorld()).removeRegion(rg.getId());
						p.sendMessage(ChatColor.LIGHT_PURPLE + "Vous venez de supprimer une zone appartenant à " + lastPlayerName + ".");
						return true;
					}
					p.sendMessage(ChatColor.RED + "Vous devez vous trouver dans une zone");
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("adminzone")) {
			if (args.length == 1) {				
				ArrayList<String> tabCmd = new ArrayList<String>(); 
				if (!args[0].equals("")) {
					for (String str : allCmd) {
						if (str.startsWith(args[0].toLowerCase())) {
							tabCmd.add(str);
						}
					}
				} else {
					tabCmd = allCmd;
				}				
				return tabCmd;
			}
		}
		return null;
	}

}
