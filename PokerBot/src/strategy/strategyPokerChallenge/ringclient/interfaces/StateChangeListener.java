package strategy.strategyPokerChallenge.ringclient.interfaces;

import strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card;
import strategy.strategyPokerChallenge.data.Action;
import strategy.strategyPokerChallenge.data.GameState;

/**
 * The listener interface that listens to changes of the ClientRingDynamics
 * Created on 14.04.2008
 * @author Kami II
 */
public interface StateChangeListener {

	/**
	 * this method is called every time an action is performed by the player 
	 * currently playing
	 * @param seat the seat the player sits in
	 * @param action the action the player has performed
	 */
	public void actionPerformed(int seat, int player, Action action);
	
	/**
	 * is called when the game state has changed
	 * @param state the new game state
	 */
	public void stateChanged(GameState state);
	
	/**
	 * 
	 * @param ownID own number
	 * @param playerAtSeatZero number of player at seat zero
	 * @param amountWon array with the amount won by all players ordered by seats (like in ringdynamics)
	 * @param inPot array with the amount in Pot by all players ordered by seats (like in ringdynamics)
	 * @param hole cardset that is shown after the game
	 */
	public void roundFinished(int ownID, int playerAtSeatZero, int[] amountWon, int[] inPot, Card hole[][]);
}
