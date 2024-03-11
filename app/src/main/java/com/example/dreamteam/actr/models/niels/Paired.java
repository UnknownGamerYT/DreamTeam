package com.example.dreamteam.actr.models.niels;

import actr.task.*;
import actr.env.*;

import java.util.*;

public class Paired extends actr.task.Task
{
	TaskLabel label;
	double lastTime = 0;
	int pairIndex = 0, pairItem = 0;
	String[][] pairs = {{"bank", "0"}, {"card", "1"}, {"dart", "2"}, {"face", "3"}, {"game", "4"},
			{"hand", "5"}, {"jack", "6"}, {"king", "7"}, {"lamb", "8"}, {"mask", "9"},
			{"neck", "0"}, {"pipe", "1"}, {"quip", "2"}, {"rope", "3"}, {"sock", "4"},
			{"tent", "5"}, {"vent", "6"}, {"wall", "7"}, {"xray", "8"}, {"zinc", "9"}};
	int iteration = 0;
	final int runIterations = 8;
	
	String response = null;
	double responseTime = 0;
	PairedTrial currentTrial;
	PairedResult currentResult;
	
	double observedTimes[] = {0.0, 2.158, 1.967, 1.762, 1.680, 1.552, 1.467, 1.402};
	double observedCorrect[] = {0.000, .526, .667, .798, .887, .924, .958, .954};
	double modelTimes[], modelCorrect[];
	double score = 0;

	class PairedTrial
	{
		int responses = 0;
		int responsesCorrect = 0;
		double responseTotalTime = 0;
	}

	class PairedResult extends actr.task.Result
	{
		Vector<PairedTrial> trials;
		PairedResult () { trials = new Vector<PairedTrial>(); }
	}

	public Paired ()
	{
		super ("Paired");
		label = new TaskLabel ("-", 150, 150, 40, 20);
		add (label);
		label.setVisible(false);
	}

	public int numberOfSimulations () { return 3; }

	public void start ()
	{
		iteration = 0;
		lastTime = -10;
		pairIndex = 0;
		pairItem = 0;
		response = null;
		responseTime = 0;
		currentTrial = new PairedTrial();
		currentResult = new PairedResult();
		Utilities.shuffle (pairs);
		
		double time = 1.0;
		double spc = 10.0;
		
		addInstruction (time, "to respond");
		addInstruction (time+=spc, "wait-for visual-change");
		addInstruction (time+=spc, "read word", label);
		addInstruction (time+=spc, "recall number for word");
		addInstruction (time+=spc, "if recall-success type number");
		addInstruction (time+=spc, "wait-for visual-change");
		addInstruction (time+=spc, "read number", label);
		addInstruction (time+=spc, "memorize state");
		addInstruction (time+=spc, "repeat");
		
		addInstruction (time+=spc, "start respond");
		lastTime = time + 10.0;

		label.setVisible(false);
		addPeriodicUpdate (.020);
	}
	
	public void eval (Iterator<String> it)
	{
//		it.next(); // (
//		String token = it.next();
//		if (token.equals("dm"))
	}
	
	public void update (double time)
	{
		if (lastTime+4.999 <= time)
		{
			label.setVisible(true);
			String lastCorrect = label.getText();
			String item = pairs[pairIndex][pairItem];
			label.setText (item);
			processDisplay();
			pairItem++;
			if (pairItem == 1)
			{
				currentTrial.responses ++;
				if (response!=null && response.equals(lastCorrect))
				{
					currentTrial.responsesCorrect ++;
					currentTrial.responseTotalTime += responseTime;
				}
				response = null;
			}
			if (pairItem>=2)
			{
				pairIndex++;
				pairItem=0;
			}
			if (pairIndex >= pairs.length)
			{
				currentResult.trials.add (currentTrial);
				iteration++;
				if (iteration >= runIterations) model.stop();
				currentTrial = new PairedTrial();
				pairIndex = 0;
				pairItem = 0;
			}
			lastTime = time;
		}
	}

	public PairedResult getResult ()
	{
		return currentResult;
	}

	public void analyze (Result[] results)
	{
		modelTimes = new double[runIterations];
		modelCorrect = new double[runIterations];
		for (int i=0 ; i<runIterations ; i++)
		{
			double responses=0, responsesCorrect=0, responseTime=0;
			for (int j=0 ; j<results.length ; j++)
			{
				PairedResult result = (PairedResult) results[j];
				responses += result.trials.elementAt(i).responses;
				responsesCorrect += result.trials.elementAt(i).responsesCorrect;
				responseTime += result.trials.elementAt(i).responseTotalTime;
			}
			modelTimes[i] = (responsesCorrect==0) ? 0 : (responseTime/responsesCorrect);
			modelCorrect[i] = (responses==0) ? 0 : (1.0*responsesCorrect/responses);
		}
		score = 0.5 * (actr.env.Statistics.correlation (modelTimes, observedTimes)
				+ actr.env.Statistics.correlation (modelCorrect, observedCorrect));
	}

	public void printResults ()
	{
		model.output ("\n=====\n");
		for (int i=0 ; i<runIterations ; i++)
		{
			String output = String.format ("%.3f\t%.3f", modelCorrect[i], modelTimes[i]);
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
