package actr.env;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Applet extends JApplet
{
	static JFrame frame = null;

	public void init ()
	{
		Main.applet = this;
		try
		{
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					JButton launch = new JButton("Launch");
					launch.addActionListener(new ActionListener() {
						public void actionPerformed (ActionEvent e) {
							frame = new Frame();
						}
					});
					getContentPane().setLayout (new BorderLayout(12,12));
					getContentPane().add (launch, BorderLayout.CENTER);

					setSize(150,30);
					setBackground (Color.white);
				}
			});
		} catch (Exception e) { e.printStackTrace(); }
	}

	public void destroy ()
	{
		if (frame!=null) { frame.setVisible(false); frame.dispose(); }
	}
}
