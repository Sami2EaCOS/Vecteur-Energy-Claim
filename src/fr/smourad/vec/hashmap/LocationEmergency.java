package fr.smourad.vec.hashmap;

import org.bukkit.Location;

public class LocationEmergency {

	public Location left;
	public Location right;
	
	public LocationEmergency(Location left, Location right) {
		this.left = left;
		this.right = right;
	}
	
	public Location getLeftLocation() {
		return left;
	}
	
	public Location getRightLocation() {
		return right;
	}
	
	public void setLeftLocation(Location left) {
		this.left = left;
	}
	
	public void setRightLocation(Location right) {
		this.right = right;
	}
}
