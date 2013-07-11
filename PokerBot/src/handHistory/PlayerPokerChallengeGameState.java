package handHistory;

import strategy.strategyPokerChallenge.data.GameState;
import gameBasics.Player;

/**
 * This class implements a datastructure in which a player and a package strategy.strategyPokerChallenge.data.GameState is saved.
 * This datastructure is used for saving in which GameState the player is the first time out.
 */
public class PlayerPokerChallengeGameState {
	
	public Player player;
	
	public GameState gameState;
	
	public PlayerPokerChallengeGameState() {
		this.player = new Player();
	}
	
	public PlayerPokerChallengeGameState( Player p, GameState g ) {
		this.player = p;
		this.gameState = g;
	}
	
	@Override
	public boolean equals( Object o ) {
		if ( ! (o instanceof PlayerPokerChallengeGameState) )
			return false;
		else
			return this.equals(o);
	}
	
	public boolean equals( PlayerPokerChallengeGameState ppcgs ) {
		if ( this.player.equals(ppcgs.player) )
			if ( this.gameState.equals(ppcgs.gameState) )
				return true;
		return false;
	}
	
	@Override
	public String toString() {
		return "(" + this.player.toString() + ", " + this.gameState + ")";
	}
	
}
