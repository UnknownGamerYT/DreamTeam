package actr.env;

public class Statistics
{
	public static double[] flatten (double a[][])
	{
		double b[] = new double[a.length*a[0].length];
		for (int i=0 ; i<a.length ; i++)
			for (int j=0 ; j<a[i].length ; j++)
				b[i*a.length+j] = a[i][j];
		return b;
	}

	public static double mean (double a[])
	{
		if (a.length == 0) return 0;
		double sum=0;
		for (int i=0 ; i<a.length ; i++) sum += a[i];
		return sum / a.length;
	}
	
	public static double average (double a[]) { return mean(a); }

	public static double stddev (double a[])
	{
		if (a.length == 0) return 0;
		double ma = mean(a);
		double sum = 0;
		for (int i=0 ; i<a.length ; i++) sum += Math.pow (a[i] - ma, 2);
		return Math.sqrt (sum / (a.length-1));
	}
	
	double stderr (double a[]) { return Math.sqrt (stddev(a)); }
	
	double confidence (double a[]) { return 1.96 * stderr(a); }

	public static double correlation (double a[], double b[])
	{
		if (a.length<=1 || (a.length != b.length)) return 0;
		double ma = mean(a), mb = mean(b);
		double sda = stddev(a), sdb = stddev(b);
		double sum = 0;
		for (int i=0 ; i<a.length ; i++) sum += (a[i]-ma)*(b[i]-mb);
		return sum / ((a.length-1) * sda * sdb);
	}

	public static double correlation (double a[][], double b[][])
	{
		return correlation (flatten(a), flatten(b));
	}
}
