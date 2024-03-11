package com.example.dreamteam.actr.core;

import java.util.*;

class Declarative extends Module
{
	private Model model;
	private Map<Symbol,Chunk> chunks;
	private Map<String,Double> similarities;
	private Vector<Chunk> finsts;
//	private double lastCleanup = 0;

	double retrievalThreshold = 0.0;
	double latencyFactor = 1.0;
	boolean baseLevelLearning = false;
	double baseLevelDecayRate = 0.5;
	boolean optimizedLearning = true;
	double activationNoiseS = 0;
	double goalActivation = 1.0;
	double imaginalActivation = 0;
	boolean spreadingActivation = false;
	double maximumAssociativeStrength = 0;
	boolean partialMatching = false;
	double mismatchPenalty = 0;
	int declarativeNumFinsts = 4;
	double declarativeFinstSpan = 3.0;
	boolean activationTrace = false;
	
	boolean addChunkOnNewRequest = true;

	Declarative (Model model)
	{
		this.model = model;
		chunks = new HashMap<Symbol,Chunk>();
		similarities = new HashMap<String,Double>();
		finsts = new Vector<Chunk>();
//		lastCleanup = 0;
	}

	Chunk add (Chunk chunk)
	{
		if (get(chunk.name) != null) return chunk;
		//		{
		//			Chunk existingChunk = get(chunk.name);
		//			existingChunk.addUse();
		//			return existingChunk;
		//		}

		Iterator<Chunk> it = chunks.values().iterator();
		while (it.hasNext())
		{
			Chunk existingChunk = it.next();
			
			if (chunk.toString().contains("location \"park\" type study-sentences person \"lawyer\"")
					&& existingChunk.toString().contains("location \"park\" type study-sentences person \"lawyer\""))
				model.output ("^^^ "+chunk.equals(existingChunk)+"\n"+chunk+"\n"+existingChunk);
			
			if (chunk.equals (existingChunk))
			{
				existingChunk.addUse();
				model.buffers.replaceSlotValues (chunk, existingChunk);
				return existingChunk;
			}
		}

		chunk.setFan (1);
		it = chunks.values().iterator();
		while (it.hasNext())
		{
			Chunk existingChunk = it.next();
			chunk.increaseFan (chunk.appearsInSlotsOf (existingChunk));
		}

		Iterator<Symbol> it2 = chunk.getSlotValues();
		while (it2.hasNext())
		{
			Chunk valueChunk = get(it2.next());
			if (valueChunk != null) valueChunk.increaseFan();
		}

		chunk.setCreationTime (model.getTime());
		chunks.put (chunk.name, chunk);
		return chunk;
	}

	Chunk get (Symbol name) { return chunks.get (name); }

	int numChunks () { return chunks.size(); }

	Chunk findRetrieval (Chunk request)
	{
		HashSet<Chunk> matches = new HashSet<Chunk>();
		if (activationTrace) model.output ("*** finding retrieval for request " + request);

		Iterator<Chunk> it = chunks.values().iterator();
		while (it.hasNext())
		{
			Chunk potential = it.next();
			boolean match = true;
			Iterator<Symbol> slots = request.getSlotNames();
			while (slots.hasNext())
			{
				Symbol slot = slots.next();
				Symbol value = request.get(slot);
				if (slot==Symbol.get(":recently-retrieved"))
				{
					if (value==Symbol.get("reset")) finsts.clear();
					else if (potential.retrieved != value.toBool()) { match=false; continue; }
				}
				else
				{
					Symbol potval = potential.get(slot);
					if (!partialMatching
							|| (slot==Symbol.isa || slot.name.charAt(0)==':'))
						if (potval==null || potval!=request.get(slot))
						{ match=false; continue; }
				}
			}
			if (match) matches.add (potential);
		}

		if (matches.isEmpty())
		{
			if (activationTrace) model.output ("*** no matching chunks");
			return null;
		}
		else
		{
			it = matches.iterator();
			Chunk chunk = it.next();
			if (activationTrace) model.output ("*** testing " +chunk.name + " " +chunk);
			double highestActivation = chunk.getActivation (request);
			if (activationTrace)
				model.output ("*** activation " +chunk.name + " = " + String.format ("%.3f", highestActivation));
			Chunk highestChunk = chunk;
			while (it.hasNext())
			{
				chunk = it.next();
				if (activationTrace) model.output ("*** testing " +chunk.name + " " +chunk);
				double act = chunk.getActivation (request);
				if (activationTrace)
					model.output ("*** activation " +chunk.name + " = " + String.format ("%.3f", act));
				if (act > highestActivation)
				{
					highestActivation = act;
					highestChunk = chunk;
				}
			}
			if (highestActivation >= retrievalThreshold)
			{
				if (activationTrace) model.output ("*** retrieving " +highestChunk.name + " "+highestChunk);
				return highestChunk;
			}
			else
			{
				if (activationTrace) model.output ("*** no chunk above retrieval threshold");
				return null;
			}
		}
	}

	void update ()
	{
		for (int i=0 ; i<finsts.size() ; i++)
		{
			Chunk c = finsts.elementAt(i);
			if (c.retrievalTime < model.getTime() - declarativeFinstSpan)
			{
				c.retrieved = false;
				c.retrievalTime = 0;
				finsts.removeElementAt(i);
			}
		}

		Chunk request = model.buffers.get (Symbol.retrieval);
		if (request!=null && request.isRequest)
		{
			request.isRequest = false;
			model.buffers.unset (Symbol.retrieval);
			if (model.verboseTrace) model.output ("declarative", "start-retrieval");
			final Chunk retrieval = findRetrieval (request);
			if (retrieval != null)
			{
				double retrievalTime = latencyFactor * Math.exp(-retrieval.getSavedActivation());
				model.buffers.setSlot (Symbol.retrievalState, Symbol.state, Symbol.busy);
				model.buffers.setSlot (Symbol.retrievalState, Symbol.buffer, Symbol.requested);
				model.addEvent (new Event (model.getTime() + retrievalTime,
						"declarative", "retrieved-chunk ["+retrieval.name+"]")
				{
					public void action() {
						retrieval.retrieved = true;
						retrieval.retrievalTime = model.getTime();
						finsts.add (retrieval);
						if (finsts.size() > declarativeNumFinsts) finsts.removeElementAt(0);
						retrieval.addUse();
						model.buffers.set (Symbol.retrieval, retrieval);
						model.buffers.setSlot (Symbol.retrievalState, Symbol.state, Symbol.free);
						model.buffers.setSlot (Symbol.retrievalState, Symbol.buffer, Symbol.full);
					}
				});
			}
			else
			{
				double retrievalTime = latencyFactor * Math.exp(- retrievalThreshold);
				model.buffers.setSlot (Symbol.retrievalState, Symbol.state, Symbol.busy);
				model.addEvent (new Event (model.getTime() + retrievalTime,
						"declarative", "retrieval-failure")
				{
					public void action() {
						model.buffers.setSlot (Symbol.retrievalState, Symbol.state, Symbol.error);
						model.buffers.setSlot (Symbol.retrievalState, Symbol.buffer, Symbol.empty);
					}
				});
			}
		}

//		if (false) //model.getTime() > lastCleanup + 60.0)
//		{
//			compact();
//			lastCleanup = model.getTime();
//		}
	}


//	void compact ()
//	{
//		Vector<Symbol> toRemove = new Vector<Symbol>();
//		Iterator<Symbol> it = chunks.keySet().iterator();
//		while (it.hasNext())
//		{
//			Symbol key = it.next();
//			Chunk chunk = get (key);
//			if (chunk.getCreationTime() < model.getTime() - 60.0
//					&& chunk.getUseCount() == 1
//					&& chunk.getBaseLevel() < -1.0)
//				toRemove.add (key);
//		}
//		it = toRemove.iterator();
//		while (it.hasNext()) chunks.remove (it.next());
//	}


	double getSimilarity (Symbol chunk1, Symbol chunk2)
	{
		if (chunk1==chunk2) return 0;
		Double d = similarities.get (chunk1.name+"$"+chunk2.name);
		if (d==null) d = similarities.get (chunk2.name+"$"+chunk1.name);
		if (d==null) return -1.0;
		else return d.doubleValue();
	}

	void setSimilarity (Symbol chunk1, Symbol chunk2, double value)
	{
		similarities.put (chunk1.name+"$"+chunk2.name, new Double(value));
	}

	void setAllBaseLevels (double baseLevel)
	{
		Iterator<Chunk> it = chunks.values().iterator();
		while (it.hasNext()) it.next().setBaseLevel(baseLevel);
	}

	public String toString ()
	{
		String s = "";
		Iterator<Chunk> it = chunks.values().iterator();
		while (it.hasNext()) s += it.next() + "\n";
		return s;
	}
}
