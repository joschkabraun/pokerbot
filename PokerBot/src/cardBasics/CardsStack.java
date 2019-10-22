package cardBasics;

import java.util.*;

import cardBasics.Card.Rank;
import cardBasics.Card.Suit;

/**
 * 
 * @author Joschka J. Braun
 * @version 1.0
 */
@SuppressWarnings("serial")
public class CardsStack extends CardList

{
	
	public CardsStack( int iMax, int jMax )
	{
		if ( iMax <= 4 && jMax <= 13 )
		{
			Rank[] ranks = Rank.values();
			Suit[] suits = Suit.values();
			for (int i = 0; i < iMax; i++)
				for (int j = 0; j < jMax; j++)
					this.add(new Card(ranks[j], suits[i]));
		}

	}
	
	public CardsStack( CardsStack cardsStack )
	{
		for ( int i = 0; i < cardsStack.size(); i++ )
			this.add( new Card( cardsStack.get( i ) ) );
	}
	
	public CardsStack() {
		Rank[] ranks = Rank.values();
		Suit[] suits = Suit.values();
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 13; j++)
				this.add(new Card(ranks[j], suits[i]));
	}
	
	public void mixing( int howOften )
	{
		for ( int j = 0; j < howOften; j++ )
			for ( int i = 0; i < this.size(); i++ )
				this.set( i, this.get( (int) (Math.random()*52) ) );
	}
	
	public void mixing()
	{
		this.mixing( 5 );
	}
	
	public Card getCardOfStack()
	{
		Card card = new Card( this.get( 0 ) );
		this.remove( 0 );		
		return card;
	}
	
	public boolean includes( Card card )
	{
		if ( this.indexOf( card ) == -1 )
			return false;
		else
			return true;
	}
	
	@Override
	public CardsStack clone()
	{
		CardsStack copy = new CardsStack( 0, 0 );
		for ( int i = 0; i < this.size(); i++ )
			copy.add( new Card( this.get( i ) ) );
		return copy;
	}
	
	public int howManyCardsWithTheSameValueInThis( Card card )
	{
		int returnValue = 0;
		Suit[] suits = Suit.values();
		
		for ( int i = 0; i < 4; i++ )
			if ( card.getSuit() != suits[ i ] ) {
					Card card2 = new Card( card.getRank(), suits[ i ] );
					if ( this.includes( card2 ) )
						++returnValue;
				}
		
		return returnValue;
	}
	
	public int howManyCardsWithTheSameColourInThis( Card card ) {
		CardsStack copy = this.clone();
		List<CardList> colours = copy.separateColours();
		return colours.get(card.getSuit().toInt()).size();
	}
	
}
