package strategy.strategyPokerChallenge.history;

/**
 * History
 *
 * @author Alex
 */

import java.util.concurrent.ConcurrentHashMap;

import strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card;

import strategy.strategyPokerChallenge.ringclient.interfaces.StateChangeListener;
import strategy.strategyPokerChallenge.data.Action;
import strategy.strategyPokerChallenge.data.Bucket;
import strategy.strategyPokerChallenge.data.CONSTANT;
import strategy.strategyPokerChallenge.data.GameState;
import strategy.strategyPokerChallenge.data.Tools;

public class History implements StateChangeListener {
	
	// Save specific data for every Round
	private ConcurrentHashMap<Integer,OneRoundData> rounds = new ConcurrentHashMap<Integer,OneRoundData>();
	// Save aggregated data in every entry in the amount given by the variable "numOfSumRounds"
	private ConcurrentHashMap<Integer,GlobalRoundData> sumOfNRounds = new ConcurrentHashMap<Integer,GlobalRoundData>();
	// Save aggregated data over the whole game
	private GlobalRoundData globalHistory = new GlobalRoundData();
	// This history is used to determine the behavior of the opponent
	// In general the global history could used but we must consider changes in the behavior
	// So if the behavior in the last hundred rounds differ to much from the one in the curHistory the curHistory will be set to this one
	private GlobalRoundData curHistory = new GlobalRoundData();
	
	// current round index
	private int roundCount = 0;
	// current index of the partially aggregated data
	private int sumRoundCount = 0;
	
	// some variables to manage the stepwise construction of the OneRoundData
	private OneRoundData roundData;
	private GameState curGameState;
	private GameState lastStateBeforShowdown;
	
	public int ownPlayerID = -1;
	
	// the last evaluated values for the bucket probabilities (the a player has cards of a special bucket)
	private double bucketProbabilities[][] = new double[CONSTANT.PLAYER_COUNT][];
	// probabilities are evaluated after every action
	private boolean actionHasOccured = true;
	
	// define the global history in a singleton pattern
	private static History gameHistory = null;
	
	public synchronized static History getHistory() {
		if (gameHistory==null)
			gameHistory = new History();
		return gameHistory;
	}
	
	private History() {
		
	}
	
	public OneRoundData getOneRound(int i) {
		return this.rounds.get(new Integer(i));
	}
	
	public GlobalRoundData getSumOfNRounds(int i) {
		return this.sumOfNRounds.get(new Integer(i));
	}

	public synchronized GlobalRoundData getGlobal() {
		return this.globalHistory;
	}
	
	public synchronized GlobalRoundData getCurrent() {
		return this.curHistory;
	}
	
	public int getCurRoundNumber() {
		return this.roundCount;
	}
	
	public OneRoundData getLastRound() {
		return rounds.get(new Integer(rounds.size()-1));
	}
	
	public GlobalRoundData getLastCompletedSumRounds() {
		return sumOfNRounds.get(new Integer(sumOfNRounds.size()-2));
	}
	
	public GlobalRoundData getAggregatedData(int fromRound, int toRound) {
		GlobalRoundData result = new GlobalRoundData();

		if (fromRound<0 || toRound<0)
			return result;
		
		for (int i=fromRound; i<toRound; i++)
			result.addOneRoundData(rounds.get(new Integer(i)));
		
		return result;
	}
	
	public boolean isActive() {
		return getCurRoundNumber() > CONSTANT.NUM_ROUNDS_BEFORE_HISTORY;
	}
	
	public double[][] getCurrentBucketProbabilities() {
		if (bucketProbabilities == null || actionHasOccured)
			for (int player=0; player<CONSTANT.PLAYER_COUNT; player++) {
				// at first reset the array to default values
				bucketProbabilities[player] = CONSTANT.BUCKET_PREFLOP_PROB.clone();
				
				// if player has folded in the current round no computations are needed
				if (roundData.playerOut[player]==GameState.PREFLOP || roundData.playerOut[player]==GameState.STARTING)
					continue;
				
				double globRaise = curHistory.getNumRaises(GameState.PREFLOP, player);
				double globCall = curHistory.getNumCalls(GameState.PREFLOP, player);
				double globFold = curHistory.getNumFolds(GameState.PREFLOP, player) +
								  curHistory.getNumFolds(GameState.STARTING, player);
				double globAllActions = globRaise + globCall + (globFold*2);
				
				globRaise = globAllActions!=0 ? 1.0-(globRaise/globAllActions) : 0;				
				
				int roundRaise = roundData.getNumRaises(GameState.PREFLOP, player);
				int roundCall = roundData.getNumCalls(GameState.PREFLOP, player);
				double curRaiseRatio = 0;
				if (roundRaise+roundCall!=0)
					curRaiseRatio = ((double)roundRaise)/(double)(roundCall+roundRaise);
						  
				double value = globRaise * curRaiseRatio * CONSTANT.BUCKET_MAX_SHIFT;
				
				for (int i=3; i>=0; i--) {
					bucketProbabilities[player][i] -= CONSTANT.BUCKET_PREFLOP_PROB_DIFF[i]*
													  CONSTANT.BUCKET_PREFLOP_PROB[0]*
													  value;
				}
			}		
		
		actionHasOccured = false;
		return Tools.cloneArray(bucketProbabilities);
	}
	
	public void checkForChangedBehaviour() {
		GlobalRoundData lastNGames = sumOfNRounds.get(new Integer(sumRoundCount-1));
		
		if (lastNGames.getAmountWonByPlayers(this.ownPlayerID)>0)
			return;
		
		double lastPlayerRatio[][][] = curHistory.getPlayerRatio();
		double curPlayerRatio[][][] = lastNGames.getPlayerRatio();
		
		double stateErrors[] = new double[GameState.values().length];
		double playerError;
		
		for (int i=0; i<CONSTANT.PLAYER_COUNT; i++) {
						
			for (GameState state : GameState.values()) {
				stateErrors[state.ordinal()] = 0;
				for (Action action : Action.values())
					stateErrors[state.ordinal()] += Math.abs(curPlayerRatio[i][state.ordinal()][action.ordinal()]-
															 lastPlayerRatio[i][state.ordinal()][action.ordinal()]);
				stateErrors[state.ordinal()] /= Action.values().length;			
			}
			playerError = 0;
			for (double value : stateErrors)
				playerError += value;
			playerError /= GameState.values().length;
			
			if (playerError>=CONSTANT.PERCENT_BEHAVIOUR_CHANGE) {
				if (CONSTANT.DEBUG_ALEX)
					System.out.println("Behaviour of player " + i + " changed: " + playerError);
				curHistory.setPlayerData(lastNGames, i);
			}
			else if (CONSTANT.DEBUG_ALEX)
				System.out.println("No change in behavior of playser " + i + " with error " + playerError);
		}	
	}
	
	public synchronized void roundFinished(int ownID, int playerAtSeatZero, int[] amountWon, int[] inPot, Card hole[][]) {
		if (ownPlayerID == -1)
			ownPlayerID = ownID;
		// complete the data of the OneRoundData
		roundData.completeData(playerAtSeatZero, amountWon, inPot, hole);
		
		// save current round data
		this.rounds.put(new Integer(roundCount), roundData);
		
		// construct every "numOfSumRounds" a new aggregated data structure
		if (roundCount%CONSTANT.NUM_OF_SUM_ROUNDS==0) {
			if (sumRoundCount>1)
				checkForChangedBehaviour();
			sumOfNRounds.put(new Integer(sumRoundCount), new GlobalRoundData());
			sumRoundCount++;
		}
		sumOfNRounds.get(sumRoundCount-1).addOneRoundData(roundData, this.lastStateBeforShowdown);
		
		this.globalHistory.addOneRoundData(roundData, this.lastStateBeforShowdown);
		this.curHistory.addOneRoundData(roundData, this.lastStateBeforShowdown);
		
		this.roundCount++;
		if(CONSTANT.DEBUG_IMMI) {
			System.out.println();
			System.out.println("*** New History ***");
			System.out.println("Current History");
			System.out.println(curHistory.toString());
		}
		
		if (CONSTANT.DEBUG_ALEX) {
			System.out.println();
			System.out.println("*** New History ***");
			System.out.println("Global History");
			System.out.println(globalHistory.toString());
			System.out.println("Current History");
			System.out.println(curHistory.toString());
			double bettingRatio[][] = this.curHistory.getBettingRatio();
			for (int i=0; i<CONSTANT.PLAYER_COUNT; i++) {
				System.out.print("Spieler " + i + " betting ratios: ");
				for (GameState state : GameState.values())
					System.out.print(bettingRatio[i][state.ordinal()] + ",");
				System.out.println();
			}
			
			double test1[][][] =  this.curHistory.getPlayerRatio();
			
			for (int i=0; i<CONSTANT.PLAYER_COUNT; i++) {
				System.out.println("Spieler " + i + " ratios: ");
				for (GameState state : GameState.values()) {
					System.out.println(state.toString() + ": ");
					for (Action action : Action.values())
						System.out.print(action.toString() + "=" + test1[i][state.ordinal()][action.ordinal()] + " ");
					System.out.println(")");		
				}
				System.out.println();
			}
		
			double test[][][][] =  this.curHistory.getPlayerEstimate();
			
			for (int i=0; i<CONSTANT.PLAYER_COUNT; i++) {
				System.out.println("Spieler " + i + " Buckets: ");
				for (GameState state : GameState.values()) {
					System.out.println(state.toString() + ": ");
					for (int j=0; j<Bucket.BUCKET_COUNT; j++) {
						System.out.print("Bucket " + j + "(");
						for (Action action : Action.values())
							System.out.print(action.toString() + "=" + test[i][state.ordinal()][j][action.ordinal()] + " ");
						System.out.println(")");
					}			
				}
				System.out.println();
			}
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
		}
	}

	public synchronized void actionPerformed(int seat, int player, Action action) {
		if (curGameState==GameState.PREFLOP || curGameState==GameState.STARTING)
			actionHasOccured = true;
		roundData.addAction(player, action, curGameState);
	}

	public synchronized void stateChanged(GameState state) {
		if (state == GameState.STARTING)
			roundData = new OneRoundData();
		if ((state==GameState.SHOWDOWN && curGameState==GameState.RIVER) || state!=GameState.SHOWDOWN)
			lastStateBeforShowdown = state;
		curGameState = state; 
	}
}
