package actr.models.tutorial;

import actr.task.TaskLabel;

public class U2Demo extends actr.task.Task
{
	TaskLabel label;

	public U2Demo ()
	{
		super ("Unit2-Demo");
		label = new TaskLabel ("A", 100, 100, 40, 20);
		add (label);
	}
	
	public void start ()
	{
		label.setText("a");
		processDisplay();
	}
	
	public void typeKey (char c)
	{
		label.setText("-");
	}

	public boolean check ()
	{
		return (model.lastProductionFired().contains("respond"));
	}
	
	public double score () { return -1; }
}
