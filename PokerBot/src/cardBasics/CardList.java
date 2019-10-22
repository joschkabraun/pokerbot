package cardBasics;

import java.util.*;

import cardBasics.Card.Rank;
import cardBasics.Card.Suit;

@SuppressWarnings("serial")
public class CardList extends ArrayList<Card>

{
	
	public CardList()
	{
	}
	
	public CardList( Card... cards )
	{
		for ( Card card : cards )
			this.add( card );
	}
	
	public CardList( CardList cardList )
	{
		for ( int i = 0; i < cardList.size(); i++ )
			this.add( new Card( cardList.get( i ) ) );
	}
	
	public CardList( CardList... cardList1 )
	{
		for ( CardList cardList : cardList1 )
			for ( int i = 0; i < cardList.size(); i++ )
				this.add( cardList.get( i ) );
	}
	
	public CardList(CardList list, Card card1, Card card2)
	{
		for ( int i = 0; i < list.size(); i++ )
			this.add( new Card(list.get(i)) );
		this.add( new Card( card1 ) );
		this.add( new Card( card2 ) );
	}

	Comparator<Card> compOfValueInt = new CardComparatorByValueInt();
	
	public void sortByCardValue()
	{
		Card[] thisAsArray = new Card[ this.size() ];
		
		for ( int i = 0; i < this.size(); i++ )
			thisAsArray[ i ] = this.get( i );
		
		Arrays.sort( thisAsArray, compOfValueInt );
		
		this.clear();
		
		for ( int i = 0; i < thisAsArray.length; i++ )
			this.add( thisAsArray[ i ] );
	}
	
	public void sortByCardValueReturnvalueJustValue()
	{
		Card[] thisAsArray = new Card[ this.size() ];
		CardList thisJustValueInt = new CardList();
		
		for ( int i = 0; i < this.size(); i++ ) {
			thisJustValueInt.add(i, new Card( this.get(i).getRank(), Suit.UNDEFINED ));
			thisAsArray[ i ] = thisJustValueInt.get( i );
		}
		
		Arrays.sort( thisAsArray, compOfValueInt );
		
		this.clear();
		
		for ( int i = 0; i < thisAsArray.length; i++ )
			this.add( new Card( thisAsArray[ i ] ) );
	}
	
	Comparator<Card> compOfColourToInt = new CardComparatorByColourToInt();
	
	public void sortByColour()
	{
		Card[] thisAsArray = this.toArray();
		Arrays.sort( thisAsArray, compOfColourToInt );
		
		this.clear();
		for ( int i = 0; i < thisAsArray.length; i++ )
			this.add( new Card( thisAsArray[ i ] ) );		
	}
	
	public void remove( Card card )
	{
		int removeInt = this.indexOf( card );
		if ( removeInt != -1 )
			this.remove( removeInt );
	}
	
	public void remove( Card... cards )
	{
		for ( Card card : cards )
			this.remove( card );
	}
	
	public void remove( CardList cardList )
	{
		for ( int i = 0; i < cardList.size(); i++ )
			this.remove( cardList.get( i ) );
	}
	
	public boolean isOneColour()
	{
		Suit suitOfFirst = this.get(0).getSuit();
		
		for ( int i = 1; i < this.size(); i++ )
		{
			if ( this.get(i).getSuit() != suitOfFirst )
				return false;
		}
		
		return true;
	}
	
	public Card getHighCard()
	{
		CardList copy = this.clone();
		copy.sortByCardValue();
		return copy.get( copy.size()-1 );
	}
	
	/**
	 * Returns whether is cardList sublist of this. This function just can be used if number of elements of cardList is more than 4.
	 * 
	 * @param cardList the list of cards to be tested is sublist of this
	 * @return true if cardList is sublist and false if cardList is not a sublist of this
	 */
	public boolean isSubCardListOfArg( CardList cardList )
	{
		CardList copy = this.clone();
		copy.sortByCardValueReturnvalueJustValue();
		cardList.sortByCardValueReturnvalueJustValue();
		
		int isOrIsNot = 0;
		
		CardList cardListNew = new CardList( cardList.get( cardList.size() -1 ), cardList.get( 0 ), cardList.get( 1 ),
				cardList.get( 2 ), cardList.get( 3 ), cardList.get( 4 ) );
		
		if ( copy.equals( cardListNew ) )
			isOrIsNot++;
		
		for ( int i = 0; i < (cardList.size() - 5); i++ )
		{
			if ( copy.equals( cardList.subList( i, i+5 ) ) )
				isOrIsNot++;
		}
		
		if ( isOrIsNot == 0 )
			return true;
		else
			return false;
	}
	
	@Override
	public CardList clone()
	{
		CardList copy = new CardList();
		for ( int i = 0; i < this.size(); i++ )
			copy.add( new Card( this.get( i ) ) );
		return copy;
	}
	
	@Override
	public Card[] toArray()
	{
		Card[] thisAsArray = new Card[ this.size() ];
		for ( int i = 0; i < this.size(); i++ )
			thisAsArray[ i ] = new Card( this.get( i ) );
		return thisAsArray;
	}
	
	public ArrayList<CardList> separateColours()
	{
		ArrayList<CardList> returnValue = new ArrayList<CardList>();
		
		CardList copy = this.clone();
		copy.sortByCardValue();
		
		CardList spades = new CardList();
		CardList hearts = new CardList();
		CardList diamonds = new CardList();
		CardList clubs = new CardList();
		
		returnValue.add( 0, spades );
		returnValue.add( 1, hearts );
		returnValue.add( 2, diamonds );
		returnValue.add( 3, clubs );
		
		for ( int i = 0; i < copy.size(); i++ )
		{
			if ( copy.get(i).getSuit() == Suit.SPADES )
				spades.add( copy.get(i) );
			else if ( copy.get(i).getSuit() == Suit.HEARTS )
				hearts.add( copy.get(i) );
			else if ( copy.get(i).getSuit() == Suit.DIAMONDS )
				diamonds.add( copy.get(i) );
			else if ( copy.get(i).getSuit() == Suit.CLUBS )
				clubs.add( copy.get(i) );
		}
		
		return returnValue;
	}
		
	public void add( CardList cardList )
	{
		for ( int i = 0; i < cardList.size(); i++ )
			this.add( cardList.get( i ) );
	}
	
	public void add( Card... cards )
	{
		for ( Card card : cards )
			this.add( card );
	}
	
	public int indexOf( Card card )
	{
		for ( int i = 0; i < this.size(); i++ )
			if ( this.get( i ).equals( card ) )
				return i;
		return -1;
	}
	
	public boolean includes( Card card )
	{
		if ( this.indexOf( card ) == -1 )
			return false;
		else
			return true;
	}
	
	public boolean doesNotInclude( Card card )
	{
		return ! this.includes( card );
	}
	
	/**
	 * Returns whether is it possible that this is with two other cards a flush.
	 * This should be the board of the game.
	 * 
	 * @return whether is it possible that this is with two other cards a flush
	 */
	public boolean isFlushPossible()
	{
		CardList copy = new CardList( this );
		
		if ( copy.size() < 5 )
			if ( this.howManyColours() > 2 )
				return false;
			else
				return true;
		else
			if ( this.howManyColours() == 4 )
				return false;
			else if ( this.howManyColours() != 3 )
				return true;
			else
				if ( (copy.separateColours().get(0).size() > 2) || (copy.separateColours().get(1).size() > 2) ||
						(copy.separateColours().get(2).size() > 2) || (copy.separateColours().get(3).size() > 2) )
					return true;
				else
					return false;
	}
	
	public int howManyColours()
	{
		CardList copy = new CardList( this );
		ArrayList<CardList> colours = copy.separateColours();
		int howMany = 0;
		
		for ( int i = 0; i < colours.size(); i++ )
			if ( colours.size() > 0 )
				++howMany;
		
		return howMany;
	}
	
	/**
	 * Returns whether "howManyCards" of this are in a row in relation to the value of the cards.
	 * 
	 * @param howManyCards how many cards should be in a row
	 * @return whether "howManyCards" are in a row
	 */
	public boolean cardsInARow( int howManyCards )
	{
		CardList copy = new CardList( this );
		copy.sortByCardValue();
		
		if ( howManyCards > this.size() )
			return false;
		else
			for ( int i = 0; i < this.size()-howManyCards+1; i++ )
			{
				int howOften = 0;
				for ( int k = i+1; k < i+howManyCards; k++ )
					if ( (copy.get(i).getRank().toInt() + (k-i)) == copy.get(k).getRank().toInt() )
						++howOften;
				if ( howOften == howManyCards-1 )
					return true;
			}
		
		return false;
	}
	
	/**
	 * Returns whether all cards of this are in a row in relation to the adjunct valueInt, so the value of the cards.
	 * 
	 * @return whether all cards are in a row
	 */
	public boolean allCardsInARow()
	{
		CardList copy = new CardList( this );
		copy.sortByCardValue();
		
		int valueFirstCard = copy.get(0).getRank().toInt();
		int val = valueFirstCard;
		int howOften = 0;
		
		for ( int i = 1; i < copy.size(); i++ )
			if ( (val+i) == copy.get(i).getRank().toInt() )
				++howOften;
		
		if ( howOften == copy.size()-1 )
			return true;
		return false;
	}
	
	/**
	 * Returns the Cards between the beginning of this and the end of this they are not in this.
	 * This function just can be used if this is sorted by the card value.
	 * The colour of the cards does not matter, so in the return value there is each possible value of a card maximal on time in it.
	 * 
	 * @return a list of cards they are by their value between the beginning of this and the end of this, but they are not in this.
	 */
	public CardList cardsBetweenBeginningAndEndMissed()
	{
		CardList returnValue = new CardList();
		Card card = new Card( Rank.TWO, Suit.SPADES );
		int beginning = this.get( 0 ).getRank().toInt();
		
		for ( int i = 1; i < this.get( this.size()-1 ).getRank().toInt()-beginning; i++ )
		{
			card.set( beginning+i , 1 );
			int howOften = 0;
			for ( int j = 0; j < this.size(); j++ )
				if ( card.getRank().toInt() != this.get( j ).getRank().toInt() )
					++howOften;
			if ( howOften == this.size() )
				returnValue.add( new Card( card ) );
		}
		
		return returnValue;
	}
	
	/**
	 * Returns the result if you subtract the second CardList from the first CardList.
	 * 
	 * @param first the CardList from which the second subtract
	 * @param second the CardList which should be subtracted from the first
	 * @return the cards which are in "first" but not in "second"
	 */
	public static CardList theNewCards( CardList first, CardList second ) {
		CardList ret = new CardList();
		for ( Card c : first )
			if ( ! second.includes(c) )
				ret.add(c);
		return ret;
	}
	
	// Functions returning the cards are a combination XXX (for example pair or flush) of this
	public CardCombinationList whichPairsAreInThis()
	{
		CardList copy = this.clone();
		copy.sortByCardValue();
		CardCombinationList whichPairsAreInThis = new CardCombinationList();
		
		if ( copy.size() > 4 )
		{			
			int i = 2;
			
			while ( i < copy.size()-2 )
			{
				if  (copy.get( i ).getRank().toInt() == copy.get( i-1 ).getRank().toInt() )
					if ( copy.get( i ).getRank().toInt() != copy.get( i+1 ).getRank().toInt() )
						if ( copy.get( i ).getRank().toInt() != copy.get( i-2 ).getRank().toInt() )
						{
							whichPairsAreInThis.add( new CardCombination( new Card[]{ copy.get( i-1 ), copy.get( i ) }, "pair" ) );
							i += 3;
							continue;
						}
				++i;
			}
		}
		
		if ( copy.size() > 3 )
		{
			if ( copy.get( 0 ).getRank().toInt() == copy.get( 1 ).getRank().toInt() )
				if ( copy.get( 0 ).getRank().toInt() != copy.get( 2 ).getRank().toInt() )
					whichPairsAreInThis.add( new CardCombination( "pair", copy.get( 0 ), copy.get( 1 ) ) );
			
			if ( copy.get( copy.size()-2).getRank().toInt() == copy.get( copy.size()-3 ).getRank().toInt() ) {
				if ( copy.get( copy.size()-2 ).getRank().toInt() != copy.get( copy.size()-1 ).getRank().toInt() )
					whichPairsAreInThis.add( new CardCombination( new Card[]{ copy.get( copy.size()-3 ), copy.get( copy.size()-2 ) }, "pair") ); }
			else if ( copy.get( copy.size()-1 ).getRank().toInt() == copy.get( copy.size()-2 ).getRank().toInt() ) {
					if ( copy.get( copy.size()-1 ).getRank().toInt() != copy.get( copy.size()-3 ).getRank().toInt() )
						whichPairsAreInThis.add( new CardCombination( new Card[]{ copy.get( copy.size()-2 ), copy.get( copy.size()-1 ) }, "pair") ); }
		}
		
		else if ( copy.size() == 3 )
		{
			if ( copy.get( 0 ).getRank().toInt() == copy.get( 1 ).getRank().toInt() )
			{
				if ( copy.get( 0 ).getRank().toInt() != copy.get( 2 ).getRank().toInt() )
					whichPairsAreInThis.add( new CardCombination( "pair", copy.get( 0 ), copy.get( 1 ) ) );
			}
			
			else
				if ( copy.get( 1 ).getRank().toInt() == copy.get( 2 ).getRank().toInt() )
					whichPairsAreInThis.add( new CardCombination( "pair", copy.get( 1 ), copy.get( 2 ) ) );			
		}
		
		else if ( copy.size() == 2 )
			if ( copy.get( 0 ).getRank().toInt() == copy.get( 1 ).getRank().toInt() )
				whichPairsAreInThis.add( new CardCombination( "pair", new Card( copy.get( 0 ) ), new Card( copy.get( 1 ) ) ) );
			
		return whichPairsAreInThis;
	}
	
	public boolean hasOnePair()
	{
		if ( this.whichPairsAreInThis().size() == 1 )
			return true;
		else
			return false;
	}
	
	public boolean hasTwoPair()
	{
		if ( this.whichPairsAreInThis().size() >= 2 )
			return true;
		else
			return false;
	}
	
	public CardCombinationList whichTripletsAreInThis()
	{
		CardList copy = this.clone();
		copy.sortByCardValue();
		CardCombinationList whichTripletsAreInThis = new CardCombinationList();
		int i = 2;
		
		while ( i < copy.size()-2 )
		{
			if ( copy.get( i ).getRank().toInt() == copy.get( i-1 ).getRank().toInt() )
				if ( copy.get( i ).getRank().toInt() == copy.get( i-2 ).getRank().toInt() )
					if ( copy.get( i ).getRank().toInt() != copy.get( i+1 ).getRank().toInt() )
					{
						whichTripletsAreInThis.add( new CardCombination( new Card[]{ copy.get( i-2 ), copy.get( i-1 ), copy.get( i ) },
								"threeOfAKind" ) );
						i += 3;
						continue;
					}
			++i;
		}
		
		if ( copy.size() > 3 )
		{		
			if ( copy.get( copy.size()-1 ).getRank().toInt() == copy.get( copy.size()-2).getRank().toInt() )
				if ( copy.get( copy.size()-1 ).getRank().toInt() == copy.get( copy.size()-3 ).getRank().toInt() )
					if ( copy.get( copy.size()-1 ).getRank().toInt() != copy.get( copy.size()-4 ).getRank().toInt() )
						whichTripletsAreInThis.add( new CardCombination( new Card[]{ copy.get( copy.size()-1 ), copy.get( copy.size()-2 ),
								copy.get( copy.size()-3 ) }, "threeOfAKind") );
		}
		
		else if ( copy.size() == 3 )
			if ( copy.get( 0 ).getRank().toInt() == copy.get( 1 ).getRank().toInt() )
				if ( copy.get( 0 ).getRank().toInt() == copy.get( 2 ).getRank().toInt() )
					whichTripletsAreInThis.add( new CardCombination( "threeOfAKind", new Card( copy.get( 0 ) ), new Card( copy.get(1) ), new Card( copy.get(2) ) ) );
		
		return whichTripletsAreInThis;
	}
	
	public boolean hasTriple()
	{
		if ( this.whichTripletsAreInThis().size() == 0 )
			return false;
		else
			return true;
	}
	
	public CardCombinationList whichStraightsAreInThis()
	{
		CardList copy = this.clone();
		copy.sortByCardValue();
		CardCombinationList whichStraightsAreInThis = new CardCombinationList();
		
		for ( int i = 0; i < copy.size()-4; i++ )
		{
			int howOften = 0;
			for ( int j = 1; j < 5; j++ )
				if ( (copy.get( i ).getRank().toInt()+j) == copy.get( i+j ).getRank().toInt() )
					++howOften;
			if ( howOften == 4 )
				whichStraightsAreInThis.add( new CardCombination( "straight",
						copy.get( i ), copy.get( i+1 ), copy.get( i+2 ), copy.get( i+3 ), copy.get( i+4 ) ) );
		}
		
		if ( copy.get(copy.size()-1).getRank() == Rank.ACE )
			if ( copy.get(0).getRank() == Rank.TWO )
				if ( copy.get(1).getRank() == Rank.THREE )
					if( copy.get(2).getRank() == Rank.FOUR )
						if( copy.get(3).getRank() == Rank.FIVE )
							whichStraightsAreInThis.add( new CardCombination( "straight",
									copy.get( copy.size()-1 ), copy.get( 0 ), copy.get( 1 ), copy.get( 2 ), copy.get( 3 ) ) );
		
		return whichStraightsAreInThis;
	}
	
	public boolean hasStraight()
	{
		if ( this.whichStraightFlushsAreInThis().size() == 0 )
			return false;
		else
			return true;
	}
	
	public CardCombinationList whichFlushsAreInThis()
	{
		CardList copy = this.clone();
		CardCombinationList whichFlushsAreInThis = new CardCombinationList();
		
		copy.sortByColour();
		List<CardList> copySeparated = copy.separateColours();
		
		for ( int i = 0; i < 4; i++ )
			if ( copySeparated.get( i ).size() >= 5 )
				for ( int k = 0; k < copySeparated.get( i ).size()-4; k++ )
					whichFlushsAreInThis.add( new CardCombination( "flush",
							copySeparated.get( i ).get( k ), copySeparated.get( i ).get( k+1 ), copySeparated.get( i ).get( k+2 ),
							copySeparated.get( i ).get( k+3 ), copySeparated.get( i ).get( k+4 ) ) );
		
		return whichFlushsAreInThis;
	}
	
	public boolean hasFlush()
	{
		if  (this.whichFlushsAreInThis().size() == 0 )
			return false;
		else
			return true;
	}
	
	public CardCombinationList whichFullHousesAreInThis()
	{
		CardCombinationList whichFullHousesAreInThis = new CardCombinationList();
		
		CardCombinationList pairsInThis = this.whichPairsAreInThis();
		int pairsInt = pairsInThis.size();
		CardCombinationList tripletsInThis = this.whichTripletsAreInThis();
		int tripletsInt = tripletsInThis.size();
		
		if ( (pairsInt == 0) || (tripletsInt == 0) )
			return whichFullHousesAreInThis;
		else
			for ( int i = 0; i < pairsInt; i++ )
				for ( int j = 0; j < tripletsInt; j++ )
				{
					CardList pairAndTriplet = new CardList( pairsInThis.get( i ).cards, tripletsInThis.get( j ).cards );
					whichFullHousesAreInThis.add( new CardCombination( pairAndTriplet, "flush" ) );
				}
		
		return whichFullHousesAreInThis;
	}
	
	public boolean hasFullHouse()
	{
		if ( this.whichFullHousesAreInThis().size() == 0 )
			return false;
		else
			return true;
	}
	
	public CardCombinationList whichQuadrupletsAreInThis()
	{
		CardList copy = this.clone();
		copy.sortByCardValue();
		CardCombinationList whichFullHousesAreInThis = new CardCombinationList();
		int i = 3;
		
		while ( i < copy.size() )
		{
			if ( copy.get( i ).getRank().toInt() == copy.get( i-1 ).getRank().toInt() )
				if ( copy.get( i ).getRank().toInt() == copy.get( i-2 ).getRank().toInt() )
					if ( copy.get( i ).getRank().toInt() == copy.get( i-3 ).getRank().toInt() )
					{
						whichFullHousesAreInThis.add( new CardCombination( "fullHouse",
								copy.get( i ), copy.get( i-1 ), copy.get( i-2 ), copy.get( i-3 ) ) );
						i += 4;
						continue;
					}
			++i;
		}
		
		return whichFullHousesAreInThis;
	}
	
	public boolean hasQuadruplet()
	{
		if ( this.whichQuadrupletsAreInThis().size() == 0 )
			return false;
		else
			return true;
	}
	
	public CardCombinationList whichStraightFlushsAreInThis()
	{
		CardCombinationList whichStraightFlushsAreInThis = new CardCombinationList();
		
		CardCombinationList straightsInThis = this.whichStraightsAreInThis();
		int straightsInt = straightsInThis.size();
		CardCombinationList flushsInThis = this.whichFlushsAreInThis();
		int flushsInt = flushsInThis.size();
		
		if ( (straightsInt == 0 ) || (flushsInt == 0 ) )
			return whichStraightFlushsAreInThis;
		else
			for ( int i = 0; i < straightsInt; i++ )
				if ( straightsInThis.get( i ).cards.isOneColour() )
					whichStraightFlushsAreInThis.add( new CardCombination( straightsInThis.get( i ).cards, "straightFlush" ) );
		
		return whichStraightFlushsAreInThis;
	}
	
	public boolean hasStraightFlush()
	{
		if ( this.whichStraightFlushsAreInThis().size() == 0 )
			return false;
		else
			return true;
	}
	
	public CardCombinationList whichRoyalFlushsAreInThis()
	{
		CardCombinationList whichRoyalFlushsAreInThis = new CardCombinationList();
		
		CardCombinationList straightFlushsInThis = this.whichStraightFlushsAreInThis();
		
		if ( straightFlushsInThis.size() == 0 )
			return whichRoyalFlushsAreInThis;
		else
		{
			for ( int i = 0; i < straightFlushsInThis.size(); i++ )
				if ( straightFlushsInThis.get( i ).highCard.getRank() == Rank.ACE )
					whichRoyalFlushsAreInThis.add( new CardCombination( straightFlushsInThis.get( i ).cards, "royalFlush" ) );
		}
		
		return whichRoyalFlushsAreInThis;		
	}
	
	public boolean hasRoyalFlush()
	{
		if ( this.whichRoyalFlushsAreInThis().size() == 0 )
			return false;
		else
			return true;
	}
	
	// Functions testing is this a combination XXX.
	
	public boolean isOnePair()
	{
		if ( this.size() != 2 )
			return false;
		else
		{
			if ( this.get( 0 ).getRank().toInt() == this.get( 1 ).getRank().toInt() )
				return true;
			else
				return false;
		}
	}
	
	public boolean isTwoPair()
	{
		if ( this.size() != 4 )
			return false;
		else
		{
			CardList copy = this.clone();
			copy.sortByCardValue();
			if ( copy.get( 0 ).getRank().toInt() == copy.get( 1 ).getRank().toInt() )
				if ( copy.get( 2 ).getRank().toInt() == copy.get( 3 ).getRank().toInt() )
					return true;
			return false;
		}
	}
	
	public boolean isTriple()
	{
		if ( this.size() != 3 )
			return false;
		else
		{
			if ( this.get(0).getRank() == this.get(1).getRank() )
				if ( this.get(0).getRank() == this.get(2).getRank() )
					return true;
			return false;
		}
	}
	
	public boolean isStraight()
	{
		if ( this.size() != 5 )
			return false;
		else
		{
			CardList copy = this.clone();
			copy.sortByCardValue();
			int howOften = 0;
			
			for ( int i = 1; i < 5; i++ )
				if ( copy.get( 0 ).getRank().toInt()+i == copy.get( i ).getRank().toInt() )
					++howOften;
			
			if ( howOften == 4 )
				return true;
			else
				return false;
		}
	}
	
	public boolean isFlush()
	{
		if ( this.size() != 5 )
			return false;
		else
			return this.isOneColour();
	}
	
	public boolean isFullHouse()
	{
		if ( this.size() != 5 )
			return false;
		else
		{
			if ( this.hasOnePair() && this.hasTriple() )
				return true;
			else
				return false;
		}
	}
	
	public boolean isQuadruplet()
	{
		if ( this.size() != 4 )
			return false;
		else
		{
			CardList copy = this.clone();
			copy.sortByCardValue();
			int howOften = 0;
			
			for ( int i = 1; i < 4; i++ )
				if ( copy.get( 0 ).getRank().toInt() == copy.get( i ).getRank().toInt() )
					++howOften;
			
			if ( howOften == 3 )
				return true;
			else
				return false;
		}
	}
	
	public boolean isStraightFlush()
	{
		if ( this.isStraight() && this.isFlush() )
			return true;
		else
			return false;
	}
	
	public boolean isRoyalFlush()
	{
		if ( this.isStraightFlush() )
		{
			CardList copy = this.clone();
			copy.sortByCardValue();
			if ( copy.get(4).getRank() == Rank.ACE )
				return true;
			else
				return false;
		}
		else
			return false;
	}
	
	// Functions returning number of possibilities for a combination XXX. Getting a combination XXX it will use two cards.
	
	public int numberOfPossibilitesForHighCard( CardsStack stack )
	{
		int returnValue = 0;
		Card card = new Card( this.getHighCard() );
		returnValue += ( stack.size() - stack.howManyCardsWithTheSameValueInThis(card) ) * stack.howManyCardsWithTheSameValueInThis(card);
		return returnValue;
	}
	
	public int numberOfPossibilitiesForOnePair( CardsStack stack )
	{
		int returnValue = 0;
		CardList copy = this.clone();
		
		for ( int i = 0; i < copy.size(); i++ )
			returnValue += ( stack.size() - stack.howManyCardsWithTheSameValueInThis( new Card(copy.get(i))) ) * stack.howManyCardsWithTheSameValueInThis( new Card(copy.get(i)) );
		
		return returnValue;
	}
	
	public int numberOfPossibilitiesForTwoPair( CardsStack stack )
	{
		int returnValue = 0;
		CardList copy = this.clone();
		
		CardList pairsInThis = new CardList();
		CardCombinationList pairs = copy.whichPairsAreInThis();
		for ( int i = 0; i < pairs.size(); i++ )
			pairsInThis.add( pairs.get( i ).cards.get( 0 ) );
		CardList allPairsInThis = new CardList();
		for ( int i = 0; i < pairs.size(); i++ )
			allPairsInThis.add( pairs.get( i ).cards.get( 0 ), pairs.get( i ).cards.get( 1 ) );
		copy.remove( allPairsInThis );
		
		
		
		if( pairsInThis.size() == 1 )							// You have one pair and than you construct with one card of copy an other pair.
			for (int i = 0; i < copy.size(); i++)
				returnValue += ( stack.size() - stack.howManyCardsWithTheSameValueInThis( new Card(copy.get(i))) ) * stack.howManyCardsWithTheSameValueInThis( new Card(copy.get(i)) );
		
		
		for ( int i = 0; i < copy.size(); i++ )			// You have two cards which are different and to each you do a card that it is a 2Pair.
			for ( int j = 0; j < copy.size(); j++ )
				if ( copy.get( i ) != copy.get( j ) )
				{
					int remainderOfi = stack.howManyCardsWithTheSameValueInThis( copy.get( i ) );
					int remainderOfj = stack.howManyCardsWithTheSameValueInThis( copy.get( j ) );
					returnValue += remainderOfi*remainderOfj;
				}
		
		return returnValue;
	}

	public int numberOfPossibilitiesForTriple( CardsStack stack )
	{
		int returnValue = 0;
		CardList copy = this.clone();

		CardList pairsInThis = new CardList();
		for ( int i = 0; i < copy.whichPairsAreInThis().size(); i++ )
			pairsInThis.add( copy.whichPairsAreInThis().get( i ).cards.get( 0 ) );
		CardList allPairsInThis = new CardList();
		for ( int i = 0; i < copy.whichPairsAreInThis().size(); i++ )
			allPairsInThis.add( copy.whichPairsAreInThis().get( i ).cards.get( 0 ), copy.whichPairsAreInThis().get( i ).cards.get( 1 ) );
		
		Suit[] suits = Suit.values();
		for( int i = 0; i < pairsInThis.size(); i++ )
			for ( int j = 0; j < 4; j++ )
				if ( pairsInThis.get( i ).getSuit() != suits[j] )
				{
					Card card = new Card( pairsInThis.get(i).getRank(), suits[ j ] );
					if ( stack.includes( card ) )
						returnValue += stack.size() - stack.howManyCardsWithTheSameValueInThis( pairsInThis.get( i ) );
				}
		
		copy.remove( allPairsInThis );
		
		for ( int i = 0; i < copy.size(); i++ )
		{
			int howOften = 0;
			for ( int j = 0; j < 4; j++ )
				if ( copy.get( i ).getSuit() != suits[ j ] )
					for ( int k = 0; k < 4; k++ )
						if ( copy.get(i).getSuit() != suits[ k ] )
							if ( suits[ k ] != suits[ j ] )
							{
								Card card1 = new Card( copy.get( i ).getRank(), suits[ j ] );
								Card card2 = new Card( copy.get( i ).getRank(), suits[ k ] );
								if ( stack.includes( card1 ) && stack.includes( card2 ) )
									++howOften;
							}
			returnValue += howOften;
		}
		
		return returnValue;
	}
		
	public int numberOfPossibilitiesForStraight( CardsStack stack )
	{
		int returnValue = 0;
		CardList copy = this.clone();
		
		CardList pairsInThis = new CardList();
		for ( int i = 0; i < copy.whichPairsAreInThis().size(); i++ )
			pairsInThis.add( copy.whichPairsAreInThis().get( i ).cards.get( 0 ) );
		CardList tripletsInThis = new CardList();
		for ( int i = 0; i < copy.whichTripletsAreInThis().size(); i++ )
			tripletsInThis.add( copy.whichTripletsAreInThis().get( i ).cards.get( 0 ) );
		
		CardList pairsTripletsInThis = new CardList( pairsInThis, tripletsInThis );
		copy.remove( pairsTripletsInThis );
		copy.sortByCardValue();
		
		Card card1 = new Card(Rank.TWO, Suit.SPADES);
		Card card2 = new Card(Rank.TWO, Suit.SPADES);
		
		for ( int i = 2; i < copy.size(); i++ )
		{
			int difference = copy.get( i ).getRank().toInt() - copy.get( i-2 ).getRank().toInt();
			
			if ( copy.get( i-2 ).getRank().toInt() == 2 )
			{
				if ( difference == 2 )
				{
					card1.set( 5, 1 );
					
					card2.set( 6, 1 );
					returnValue += stack.howManyCardsWithTheSameValueInThis( card1 ) * stack.howManyCardsWithTheSameValueInThis( card2 );
					
					card2.set( 14, 1);		// card1 remains the same.
					returnValue += stack.howManyCardsWithTheSameValueInThis( card1 ) * stack.howManyCardsWithTheSameValueInThis( card2 );
				}
				
				else if ( difference == 3 )
				{
					CardList theActualThreeCards = new CardList( copy.get( i-2 ), copy.get( i-1 ), copy.get( i ) );
					card1.set( theActualThreeCards.cardsBetweenBeginningAndEndMissed().get( 0 ) );
					
					card2.set( 14, 1 );
					returnValue += stack.howManyCardsWithTheSameValueInThis( card1 ) * stack.howManyCardsWithTheSameValueInThis( card2 );
					
					card2.set( 6, 1 );
					returnValue += stack.howManyCardsWithTheSameValueInThis( card1 ) * stack.howManyCardsWithTheSameValueInThis( card2 );
				}
				
				else if ( difference == 4 )
				{
					CardList theActualThreeCards = new CardList( copy.get( i-2 ), copy.get( i-1 ), copy.get( i ) );
					card1.set( theActualThreeCards.cardsBetweenBeginningAndEndMissed().get( 0 ) );
					card2.set( theActualThreeCards.cardsBetweenBeginningAndEndMissed().get( 1 ) );
					returnValue += stack.howManyCardsWithTheSameValueInThis( card1 ) * stack.howManyCardsWithTheSameValueInThis( card2 );
				}
			}
			
			else if ( copy.get( i-2 ).getRank().toInt() == 3 )
			{
				if ( difference == 2 )
				{
					card1.set( 6, 1 );
					
					card2.set( 7, 1 );		// card1 remains the same.
					returnValue += stack.howManyCardsWithTheSameValueInThis( card1 ) * stack.howManyCardsWithTheSameValueInThis( card2 );
					
					card2.set( 2, 1 );		// card1 remains the same.
					returnValue += stack.howManyCardsWithTheSameValueInThis( card1 ) * stack.howManyCardsWithTheSameValueInThis( card2 );
					
					card1.set( 14, 1 );		// card2 remains the same.
					returnValue += stack.howManyCardsWithTheSameValueInThis( card1 ) * stack.howManyCardsWithTheSameValueInThis( card2 );
				}
				
				else if ( difference == 3 )
				{
					CardList theActualThreeCards = new CardList( copy.get( i-2 ), copy.get( i-1 ), copy.get( i ) );
					card1.set( theActualThreeCards.get( 0 ) );
					
					card2.set( 2, 1 );
					returnValue += stack.howManyCardsWithTheSameValueInThis( card1 ) * stack.howManyCardsWithTheSameValueInThis( card2 );
					
					card2.set( 7, 1 );
					returnValue += stack.howManyCardsWithTheSameValueInThis( card1 ) * stack.howManyCardsWithTheSameValueInThis( card2 );
				}
				
				else if ( difference == 4 )
				{
					CardList theActualThreeCards = new CardList( copy.get( i-2 ), copy.get( i-1 ), copy.get( i ) );
					card1.set( theActualThreeCards.get( 0 ) );
					card2.set( theActualThreeCards.get( 1 ) );
					returnValue += stack.howManyCardsWithTheSameValueInThis( card1 ) * stack.howManyCardsWithTheSameValueInThis( card2 );
				}
			}
			
			else
			{
				if (difference == 2) {
					card1.set( copy.get( i ).getRank().toInt()+1, copy.get( i ).getSuit().toInt());
					card2.set (copy.get( i ).getRank().toInt()+2, copy.get( i ).getSuit().toInt());
					returnValue += stack.howManyCardsWithTheSameValueInThis( card1 ) * stack.howManyCardsWithTheSameValueInThis( card2 );
					
					card1.set( copy.get( i-2 ).getRank().toInt()-1, copy.get( i-2 ).getSuit().toInt() );
					
					card2.set( copy.get( i-2 ).getRank().toInt()-2, copy.get( i-2 ).getSuit().toInt() );
					returnValue += stack.howManyCardsWithTheSameValueInThis( card1 ) * stack.howManyCardsWithTheSameValueInThis( card2 );
					
					card2.set( copy.get( i ).getRank().toInt()+1, copy.get( i ).getSuit().toInt() );		// card1 remains the same.
					returnValue += stack.howManyCardsWithTheSameValueInThis( card1 ) * stack.howManyCardsWithTheSameValueInThis( card2 );
				}
				
				else if (difference == 3)
				{
					CardList theActualThreeCards = new CardList( copy.get( i-2 ), copy.get( i-1 ), copy.get( i ) );
					
					card1.set( theActualThreeCards.cardsBetweenBeginningAndEndMissed().get( 0 ) );
					int cardsLikeCard1 = stack.howManyCardsWithTheSameValueInThis( card1 );
					
					card2.set( copy.get( i ).getRank().toInt()+1, copy.get( i ).getSuit().toInt() );
					returnValue += stack.howManyCardsWithTheSameValueInThis( card2 ) * cardsLikeCard1;
					
					card2.set( copy.get( i-2 ).getRank().toInt()-1, copy.get( i-2 ).getSuit().toInt() );
					returnValue += stack.howManyCardsWithTheSameValueInThis( card2 ) * cardsLikeCard1;
				}
				
				else if (difference == 4)
				{
					CardList theActualThreeCards = new CardList( copy.get( i-2 ), copy.get( i-1 ), copy.get( i ) );
					card1.set( theActualThreeCards.cardsBetweenBeginningAndEndMissed().get( 0 ) );
					card2.set( theActualThreeCards.cardsBetweenBeginningAndEndMissed().get( 1 ) );
					returnValue += stack.howManyCardsWithTheSameValueInThis( card1 ) * stack.howManyCardsWithTheSameValueInThis( card2 );
				}
			}
		}
		
		return returnValue;
	}
	
	public int numberOfPossibilitiesForFlush( CardsStack stack )
	{
		int returnValue = 0;
		CardList copy = this.clone();
		
		List<CardList> differentColours = copy.separateColours();			// differentColours is a list of CardLists. The cards of copy are sorted by their color, it is differentColours.
		
		for ( int i = 0; i < 4; i++ )
		{
			CardList colours = differentColours.get( i );
			if ( colours.size() == 4 )
				for ( int j = 2; j < 15; j++ )
				{
					Card card = new Card(Rank.toRank(j), colours.get(0).getSuit());
					if ( stack.includes( card ) && colours.doesNotInclude( card ) )
						returnValue += stack.size()-8;
				}
			else if ( colours.size() == 3 )
				for ( int j = 2; j < 15; j++ )
				{
					Card card1 = new Card(Rank.toRank(j), colours.get(0).getSuit());
					if( stack.includes( card1 ) && colours.doesNotInclude( card1 ) )
						for ( int k = 2; k < 15; k++ )
						{
							Card card2 = new Card(Rank.toRank(k), colours.get(0).getSuit());
							if ( stack.includes( card2 ) && colours.doesNotInclude( card2 ) &&  (! card1.equals(card2)) )
								++returnValue;
						}
				}
		}
		
		return returnValue;
	}
	
	public int numberOfPossibilitiesForFullHouse( CardsStack stack )
	{
		int returnValue = 0;
		CardList copy = this.clone();
		
		CardList pairsInThis = new CardList();
		for ( int i = 0; i < this.whichPairsAreInThis().size(); i++ )
			pairsInThis.add( this.whichPairsAreInThis().get( i ).cards );
		CardList tripletsInThis = new CardList();
		for ( int i = 0; i < this.whichTripletsAreInThis().size(); i++ )
			tripletsInThis.add( this.whichTripletsAreInThis().get( i ).cards );
		
		CardList pairsAndTriplets = new CardList( pairsInThis, tripletsInThis );
		copy.remove( pairsAndTriplets );
		
		if ( (tripletsInThis.size() > 0) && (pairsInThis.size() > 0) )
			for ( int i = 0; i < tripletsInThis.size(); i++ )
			{
				for ( int j = 0; j < pairsInThis.size(); j++ )
					++returnValue;
				
				for ( int j = 0; j < copy.size(); j++ )
					returnValue += stack.howManyCardsWithTheSameValueInThis( copy.get( j ) ) *
					( stack.size() - stack.howManyCardsWithTheSameValueInThis( copy.get( j ) ) );
			}
		
		if ( pairsInThis.size() > 0 )
		{
			if ( pairsInThis.size() >= 2 )					// You take one pair and do all other pairs to a triple, so you have a full house.
				for ( int i = 0; i < pairsInThis.size(); i++ )
					for ( int j = 0; j < pairsInThis.size(); j++ )
						if ( pairsInThis.get( i ) != pairsInThis.get( j ) )
							returnValue += stack.howManyCardsWithTheSameValueInThis( pairsInThis.get( j ) ) *
							( stack.size() - stack.howManyCardsWithTheSameValueInThis( copy.get( j ) ) );
			
			for ( int i = 0; i < pairsInThis.size(); i++ )	// You take one pair and construct with one card of copy a triple, so you have a full house.
				for ( int j = 0; j < copy.size(); j++ )
				{
					if ( stack.howManyCardsWithTheSameValueInThis( copy.get( j ) ) == 2 )
						++returnValue;
					else if ( stack.howManyCardsWithTheSameValueInThis( copy.get( j ) ) == 3 )
						returnValue += 3;
				}
			
			for ( int i = 0; i < pairsInThis.size(); i++ )	// You take one pair and one card of copy. The pair do you to a triple, the card to a pair.
				for ( int j = 0; j < copy.size(); j++ )
					returnValue += stack.howManyCardsWithTheSameValueInThis( pairsInThis.get( i ) ) *
					stack.howManyCardsWithTheSameValueInThis( copy.get( j ) );
		}
		
		return returnValue;
	}
	
	public int numberOfPossibilitiesForQuadruplet( CardsStack stack )
	{
		int returnValue = 0;
		CardList copy = this.clone();
		
		CardList pairsInThis = new CardList();
		for ( int i = 0; i < copy.whichPairsAreInThis().size(); i++ )
			pairsInThis.add( copy.whichPairsAreInThis().get( i ).cards.get( 0 ) );
		CardList allPairsInThis = new CardList();
		for ( int i = 0; i < copy.whichPairsAreInThis().size(); i++ )
			allPairsInThis.add( copy.whichPairsAreInThis().get( i ).cards.get( 0 ), copy.whichPairsAreInThis().get( i ).cards.get( 1 ) );
		
		CardList tripletsInThis = new CardList();
		for ( int i = 0; i < copy.whichTripletsAreInThis().size(); i++ )
			tripletsInThis.add( copy.whichTripletsAreInThis().get( i ).cards.get( 0 ) );
		CardList allTripletsInThis = new CardList();
		for ( int i = 0; i < copy.whichTripletsAreInThis().size(); i++ )
			allTripletsInThis.add( copy.whichTripletsAreInThis().get( i ).cards.get( 0 ), copy.whichTripletsAreInThis().get( i ).cards.get( 1 ),
					copy.whichTripletsAreInThis().get( i ).cards.get( 2 ) );
		
		CardList allPairsAndTriplets = new CardList( allPairsInThis, allTripletsInThis );
		copy.remove( allPairsAndTriplets );
		
		for ( int i = 0; i < tripletsInThis.size(); i++ )			// You take one triple and do the last card to it.
			if ( stack.howManyCardsWithTheSameValueInThis( tripletsInThis.get( i ) ) == 1 )
				returnValue += stack.size() - 1;
		
		for ( int i = 0; i < pairsInThis.size(); i++ )				// You take a pair and do the last two cards to it.
			if ( stack.howManyCardsWithTheSameValueInThis( pairsInThis.get( i ) ) == 2 )
				++returnValue;
				
		return returnValue;
	}
	
	/**
	 * Returns the number of possibilities for a straight flush.
	 * 
	 * @param stack these are the cards they are in the stack and can be used for a card combination
	 * 
	 * @return the number of possibilities for a straight flush with this cards
	 */
	public int numberOfPossibilitiesForStraightFlush( CardsStack stack )
	{
		int returnValue = 0;
		CardList copy = this.clone();
		
		if ( (copy.numberOfPossibilitiesForFlush( stack ) == 0) || ( copy.numberOfPossibilitiesForStraight( stack ) == 0 ) )
			return returnValue;
		
		else
		{			
			CardList pairsInThis = new CardList();
			for ( int i = 0; i < copy.whichPairsAreInThis().size(); i++ )
				pairsInThis.add( copy.whichPairsAreInThis().get( i ).cards.get( 0 ) );
			CardList tripletsInThis = new CardList();
			for ( int i = 0; i < copy.whichTripletsAreInThis().size(); i++ )
				tripletsInThis.add( copy.whichTripletsAreInThis().get( i ).cards.get( 0 ) );
			
			CardList pairsTripletsInThis = new CardList( pairsInThis, tripletsInThis );
			copy.remove( pairsTripletsInThis );
			copy.sortByCardValue();
			
			Card card1 = new Card(Rank.TWO, Suit.SPADES);
			Card card2 = new Card(Rank.TWO, Suit.SPADES);
			
			for ( int i = 2; i < copy.size(); i++ )
			{
				int difference = copy.get( i ).getRank().toInt() - copy.get( i-2 ).getRank().toInt();
				int colourInt = copy.get( i ).getSuit().toInt();
				
				if ( (colourInt != copy.get(i-1).getSuit().toInt()) || (colourInt != copy.get(i-2).getSuit().toInt()) )
					continue;
				
				if ( copy.get( i-2 ).getRank().toInt() == 2 )
				{
					if ( difference == 2 )
					{
						card1.set( 5, colourInt );
						
						card2.set( 6, colourInt );
						if ( stack.includes( card1 ) && stack.includes( card2 ) )
							++returnValue;
						
						card2.set( 14, colourInt);		// card1 remains the same.
						if ( stack.includes( card1 ) && stack.includes( card2 ) )
							++returnValue;
					}
					
					else if ( difference == 3 )
					{
						CardList theActualThreeCards = new CardList( copy.get( i-2 ), copy.get( i-1 ), copy.get( i ) );
						card1.set( theActualThreeCards.cardsBetweenBeginningAndEndMissed().get( 0 ).getRank().toInt(), colourInt );
						
						card2.set( 14, colourInt );
						if ( stack.includes( card1 ) && stack.includes( card2 ) )
								++returnValue;
						
						card2.set( 6, colourInt );
						if ( stack.includes( card1 ) && stack.includes( card2 ) )
								++returnValue;
					}
					
					else if ( difference == 4 )
					{
						CardList theActualThreeCards = new CardList( copy.get( i-2 ), copy.get( i-1 ), copy.get( i ) );
						card1.set( theActualThreeCards.cardsBetweenBeginningAndEndMissed().get( 0 ).getRank().toInt(), colourInt );
						card2.set( theActualThreeCards.cardsBetweenBeginningAndEndMissed().get( 1 ).getRank().toInt(), colourInt );
						if ( stack.includes( card1 ) && stack.includes( card2 ) )
							++returnValue;
					}
				}
				
				else if ( copy.get( i-2 ).getRank().toInt() == 3 )
				{
					if ( difference == 2 )
					{
						card1.set( 6, colourInt );
						
						card2.set( 7, colourInt );		// card1 remains the same.
						if ( stack.includes( card1 ) && stack.includes( card2 ) )
							++returnValue;
						
						card2.set( 2, colourInt );		// card1 remains the same.
						if ( stack.includes( card1 ) && stack.includes( card2 ) )
							++returnValue;
						
						card1.set( 14, colourInt );		// card2 remains the same.
						if ( stack.includes( card1 ) && stack.includes( card2 ) )
							++returnValue;
					}
					
					else if ( difference == 3 )
					{
						CardList theActualThreeCards = new CardList( copy.get( i-2 ), copy.get( i-1 ), copy.get( i ) );
						card1.set( theActualThreeCards.cardsBetweenBeginningAndEndMissed().get(0).getRank().toInt(), colourInt );
						
						card2.set( 2, colourInt );
						if ( stack.includes( card1 ) && stack.includes( card2 ) )
							++returnValue;
						
						card2.set( 7, colourInt );
						if ( stack.includes( card1 ) && stack.includes( card2 ) )
							++returnValue;						
					}
					
					else if ( difference == 4 )
					{
						CardList theActualThreeCards = new CardList( copy.get( i-2 ), copy.get( i-1 ), copy.get( i ) );
						card1.set( theActualThreeCards.cardsBetweenBeginningAndEndMissed().get( 0 ).getRank().toInt(), colourInt );
						card2.set( theActualThreeCards.cardsBetweenBeginningAndEndMissed().get( 1 ).getRank().toInt(), colourInt );
						if ( stack.includes( card1 ) && stack.includes( card2 ) )
							++returnValue;
					}
				}
				
				else
				{
					if (difference == 2)
					{
						card1.set( copy.get( i ).getRank().toInt()+1, colourInt );
						card2.set (copy.get( i ).getRank().toInt()+2, colourInt );
						if ( stack.includes( card1 ) && stack.includes( card2 ) )
							++returnValue;
						
						card1.set( copy.get( i-2 ).getRank().toInt()-1, colourInt );
						
						card2.set( copy.get( i-2 ).getRank().toInt()-2, colourInt );
						if ( stack.includes( card1 ) && stack.includes( card2 ) )
							++returnValue;
						
						card2.set( copy.get( i ).getRank().toInt()+1, colourInt );		// card1 remains the same.
						if ( stack.includes( card1 ) && stack.includes( card2 ) )
							++returnValue;
					}
					
					else if (difference == 3)
					{
						CardList theActualThreeCards = new CardList( copy.get( i-2 ), copy.get( i-1 ), copy.get( i ) );
						card1.set( theActualThreeCards.cardsBetweenBeginningAndEndMissed().get(0).getRank().toInt(), colourInt );
						
						card2.set( copy.get(i).getRank().toInt()+1, colourInt );
						if ( stack.includes( card1 ) && stack.includes( card2 ) )
							++returnValue;
						
						card2.set( copy.get(i-2).getRank().toInt()-1, colourInt );
						if ( stack.includes( card1 ) && stack.includes( card2 ) )
							++returnValue;						
					}
					
					else if (difference == 4)
					{
						CardList theActualThreeCards = new CardList( copy.get( i-2 ), copy.get( i-1 ), copy.get( i ) );
						card1.set( theActualThreeCards.cardsBetweenBeginningAndEndMissed().get( 0 ).getRank().toInt(), colourInt );
						card2.set( theActualThreeCards.cardsBetweenBeginningAndEndMissed().get( 1 ).getRank().toInt(), colourInt );
						if ( stack.includes( card1 ) && stack.includes( card2 ) )
							++returnValue;
					}
				}
			}
		}		
		
		return returnValue;
	}
	
	/**
	 * Returns the number of possibilities for a royal flush.
	 * This function just can get used if there are two cards needed for a royal flush.
	 * 
	 * @param stack these are the cards they are in the stack and can be used for a card combination
	 * @return
	 */
	public int numberOfPossibilitesForRoyalFlush( CardsStack stack )
	{
		int returnValue = 0;
		CardList copy = this.clone();
		copy.sortByCardValue();
		int size = copy.size();
		CardList last3Cards = new CardList( copy.get( size-1 ), copy.get( size-2 ), copy.get( size-3 ) );
		
		if ( copy.numberOfPossibilitiesForStraightFlush( stack ) == 0 )
			return returnValue;
		
		else if ( copy.getHighCard().getRank().toInt() != 14 )
			if ( copy.getHighCard().getRank().toInt() != 13 )
				if ( copy.getHighCard().getRank().toInt() != 12 )
					return returnValue;
		
		else if ( ! last3Cards.isOneColour() )
			return returnValue;
		
		else if ( copy.get( size-3 ).getRank().toInt() <= 9 )
			return returnValue;
		
		else
		{
			Card card1 = new Card( Rank.TWO, Suit.SPADES );
			Card card2 = new Card( Rank.TWO, Suit.SPADES );
			
			if ( copy.includes( new Card(Rank.TEN, copy.getHighCard().getSuit() ) ) )
			{
				if ( copy.getHighCard().getRank().toInt() == 14 )
				{
					CardList tenAndAce = new CardList( new Card(Rank.TEN, copy.getHighCard().getSuit() ), new Card( copy.getHighCard() ) );
					CardList neededCards = tenAndAce.cardsBetweenBeginningAndEndMissed();
					
					card1.set( neededCards.get(0).getRank().toInt(), copy.getHighCard().getSuit().toInt() );
					card2.set( neededCards.get( 1 ).getRank().toInt(), copy.getHighCard().getSuit().toInt() );
					
					if ( stack.includes( card1 ) && stack.includes( card2 ) )
						++returnValue;
				}
				else
				{
					card1.set( 14, copy.getHighCard().getSuit().toInt() );
					
					CardList tenAndAce = new CardList( new Card(Rank.TEN, copy.getHighCard().getSuit() ), new Card( copy.getHighCard() ) );
					CardList neededCards = tenAndAce.cardsBetweenBeginningAndEndMissed();
					
					card2.set( neededCards.get( 0 ).getRank().toInt(), copy.getHighCard().getSuit().toInt() );
					
					if ( stack.includes( card1 ) && stack.includes( card2 ) )
						++returnValue;
				}
			}
			
			else
			{
				card1.set( 10, copy.getHighCard().getSuit().toInt() );
				
				if ( copy.getHighCard().getRank().toInt() == 14 )
				{
					CardList tenAndAce = new CardList( new Card(Rank.TEN, copy.getHighCard().getSuit()), new Card( copy.getHighCard() ) );
					CardList neededCards = tenAndAce.cardsBetweenBeginningAndEndMissed();
					
					card2.set( neededCards.get( 0 ).getRank().toInt(), copy.getHighCard().getSuit().toInt() );
					
					if( stack.includes( card1 ) && stack.includes( card2 ) )
						++returnValue;
				}
				else
				{
					card2.set( 14, copy.getHighCard().getSuit().toInt() );
					
					if ( stack.includes( card1 ) && stack.includes( card2 ) )
						++returnValue;
				}
			}
		}
		
		return returnValue;
	}
	
	/**
	 * Returns the best CardCombination and its probability for this CardList.
	 * The number of elements of the CardList should be three or five.
	 * The cards of the return value are all initialized with (undefined,undefined).
	 * 
	 * @param stack these are the cards they are in the stack and can be used for a card combination
	 * @return the best CardCombination and its probability for the CardList
	 */
	public CardCombinationDraw probabilityBestCombination( CardsStack stack )
	{
		CardCombinationDraw returnValue = new CardCombinationDraw();
		CardList returnValueCards = new CardList();
		double probability = (double) 1 / (stack.size() * (stack.size()-1));
		
		if ( (probability *= this.numberOfPossibilitesForRoyalFlush(stack)) > 0 )
		{
			for ( int i = 0; i < 5; i++ )
				returnValueCards.add(new Card(Rank.UNDEFINED, Suit.UNDEFINED));
			returnValue.set( returnValueCards, "royalFlush", probability );
		}
		
		else
		{
			if ( (probability *= this.numberOfPossibilitiesForStraightFlush(stack)) > 0 )
			{
				for ( int i = 0; i < 5; i++ )
					returnValueCards.add(new Card(Rank.UNDEFINED, Suit.UNDEFINED));
				returnValue.set( returnValueCards, "straightFlush", probability );
			}
			
			else
			{
				if ( (probability *= this.numberOfPossibilitiesForQuadruplet(stack)) > 0 )
				{
					for ( int i = 0; i < 4; i++ )
						returnValueCards.add(new Card(Rank.UNDEFINED, Suit.UNDEFINED));
					returnValue.set( returnValueCards, "fourOfAKind", probability );
				}
				
				else
				{
					if ( (probability *= this.numberOfPossibilitiesForFullHouse(stack)) > 0 )
					{
						for ( int i = 0; i < 5; i++ )
							returnValueCards.add(new Card(Rank.UNDEFINED, Suit.UNDEFINED));
						returnValue.set( returnValueCards, "fullHouse", probability );
					}
					
					else
					{
						if ( (probability *= this.numberOfPossibilitiesForFlush(stack)) > 0 )
						{
							for ( int i = 0; i < 5; i++ )
								returnValueCards.add(new Card(Rank.UNDEFINED, Suit.UNDEFINED));
							returnValue.set( returnValueCards, "flush", probability );
						}
						
						else
						{
							if ( (probability *= this.numberOfPossibilitiesForStraight(stack)) > 0 )
							{
								for ( int i = 0; i < 5; i++ )
									returnValueCards.add(new Card(Rank.UNDEFINED, Suit.UNDEFINED));
								returnValue.set( returnValueCards, "straight", probability );
							}
							
							else
							{
								if ( (probability *= this.numberOfPossibilitiesForTriple(stack)) > 0 )
								{
									for ( int i = 0; i < 3; i++ )
										returnValueCards.add(new Card(Rank.UNDEFINED, Suit.UNDEFINED));
									returnValue.set( returnValueCards, "threeOfAKind", probability );
								}
								
								else
								{
									if ( (probability *= this.numberOfPossibilitiesForTwoPair(stack)) > 0 )
									{
										for ( int i = 0; i < 4; i++ )
											returnValueCards.add(new Card(Rank.UNDEFINED, Suit.UNDEFINED));
										returnValue.set( returnValueCards, "twoPair", probability );
									}
									
									else
									{
										if ( (probability *= this.numberOfPossibilitiesForOnePair(stack)) > 0 )
										{
											for ( int i = 0; i < 2; i++ )
												returnValueCards.add(new Card(Rank.UNDEFINED, Suit.UNDEFINED));
											returnValue.set( returnValueCards, "pair", probability );
										}
										
										else
										{
											probability *= this.numberOfPossibilitesForHighCard(stack);
											for ( int i = 0; i < 2; i++ )
												returnValueCards.add(new Card(Rank.UNDEFINED, Suit.UNDEFINED));
											returnValue.set( returnValueCards, "highCard", probability );
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		return returnValue;
	}
	
	/**
	 * Returns the best possible card combination with the board and two other cards (hand cards).
	 * 
	 * @param board open cards
	 * @param stack the stack of the actual game
	 * @return best possible card combination with board
	 */
	public static CardCombination bestCardCombination( CardList board, CardsStack stack )
	{
		CardCombination cc = new CardCombination();
		
		for ( int i = 0; i < stack.size(); i++ )
			for ( int k = 0; k < stack.size(); k++ )
				if ( i != k )
				{
					CardList l = new CardList( board, stack.get(i), stack.get(k) );
					
					if ( l.hasRoyalFlush() )
						cc.set( CardCombination.higherCardCombination(cc, new CardCombination(l, "royalFlush")).get(0) );
					else if ( l.hasStraightFlush() )
						cc.set( CardCombination.higherCardCombination(cc, new CardCombination(l, "straightFlush")).get(0) );
					else if ( l.hasQuadruplet() )
						cc.set( CardCombination.higherCardCombination(cc, new CardCombination(l, "fourOfAKind")).get(0) );
					else if ( l.hasFullHouse() )
						cc.set( CardCombination.higherCardCombination(cc, new CardCombination(l, "fullHouse")).get(0) );
					else if ( l.hasFlush() )
						cc.set( CardCombination.higherCardCombination(cc, new CardCombination(l, "flush")).get(0) );
					else if ( l.hasStraight() )
						cc.set( CardCombination.higherCardCombination(cc, new CardCombination(l, "straight")).get(0) );
					else if ( l.hasTriple() )
						cc.set( CardCombination.higherCardCombination(cc, new CardCombination(l, "threeOfAKind")).get(0) );
					else if ( l.hasTwoPair() )
						cc.set( CardCombination.higherCardCombination(cc, new CardCombination(l, "twoPair")).get(0) );
					else if ( l.hasOnePair() )
						cc.set( CardCombination.higherCardCombination(cc, new CardCombination(l, "onePair")).get(0) );
					else
						cc.set( CardCombination.higherCardCombination(cc, new CardCombination(l, "highCard")).get(0) );
				}
		
		return cc;
	}
	
	public CardCombination whichCardCombinationIsThis()
	{
		CardCombination cc = new CardCombination();
		
		if ( this.hasRoyalFlush() )
			cc.set( this, "royalFlush" );
		else if ( this.hasStraightFlush() )
			cc.set( this, "straightFlush" );
		else if ( this.hasQuadruplet() )
			cc.set( this, "fourOfAKind" );
		else if ( this.hasFlush() )
			cc.set( this, "fullHouse" );
		else if ( this.hasFlush() )
			cc.set( this, "flush" );
		else if ( this.hasStraight() )
			cc.set( this, "straight" );
		else if ( this.hasTriple() )
			cc.set( this, "threeOfAKind" );
		else if ( this.hasTwoPair() )
			cc.set( this, "twoPair" );
		else if ( this.hasOnePair() )
			cc.set( this, "onePair" );
		else
			cc.set( this, "highCard" );
		
		return cc;
	}
	
	// extra methods for the texas hold'em fixed limit strategy of www.PokerStars.com
	
	public static boolean isTopPair( CardList yourCards, CardList board )
	{
		Card highCardBoard = board.getHighCard();
		if ( (new CardList(yourCards.get(0), highCardBoard)).isOnePair() || (new CardList(yourCards.get(1), highCardBoard)).isOnePair() )
			return true;
		return false;
	}
	
	public static boolean isOverPair( CardList yourCards, CardList board )
	{
		if ( yourCards.isOnePair() )
			if ( board.getHighCard().getRank().toInt() < yourCards.get(0).getRank().toInt() )
				return true;
		return false;
	}
	
	public static boolean isOESD( CardList yourCards, CardList board )
	{
		CardList cards = new CardList( yourCards, board );
		cards.sortByCardValue();
		for ( int i = 0; i < cards.size()-3; i++ )			// -3 because of you want to test whether is it a OESD, so there have to be four cards in a row in relation to valueInt
			if ( (new CardList(cards.get(i), cards.get(i+1), cards.get(i+2), cards.get(i+3))).cardsInARow(4) )
				return true;
		return false;
	}
	
	public static boolean isFlushDraw( CardList yourCards, CardList board )
	{
		CardList cards = new CardList( yourCards, board );
		if ( cards.separateColours().get(0).size()==4 || cards.separateColours().get(1).size()==4 ||
				cards.separateColours().get(2).size()==4 || cards.separateColours().get(3).size()==4 )
			return true;
		return false;
	}
	
	public static boolean isMonsterDraw( CardList yourCards, CardList board )
	{
		if ( isOESD(yourCards, board) && isFlushDraw(yourCards, board) )
			return true;
		return false;
	}
	
	public static boolean isBellyBuster( CardList yourCards, CardList board )
	{
		CardList cards = new CardList( yourCards, board );
		cards.sortByCardValue();
		for ( int i = 0; i < cards.size()-3; i++ )	// -3 because of you want to test whether is it a belly buster, so there have to be four cards in a row in relation to valueInt
			if ( (new CardList(cards.get(i), cards.get(i+1), cards.get(i+2), cards.get(i+3))).cardsBetweenBeginningAndEndMissed().size() == 1 )
					return true;
		return false;
	}
	
	public static boolean isDoubleBellyBuster( CardList yourCards, CardList board )
	{
		CardList cards = new CardList( yourCards, board );
		cards.sortByCardValue();
		int howOften = 0;
		for ( int i = 0; i < cards.size()-3; i++ )	// -3 because of you want to test whether is it a belly buster, so there have to be four cards in a row in relation to valueInt
			if ( (new CardList(cards.get(i), cards.get(i+1), cards.get(i+2), cards.get(i+3))).cardsBetweenBeginningAndEndMissed().size() == 1 )
				++howOften;
		if( howOften > 1 )
			return true;
		return false;
	}
	
	public static boolean isOverCards( CardList yourCards, CardList board )
	{
		if ( (yourCards.get(0).getRank().toInt() > board.getHighCard().getRank().toInt()) && (yourCards.get(1).getRank().toInt() > board.getHighCard().getRank().toInt()) )
			return true;
		return false;			
	}
	
	public static boolean isOverCardsAndBellyBuster( CardList yourCards, CardList board )
	{
		return (isOverCards(yourCards, board) && isBellyBuster(yourCards, board));
	}
	
}
