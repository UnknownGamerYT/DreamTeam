package actr.models.tutorial;

public class U1Count extends actr.task.Task
{
	public U1Count ()
	{
		super ("Unit1-Count");
	}
	
	public boolean check ()
	{
		return (model.lastProductionFired().contains("stop"));
	}
	
	public double score () { return -1; }
}
