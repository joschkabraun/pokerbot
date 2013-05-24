package handHistory;

import gameBasics.PlayerYou;
import gameBasics.Action;

import java.util.*;

/**
 * This class implements a two-dimensional list.
 * An element of this list is a PlayerAction, so an element consists of a player and an action the player did.
 * 
 * @author Joschka J. Braun
 * @version 1.0
 */
@SuppressWarnings("serial")
public class PlayerActionList extends ArrayList<PlayerAction>

{
	
	public PlayerActionList()
	{
	}
	
	public PlayerActionList( PlayerActionList playActList )
	{
		for ( int i = 0; i < playActList.size(); i++ )
			this.add( playActList.get(i) );
	}
	
	public PlayerActionList( List<PlayerAction> list )
	{
		for ( int i = 0; i < list.size(); i++ )
			this.add( list.get(i) );
	}

	/**
	 * Returns a sublist of "this".
	 * 
	 * @return a PlayerActionList from fromIndex (inclusive) to toIndex (exclusive)
	 */
	public PlayerActionList sublist( int fromIndex, int toIndex )
	{
		PlayerActionList list = new PlayerActionList();
		for ( int i = fromIndex; i < toIndex; i++ )
			list.add( this.get(i) );
		return list;
	}
	
	@Override
	public String toString() {
		String s = String.format( "" );
		for ( int i = 0; i < this.size(); i++ )
			s += String.format( this.get(i).toString() + "%n" );
		return s;
	}
	
	public int howOftenFoldet()
	{
		int howOften = 0;
		for ( int i = 0; i < this.size(); i++ )
			if ( this.get(i).action.actionName.equals( "fold" ) )
				++howOften;
		return howOften;
	}
	
	public boolean haveAllFoldedBefore()
	{
		if ( this.size() == 0 )
			return true;
		else if ( ((double) this.howOftenFoldet() / this.size()) == 1 )
			return true;
		return false;
	}
	
	public int howOftenCalled()
	{
		int howOften = 0;
		for ( int i = 0; i <this.size(); i++ )
			if ( this.get(i).action.actionName.equals( "call" ) )
				++howOften;
		return howOften;
	}
	
	/**
	 * Returns the number of the raises inclusive the bet in this game.
	 * 
	 * @return the number of raises+bet in this game.
	 */
	public int howOftenBettedRaised()
	{
		int howOften = 0;
		for ( int i = 0; i <this.size(); i++ )
			if ( this.get(i).action.actionName.equals( "raise" ) || this.get(i).action.actionName.equals( "bet" ) )
				++howOften;
		return howOften;
	}
	
	public int howOftenHaveYouPlayed( PlayerYou you )
	{
		int howOften = 0;
		for ( int i = 0; i < this.size(); i++ )
			if ( this.get(i).player.name.equals( you.name ) )
				++howOften;
		return howOften;
	}
	
	
	/**
	 * Returns the number of calls after the last bet or raise.
	 * 
	 * @return number of calls after the last bet or raise
	 */
	public int howOftenCalledAfterLastBetRaise()
	{
		int howOften = 0;
		
		if ( this.howOftenBettedRaised() == 0 )
			System.err.println( "There was not any raise in this game this round, so you cannot use this method!" +
					"(class PreFlop, method howOftenCalledAfterLastRaise(), parameter " + this.toString() );
		else
		{
			ArrayList<String> actionsName = new ArrayList<String>();
			for ( int i = 0; i < this.size(); i++ )
				actionsName.add( this.get(i).action.actionName );
			int indexLastRaise = actionsName.lastIndexOf( "raises" );
			if ( indexLastRaise == -1 )
				indexLastRaise = actionsName.lastIndexOf( "bets" );
			
			for ( int i = (indexLastRaise+1); i < this.size(); i++ )
				if ( this.get(i).action.actionName.equals( "call" ) )
					++howOften;
		}
		
		return howOften;
	}
	
	public int howManyPlayerInGame( int playersLastStageInGame )
	{
		int howMany = 0;
		
		if ( this.howOftenBettedRaised() > 0 )
			howMany = this.howOftenCalledAfterLastBetRaise() + 1;
		else
			howMany = playersLastStageInGame;
		
		return howMany;
	}
	
	public int howOftenRaisedAfterFirstPlay( PlayerYou you )
	{
		int howOften = 0;
		
		if ( this.howOftenHaveYouPlayed( you ) == 0 )
			System.err.println( "You have not ever played before (class PlayerActionList, method howOftenRaisedAfterFirstPlay, this: " + this.toString() + ", player: " + you.toString() );
		else
		{
			ArrayList<String> player = new ArrayList<String>();
			for ( int i = 0; i < this.size(); i++ )
				player.add( this.get(i).player.name );
			int indexFirstPlay = player.indexOf( you.name );
			
			PlayerActionList listAfterFirstPlay = new PlayerActionList( this.sublist( indexFirstPlay+1, this.size() ) );
			howOften = listAfterFirstPlay.howOftenBettedRaised();
		}
		
		return howOften;
	}
	
	/**
	 * Returns whether you have ever raised in this.
	 * 
	 * @param you the player you are
	 * @return whether you have raised in this
	 */
	public boolean haveRaised( PlayerYou you )
	{
		ArrayList<String> actionNames = new ArrayList<String>();
		for (int i = 0; i < this.size(); i++)
			actionNames.add(this.get(i).action.actionName);

		int i = actionNames.indexOf("raise");
		PlayerActionList list = new PlayerActionList(this);
		while (i < this.size() - 1) {
			if (i == -1)
				break;
			if (list.get(i).player.name.equalsIgnoreCase(you.name))
				return true;
			else {
				list = list.sublist(i + 1, this.size());
				i = list.indexOf("raise");
			}
		}
		
		return false;
	}
	
	/**
	 * Returns whether you were the player who raised or bet at last.
	 * 
	 * @param you the player you are
	 * @return whether you raised/bet at last
	 */
	public boolean wasLastRaiser( PlayerYou you )
	{
		if ( this.howOftenBettedRaised() == 0 )
			return false;
		else
		{
			ArrayList<String> actionNames = new ArrayList<String>();
			for ( int i = 0; i < this.size(); i++ )
				actionNames.add( this.get(i).action.actionName );
			
			if ( this.howOftenBettedRaised() == 1 ) {
				int indexBet = actionNames.indexOf( "bet" );
				if ( this.get(indexBet).player.name.equalsIgnoreCase(you.name) )
					return true;
				else
					return false;
			}
			else {
				int indexLastRaise = actionNames.lastIndexOf( "raise" );
				if ( this.get(indexLastRaise).player.name.equalsIgnoreCase(you.name) )
					return true;
				else
					return false;
			}
		}
	}
	
	/**
	 * Returns the minimal needed action coming in the next round (flop, turn, river).
	 * 
	 * @return minimal needed action for the next round
	 */
	public Action actionMin()
	{
		Action act;
		act = (this.howOftenBettedRaised() > 0) ? new Action("call") : new Action("check");
		return act;
	}
	
}
