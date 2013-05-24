package cardBasics;

import cardBasics.CardCombination;

public class CardCombinationDraw

{
	
	public CardList cards;
	public String combination;
	public int combinationValue;
	public Card highCard;
	public double probability;
	
	public CardCombinationDraw()
	{
	}
	
	public CardCombinationDraw( CardCombination cardCombination, double probability )
	{
		this.cards = new CardList( cardCombination.cards );
		this.combination = cardCombination.combination;
		this.combinationValue = cardCombination.combinationValue;
		this.highCard = cardCombination.highCard;
		this.probability = probability;
	}
	
	public CardCombinationDraw( CardList cards, String combination, double probability )
	{
		this.cards = new CardList( cards );
		this.combination = combination;
		this.combinationValue = this.combinationClassification();
		this.highCard = this.getHighCard();
		this.probability = probability;
	}
	
	public CardCombinationDraw( CardCombinationDraw cardCombinationDraw )
	{
		this.cards = cardCombinationDraw.cards;
		this.combination = cardCombinationDraw.combination;
		this.combinationValue = cardCombinationDraw.combinationValue;
		this.highCard = cardCombinationDraw.highCard;
		this.probability = cardCombinationDraw.probability;
	}
	
	public CardCombinationDraw( Card[] cards, String combination, double probability )
	{
		this.cards = new CardList();
		for ( Card card : cards )
			this.cards.add( card );
		this.combination = combination;
		this.combinationValue = this.combinationClassification();
		this.highCard = this.getHighCard();
		this.probability = probability;
	}
	
	public CardCombinationDraw( String combination, double probability, Card... cards )
	{
		this.cards = new CardList( cards );
		this.combination = combination;
		this.combinationValue = this.combinationClassification();
		this.highCard = this.getHighCard();
		this.probability = probability;
	}
	
	public void set( CardList cards, String combination, double probability )
	{
		this.cards = new CardList( cards );
		this.combination = combination;
		this.combinationValue = this.combinationClassification();
		this.highCard = this.getHighCard();
		this.probability = probability;
	}
	
	public void set( Card[] cards, String combination, double probability )
	{
		for ( Card card : cards )
			this.cards.add( card );
		this.combination = combination;
		this.combinationValue = this.combinationClassification();
		this.highCard = this.getHighCard();
		this.probability = probability;
	}
	
	public void set( String combination, double probability, Card... cards )
	{
		for ( Card card : cards )
			this.cards.add( card );
		this.combination = combination;
		this.combinationValue = this.combinationClassification();
		this.highCard = this.getHighCard();
		this.probability = probability;
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
			throw new IllegalStateException( "It was not possible to classify the valueInt by the value for the CardCombinationDraw: " + this.toString() );
			
		return combinationClassification;
	}
	
	public Card getHighCard()
	{
		CardCombinationDraw copy = new CardCombinationDraw( this );
		copy.cards.sortByCardValue();
		Card highCard = new Card( copy.cards.get( copy.cards.size()-1 ) );
		return highCard;
	}
	
	@Override
	public String toString()
	{
		return "(" + this.cards.toString() + ", " + this.combination + ", " + this.probability + ")";
	}
	
}
