package Mars;

import java.util.ArrayList;

class Vehicle extends Entity {
	public boolean carryingSample;

	public Vehicle(Location l) {
		super(l);
		this.carryingSample = false;
	}

	/**
	 * Decide which implementation to execute - This must be done manually by
	 * commenting out the opposite implementation call
	 * 
	 * @param f
	 *            Instance of class Field
	 * @param m
	 *            Instance of class Mothership
	 * @param rocksCollected
	 *            ArrayList containing the rocks this vehicle is carrying
	 */
	public void act(Field f, Mothership m, ArrayList<Rock> rocksCollected) {
		actCollaborative(f, m, rocksCollected);
		// actSimple(f, m, rocksCollected);
	}

	/**
	 * Execute the collaborative implementation - Drop crumbs to help other agents
	 * find their way to rock clusters
	 * 
	 * @param f
	 *            Instance of class Field
	 * @param m
	 *            Instance of class Mothership
	 * @param rocksCollected
	 *            ArrayList containing the rocks this vehicle is carrying
	 */
	public void actCollaborative(Field f, Mothership m, ArrayList<Rock> rocksCollected) {

		if (carryingSample) {
			if (findAdjacentMothership(f) != null) {
				dropSample(rocksCollected);
			} else {
				dropCrumbs(f);
				moveUpGradient(f);
			}
		} else {
			if (detectSample(f) != null) {
				pickUpSample(f, detectSample(f), rocksCollected);
			} else {
				if (senseCrumbs(f)) {
					pickUpCrumb(f);
					moveDownGradient(f);
				} else {
					moveRandomly(f);
				}
			}
		}
	}

	/**
	 * Drop 2 crumbs at this location
	 * 
	 * @param f
	 *            Instance of class Field
	 */
	private void dropCrumbs(Field f) {
		f.dropCrumbs(this.getLocation(), 2);
	}

	/**
	 * Sense if there are any crumbs at the vehicles current location
	 * 
	 * @param f
	 *            Instance of class Field
	 * @return True if there are crumbs present
	 */
	private Boolean senseCrumbs(Field f) {
		return (f.getCrumbQuantityAt(this.getLocation()) > 0);
	}

	/**
	 * Pick up a crumb from the vehicles location
	 * 
	 * @param f
	 *            Instance of class Field
	 */
	private void pickUpCrumb(Field f) {
		f.pickUpACrumb(this.getLocation());
	}

	/**
	 * Move away from the mothership, by detecting the signal values of adjacent
	 * locations and moving to the lowest value
	 * 
	 * @param f
	 *            Instance of class Field
	 */
	private void moveDownGradient(Field f) {
		ArrayList<Location> adjacentLocations = f.getAllAdjacentLocations(this.getLocation());

		// Find adjacent empty location with highest signal strength
		double currentMin = 9999;
		Location minSignalStrengthAdjacent = f.freeAdjacentLocation(this.getLocation());

		for (Location adjacent : adjacentLocations) {
			if (f.getObjectAt(adjacent) == null) {
				if (f.getSignalStrength(adjacent) < currentMin) {
					currentMin = f.getSignalStrength(adjacent);
					minSignalStrengthAdjacent = adjacent;
				}
			}

			if (minSignalStrengthAdjacent != null) {
				moveTo(f, minSignalStrengthAdjacent);
			}
		}
	}

	/**
	 * Execute the simple implementation
	 * 
	 * @param f
	 *            Instance of class Field
	 * @param m
	 *            Instance of class Mothership
	 * @param rocksCollected
	 *            ArrayList containing the rocks this vehicle is carrying
	 */
	public void actSimple(Field f, Mothership m, ArrayList<Rock> rocksCollected) {

		if (carryingSample) {
			if (findAdjacentMothership(f) != null) {
				dropSample(rocksCollected);
			} else {
				moveUpGradient(f);
			}
		} else {
			if (detectSample(f) != null) {
				pickUpSample(f, detectSample(f), rocksCollected);
			} else {
				moveRandomly(f);
			}
		}
	}

	/**
	 * Drop the sample at the mothership
	 * 
	 * @param rocksCollected
	 *            ArrayList containing the rocks this vehicle is carrying
	 */
	private void dropSample(ArrayList<Rock> rocksCollected) {
		rocksCollected.clear();
		// Couldn't find a global counter/variable to store rocks in mothership?
		carryingSample = false;
	}

	/**
	 * Move towards the mothership, by detecting signal values of adjacent locations
	 * and moving to highest value
	 * 
	 * @param f
	 *            Instance of class Field
	 */
	private void moveUpGradient(Field f) {
		ArrayList<Location> adjacentLocations = f.getAllAdjacentLocations(this.getLocation());

		double currentMax = 0;
		Location maxSignalStrengthAdjacent = f.freeAdjacentLocation(this.getLocation());

		for (Location adjacent : adjacentLocations) {
			if (f.getObjectAt(adjacent) == null) {
				if (f.getSignalStrength(adjacent) > currentMax) {
					currentMax = f.getSignalStrength(adjacent);
					maxSignalStrengthAdjacent = adjacent;
				}
			}

			if (maxSignalStrengthAdjacent != null) {
				moveTo(f, maxSignalStrengthAdjacent);
			}
		}
	}

	/**
	 * Search for an adjacent location with a rock sample in it
	 * 
	 * @param f
	 *            Instance of class Field
	 * @return Location of adjacent rock sample
	 */
	private Location detectSample(Field f) {
		ArrayList<Location> adjacentLocations = f.getAllAdjacentLocations(this.getLocation());
		for (Location adjacent : adjacentLocations) {
			Entity objectAtLocation = f.getObjectAt(adjacent);
			if (objectAtLocation instanceof Rock) {
				return adjacent;
			}
		}
		return null;
	}

	/**
	 * Remove the rock sample from the location and add rock sample to ArrayList
	 * variable
	 * 
	 * @param f
	 *            Instance of class Field
	 * @param l
	 *            Location of adjacent rock sample
	 * @param rocksCollected
	 *            ArrayList containing the rocks this vehicle is carrying
	 */
	private void pickUpSample(Field f, Location l, ArrayList<Rock> rocksCollected) {
		rocksCollected.add((Rock) f.getObjectAt(l));
		f.clearLocation(l);
		this.carryingSample = true;
	}

	/**
	 * Look for the mothership in adjacent squares
	 * 
	 * @param f
	 *            Instance of class Field
	 * @return Location of mothership
	 */
	private Location findAdjacentMothership(Field f) {
		ArrayList<Location> adjacentLocations = f.getAllAdjacentLocations(this.getLocation());
		for (Location adjacent : adjacentLocations) {
			Entity objectAtLocation = f.getObjectAt(adjacent);
			if (objectAtLocation instanceof Mothership) {
				return adjacent;
			}
		}
		return null;
	}

	/**
	 * Move to a random, free, adjacent location
	 * 
	 * @param f
	 *            Instance of class Field
	 */
	private void moveRandomly(Field f) {
		Location vehicleLocation = this.getLocation();
		Location freeAdjacentLocation = f.freeAdjacentLocation(vehicleLocation);

		moveTo(f, freeAdjacentLocation);
	}

	/**
	 * Move to a specified location
	 * 
	 * @param f
	 *            Instance of class Field
	 * @param l
	 *            Location to move to
	 */
	private void moveTo(Field f, Location l) {
		f.clearLocation(this.getLocation());
		f.place(this, l);
		this.setLocation(l);
	}
}
