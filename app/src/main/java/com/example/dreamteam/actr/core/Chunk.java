package com.example.dreamteam.actr.core;

import java.util.*;

class Chunk
{
	Symbol name;
	boolean isRequest;
	boolean retrieved;
	double retrievalTime;

	private Model model;
	private Map<Symbol,Symbol> slots;
	private double creationTime;
	private int useCount;
	private Vector<Double> uses;
	private int fan;
	private double baseLevel, activation;

	Chunk (Symbol name, Model model)
	{
		this.name = name;
		this.model = model;
		slots = new HashMap<Symbol,Symbol>();
		creationTime = model.getTime();
		isRequest = false;
		useCount = 0;
		uses = new Vector<Double>();
		fan = 1;
		baseLevel = 0;
		activation = 0;
		retrieved = false;
		retrievalTime = 0;
	}

	Chunk copy ()
	{
		Chunk c2 = new Chunk (Symbol.getUnique(name.name), model);
		Iterator<Symbol> it = slots.keySet().iterator();
		while (it.hasNext())
		{
			Symbol slot = it.next();
			c2.set (slot, get(slot));
		}
		//c2.creationTime = creationTime;
		//c2.request = request;
		//c2.useCount = useCount;
		//c2.uses = (Vector<Double>) uses.clone();
		//c2.fan = fan;
		//c2.baseLevel = baseLevel;
		//c2.activation = activation;
		return c2;
	}

	Symbol get (Symbol slot)
	{
		Symbol sym = slots.get (slot);
		if (sym == null) return Symbol.nil;
		else return sym;
	}

	int numSlots ()
	{
		return slots.size();
	}

	Iterator<Symbol> getSlotNames() { return slots.keySet().iterator(); }
	Iterator<Symbol> getSlotValues() { return slots.values().iterator(); }

	void set (Symbol slot, Symbol value)
	{
		boolean adjustFan = (model.declarative.get(name) != null);

		Symbol oldValue = get (slot);
		if (adjustFan && oldValue != Symbol.nil)
		{
			Chunk oldValueChunk = model.declarative.get (oldValue);
			if (oldValueChunk != null) oldValueChunk.decreaseFan();
		}

		if (value==Symbol.nil && !slot.name.startsWith(":")) slots.remove (slot);
		else
		{
			slots.put (slot, value);
			if (adjustFan && slot!=Symbol.isa && value!=Symbol.nil)
			{
				Chunk valueChunk = model.declarative.get (value);
				if (valueChunk == null)
				{
					valueChunk = new Chunk (value, model);
					valueChunk = model.declarative.add (valueChunk);
				}
				valueChunk.increaseFan();
			}
		}
	}

	void setCreationTime (double time)
	{
		creationTime = time;
		useCount = 1;
		uses.clear();
		uses.add (new Double (time));
	}

	void setBaseLevel (double baseLevel)
	{
		if (!model.declarative.baseLevelLearning)
			this.baseLevel = baseLevel;
		else if (model.declarative.optimizedLearning)
			useCount = (int) Math.round(baseLevel);
		else
		{
			int n = (int) Math.round(baseLevel);
			for (int i=0 ; i<n ; i++)
			{
				double frac = 1.0*i/n;
				uses.add ((1.0-frac)*creationTime + frac*model.getTime());
			}
		}
	}

	boolean equals (Chunk c2)
	{
		if (numSlots()==0 && c2.numSlots()==0) return (name == c2.name);
		if (numSlots() != c2.numSlots()) return false;
		Iterator<Symbol> it = getSlotNames();
		while (it.hasNext())
		{
			Symbol slot = it.next();
			Symbol value = get (slot);
			Symbol value2 = c2.get (slot);
			if (value != value2) return false;
		}
		return true;
	}

	double getBaseLevel ()
	{
		if (!model.declarative.baseLevelLearning) return baseLevel;
		double time = model.getTime();
		if (time <= creationTime) time = creationTime+.001;
		if (model.declarative.optimizedLearning)
		{
			baseLevel = Math.log(useCount/(1-model.declarative.baseLevelDecayRate))
			- model.declarative.baseLevelDecayRate*Math.log(time-creationTime);
		}
		else
		{
			double sum = 0;
			for (int i=0 ; i<uses.size() ; i++)
			{
				double use = uses.elementAt(i).doubleValue();
				sum += Math.pow (time - use, -model.declarative.baseLevelDecayRate);
			}
			baseLevel = Math.log (sum);
		}
		return baseLevel;
	}

	int appearsInSlotsOf (Chunk c2)
	{
		int count = 0;
		Iterator<Symbol> it = c2.slots.keySet().iterator();
		while (it.hasNext())
		{
			Symbol slot = it.next();
			if (slot != Symbol.get("isa"))
			{
				Symbol value = c2.get (slot);
				if (value == name) count++;
			}
		}
		return count;
	}

	void setFan (int f) { fan=f; }
	void increaseFan () { fan++; }
	void increaseFan (int df) { fan+=df; }
	void decreaseFan () { fan--; }

	double getSji (Chunk cj, Chunk ci)
	{
		if (cj.appearsInSlotsOf(ci)==0 && cj.name!=ci.name) return 0;
		else return model.declarative.maximumAssociativeStrength - Math.log(cj.fan);
	}

	double getSpreadingActivation (Chunk goal, double totalW)
	{
		double sum = 0;
		int numGoalSlots = 0;
		Iterator<Symbol> it = goal.getSlotNames();
		while (it.hasNext())
		{
			Symbol slot = it.next();
			if (slot==Symbol.isa || slot.name.startsWith(":")) continue;
			Symbol value = goal.get (slot);
			//if (value==Symbol.nil || value.isNumber() || value.isString()) continue;
			if (value==Symbol.nil) continue;
			Chunk cj = model.declarative.get (value);
			if (cj == null) continue;
			numGoalSlots ++;
			if (model.declarative.activationTrace && getSji(cj,this)!=0)
				model.output ("***    spreading activation " + goal.name + ": "
						+ cj.name + " -> " + this.name + " ["
						+String.format("%.3f", getSji(cj,this))+"]");
			sum += getSji (cj, this);
		}
		double wji = (numGoalSlots==0) ? 0 : totalW/numGoalSlots;
		return wji * sum;
	}

	double getPartialMatch (Chunk request)
	{
		double sum = 0;
		Iterator<Symbol> it = request.slots.keySet().iterator();
		while (it.hasNext())
		{
			Symbol slot = it.next();
			if (slot == Symbol.isa) continue;
			Symbol value = request.get (slot);
			sum += model.declarative.getSimilarity (value, get(slot));
		}
		return model.declarative.mismatchPenalty * sum;
	}

	double getActivation (Chunk request)
	{
		activation = getBaseLevel();
		if (model.declarative.spreadingActivation)
		{
			if (model.declarative.goalActivation > 0)
			{
				Chunk goal = model.buffers.get (Symbol.goal);
				if (goal != null) activation += getSpreadingActivation (goal, model.declarative.goalActivation);
			}
			if (model.declarative.imaginalActivation > 0)
			{
				Chunk imaginal = model.buffers.get (Symbol.imaginal);
				if (imaginal != null) activation += getSpreadingActivation (imaginal, model.declarative.imaginalActivation);
			}
		}
		if (model.declarative.partialMatching)
			activation += getPartialMatch(request);
		if (model.declarative.activationNoiseS != 0)
			activation += Utilities.getNoise(model.declarative.activationNoiseS);
		return activation;
	}

	double getSavedActivation () { return activation; }

	int getUseCount ()
	{
		if (model.declarative.optimizedLearning) return useCount;
		else return uses.size();
	}
	
	double getCreationTime () { return creationTime; }
	
	void addUse ()
	{
		if (model.declarative.optimizedLearning) useCount++;
		else uses.add (new Double (model.getTime()));
	}

	public String toString ()
	{
		String s = "(" + name;
		Iterator<Symbol> it = slots.keySet().iterator();
		while (it.hasNext())
		{
			Symbol slot = it.next();
			Symbol value = slots.get (slot);
			s += " " + slot + " " + value;
		}
		return s + ")"; // + " [bl="+getBaseLevel()+"]"; // [fan=" + fan + "]";
	}
}
