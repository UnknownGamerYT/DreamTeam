package com.example.dreamteam.actr.models.tutorial;

public class U1Semantic extends actr.task.Task
{
	public U1Semantic ()
	{
		super ("Unit1-Semantic");
	}
	
	public boolean check ()
	{
		return (model.lastProductionFired().contains("direct-verify"));
	}
	
	public double score () { return -1; }
}
