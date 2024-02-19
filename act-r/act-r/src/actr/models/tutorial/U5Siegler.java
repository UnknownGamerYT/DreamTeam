package actr.models.tutorial;

import actr.task.Result;

public class U5Siegler extends actr.task.Task
{
	int tupleIndex = 0;
	int[][] tuples = {{1,1}, {1,2}, {1,3}, {2,2}, {2,3}, {3,3}};
	double lastTime = 0;
	String response;
	U5SieglerResult currentResult;

	double observedCounts[][] = {{0, .05, .86, 0, .02, 0, .02, 0, 0, .06},
			{0, .04, .07, .75, .04, 0, .02, 0, 0, .09},
			{0, .02, 0, .10, .75, .05, .01, .03, 0, .06},
			{.02, 0, .04, .05, .80, .04, 0, .05, 0, 0},
			{0, 0, .07, .09, .25, .45, .08, .01, .01, .06},
			{.04, 0, 0, .05, .21, .09, .48, 0, .02, .11}};
	double modelCounts[][];
	double score;

	class U5SieglerResult extends actr.task.Result
	{
		String responses[] = new String[tuples.length];
	}

	public U5Siegler ()
	{
		super ("Unit5-Siegler");
	}

	public int numberOfSimulations () { return 100; }

	public void start ()
	{
		tupleIndex = -1;
		response = null;
		lastTime = -100;
		currentResult = new U5SieglerResult();
		addPeriodicUpdate (.25);
	}

	public void update (double time)
	{
		if (lastTime+29.999 <= time)
		{
			if (tupleIndex >= 0)
			{
				currentResult.responses[tupleIndex] = response;
				response = "";
			}
			tupleIndex++;
			if (tupleIndex >= tuples.length) model.stop();
			else
			{
				addAural (0.00, "arg1", "sound", ""+tuples[tupleIndex][0]);
				addAural (0.75, "arg2", "sound", ""+tuples[tupleIndex][1]);
			}
			lastTime = time;
		}
	}

	public U5SieglerResult getResult ()
	{
		return currentResult;
	}

	String numbers[] = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "other"};
	int getIndex (String s) { for (int i=0; i<numbers.length; i++) if (numbers[i].equals(s)) return i; return numbers.length-1; }

	public void analyze (Result[] results)
	{
		modelCounts = new double[tuples.length][numbers.length];
		int totals[] = new int[tuples.length];
		for (int n=0 ; n<results.length ; n++)
		{
			U5SieglerResult result = (U5SieglerResult) results[n];
			for (int i=0 ; i<result.responses.length ; i++)
			{
				int index = getIndex(result.responses[i]);
				modelCounts[i][index] += 1;
				totals[i] ++;
			}
		}
		for (int i=0 ; i<tuples.length ; i++)
			for (int j=0 ; j<numbers.length ; j++)
				if (totals[i] > 0) modelCounts[i][j] /= totals[i];
				else modelCounts[i][j] = 0;
		score = actr.env.Statistics.correlation (observedCounts, modelCounts);
	}

	public void printResults ()
	{
		model.output ("\n=====\n");
		for (int i=0 ; i<tuples.length ; i++)
		{
			String s = "";
			for (int j=0 ; j<numbers.length ; j++)
				s += String.format ("%.2f\t", modelCounts[i][j]);
			model.output (s);
		}
		model.output ("Score = " + String.format("%.2f", score));
	}

	public void speak (String s)
	{
		response = s;
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
