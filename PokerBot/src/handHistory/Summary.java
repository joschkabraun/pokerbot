package handHistory;

import cardBasics.CardList;

public class Summary

{
	
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
	
	public Summary( Summary summary )
	{
		this.board = summary.board;
		this.rake = summary.rake;
	}
	
	public boolean isEmpty() {
		if ( this.board.isEmpty() )
			return true;
		return false;
	}
	
}
