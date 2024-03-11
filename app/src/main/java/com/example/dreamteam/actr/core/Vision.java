package com.example.dreamteam.actr.core;

import java.util.*;

class Vision extends Module
{
	private Model model;
	private Map<Symbol,VisualObject> visicon;
	private Map<Symbol,VisualObject> vislocs;
	private Vector<VisualObject> finsts;

	double visualAttentionLatency = .085;
	double visualMovementTolerance = Utilities.angle2pixels (0.5);
	int visualNumFinsts = 4;
	double visualFinstSpan = 3.0;
	double visualOnsetSpan = 0.5;

	Vision (Model model)
	{
		this.model = model;
		visicon = new HashMap<Symbol,VisualObject>();
		vislocs = new HashMap<Symbol,VisualObject>();
		finsts = new Vector<VisualObject>();
	}

	class VisualObject
	{
		Symbol id, type, value;
		int x, y, w, h;
		double d;
		boolean attended;
		double attendedTime;
		double creationTime;
		Chunk visloc;

		VisualObject (Symbol id, Symbol type, Symbol value, int x, int y, int w, int h, double d)
		{
			this.id = id;
			this.type = type;
			this.value = value;
			this.x = x + w/2;
			this.y = y + h/2;
			this.w = w;
			this.h = h;
			this.d = d;
			attended = false;
			attendedTime = 0;
			creationTime = model.getTime();
			visloc = null;
		}

		public String toString ()
		{
			return "[" + id + " " + type + " " + value + " " + x + " " + y + " " + attended + "]"; 
		}
	}

	public Chunk getVisualLocation (Symbol name)
	{
		VisualObject vo = vislocs.get(name);
		if (vo!=null) return vo.visloc;
		else return model.declarative.get(name);
	}

	public void clearVisual ()
	{
		visicon.clear();
	}

	public void addVisual (String id, String type, String value, int x, int y, int w, int h, double d)
	{
		VisualObject vo = new VisualObject (Symbol.get(id), Symbol.get(type), Symbol.get(value), x, y, w, h, d);
		visicon.put (Symbol.get(id), vo);
		if (model.bufferStuffing &&
				(model.buffers.get(Symbol.visloc)==null
						|| model.buffers.get(Symbol.vislocState).get(Symbol.buffer)==Symbol.unrequested))
		{
			Chunk visloc = createVisLocChunk (vo);
			if (model.verboseTrace && model.buffers.get(Symbol.visloc)==null)
				model.output ("vision", "unrequested [" + visloc.name.name + "]");
			model.buffers.set (Symbol.visloc, visloc);
			model.buffers.setSlot (Symbol.vislocState, Symbol.state, Symbol.free);
			model.buffers.setSlot (Symbol.vislocState, Symbol.buffer, Symbol.unrequested);
		}
	}

	public void removeVisual (String id)
	{
		visicon.remove (Symbol.get(id));
	}

	boolean matchesVisualObject (Chunk request, VisualObject vo)
	{
		Iterator<Symbol> it = request.getSlotNames();
		while (it.hasNext())
		{
			Symbol slot = it.next();
			Symbol value = request.get(slot);

			if (slot==Symbol.isa) continue;
			if (slot==Symbol.kind)
			{ if (value!=vo.type) return false; else continue; }
			if (slot==Symbol.get("-kind"))
			{ if (value==vo.type) return false; else continue; }
			if (slot==Symbol.get(":nearest")) continue;
			
			if (slot==Symbol.get(":attended"))
			{
				if (value==Symbol.get("new"))
				{ if (vo.creationTime < model.getTime()-visualOnsetSpan) return false; else continue; }
				else
				{ if (value!=Symbol.get(vo.attended)) return false; else continue; }
			}

			if (value==Symbol.lowest || value==Symbol.highest) continue;

			double vi = 0;
			try { vi = value.toDouble(); }
			catch (Exception e) { return false; }
			
			if (slot==Symbol.screenx && !(Math.abs(vo.x-vi)<=visualMovementTolerance)) return false;
			if (slot==Symbol.screeny && !(Math.abs(vo.y-vi)<=visualMovementTolerance)) return false;
			
			if (slot.name.length() <= 8) continue;
			if (slot==Symbol.get("-screen-x") && !(vo.x != vi)) return false;
			if (slot==Symbol.get("-screen-y") && !(vo.y != vi)) return false;
			if (slot==Symbol.get("<screen-x") && !(vo.x < vi)) return false;
			if (slot==Symbol.get("<screen-y") && !(vo.y < vi)) return false;
			if (slot==Symbol.get(">screen-x") && !(vo.x > vi)) return false;
			if (slot==Symbol.get(">screen-y") && !(vo.y > vi)) return false;
			if (slot==Symbol.get("<=screen-x") && !(vo.x <= vi)) return false;
			if (slot==Symbol.get("<=screen-y") && !(vo.y <= vi)) return false;
			if (slot==Symbol.get(">=screen-x") && !(vo.x >= vi)) return false;
			if (slot==Symbol.get(">=screen-y") && !(vo.y >= vi)) return false;
		}
		return true;
	}

	Chunk createVisLocChunk (VisualObject vo)
	{
		if (vo == null) return null;
		Chunk visloc = new Chunk (Symbol.getUnique("vision"), model);
		visloc.set (Symbol.isa, Symbol.visloc);
		visloc.set (Symbol.kind, vo.type);
		visloc.set (Symbol.screenx, Symbol.get(vo.x));
		visloc.set (Symbol.screeny, Symbol.get(vo.y));
		visloc.set (Symbol.width, Symbol.get(vo.w));
		visloc.set (Symbol.height, Symbol.get(vo.h));
		visloc.set (Symbol.distance, Symbol.get(vo.d));
		vo.visloc = visloc;
		vislocs.put (visloc.name, vo);
		return visloc;
	}

	Symbol slotWithLowestHighest (Chunk request)
	{
		Iterator<Symbol> it = request.getSlotNames();
		while (it.hasNext())
		{
			Symbol slot = it.next();
			Symbol value = request.get(slot);
			if (value==Symbol.lowest || value==Symbol.highest) return slot;
		}
		return null;
	}

	Chunk findVisualLocation (Chunk request, Iterator<VisualObject> it)
	{
		HashSet<VisualObject> found = new HashSet<VisualObject>();
		while (it.hasNext())
		{
			VisualObject vo = it.next();
			if (matchesVisualObject (request, vo)) found.add(vo);
		}
		if (found.isEmpty()) return null;

		Symbol nearest = request.get(Symbol.get(":nearest"));
		if (nearest == Symbol.nil)
		{		
			Symbol lohiSlot = slotWithLowestHighest (request);
			if (lohiSlot != null)
			{
				VisualObject bestvo = null;
				int best = 0;
				Iterator<VisualObject> itVO = found.iterator();
				while (itVO.hasNext())
				{
					VisualObject voTry = itVO.next();
					int vovalue = (lohiSlot.name.charAt(lohiSlot.name.length()-1)=='x') ? voTry.x : voTry.y;
					if (request.get(lohiSlot)==Symbol.lowest)
					{ if (bestvo==null || vovalue < best) { bestvo=voTry; best=vovalue; } }
					else // "highest"
					{ if (bestvo==null || vovalue > best) { bestvo=voTry; best=vovalue; } }
				}
				request.set (lohiSlot, Symbol.get(best));
				return findVisualLocation (request, found.iterator());
			}
			else
			{
				VisualObject bestvo = found.iterator().next();
				return createVisLocChunk (bestvo);
			}
		}
		else
		{
			VisualObject vo = vislocs.get (nearest);
			if (vo==null)
			{
				model.error ("*** :nearest value is not a visual-location");
				return createVisLocChunk (found.iterator().next());
			}
			Chunk vislocChunk = vo.visloc;
			double nearestX = vislocChunk.get(Symbol.screenx).toDouble();
			double nearestY = vislocChunk.get(Symbol.screeny).toDouble();
			VisualObject bestvo = null;
			double best = 99999;
			Iterator<VisualObject> itVO = found.iterator();
			while (itVO.hasNext())
			{
				VisualObject voTry = itVO.next();
				double dx = voTry.x - nearestX;
				double dy = voTry.y - nearestY;
				double dist = Math.sqrt (dx*dx + dy*dy);
				if (bestvo==null || dist < best) { bestvo=voTry; best=dist; }
			}
			return createVisLocChunk (bestvo);
		}
	}

	void update ()
	{
		for (int i=0 ; i<finsts.size() ; i++)
		{
			VisualObject vo = finsts.elementAt(i);
			if (vo.attendedTime < model.getTime() - visualFinstSpan)
			{
				vo.attended = false;
				vo.attendedTime = 0;
				finsts.removeElementAt(i);
			}
		}
		
		Chunk request = model.buffers.get (Symbol.visloc);
		if (request!=null && request.isRequest)
		{
			request.isRequest = false;
			model.buffers.unset (Symbol.visloc);
			Chunk visloc = findVisualLocation (request, visicon.values().iterator());
			if (visloc != null)
			{
				if (model.verboseTrace) model.output ("vision", "find-location [" + visloc.name.name + "]");
				model.buffers.set (Symbol.visloc, visloc);
				model.buffers.setSlot (Symbol.vislocState, Symbol.state, Symbol.free);
				model.buffers.setSlot (Symbol.vislocState, Symbol.buffer, Symbol.requested);
			}
			else
			{
				if (model.verboseTrace) model.output ("vision", "error");
				model.buffers.setSlot (Symbol.vislocState, Symbol.state, Symbol.error);
				model.buffers.setSlot (Symbol.vislocState, Symbol.buffer, Symbol.empty);
			}
		}

		request = model.buffers.get (Symbol.visual);
		if (request!=null && request.isRequest && request.get(Symbol.isa)==Symbol.get("move-attention"))
		{
			request.isRequest = false;
			model.buffers.unset (Symbol.visual);
			Symbol vislocName = request.get (Symbol.screenpos);
			if (vislocName == null) { model.warning ("bad visual location"); return; }
			final VisualObject vo = vislocs.get (vislocName);
			Chunk visloc = vo.visloc;
			if (visloc == null) { model.warning ("bad visual location"); return; }
			Symbol kind = visloc.get (Symbol.kind);
			final Chunk visual = new Chunk (Symbol.getUnique(kind.name), model);
			visual.set (Symbol.isa, kind);
			visual.set (Symbol.screenpos, vislocName);
			visual.set (Symbol.value, vo.value);
			visual.set (Symbol.width, Symbol.get(vo.w));
			visual.set (Symbol.height, Symbol.get(vo.h));
			if (model.verboseTrace) model.output ("vision", "move-attention");
			model.buffers.setSlot (Symbol.visualState, Symbol.state, Symbol.busy);
			model.buffers.setSlot (Symbol.visualState, Symbol.buffer, Symbol.requested);
			model.addEvent (new Event (model.getTime() + visualAttentionLatency,
					"vision", "encoding-complete ["+visual.name+"]")
			{
				public void action() {
					model.task.moveAttention (vo.x, vo.y);
					vo.attended = true;
					vo.attendedTime = model.getTime();
					finsts.add (vo);
					if (finsts.size() > visualNumFinsts) finsts.removeElementAt(0);
					model.buffers.set (Symbol.visual, visual);
					model.buffers.setSlot (Symbol.visualState, Symbol.state, Symbol.free);
					model.buffers.setSlot (Symbol.visualState, Symbol.buffer, Symbol.full);
				}
			});
		}
	}
}
