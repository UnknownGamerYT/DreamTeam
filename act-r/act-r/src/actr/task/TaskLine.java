package actr.task;

import java.awt.*;
import javax.swing.JPanel;

public class TaskLine extends JPanel implements TaskComponent
{
	TaskLine (int x, int y, int sx, int sy, Color color) 
	{
		super();
		setForeground (color);
		setBounds (x, y, sx, sy);
	}
	
	public TaskLine (int x, int y, int sx, int sy) 
	{
		this (x, y, sx, sy, Color.black);
	}
	
	public void setWidth (int sx)
	{
		setBounds (getX(), getY(), sx, getHeight());
	}
	
	public void changeWidth (int dx)
	{
		setBounds (getX(), getY(), dx+getWidth(), getHeight());
	}
	
	protected void paintComponent (Graphics g)
	{
		g.setColor (this.getForeground());
		g.fillRect (0, 0, getWidth(), getHeight());
	}

	public int centerX () { return getX() + getWidth()/2; }
	public int centerY () { return getY() + getHeight()/2; }

	public void doClick () { }
	public void doKey (char c) { }
}
