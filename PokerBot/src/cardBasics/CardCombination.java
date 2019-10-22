package cardBasics;

import java.util.Comparator;

import cardBasics.CardCombinationList;

/**
 * 
 * @author Joschka J. Braun
 * @version 1.0
 */
public class CardCombination implements Comparator<CardCombination>

{
	
	public CardList cards;
	public String combination;
	public int combinationValue;
	public Card highCard;
	
	public CardCombination() {
	}
	
	public CardCombination( CardList cards2, String combination )
	{
		this.cards = new CardList( cards2 );
		this.combination = combination;
		this.combinationValue = this.combinationClassification();
		this.highCard = this.getHighCard();
	}
	
	public CardCombination( CardCombination cardCombination )
	{
		this.cards = cardCombination.cards;
		this.combination = cardCombination.combination;
		this.combinationValue = cardCombination.combinationValue;
		this.highCard = cardCombination.highCard;
	}
	
	public CardCombination( Card[] cards, String combination )
	{
		this.cards = new CardList();
		for ( Card card : cards )
			this.cards.add( card );
		this.combination = combination;
		this.combinationValue = this.combinationClassification();
		this.highCard = this.getHighCard();		
	}
	
	public CardCombination( String combination, Card... cards )
	{
		this.cards = new CardList( cards );
		this.combination = combination;
		this.combinationValue = this.combinationClassification();
		this.highCard = this.getHighCard();
	}
	
	@Override
	public int compare( CardCombination cardCombination1, CardCombination cardCombination2 )
	{
		if ( cardCombination1.combinationValue < cardCombination2.combinationValue )
			return -1;
		else if( cardCombination1.combinationValue == cardCombination2.combinationValue )
		{
			if ( cardCombination1.highCard.getRank().toInt() < cardCombination2.highCard.getRank().toInt() )
				return -1;
			else if ( cardCombination1.highCard.getRank().toInt() == cardCombination2.highCard.getRank().toInt() )
				return 0;
			else
				return 1;
		}
		else
			return 1;
	}
	
	public int combinationClassification()
	{
		int combinationClassification = 0;
		
		switch( this.combination )
		{
		case "highCard":
			combinationClassification = 1;
			break;
		case "onePair":
			combinationClassification = 2;
			break;
		case "pair":
			combinationClassification = 2;
			break;
		case "twoPair":
			combinationClassification = 3;
			break;
		case "threeOfAKind":
			combinationClassification = 4;
			break;
		case "straight":
			combinationClassification = 5;
			break;
		case "flush":
			combinationClassification = 6;
			break;
		case "fullHouse":
			combinationClassification = 7;
			break;
		case "fourOfAKind":
			combinationClassification = 8;
			break;
		case "straightFlush":
			combinationClassification = 9;
			break;
		case "royalFlush":
			combinationClassification = 10;
			break;
		}
		
		if ( combinationClassification == 0 )
			throw new IllegalStateException( "It was not possible to classify the combinationValue by the combination for the CardCombination: " + this.toString() );
		
		return combinationClassification;
	}
	
	public Card getHighCard()
	{
		CardCombination copy = new CardCombination( this );
		copy.cards.sortByCardValue();
		Card highCard = new Card( copy.cards.get( copy.cards.size()-1 ) );
		return highCard;
	}
	
	public boolean equals( CardCombination cardCombination )
	{
		if ( this.combinationValue == cardCombination.combinationValue )
			if ( this.highCard == cardCombination.highCard )
				return true;
			else
				return false;
		else
			return false;
	}
	
	public void set( CardList list, String combination )
	{
		this.cards = new CardList( list );
		this.combination = combination;
		this.combinationValue = this.combinationClassification();
		this.highCard = this.getHighCard();
	}
	
	public void set( CardCombination cc )
	{
		this.cards = cc.cards;
		this.combination = cc.combination;
		this.combinationValue = cc.combinationValue;
		this.highCard = cc.highCard;
	}
	
	/**
	 * Returns the list in which the both card combination are sorted by them combination value.
	 * The first element of the list is the card combination which is higher than the other.
	 * 
	 * @param cc1 the first card combination you want to test
	 * @param cc2 the second card combination you want to test
	 * @return list of the card combinations sorted by them combination value
	 */
	public static CardCombinationList higherCardCombination( CardCombination cc1, CardCombination cc2 )
	{
		CardCombinationList resultOfHigherCardCombination = new CardCombinationList();
		
		if ( cc1.combinationValue == cc2.combinationValue )
		{
			CardCombination higherCardCombination = ( cc1.highCard.getRank().toInt() > cc2.highCard.getRank().toInt() ) ?  cc1 : cc2;
			
			if ( cc1.equals(higherCardCombination) )
			{
				resultOfHigherCardCombination.add( cc1 );
				resultOfHigherCardCombination.add( cc2 );
			} else {
				resultOfHigherCardCombination.add( cc2 );
				resultOfHigherCardCombination.add( cc1 );
			}
		}
		else
		{
			CardCombination higherCardCombination = ( cc1.combinationValue > cc2.combinationValue ) ? 
					cc1 : cc2;
			
			if ( cc1.equals( higherCardCombination ) ) {
				resultOfHigherCardCombination.add( cc1 );
				resultOfHigherCardCombination.add( cc2 );
			} else
			{
				resultOfHigherCardCombination.add( cc2 );
				resultOfHigherCardCombination.add( cc1 );
			}
		}
		
		return resultOfHigherCardCombination;
	}
	
	@Override
	public String toString()
	{
		return "(" + this.cards.toString() + ", " + this.combination + ")";
	}
	
}
