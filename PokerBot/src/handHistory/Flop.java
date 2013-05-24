package handHistory;

import gameBasics.Pot;
import cardBasics.CardList;

public class Flop

{
	
	/**
	 * The cards the dealer opened for everyone.
	 */
	public CardList flop;
	
	/**
	 * All open cards.
	 */
	public CardList board;
	
	/**
	 * The list of the actions of each player. The list is sorted by the actions the players did, when the player's turn.
	 */
	public PlayerActionList playerActionList;
	
	/**
	 * The pot in the flop-phase.
	 */
	public Pot pot;
	
	/**
	 * The number of players they are in game.
	 */
	public int howManyPlayersInGame;
	
	public Flop() {
		this.flop = new CardList();
		this.board = new CardList();
		this.pot = new Pot();
	}
	
	public Flop( CardList flop, CardList board, PlayerActionList playActList, Pot pot, int playersInGameBefore )
	{
		this.flop = new CardList( flop );
		this.board = new CardList( board );
		this.playerActionList = new PlayerActionList( playActList );
		this.pot = new Pot( pot );
		this.howManyPlayersInGame = this.howManyPlayersInGame( playersInGameBefore );
	}
	
	public Flop( CardList flop, PlayerActionList playActList, int playersInGameBefore )
	{
		this.flop = new CardList( flop );
		this.board = new CardList( flop );
		this.playerActionList = new PlayerActionList( playActList );
		this.pot = new Pot( pot );
		this.howManyPlayersInGame = this.howManyPlayersInGame( playersInGameBefore );
	}
	
	public Flop( Flop flop )
	{
		this.flop = new CardList( flop.flop );
		this.board = new CardList( flop.board );
		this.playerActionList = new PlayerActionList( flop.playerActionList );
		this.pot = new Pot( pot );
		this.howManyPlayersInGame = flop.howManyPlayersInGame;
	}
	
	@Override
	public String toString() {
		return String.format( "board/flop cards: " + this.flop + ", pot: " + this.pot + ", many players in game: " + this.howManyPlayersInGame +
				"%naction player list:%n" + this.playerActionList );
	}
	
	public boolean isEmpty() {
		if ( this.board.isEmpty() )
			return true;
		return false;
	}
	
	public int howManyPlayersInGame( int playersInGameBefore )
	{
		return this.playerActionList.howManyPlayerInGame( playersInGameBefore );
	}
	
}
