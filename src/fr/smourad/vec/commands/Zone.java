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
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import fr.smourad.vec.Claim;
import fr.smourad.vec.file.PlayerData;
import fr.smourad.vec.hashmap.LocationEmergency;
import net.minecraft.util.com.google.common.collect.Lists;

public class Zone implements CommandExecutor, TabExecutor {

	private Claim plugin;
	
	private ArrayList<String> allCmd = new ArrayList<String>();
	
	public Zone(Claim plugin) {
		this.plugin = plugin;
		allCmd.add("claim");
		allCmd.add("delete");
		allCmd.add("trust");
		allCmd.add("untrust");
		allCmd.add("trustlist");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Tu ne peux pas utiliser les commandes de claim!");
			return true;
		}
		
		Player p = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("zone")) {
			
			if (!(Claim.selectionHashmap.containsKey(p))) {
				Claim.selectionHashmap.put(p, new LocationEmergency(null, null));
			}
			
			if (args.length == 0) {
				p.sendMessage(ChatColor.RED + "Il manque des arguments à ta commande.");
				return true;
			}
			
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("claim")) {					
					if (Claim.selectionHashmap.get(p).getRightLocation() == null || Claim.selectionHashmap.get(p).getLeftLocation() == null) {
						p.sendMessage(ChatColor.RED + "Vous devez faire une selection avant!");
						return true;
					}
					
					CuboidSelection sel = new CuboidSelection(
							p.getWorld(), 
							Claim.selectionHashmap.get(p).getRightLocation(), 
							Claim.selectionHashmap.get(p).getLeftLocation()
					);
							
					RegionManager rgManager = plugin.getWorldGuard().getRegionManager(p.getWorld());
					String rgName = "zone_" + p.getName();
					boolean isOk = false;
					int numberOfRegion = 0;
					
					while (!(isOk)) {
						if (rgManager.hasRegion(rgName)) {
							numberOfRegion++;
							rgName = "zone_" + p.getName() + numberOfRegion;
						} else {
							isOk = true;
						}
					}
					
					ProtectedCuboidRegion region = new ProtectedCuboidRegion(
							rgName, 
							new BlockVector(sel.getNativeMinimumPoint()), 
							new BlockVector(sel.getNativeMaximumPoint())
					);
					
					DefaultDomain owners = new DefaultDomain();
					owners.addPlayer(plugin.getWorldGuard().wrapPlayer(p));
					
					region.setOwners(owners);
					region.setFlag(DefaultFlag.GREET_MESSAGE, ChatColor.GREEN + "Bienvenue dans une zone de " + p.getName());
					region.setFlag(DefaultFlag.FAREWELL_MESSAGE, ChatColor.RED + "Vous êtes sortis de la zone de " + p.getName());	
					
					Map<String, ProtectedRegion> rg = rgManager.getRegions();
					List<ProtectedRegion> otherRegions = Lists.newArrayList(rg.values());
							
					if (region.getIntersectingRegions(otherRegions).size() > 0) {
						p.sendMessage(ChatColor.RED + "Vous ne pouvez pas créer un claim sur une zone déjà protégée.");
						return true;
					}
					
					int X = (int) (region.getMaximumPoint().getX() - region.getMinimumPoint().getX());
					int Z = (int) (region.getMaximumPoint().getZ() - region.getMinimumPoint().getZ());
					
					if (X < 0) {
						X*=-1;
					}
					if (Z < 0) {
						Z*=-1;
					}
					
					plugin.playerDataLoad(p.getName(), p);
					PlayerData playerfile = Claim.playerDataMap.get(p);
					
					int numberOfBlocks = (Z+1)*(X+1);
					int playerBlocks = playerfile.getInt("Player.blocks") - numberOfBlocks;
					
					if (playerBlocks < 0 ) {
						p.sendMessage(ChatColor.RED + "Il ne vous reste plus assez de blocs pour ce claim! Votre solde actuelle est de " + playerfile.getInt("Player.blocks"));
						return true;
					}
					
					rgManager.addRegion(region);
					playerfile.setInt("Player.blocks", playerBlocks);
					p.sendMessage(ChatColor.GREEN + "Votre zone de " + numberOfBlocks + " blocs est maintenant créée! Il vous reste maintenant " + playerBlocks + " blocs.");
					return true;
				}
				
				if (args[0].equalsIgnoreCase("blocks")) {
					plugin.playerDataLoad(p.getName(), p);
					PlayerData playerfile = Claim.playerDataMap.get(p);
					
					p.sendMessage(ChatColor.YELLOW + "Il vous reste " + playerfile.getInt("Player.blocks") + " blocs de claim!");
					return true;
				}
				
				
				if (args[0].equalsIgnoreCase("trustlist")) {
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
						String own = "Propriétaire: ";
						for (UUID id : rg.getOwners().getUniqueIds()) {
							own += Bukkit.getPlayer(id).getName();
						}
						String members = "Membres: ";
						for (UUID id : rg.getMembers().getUniqueIds()) {
							members += Bukkit.getPlayer(id).getName() + ",";
						}
						if (rg.getMembers().getUniqueIds().size() > 0) {
							members.substring(0, members.length()-2);
						} else {
							members += "aucun";
						}
						
						p.sendMessage(ChatColor.YELLOW + own + " / " + members);
						return true;
					}
					p.sendMessage(ChatColor.RED + "Vous devez vous trouver dans une zone claim.");
					return true;
				}
				
				if (args[0].equalsIgnoreCase("trust")) {
					p.sendMessage(ChatColor.RED + "Vous ne pouvez pas rajouter personne!");
					return true;
				}
				
				if (args[0].equalsIgnoreCase("untrust")) {
					p.sendMessage(ChatColor.RED + "Vous ne pouvez pas enlever personne!");
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
						if (rg.getOwners().contains(p.getUniqueId())) {
							
							int X = (int) (rg.getMaximumPoint().getX() - rg.getMinimumPoint().getX());
							int Z = (int) (rg.getMaximumPoint().getZ() - rg.getMinimumPoint().getZ());
							
							if (X < 0) {
								X*=-1;
							}
							if (Z < 0) {
								Z*=-1;
							}
							
							plugin.playerDataLoad(p.getName(), p);
							PlayerData playerfile = Claim.playerDataMap.get(p);
							
							int numberOfBlocks = (Z+1)*(X+1);
							int playerBlocks = numberOfBlocks + playerfile.getInt("Player.blocks");
							
							plugin.getWorldGuard().getRegionManager(p.getWorld()).removeRegion(rg.getId());
							playerfile.setInt("Player.blocks", playerBlocks);
							p.sendMessage(ChatColor.GRAY + "Votre zone est maintenant supprimée, vous avez récupéré " + numberOfBlocks + " blocs! Vous avez " + playerBlocks + " au total!");
							return true;
						}
					}
					p.sendMessage(ChatColor.RED + "Vous devez vous trouver sur une zone qui vous appartient pour la supprimer.");
					return true;
				}
			}
			
			if (args.length == 2) {
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
				
				if (args[0].equalsIgnoreCase("trust")) {
					if (regionHere.size() > 0) {
						ProtectedRegion rg = regionHere.get(0);
						if(rg.getOwners().contains(p.getUniqueId())) {
							if (Bukkit.getPlayer(args[1]) == null) {
								p.sendMessage(ChatColor.RED + "Le joueur doit être en ligne!");
								return true;
							}
							if (Bukkit.getPlayer(args[1]) == p) {
								p.sendMessage(ChatColor.RED + "Vous ne pouvez pas vous ajouter vous même!");
								return true;
							}
							DefaultDomain members = rg.getMembers();
							members.addPlayer(plugin.getWorldGuard().wrapPlayer(Bukkit.getPlayer(args[1])));
							rg.setMembers(members);
							p.sendMessage(ChatColor.GRAY + args[1] + " a été ajouté à votre zone.");
							return true;
						}
					}
					p.sendMessage(ChatColor.RED + "Vous devez vous trouver dans votre zone!");
					return true;
				}
				
				if (args[0].equalsIgnoreCase("untrust")) {
					if (regionHere.size() > 0) {
						ProtectedRegion rg = regionHere.get(0);
						if(rg.getOwners().contains(p.getUniqueId())) {
							if (Bukkit.getPlayer(args[1]) == null) {
								p.sendMessage(ChatColor.RED + "Le joueur doit être en ligne!");
								return true;
							}
							if (Bukkit.getPlayer(args[1]) == p) {
								p.sendMessage(ChatColor.RED + "Vous ne pouvez pas vous supprimer vous même!");
								return true;
							}
							DefaultDomain members = rg.getMembers();
							members.removePlayer(plugin.getWorldGuard().wrapPlayer(Bukkit.getPlayer(args[1])));
							rg.setMembers(members);
							p.sendMessage(ChatColor.GRAY + args[1] + " a été supprimé de votre zone.");
							return true;
						}
					}
					p.sendMessage(ChatColor.RED + "Vous devez vous trouver dans votre zone!");
					return true;
				}
			}
				
		}
		
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("zone")) {
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
