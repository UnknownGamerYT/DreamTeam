package actr.core;

import java.io.*;
import java.net.URL;

import actr.env.Main;
import actr.task.Task;



class Parser
{
	private Tokenizer t;
	private static Model model;

	Parser (File file) throws FileNotFoundException
	{
		t = new Tokenizer (file);
	}

	Parser (URL url) throws IOException
	{
		t = new Tokenizer (url);
	}

	Parser (String s)
	{
		t = new Tokenizer (s);
	}

	class ParseException extends Exception
	{
		ParseException () { super ("parse error on line " + t.getLine()); }
	}

	Model parseModel (String name, Task task, Main main) throws ParseException
	{
		model = new Model (name, task, main);
		parseCommands (model);
		return model;
	}

	void parseCommands (Model model) throws ParseException
	{
		while (t.hasMoreTokens())
		{
			if (!t.getToken().equals("(")) throw new ParseException();
			t.advance();

			if (t.getToken().equals("p") || t.getToken().equals("p*"))
			{
				Production p = parseProduction ();
				model.procedural.add (p);
			}
			else if (t.getToken().equals("add-dm"))
			{
				t.advance();
				while (!t.getToken().equals(")"))
				{
					Chunk c = parseChunk (model.declarative);
					c = model.declarative.add (c);
				}
				if (!t.getToken().equals(")")) throw new ParseException();;
				t.advance();
			}
			else if (t.getToken().equals("goal-focus"))
			{
				t.advance();
				String goalName = t.getToken();
				if (goalName.equals("*task*")) goalName = model.task.getClass().getSimpleName().toLowerCase();
				t.advance();
				Chunk c = model.declarative.get (Symbol.get (goalName));
				if (c!=null) model.buffers.set (Symbol.goal, c);
				if (!t.getToken().equals(")")) throw new ParseException();;
				t.advance();
			}
			else if (t.getToken().equals("spp"))
			{
				t.advance();
				Production p = model.procedural.get (Symbol.get (t.getToken()));
				if (p == null)
				{
					model.warning ("production " + t.getToken() + " not found");
					while (!t.getToken().equals(")")) t.advance();
					t.advance();
					break;
				}
				t.advance();
				while (!t.getToken().equals(")"))
				{
					String parameter = t.getToken();
					t.advance();
					String value = t.getToken();
					t.advance();
					p.setParameter (parameter, value);
				}
				if (!t.getToken().equals(")")) throw new ParseException();;
				t.advance();
			}
			else if (t.getToken().equals("sgp"))
			{
				t.advance();
				while (!t.getToken().equals(")"))
				{
					String parameter = t.getToken();
					t.advance();
					String value = t.getToken();
					t.advance();
					model.setParameter (parameter, value);
				}
				if (!t.getToken().equals(")")) throw new ParseException();;
				t.advance();
			}
			else if (t.getToken().equals("set-base-levels"))
			{
				t.advance();
				while (!t.getToken().equals(")"))
				{
					if (!t.getToken().equals("(")) throw new ParseException();;
					t.advance();
					Chunk c = model.declarative.get (Symbol.get(t.getToken()));
					t.advance();
					double baseLevel = Double.valueOf (t.getToken());
					t.advance();
					if (c != null) c.setBaseLevel (baseLevel);
					else model.warning ("chunk " + c + " not defined");
					if (!t.getToken().equals(")")) throw new ParseException();;
					t.advance();
				}
				if (!t.getToken().equals(")")) throw new ParseException();;
				t.advance();
			}
			else if (t.getToken().equals("set-all-base-levels"))
			{
				t.advance();
				double baseLevel = Double.valueOf (t.getToken());
				model.declarative.setAllBaseLevels(baseLevel);
				t.advance();
				if (!t.getToken().equals(")")) throw new ParseException();;
				t.advance();
			}
			else if (t.getToken().equals("set-similarities"))
			{
				t.advance();
				while (!t.getToken().equals(")"))
				{
					if (!t.getToken().equals("(")) throw new ParseException();;
					t.advance();
					Symbol s1 = Symbol.get (t.getToken());
					t.advance();
					Symbol s2 = Symbol.get (t.getToken());
					t.advance();
					model.declarative.setSimilarity (s1, s2, Double.valueOf(t.getToken()));
					t.advance();
					if (!t.getToken().equals(")")) throw new ParseException();;
					t.advance();
				}
				if (!t.getToken().equals(")")) throw new ParseException();;
				t.advance();
			}
			else if (t.getToken().equals("start-hand-at-mouse"))
			{
				t.advance();
				model.motor.moveHandToMouse();
				if (!t.getToken().equals(")")) throw new ParseException();;
				t.advance();
			}
		}
	}

	public Production parseProduction () throws ParseException
	{
		//if (!t.getToken().equals("(")) throw new ParseException();;
		//t.advance();
		if (!t.getToken().equals("p") && !t.getToken().equals("p*")) throw new ParseException();;
		t.advance();
		Symbol name = Symbol.get (t.getToken());
		t.advance();

		Production p = new Production (name, model);

		while (t.hasMoreTokens() && !t.getToken().equals("==>"))
		{
			BufferCondition bc = parseBufferCondition ();
			p.addBufferCondition (bc);
		}

		if (!t.getToken().equals("==>")) throw new ParseException();;
		t.advance();

		while (t.hasMoreTokens() && !t.getToken().equals(")"))
		{
			BufferAction ba = parseBufferAction ();
			p.addBufferAction (ba);
		}

		if (!t.getToken().equals(")")) throw new ParseException();;
		t.advance();

		return p;
	}

	BufferCondition parseBufferCondition () throws ParseException
	{
		char prefix = t.getToken().charAt(0);
		String bufferName = t.getToken().substring (1, t.getToken().length()-1);
		Symbol buffer = Symbol.get ((prefix=='?' ? "?" : "") + bufferName);
		t.advance();

		BufferCondition bc = new BufferCondition (prefix, buffer, model);

		if (prefix == '!')
		{
			if (!t.getToken().equals("(")) throw new ParseException();
			bc.addSpecial (t.getToken());
			t.advance();
			int nparens = 0;
			while (nparens>0 || !t.getToken().equals(")"))
			{
				if (t.getToken().equals("(")) nparens++;
				else if (t.getToken().equals(")")) nparens--;
				bc.addSpecial (t.getToken());
				t.advance();
			}
			if (!t.getToken().equals(")")) throw new ParseException();
			bc.addSpecial (t.getToken());
			t.advance();
		}
		else
		{
			while (t.hasMoreTokens() && !t.getToken().startsWith("?")
					&& !t.getToken().startsWith("!")
					&& !(t.getToken().startsWith("=") && t.getToken().endsWith(">")))
			{
				//System.out.println ("-"+t.getToken()+"-");
				SlotCondition slotCondition = parseSlotCondition ();
				bc.addCondition (slotCondition);
			}
		}
		return bc;
	}

	SlotCondition parseSlotCondition ()
	{
		String operator = null;
		if (t.getToken().equals("-") || t.getToken().equals("<") || t.getToken().equals(">")
				|| t.getToken().equals("<=") || t.getToken().equals(">="))
		{
			operator = t.getToken();
			t.advance();
		}
		Symbol slot = Symbol.get (t.getToken());
		t.advance();
		Symbol value = Symbol.get (t.getToken());
		t.advance();
		return new SlotCondition (operator, slot, value, model);
	}

	BufferAction parseBufferAction () throws ParseException
	{
		char prefix = t.getToken().charAt(0);
		String bufferName = t.getToken().substring (1, t.getToken().length()-1);
		Symbol buffer = Symbol.get ((prefix=='?' ? "?" : "") + bufferName);
		t.advance();

		BufferAction ba = new BufferAction (prefix, buffer, model);
		if (prefix == '!')
		{
			if (!t.getToken().equals("("))
			{
				ba.setBind (Symbol.get(t.getToken()));
				t.advance();
			}
			if (!t.getToken().equals("(")) throw new ParseException();
			ba.addSpecial (t.getToken());
			t.advance();
			int nparens = 0;
			while (nparens>0 || !t.getToken().equals(")"))
			{
				if (t.getToken().equals("(")) nparens++;
				else if (t.getToken().equals(")")) nparens--;
				ba.addSpecial (t.getToken());
				t.advance();
			}
			if (!t.getToken().equals(")")) throw new ParseException();
			ba.addSpecial (t.getToken());
			t.advance();
			return ba;
		}
		else if (prefix == '-')
		{
			return ba;
		}
		else if (t.getToken().startsWith("=") && !t.getToken().endsWith(">")) // direct setting of buffer?
		{
			String direct = t.getToken();
			t.advance();
			String next = t.getToken();
			if (next.equals(")") || next.startsWith("-") || next.startsWith("+") || next.startsWith("!")
					|| (next.startsWith("=") && next.endsWith(">")))
			{
				ba.setDirect (Symbol.get(direct));
				return ba;
			}
			else t.pushBack (direct);
		}

		while (t.hasMoreTokens() && !t.getToken().equals(")")
				&& !(t.getToken().startsWith("=") && t.getToken().endsWith(">"))
				&& !(t.getToken().startsWith("-") && t.getToken().length()>1)
				&& !t.getToken().startsWith("+") && !t.getToken().startsWith("!"))
		{
			SlotAction slotAction = parseSlotAction ();
			ba.addAction (slotAction);
		}
		return ba;
	}

	SlotAction parseSlotAction ()
	{
		String operator = "";
		if (t.getToken().equals("-") || t.getToken().equals("<") || t.getToken().equals(">")
				|| t.getToken().equals("<=") || t.getToken().equals(">="))
		{
			operator = t.getToken();
			t.advance();
		}
		Symbol slot = Symbol.get (operator+t.getToken());
		t.advance();
		Symbol value = Symbol.get (t.getToken());
		t.advance();
		return new SlotAction (slot, value);
	}

	Chunk parseChunk (Declarative dm) throws ParseException
	{
		if (!t.getToken().equals("(")) throw new ParseException();;
		t.advance();
		Symbol name = Symbol.get (t.getToken());
		t.advance();

		Chunk c = dm.get (name);
		if (c == null) c = new Chunk (name, model);

		while (t.hasMoreTokens() && !t.getToken().equals(")"))
		{
			Symbol slot = Symbol.get (t.getToken());
			t.advance();
			Symbol value = Symbol.get (t.getToken());
			t.advance();
			c.set (slot, value);
		}
		if (!t.getToken().equals(")")) throw new ParseException();;
		t.advance();

		return c;
	}

	static Chunk parseNewChunk (Symbol name, String slots)
	{
		Parser p = new Parser (slots);
		Chunk c = new Chunk (name, model);
		while (p.t.hasMoreTokens())
		{
			Symbol slot = Symbol.get (p.t.getToken());
			p.t.advance();
			Symbol value = Symbol.get (p.t.getToken());
			p.t.advance();
			c.set (slot, value);
		}
		return c;
	}
}
