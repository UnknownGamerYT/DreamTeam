package com.example.dreamteam.actr.core;

import java.text.DecimalFormat;
import java.util.*;

class Symbol
{
	String name;

	static Map<String,Symbol> hashmap = new HashMap<String,Symbol>();
	static long unique = 1;
	
	static final Symbol t = Symbol.get ("t");
	static final Symbol nil = Symbol.get ("nil");
	
	static final Symbol isa = Symbol.get("isa");

	static final Symbol goal = Symbol.get("goal");
	static final Symbol retrieval = Symbol.get("retrieval");
	static final Symbol retrievalState = Symbol.get("?retrieval");
	static final Symbol visloc = Symbol.get ("visual-location");
	static final Symbol vislocState = Symbol.get ("?visual-location");
	static final Symbol visual = Symbol.get ("visual");
	static final Symbol visualState = Symbol.get ("?visual");
	static final Symbol aurloc = Symbol.get ("aural-location");
	static final Symbol aurlocState = Symbol.get ("?aural-location");
	static final Symbol aural = Symbol.get ("aural");
	static final Symbol auralState = Symbol.get ("?aural");
	static final Symbol manual = Symbol.get("manual");
	static final Symbol manualState = Symbol.get("?manual");
	static final Symbol vocal = Symbol.get("vocal");
	static final Symbol vocalState = Symbol.get("?vocal");
	static final Symbol imaginal = Symbol.get("imaginal");
	static final Symbol imaginalState = Symbol.get("?imaginal");
	
	static final Symbol buffer = Symbol.get("buffer");
	static final Symbol state = Symbol.get("state");
	static final Symbol preparation = Symbol.get("preparation");
	static final Symbol processor = Symbol.get("processor");
	static final Symbol execution = Symbol.get("execution");
	static final Symbol free = Symbol.get("free");
	static final Symbol busy = Symbol.get("busy");
	static final Symbol empty = Symbol.get("empty");
	static final Symbol full = Symbol.get("full");
	static final Symbol requested = Symbol.get("requested");
	static final Symbol unrequested = Symbol.get("unrequested");
	static final Symbol error = Symbol.get("error");
	
	static final Symbol kind = Symbol.get("kind");
	static final Symbol screenx = Symbol.get("screen-x");
	static final Symbol screeny = Symbol.get("screen-y");
	static final Symbol screenpos = Symbol.get("screen-pos");
	static final Symbol value = Symbol.get("value");
	static final Symbol width = Symbol.get("width");
	static final Symbol height = Symbol.get("height");
	static final Symbol distance = Symbol.get("distance");
	static final Symbol lowest = Symbol.get("lowest");
	static final Symbol highest = Symbol.get("highest");
	static final Symbol current = Symbol.get("current");
	
	static final Symbol tone = Symbol.get("tone");
	static final Symbol word = Symbol.get("word");
	static final Symbol digit = Symbol.get("digit");
	static final Symbol location = Symbol.get("location");
	static final Symbol event = Symbol.get("event");
	static final Symbol content = Symbol.get("content");
	
	static final Symbol hand = Symbol.get("hand");
	static final Symbol finger = Symbol.get("finger");
	static final Symbol left = Symbol.get("left");
	static final Symbol right = Symbol.get("right");
	
	Symbol (String name)
	{
		this.name = name;
	}

	public static Symbol get (String name)
	{
		if (name==null) return nil;
		Symbol sym = hashmap.get (name);
		if (sym == null)
		{
			sym = new Symbol (name);
			hashmap.put (name, sym);
		}
		return sym;
	}

	static DecimalFormat df = new DecimalFormat("#0.####");
 
	public static Symbol get (int x) { return get (Integer.toString(x)); }
	public static Symbol get (double x) { return get (df.format(x)); }
	public static Symbol get (boolean b) { if (b) return t; else return nil; }

	public static Symbol getUnique (String name)
	{
		if (name==null) name = "nil";
		Symbol sym;
		while ((sym = hashmap.get (name + "-" + unique++)) != null) ;
		name = name + "-" + unique;
		sym = new Symbol (name);
		hashmap.put (name, sym);
		return sym;
	}

	public boolean isVariable ()
	{
		return name.length()>1 && name.charAt(0)=='=';
	}
	
	public boolean isNumber ()
	{
		try { toDouble(); return true; }
		catch (Exception e) { return false; }
	}
	
	public boolean isString ()
	{
		return name.length()>0 && name.charAt(0)=='"';
	}
	
	public double toDouble ()
	{
		return Double.valueOf(name).doubleValue();
	}
	
	public int toInt ()
	{
		return (int) Math.round(toDouble());
	}

	public boolean toBool ()
	{
		return (this != nil);
	}

	public String toString ()
	{
		return name;
	}
}
