package actr.models.tutorial;

public class U1Addition extends actr.task.Task
{
	public U1Addition ()
	{
		super ("Unit1-Addition");
	}
	
	public boolean check ()
	{
		return (model.lastProductionFired().contains("terminate-addition"));
	}
	
	public double score () { return -1; }
}
