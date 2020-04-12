package com.cornchipss.com;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class Stairs
{
	/**
	 * Recursively checks how many different combinations
	 * of stairs a given n amount of bricks can make
	 * by recursively making smaller staircases
	 * @param n The # of bricks
	 * @return The # of possible staircases you could make with n bricks
	 */
	public static int solution(int n)
    {
        return solution(n, n + 1);
    }
	
	/**
	 * Finds the minimum ending height for a staircase for it to be valid
	 * @param n The total amount of bricks
	 * @return the minimum ending height for a staircase for it to be valid
	 */
	public static int findMin(int n)
	{
		int min = 0;
		
		int sum = 0;
		
		for(int i = 1; i < n; i++)
		{
			sum += i;
			
			if(sum >= n)
			{
				min = i;
				break;
			}
		}
		
		return min;
	}
    
	/**
	 * Recursively checks how many different combinations
	 * of stairs a given n amount of bricks can make
	 * by recursively making smaller staircases
	 * @param n The number of bricks in this set of stairs
	 * @param numBefore The number before this staircase, used to check for valid solutions
	 * @return The number of valid solutions
	 */
    public static int solution(int n, int numBefore)
	{
		if(n < 3)
			return 0;
		if(n < 5)
			return 1;
		
		// After this number, there is no point in checking any lower
		// Any staircase with a maximum point lower than this would be invalid
		int min = findMin(n);
		
		int solution = 0;
		
		for(int checking = n - 1; checking >= min; checking--)
		{
			if(checking < numBefore) // Makes sure this is a strictly decreasing staircase
			{
				if(checking > n - checking) // Makes sure this arrangement of stairs is legitimate before adding
					solution += 1;
				
				solution += solution(n - checking, checking);
			}
		}
		
		return solution;
	}
	
	public static void main(String[] args) throws Exception
	{
		Robot r = new Robot();
//		r.keyPress(KeyEvent.VK_V);
		while(true)
		{
			Thread.sleep(10000);
			r.keyPress(KeyEvent.VK_V);
			System.out.println("V");
		}
		//System.out.println("SOLUTION: " + solution(200));
	}
}
