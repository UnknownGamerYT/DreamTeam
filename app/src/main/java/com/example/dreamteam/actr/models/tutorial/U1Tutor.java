package com.example.dreamteam.actr.models.tutorial;

public class U1Tutor extends actr.task.Task
{
	public U1Tutor ()
	{
		super ("Unit1-Tutor");
	}
	
	public boolean check ()
	{
		return (model.lastProductionFired().contains("add-tens-done"));
	}
	
	public double score () { return -1; }
}
