package handHistory;

import gameBasics.Pot;
import cardBasics.Card;
import cardBasics.CardList;

public class River

{
	
	/**
	 * The next card the dealer opened for everyone.
	 */
	public Card river;
	
	/**
	 * The rest of the open cards. The CardList does not contain the river-card.
	 */
	public CardList restOpenCards;
	
	/**
	 * All open cards.
	 */
	public CardList board;
	
	/**
	 * The list of the actions of each player. The list is sorted by the actions the players did, when the player's turn.
	 */
	public PlayerActionList playerActionList;
	
	/**
	 * The pot in the pre-flop-phase.
	 */
	public Pot pot;
	
	/**
	 * The number of players they are in game.
	 */
	public int howManyPlayersInGame;
	
	public River() {
		this.river = new Card();
		this.restOpenCards = new CardList();
		this.board = new CardList();
		this.pot = new Pot();
	}
	
	public River( Card river, CardList restOpenCards, CardList board, PlayerActionList playActList, Pot pot, int playersBeforeInGame )
	{
		this.river = new Card( river );
		this.restOpenCards = new CardList( restOpenCards );
		this.board = new CardList( board );
		this.playerActionList = new PlayerActionList( playActList );
		this.pot = new Pot( pot );
		this.howManyPlayersInGame = this.howManyPlayersBeforeInGame( playersBeforeInGame );
	}
	
	public River( Card river, CardList restOpenCards, PlayerActionList playActList, Pot pot, int playersBeforeInGame )
	{
		this.river = new Card( river );
		this.restOpenCards = new CardList( restOpenCards );
		this.board = new CardList( restOpenCards );
		this.board.add( new Card( river ) );
		this.playerActionList = new PlayerActionList( playActList );
		this.pot = new Pot( pot );
		this.howManyPlayersInGame = this.howManyPlayersBeforeInGame( playersBeforeInGame );
	}
	
	public River( River river )
	{
		this.river = new Card( river.river );
		this.restOpenCards = new CardList( river.restOpenCards );
		this.board = new CardList( river.board );
		this.playerActionList = new PlayerActionList( river.playerActionList );
		this.pot = new Pot( river.pot );
		this.howManyPlayersInGame = river.howManyPlayersInGame;
	}
	
	@Override
	public String toString() {
		return "river card: " + this.river + ", board cards: " + this.board + ", pot: " + this.pot + ", many players in game: " + this.howManyPlayersInGame +
				"%naction player list:%n" + this.playerActionList;
	}
	
	public boolean isEmpty() {
		if ( this.board.isEmpty() )
			return true;
		return false;
	}
	
	public int howManyPlayersBeforeInGame( int playersBeforeInGame )
	{
		return this.playerActionList.howManyPlayerInGame( playersBeforeInGame );
	}
	
}
