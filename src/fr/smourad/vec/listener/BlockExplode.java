package fr.smourad.vec.listener;

import java.util.List;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import fr.smourad.vec.Claim;
import net.minecraft.util.com.google.common.collect.Lists;

public class BlockExplode implements Listener {

	public Claim plugin;
	
	public BlockExplode(Claim plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBlockExplosion(EntityExplodeEvent e) {
		for (int i = e.blockList().size()-1; i>=0; i--) {
			Block b = e.blockList().get(i);
			CuboidSelection sel = new CuboidSelection(
					b.getWorld(), 
					b.getLocation(), 
					b.getLocation()
			);
			
			ProtectedCuboidRegion region = new ProtectedCuboidRegion(
					"BlockOrNot", 
					new BlockVector(sel.getNativeMinimumPoint()), 
					new BlockVector(sel.getNativeMaximumPoint())
			);
			
			Map<String, ProtectedRegion> mapRg = plugin.getWorldGuard().getRegionManager(b.getWorld()).getRegions();
			List<ProtectedRegion> otherRegions = Lists.newArrayList(mapRg.values());
			List<ProtectedRegion> regionHere = region.getIntersectingRegions(otherRegions);
			
			if (regionHere.size() > 0) {
				e.blockList().remove(i);
			}
		}
	}
}
