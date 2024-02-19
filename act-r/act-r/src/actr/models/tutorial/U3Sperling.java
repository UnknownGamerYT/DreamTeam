package actr.models.tutorial;

import actr.task.TaskLabel;

public class U3Sperling extends actr.task.Task
{
	String response = "";

	public U3Sperling ()
	{
		super ("Unit3-Sperling");
		String letters[] = {"V", "N", "T", "Z", "C", "R", "Y", "K", "W", "J", "G", "F"};
		for (int i=0 ; i<3 ; i++)
			for (int j=0 ; j<4 ; j++)
			{
				String letter = letters[i*4 + j];
				TaskLabel label = new TaskLabel (letter, 75+j*50, 101+i*50, 40, 20);
				add (label);
			}
	}

	public void start ()
	{
		response = "";
		processDisplay();
		model.addAural ("sound", "sound", "1000");
	}

	public void typeKey (char c)
	{
		response += c;
	}

	public boolean check ()
	{
		return response.length()==5 && model.lastProductionFired().contains("stop-report");
	}

	public double score () { return -1; }
}
