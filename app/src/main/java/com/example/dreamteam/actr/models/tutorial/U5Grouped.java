package com.example.dreamteam.actr.models.tutorial;

import java.util.Iterator;

public class U5Grouped extends actr.task.Task
{
	String responses = "";

	public U5Grouped ()
	{
		super ("Unit5-Grouped");
	}
	
	public void start ()
	{
		responses = "";
	}

	public void eval (Iterator<String> it)
	{
		it.next(); // (
		it.next(); // record-response
		String response = it.next().replaceAll("\"", "");
		it.next(); // )
		responses += response;
	}

	public void printResults ()
	{ 
		model.output ("\n-----\n\n'" + responses + "'");
	}

	public boolean check ()
	{
		return model.getTime()>1.0 && responses.length()>0;
	}

	public double score () { return -1; }
}
