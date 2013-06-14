package strategy.strategyPokerChallenge.history;

/**
 * OneRoundData
 *
 * @author,Alex
 */

import strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card;
import strategy.strategyPokerChallenge.data.Action;
import strategy.strategyPokerChallenge.data.CONSTANT;
import strategy.strategyPokerChallenge.data.GameState;

public class OneRoundData extends RoundData {

	public int playerAtSeatZero = -1;
	/** Show the GameState every player has fold in this round */
	public GameState playerOut[] = new GameState[CONSTANT.PLAYER_COUNT];
	public Card hole[][];

	private boolean playerHasDoneAnything[] = new boolean[CONSTANT.PLAYER_COUNT];
	public int playerOutBettingRounds[] = new int[CONSTANT.PLAYER_COUNT];
	public int numBetsPerState[] = new int[GameState.values().length];

	public OneRoundData() {
		for (int i = 0; i < CONSTANT.PLAYER_COUNT; i++) {
			playerOut[i] = GameState.SHOWDOWN;
			playerHasDoneAnything[i] = false;
			playerOutBettingRounds[i] = 0;
			numBetsPerState[i] = 0;
		}
	}
	
	public void completeData(int playerAtSeatZero, int[] amountWon, int[] inPot, Card hole[][]) {
		this.playerAtSeatZero = playerAtSeatZero;
		setAmountWonByPlayers(correctArray(playerAtSeatZero, amountWon));
		setInPotFromPlayers(correctArray(playerAtSeatZero, inPot));
		this.hole = new Card[CONSTANT.PLAYER_COUNT][2];
		for (int i = 0; i < hole.length; i++)
			if(hole[i]!=null && hole[i].length == 2)
				for (int j = 0; j < hole[i].length; j++) {
					Card card = hole[i][j];
					if (card != null)
						hole[i][j] = new Card(card.rank, card.suit);
					else {
						hole[i][j] = null;
					}
				}
	}

	public void addAction(int player, Action action, GameState state) {
		switch (action) {
		case FOLD:
			if (!playerHasDoneAnything[player])
				playerOut[player] = GameState.STARTING;
			else {
				playerOut[player] = state;
				playerOutBettingRounds[player] = numBetsPerState[state.ordinal()];
			}
			addToNumFolds(state, player, 1);
			break;
		case CALL:
			addToNumCalls(state, player, 1);
			break;
		case RAISE:
			addToNumRaises(state, player, 1);
			numBetsPerState[state.ordinal()]++;
			break;
		default:
			System.out.println("Error while handle action: Unknown action '"
					+ action.toChar() + "'");
		}
		playerHasDoneAnything[player] = true;
	}

	private int[] correctArray(int playerAtSeatZero, int[] array) {
		int correctedArray[] = array.clone();
		for (int i = 0; i < CONSTANT.PLAYER_COUNT; i++)
			correctedArray[(i + playerAtSeatZero) % CONSTANT.PLAYER_COUNT] = array[i];
		return correctedArray;
	}
}
