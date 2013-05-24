package handHistory;

import cardBasics.CardList;

public class Summary

{
	
	
	// pot and sidepot
	
	/**
	 * It is the money the poker room deduct.
	 */
	public double rake;
	
	/**
	 * The open cards.
	 */
	public CardList board;
	
	public Summary() {
		this.board = new CardList();
	}
	
//	public Summary( args ... )
	
	public Summary( Summary summary )
	{
		
	}
	
	public boolean isEmpty() {
		if ( this.board.isEmpty() )
			return true;
		return false;
	}
	
}
