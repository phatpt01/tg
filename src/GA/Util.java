package GA;

import java.util.Random;

public class Util implements Cons {
	static public boolean s_isDebug = true;

	private static Random r = new Random();

	private static long s_lTimer = k_iTIMER_NOT_SET_YET;

	public static int ABS(int value) {
		return (value < 0 ? -value : value);
	}

	public static boolean BOOL(int value) {
		if (value == k_iTRUE)
			return true;
		else
			return false;
	}

	public static int endTimer() {
		if (s_lTimer == k_iTIMER_NOT_SET_YET)
			return k_iTIMER_NOT_SET_YET;
		else {
			int ret = (int) (System.currentTimeMillis() - s_lTimer);
			s_lTimer = k_iTIMER_NOT_SET_YET;
			return ret;
		}
	}

	public static double getRandomProbability() {
		return r.nextDouble();
	}

	public static int getTimer() {
		if (s_lTimer == k_iTIMER_NOT_SET_YET)
			return k_iTIMER_NOT_SET_YET;
		else {
			int ret = (int) (System.currentTimeMillis() - s_lTimer - 990);
			return ret;
		}
	}

	public static void PRINT(String s) {
		if (s_isDebug)
			System.out.println(s);
	}

	public static boolean Random() {
		return r.nextBoolean();
	}

	public static int[] Random(int num) {
		int[] a = new int[num];
		for (int i = 0; i < num; i++)
			a[i] = r.nextInt() % 10;
		return a;
	}

	public static void setTimer() {
		s_lTimer = System.currentTimeMillis();
		System.out.println(s_lTimer);
	}

}
