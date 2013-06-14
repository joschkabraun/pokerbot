package strategy.strategyPokerChallenge.history;

/**
 * GlobalRoundData
 *
 * @author Alex
 */

import java.util.ArrayList;

import strategy.strategyPokerChallenge.data.Action;
import strategy.strategyPokerChallenge.data.Bucket;
import strategy.strategyPokerChallenge.data.CONSTANT;
import strategy.strategyPokerChallenge.data.GameState;
import strategy.strategyPokerChallenge.data.SimulationGuide;
import strategy.strategyPokerChallenge.data.Tools;

public class GlobalRoundData extends RoundData {
	
	private int numsOfRoundsAdded = 0;

	// Ratio of raise / fold / call in the added games
	private double playerRatio[][][] = new double[CONSTANT.PLAYER_COUNT][GameState.values().length][Action.values().length];
	// Counter array of actions we have seen (player has played up to the showdown and have to show his card) in the past
	private int playerBuckets[][][][] = new int[CONSTANT.PLAYER_COUNT][GameState.values().length][Bucket.BUCKET_COUNT][Action.values().length];
	// A merge between playerRatio and playerBuckets ==> if less den CONSTANT.SIGNIFICANT_ROUND_COUNT actions 
	// are measured the playerRatio is used. Otherwise the bucket-ratio will be used
	private double playerEstimate[][][][] = new double[CONSTANT.PLAYER_COUNT][GameState.values().length][Bucket.BUCKET_COUNT][Action.values().length];
	// Percent a player call (or initialize per raise by his own) the betting rounds in every state 
	private double bettingRatio[][] = new double[CONSTANT.PLAYER_COUNT][GameState.values().length];
	// Counter for the played betting rounds per State
	private int bettingAddCount[][] = new int[CONSTANT.PLAYER_COUNT][GameState.values().length];
	// Counter for the number of times a player has play up to this state
	private int playerRoundCount[][] = new int[CONSTANT.PLAYER_COUNT][GameState.values().length];
	// Amount of money we had grab from the other players (including negative values)
	private double playerGrab[] = new double[CONSTANT.PLAYER_COUNT];
	
	public GlobalRoundData() {
		for (int i=0; i<CONSTANT.PLAYER_COUNT; i++) {
			for (GameState state : GameState.values())
				bettingRatio[i][state.ordinal()] = 1.0;
		}
		this.resetRatio();
	}
	
	public GlobalRoundData(GlobalRoundData data) {
		super(data);
		this.numsOfRoundsAdded = data.numsOfRoundsAdded;

		this.playerRatio = Tools.cloneArray(data.playerRatio);
		this.playerBuckets = Tools.cloneArray(data.playerBuckets);
		this.playerEstimate = Tools.cloneArray(data.playerEstimate);
		this.bettingRatio = Tools.cloneArray(data.bettingRatio);
	}
	
	/** The array is ordered in the first dimension by the number of the players, in the second according 
	 * 	to the six gamestates (STARTING and SHOWDOWN is not used) and in the third to the three actions
	 *  (like in the actual enums) */
	
	public double[][][] getPlayerRatio() {
		return Tools.cloneArray(playerRatio);
	}
	
	public double getAmountWonFromPlayer(int player) {
		if (player<0 || player>=CONSTANT.PLAYER_COUNT)
			return 0;
		return this.playerGrab[player];
	}
	
	public double getPlayerRatio(int player, GameState state, Action action) {
		return this.playerRatio[player][state.ordinal()][action.ordinal()];
	}
	/**
	 * This array is very similar to the playerRatio except that the ratio will be given per bucket
	 * and additionally changing in the behavior will be respected
	 * @return
	 */
	public synchronized double[][][][] getPlayerEstimate() {
		return Tools.cloneArray(playerEstimate);
	}
	
	public double[][] getBettingRatio() {
		return Tools.cloneArray(this.bettingRatio);
	}
	
	public int getNumsOfRoundsAdded() {
		return numsOfRoundsAdded;
	}
	
	public synchronized void addOneRoundData(OneRoundData data) {
		this.addOneRoundData(data, GameState.SHOWDOWN);
	}
	
	public synchronized void addOneRoundData(OneRoundData data, GameState finalState) {
		if (amountWonByPlayers == null)
			amountWonByPlayers = new int[CONSTANT.PLAYER_COUNT];
		if (inPotFromPlayers == null)
			inPotFromPlayers = new int[CONSTANT.PLAYER_COUNT];
		
		for (int i=0; i<CONSTANT.PLAYER_COUNT; i++) {
			this.amountWonByPlayers[i] += data.amountWonByPlayers[i];
			this.inPotFromPlayers[i] += data.inPotFromPlayers[i];
		}
		
		this.totalJackpot+=data.totalJackpot;
		
		this.mostWinningPlayer = 0;
		int tmpCount = 0;
		int playerBucket = -1;
		ArrayList<Integer> winningPlayers = new ArrayList<Integer>();
		boolean weAreWinning = false;
		double playerGrabRound[] = new double[CONSTANT.PLAYER_COUNT];
		
		// evaluate the players that has win in the current round
		for (int player = 0; player < CONSTANT.PLAYER_COUNT; player++)
			if (data.amountWonByPlayers[player]>0) {
				if (player==History.getHistory().ownPlayerID)
					weAreWinning = true;
				winningPlayers.add(new Integer(player));
			}
		
		if (CONSTANT.DEBUG_ALEX) {
			System.out.println();
			System.out.println();
			System.out.print("Winning players: ");
			for (int i=0; i<winningPlayers.size(); i++)
				System.out.print(winningPlayers.get(i).intValue());
			System.out.println();
		}
		// use this to compute the amount win (lost) from every player
		if (weAreWinning) {
			for (int player = 0; player < CONSTANT.PLAYER_COUNT; player++)
				if (data.amountWonByPlayers[player]<0)
					playerGrabRound[player] = (data.amountWonByPlayers[player]*-1.0)/(double)winningPlayers.size();
		}
		else {
			for (int i=0; i<winningPlayers.size(); i++)
				playerGrabRound[winningPlayers.get(i).intValue()] = data.amountWonByPlayers[History.getHistory().ownPlayerID]/(double)winningPlayers.size();
		}
			
		// add the computed values to global list
		for (int player = 0; player < CONSTANT.PLAYER_COUNT; player++)
			this.playerGrab[player] += playerGrabRound[player];
		
		if (CONSTANT.DEBUG_ALEX) {
			System.out.println("Own ID: " + History.getHistory().ownPlayerID);
			System.out.print("Won from players: ");
			for (int i=0; i<CONSTANT.PLAYER_COUNT; i++)
				System.out.print(this.playerGrab[i]+",");
			System.out.println();
			System.out.println();
			System.out.println();
		}
		
		for (int player = 0; player < CONSTANT.PLAYER_COUNT; player++) {			
			playerBucket = (data.hole[player] != null) ? Bucket.getBucket(data.hole[player]) : -1;
			
			if (amountWonByPlayers[player] > amountWonByPlayers[mostWinningPlayer])
				mostWinningPlayer = player;
			
			for(GameState state: GameState.values()) {
				// if the game was not play up to the showdown we don't need to do any more
				if (state.ordinal()>finalState.ordinal())
					continue;
				
				tmpCount = data.getNumCalls(state, player);
				this.addToNumCalls(state, player, tmpCount);
				this.playerRatio[player][state.ordinal()][Action.CALL.ordinal()] += tmpCount;
				if (playerBucket>=0)
					playerBuckets[player][state.ordinal()][playerBucket][Action.CALL.ordinal()] += tmpCount;
				
				tmpCount = data.getNumRaises(state,player);
				this.addToNumRaises(state, player, tmpCount);
				this.playerRatio[player][state.ordinal()][Action.RAISE.ordinal()] += tmpCount;
				// if the opponent had has to shown his cards count these explicit per bucket
				if (playerBucket>=0)
					playerBuckets[player][state.ordinal()][playerBucket][Action.RAISE.ordinal()] += tmpCount;

				tmpCount = data.getNumFolds(state,player);
				this.addToNumFolds(state, player, tmpCount);
				this.playerRatio[player][state.ordinal()][Action.FOLD.ordinal()] += tmpCount;
				// if the opponent had has to shown his cards count these explicit per bucket
				if (playerBucket>=0)
					playerBuckets[player][state.ordinal()][playerBucket][Action.FOLD.ordinal()] += tmpCount;

				// in every gamestate smaller then the one the player has folded he has played all betting rounds
				if (state.ordinal() > GameState.STARTING.ordinal() && state.ordinal() < GameState.SHOWDOWN.ordinal()) {
					if (state.ordinal() < data.playerOut[player].ordinal())
						this.bettingRatio[player][state.ordinal()] =
							(this.bettingRatio[player][state.ordinal()]*(double)bettingAddCount[player][state.ordinal()]+1.0)/(double)(bettingAddCount[player][state.ordinal()]+1);
					if (state.ordinal() > data.playerOut[player].ordinal())
						this.bettingRatio[player][state.ordinal()] =
							(this.bettingRatio[player][state.ordinal()]*(double)bettingAddCount[player][state.ordinal()]+0.0)/(double)(bettingAddCount[player][state.ordinal()]+1);
					if (state.ordinal() != data.playerOut[player].ordinal())
						bettingAddCount[player][state.ordinal()]++;
				}
				
				// counting the rounds a player has played per state
				if (state.ordinal()<=data.playerOut[player].ordinal())
					if (data.playerOut[player]==GameState.STARTING)
						playerRoundCount[player][GameState.PREFLOP.ordinal()]++;
					else
						playerRoundCount[player][state.ordinal()]++;
					
			}
			
			// additionally the percent of played betting rounds in the last round of the player has to be added
			int state = data.playerOut[player].ordinal();
			if (state>GameState.STARTING.ordinal() && state<GameState.SHOWDOWN.ordinal()) {
				//double addingValue = data.numBetsPerState[state]!=0 ? (double)data.playerOutBettingRounds[player]/(double)data.numBetsPerState[state] : 0;
				double addingValue = 1.0/((double)data.playerOutBettingRounds[player]+1);
				this.bettingRatio[player][state] =
					(this.bettingRatio[player][state]*(double)bettingAddCount[player][state]+addingValue)/(double)(bettingAddCount[player][state]+1);
				bettingAddCount[player][state]++;
			}
		}
		this.fillRatio();
		
		if (CONSTANT.DEBUG_ALEX) {
			for (int i=0; i<CONSTANT.PLAYER_COUNT; i++) {
				System.out.println("Spieler " + i + " RoundCount: ");
				for (GameState state : GameState.values())
					System.out.print(playerRoundCount[i][state.ordinal()]+",");
				System.out.println();
			}
		}
		
		numsOfRoundsAdded++;
	}
	
	private synchronized void resetRatio() {
		for (int i=0; i<CONSTANT.PLAYER_COUNT; i++)
			for (int j=0; j<GameState.values().length; j++) {
				playerRatio[i][j][0] = 1;
				playerRatio[i][j][0] = 0;
				playerRatio[i][j][0] = 0;
			}
	}
	
	private synchronized void fillRatio() {
		double tmpSum, foldRatio, remaningProb;
		for (int player=0; player<CONSTANT.PLAYER_COUNT; player++) {
			
			for(GameState state: GameState.values()) {
				this.playerRatio[player][state.ordinal()][0] = getNumRaises(state,player);
				this.playerRatio[player][state.ordinal()][1] = getNumCalls(state,player);
				this.playerRatio[player][state.ordinal()][2] = getNumFolds(state,player);
			}
			
			playerRatio[player][1][2] += playerRatio[player][0][2];
			playerRatio[player][0][2] = 0;
			
			for(GameState state: GameState.values()) {
				foldRatio = 0;
				if ((double)playerRoundCount[player][state.ordinal()]>0)
					foldRatio = (double)getNumFolds(state,player)/(double)playerRoundCount[player][state.ordinal()];
				this.playerRatio[player][state.ordinal()][2] = foldRatio;
				
				tmpSum = playerRatio[player][state.ordinal()][0];
				tmpSum += playerRatio[player][state.ordinal()][1];
				
				if (tmpSum==0 && foldRatio==0) {
					// if no value is set assume a probability of 100% for raise
					playerRatio[player][state.ordinal()][0] = 1.0;
					playerRatio[player][state.ordinal()][1] = 0.0;
					playerRatio[player][state.ordinal()][2] = 0.0;
					continue;
				}
				
				if (tmpSum>0) {
					remaningProb = 1.0 - foldRatio;
					playerRatio[player][state.ordinal()][0] = (playerRatio[player][state.ordinal()][0]/tmpSum)*remaningProb;
					playerRatio[player][state.ordinal()][1] = (playerRatio[player][state.ordinal()][1]/tmpSum)*remaningProb;
				}
			}
		}
		this.fillPlayerEstimate();
	}
	
	private synchronized void fillPlayerEstimate() {
		int actionCount;
		double tmpSum;
		
		for (int player=0; player<CONSTANT.PLAYER_COUNT; player++) {
			for (GameState state : GameState.values()) {
				for (int j=0; j<Bucket.BUCKET_COUNT; j++) {
					actionCount = 0;
					for (Action action : Action.values())
						actionCount += playerBuckets[player][state.ordinal()][j][action.ordinal()];
					if (actionCount<CONSTANT.SIGNIFICANT_ROUND_COUNT || j==0)
						playerEstimate[player][state.ordinal()][j] = playerRatio[player][state.ordinal()].clone();
					else {
						tmpSum = 0.0;
						for (Action action : Action.values())
							tmpSum += playerBuckets[player][state.ordinal()][j][action.ordinal()];
						for (Action action : Action.values())
							playerEstimate[player][state.ordinal()][j][action.ordinal()] = 
								(double)playerBuckets[player][state.ordinal()][j][action.ordinal()] / tmpSum;
					}
				}			
			}
		}
		
		SimulationGuide.setEstimates();
	}
	
	/**
	 * This method offer the possibility to replace the data of one player through the one of an other data set
	 * 
	 * @param data new data source
	 * @param player id of the player
	 */
	public synchronized void setPlayerData(GlobalRoundData data, int player) {
		totalNumCalls[player] = data.totalNumCalls[player];
		totalNumRaises[player] = data.totalNumRaises[player];
		totalNumFolds[player] = data.totalNumFolds[player];
		for (GameState state : GameState.values()) {
			numCalls.get(state)[player] =  data.numCalls.get(state)[player];
			numRaises.get(state)[player] =  data.numRaises.get(state)[player];
			numFolds.get(state)[player] = data.numFolds.get(state)[player];
		}
		
		this.playerBuckets[player] = Tools.cloneArray(data.playerBuckets[player]);
		this.playerGrab[player] = data.playerGrab[player];
			
		this.bettingAddCount[player] = data.bettingAddCount[player].clone();
		this.playerRoundCount[player] = data.playerRoundCount[player].clone();
		
		this.fillRatio();
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		
		result.append("----------------------------------------\r\n");
		result.append("Global history after " + numsOfRoundsAdded + " added rounds from player "+History.getHistory().ownPlayerID+"\r\n\r\n");
		for (int i=0; i<CONSTANT.PLAYER_COUNT; i++) {
			result.append("  data for player " + i + ":\r\n");
			result.append("    raises per state  =");
			for (GameState state : GameState.values())
				result.append(" " + numRaises.get(state)[i]);
			result.append(" / " + totalNumRaises[i] + "\r\n");
			result.append("    calls  per state  =");
			for (GameState state : GameState.values())
				result.append(" " + numCalls.get(state)[i]);
			result.append(" / " + totalNumCalls[i] + "\r\n");
			result.append("    folds per state  =");
			for (GameState state : GameState.values())
				result.append(" " + numFolds.get(state)[i]);
			result.append(" / " + totalNumFolds[i] + "\r\n");
			result.append("    over all winning = " + amountWonByPlayers[i] + "\r\n");
			result.append("    over all in pot  = " + inPotFromPlayers[i] + "\r\n");
		}
		result.append("----------------------------------------\r\n");
		
		return result.toString();
	}
	
}
