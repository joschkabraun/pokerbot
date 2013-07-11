package handHistory;

import gameBasics.Pot;

public abstract class BettingRound {
	
	/**
	 * The list of the actions of each player. The list is sorted by the actions the players did, when the player's turn.
	 */
	public PlayerActionList playerActionList;
	
	/**
	 * The pot in this phase.
	 */
	public Pot pot;
	
	/**
	 * Returns the GameState of "this" at which strategy.strategyPokerChallenge.data.GameState is used.
	 * 
	 * @return the GameState of "this"; GameState in meaning of PokerChallenge(AKI-RealBot/TU Darmstadt)
	 */
	public abstract strategy.strategyPokerChallenge.data.GameState getPokerChallengeGameState();
	
}
