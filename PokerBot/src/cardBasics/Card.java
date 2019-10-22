package cardBasics;

import cardBasics.CardList;

/**
 * 
 * @author Joschka J. Braun
 * @version 1.0
 */
public class Card {
	
	public enum Rank {
		TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE, UNDEFINED;
		
		public int toInt() {
			switch ( this ) {
			case TWO:
				return 2;
			case THREE:
				return 3;
			case FOUR:
				return 4;
			case FIVE:
				return 5;
			case SIX:
				return 6;
			case SEVEN:
				return 7;
			case EIGHT:
				return 8;
			case NINE:
				return 9;
			case TEN:
				return 10;
			case JACK:
				return 11;
			case QUEEN:
				return 12;
			case KING:
				return 13;
			case ACE:
				return 14;
			case UNDEFINED:
				return 999;
			default:
				throw new RuntimeException("It was not possible find the Integer to this Rank");
			}
		}
		
		public static Rank toRank(int i) {
			switch ( i ) {
			case 2:
				return TWO;
			case 3:
				return THREE;
			case 4:
				return FOUR;
			case 5:
				return FIVE;
			case 6:
				return SIX;
			case 7:
				return SEVEN;
			case 8:
				return EIGHT;
			case 9:
				return NINE;
			case 10:
				return TEN;
			case 11:
				return JACK;
			case 12:
				return QUEEN;
			case 13:
				return KING;
			case 14:
				return ACE;
			case 999:
				return UNDEFINED;
			default:
				throw new RuntimeException("It was not possilbe finding the Rank to the Integer: " + i);
			}
		}
	}
	
	public enum Suit {
		CLUBS, DIAMONDS, HEARTS, SPADES, UNDEFINED;
		
		public int toInt() {
			switch (this) {
			case SPADES:
				return 1;
			case HEARTS:
				return 2;
			case DIAMONDS:
				return 3;
			case CLUBS:
				return 4;
			default:
				throw new RuntimeException("It was not possible to determine the Integer to the Suit");
			}
		}
		
		public static Suit toSuit(int i) {
			switch (i) {
			case 1:
				return SPADES;
			case 2:
				return HEARTS;
			case 3:
				return DIAMONDS;
			case 4:
				return CLUBS;
			default:
				throw new RuntimeException("It was not possible to determine the Suit for the Integer: " + i);
			}
		}
	}
	
	public Rank rank;
	public Suit suit;
	
	public Card( Card card ) {
		this.rank = card.rank;
		this.suit = card.suit;
	}
	
	public Card(Rank rank, Suit suit) {
		this.rank = rank;
		this.suit = suit;
	}
	
	public Card() {
	}
	
	public Rank getRank() {
		return this.rank;
	}
	
	public void setRank( Rank rank ) {
		this.rank = rank;
	}
	
	public Suit getSuit() {
		return this.suit;
	}
	
	public void setSuit( Suit suit ) {
		this.suit = suit;
	}
	
	public void set( int rankInt, int suitInt ) {
		this.rank = Rank.toRank(rankInt);
		this.suit = Suit.toSuit(suitInt);
	}
	
	public void set( Card card ) {
		this.rank = card.rank;
		this.suit = card.suit;
	}
	
	@Override
	public String toString() {
		return "(" + this.suit + ", " + this.rank + ")";
	}

	public boolean equals( Card card ) {
		if (this.getRank() == card.getRank())
			if (this.getSuit() == card.getSuit())
				return true;
		return false;
	}
	
	public static CardList higherValue( Card card1, Card card2 ) {
		CardList result = new CardList();
		if (card1.getRank().toInt() >= card2.getRank().toInt()) {
			result.add(card1);
			result.add(card2);
		} else {
			result.add(card2);
			result.add(card1);
		}
		return result;
	}
	
	/**
	 * Returns from a string, for example the string "9c" represents the card "(clubs, nine)".
	 * 
	 * @param s a string from which you want get the card representation
	 * @return the card representation
	 */
	public static Card stringToCard( String s ) {
		if ( s.length() != 2 )
			throw new IllegalArgumentException( "The length of the commited input is unequal two." );
		
		Rank rank;
		switch ( s.charAt(0) )
		{
		case '2':
			rank = Rank.TWO;
			break;
		case '3':
			rank = Rank.THREE;
			break;
		case '4':
			rank = Rank.FOUR;
			break;
		case '5':
			rank = Rank.FIVE;
			break;
		case '6':
			rank = Rank.SIX;
			break;
		case '7':
			rank = Rank.SEVEN;
			break;
		case '8':
			rank = Rank.EIGHT;
			break;
		case '9':
			rank = Rank.NINE;
			break;
		case 'T':
			rank = Rank.TEN;
			break;
		case 'J':
			rank = Rank.JACK;
			break;
		case 'Q':
			rank = Rank.QUEEN;
			break;
		case 'K':
			rank = Rank.KING;
			break;
		case 'A':
			rank = Rank.ACE;
			break;
		default:
			throw new RuntimeException("It was not possible to determine the rank for the character: " + s.charAt(0));
		}
		
		Suit suit;
		switch ( s.charAt(1) )
		{
		case 's':
			suit = Suit.SPADES;
			break;
		case 'h':
			suit = Suit.HEARTS;
			break;
		case 'd':
			suit = Suit.DIAMONDS;
			break;
		case 'c':
			suit = Suit.CLUBS;
			break;
		default:
			throw new RuntimeException("It was not possible to determine the suit for the character: " + s.charAt(1));
		}
		
		return new Card(rank, suit);
	}
	
}
