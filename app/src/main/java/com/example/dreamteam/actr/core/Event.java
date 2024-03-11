package com.example.dreamteam.actr.core;

public abstract class Event implements Comparable<Event>
{
	double time;
	String module;
	String description;
	long unique;

	static long counter = 0;

	public Event (double time, String module, String description)
	{
		this.time = time;
		this.module = module;
		this.description = description;
		unique = counter++;
	}
	
	double getTime() { return time; }
	String getModule() { return module; }
	String getDescription() { return description; }

	public int compareTo (Event e2)
	{
		if (time < e2.time) return -1;
		else if (time > e2.time) return +1;
		else if (module.equals("task") && !e2.module.equals("task")) return -1;
		else if (!module.equals("task") && e2.module.equals("task")) return +1;
		else if (unique < e2.unique) return -1;
		else if (unique > e2.unique) return +1;
		else return 0;
		// 0 means events will be collapsed together!
	}

	public abstract void action ();

	public String toString()
	{
		return "[event: " + time + " " + module + "," + description + "]";
	}
}
