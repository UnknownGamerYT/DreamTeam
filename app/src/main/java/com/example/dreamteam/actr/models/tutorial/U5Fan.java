package com.example.dreamteam.actr.models.tutorial;

import actr.task.Result;
import actr.task.TaskLabel;

public class U5Fan extends actr.task.Task
{
	TaskLabel personLabel, locationLabel;
	boolean lastCorrect = false;
	double lastTime = 0;
	int tupleIndex = 0;
	String[][] tuples = {{"lawyer", "store", "t"}, {"captain", "cave", "t"}, {"hippie", "church", "t"}, 
			{"debutante", "bank", "t"}, {"earl", "castle", "t"}, {"hippie", "bank", "t"}, 
			{"fireman", "park", "t"}, {"captain", "park", "t"}, {"hippie", "park", "t"}, 
			{"fireman", "store", "nil"}, {"captain", "store", "nil"}, {"giant", "store", "nil"}, 
			{"fireman", "bank", "nil"}, {"captain", "bank", "nil"}, {"giant", "bank", "nil"}, 
			{"lawyer", "park", "nil"}, {"earl", "park", "nil"}, {"giant", "park", "nil"}};

	String response = null;
	double responseTime = 0;
	MyResult currentResult;

	double observedTimes[] = {1.11, 1.17, 1.22, 1.17, 1.20, 1.22, 1.15, 1.23, 1.36,
			1.20, 1.22, 1.26, 1.25, 1.36, 1.29, 1.26, 1.47, 1.47};
	double modelTimes[];
	double score;

	class MyResult extends actr.task.Result
	{
		boolean correct[] = new boolean[tuples.length];
		double rts[] = new double[tuples.length];
	}

	public U5Fan ()
	{
		super ("Unit5-Fan");
		setLayout (null);

		String words[] = {"The", "person", "is", "in", "the", "location"};
		int x = 15; // 25;
		for (int i=0 ; i<words.length ; i++)
		{
			TaskLabel label = new TaskLabel (words[i], x, 150, 75, 20);
			add (label);
			if (words[i].equals("person")) personLabel = label;
			else if (words[i].equals("location")) locationLabel = label;
			x += 75;
		}
	}

	public int numberOfSimulations () { return 20; }

	boolean lastRunUsedPerson = false;

	public void start ()
	{
		String add = (model.getName().equals("ALL")) ? "u5fan*" : "";
		if (lastRunUsedPerson) model.runCommand ("(spp "+add+"retrieve-from-location :u 10)");
		else model.runCommand ("(spp "+add+"retrieve-from-person :u 10)");
		lastRunUsedPerson = !lastRunUsedPerson;

		lastTime = -100;
		tupleIndex = -1;
		response = null;
		responseTime = 0;
		currentResult = new MyResult();
		//randomizePairs();
		addPeriodicUpdate (.020);
	}

	public void update (double time)
	{
		if (lastTime+29.999 <= time)
		{
			if (tupleIndex >= 0)
			{
				if (response == null) currentResult.correct[tupleIndex] = false;
				else
				{
					currentResult.correct[tupleIndex] = (tuples[tupleIndex][2].equals("t")) ? response.equals("k") : response.equals("d");
					currentResult.rts[tupleIndex] = responseTime;
				}
				response = null;
				responseTime = 0;
			}
			tupleIndex++;
			if (tupleIndex >= tuples.length) model.stop();
			else
			{
				personLabel.setText (tuples[tupleIndex][0]);
				locationLabel.setText (tuples[tupleIndex][1]);
				processDisplay();
				lastTime = time;
			}
		}
	}

	public MyResult getResult ()
	{
		return currentResult;
	}

	public void analyze (Result[] results)
	{
		modelTimes = new double[tuples.length];
		double counts[] = new double[tuples.length];
		for (int n=0 ; n<results.length ; n++)
		{
			MyResult result = (MyResult) results[n];
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
		responseTime = model.getTime() - lastTime;
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
