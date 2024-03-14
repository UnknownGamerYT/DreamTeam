package com.example.dreamteam.actr.core.task;

//import java.awt.*;
//import java.awt.event.*;
//import java.awt.geom.*;
import java.util.Iterator;
//import javax.swing.*;

import com.example.dreamteam.actr.core.Model;
//import com.example.dreamteam.actr.env.Main;

public abstract class Task
{
	String name;
	public Model model;
	boolean showMouse;
	int mouseX, mouseY;
	boolean showAttention;
	int attentionX, attentionY;
	boolean fingerPlaced;
	static int fingerX, fingerY;
	boolean soundAttended;
	//JTextField instructionField;

	public Task (String name)
	{
		super ();

		this.name = name;
		model = null;
		showMouse = false;
		mouseX = mouseY = 0;
		showAttention = false;
		attentionX = attentionY = 0;
		fingerPlaced = false;
		fingerX = fingerY = 0;

		//instructionField.requestFocusInWindow();
		//instructionField.addActionListener (new ActionListener() {
			//public void actionPerformed(ActionEvent e) {
				//processInstruction (instructionField.getText());
			//}
		//});

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

	//public Component add (Component comp)
	//{
		//return super.add (comp);
	//}

	//public void processDisplay ()
	//{
		//model.clearVisual();
		//Component[] components = getComponents();
		//for (int i=0 ; i<components.length ; i++)
		//{
		//	Component component = components[i];
		//	if (component instanceof TaskLine  && component.isVisible())
		//		model.addVisual ("line"+i, "line", "nothing",
		//				component.getX(), component.getY(), component.getWidth(), component.getHeight());
		//	else if (component instanceof TaskButton && component.isVisible())
		//		model.addVisual ("button"+i, "oval", ((JButton) component).getText(),
		//				component.getX(), component.getY(), component.getWidth(), component.getHeight());
		//	else if (component instanceof TaskLabel && component.isVisible())
		//		model.addVisual ("text"+i, "text", '"' + ((JLabel) component).getText() + '"',
		//				component.getX(), component.getY(), component.getWidth(), component.getHeight());
		//}
	//}

	public void moveMouse (int x, int y)
	{
		showMouse = true;
		mouseX = x;
		mouseY = y;
		//repaint();
	}

	//public void clickMouse ()
	//{
		//Point mousePoint = new Point (mouseX, mouseY);
		//Component[] components = getComponents();
		//for (int i=0 ; i<components.length ; i++)
		//{
			//Component component = components[i];
			//Rectangle bounds = component.getBounds();
			//if (bounds.contains (mousePoint))
			//{
				//TaskComponent tc = (TaskComponent) component;
				//tc.doClick();
				//repaint();
				//return;
			//}
		//}
	//}

	public void typeKey (char c) { }

	public void speak (String s) { }

	public void moveAttention (int x, int y)
	{
		showAttention = true;
		attentionX = x;
		attentionY = y;
		//repaint();
	}


	public void addAural (double timeDelta, final String id, final String type, final String content)
	{
		model.addEvent (new com.example.dreamteam.actr.core.Event (model.getTime()+timeDelta, "task", "update") {
			public void action() {
				//addAural (id, type, content);

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
			//repaint();
		}
	}

	public void placeFinger (double timeDelta, final int x, final int y)
	{
		model.addEvent (new com.example.dreamteam.actr.core.Event (model.getTime()+timeDelta, "task", "update") {
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
		model.addEvent (new com.example.dreamteam.actr.core.Event (model.getTime()+timeDelta, "task", "update") {
			public void action() {
				removeFinger();
			}
		});
	}

	public void attendSound () { soundAttended = true; }
	public void unattendSound () { soundAttended = false; }

	public void addEvent (com.example.dreamteam.actr.core.Event event)
	{
		model.addEvent (event);
	}

	public void addUpdate (final double timeDelta)
	{
		model.addEvent (new com.example.dreamteam.actr.core.Event (model.getTime()+timeDelta, "task", "update") {
			public void action() {
				update (model.getTime());
			}
		});
	}

	public void addPeriodicUpdate (final double timeDelta)
	{
		update (model.getTime());
		model.addEvent (new com.example.dreamteam.actr.core.Event (model.getTime()+timeDelta, "task", "update") {
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
		model.addEvent (new com.example.dreamteam.actr.core.Event (model.getTime()+timeDelta, "task", "update") {
			public void action() {
				removeFinger();
				//instructionField.setText (text);
				processInstruction (text);
			}
		});
	}

	public void addInstruction (double timeDelta, final String text, final TaskComponent component)
	{
		model.addEvent (new com.example.dreamteam.actr.core.Event (model.getTime()+timeDelta, "task", "update") {
			public void action() {
				placeFinger (component.centerX(), component.centerY());
				//instructionField.setText (text);
				processInstruction (text);
			}
		});
	}

	public String toString () { return name; }
}
