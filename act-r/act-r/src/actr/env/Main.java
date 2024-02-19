package actr.env;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.Vector;
import javax.swing.*;
import actr.core.Model;
import actr.task.*;

public class Main extends JPanel
{
	Main main;
	static JApplet applet;
	JComboBox groupBox;
	JComboBox taskBox;
	JTextField goalTextField;
	JSplitPane splitPane, taskoutPane;
	JButton runRealTimeButton, runBatchButton, runAllButton, buffersButton, whynotButton, stopButton;
	JEditorPane modelArea;
	JTextArea outputArea;
	Thread actrThread;
	boolean stop;
	Color grayBG = new Color (230,230,230);

	String groups[];
	String groupTasks[];
	Task currentTask;
	Model currentModel;

	Main ()
	{
		super ();
		main = this;
		setOpaque (true);
		setBackground (grayBG);

		JPanel modelPanel = new JPanel();
		modelPanel.setLayout (new BorderLayout(12,12));
		modelPanel.setOpaque (true);
		modelPanel.setBackground (grayBG);

		groups = loadGroups();
		groupBox = new JComboBox (groups);
		groupBox.addActionListener (new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				String name = (String) groupBox.getSelectedItem();
				groupTasks = loadGroupTasks (name);
				DefaultComboBoxModel cbm = (DefaultComboBoxModel) taskBox.getModel();
				cbm.removeAllElements();
				for (int i=0 ; i<groupTasks.length ; i++) cbm.addElement (groupTasks[i]);
				taskBox.validate();
				taskBox.repaint();
				currentTask = null;
				currentModel = null;
				taskBox.setSelectedIndex(0);
			}
		});
		
		taskBox = new JComboBox (new DefaultComboBoxModel());
		taskBox.addActionListener (new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				String name = (String) taskBox.getSelectedItem();
				setCurrentTask (name);
				setCurrentModel (name);
			}
		});

		JPanel northPanel = new JPanel();
		northPanel.setLayout (new GridLayout(0,2));
		northPanel.add (groupBox);
		northPanel.add (taskBox);
		modelPanel.add (northPanel, BorderLayout.NORTH);

		modelArea = new JEditorPane();
		modelPanel.add (new JScrollPane(modelArea), BorderLayout.CENTER);

		runRealTimeButton = new JButton ("Run Real-Time");
		runRealTimeButton.addActionListener (new ActionListener() {
			public void actionPerformed (ActionEvent e)
			{
				if (currentTask==null || currentModel==null) return;
				synchronized (main) { stop = false; }
				(new SwingWorker<Object,Object>() {
					public Object doInBackground() {
						disableRun();
						outputArea.setText ("");
						resetCurrentModel ();
						if (currentModel != null)
						{
							currentModel.setParameter (":real-time", "true");
							currentModel.run();
						}
						enableRun();
						return null;
					}
				}).execute();
			}
		});

		runBatchButton = new JButton ("Run Batch");
		runBatchButton.addActionListener (new ActionListener() {
			public void actionPerformed (ActionEvent e)
			{
				if (currentTask==null || currentModel==null) return;
				synchronized (main) { stop = false; }
				(new SwingWorker<Object,Object>() {
					public Object doInBackground() {
						disableRun();
						outputArea.setText ("");
						setCurrentTask ((String) taskBox.getSelectedItem());
						if (currentTask == null) return null;
						int n = currentTask.numberOfSimulations();
						Result[] results = new Result[n];
						for (int i=0 ; !stop && i<n ; i++)
						{
							resetCurrentModel ();
							if (currentModel == null) continue;
							currentModel.setParameter (":real-time", "nil");
							currentModel.run();
							results[i] = currentTask.getResult();
						}
						if (!stop && currentModel != null)
						{
							currentTask.analyze (results);
							currentTask.printResults();
						}
						enableRun();
						return null;
					};
				}).execute();
			}
		});

		runAllButton = new JButton ("Run All Models");
		runAllButton.addActionListener (new ActionListener() {
			public void actionPerformed (ActionEvent e)
			{
				synchronized (main) { stop = false; }
				(new SwingWorker<Object,Object>() {
					public Object doInBackground() {
						disableRun();
						String tasks[] = groupTasks;
						outputArea.setText ("");
						output ("Task                Check       Score");
						output ("-------------------------------------");
						for (int k=0 ; !stop && k<tasks.length ; k++)
						{
							taskBox.setSelectedIndex(k);
							setCurrentTask (tasks[k]);
							//setCurrentModel (tasks[k]);
							if (currentTask == null) continue;
							int n = currentTask.numberOfSimulations();
							Result[] results = new Result[n];
							for (int i=0 ; !stop && i<n ; i++)
							{
								setCurrentModel (tasks[k]);
								if (currentModel == null) continue;
								currentModel.setParameter (":real-time", "nil");
								currentModel.setParameter (":v", "nil");
								currentModel.run();
								results[i] = currentTask.getResult();
							}
							if (!stop && currentModel != null)
							{
								currentTask.analyze (results);
								boolean check = currentTask.check();
								double score = currentTask.score();
								output (String.format ("%-20s", currentTask) +
										String.format ("%-12s", (check ? "true":"false")) +
										(score==-1 ? "----" : String.format ("%.2f",score)));
							}
						}
						enableRun();
						return null;
					}
				}).execute();
			}
		});

		buffersButton = new JButton ("Buffers");
		buffersButton.addActionListener (new ActionListener()
		{
			public void actionPerformed (ActionEvent e) {
				synchronized (main) { 
					if (currentModel!=null) currentModel.printBuffers();
				}
			}
		});

		whynotButton = new JButton ("Why Not");
		whynotButton.addActionListener (new ActionListener()
		{
			public void actionPerformed (ActionEvent e) {
				synchronized (main) { 
					if (currentModel!=null) currentModel.printWhyNot();
				}
			}
		});

		stopButton = new JButton ("Stop");
		stopButton.addActionListener (new ActionListener()
		{
			public void actionPerformed (ActionEvent e) {
				synchronized (main) { 
					stop = true;
					if (currentModel!=null) currentModel.stop();
				}
			}
		});
		stopButton.setEnabled(false);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout (new GridLayout(0,3));
		bottomPanel.add (runRealTimeButton);
		bottomPanel.add (runBatchButton);
		bottomPanel.add (runAllButton);
		bottomPanel.add (buffersButton);
		bottomPanel.add (whynotButton);
		bottomPanel.add (stopButton);
		bottomPanel.setOpaque (true);
		bottomPanel.setBackground (grayBG);

		modelPanel.add (bottomPanel, BorderLayout.SOUTH);

		JPanel taskPanel = new JPanel();
		taskPanel.setOpaque(true);
		taskPanel.setBackground (grayBG);

		outputArea = new JTextArea();
		outputArea.setFont (new Font("Consolas", Font.PLAIN, 11));
		outputArea.setLineWrap (false);
		outputArea.setWrapStyleWord (false);

		taskoutPane = new JSplitPane (JSplitPane.VERTICAL_SPLIT, taskPanel, new JScrollPane(outputArea));
		taskoutPane.setBorder (BorderFactory.createEmptyBorder(12,12,12,12));
		taskoutPane.setOpaque (true);
		taskoutPane.setBackground (grayBG);

		splitPane = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT, modelPanel, taskoutPane);
		splitPane.setBorder (BorderFactory.createEmptyBorder(12,12,12,12));
		splitPane.setOpaque(true);
		splitPane.setBackground (grayBG);

		setLayout (new BorderLayout(12,12));
		add (splitPane, BorderLayout.CENTER);

		setSize (1100, 700);
		splitPane.setDividerLocation (0.50);
		taskoutPane.setDividerLocation (0.50);

		groupBox.setSelectedIndex (1);
	}

	String[] loadGroups ()
	{
		File dir = new File ("bin/actr/models/");
		File[] groupFiles = dir.listFiles();
		String groups[] = new String[groupFiles.length];
		for (int i=0 ; i<groups.length ; i++)
		{
			groups[i] = groupFiles[i].getName();
		}
		return groups;
	}

	String[] loadGroupTasks (String group)
	{
		try
		{
			File file = new File ("bin/actr/models/"+group+"/ALL.txt");
			BufferedReader reader = new BufferedReader (new FileReader (file));
			Vector<String> v = new Vector<String>();
			String line;
			while ((line = reader.readLine()) != null) v.add (line);
			String[] names = new String[v.size()];
			for (int i=0 ; i<v.size() ; i++) names[i] = v.elementAt(i);
			return names;
		}
		catch (Exception e) { e.printStackTrace(); return null; }
	}

	void setCurrentTask (String name)
	{
		if (name==null) return;
		if (name.contains(":")) name = name.substring (name.indexOf(":")+1);
		if (currentTask==null || !currentTask.getName().equals(name))
		{
			try
			{
				String groupName = (String) groupBox.getSelectedItem();
				currentTask = (Task) (Class.forName("actr.models."+groupName+"."+name).newInstance());
				taskoutPane.setTopComponent (currentTask);
				taskoutPane.setDividerLocation (0.50);
			}
			catch (Exception e) { e.printStackTrace(); }
		}
	}

	void setCurrentModel (String name)
	{
		if (currentTask == null) return;
		if (name==null) return;
		if (name.contains(":")) name = name.substring (0, name.indexOf(":"));
		try {
			String groupName = (String) groupBox.getSelectedItem();
			//URL url = (applet != null) ? (new URL (applet.getCodeBase()+"/actr/models/"+name+".txt"))
			URL url = new File ("bin/actr/models/"+groupName+"/"+name+".txt").toURI().toURL();
			currentModel = Model.load (url, name, currentTask, main);
			if (currentModel == null) return;
			currentTask.setModel (currentModel);
			modelArea.setPage (url);
			main.repaint();
		} catch (Exception e) { e.printStackTrace(); }
	}

	void resetCurrentModel ()
	{
		if (currentModel == null) return;
		String name = currentModel.getName();
		currentModel = Model.parse (modelArea.getText(), name, currentTask, main);
		if (currentModel == null) return;
		currentTask.setModel (currentModel);
		main.repaint();
	}

	void disableRun ()
	{
		runRealTimeButton.setEnabled(false);
		runBatchButton.setEnabled(false);
		runAllButton.setEnabled(false);
		buffersButton.setEnabled(false);
		whynotButton.setEnabled(false);
		stopButton.setEnabled(true);
	}

	void enableRun ()
	{
		runRealTimeButton.setEnabled(true);
		runBatchButton.setEnabled(true);
		runAllButton.setEnabled(true);
		buffersButton.setEnabled(true);
		whynotButton.setEnabled(true);
		stopButton.setEnabled(false);
	}

	public void output (String s)
	{
		outputArea.append (s + "\n");
		outputArea.setCaretPosition (outputArea.getDocument().getLength());
	}

	public static Image getImage (final String name)
	{
		Image image = null;
		if (applet != null)
		{
			image = applet.getImage (applet.getCodeBase(), name);
		}
		else
		{
			URL url = Main.class.getResource (name);
			image = Toolkit.getDefaultToolkit().getImage (url);
		}
		return image;
	}
}
