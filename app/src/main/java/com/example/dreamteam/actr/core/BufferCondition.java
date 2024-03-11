package com.example.dreamteam.actr.core;

import java.util.Iterator;
import java.util.Vector;

class BufferCondition
{
	private Model model;
	private char prefix;
	private Symbol buffer;
	private Vector<SlotCondition> slotConditions;
	private Vector<String> specials;

	BufferCondition (char prefix, Symbol buffer, Model model)
	{
		this.prefix = prefix;
		this.buffer = buffer;
		this.model = model;
		slotConditions = new Vector<SlotCondition>();
		specials = new Vector<String>();
	}
	
	BufferCondition copy ()
	{
		BufferCondition bc = new BufferCondition (prefix, buffer, model);
		for (int i=0 ; i<slotConditions.size() ; i++)
			bc.slotConditions.add (slotConditions.elementAt(i).copy());
		for (int i=0 ; i<specials.size() ; i++)
			bc.specials.add (specials.elementAt(i));
		return bc;
	}

	boolean equals (BufferCondition bc2)
	{
		if (prefix != bc2.prefix) return false;
		if (buffer != bc2.buffer) return false;
		if (slotConditions.size() != bc2.slotConditions.size()) return false;
		for (int i=0 ; i<slotConditions.size() ; i++)
			if (!slotConditions.elementAt(i).equals(bc2.slotConditions.elementAt(i))) return false;
		if (specials.size() != bc2.specials.size()) return false;
		for (int i=0 ; i<specials.size() ; i++)
			if (!specials.elementAt(i).equals(bc2.specials.elementAt(i))) return false;
		return true;
	}

	char getPrefix () { return prefix; }
	Symbol getBuffer() { return buffer; }
	
	int numSlotConditions() { return slotConditions.size(); }
	SlotCondition getSlotCondition (int i) { return slotConditions.elementAt(i); }
	Iterator<SlotCondition> getSlotConditions() { return slotConditions.iterator(); }
	
	SlotCondition getSlotCondition (Symbol slot)
	{
		for (int i=0 ; i<slotConditions.size() ; i++)
			if (slotConditions.elementAt(i).getSlot() == slot) return slotConditions.elementAt(i);
		return null;
	}
	boolean hasSlotCondition (Symbol slot) { return getSlotCondition(slot)!=null; }

	boolean hasSlotValue (Symbol value)
	{
		for (int i=0 ; i<slotConditions.size() ; i++)
			if (slotConditions.elementAt(i).getValue() == value) return true;
		return false;
	}
	
	void addCondition (SlotCondition sc) { slotConditions.add(sc); }
	void addSpecial (String s) { specials.add(s); }
	
	boolean test (Instantiation inst)
	{
		if (model.procedural.whyNotTrace) model.output ("    " + prefix + buffer + ">");
		if (prefix == '!')
		{
			Vector<String> tokens = new Vector<String>();
			for (int i=0 ; i<specials.size() ; i++)
			{
				String special = specials.elementAt(i);
				if (Symbol.get(special).isVariable()) special = inst.get(Symbol.get(special)).name;
				tokens.add (special);
			}
			return Utilities.evalComputeCondition(tokens.iterator());
		}
		else
		{
			Chunk bufferChunk = model.buffers.get (buffer);
			if (bufferChunk == null) return false;
			for (int i=0 ; i<slotConditions.size() ; i++)
			{
				SlotCondition slotCondition = slotConditions.elementAt(i);
				if (!slotCondition.test(bufferChunk, inst)) return false;
			}
			if (prefix == '=') inst.set (Symbol.get("="+buffer), bufferChunk.name);
			return true;
		}
	}
	
	void specialize (Symbol variable, Symbol value)
	{
		for (int i=0 ; i<slotConditions.size() ; i++)
			slotConditions.elementAt(i).specialize (variable, value);
	}	
	
	public String toString ()
	{
		String s = "";
		s += "   " + ((prefix=='?') ? "" : prefix) + buffer + ">\n";
		for (int i=0 ; i<slotConditions.size() ; i++)
			s += slotConditions.elementAt(i) + "\n";
		return s;
	}
}
