package Mars;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashMap;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 *
 * (ITN) modified so that when window is closed the application terminates.
 * 
 * @author David J. Barnes and Michael Kolling
 * @version 2002-04-23
 * @author Ian T. Nabney
 * @version 02-02-2005
 * @author Maria Chli
 * @version 01-11-2009	
 */
public class SimulatorView extends JFrame
{
	static final long serialVersionUID = -3018063635072997091L;
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Pop.: ";
    private JLabel stepLabel, population;
    //private JPanel parameters;
    private FieldView fieldView;
    private Simulator s;
    
    // A map for storing colors for participants in the simulation
    private HashMap<Class, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;

    /**
     * Create a view of the given width and height.
     */
    public SimulatorView(int height, int width, Simulator s)
    {
        this.s = s;
        stats = new FieldStats();
        colors = new HashMap<Class, Color>();

        setTitle("Mars Explorer Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.LEFT);
        //parameters = makeParametersPanel();
        
        setLocation(100, 50);
        
        fieldView = new FieldView(height, width);

        Container contents = getContentPane();
        contents.add(stepLabel, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
        //contents.add(parameters, BorderLayout.EAST);
	
	// Make sure that closing the window ends the application
	this.setDefaultCloseOperation(
	      WindowConstants.DISPOSE_ON_CLOSE);

	this.addWindowListener(new WindowAdapter() {
                @Override
		public void windowClosing(WindowEvent e) {
		    exitApp();
		}
	});

        pack();
        setVisible(true);
    }
 /*   
    public JPanel makeParametersPanel(){
    	JPanel panel = new JPanel();
    	JButton stepOnceButton = new JButton("Step Once");
    	stepOnceButton.addActionListener(
    		new ActionListener(){
    			public void actionPerformed(ActionEvent e){
    				s.simulateOneStep();
    				fieldView.repaint();
    			}
    		}
    	);
    	panel.add(stepOnceButton);
    	return panel;
    }
*/
    /**
     * Method called when application is quit
     */
    private void exitApp() {
	System.exit(0);
    }
    
    /**
     * Define a color to be used for a given class of actor.
     */
    public void setColor(Class actorClass, Color color)
    {
        colors.put(actorClass, color);
    }

    /**
     * Define a color to be used for a given class of actor.
     */
    private Color getColor(Class actorClass)
    {
        Color col = colors.get(actorClass);
        if(col == null) {
            // no color defined for this class
            return UNKNOWN_COLOR;
        }
        else {
            return col;
        }
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field to represent.
     */
    public void showStatus(int step, Field field)
    {
        if(!isVisible())
            setVisible(true);

        stepLabel.setText(STEP_PREFIX + step);

        stats.reset();
        fieldView.preparePaint();
            
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
            	Object actor = field.getObjectAt(row, col);
                if(actor != null) {
                    stats.incrementCount(actor.getClass());
                    fieldView.drawMark(col, row, getColor(actor.getClass()));
                    if(actor instanceof Vehicle)
                    	if(((Vehicle)actor).carryingSample)
                            fieldView.drawMark(col, row, ModelConstants.vehicleCarryingSampleColor);
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                	if(ModelConstants.SHOW_CRUMBS){
                    	//fieldView.drawNum((field.getCrumbQuantityAt(new Location(row,col))), col, row);
                    	int numCrumbs = field.getCrumbQuantityAt(new Location(row,col));
                    	numCrumbs = 255-20*numCrumbs;
                    	numCrumbs = (numCrumbs<=255)?numCrumbs:255;
                    	numCrumbs = (numCrumbs>=0)?numCrumbs:0;
                    	fieldView.drawMark(col, row, new Color(255, numCrumbs, numCrumbs));
                    }
                }
                
            }
        }
        stats.countFinished();

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species present.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }
    
    /**
     * Provide a graphical view of a rectangular field. This is 
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this 
     * for your project if you like.
     */
    private class FieldView extends JPanel
    {
    	static final long serialVersionUID = -3018063635072997091L;
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        @Override
        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                                 gridHeight * GRID_VIEW_SCALING_FACTOR);
        }
        
        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint()
        {
            if(! size.equals(getSize())) {  // if the size has changed...
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if(xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if(yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }
        
        /**
         * Paint on grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        /**
         * Write on grid location on this field a given number.
         */
        public void drawNum(int num, int x, int y)
        {
            g.setColor(Color.darkGray);
        	g.drawString(Integer.toString(num), (x) * xScale, (y+1) * yScale);
        }
        
        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        /*
        public void paintComponent(Graphics g)
        {
            if(fieldImage != null) {
                g.drawImage(fieldImage, 0, 0, null);
            }
        }
        */
        
        @Override
        public void paint(Graphics g)
        {
            if(fieldImage != null) {
                g.drawImage(fieldImage, 0, 0, null);
            }       	
        }
    }
}
