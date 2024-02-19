package actr.core;

import java.util.*;

class Events
{
	TreeSet<Event> events;
	
	Events ()
	{
		events = new TreeSet<Event>();
	}
	
	boolean moreEvents ()
	{
		return !events.isEmpty();
	}
	
	Event next ()
	{
		Event e = events.first();
		events.remove (e);
		return e;
	}
	
	double nextTime ()
	{
		Event e = events.first();
		return e.time;
	}
	
	void add (Event event)
	{
		events.add (event);
	}
	
	boolean scheduled (String module)
	{
		Iterator<Event> it = events.iterator();
		while (it.hasNext())
			if (it.next().module.equals(module)) return true;
		return false;
	}
	
	void removeModuleEvents (String module)
	{
		Set<Event> moduleEvents = new HashSet<Event>();
		Iterator<Event> it = events.iterator();
		while (it.hasNext())
		{
			Event e = it.next();
			if (e.module.equals(module)) moduleEvents.add(e);
		}
		events.removeAll (moduleEvents);
	}
	
	public String toString ()
	{
		String s = "Events:\n";
		Iterator<Event> it = events.iterator();
		while (it.hasNext())
			s += it.next() + "\n";
		return s + "\n";
	}
}
