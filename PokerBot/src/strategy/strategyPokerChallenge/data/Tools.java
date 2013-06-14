package strategy.strategyPokerChallenge.data;

/**
*
* Created on 21.05.2008
* @author Kami II
*/
public class Tools {

	public static double[][] cloneArray(double[][] array) {
		double[][] ca = array.clone();
		for (int x = 0; x < array.length; x++)
			ca[x] = ca[x].clone();
		return ca;
	}

	public static double[][][] cloneArray(double[][][] array) {
		double[][][] ca = array.clone();
		for(int index = 0; index < ca.length; index++)
			ca[index] = cloneArray(ca[index]);
		return ca;
	}

	public static double[][][][] cloneArray(double[][][][] array) {
		double[][][][] ca = array.clone();
		for(int index = 0; index < ca.length; index++)
			ca[index] = cloneArray(ca[index]);
		return ca;
	}
	
	public static int[][] cloneArray(int[][] array) {
		int[][] ca = array.clone();
		for (int x = 0; x < array.length; x++)
			ca[x] = array[x].clone();
		return ca;
	}

	public static int[][][] cloneArray(int[][][] array) {
		int[][][] ca = array.clone();
		for(int index = 0; index < ca.length; index++)
			ca[index] = cloneArray(ca[index]);
		return ca;
	}

	public static int[][][][] cloneArray(int[][][][] array) {
		int[][][][] ca = array.clone();
		for(int index = 0; index < ca.length; index++)
			ca[index] = cloneArray(ca[index]);
		return ca;
	}
}
