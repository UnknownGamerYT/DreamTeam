package actr.core;

import java.util.Iterator;
import java.util.Random;

class Utilities
{
	static Random random = new Random();

	static double getNoise (double s)
	{
		if (s == 0) return 0;
		// tutorial says logistic distribution, but act-r code has normal approximation
		// normal is used below, derived from act-r code
		double p = Math.max (0.0001, Math.min (random.nextDouble(), 0.9999));
		return s * Math.log ((1.0 - p) / p);
	}
	
	static double deg2rad (double x) { return x * (Math.PI / 180.0); }
	static double rad2deg (double x) { return x * (180.0 / Math.PI); }
	
	static double viewingDistance = 15;
	static double pixelsPerInch = 72;

	static double pixels2angle (double pixels)
	{
		return rad2deg (Math.atan2 (pixels/pixelsPerInch, viewingDistance));
	}
	
	static double angle2pixels (double angle)
	{
		return viewingDistance * Math.tan(deg2rad(angle)) * pixelsPerInch;
	}
	            
	static double evalCompute (Iterator<String> it) throws Exception
	{
		String test = it.next();
		if (test.equals(")")) return -9999;
		if (!test.equals("(")) return Double.valueOf(test);
		String operator = it.next();
		double result = evalCompute(it);
		if (operator.equals("abs")) return Math.abs(result);
		double last;
		while (!((last=evalCompute(it)) == -9999))
		{
			if (operator.equals("+")) result += last;
			else if (operator.equals("-")) result -= last;
			else if (operator.equals("*")) result *= last;
			else if (operator.equals("/")) result /= last;
			else if (operator.equals("my/")) result = (last==0) ? 0 : result/last;
			else if (operator.equals("min")) result = (last<result) ? last : result;
			else if (operator.equals("max")) result = (last>result) ? last : result;
			else throw new Exception();
		}
		return result;
	}

	static boolean evalComputeCondition (Iterator<String> it)
	{
		try {
		it.next(); // "("
		String operator = it.next();
		double r1 = evalCompute(it);
		double r2 = evalCompute(it);
		//it.next(); // ")"
		if (operator.equals("=")) return (r1 == r2);
		else if (operator.equals("<>")) return (r1 != r2);
		else if (operator.equals("<")) return (r1 < r2);
		else if (operator.equals(">")) return (r1 > r2);
		else if (operator.equals("<=")) return (r1 <= r2);
		else if (operator.equals(">=")) return (r1 >= r2);
		else return false;
		} catch (Exception e) { e.printStackTrace(); System.exit(1); }
		return false;
	}
}
