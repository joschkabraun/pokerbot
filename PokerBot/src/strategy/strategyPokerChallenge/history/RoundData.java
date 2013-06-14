package strategy.strategyPokerChallenge.history;

/**
 * Round data
 *
 * @author Alex
 */

import java.util.concurrent.ConcurrentHashMap;

import strategy.strategyPokerChallenge.data.CONSTANT;
import strategy.strategyPokerChallenge.data.GameState;

public class RoundData {
	
	protected ConcurrentHashMap<GameState,int[]> getSampleMap() {
		GameState values[] = GameState.values();
		ConcurrentHashMap<GameState,int[]> result = new ConcurrentHashMap<GameState,int[]>();
		
		for (int i=0; i<values.length; i++) {
			int zeroArray[] = new int[CONSTANT.PLAYER_COUNT];
			for (int j=0; j<CONSTANT.PLAYER_COUNT; j++)
				zeroArray[j] = 0;
			result.put(values[i], zeroArray);
		}
		return result;
	}
	
	protected ConcurrentHashMap<GameState,int[]> numRaises = getSampleMap();
	protected ConcurrentHashMap<GameState,int[]> numCalls = getSampleMap();
	protected ConcurrentHashMap<GameState,int[]> numFolds =  getSampleMap();
	protected int totalNumRaises[] = new int[CONSTANT.PLAYER_COUNT];
	protected int totalNumCalls[] = new int[CONSTANT.PLAYER_COUNT];
	protected int totalNumFolds[] = new int[CONSTANT.PLAYER_COUNT];
	
	protected int mostWinningPlayer = -1;
	protected float totalJackpot = 0;
	protected int amountWonByPlayers[] = new int[CONSTANT.PLAYER_COUNT];
	protected int inPotFromPlayers[] = new int[CONSTANT.PLAYER_COUNT];
	
	public RoundData () {
		for (int i=0; i<CONSTANT.PLAYER_COUNT; i++) {
			totalNumRaises[i] = 0;
			totalNumCalls[i] = 0;
			totalNumFolds[i] = 0;
		}
	}
	
	public RoundData(RoundData data) {
		for (GameState state : GameState.values()) {
			this.numCalls.put(state, data.numCalls.get(state).clone());
			this.numRaises.put(state, data.numRaises.get(state).clone());
			this.numFolds.put(state, data.numFolds.get(state).clone());
		}
		this.totalNumCalls = data.totalNumCalls.clone();
		this.totalNumRaises = data.totalNumRaises.clone();
		this.totalNumFolds = data.totalNumFolds.clone();
		this.mostWinningPlayer = data.mostWinningPlayer;
		this.totalJackpot = data.totalJackpot;
		this.amountWonByPlayers = data.amountWonByPlayers.clone();
		this.inPotFromPlayers = data.inPotFromPlayers.clone(); 
	}
	
	public int getMostWinningPlayer() {
		return mostWinningPlayer;
	}

	public float getTotalJackpot() {
		return totalJackpot;
	}
	
	public int getAmountWonByPlayers(int player) {
		if (player>=0 && player<CONSTANT.PLAYER_COUNT)
			return amountWonByPlayers[player];
		else
			return 0;
	}
	
	public int getNumCalls(GameState state, int player) {
		if (player>=0 && player<CONSTANT.PLAYER_COUNT)
			return this.numCalls.get(state)[player];
		else
			return 0;
	}
	
	public int getNumRaises(GameState state, int player) {
		if (player>=0 && player<CONSTANT.PLAYER_COUNT)
			return this.numRaises.get(state)[player];
		else
			return 0;
	}

	public int getNumFolds(GameState state, int player) {
		if (player>=0 && player<CONSTANT.PLAYER_COUNT)
			return this.numFolds.get(state)[player];
		else
			return 0;
	}

	public int getTotalNumCalls(int player) {
		if (player>=0 && player<=CONSTANT.PLAYER_COUNT)
			return totalNumCalls[player];
		else
			return 0;
	}
	
	public int getTotalNumRaises(int player) {
		if (player>=0 && player<CONSTANT.PLAYER_COUNT)
			return totalNumRaises[player];
		else
			return 0;
	}
	
	public int getTotalNumFolds(int player) {
		if (player>=0 && player<CONSTANT.PLAYER_COUNT)
			return totalNumFolds[player];
		else
			return 0;
	}

	public synchronized void setAmountWonByPlayers(int[] amountWonByPlayers) {
		this.amountWonByPlayers = amountWonByPlayers.clone();
		this.mostWinningPlayer = 0;
		for (int i=1; i<CONSTANT.PLAYER_COUNT; i++)
			if (this.amountWonByPlayers[i]>this.amountWonByPlayers[this.mostWinningPlayer])
				this.mostWinningPlayer = i;
	}
	
	public synchronized void setInPotFromPlayers(int[] inPotFromPlayers) {
		this.inPotFromPlayers = inPotFromPlayers.clone();
		this.totalJackpot = 0;
		for (int i=1; i<CONSTANT.PLAYER_COUNT; i++)
			this.totalJackpot += this.inPotFromPlayers[i];
	}
	
	public synchronized void addToNumCalls(GameState state, int player, int amount) {
		this.numCalls.get(state)[player] += amount;
		this.totalNumCalls[player] += amount;
	}
	
	public synchronized void addToNumRaises(GameState state, int player, int amount) {
		this.numRaises.get(state)[player] += amount;
		this.totalNumRaises[player] += amount;
	}
	
	public synchronized void addToNumFolds(GameState state, int player, int amount) {
		this.numFolds.get(state)[player] += amount;
		if (state != GameState.SHOWDOWN)
			this.totalNumFolds[player] += amount;
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		
		result.append("----------------------------------------\r\n");
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
