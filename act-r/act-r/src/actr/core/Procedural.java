package actr.core;

import java.util.*;

class Procedural extends Module
{
	private Model model;
	private Map<Symbol,Production> productions;
	private Vector<Instantiation> rewardFirings;
	private Instantiation lastFired;

	boolean utilityLearning = false;
	double utilityNoiseS = 0;
	double utilityLearningAlpha = 0.2;
	boolean productionLearning = false;
	double initialUtility = 0;
	double productionCompilationThresholdTime = 2.0;
	double productionLearningNewUtility = 0;

	boolean conflictSetTrace = false;
	boolean whyNotTrace = false;
	boolean productionCompilationTrace = false;

	Procedural (Model model)
	{
		this.model = model;
		productions = new HashMap<Symbol,Production>();
		rewardFirings = new Vector<Instantiation>();
		lastFired = null;
	}

	void add (Production p)
	{
		if (productions.get(p.name) != null)
		{
			model.warning ("redefining production "+p.name+"; renaming second production");
			p.name = Symbol.getUnique(p.name.name);
		}
		productions.put (p.name, p);
	}

	Production get (Symbol sym) { return productions.get (sym); }
	
	int numProductions () { return productions.size(); }
	
	Production exists (Production p)
	{
		Iterator<Production> it = productions.values().iterator();
		while (it.hasNext())
		{
			Production ip = it.next();
			if (ip.equals(p)) return ip;
		}
		return null;
	}
	
	Instantiation getLastFired() { return lastFired; }

	void findInstantiations (final Buffers buffers)
	{
		//if (model.verboseTrace) model.output ("procedural", "conflict-resolution");
		HashSet<Instantiation> set = new HashSet<Instantiation>();
		Iterator<Production> it = productions.values().iterator();
		while (it.hasNext())
		{
			Production p = it.next();
//model.output ("---"+model.getTime()+"--- trying "+p.name);
			Instantiation inst = p.instantiate (buffers);
			if (inst != null) set.add (inst);
		}
//model.output ("---"+model.getTime()+"--- done");

		if (!set.isEmpty())
		{
			if (conflictSetTrace) model.output ("Conflict Set:");
			Iterator<Instantiation> itInst = set.iterator();
			Instantiation highestU = itInst.next();
			if (conflictSetTrace) model.output ("* (" + highestU.getUtility() + ") " + highestU);
			while (itInst.hasNext())
			{
				Instantiation inst = itInst.next();
				if (conflictSetTrace) model.output ("* (" + inst.getUtility() + ") " + inst);
				if (inst.getUtility() > highestU.getUtility()) highestU = inst;
			}

			final Instantiation finalInst = highestU;
			if (conflictSetTrace) model.output ("-> (" + finalInst.getUtility() + ") " + finalInst);

			model.addEvent (new Event (model.getTime() + .050,
					"procedural", "** " + finalInst.getProduction().name.name.toUpperCase() + " **")
			{
				public void action() {
					fire (finalInst, buffers);
					findInstantiations (buffers);
				}
			});
		}
	}

	void fire (Instantiation inst, Buffers buffers)
	{
		inst.getProduction().fire (inst);
		model.update();

		if (productionLearning && lastFired!=null)
		{
			Compilation comp = new Compilation (lastFired, inst, model);
			Production newp = comp.compile();
			if (newp!=null)
			{
				Production oldp = exists (newp);
				if (oldp != null)
				{
					double alpha = utilityLearningAlpha;
					oldp.setUtility (oldp.getUtility() + alpha*(lastFired.getProduction().getUtility() - oldp.getUtility()));
					if (productionCompilationTrace)
						model.output ("*** (pct) strengthening " + oldp.name + " [u="
								   	  + String.format ("%.3f", oldp.getUtility()) + "]");
				}
				else
				{
					model.procedural.add (newp);
					if (productionCompilationTrace)
					{
						model.output ("\n*** (pct)\n");
						model.output (""+lastFired.getProduction());
						model.output (""+inst.getProduction());
						model.output (""+newp);
						//model.output ("*** (pct) new production:\n"+newp);
					}
				}
			}
		}
		lastFired = inst;

		rewardFirings.add (inst);
		if (utilityLearning && inst.getProduction().hasReward())
		{
			adjustUtilities (inst.getProduction().getReward());
			rewardFirings.clear();
		}
	}

	void adjustUtilities (double reward)
	{
		double alpha = utilityLearningAlpha;
		for (int i=0 ; i<rewardFirings.size() ; i++)
		{
			Instantiation inst = rewardFirings.elementAt(i);
			Production p = inst.getProduction();
			double pReward = reward - (model.getTime() - inst.getTime());
			p.setUtility (p.getUtility() + alpha*(pReward - p.getUtility()));
			//model.output ("*** "+ p.name + " : " + p.getUtility());
		}
	}

	public String toString ()
	{
		String s = "";
		Iterator<Production> it = productions.values().iterator();
		while (it.hasNext()) s += it.next() + "\n";
		return s;
	}
}
