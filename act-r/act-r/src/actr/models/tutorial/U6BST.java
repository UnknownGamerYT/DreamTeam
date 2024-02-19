package actr.models.tutorial;

import java.util.Iterator;

import actr.task.*;


public class U6BST extends actr.task.Task
{
	U6BST panel;
	TaskButton buttonA, buttonB, buttonC, buttonReset;
	TaskLine lineA, lineB, lineC, lineTarget, line;
	TaskLabel doneLabel;
	String choice = null;
	boolean done = false;
	U6BSTResult currentResult;

	int stimuli[][] = {{15, 250, 55, 125}, {10, 155, 22, 101}, {14, 200, 37, 112}, {22, 200, 32, 114},
			{10, 243, 37, 159}, {22, 175, 40, 73}, {15, 250, 49, 137}, {10, 179, 32, 105},
			{20, 213, 42, 104}, {14, 237, 51, 116}, {12, 149, 30, 72}, {14, 237, 51, 121},
			{22, 200, 32, 114}, {14, 200, 37, 112}, {15, 250, 55, 125}};
	int stimIndex = 0;

	double observedCounts[] = {20.0, 67.0, 20.0, 47.0, 87.0, 20.0, 80.0, 93.0,
			83.0, 13.0, 29.0, 27.0, 80.0, 73.0, 53.0};
	double modelCounts[];
	double utilities[] = new double[4];
	double score;

	class U6BSTResult extends actr.task.Result
	{
		String responses[] = new String[stimuli.length];
		double utilities[] = new double[4];
	}

	public U6BST ()
	{
		super ("Unit6-BST");
		panel = this;
		setLayout (null);

		buttonA = new TaskButton ("A", 10, 25, 20, 20) {
			public void doClick() {
				if (choice==null) choice = "under";
				changeLine ((line.getWidth() > lineTarget.getWidth()) ? -lineA.getWidth() : lineA.getWidth());
			}
		};
		add (buttonA);

		buttonB = new TaskButton ("B", 10, 50, 20, 20) {
			public void doClick() {
				if (choice==null) choice = "over";
				changeLine ((line.getWidth() > lineTarget.getWidth()) ? -lineB.getWidth() : lineB.getWidth());
			}
		};
		add (buttonB);

		buttonC = new TaskButton ("C", 10, 75, 20, 20) {
			public void doClick() {
				if (choice==null) choice = "under";
				changeLine ((line.getWidth() > lineTarget.getWidth()) ? -lineC.getWidth() : lineC.getWidth());
			}
		};
		add (buttonC);

		buttonReset = new TaskButton ("Reset", 10, 125, 40, 20) {
			public void doClick() {
				changeLine (-line.getWidth());
				panel.repaint();
			}
		};
		add (buttonReset);

		lineA = new TaskLine (50, 35, 10, 1);
		add (lineA);
		lineB = new TaskLine (50, 60, 40, 1);
		add (lineB);
		lineC = new TaskLine (50, 85, 20, 1);
		add (lineC);
		lineTarget = new TaskLine (50, 110, 0, 1);
		add (lineTarget);
		line = new TaskLine (50, 135, 0, 1);
		add (line);

		doneLabel = new TaskLabel ("done", 180, 200, 35, 20);
		doneLabel.setVisible (false);
		add (doneLabel);
	}

	public int numberOfSimulations () { return 20; }

	public void start ()
	{
		currentResult = new U6BSTResult();
		stimIndex = -1;
		addUpdate (0);
	}

	void changeLine (int delta)
	{
		line.changeWidth (delta);
		if (line.getWidth() == lineTarget.getWidth())
		{
			done = true;
			doneLabel.setVisible (true);
			addUpdate (5.0);
		}
		processDisplay();
		panel.repaint();
	}

	public void update (double time)
	{
		if (stimIndex >= 0) currentResult.responses[stimIndex] = choice;
		stimIndex++;
		if (stimIndex < stimuli.length)
		{
			lineA.setWidth (stimuli[stimIndex][0]);
			lineB.setWidth (stimuli[stimIndex][1]);
			lineC.setWidth (stimuli[stimIndex][2]);
			lineTarget.setWidth (stimuli[stimIndex][3]);
			line.setWidth (0);
			choice = null;
			done = false;
			doneLabel.setVisible (false);
			panel.repaint();
			processDisplay();
		}
		else model.stop();
	}
	
	public void eval (Iterator<String> it)
	{
		String prefix = (model.getProductionUtility("decide-over")==0) ? "u6bst*" : "";
		String s = "***** ( ";
		s += model.getProductionUtility (prefix+"decide-over") + " ";
		s += model.getProductionUtility (prefix+"decide-under") + " ";
		s += model.getProductionUtility (prefix+"force-over") + " ";
		s += model.getProductionUtility (prefix+"force-under") + " ";
		s += ")";
		model.output(s);
	}

	public U6BSTResult getResult ()
	{
		String prefix = (model.getProductionUtility("decide-over")==0) ? "u6bst*" : "";
		currentResult.utilities[0] = model.getProductionUtility (prefix+"decide-over");
		currentResult.utilities[1] = model.getProductionUtility (prefix+"decide-under");
		currentResult.utilities[2] = model.getProductionUtility (prefix+"force-over");
		currentResult.utilities[3] = model.getProductionUtility (prefix+"force-under");
		return currentResult;
	}

	public void analyze (Result[] results)
	{
		modelCounts = new double[stimuli.length];
		for (int n=0 ; n<results.length ; n++)
		{
			U6BSTResult result = (U6BSTResult) results[n];
			for (int i=0 ; i<stimuli.length ; i++)
				if (result.responses[i] != null)
					modelCounts[i] += (result.responses[i].equals("over")) ? 1 : 0;
			for (int i=0 ; i<4 ; i++)
				utilities[i] += result.utilities[i];
		}
		for (int i=0 ; i<stimuli.length ; i++)
			modelCounts[i] = 100.0 * (modelCounts[i] / results.length);
		for (int i=0 ; i<4 ; i++)
			utilities[i] /= results.length;
		score = actr.env.Statistics.correlation (observedCounts, modelCounts);
	}

	public void printResults ()
	{
		model.output ("\n=====\n");
		String s1="", s2="";
		for (int i=0 ; i<stimuli.length ; i++)
		{
			s1 += (i+1) + "\t";
			s2 += String.format ("%.1f\t", modelCounts[i]);
		}
		model.output (s1 + "\n" + s2 + "\n");
		model.output ("Score = " + String.format("%.2f", score));
		String s="decide-over"; model.output (String.format ("%-15s : %.3f", s, utilities[0]));
		s="decide-under"; model.output (String.format ("%-15s : %.3f", s, utilities[1]));
		s="force-over"; model.output (String.format ("%-15s : %.3f", s, utilities[2]));
		s="force-under"; model.output (String.format ("%-15s : %.3f", s, utilities[3]));
	}

	public boolean check ()
	{
		return score>0;
	}

	public double score ()
	{
		return score;
	}
}
