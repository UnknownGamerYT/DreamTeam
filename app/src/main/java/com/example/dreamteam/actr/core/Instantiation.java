package com.example.dreamteam.actr.core;

import java.util.*;

class Instantiation
{
	private Production p;
	private double time;
	private double u;
	private Map<Symbol,Symbol> mapping;
	
	Instantiation (Production p, double time, double u)
	{
		this.p = p;
		this.time = time;
		this.u = u;
		mapping = new HashMap<Symbol,Symbol>();
	}
	
	Instantiation copy ()
	{
		Instantiation newi = new Instantiation (p, time, u);
		Iterator<Symbol> it = mapping.keySet().iterator();
		while (it.hasNext())
		{
			Symbol variable = it.next();
			Symbol chunk = get(variable);
			newi.set (variable, chunk);
		}
		return newi;
	}
	
	void set (Symbol variable, Symbol chunk)
	{
		mapping.put (variable, chunk);
	}
	
	Symbol get (Symbol variable)
	{
		return mapping.get (variable);
	}
	
	Iterator<Symbol> getVariables ()
	{
		return mapping.keySet().iterator();
	}
	
	void replaceVariable (Symbol var, Symbol var2)
	{
		Symbol previousValue = mapping.remove (var);
		if (previousValue!=null) set (var2, previousValue);
	}
	
	void replaceValue (Symbol value1, Symbol value2)
	{
		Iterator<Symbol> it = mapping.keySet().iterator();
		while (it.hasNext())
		{
			Symbol variable = it.next();
			Symbol chunk = get(variable);
			if (chunk==value1) set (variable, value2);
		}
	}
	
	Production getProduction () { return p; }
	double getUtility () { return u; }
	double getTime () { return time; }
	
	public String toString ()
	{
		String s = "<inst " + p.name + " ";
		Iterator<Symbol> it = mapping.keySet().iterator();
		while (it.hasNext())
		{
			Symbol v = it.next();
			Symbol c = get(v);
			s += " " + v + "->" + c;
		}
		return s+">";
	}
}
