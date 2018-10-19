package fr.smourad.vec.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.smourad.vec.Claim;
import fr.smourad.vec.hashmap.LocationEmergency;

public class PlayerInteract implements Listener {
	
	private int numberOfBlocks;
	
	@EventHandler
	public void interactWithPlayer(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Action a = e.getAction();
		
		if (p.getItemInHand().getType() == Material.GOLD_SPADE) {
			if (!(Claim.selectionHashmap.containsKey(p))) {
				Claim.selectionHashmap.put(p, new LocationEmergency(null, null));
			}
			
			Block block = e.getClickedBlock();
			if (a.equals(Action.RIGHT_CLICK_BLOCK)) {						
				Location right = new Location(p.getWorld(), block.getLocation().getX(), 0, block.getLocation().getZ());
				Claim.selectionHashmap.get(p).setRightLocation(right);
				p.sendMessage(ChatColor.LIGHT_PURPLE + "Position du clic droit mis en X:" + block.getX() + " / Z:" + block.getZ());
			}
			
			if (a.equals(Action.LEFT_CLICK_BLOCK)) {
				Location left = new Location(p.getWorld(), block.getLocation().getX(), 256, block.getLocation().getZ());
				Claim.selectionHashmap.get(p).setLeftLocation(left);
				p.sendMessage(ChatColor.LIGHT_PURPLE + "Position du clic droit mis en X:" + block.getX() + " / Z:" + block.getZ());
			}
			
			if (!(Claim.selectionHashmap.get(p).getRightLocation() == null || Claim.selectionHashmap.get(p).getLeftLocation() == null)) {
				
				int X = (int) (Claim.selectionHashmap.get(p).getRightLocation().getX() - Claim.selectionHashmap.get(p).getLeftLocation().getX());
				int Z = (int) (Claim.selectionHashmap.get(p).getRightLocation().getZ() - Claim.selectionHashmap.get(p).getLeftLocation().getZ());
				
				if (X < 0) {
					X*=-1;
				}
				if (Z < 0) {
					Z*=-1;
				}
				
				numberOfBlocks = (Z+1)*(X+1);
				
				p.sendMessage(ChatColor.LIGHT_PURPLE + "L'aire de la zone voulue est de " + numberOfBlocks);
			}
			
			e.setCancelled(true);
		}		
	}
}

