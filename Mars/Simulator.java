package Mars;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * A simulator simulator for Steels'(1990) Mars exploration paradigm, based on a toroidal field
 * containing rocks and obstacles.
 * 
 * @author Maria Chli
 * @version 2009.10.31
 */
public class Simulator
{
    // Lists of entities in the field. Separate lists are kept for ease of iteration.
    private ArrayList<Rock> rocks;
    //private ArrayList<Obstacle> obstacles;
    private ArrayList<Vehicle> vehicles;
    private Mothership mothership;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(ModelConstants.DEFAULT_DEPTH, ModelConstants.DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = ModelConstants.DEFAULT_DEPTH;
            width = ModelConstants.DEFAULT_WIDTH;
        }
        
        rocks = new ArrayList<Rock>();
        //obstacles = new ArrayList<Obstacle>();
        vehicles = new ArrayList<Vehicle>();
        field = new Field(depth, width);
        

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width,this);
        view.setColor(Rock.class, ModelConstants.rockColor);
        view.setColor(Obstacle.class, ModelConstants.obstacleColor);
        view.setColor(Vehicle.class, ModelConstants.vehicleColor);
        view.setColor(Mothership.class, ModelConstants.mothershipColor);
        
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * e.g. 500 steps.
     */
    public void runLongSimulation()
    {
        simulate(500);
    }
    
    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int tick = 1; tick <= numSteps && view.isViable(field); tick++) {
            simulateOneStep();
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * vehicle.
     */
    public void simulateOneStep()
    {
        step++;
		Field tempField = field.cloneField();
	
		ArrayList<Rock> rocksToRemove = new ArrayList<Rock>();  
		
       for(Iterator<Vehicle> it = vehicles.iterator(); it.hasNext(); ) {
            Vehicle v = it.next();
            v.act(tempField,mothership,rocksToRemove);
       }
   		
       for(Iterator<Rock> it = rocksToRemove.iterator(); it.hasNext(); ) {
       		Rock r = it.next();
       		//tempField.clearLocation(r.getLocation());
       		rocks.remove(r);
       }
                 
      field = tempField;
      view.showStatus(step, field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        populate();
        randomLandMothership();
        // Show the starting state in the view.
        view.showStatus(step, field);
    }
    
    /**
     * Randomly populate the field with obstacles and rocks.
     */
    private void populate()
    {
        field.clear();

        Location rockLocations[];
    	
    	rockLocations = ClusterGenerator.generateClusters(ModelConstants.ROCK_CLUSTERS, 
    			ModelConstants.ROCK_LOCATIONS, field.getWidth(), field.getDepth(), 
    			ModelConstants.ROCK_CLUSTER_STD);
    	
    	for(int i=0; i<rockLocations.length; i++)
    	{
            Location location = rockLocations[i];
            Rock r = new Rock(location);
            field.place(r,location);
            rocks.add(r);    		
    	}
    	
    	 		
 		double obsProb = ModelConstants.OBSTACLE_CREATION_PROBABILITY;
 		double vehProb = ModelConstants.OBSTACLE_CREATION_PROBABILITY + ModelConstants.VEHICLE_CREATION_PROBABILITY;
 		
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
            	Location location = new Location(row, col);
            	if(field.getObjectAt(location)==null)
            	{
                	double ran = ModelConstants.random.nextDouble();
	                if(ran <= obsProb) {
	                    Obstacle o = new Obstacle(location);
	                    field.place(o,location);
	                    //obstacles.add(o);
	                }
	                else if(ran > obsProb && ran <= vehProb) {
	                   	//location = new Location(20, 20);
	                    Vehicle v = new Vehicle(location);
	                    //v.carryingSample = true;
	                    field.place(v,location);
	                    vehicles.add(v);
	                }
	                // else leave the location empty.
            	}
            }
        }
        
    }
    
   
    	
   /**
     * Land the mothership at a random location
     */
    public void randomLandMothership(){
    	int x = ModelConstants.random.nextInt(field.getDepth()-1);
    	int y = ModelConstants.random.nextInt(field.getWidth()-1);
    	Location l = new Location(x,y);
    	Entity e = field.getObjectAt(l);
    	while (e!=null){
    		x = ModelConstants.random.nextInt(field.getDepth()-1);
    		y = ModelConstants.random.nextInt(field.getWidth()-1);
    		l = new Location(x,y);
    		e = field.getObjectAt(l);
    	}
    	Mothership m = new Mothership(l);
    	field.place(m,l);
    	mothership = m;
    	mothership.emitSignal(field);
    	view.showStatus(step, field);
    }   	
    
    public void closeView()
    {
    	this.view.setVisible(false);
    }
    
    public static void main(String[] args){
    	Simulator s = new Simulator();
		//s.randomLandMothership();
		s.simulate(11000);
		
    }
}
