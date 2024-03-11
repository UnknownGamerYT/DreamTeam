package com.example.dreamteam.actr.core;

class SlotAction
{
	private Symbol slot, value;

	SlotAction (Symbol slot, Symbol value)
	{
		this.slot = slot;
		this.value = value;
	}
	
	SlotAction copy ()
	{
		return new SlotAction (slot, value);
	}

	boolean equals (SlotAction sa2)
	{
		return (slot==sa2.slot && value==sa2.value);
	}
	
	Symbol getSlot() { return slot; }
	Symbol getValue() { return value; }
	
	void fire (Instantiation inst, Chunk bufferChunk)
	{
		Symbol realSlot = (slot.isVariable()) ? inst.get(slot) : slot;
		if (realSlot==null) return;
		if (value.isVariable()) bufferChunk.set (realSlot, inst.get (value));
		else bufferChunk.set (realSlot, value);
	}

	void specialize (Symbol variable, Symbol instvalue)
	{
		if (slot==variable) slot = instvalue;
		if (value==variable) value = instvalue;
	}	
	
	public String toString ()
	{
		return "      " + slot + " " + value;
	}
}
