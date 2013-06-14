package strategy.strategyPokerChallenge.data;

public class CONSTANT {
	
	public static int PLAYER_COUNT = 6;
	public static final boolean DEBUG = true;
	public static final boolean DEBUG_ALEX = false && DEBUG;
	public static final boolean DEBUG_IMMI = true && DEBUG;
	public static final boolean DEBUG_KAMI = false && DEBUG;
	public static final long HANDTIME = 7000;
	public static final long HANDCOUNT = 6000;
	
	// Give the mathematical probabilities for the five buckets in the preflop
	//public static final double BUCKET_PREFLOP_PROB[] = {0.65,0.14,0.11,0.07,0.03};
	public static final double BUCKET_PREFLOP_PROB[] = {0.65,0.79,0.9,0.97,1.0};
	public static final double BUCKET_PREFLOP_PROB_DIFF[] = {1.0,  0.6, 0.285714285714, 0.0857142857142, 0.0};
	// Give the max value in percent a value in the BUCKET_PREFLOP_PROB can be changed by the behaviour
	public static final double BUCKET_MAX_SHIFT = 1.0;
	// Number of counted actions per round and bucket we have to see explicit (player show his cards in showdown) to be significant
	public static final int SIGNIFICANT_ROUND_COUNT = 50;
	// Minimum percent difference of the over all behavior to the one of the last hundred rounds that denote a change in the behaviour of the oppentn   
	public static final double PERCENT_BEHAVIOUR_CHANGE = 0.03;
	// denote the number of Rounds to aggregate to check for behavior changes
	public final static int NUM_OF_SUM_ROUNDS = 500;
	// number of rounds the history will be used
	public final static int NUM_ROUNDS_BEFORE_HISTORY = 100;
	public static final double A_PREFLOP = -2.0;
	
	public static double[] AGGRESSIVE_PREFLOP;
	
	public static long NODE_ID = 0;
	public static final double A_RAISE = 15.0;
	public static double[] AGGRESSIVE_RAISE;
	
	public static long getNodeId() {
		return NODE_ID++;
	}
}
