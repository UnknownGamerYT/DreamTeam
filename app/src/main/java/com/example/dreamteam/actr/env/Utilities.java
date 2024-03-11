package com.example.dreamteam.actr.env;

import java.util.Random;

public class Utilities
{
	public static void shuffle (Object a[])
	{
		Random random = new Random (System.currentTimeMillis());
		for (int i=0 ; i<50 ; i++)
		{
			int r1 = random.nextInt (a.length);
			int r2 = random.nextInt (a.length);
			if (r1 != r2)
			{
				Object pair1 = a[r1];
				a[r1] = a[r2];
				a[r2] = pair1;
			}
		}
	}
}
