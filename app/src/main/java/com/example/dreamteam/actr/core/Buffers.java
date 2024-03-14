package com.example.dreamteam.actr.core;

import java.util.*;

class Buffers
{
	private Model model;
	private Map<Symbol,Chunk> buffers;

	Buffers (Model model)
	{
		this.model = model;
		buffers = new HashMap<Symbol,Chunk>();
	}

	boolean isState (Symbol buffer)
	{
		return buffer.name.charAt(0) == '?';
	}

	Chunk get (Symbol buffer)
	{
		return buffers.get (buffer);
	}

	Symbol getSlot (Symbol buffer, Symbol slot)
	{
		Chunk c = buffers.get (buffer);
		if (c==null) return null;
		else return c.get(slot);
	}

	void set (Symbol buffer, Chunk c)
	{
		//model.output (buffer.name, "buffer-set ["+c.name+"]");
		buffers.put (buffer, c);
		if (!isState(buffer) && !c.isRequest)
		{
			Chunk state = get (Symbol.get("?" + buffer));
			if (state != null) state.set (Symbol.buffer, Symbol.full);
		}
	}

	void setSlot (Symbol buffer, Symbol slot, Symbol value)
	{
		Chunk c = buffers.get (buffer);
		if (c!=null) c.set (slot, value);
	}

	void unset (Symbol buffer)
	{
		//model.output (buffer.name, "buffer-clear");
		buffers.remove (buffer);
		if (!isState(buffer))
		{
			Chunk state = get (Symbol.get("?" + buffer));
			if (state != null)
			{
				state.set (Symbol.buffer, Symbol.empty);
				state.set (Symbol.state, Symbol.free);
			}
		}
		//if (buffer==Symbol.get("aural")) model.task.unattendSound();
	}

	Chunk getBufferChunk (Symbol value)
	{
		Iterator<Chunk> it = buffers.values().iterator();
		while (it.hasNext())
		{
			Chunk chunk = it.next();
			if (chunk.name == value) return chunk;
		}
		return null;
	}

	void replaceSlotValues (Chunk c1, Chunk c2)
	{
		Iterator<Symbol> it = buffers.keySet().iterator();
		while (it.hasNext())
		{
			Symbol buffer = it.next();
			Chunk chunk = get (buffer);
			Iterator<Symbol> slots = chunk.getSlotNames();
			while (slots.hasNext())
			{
				Symbol slot = slots.next();
				Symbol value = chunk.get (slot);
				if (value == c1.name) chunk.set (slot, c2.name);
			}
		}
	}

	public String toString ()
	{
		String s = "";
		Iterator<Symbol> it = buffers.keySet().iterator();
		while (it.hasNext())
		{
			Symbol buffer = it.next();
			s += buffer + " : " + get(buffer) + "\n";
		}
		return s;
	}
}
