package com.example.dreamteam.actr.core;

import java.util.*;

class Audio extends Module
{
	private Model model;
	private Map<Symbol,AuralObject> audicon;
	private Map<Symbol,AuralObject> aurallocs;

	double toneDetectDelay = .050;
	double toneRecodeDelay = .285;
	double digitDetectDelay = .300;
	double digitRecodeDelay = .500;

	Audio (Model model)
	{
		this.model = model;
		audicon = new HashMap<Symbol,AuralObject>();
		aurallocs = new HashMap<Symbol,AuralObject>();
	}

	class AuralObject
	{
		Symbol id, type, content;
		boolean attended;
		Chunk auralloc;

		AuralObject (Symbol id, Symbol type, Symbol content)
		{
			this.id = id;
			this.type = type;
			this.content = content;
			attended = false;
			auralloc = null;
		}

		public String toString ()
		{
			return "[" + id + " " + type + " " + content + "]"; 
		}
	}

	public void clearAural ()
	{
		audicon.clear();
	}

	public void addAural (final String id, String type, String content)
	{
		final AuralObject ao = new AuralObject (Symbol.get(id), Symbol.get(type), Symbol.get(content));
		final Chunk auralloc = createAuralLocChunk (ao);
		if (auralloc != null)
		{
			double detectDelay = (auralloc.get(Symbol.kind)==Symbol.tone) ? toneDetectDelay : digitDetectDelay;
			model.addEvent (new Event (model.getTime() + detectDelay,
					"audio", "audio-event ["+auralloc.name+"]") {
				public void action() {
					audicon.put (Symbol.get(id), ao);
					if (model.bufferStuffing && model.buffers.get(Symbol.aurloc)==null)
					{
						if (model.verboseTrace) model.output ("audio", "unrequested ["+auralloc.name+"]");
						model.buffers.set (Symbol.aurloc, auralloc);
						model.buffers.setSlot (Symbol.aurlocState, Symbol.state, Symbol.free);
						model.buffers.setSlot (Symbol.aurlocState, Symbol.buffer, Symbol.unrequested);
					}
				}
			});
		}
	}

	Chunk createAuralLocChunk (AuralObject ao)
	{
		if (ao == null) return null;
		Chunk auralloc = new Chunk (Symbol.getUnique("audio-event"), model);
		auralloc.set (Symbol.isa, Symbol.get("audio-event"));
		auralloc.set (Symbol.kind, ao.type);
		auralloc.set (Symbol.location, Symbol.get("external"));
		ao.auralloc = auralloc;
		aurallocs.put (auralloc.name, ao);
		return auralloc;
	}

	Chunk findAuralLocation (Chunk request)
	{
		Symbol kind = request.get (Symbol.kind);

		AuralObject found = null;
		Iterator<AuralObject> it = audicon.values().iterator();
		while (found==null && it.hasNext())
		{
			AuralObject ao = it.next();
			if (kind!=Symbol.nil && kind!=ao.type) continue;
			found = ao;
		}
		return createAuralLocChunk (found);
	}

	void update ()
	{
		Chunk request = model.buffers.get (Symbol.aurloc);
		if (request!=null && request.isRequest)
		{
			request.isRequest = false;
			model.buffers.unset (Symbol.aurloc);
			final Chunk auralloc = findAuralLocation (request);
			if (auralloc != null)
			{
				if (model.verboseTrace) model.output ("audio", "find-sound ["+auralloc.name+"]");
				model.buffers.set (Symbol.aurloc, auralloc);
				model.buffers.setSlot (Symbol.aurlocState, Symbol.state, Symbol.free);
				model.buffers.setSlot (Symbol.aurlocState, Symbol.buffer, Symbol.full);
			}
			else
			{
				if (model.verboseTrace) model.output ("audio", "find-sound-failure");
				model.buffers.setSlot (Symbol.aurlocState, Symbol.state, Symbol.error);
				model.buffers.setSlot (Symbol.aurlocState, Symbol.buffer, Symbol.empty);
			}
		}

		request = model.buffers.get (Symbol.aural);
		if (request!=null && request.isRequest && request.get(Symbol.isa)==Symbol.get("sound"))
		{
			request.isRequest = false;
			model.buffers.unset (Symbol.aural);
			if (model.verboseTrace) model.output ("audio", "attend-sound");
			Symbol aurallocName = request.get (Symbol.event);
			if (aurallocName == null) { model.warning ("bad aural location"); return; }
			AuralObject ao = aurallocs.get (aurallocName);
			Chunk auralloc = ao.auralloc;
			if (auralloc == null) { model.warning ("bad aural location"); return; }
			Symbol kind = auralloc.get (Symbol.kind);
			ao.attended = true;
			final Chunk aural = new Chunk (Symbol.getUnique(kind.name), model);
			aural.set (Symbol.isa, kind);
			aural.set (Symbol.event, aurallocName);
			aural.set (Symbol.content, ao.content);
			double recodeDelay = (auralloc.get(Symbol.kind)==Symbol.tone) ? toneRecodeDelay : digitRecodeDelay;
			model.buffers.setSlot (Symbol.auralState, Symbol.state, Symbol.busy);
			model.buffers.setSlot (Symbol.auralState, Symbol.buffer, Symbol.requested);
			model.addEvent (new Event (model.getTime() + recodeDelay, "audio", "audio-encoding-complete ["+aural.name+"]")
			{
				public void action() {
					model.task.attendSound();
					model.buffers.set (Symbol.aural, aural);
					model.buffers.setSlot (Symbol.auralState, Symbol.state, Symbol.free);
					model.buffers.setSlot (Symbol.auralState, Symbol.buffer, Symbol.full);
				}
			});
			// XXX doesn't handle failure
		}
	}
}
