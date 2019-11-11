package Mars;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;



/**
 * Creates a JLabel and JTextArea coupled together
 *
 * @author Maria Chli
 * @version 8/11/2009
 */


public class LabelledTextArea extends JComponent {
	
	private static final long serialVersionUID = -1978073598544126546L;
	private JLabel label;
	private JTextArea textArea;

	
	/**
	 * Creates a <code>LabelledTextArea</code>.
	 *
	 * @param text a {@link java.lang.String} that names the label and text area
	 * @param value a double that gives the initial value
	 */
	public LabelledTextArea(String text, String value) 
	{
		this.label = new JLabel(text);
		this.textArea = new JTextArea(value);
		
		
		this.setLayout(new BorderLayout());
		this.add(label, BorderLayout.WEST);
		this.add(textArea, BorderLayout.EAST);
		this.textArea.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0),new EtchedBorder()));
		
	}
	
	
	/**
	 * Returns text area value
	 *
	 * @return the double value.
	 */
	public double getValue() {
		double d = Double.parseDouble(textArea.getText().trim()); 
		return d;
	}
	
}
