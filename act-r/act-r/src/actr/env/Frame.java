package actr.env;

import java.awt.*;
import javax.swing.*;

public class Frame extends JFrame
{
	static Frame frame;
	static Main main;

	Frame ()
	{
		super ("ACT-R");
		
		frame = this;
		main = new Main();

		getContentPane().setLayout (new BorderLayout(12,12));
		getContentPane().add (main, BorderLayout.CENTER);
		getRootPane().setDefaultButton (main.runRealTimeButton);

		setSize (1100,700);
		setVisible (true);
		main.splitPane.setDividerLocation (0.50);
		main.taskoutPane.setDividerLocation (0.50);
		repaint();
	}

	public static void main (String[] args)
	{
		(new SwingWorker<Object,Object>() {
			public Object doInBackground() {
				return new Frame ();
			}
		}).execute();
	}
}
