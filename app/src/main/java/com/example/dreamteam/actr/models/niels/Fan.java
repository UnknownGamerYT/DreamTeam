package com.example.dreamteam.actr.models.niels;

import actr.task.*;
import java.util.Random;


public class Fan extends actr.task.Task
{
	TaskLabel labels[];
	TaskLabel personLabel, locationLabel;
	boolean lastCorrect = false;

	String[][] tuples = {{"lawyer", "store", "t"}, {"captain", "cave", "t"}, {"hippie", "church", "t"}, 
			{"debutante", "bank", "t"}, {"earl", "castle", "t"}, {"hippie", "bank", "t"}, 
			{"fireman", "park", "t"}, {"captain", "park", "t"}, {"hippie", "park", "t"}, 
			{"fireman", "store", "nil"}, {"captain", "store", "nil"}, {"giant", "store", "nil"}, 
			{"fireman", "bank", "nil"}, {"captain", "bank", "nil"}, {"giant", "bank", "nil"}, 
			{"lawyer", "park", "nil"}, {"earl", "park", "nil"}, {"giant", "park", "nil"}};
	
	double trialStart = 0;
	int currentTuple = 0;
	String response = null;
	double responseTime = 0;
	FanResult currentResult;

	double observedTimes[] = {1.11, 1.17, 1.22, 1.17, 1.20, 1.22, 1.15, 1.23, 1.36,
			1.20, 1.22, 1.26, 1.25, 1.36, 1.29, 1.26, 1.47, 1.47};
	double modelTimes[];
	double score;

	class FanResult extends actr.task.Result
	{
		boolean correct[] = new boolean[tuples.length];
		double rts[] = new double[tuples.length];
	}

	public Fan ()
	{
		super ("Fan");
		setLayout (null);

		String words[] = {"The", "person", "is", "in", "the", "location"};
		labels = new TaskLabel[words.length];
		int x = 15;
		for (int i=0 ; i<words.length ; i++)
		{
			TaskLabel label = new TaskLabel (words[i], x, 150, 75, 20);
			add (label);
			if (words[i].equals("person")) personLabel = label;
			else if (words[i].equals("location")) locationLabel = label;
			x += 75;
			labels[i] = label;
		}
	}

	public int numberOfSimulations () { return 10; }

	boolean lastRunUsedPerson = false;

	public void start ()
	{
		int studyIterations = 10;

		response = null;
		responseTime = 0;
		currentResult = new FanResult();

		double time = 1.0;
		double spc = 15.0;

		addInstruction (time, "to study-sentences");
		addInstruction (time+=spc, "wait-for visual-change");
		addInstruction (time+=spc, "read person", personLabel);
		addInstruction (time+=spc, "read location", locationLabel);
		addInstruction (time+=spc, "memorize state");
		addInstruction (time+=spc, "repeat");

		addInstruction (time+=spc, "to recall-sentences");
		addInstruction (time+=spc, "wait-for visual-change");
		if (lastRunUsedPerson)
		{
			addInstruction (time+=spc, "read person", personLabel);
			addInstruction (time+=spc, "read location", locationLabel);
			addInstruction (time+=spc, "recall location for person as recalled-location");
			addInstruction (time+=spc, "if same location recalled-location type k");
			addInstruction (time+=spc, "if different location recalled-location type d");
		}
		else
		{
			addInstruction (time+=spc, "read person", personLabel);
			addInstruction (time+=spc, "read location", locationLabel);
			addInstruction (time+=spc, "recall person for location as recalled-person");
			addInstruction (time+=spc, "if same person recalled-person type k");
			addInstruction (time+=spc, "if different person recalled-person type d");
		}
		lastRunUsedPerson = !lastRunUsedPerson;
		addInstruction (time+=spc, "repeat");

		addInstruction (time+=spc, "start study-sentences");

		//double savedspc = spc;
		spc = 5.0;

		int[] studyIndices = {0,1,2,3,4,5,6,7,8};
		for (int i=0 ; i<studyIterations ; i++)
		{
			randomize (studyIndices);
			for (int j=0 ; j<studyIndices.length ; j++)
			{
				final int index = studyIndices[j];
				addEvent (new actr.core.Event (time+=spc, "task", "update") {
					public void action() {
						personLabel.setText (tuples[index][0]);
						locationLabel.setText (tuples[index][1]);
						processDisplay();
					}
				});
			}
		}

		//spc = savedspc;

		addInstruction (time+=spc, "start recall-sentences");

		int[] testIndices = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17};
		randomize (testIndices);
		for (int j=0 ; j<tuples.length ; j++)
		{
			final int index = testIndices[j];
			addEvent (new actr.core.Event (time+=spc, "task", "update") {
				public void action() {
					trialStart = model.getTime();
					currentTuple = index;
					personLabel.setText (tuples[index][0]);
					locationLabel.setText (tuples[index][1]);
					processDisplay();
				}
			});
			addEvent (new actr.core.Event (time+spc-1.0, "task", "update") {
				public void action() {
					if (response == null) currentResult.correct[index] = false;
					else
					{
						currentResult.correct[index] =
							(tuples[index][2].equals("t")) ? response.equals("k") : response.equals("d");
							currentResult.rts[index] = responseTime;
					}
					response = null;
					responseTime = 0;
				}
			});
		}
	}

	Random r = new Random (System.currentTimeMillis());
	
	public void randomize (int a[])
	{
		for (int i=0 ; i<a.length-1 ; i++)
		{
			int tmp = a[i];
			int index = i + 1 + r.nextInt (a.length - i - 1);
			a[i] = a[index];
			a[index] = tmp;
		}
	}

	public FanResult getResult ()
	{
		return currentResult;
	}

	public void analyze (Result[] results)
	{
		modelTimes = new double[tuples.length];
		double counts[] = new double[tuples.length];
		for (int n=0 ; n<results.length ; n++)
		{
			FanResult result = (FanResult) results[n];
			for (int i=0 ; i<result.rts.length ; i++)
				if (result.correct[i])
				{
					modelTimes[i] += result.rts[i];
					counts[i] += 1;
				}
		}
		for (int i=0 ; i<tuples.length ; i++)
			modelTimes[i] = (counts[i]==0) ? 0 : (modelTimes[i]/counts[i]);
		score = actr.env.Statistics.correlation (modelTimes, observedTimes);
	}

	public void printResults ()
	{
		model.output ("\n=====\n");
		for (int i=0 ; i<tuples.length ; i+=3)
		{
			String output = String.format ("%.3f\t%.3f\t%.3f",
					modelTimes[i], modelTimes[i+1], modelTimes[i+2]);
			model.output (output);
		}
		model.output ("Score = " + String.format("%.2f", score));
	}

	public void typeKey (char c)
	{
		response = c + "";
		responseTime = model.getTime() - trialStart;
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
