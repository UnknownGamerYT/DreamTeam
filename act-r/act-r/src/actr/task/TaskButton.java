package actr.task;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class TaskButton extends JButton implements TaskComponent
{
	public TaskButton (String text, int x, int y, int sx, int sy) 
	{
		super (text);
		setBounds (x, y, sx, sy);
		addActionListener (new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doClick();
			}
		});
		addMouseMotionListener (Task.fingerListener);
	}
	
	public int centerX () { return getX() + getWidth()/2; }
	public int centerY () { return getY() + getHeight()/2; }
	
	public void doClick () { }
	public void doKey (char c) { }
}
