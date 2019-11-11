package Mars;

//Import general packages
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

/**
 * Main GUI for Mars Explorer simulator.
 *
 * @author Maria Chli
 * @version 08-Nov-2009
 *
 */

public class GUImain {

	// Convenient to have base windows available everywhere within this class
	private JFrame mainFrame;

	private LabelledTextArea simLength;
	private LabelledTextArea simSeed;
	private LabelledTextArea marsWidth;
	private LabelledTextArea marsDepth;
	private LabelledTextArea obstacleCreationProb;
	private LabelledTextArea vehicleCreationProb;
	private LabelledTextArea rockClusterNum;
	private LabelledTextArea rockLocations;
	private LabelledTextArea rockClusterStd;
	private LabelledCheckBox showTrails;

	JButton setUpButton;
	JButton stepOnceButton;
	JButton runLongButton;
	JButton resetButton;
	JButton quitButton;

	private String defSimLength = "1000";
	private String defSimSeed = "133";
	private String defSimDim = "50"; // default value for the dimension of the simulation grid
	private String defSimProb = "0.002"; // default value for wherever a probability is needed
	private String defRockClusterNum = "7"; // default value for the number of clusters
	private String defRockLocations = "300"; // default value for number of rock locations
	private String defRockClusterStd = "2.0"; // default value for number of rock cluster std

	private Simulator s;

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		final GUImain gm = new GUImain();
	}

	/**
	 * Constructs GUI and attaches listeners
	 */
	private GUImain() {
		// Step 1: create the components
		setUpButton = new JButton();
		stepOnceButton = new JButton();
		runLongButton = new JButton();
		resetButton = new JButton();
		quitButton = new JButton();

		// Use LabelledTextAreas for input
		simLength = new LabelledTextArea("Simulation Length: ", defSimLength);
		simSeed = new LabelledTextArea("Simulation Seed: ", defSimSeed);
		marsWidth = new LabelledTextArea(" Mars Width: ", defSimDim);
		marsDepth = new LabelledTextArea(" Mars Depth: ", defSimDim);

		obstacleCreationProb = new LabelledTextArea("Obstacle: ", defSimProb);
		vehicleCreationProb = new LabelledTextArea("Vehicle: ", defSimProb);

		rockLocations = new LabelledTextArea("Number of Rocks: ", defRockLocations);
		rockClusterNum = new LabelledTextArea("Number of Clusters: ", defRockClusterNum);
		rockClusterStd = new LabelledTextArea("Rock Clusters Std: ", defRockClusterStd);
		showTrails = new LabelledCheckBox("Show Crumb Trails: ", true);

		// Step 2: set the properties of the components
		setUpButton.setText("Set up simulation");
		setUpButton.setToolTipText("Feed simulation parameters and set up simulation.");
		setUpButton.setEnabled(true);
		stepOnceButton.setText("Step Once");
		stepOnceButton.setToolTipText("Run simulation for only one step.");
		stepOnceButton.setEnabled(false);
		runLongButton.setText("Run");
		runLongButton.setToolTipText("Run simulation for the duration specified.");
		runLongButton.setEnabled(false);
		resetButton.setText("Reset");
		resetButton.setToolTipText("Allow changing of the parameters.");
		resetButton.setEnabled(false);
		quitButton.setText("Quit");
		quitButton.setToolTipText("Quit simulation.");

		// Step 3: create containers to hold the components
		mainFrame = new JFrame("Mars Setup");

		JPanel simParamsBox = new JPanel();
		JPanel creationProbBox = new JPanel();
		JPanel rockBox = new JPanel();
		JPanel commandBox = new JPanel();
		JPanel entityParamsBox = new JPanel();
		JPanel lowerBox = new JPanel();
		lowerBox.setBorder(BorderFactory.createEtchedBorder());

		// Step 3.5: add borders to containers
		simParamsBox.setBorder(new TitledBorder("Simulation Parameters"));
		creationProbBox.setBorder(new TitledBorder("Obstacles & Vehicles"));
		rockBox.setBorder(new TitledBorder("Rock Placement"));

		// Step 4: specify LayoutManagers
		mainFrame.getContentPane().setLayout(new BorderLayout());
		simParamsBox.setLayout(new GridLayout(2, 2));
		creationProbBox.setLayout(new GridLayout(3, 1));
		rockBox.setLayout(new GridLayout(3, 1));
		commandBox.setLayout(new GridLayout(3, 2));
		lowerBox.setLayout(new BorderLayout());
		entityParamsBox.setLayout(new BorderLayout());

		// Step 5: add components to containers
		commandBox.add(setUpButton);
		commandBox.add(resetButton);
		commandBox.add(stepOnceButton);
		commandBox.add(runLongButton);
		commandBox.add(new JLabel());
		commandBox.add(quitButton);

		simParamsBox.add(simLength);
		simParamsBox.add(marsWidth);
		simParamsBox.add(simSeed);
		simParamsBox.add(marsDepth);

		creationProbBox.add(obstacleCreationProb);
		creationProbBox.add(vehicleCreationProb);
		creationProbBox.add(showTrails);

		rockBox.add(rockLocations);
		rockBox.add(rockClusterNum);
		rockBox.add(rockClusterStd);

		entityParamsBox.add(creationProbBox, BorderLayout.CENTER);
		entityParamsBox.add(rockBox, BorderLayout.WEST);
		lowerBox.add(entityParamsBox, BorderLayout.NORTH);
		lowerBox.add(commandBox, BorderLayout.SOUTH);

		mainFrame.getContentPane().add(simParamsBox, BorderLayout.NORTH);
		mainFrame.getContentPane().add(lowerBox, BorderLayout.SOUTH);

		// Step 6: arrange to handle events in the user interface
		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exitApp();
			}
		});

		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exitApp();
			}
		});

		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});

		runLongButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Thread thread;
				thread = new Thread(new Runnable() {
					@Override
					public void run() {
						runSimulation();
						mainFrame.setVisible(true);
					}
				});
				thread.start();
				mainFrame.setVisible(false);
			}
		});
		stepOnceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Thread thread;
				thread = new Thread(new Runnable() {
					@Override
					public void run() {
						runSimulationOnce();
						mainFrame.setVisible(true);
					}
				});
				thread.start();
				mainFrame.setVisible(false);
			}
		});
		setUpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Thread thread;
				thread = new Thread(new Runnable() {
					@Override
					public void run() {
						setUp();
						mainFrame.setVisible(true);
					}
				});
				thread.start();
				mainFrame.setVisible(false);
			}
		});

		// Step 7: Display the GUI
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	// Helper methods to provide functionality in actions.

	/**
	 * Displays dialog box for user to confirm exit
	 */
	private void exitApp() {
		// Display confirmation dialog before exiting application
		int response = JOptionPane.showConfirmDialog(mainFrame, "Do you really want to quit?", "Quit?",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (response == JOptionPane.YES_OPTION) {
			System.exit(0);
		}

		// Don't quit
	}

	/**
	 * Reads the values from the GUI and then runs the simulation
	 */
	private void setUp() {
		try {
			// Get the values inserted by the user
			int time = (int) (Math.round(simLength.getValue()));
			int valSimSeed = (int) (Math.round(simSeed.getValue()));
			int valMarsWidth = (int) (Math.round(marsWidth.getValue()));
			int valMarsDepth = (int) (Math.round(marsDepth.getValue()));
			double valObstacleCreationProb = obstacleCreationProb.getValue();
			double valVehicleCreationProb = vehicleCreationProb.getValue();
			int valRockClusters = (int) (Math.round(rockClusterNum.getValue()));
			int valRockLocations = (int) (Math.round(rockLocations.getValue()));
			double valRockClusterStd = rockClusterStd.getValue();
			boolean valShowTrails = showTrails.getValue();

			// Set the values in the ModelConstants class
			ModelConstants.LENGTH = time;
			ModelConstants.RANDOM_SEED = valSimSeed;
			ModelConstants.DEFAULT_WIDTH = valMarsWidth;
			ModelConstants.DEFAULT_DEPTH = valMarsDepth;
			ModelConstants.OBSTACLE_CREATION_PROBABILITY = valObstacleCreationProb;
			ModelConstants.VEHICLE_CREATION_PROBABILITY = valVehicleCreationProb;
			ModelConstants.ROCK_CLUSTERS = valRockClusters;
			ModelConstants.ROCK_CLUSTER_STD = valRockClusterStd;
			ModelConstants.ROCK_LOCATIONS = valRockLocations;
			ModelConstants.SHOW_CRUMBS = valShowTrails;

			ModelConstants.setRandom();

			// set up simulator
			this.s = new Simulator();

			// Enable run buttons
			setUpButton.setEnabled(false);
			stepOnceButton.setEnabled(true);
			runLongButton.setEnabled(true);
			resetButton.setEnabled(true);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(mainFrame, "Problem creating simulation." + e.getMessage());
		}
	}

	private void runSimulation() {
		try {

			// Run the simulation
			for (int i = 0; i < ModelConstants.LENGTH; i++) {
				s.simulateOneStep();
			}
			runLongButton.setText("Continue running");
			runLongButton.setToolTipText("Continue running simulation for the duration specified.");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(mainFrame, "Problem running simulation." + e.getMessage());
		}
	}

	private void runSimulationOnce() {
		try {
			s.simulateOneStep();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(mainFrame, "Problem running simulation." + e.getMessage());
		}
	}

	public void reset() {
		if (this.s != null)
			s.closeView();
		setUpButton.setEnabled(true);
		stepOnceButton.setEnabled(false);
		runLongButton.setEnabled(false);
		runLongButton.setText("Run");
		runLongButton.setToolTipText("Run simulation for the duration specified.");
	}
}
