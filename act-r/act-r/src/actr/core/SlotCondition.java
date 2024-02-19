package actr.core;

class SlotCondition
{
	private Model model;
	private Symbol slot, value;
	private String operator;

	SlotCondition (String operator, Symbol slot, Symbol value, Model model)
	{
		this.model = model;
		this.slot = slot;
		this.value = value;
		this.operator = operator;
	}

	SlotCondition copy ()
	{
		return new SlotCondition (operator, slot, value, model);
	}
	
	boolean equals (SlotCondition sc2)
	{
		if (operator==null) return (sc2.operator==null && slot==sc2.slot && value==sc2.value);
		else return (operator.equals(sc2.operator) && slot==sc2.slot && value==sc2.value);
	}
	
	Symbol getSlot() { return slot; }
	Symbol getValue() { return value; }
	String getOperator() { return operator; }

	boolean test (Chunk bufferChunk, Instantiation inst)
	{
		if (model.procedural.whyNotTrace) model.output ("   "+this);
		Symbol realSlot = (slot.isVariable()) ? inst.get(slot) : slot;
		if (realSlot==null) return false;
		Symbol bufferValue = bufferChunk.get(realSlot);
		if (bufferValue==null) return false;
		Symbol testValue = value;
		if (testValue.isVariable())
		{
			if (bufferValue == Symbol.nil) return false;
			testValue = inst.get (testValue);
			if (testValue == null)
			{
				inst.set (value, bufferValue);
				if (model.procedural.whyNotTrace) model.output ("            ["+value+" -> "+bufferValue+"]");
				return true;
			}
		}

		if (operator == null) return (testValue == bufferValue);
		else if (operator.equals("-")) return (testValue != bufferValue);
		else
		{
			double bufferNumber = Double.valueOf (bufferValue.name);
			double testNumber = Double.valueOf (testValue.name);
			if (operator.equals("<")) return (bufferNumber < testNumber);
			else if (operator.equals(">")) return (bufferNumber > testNumber);
			else if (operator.equals("<=")) return (bufferNumber <= testNumber);
			else if (operator.equals(">=")) return (bufferNumber >= testNumber);
		}
		return false;
	}

	void specialize (Symbol variable, Symbol instvalue)
	{
		if (slot==variable) slot = instvalue;
		if (value==variable) value = instvalue;
	}	
	
	public String toString ()
	{
		String s = "      ";
		if (operator!=null) s += operator + " ";
		s += slot + " " + value;
		return s;
	}
}
