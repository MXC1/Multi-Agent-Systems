package Mars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * Represent a rectangular grid of field positions. Each position is able to
 * store a single entity.
 * 
 * @author David J. Barnes and Michael Kolling
 * @author Maria Chli
 * @version 22-Oct-2008
 */
public class Field {
	// The depth and width of the field.
	private int depth, width;
	// Storage for the entities.
	private Entity[][] field;
	// Storage for the crumbs.
	private int[][] crumbsQuant;
	private int[][] signalStrength;

	/**
	 * Represent a field of the given dimensions. The topology of the grid is torus
	 * shaped.
	 * 
	 * @param depth
	 *            The depth of the field.
	 * @param width
	 *            The width of the field.
	 */
	public Field(int depth, int width) {
		this.depth = depth;
		this.width = width;
		field = new Entity[depth][width];
		crumbsQuant = new int[depth][width];
		signalStrength = new int[depth][width];
		for (int row = 0; row < depth; row++) {
			for (int col = 0; col < width; col++) {
				crumbsQuant[row][col] = 0;
				signalStrength[row][col] = 0;
			}
		}

	}

	public void setSignalStrength(int row, int col, int signal) {
		signalStrength[row][col] = signal;
	}

	public int getSignalStrength(int row, int col) {
		return signalStrength[row][col];
	}

	public int getSignalStrength(Location loc) {
		return getSignalStrength(loc.getRow(), loc.getCol());
	}

	/**
	 * Empty the field.
	 */
	public void clear() {
		for (int row = 0; row < depth; row++) {
			for (int col = 0; col < width; col++) {
				field[row][col] = null;
			}
		}
	}

	/**
	 * Place an entity at the given location. If there is already an entity at the
	 * location it will be lost.
	 * 
	 * @param entity
	 *            The entity to be placed
	 * @param row
	 *            Row coordinate of the location.
	 * @param col
	 *            Column coordinate of the location.
	 */
	public void place(Entity entity, int row, int col) {
		place(entity, new Location(row, col));
	}

	/**
	 * Place an entity at the given location. If there is already an entity at the
	 * location it will be lost.
	 * 
	 * @param entity
	 *            The entity to be placed.
	 * @param location
	 *            Where to place the entity.
	 */
	public void place(Entity entity, Location location) {
		field[location.getRow()][location.getCol()] = entity;
	}

	/**
	 * Clear the given location. If there is an entity at the location it will be
	 * lost.
	 * 
	 * @param location
	 *            The location to be cleared.
	 */
	public void clearLocation(Location location) {
		field[location.getRow()][location.getCol()] = null;
	}

	/**
	 * Return the entity at the given location, if any.
	 * 
	 * @param location
	 *            Where in the field.
	 * @return The entity at the given location, or null if there is none.
	 */
	public Entity getObjectAt(Location location) {
		return getObjectAt(location.getRow(), location.getCol());
	}

	/**
	 * Return the entity at the given location, if any.
	 * 
	 * @param row
	 *            The desired row.
	 * @param col
	 *            The desired column.
	 * @return The entity at the given location, or null if there is none.
	 */
	public Entity getObjectAt(int row, int col) {
		return field[row][col];
	}

	/**
	 * Return all free locations that are adjacent to the given location. If there
	 * is none, then return an empty list.
	 * 
	 * @param location
	 *            The location from which to generate an adjacency.
	 * @return A list of valid location within the grid area. This may be the empty
	 *         list if all locations around are full.
	 */
	public ArrayList<Location> getAllfreeAdjacentLocations(Location location) {
		Iterator<Location> adjacent = adjacentLocations(location);
		ArrayList<Location> freeLocations = new ArrayList<Location>();
		while (adjacent.hasNext()) {
			Location next = (Location) adjacent.next();
			if (field[next.getRow()][next.getCol()] == null) {
				freeLocations.add(next);
			}
		}
		return freeLocations;
	}

	public ArrayList<Location> getAllAdjacentLocations(Location location) {
		Iterator<Location> adjacent = adjacentLocations(location);
		ArrayList<Location> locations = new ArrayList<Location>();
		while (adjacent.hasNext()) {
			Location next = (Location) adjacent.next();
			locations.add(next);
		}
		return locations;
	}

	/**
	 * Try to find a free location that is adjacent to the given location. If there
	 * is none, then return the current location if it is free. If not, return null.
	 * The returned location will be within the valid bounds of the field.
	 * 
	 * @param location
	 *            The location from which to generate an adjacency.
	 * @return A valid location within the grid area. This may be the same object as
	 *         the location parameter, or null if all locations around are full.
	 */
	public Location freeAdjacentLocation(Location location) {
		Iterator<Location> adjacent = adjacentLocations(location);
		while (adjacent.hasNext()) {
			Location next = (Location) adjacent.next();
			if (field[next.getRow()][next.getCol()] == null) {
				return next;
			}
		}
		// check whether current location is free
		if (field[location.getRow()][location.getCol()] == null) {
			return location;
		} else {
			return null;
		}
	}

	/*
	 * 
	 * @return A copy of this field
	 */
	public Field cloneField() {
		Field clone = new Field(this.getDepth(), this.getWidth());
		for (int row = 0; row < depth; row++) {
			for (int col = 0; col < width; col++) {
				clone.field[row][col] = this.field[row][col];
				clone.crumbsQuant[row][col] = this.crumbsQuant[row][col];
				clone.signalStrength[row][col] = this.signalStrength[row][col];
			}
		}
		return clone;
	}

	/**
	 * Generate an iterator over a shuffled list of locations adjacent to the given
	 * one. The list will not include the location itself. All locations will lie
	 * within the grid. The topology of the grid is torus shaped. This means that it
	 * is like a chessboard but when a piece goes beyond the bottom row it reappears
	 * from the first row and vice versa. Similarly when it goes beyond the
	 * rightmost column it reappears from the leftmost column and vice versa.
	 * 
	 * @param location
	 *            The location from which to generate adjacencies.
	 * @return An iterator over locations adjacent to that given.
	 */
	private Iterator<Location> adjacentLocations(Location location) {
		return adjacentLocations(location, 1);
	}

	/**
	 * Detect if specified class is adjacent
	 * 
	 * @param loc
	 *            The location from which to generate adjacencies.
	 * @param c
	 *            The class of the object to check for
	 * @return boolean.
	 */
	public <T> boolean isNeighbourTo(Location loc, Class<T> c) {
		Iterator<Location> it = adjacentLocations(loc, 1);
		while (it.hasNext()) {
			Entity e = getObjectAt(it.next());
			if (e != null && e.getClass() == c)
				return true;
		}
		return false;
	}

	public <T> Location getNeighbour(Location loc, Class<T> c) {
		Iterator<Location> it = adjacentLocations(loc, 1);
		while (it.hasNext()) {
			Location adjLoc = it.next();
			Entity e = getObjectAt(adjLoc);
			if (e != null && e.getClass() == c)
				return adjLoc;
		}
		return null;
	}

	/**
	 * Generate an iterator over a shuffled list of locations within "manhattan
	 * distance" w to the given one. The list will not include the location itself.
	 * All locations will lie within the grid. The topology of the grid is torus
	 * shaped. This means that it is like a chessboard but when a piece goes beyond
	 * the bottom row it reappears from the first row and vice versa. Similarly when
	 * it goes beyond the rightmost column it reappears from the leftmost column and
	 * vice versa.
	 * 
	 * @param location
	 *            The location from which to generate adjacencies.
	 * @param w
	 *            The "manhattan" radius of the neighbourhood.
	 * @return An iterator over locations adjacent to that given.
	 */
	private Iterator<Location> adjacentLocations(Location location, int w) {
		int row = location.getRow();
		int col = location.getCol();
		LinkedList<Location> locations = new LinkedList<Location>();
		for (int roffset = -w; roffset <= w; roffset++) {
			int nextRow = row + roffset;
			// if(nextRow >= 0 && nextRow < depth) {
			for (int coffset = -w; coffset <= w; coffset++) {
				int nextCol = col + coffset;
				// Exclude invalid locations and the original location.
				/*
				 * if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
				 * locations.add(new Location(nextRow, nextCol)); }
				 */
				if (nextCol < 0)
					nextCol = nextCol + width;
				if (nextCol >= width)
					nextCol = nextCol - width;
				if (nextRow < 0)
					nextRow = nextRow + depth;
				if (nextRow >= depth)
					nextRow = nextRow - depth;
				locations.add(new Location(nextRow, nextCol));
			}
			// }
		}
		Random rand = ModelConstants.random;
		Collections.shuffle(locations, rand);
		return locations.iterator();
	}

	public void reduceCrumbs() {
		for (int row = 0; row < depth; row++) {
			for (int col = 0; col < width; col++) {
				if (crumbsQuant[row][col] > 0) {
					crumbsQuant[row][col]--;
				}
			}
		}
	}

	/**
	 * @return The depth of the field.
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * @return The width of the field.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Return the number of crumbs at the given location, if any.
	 * 
	 * @param l
	 *            The location to check
	 * @return The number of crumbs at the given location.
	 */
	public int getCrumbQuantityAt(Location l) {
		return this.crumbsQuant[l.getRow()][l.getCol()];
	}

	public void pickUpACrumb(Location l) {
		if (this.getCrumbQuantityAt(l) > 0) {
			this.crumbsQuant[l.getRow()][l.getCol()]--;
		}
	}

	public void dropCrumbs(Location l, int q) {
		if (this.getCrumbQuantityAt(l) < 10 - q) {
			this.crumbsQuant[l.getRow()][l.getCol()] += q;
		}
	}
}
