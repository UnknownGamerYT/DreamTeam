package com.example.dreamteam.actr.core;

class Speech extends Module
{
	private Model model;
	private String lastText;

	double syllableRate = .150;
	int charsPerSyllable = 3;
	double subvocalizeDetectDelay = .300;

	final double prepFirstText = .150;
	final double prepDiffText = .100;
	final double prepSameText = .000;
	final double initiationTime = .050;
	final double clearTime = .050;

	Speech (Model model)
	{
		this.model = model;
		lastText = null;
	}

	double prepareMovement (double time, String text)
	{
		time += (lastText==null) ? prepFirstText :
			(lastText.equals(text) ? prepSameText : prepDiffText);
		model.buffers.setSlot (Symbol.vocalState, Symbol.preparation, Symbol.busy);
		model.buffers.setSlot (Symbol.vocalState, Symbol.processor, Symbol.busy);
		model.buffers.setSlot (Symbol.vocalState, Symbol.state, Symbol.busy);
		model.addEvent (new Event (time, "speech", "preparation-complete") {
			public void action() {
				model.buffers.setSlot (Symbol.vocalState, Symbol.preparation, Symbol.free);
			}
		});
		lastText = text;
		return time;
	}

	double initiateMovement (double time)
	{
		time += initiationTime;
		model.addEvent (new Event (time, "speech", "initiation-complete") {
			public void action() {
				model.buffers.setSlot (Symbol.vocalState, Symbol.processor, Symbol.free);
				model.buffers.setSlot (Symbol.vocalState, Symbol.execution, Symbol.busy);
			}
		});
		return time;
	}

	void finishMovement (double time)
	{
		model.addEvent (new Event (time, "speech", "finish-movement") {
			public void action() {
				model.buffers.setSlot (Symbol.vocalState, Symbol.execution, Symbol.free);
				model.buffers.setSlot (Symbol.vocalState, Symbol.state, Symbol.free);
			}
		});
	}
	
	double getArticulationTime (String text)
	{
		return syllableRate * (1.0 * text.length() / charsPerSyllable);
	}
	
	void sendVocalToAudio (String text)
	{
		model.addAural (Symbol.getUnique("vocal").name, "word", text);
	}

	void update ()
	{
		Chunk request = model.buffers.get (Symbol.vocal);
		if (request==null || !request.isRequest) return;
		request.isRequest = false;
		model.buffers.unset (Symbol.vocal);
		double eventTime = model.getTime();

		if (request.get(Symbol.isa)==Symbol.get("clear"))
		{
			if (model.verboseTrace) model.output ("speech", "clear");
			model.buffers.setSlot (Symbol.vocalState, Symbol.preparation, Symbol.busy);
			model.buffers.setSlot (Symbol.vocalState, Symbol.state, Symbol.busy);
			model.addEvent (new Event (eventTime + clearTime, "speech", "change state last none prep free") {
				public void action() {
					lastText = null;
					model.buffers.setSlot (Symbol.vocalState, Symbol.preparation, Symbol.free);
					model.buffers.setSlot (Symbol.vocalState, Symbol.state, Symbol.free);
				}
			});
			return;
		}

		else if (request.get(Symbol.isa)==Symbol.get("speak"))
		{
			final String text = request.get (Symbol.get("string")).name.replace("\"", "");
			if (model.verboseTrace) model.output ("speech", "speak text "+text);
			eventTime = prepareMovement (eventTime, text);
			eventTime = initiateMovement (eventTime);
			model.addEvent (new Event (eventTime, "speech", "output-speech "+text) {
				public void action() {
					model.task.speak (text);
					sendVocalToAudio (text);
				}
			});
			eventTime += getArticulationTime(text);
			finishMovement (eventTime);
		}

		else if (request.get(Symbol.isa)==Symbol.get("subvocalize"))
		{
			final String text = request.get (Symbol.get("string")).name.replace("\"", "");
			if (model.verboseTrace) model.output ("speech", "speak text "+text);
			eventTime = prepareMovement (eventTime, text);
			eventTime = initiateMovement (eventTime);
			model.addEvent (new Event (eventTime, "", "output-subvocalize "+text) {
				public void action() {
					sendVocalToAudio (text);
				}
			});
			eventTime += getArticulationTime(text);
			finishMovement (eventTime);
		}
	}
}
