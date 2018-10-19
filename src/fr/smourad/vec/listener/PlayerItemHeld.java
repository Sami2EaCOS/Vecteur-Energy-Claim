package fr.smourad.vec.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

import fr.smourad.vec.Claim;
import fr.smourad.vec.hashmap.LocationEmergency;

public class PlayerItemHeld implements Listener {

	@EventHandler
	public void onPlayerSwitchHotbar(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		if (!(Claim.selectionHashmap.containsKey(p))) {
			Claim.selectionHashmap.put(p, new LocationEmergency(null, null));
		}
		
		if (null == e.getPlayer().getInventory().getItem(e.getPreviousSlot())) {
			return;
		}
		
		if (p.getInventory().getItem(e.getPreviousSlot()).getType().equals(Material.GOLD_SPADE)) {
			if (Claim.selectionHashmap.get(p).getLeftLocation() != null || Claim.selectionHashmap.get(p).getRightLocation() != null) {
				Claim.selectionHashmap.get(p).setLeftLocation(null);
				Claim.selectionHashmap.get(p).setRightLocation(null);
				p.sendMessage(ChatColor.DARK_GRAY + "Vous n'êtes plus dans le mode de claim.");
			}
		}
	}
}
