package actr.core;

import java.util.Iterator;
import java.util.Vector;

class Production
{
	Symbol name;
	private Model model;
	private Vector<BufferCondition> conditions;
	private Vector<BufferAction> actions;
	private double u;
	private boolean hasReward = false;
	private double reward = 0;

	Production (Symbol name, Model model)
	{
		this.name = name;
		this.model = model;
		conditions = new Vector<BufferCondition>();
		actions = new Vector<BufferAction>();
		u = model.procedural.initialUtility;
	}

	Production copy ()
	{
		Production p = new Production (Symbol.getUnique(name.name), model);
		for (int i=0 ; i<conditions.size() ; i++)
			p.conditions.add (conditions.elementAt(i).copy());
		for (int i=0 ; i<actions.size() ; i++)
			p.actions.add (actions.elementAt(i).copy());
		return p;
	}

	boolean equals (Production p2)
	{
		if (conditions.size() != p2.conditions.size()) return false;
		for (int i=0 ; i<conditions.size() ; i++)
			if (!conditions.elementAt(i).equals(p2.conditions.elementAt(i))) return false;
		if (actions.size() != p2.actions.size()) return false;
		for (int i=0 ; i<actions.size() ; i++)
			if (!actions.elementAt(i).equals(p2.actions.elementAt(i))) return false;
		return true;
	}

	void addBufferCondition (BufferCondition bc)
	{
		conditions.add (bc);
	}

	void addBufferAction (BufferAction ac)
	{
		actions.add (ac);
	}

	double getUtility() { return u; }
	void setUtility (double x) { u = x; }

	boolean hasReward() { return hasReward; }
	double getReward() { return reward; }

	void setParameter (String parameter, String value)
	{
		if (parameter.equals(":u")) u = Double.valueOf(value);
		else if (parameter.equals(":reward"))
		{
			hasReward = true;
			reward = Double.valueOf(value);
		}
		else model.warning("unknown production parameter "+parameter);
	}

	Iterator<BufferCondition> getConditions() { return conditions.iterator(); }
	BufferCondition getBufferCondition (Symbol buffer)
	{
		for (int i=0 ; i<conditions.size() ; i++)
			if (conditions.elementAt(i).getBuffer() == buffer) return conditions.elementAt(i);
		return null;
	}
	boolean hasBufferCondition (Symbol buffer) { return getBufferCondition(buffer)!=null; }
	
	SlotCondition getSlotCondition (Symbol buffer, Symbol slot)
	{
		BufferCondition bc = getBufferCondition (buffer);
		if (bc!=null) return bc.getSlotCondition(slot);
		else return null;
	}

	Iterator<BufferAction> getActions() { return actions.iterator(); }
	BufferAction getBufferAction (Symbol buffer)
	{
		for (int i=0 ; i<actions.size() ; i++)
			if (actions.elementAt(i).getBuffer() == buffer) return actions.elementAt(i);
		return null;
	}
	boolean hasBufferAction (Symbol buffer) { return getBufferAction(buffer)!=null; }

	SlotAction getSlotAction (Symbol buffer, Symbol slot)
	{
		BufferAction ba = getBufferAction (buffer);
		if (ba!=null) return ba.getSlotAction(slot);
		else return null;
	}

	BufferAction getBufferAction (char prefix, Symbol buffer)
	{
		for (int i=0 ; i<actions.size() ; i++)
		{
			BufferAction ba = actions.elementAt(i);
			if (ba.getBuffer()==buffer && ba.getPrefix()==prefix) return ba;
		}
		return null;
	}
	boolean hasBufferAction (char prefix, Symbol buffer) { return getBufferAction(prefix,buffer)!=null; }
	boolean hasBufferRequest (Symbol buffer) { return hasBufferAction('+',buffer); }

	boolean hasSpecials ()
	{
		for (int i=0 ; i<actions.size() ; i++)
		{
			BufferAction ba = actions.elementAt(i);
			if (ba.isDirect() || ba.isSpecial()) return true;
		}
		return false;
	}
	
	boolean hasConditionSlotValue (Symbol value)
	{
		for (int i=0 ; i<conditions.size() ; i++)
			if (conditions.get(i).hasSlotValue(value)) return true;
		return false;
	}
	
	boolean hasActionSlotValue (Symbol value)
	{
		for (int i=0 ; i<actions.size() ; i++)
			if (actions.get(i).hasSlotValue(value)) return true;
		return false;
	}
	
	boolean hasSlotValue (Symbol value)
	{
		return hasConditionSlotValue(value) || hasActionSlotValue(value);
	}
	
	Vector<Symbol> getConditionVariables ()
	{
		Vector<Symbol> variables = new Vector<Symbol>();
		Iterator<BufferCondition> it = getConditions();
		while (it.hasNext())
		{
			BufferCondition bc = it.next();
			if (bc.getPrefix() == '=') variables.add (Symbol.get("="+bc.getBuffer()));
			Iterator<SlotCondition> itBC = bc.getSlotConditions();
			while (itBC.hasNext())
			{
				SlotCondition sc = itBC.next();
				if (sc.getSlot().isVariable()) variables.add (sc.getSlot());
				if (sc.getValue().isVariable()) variables.add (sc.getValue());
			}
		}
		return variables;
	}
	
	Vector<Symbol> getActionVariables ()
	{
		Vector<Symbol> variables = new Vector<Symbol>();
		Iterator<BufferAction> it = getActions();
		while (it.hasNext())
		{
			BufferAction ba = it.next();
			Iterator<SlotAction> itBA = ba.getSlotActions();
			while (itBA.hasNext())
			{
				SlotAction sa = itBA.next();
				if (sa.getSlot().isVariable()) variables.add (sa.getSlot());
				if (sa.getValue().isVariable()) variables.add (sa.getValue());
			}
		}
		return variables;
	}
	
	Vector<Symbol> getVariables ()
	{
		Vector<Symbol> cvs = getConditionVariables();
		Vector<Symbol> avs = getActionVariables();
		for (int i=0; i<avs.size(); i++)
			if (!cvs.contains(avs.elementAt(i))) cvs.add (avs.elementAt(i));
		return cvs;
	}
	
	boolean queriesOtherThanBusy (Symbol buffer)
	{
		BufferCondition bc = getBufferCondition (buffer);
		if (bc==null) return false;
		if (bc.numSlotConditions()==0) return false;
		SlotCondition state = bc.getSlotCondition(Symbol.state);
		if (state==null) return true;
		return (state.getValue()!=Symbol.busy);
	}

	boolean queriesForError (Symbol buffer)
	{
		BufferCondition bc = getBufferCondition (buffer);
		if (bc==null) return false;
		SlotCondition state = bc.getSlotCondition(Symbol.state);
		if (state==null) return false;
		return (state.getValue()==Symbol.error);
	}
	
	
	Instantiation instantiate (Buffers buffers)
	{
		if (model.procedural.whyNotTrace) model.output ("? " + name);
		double instU = u + Utilities.getNoise (model.procedural.utilityNoiseS);		
		Instantiation inst = new Instantiation (this, model.getTime(), instU);

		for (int i=0 ; i<conditions.size() ; i++)
		{
			BufferCondition bc = conditions.elementAt(i);
			if (! bc.test (inst))
			{
				if (model.procedural.whyNotTrace) model.output ("    X instantiation failed");
				return null;
			}
		}
		if (model.procedural.whyNotTrace) model.output ("    * instantiation succeeded: "+inst);
		return inst;
	}


	void fire (Instantiation inst)
	{
		for (int i=0 ; i<conditions.size() ; i++)
		{
			BufferCondition bc = conditions.elementAt(i);
			if (bc.getPrefix()=='=' && bc.getBuffer()!=Symbol.goal)
			{
				Symbol buffer = bc.getBuffer();
				boolean found = false;
				for (int j=0 ; j<actions.size() ; j++)
					if (actions.elementAt(j).getBuffer() == buffer) found = true;
				if (!found) model.buffers.unset (bc.getBuffer());
			}
		}
		for (int i=0 ; i<actions.size() ; i++)
		{
			BufferAction ba = actions.elementAt(i);
			ba.fire (inst);
		}
	}

	void specialize (Symbol variable, Symbol value)
	{
		if (value==null) { model.error ("cannot specialize "+variable+" to null value"); return; }
		for (int i=0 ; i<conditions.size() ; i++)
			conditions.elementAt(i).specialize (variable, value);
		for (int i=0 ; i<actions.size() ; i++)
			actions.elementAt(i).specialize (variable, value);
	}

	void expandDirectActions (Instantiation inst)
	{
		for (int i=0 ; i<actions.size() ; i++)
			actions.elementAt(i).expandDirectAction(inst);
	}

	public String toString ()
	{
		String s = "(p " + name + "\n";
		for (int i=0 ; i<conditions.size() ; i++) s += conditions.elementAt(i);
		s += "==>\n";
		for (int i=0 ; i<actions.size() ; i++) s += actions.elementAt(i);
		s += ")\n";
		return s;
	}
}
