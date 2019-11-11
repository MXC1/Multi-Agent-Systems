package Mars;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;



/**
 * Creates a JLabel and JCheckBoc coupled together
 *
 * @author Maria Chli
 * @version 8/11/2009
 */


public class LabelledCheckBox extends JComponent {
	
	private static final long serialVersionUID = -1978073598544126547L;
	private JLabel label;
	private JCheckBox checkBox;

	
	/**
	 * Creates a <code>LabelledCheckBox</code>.
	 *
	 * @param text a {@link java.lang.String} that names the label and text area
	 * @param value a boolean that gives the initial value
	 */
	public LabelledCheckBox(String text, boolean value) 
	{
		this.label = new JLabel(text);
		this.checkBox = new JCheckBox();
		this.checkBox.setSelected(value);
		
		this.setLayout(new BorderLayout());
		this.add(label, BorderLayout.WEST);
		this.add(checkBox, BorderLayout.EAST);
		this.checkBox.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0),new EtchedBorder()));
		
	}
	
	
	/**
	 * Returns checkBox value
	 *
	 * @return the boolean value.
	 */
	public boolean getValue() {
		boolean value = checkBox.isSelected(); 
		return value;
	}
	
}
