package com.example.dreamteam.actr.task;

import javax.swing.*;

public class TaskLabel extends JLabel implements TaskComponent
{
	public TaskLabel (String text, int x, int y, int sx, int sy) 
	{
		super (text);
		setBounds (x, y, sx, sy);
		setHorizontalAlignment (JLabel.CENTER);
		setVerticalAlignment (JLabel.CENTER);
	}
	
	public int centerX () { return getX() + getWidth()/2; }
	public int centerY () { return getY() + getHeight()/2; }

	public void doClick () { }
	public void doKey (char c) { }
}
