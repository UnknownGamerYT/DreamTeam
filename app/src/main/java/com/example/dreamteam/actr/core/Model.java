package com.example.dreamteam.actr.core;

import android.widget.Toast;

import com.example.dreamteam.MainActivity;
import com.example.dreamteam.game_activity;

import java.net.URL;
//import com.example.dreamteam.actr.env.*;
import com.example.dreamteam.actr.core.task.Task;

public class Model
{
	String name;
	Task task;
	Declarative declarative;
	Procedural procedural;
	Vision vision;
	//Audio audio;
	Motor motor;
	Speech speech;
	Imaginal imaginal;
	Buffers buffers;
	Events events;

	private double time;
	private boolean stop;
	//private Main main;
	private boolean taskUpdated = false;

	public boolean realTime = false;
	boolean verboseTrace = true;
	boolean runUntilStop = false;
	boolean bufferStuffing = true;

	Model (String name,Task task)
	{
		this.name = name;
		this.task = task;
		//this.main = main;
		declarative = new Declarative (this);
		procedural = new Procedural (this);
		vision = new Vision (this);
		//audio = new Audio (this);
		motor = new Motor (this);
		speech = new Speech (this);
		imaginal = new Imaginal (this);
		buffers = new Buffers (this);
		events = new Events();
		time = 0;

		initialize();
	}


	public static Model load (URL url, String name,Task task)
	{
		try { return new Parser(url).parseModel (name , task); }
		catch (Exception e) { e.printStackTrace(); return null; }
	}

	public static Model parse (String text, String name,Task task)
	{
		try { return new Parser(text).parseModel (name, task); }
		catch (Exception e) { e.printStackTrace(); return null; }
	}


	public String getName() { return name; }
	public double getTime () { return time; }

	Chunk createBufferStateChunk (String buffer, boolean hasBuffer)
	{
		Chunk c = new Chunk (Symbol.get (buffer), this);
		c.set (Symbol.get("isa"), Symbol.get("buffer-state"));
		c.set (Symbol.get("state"), Symbol.get("free"));
		if (hasBuffer) c.set (Symbol.get("buffer"), Symbol.get("empty"));
		return c;
	}

	void initialize ()
	{
		declarative.initialize();
		procedural.initialize();
		vision.initialize();
		//audio.initialize();
		motor.initialize();
		speech.initialize();
		imaginal.initialize();

		buffers.set (Symbol.get("?goal"), createBufferStateChunk ("goal",true));
		buffers.set (Symbol.get("?retrieval"), createBufferStateChunk ("retrieval-state",true));
		buffers.set (Symbol.get("?visual-location"), createBufferStateChunk ("visloc-state",true));
		buffers.set (Symbol.get("?visual"), createBufferStateChunk ("visual-state",true));
		buffers.set (Symbol.get("?aural-location"), createBufferStateChunk ("aurloc-state",true));
		buffers.set (Symbol.get("?aural"), createBufferStateChunk ("aural-state",true));
		buffers.set (Symbol.get("?manual"), createBufferStateChunk ("manual-state",false));
		buffers.set (Symbol.get("?vocal"), createBufferStateChunk ("vocal-state",false));
		buffers.set (Symbol.get("?imaginal"), createBufferStateChunk ("imaginal-state",true));
	}

	void update ()
	{
		vision.update();
		//audio.update();
		motor.update();
		speech.update();
		declarative.update();
		imaginal.update();

		procedural.update();

		//		vision.update();
		//		audio.update();
		//		motor.update();
		//		speech.update();
		//		declarative.update();
		//		imaginal.update();
	}

	public void addEvent (Event event)
	{
		events.add (event);
	}

	public void removeEvents (Symbol module)
	{
		events.removeModuleEvents (module.name);
	}

	public void run ()
	{
		//System.out.println (procedural);

		stop = false;
		taskUpdated = false;
		task.start();

		addEvent (new Event (0.0, "procedural", "start") {
			public void action() {
				procedural.findInstantiations (buffers);
			}});

		while (!stop && (events.moreEvents() || runUntilStop))
		{
			if (events.moreEvents())
			{
				Event event = events.next();
				if (realTime)
				{
					try {
						if (event.getTime() > time)
							Thread.sleep ((long) Math.round(1000*(event.getTime() - time)));
					} catch (InterruptedException e) { e.printStackTrace(); System.exit(1); }
				}
				time = event.getTime();
				
				taskUpdated = false;
				if (verboseTrace && !event.getModule().equals("task") && !event.getModule().equals(""))
					output (event.getModule(), event.getDescription());
				event.action();

				if (!event.getModule().equals("procedural")
						&& (taskUpdated || !event.getModule().equals("task"))
						&& !events.scheduled("procedural"))
					procedural.findInstantiations (buffers);
			}
			else try { Thread.sleep (50); } catch (Exception e) { e.printStackTrace(); System.exit(1); }
		}
		if (verboseTrace) output ("------", "done");

		System.out.println (declarative);
	}


	public void stop ()
	{
		stop = true;
	}


	public void setParameter (String parameter, String value)
	{
		if (parameter.equals(":esc"))
		{ if (value.equals("nil")) warning ("unsupported parameter value: "+parameter+" "+value); }
		else if (parameter.equals(":v")) verboseTrace = !value.equals("nil");
		else if (parameter.equals(":real-time")) realTime = !value.equals("nil");
		else if (parameter.equals(":rus")) runUntilStop = !value.equals("nil");

		else if (parameter.equals(":ul")) procedural.utilityLearning = !value.equals("nil");
		else if (parameter.equals(":egs")) procedural.utilityNoiseS = Double.valueOf(value);
		else if (parameter.equals(":alpha")) procedural.utilityLearningAlpha = Double.valueOf(value);
		else if (parameter.equals(":epl")) procedural.productionLearning = !value.equals("nil");
		else if (parameter.equals(":iu")) procedural.initialUtility = Double.valueOf(value);
		else if (parameter.equals(":tt")) procedural.productionCompilationThresholdTime = Double.valueOf(value);
		else if (parameter.equals(":nu")) procedural.productionLearningNewUtility = Double.valueOf(value);
		else if (parameter.equals(":cst")) procedural.conflictSetTrace = !value.equals("nil");
		else if (parameter.equals(":pct")) procedural.productionCompilationTrace = !value.equals("nil");

		else if (parameter.equals(":rt")) declarative.retrievalThreshold = Double.valueOf(value);
		else if (parameter.equals(":lf")) declarative.latencyFactor = Double.valueOf(value);
		else if (parameter.equals(":bll"))
		{
			declarative.baseLevelLearning = (!value.equals("nil"));
			declarative.baseLevelDecayRate = (!value.equals("nil")) ? Double.valueOf(value) : 0;
		}
		else if (parameter.equals(":ol")) declarative.optimizedLearning = !value.equals("nil");
		else if (parameter.equals(":ans")) declarative.activationNoiseS = Double.valueOf(value);
		else if (parameter.equals(":ga")) declarative.goalActivation = Double.valueOf(value);
		else if (parameter.equals(":imaginal-activation")) declarative.imaginalActivation = Double.valueOf(value);
		else if (parameter.equals(":mas"))
		{
			declarative.spreadingActivation = (!value.equals("nil"));
			declarative.maximumAssociativeStrength = (!value.equals("nil")) ? Double.valueOf(value) : 0;
		}
		else if (parameter.equals(":mp"))
		{
			declarative.partialMatching = (!value.equals("nil"));
			declarative.mismatchPenalty = (!value.equals("nil")) ? Double.valueOf(value) : 0;
		}
		else if (parameter.equals(":declarative-num-finsts")) declarative.declarativeNumFinsts = Integer.valueOf(value);
		else if (parameter.equals(":declarative-finst-span")) declarative.declarativeFinstSpan = Double.valueOf(value);
		else if (parameter.equals(":act")) declarative.activationTrace = !value.equals("nil");

		else if (parameter.equals(":visual-attention-latency")) vision.visualAttentionLatency = Double.valueOf(value);
		else if (parameter.equals(":visual-movement-tolerance")) vision.visualMovementTolerance = Double.valueOf(value);
		else if (parameter.equals(":visual-num-finsts")) vision.visualNumFinsts = Integer.valueOf(value);
		else if (parameter.equals(":visual-finst-span")) vision.visualFinstSpan = Double.valueOf(value);
		else if (parameter.equals(":visual-onset-span")) vision.visualOnsetSpan = Double.valueOf(value);

		//else if (parameter.equals(":tone-detect-delay")) audio.toneDetectDelay = Double.valueOf(value);
		//else if (parameter.equals(":tone-recode-delay")) audio.toneRecodeDelay = Double.valueOf(value);
		//else if (parameter.equals(":digit-detect-delay")) audio.digitDetectDelay = Double.valueOf(value);
		//else if (parameter.equals(":digit-recode-delay")) audio.digitRecodeDelay = Double.valueOf(value);

		else if (parameter.equals(":motor-feature-prep-time")) motor.featurePrepTime = Double.valueOf(value);
		else if (parameter.equals(":motor-initiation-time")) motor.movementInitiationTime = Double.valueOf(value);
		else if (parameter.equals(":motor-burst-time")) motor.burstTime = Double.valueOf(value);
		else if (parameter.equals(":peck-fitts-coeff")) motor.peckFittsCoeff = Double.valueOf(value);
		else if (parameter.equals(":mouse-fitts-coeff")) motor.mouseFittsCoeff = Double.valueOf(value); // new
		else if (parameter.equals(":min-fitts-time")) motor.minFittsTime = Double.valueOf(value);
		else if (parameter.equals(":default-target-width")) motor.defaultTargetWidth = Double.valueOf(value);

		else if (parameter.equals(":syllable-rate")) speech.syllableRate = Double.valueOf(value);
		else if (parameter.equals(":char-per-syllable")) speech.charsPerSyllable = Integer.valueOf(value);
		else if (parameter.equals(":subvocalize-detect-delay")) speech.subvocalizeDetectDelay = Double.valueOf(value);

		else if (parameter.equals(":imaginal-delay")) imaginal.imaginalDelay = Double.valueOf(value);
		
		// below are new parameters

		else if (parameter.equals(":add-chunk-on-new-request")) declarative.addChunkOnNewRequest = !value.equals("nil");
		
		else warning("ignoring parameter "+parameter);
	}

	public void clearVisual ()
	{ 
		vision.clearVisual ();
		taskUpdated = true;
	}

	public void addVisual (String id, String type, String value, int x, int y, int w, int h, double d)
	{ 
		vision.addVisual (id, type, value, x, y, w, h, d);
		taskUpdated = true;
	}

	public void addVisual (String id, String type, String value, int x, int y, int w, int h)
	{ 
		addVisual (id, type, value, x, y, w, h, 0);
	}

	public void removeVisual (String id)
	{ 
		vision.removeVisual (id);
		taskUpdated = true;
	}

	//public void addAural (final String id, final String type, final String content)
	//{
	//	audio.addAural (id, type, content);
	//	taskUpdated = true;
	//}

	public void runCommand (String s)
	{
		try {
			new Parser (s).parseCommands (this);
		}
		catch (Exception e) {
			error (e.getMessage());
		}
	}

	public void printWhyNot ()
	{
		boolean old = procedural.whyNotTrace;
		procedural.whyNotTrace = true;
		procedural.findInstantiations (buffers);
		procedural.whyNotTrace = old;
	}

	public void printBuffers ()
	{
		output (buffers.toString());
	}

	public double getProductionUtility (String s)
	{
		Production p = procedural.get(Symbol.get(s));
		return (p==null) ? 0 : p.getUtility();
	}

	public String lastProductionFired ()
	{
		if (procedural.getLastFired() == null) return null;
		return procedural.getLastFired().getProduction().name.toString();
	}
	public void output (String resource, String s)
	{
		String out = String.format ("%9.3f   %-15s   %s", time, resource, s);
				System.out.println (out);
	}

	public void output (String s)
	{
			System.out.println (s);
	}

	public void warning (String error)
	{
		String out = String.format ("Warning: %s", error);
				System.out.println (out);
	//	main.output (out);
	}

	public void error (String error)
	{
		String out = String.format ("Error: %s", error);
				System.out.println (out);
		//main.output (out);
	}

	public String toString ()
	{
		String s = "Model:\n\n";
		s += "DM:\n" + declarative;
		s += "\nPS:\n" + procedural;
		s += "\nBuffers:\n" + buffers;
		return s;
	}
}
