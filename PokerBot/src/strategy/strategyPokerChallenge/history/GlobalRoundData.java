package strategy.strategyPokerChallenge.history;

/**
 * GlobalRoundData
 *
 * @author Alex
 */

import strategy.strategyPokerChallenge.data.Action;
import strategy.strategyPokerChallenge.data.Bucket;
import strategy.strategyPokerChallenge.data.CONSTANT;
import strategy.strategyPokerChallenge.data.GameState;
import strategy.strategyPokerChallenge.data.Tools;

public class GlobalRoundData {
	
	public int numsOfRoundsAdded = 0;

	// Ratio of raise / fold / call in the added games
	public double playerRatio[][][] = new double[CONSTANT.PLAYER_COUNT][GameState.values().length][Action.values().length];
	// Counter array of actions we have seen (player has played up to the showdown and have to show his card) in the past
	public int playerBuckets[][][][] = new int[CONSTANT.PLAYER_COUNT][GameState.values().length][Bucket.BUCKET_COUNT][Action.values().length];
	// A merge between playerRatio and playerBuckets ==> if less den CONSTANT.SIGNIFICANT_ROUND_COUNT actions 
	// are measured the playerRatio is used. Otherwise the bucket-ratio will be used
	public double playerEstimate[][][][] = new double[CONSTANT.PLAYER_COUNT][GameState.values().length][Bucket.BUCKET_COUNT][Action.values().length];
	// Percent a player call (or initialize per raise by his own) the betting rounds in every state 
	public double bettingRatio[][] = new double[CONSTANT.PLAYER_COUNT][GameState.values().length];
	// Amount of money we had grab from the other players (including negative values)
	public double playerGrab[] = new double[CONSTANT.PLAYER_COUNT];
	
	public GlobalRoundData() {
		for (int i=0; i<CONSTANT.PLAYER_COUNT; i++) {
			for (GameState state : GameState.values())
				bettingRatio[i][state.ordinal()] = 1.0;
		}
		this.resetRatio();
	}
	
	public GlobalRoundData(GlobalRoundData data) {
		this.numsOfRoundsAdded = data.numsOfRoundsAdded;
		this.playerRatio = Tools.cloneArray(data.playerRatio);
		this.playerBuckets = Tools.cloneArray(data.playerBuckets);
		this.playerEstimate = Tools.cloneArray(data.playerEstimate);
		this.bettingRatio = Tools.cloneArray(data.bettingRatio);
		this.playerGrab = data.playerGrab.clone();
	}
	
	public double getAmountWonFromPlayer(int player) {
		if (player<0 || player>=CONSTANT.PLAYER_COUNT)
			return 0;
		return this.playerGrab[player];
	}
	
	public double getPlayerRatio(int player, GameState state, Action action) {
		return this.playerRatio[player][state.ordinal()][action.ordinal()];
	}
	
	private synchronized void resetRatio() {
		for (int i=0; i<CONSTANT.PLAYER_COUNT; i++)
			for (int j=0; j<GameState.values().length; j++) {
				playerRatio[i][j][0] = 1;
				playerRatio[i][j][0] = 0;
				playerRatio[i][j][0] = 0;
			}
	}
	
	/**
	 * This array is very similar to the playerRatio except that the ratio will be given per bucket
	 * and additionally changing in the behavior will be respected
	 * @return
	 */
	public synchronized double[][][][] getPlayerEstimate() {
		return Tools.cloneArray(playerEstimate);
	}
	
}
