package Mars;

import java.awt.Color;
import java.util.Random;

class ModelConstants {
	public static int RANDOM_SEED = 52;

	// the duration of the simulation
	public static int LENGTH = 10000;

	// Constants representing configuration information for the simulation.
	// The default width for the grid.
	public static int DEFAULT_WIDTH = 100;
	// The default depth of the grid.
	public static int DEFAULT_DEPTH = 100;

	// The number of rock clusters.
	public static int ROCK_CLUSTERS = 7;
	// The std dev of rock clusters.
	public static double ROCK_CLUSTER_STD = 2.0;
	// The number of rock locations.
	public static int ROCK_LOCATIONS = 300;

	// The probability that an obstacle will be created in any given grid position.
	public static double OBSTACLE_CREATION_PROBABILITY = 0.01;
	public static double VEHICLE_CREATION_PROBABILITY = 0.01;

	// Whether or not to show in colour how many crumbs a location contains
	public static boolean SHOW_CRUMBS = true;

	// The colours
	public static final Color rockColor = Color.orange;
	public static final Color obstacleColor = Color.black;
	public static final Color vehicleColor = Color.cyan;
	public static final Color vehicleCarryingSampleColor = Color.blue;
	public static final Color mothershipColor = Color.magenta;

	public static Random random = null;

	public static void setRandom() {
		random = new Random(RANDOM_SEED);
	}
}
