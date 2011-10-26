package tests;

import java.util.Random;

import massim.Board;

public class RandomTester {

	/**
	 * @param args
	 */
	//static 	Random rnd1 = new Random(12);
	static Random rnd2 = new Random();
	
	static int[] oc = {0,0,0,0,0,0};
	public static void main(String[] args) {
		
		testDoubleRandom();

	}

	public static void testRandom() {
	for (int i=0;i<100;i++)
	{
		printRandom();
		System.out.println("----");
	}

	for (int i=0;i<6;i++)
		System.out.printf("Occrance of %d = %d\n",i,oc[i]);
	}
	public static void printRandom() {
		Random rnd1 = new Random();
		
		for (int i=0;i<100;i++)	
		{
			int n = rnd1.nextInt(6);
			oc[n]++;
			System.out.printf("%d  ", n);
		}
		System.out.println();
		
		for (int i=0;i<100;i++)	
			System.out.printf("%d  ", rnd2.nextInt(6));
		System.out.println();
	}
	
	public static void testDoubleRandom() {
		double prob = 0.0;	
		Random rnd = new Random();
		
		for (int p=0;p<11;p++)
		{
			int loopCount = 0;
			int changeCount = 0;
			prob=p*0.1;
			for (int i=0;i<60000;i++)
			{
				if (rnd.nextDouble() < prob)
					changeCount++;
				loopCount++;
			}
				
			System.out.printf("The loop count = %d, the changes count = %d, changes/loop = %f\n", loopCount,changeCount,(double)changeCount/loopCount);
		}
		    
	}
	public static void testBoard() {
		
		Board board = Board.randomBoard(10, 10);
		
		
	}
	
}
