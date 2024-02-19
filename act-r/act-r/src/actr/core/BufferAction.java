package actr.core;

import java.util.*;

class BufferAction
{
	private Model model;
	private char prefix;
	private Symbol buffer;
	private Vector<SlotAction> slotActions;
	private Symbol directAction;
	private Symbol bind;
	private Vector<String> specials;

	BufferAction (char prefix, Symbol buffer, Model model)
	{
		this.prefix = prefix;
		this.buffer = buffer;
		this.model = model;
		slotActions = new Vector<SlotAction>();
		directAction = null;
		bind = null;
		specials = new Vector<String>();
	}

	BufferAction copy ()
	{
		BufferAction ba = new BufferAction (prefix, buffer, model);
		for (int i=0 ; i<slotActions.size() ; i++)
			ba.slotActions.add (slotActions.elementAt(i).copy());
		ba.directAction = directAction;
		ba.bind = bind;
		for (int i=0 ; i<specials.size() ; i++)
			ba.specials.add (specials.elementAt(i));
		return ba;
	}

	boolean equals (BufferAction ba2)
	{
		if (prefix != ba2.prefix) return false;
		if (buffer != ba2.buffer) return false;
		if (slotActions.size() != ba2.slotActions.size()) return false;
		for (int i=0 ; i<slotActions.size() ; i++)
			if (!slotActions.elementAt(i).equals(ba2.slotActions.elementAt(i))) return false;
		if (directAction != ba2.directAction) return false;
		if (bind != ba2.bind) return false;
		if (specials.size() != ba2.specials.size()) return false;
		for (int i=0 ; i<specials.size() ; i++)
			if (!specials.elementAt(i).equals(ba2.specials.elementAt(i))) return false;
		return true;
	}

	char getPrefix () { return prefix; }
	Symbol getBuffer() { return buffer; }

	int numSlotActions() { return slotActions.size(); }
	SlotAction getSlotAction (int i) { return slotActions.elementAt(i); }
	Iterator<SlotAction> getSlotActions() { return slotActions.iterator(); }

	SlotAction getSlotAction (Symbol slot)
	{
		for (int i=0 ; i<slotActions.size() ; i++)
			if (slotActions.elementAt(i).getSlot() == slot) return slotActions.elementAt(i);
		return null;
	}
	boolean hasSlotAction (Symbol slot) { return getSlotAction(slot)!=null; }

	boolean hasSlotValue (Symbol value)
	{
		for (int i=0 ; i<slotActions.size() ; i++)
			if (slotActions.elementAt(i).getValue() == value) return true;
		return false;
	}

	boolean isDirect () { return (directAction != null); }
	boolean isSpecial () { return (prefix == '!'); }

	void addAction (SlotAction sa) { slotActions.add(sa); }
	void setPrefix (char c) { prefix = c; }
	void setDirect (Symbol s) { directAction=s; }
	void setBind (Symbol s) { bind=s; }
	void addSpecial (String s) { specials.add(s); }
	
	void storeAndClear (Symbol buffer, Instantiation inst)
	{
		Chunk bufferChunk = model.buffers.get (buffer);
		if (bufferChunk!=null
				//&& buffer!=Symbol.visloc && buffer!=Symbol.aurloc
				//&& buffer!=Symbol.visual && buffer!=Symbol.aural
				)
		{
			//model.output ("declarative", "store chunk ["+bufferChunk.name+"] " + bufferChunk);
			Chunk newChunk = model.declarative.add (bufferChunk);
			//if (newChunk != bufferChunk) model.output ("declarative", "merged into ["+newChunk.name+"]");
			if (newChunk != bufferChunk) inst.replaceValue (bufferChunk.name, newChunk.name);
		}
		model.buffers.unset (buffer);
	}

	void fire (Instantiation inst)
	{
		if (directAction != null)
		{
			Symbol directSymbol = inst.get (directAction);
			if (directSymbol == null) { model.warning(directAction+" not a valid symbol"); return; }
			Chunk direct = model.declarative.get (directSymbol);
			if (direct==null) direct = model.buffers.getBufferChunk(directSymbol);
			if (direct==null) { model.warning(directAction+" -> "+directSymbol+" not a valid chunk"); return; }
			direct = direct.copy ();
			direct.isRequest = true;
			model.buffers.set (buffer, direct);
		}
		else if (prefix == '=')
		{
			Chunk bufferChunk = model.buffers.get (buffer); // inst.get (Symbol.get("="+buffer));
			if (bufferChunk==null) { model.warning(buffer+" empty, not referenced in LHS?"); return; }
			for (int i=0 ; i<slotActions.size() ; i++)
				slotActions.elementAt(i).fire (inst, bufferChunk);
		}
		else if (prefix == '+')
		{
			if (model.declarative.addChunkOnNewRequest) storeAndClear (buffer, inst);
			else model.buffers.unset (buffer);
			
			Chunk requestChunk = new Chunk (Symbol.getUnique("chunk"), model);
			for (int i=0 ; i<slotActions.size() ; i++)
				slotActions.elementAt(i).fire (inst, requestChunk);
			Symbol chunkType = requestChunk.get(Symbol.get("isa"));
			if (chunkType != Symbol.nil)
				requestChunk.name = Symbol.getUnique (chunkType.name);
			requestChunk.isRequest = true;
			model.buffers.set (buffer, requestChunk);
		}
		else if (prefix == '-')
		{
			storeAndClear (buffer, inst);
		}
		else if (prefix == '!')
		{
			Vector<String> tokens = new Vector<String>();
			String s = "";
			for (int i=0 ; i<specials.size() ; i++)
			{
				String special = specials.elementAt(i);
				if (Symbol.get(special).isVariable())
				{
					Symbol value = inst.get(Symbol.get(special));
					special = (value==null) ? "<unbound variable>" : value.name;
				}
				tokens.add (special);
				s += special + ((i==specials.size()-1) ? "" : " ");
			}
			if (buffer==Symbol.get("output"))
			{
				if (s.length()>=3) s = s.substring (2, s.length()-2);
				if (model.verboseTrace) model.output (s);
			}
			else if (buffer==Symbol.get("bind"))
			{
				try {
					double value = Utilities.evalCompute (tokens.iterator());
					inst.set (bind, Symbol.get(value));
				} catch (Exception e) {
					double value = model.task.bind (tokens.iterator());
					inst.set (bind, Symbol.get(value));
				}
			}
			else
			{
				try {
					Utilities.evalCompute (tokens.iterator());
				} catch (Exception e) { model.task.eval (tokens.iterator()); }
			}
		}
	}

	void specialize (Symbol variable, Symbol value)
	{
		for (int i=0 ; i<slotActions.size() ; i++)
			slotActions.elementAt(i).specialize (variable, value);
	}

	void expandDirectAction (Instantiation inst)
	{
		if (directAction==null) return;
		Symbol name = inst.get(directAction);
		Chunk chunk = model.declarative.get (name);
		if (chunk==null) chunk = model.buffers.getBufferChunk(name);
		Iterator<Symbol> it = chunk.getSlotNames();
		while (it.hasNext())
		{
			Symbol slot = it.next();
			Symbol value = chunk.get(slot);
			slotActions.add (new SlotAction(slot,value));
		}
		directAction = null;
	}

	public String toString ()
	{
		String s = "";
		if (directAction==null)
		{
			s += "   " + prefix + buffer + ">\n";
			for (int i=0 ; i<slotActions.size() ; i++)
				s += slotActions.elementAt(i) + "\n";
		}
		else s += "   " + prefix + buffer + "> " + directAction + "\n";
		return s;
	}
}
