package handHistory;

import gameBasics.Action;
import gameBasics.Pot;
import cardBasics.Card;
import cardBasics.CardList;

public class Turn extends BettingRound implements IPostFlop

{
	
	/**
	 * The next card the dealer opened for everyone.
	 */
	public Card turn;
	
	/**
	 * The rest of the open cards. The CardList does not contain the turn-card.
	 */
	public CardList restOpenCards;
	
	/**
	 * All open cards.
	 */
	public CardList board;
	
	/**
	 * The number of players they are in game.
	 */
	public int howManyPlayersInGame;
	
	public Turn() {
		this.turn = new Card();
		this.restOpenCards = new CardList();
		this.board = new CardList();
		this.pot = new Pot();
		this.playerActionList = new PlayerActionList();
	}
	
	public Turn( Card turn, CardList restOpenCards, CardList board, PlayerActionList playActList, Pot pot, int playersBeforeInGame )
	{
		this.turn = new Card( turn );
		this.restOpenCards = new CardList( restOpenCards );
		this.board = new CardList( board );
		this.playerActionList = new PlayerActionList( playActList );
		this.pot = new Pot( pot );
		this.howManyPlayersInGame = this.howManyPlayersInGame( playersBeforeInGame );
	}
	
	public Turn( Card turn, CardList restOpenCards, PlayerActionList playActList, Pot pot, int playersBeforeInGame )
	{
		this.turn = new Card( turn );
		this.restOpenCards = new CardList( restOpenCards );
		this.board = new CardList( restOpenCards );
		this.board.add( new Card( turn ) );
		this.playerActionList = new PlayerActionList( playActList );
		this.pot = new Pot( pot );
		this.howManyPlayersInGame = this.howManyPlayersInGame( playersBeforeInGame );
	}
	
	public Turn( Turn turn )
	{
		this.turn = new Card( turn.turn );
		this.restOpenCards = new CardList( turn.restOpenCards );
		this.board = new CardList( turn.board );
		this.playerActionList = new PlayerActionList( turn.playerActionList );
		this.pot = new Pot( turn.pot );
		this.howManyPlayersInGame = turn.howManyPlayersInGame;
	}
	
	@Override
	public String toString() {
		return "turn card: " + this.turn + ", board cards: " + this.board + ", pot: " + this.pot + ", many players in game: " + this.howManyPlayersInGame +
				"%naction player lsit:%n" + this.playerActionList;
	}
	
	public boolean isEmpty() {
		if ( this.board.isEmpty() )
			return true;
		return false;
	}
	
	public int howManyPlayersInGame( int playersBeforeInGame )
	{
		return this.playerActionList.howManyPlayerInGame( playersBeforeInGame );
	}
	
	@Override
	/**
	 * Returns the GameState of "this" at which strategy.strategyPokerChallenge.data.GameState is used.
	 * 
	 * @return the GameState of "this"; GameState in meaning of PokerChallenge(AKI-RealBot/TU Darmstadt)
	 */
	public strategy.strategyPokerChallenge.data.GameState getPokerChallengeGameState() {
		return strategy.strategyPokerChallenge.data.GameState.TURN;
	}
	
	@Override
	public Action actionMin() {
		return this.playerActionList.actionMin();
	}
	
}
