package handHistory;

import gameBasics.Action;
import gameBasics.PlayerYou;
import gameBasics.Pot;
import cardBasics.CardList;
import java.util.ArrayList;

public class PreFlop extends BettingRound

{
	
	/**
	 * The cards the dealer dealt to you. These are your start hand.
	 */
	public CardList startHand;
	
	/**
	 * The number of players they are in game.
	 */
	public int howManyPlayersInGame;
	
	public PreFlop() {
		this.startHand = new CardList();
		this.pot = new Pot();
	}
	
	public PreFlop( CardList startHand, PlayerActionList playActList, Pot pot )
	{
		this.startHand = new CardList( startHand );
		this.playerActionList = new PlayerActionList( playActList );
		this.pot = new Pot( pot );
	}
	
	public PreFlop( PreFlop preFlop )
	{
		this.startHand = new CardList( preFlop.startHand );
		this.playerActionList = new PlayerActionList( preFlop.playerActionList );
		this.pot = new Pot( preFlop.pot );
	}
	
	@Override
	public String toString() {
		return "start cards: " + this.startHand + ", players in game: " + this.howManyPlayersInGame + ", pot: " + this.pot +
				"%naction player list:%n" + this.playerActionList;
	}
	
	public boolean isEmpty() {
		if ( this.startHand.isEmpty() )
			return true;
		return false;
	}
	
	public int howOftenFolded()
	{
		int returnValue = 0;
		for ( int i = 0; i < this.playerActionList.size(); i++ )
			if ( this.playerActionList.get(i).action.actionName.equals( "folds" ) )
				++returnValue;
		return returnValue;
	}
	
	public boolean haveAllFoldedBefore()
	{
		if ( this.playerActionList.size() == 0 )
			return true;
		else if ( ((double) this.howOftenFolded() / this.playerActionList.size()) == 1 )
			return true;
		return false;
	}
	
	public int howOftenCalled()
	{
		int returnValue = 0;
		for ( int i = 0; i < this.playerActionList.size(); i++ )
			if ( this.playerActionList.get(i).action.actionName.equals( "calls" ) )
				++returnValue;
		return returnValue;
	}
	
	public int howOftenRaised()
	{
		int returnValue = 0;
		for ( int i = 0; i < this.playerActionList.size(); i++ )
			if ( this.playerActionList.get(i).action.actionName.equals( "raises" ) )
				++returnValue;
		return returnValue;
	}
	
	public int howOftenHaveYouPlayed( PlayerYou you )
	{
		int howOften = 0;
		for ( int i = 0; i < this.playerActionList.size(); i++ )
			if ( this.playerActionList.get(i).player.name.equals( you.name ) )
				++howOften;
		return howOften;
	}
	
	/**
	 * Returns the number of calls after the last raise, so you can look how many players are now in game.
	 * This function just can get used if there was a raise ever in this phase.
	 * 
	 * @return the number of calls after the last raise
	 */
	public int howOftenCalledAfterLastRaise()
	{
		int howOften = 0;
		
		if( this.howOftenRaised() == 0 )
			System.err.println( "There was not any raise in this game this round, so you cannot use this method!" +
					"(class PreFlop, method howOftenCalledAfterLastRaise(), parameter " + this.toString() );
		else
		{
			ArrayList<String> actionsName = new ArrayList<String>();
			for (int i = 0; i < this.playerActionList.size(); i++ )
				actionsName.add( this.playerActionList.get(i).action.actionName );
			int indexLastRaise = actionsName.lastIndexOf( "raises" );
			
			for ( int i = (indexLastRaise+1); i < this.playerActionList.size(); i++ )
				if ( this.playerActionList.get(i).action.actionName.equals( "calls" ) )
					++howOften;
		}
		
		return howOften;
	}
	
	/**
	 * Returns the number of players they are still in game.
	 * 
	 * @param numberOfBigBlinds the number of the big blinds. You can get it from HandHistory.bigBlind
	 * @return the number of players are in game
	 */
	public int howManyPlayersInGame( int numberOfBigBlinds )
	{
		int howOften = 0;
		
		if ( this.howOftenRaised() > 0 )
			howOften = this.howOftenCalledAfterLastRaise() + 1;
		else
			howOften = this.howOftenCalled() + numberOfBigBlinds; // Plus one about the big blind, the small blind still has to call.
		
		return howOften;
	}
	
	/**
	 * Returns the number of raises after your first play in this game.
	 * This function just can get used if you have already played in this phase.
	 * 
	 * @param you the player you are
	 * @return the number of raises after your first play in
	 */
	public int howOftenRaisedAfterFirstPlay( PlayerYou you )
	{
		int howOften = 0;
		
		if ( this.howOftenHaveYouPlayed( you ) == 0 )
			System.err.println( "You have not ever played before (class PreFlop, method howOftenRaisedAfterFirstPlay, this: " + this.toString() + ", player: " + you.toString() );
		else
		{
			ArrayList<String> player = new ArrayList<String>();
			for ( int i = 0; i < this.playerActionList.size(); i++ )
				player.add( this.playerActionList.get(i).player.name );
			int indexFirstPlay = player.indexOf( you.name );
			
			PlayerActionList listAfterFirstPlay = new PlayerActionList( this.playerActionList.subList( indexFirstPlay+1, this.playerActionList.size() ) );
			
			for ( int i = 0; i < listAfterFirstPlay.size(); i++ )
				if ( listAfterFirstPlay.get(i).action.actionName.equals( "raises" ) )
					++howOften;
		}
		
		return howOften;
	}
	
	/**
	 * Returns the GameState of "this" at which strategy.strategyPokerChallenge.data.GameState is used.
	 * 
	 * @return the GameState of "this"; GameState in meaning of PokerChallenge(AKI-RealBot/TU Darmstadt)
	 */
	public strategy.strategyPokerChallenge.data.GameState getPokerChallengeGameState() {
		return strategy.strategyPokerChallenge.data.GameState.PRE_FLOP;
	}
	
	/**
	 * Returns the minimal needed action coming in the flop-phase. The action depends on who is small and big blind.
	 * 
	 * @param hh the hand history of the actual game
	 * @param you the PlayerYou from which you want to know the minimal needed action
	 * @return minimal needed action coming in the flop-phase
	 */
	public static Action actionMin( HandHistory hh, PlayerYou you )
	{
		Action act;
		
		boolean areYouBlind = false;
		for ( int i = 0; i < hh.smallBlindP.size(); i++ )
			if ( hh.smallBlindP.get(i).name.equals( you.name ) )
				areYouBlind = true;
		for ( int i = 0; i < hh.bigBlindP.size(); i++ )
				if ( hh.bigBlindP.get(i).name.equals( you.name ) )
					areYouBlind = true;
		
		if ( areYouBlind )
			act = hh.preFlop.playerActionList.actionMin();
		else
			act = (hh.preFlop.playerActionList.howOftenBettedRaised() > 0) ? new Action("raise") : new Action("call");
		
		return act;	
	}
	
}
