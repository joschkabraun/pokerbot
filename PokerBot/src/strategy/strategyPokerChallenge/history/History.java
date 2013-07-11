package strategy.strategyPokerChallenge.history;

/**
 * History
 *
 * @author Alex
 */

import strategy.strategyPokerChallenge.data.CONSTANT;

public class History {
	
	// Save aggregated data over the whole game
	public GlobalRoundData globalHistory = new GlobalRoundData();
	// This history is used to determine the behavior of the opponent
	// In general the global history could used but we must consider changes in the behavior
	// So if the behavior in the last hundred rounds differ to much from the one in the curHistory the curHistory will be set to this one
	public GlobalRoundData curHistory = new GlobalRoundData();
	
	// current round index
	public int roundCount = 0;
	
	public int ownPlayerID = -1;
	
	// define the global history in a singleton pattern
	public static History gameHistory = null;
	
	public synchronized static History getHistory() {
		if (gameHistory==null)
			gameHistory = new History();
		return gameHistory;
	}
	
	public synchronized static void setHistory( History h ) {
		History.gameHistory = h;
	}
	
	private History() {
		
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
	
	public boolean isActive() {
		return getCurRoundNumber() > CONSTANT.NUM_ROUNDS_BEFORE_HISTORY;
	}
	
}
