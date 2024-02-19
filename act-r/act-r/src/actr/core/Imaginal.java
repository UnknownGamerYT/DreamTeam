package actr.core;

class Imaginal extends Module
{
	private Model model;

	double imaginalDelay = .200;

	Imaginal (Model model)
	{
		this.model = model;
	}

	void update ()
	{
		final Chunk chunk = model.buffers.get (Symbol.imaginal);
		if (chunk!=null && chunk.isRequest)
		{
			chunk.isRequest = false;
			model.buffers.unset (Symbol.imaginal);
			if (model.buffers.getSlot(Symbol.imaginalState,Symbol.state) == Symbol.busy)
			{ model.warning("imaginal busy, request ignored"); return; }
			if (model.verboseTrace) model.output ("imaginal", "set-buffer init");
			model.buffers.setSlot (Symbol.imaginalState, Symbol.state, Symbol.busy);
			model.buffers.setSlot (Symbol.imaginalState, Symbol.buffer, Symbol.requested);
			model.addEvent (new Event (model.getTime() + imaginalDelay, "imaginal", "set-buffer ["+chunk.name+"]")
			{
				public void action() {
					model.buffers.set (Symbol.imaginal, chunk);
					model.buffers.setSlot (Symbol.imaginalState, Symbol.state, Symbol.free);
					model.buffers.setSlot (Symbol.imaginalState, Symbol.buffer, Symbol.full);
				}
			});
		}
	}
}
