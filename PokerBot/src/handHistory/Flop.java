package handHistory;

import gameBasics.Action;
import gameBasics.Pot;
import cardBasics.CardList;

public class Flop extends BettingRound implements IPostFlop

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
	 * The number of players they are in game.
	 */
	public int howManyPlayersInGame;
	
	public Flop() {
		this.flop = new CardList();
		this.board = new CardList();
		this.pot = new Pot();
		this.playerActionList = new PlayerActionList();
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
		this.pot = new Pot( flop.pot );
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
	
	@Override
	/**
	 * Returns the GameState of "this" at which strategy.strategyPokerChallenge.data.GameState is used.
	 * 
	 * @return the GameState of "this"; GameState in meaning of PokerChallenge(AKI-RealBot/TU Darmstadt)
	 */
	public strategy.strategyPokerChallenge.data.GameState getPokerChallengeGameState() {
		return strategy.strategyPokerChallenge.data.GameState.FLOP;
	}
	
	@Override
	public Action actionMin() {
		return this.playerActionList.actionMin();
	}
	
}
