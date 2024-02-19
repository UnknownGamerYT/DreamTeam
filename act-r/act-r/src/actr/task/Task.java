package actr.task;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Iterator;
import javax.swing.*;

import actr.core.Model;
import actr.env.Main;

public abstract class Task extends JPanel
{
	String name;
	public Model model;
	boolean showMouse;
	int mouseX, mouseY;
	boolean showAttention;
	int attentionX, attentionY;
	boolean fingerPlaced;
	static int fingerX, fingerY;
	static MouseMotionListener fingerListener;
	JLabel auralLabel;
	boolean soundAttended;
	JTextField instructionField;
	static Image imageFinger = Main.getImage ("finger.png");

	public Task (String name)
	{
		super ();
		setLayout (null);
		setBackground (Color.white);

		this.name = name;
		model = null;
		showMouse = false;
		mouseX = mouseY = 0;
		showAttention = false;
		attentionX = attentionY = 0;
		fingerPlaced = false;
		fingerX = fingerY = 0;

		auralLabel = new JLabel ();
		auralLabel.setBounds (300, 10, 100, 20);
		add (auralLabel);
		soundAttended = false;

		instructionField = new JTextField ("", 200);
		auralLabel.setBounds (300, 10, 100, 20);
		add (instructionField);
		instructionField.requestFocusInWindow();
		instructionField.addActionListener (new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				processInstruction (instructionField.getText());
			}
		});

		//		fingerListener = new MouseMotionAdapter() {
		//			public void mouseMoved(MouseEvent e) {
		//				Point pt = getLocationOnScreen();
		//				placeFinger (e.getXOnScreen() - pt.x, e.getYOnScreen() - pt.y);
		//			}
		//		};
		//		addMouseMotionListener (fingerListener);
	}

	public void setModel (Model model)
	{
		this.model = model;
	}

	public String getName() { return name; }

	public int numberOfSimulations () { return 1; }

	public void start () { }
	public void update (double time) { }

	public void eval (Iterator<String> it) { }
	public double bind (Iterator<String> it) { return 0; }

	public Result getResult () { return null; }
	public void analyze (Result[] results) { }
	public void printResults () { }

	public abstract boolean check ();
	public abstract double score ();

	public Component add (Component comp)
	{
		return super.add (comp);
	}

	public void processDisplay ()
	{
		model.clearVisual();
		Component[] components = getComponents();
		for (int i=0 ; i<components.length ; i++)
		{
			Component component = components[i];
			if (component instanceof TaskLine  && component.isVisible())
				model.addVisual ("line"+i, "line", "nothing",
						component.getX(), component.getY(), component.getWidth(), component.getHeight());
			else if (component instanceof TaskButton && component.isVisible())
				model.addVisual ("button"+i, "oval", ((JButton) component).getText(),
						component.getX(), component.getY(), component.getWidth(), component.getHeight());
			else if (component instanceof TaskLabel && component.isVisible())
				model.addVisual ("text"+i, "text", '"' + ((JLabel) component).getText() + '"',
						component.getX(), component.getY(), component.getWidth(), component.getHeight());
		}
	}

	public void moveMouse (int x, int y)
	{
		showMouse = true;
		mouseX = x;
		mouseY = y;
		repaint();
	}

	public void clickMouse ()
	{
		Point mousePoint = new Point (mouseX, mouseY);
		Component[] components = getComponents();
		for (int i=0 ; i<components.length ; i++)
		{
			Component component = components[i];
			Rectangle bounds = component.getBounds();
			if (bounds.contains (mousePoint))
			{
				TaskComponent tc = (TaskComponent) component;
				tc.doClick();
				repaint();
				return;
			}
		}
	}

	public void typeKey (char c) { }

	public void speak (String s) { }

	public void moveAttention (int x, int y)
	{
		showAttention = true;
		attentionX = x;
		attentionY = y;
		repaint();
	}

	public void addAural (String id, String type, String content)
	{
		model.addAural (id, type, content);
		auralLabel.setText (type+":"+content);
		repaint();
	}

	public void addAural (double timeDelta, final String id, final String type, final String content)
	{
		model.addEvent (new actr.core.Event (model.getTime()+timeDelta, "task", "update") {
			public void action() {
				addAural (id, type, content);
			}
		});
	}

	public void placeFinger (int x, int y)
	{
		synchronized (model) {
			fingerX = x;
			fingerY = y;
			fingerPlaced = true;
			model.addVisual ("finger", "finger", "nothing", fingerX, fingerY, 1, 1);
			repaint();
		}
	}

	public void placeFinger (double timeDelta, final int x, final int y)
	{
		model.addEvent (new actr.core.Event (model.getTime()+timeDelta, "task", "update") {
			public void action() {
				placeFinger (x, y);
			}
		});
	}

	public void removeFinger ()
	{
		fingerPlaced = false;
		model.removeVisual ("finger");
	}

	public void removeFinger (double timeDelta)
	{
		model.addEvent (new actr.core.Event (model.getTime()+timeDelta, "task", "update") {
			public void action() {
				removeFinger();
			}
		});
	}

	public void attendSound () { soundAttended = true; }
	public void unattendSound () { soundAttended = false; }

	public void addEvent (actr.core.Event event)
	{
		model.addEvent (event);
	}

	public void addUpdate (final double timeDelta)
	{
		model.addEvent (new actr.core.Event (model.getTime()+timeDelta, "task", "update") {
			public void action() {
				update (model.getTime());
			}
		});
	}

	public void addPeriodicUpdate (final double timeDelta)
	{
		update (model.getTime());
		model.addEvent (new actr.core.Event (model.getTime()+timeDelta, "task", "update") {
			public void action() {
				addPeriodicUpdate (timeDelta);
			}
		});
	}

	public void processInstruction (String text)
	{
		text += " .";
		double wordSpacing = 1.0;
		String words[] = text.split(" ");
		for (int i=0 ; i<words.length ; i++)
			addAural (i*wordSpacing, "sound", "sound", words[i]);
	}

	public void addInstruction (double timeDelta, final String text)
	{
		model.addEvent (new actr.core.Event (model.getTime()+timeDelta, "task", "update") {
			public void action() {
				removeFinger();
				instructionField.setText (text);
				processInstruction (text);
			}
		});
	}

	public void addInstruction (double timeDelta, final String text, final TaskComponent component)
	{
		model.addEvent (new actr.core.Event (model.getTime()+timeDelta, "task", "update") {
			public void action() {
				placeFinger (component.centerX(), component.centerY());
				instructionField.setText (text);
				processInstruction (text);
			}
		});
	}

	public void paintComponent (Graphics g)
	{
		g.setColor (Color.white);
		g.fillRect (0, 0, getWidth(), getHeight());
		super.paintComponent (g);
	}

	public void paintChildren (Graphics g)
	{
		auralLabel.setBounds (getWidth()-151, getHeight()-41, 150, 20);
		instructionField.setBounds (20, getHeight()-41, 200, 20);

		super.paintChildren (g);
		Graphics2D g2d = (Graphics2D) g;

		Composite oldComp = g2d.getComposite();
		Composite alphaComp = AlphaComposite.getInstance (AlphaComposite.SRC_OVER, 0.50f);

		if (showAttention)
		{
			g2d.setComposite (alphaComp);
			g2d.setPaint (Color.yellow);
			Ellipse2D.Double circle = new Ellipse2D.Double (attentionX-20, attentionY-20, 40, 40);
			g2d.fill (circle);
			g2d.setComposite (oldComp);
		}

		if (showMouse)
		{
			g2d.setPaint (Color.black);
			Polygon cursor = new Polygon();
			cursor.addPoint (0, 0);
			cursor.addPoint (0, 13);
			cursor.addPoint (3, 10);
			cursor.addPoint (5, 16);
			cursor.addPoint (7, 15);
			cursor.addPoint (5, 10);
			cursor.addPoint (10, 10);
			cursor.translate (mouseX, mouseY);
			g2d.fill (cursor);
		}

		if (fingerPlaced)
		{
			g2d.drawImage (imageFinger, fingerX, fingerY, 80, 40, null);

			//			g2d.setComposite (alphaComp);
			//			g2d.setPaint (Color.blue);
			//			Ellipse2D.Double circle = new Ellipse2D.Double (fingerX-10, fingerY-10, 20, 20);
			//			g2d.fill (circle);
			//			g2d.setComposite (oldComp);
		}

		if (soundAttended)
		{
			Rectangle rect = auralLabel.getBounds();
			g2d.setPaint (Color.green);
			g2d.draw (rect);
		}
	}

	public String toString () { return name; }
}
