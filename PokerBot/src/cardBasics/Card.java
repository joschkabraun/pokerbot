package cardBasics;

import cardBasics.CardList;

/**
 * 
 * @author Joschka J. Braun
 * @version 1.0
 */
public class Card

{
	
	public String value;
	public String colour;
	public int valueInt;
	public int colourToInt;
	
	public Card( String value, String colour )
	{
		this.value = value;
		this.colour = colour;
		this.valueInt = this.valueClassification();
		this.colourToInt = this.colourClassification();
	}
	
	public Card( Card card )
	{
		this.value = card.value;
		this.colour = card.colour;
		this.valueInt = card.valueClassification();
		this.colourToInt = this.colourClassification();
	}
	
	public Card( int valueInt, String colour )
	{
		this.valueInt = valueInt;
		this.value = this.valueIntClassification();
		this.colour = colour;
		this.colourToInt = this.colourClassification();
	}
	
	public Card( int valueInt, int colourInt )
	{
		this.valueInt = valueInt;
		this.value = this.valueIntClassification();
		this.colourToInt = colourInt;
		this.colour = this.colourToIntClassification();
	}
	
	public Card() {
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void setValue( String value ) {
		this.value = value;
	}
	
	public String getColour() {
		return this.colour;
	}
	
	public void setColour( String colour ) {
		this.colour = colour;
	}
	
	public void set( String value, String colour )
	{
		this.value = value;
		this.valueInt = this.valueClassification();
		this.colour = colour;
		this.colourToInt = this.colourClassification();
	}
	
	public void set( int valueInt, int colourInt )
	{
		this.valueInt = valueInt;
		this.value = this.valueIntClassification();
		this.colourToInt = colourInt;
		this.colour = this.colourToIntClassification();
	}
	
	public void set( Card card )
	{
		this.value = card.value;
		this.colour = card.colour;
		this.valueInt = card.valueClassification();
		this.colourToInt = this.colourClassification();
	}
	
	/**
	 * This function classifies the value of a card and does the value comparable.
	 * 
	 * @return an integer value matched to the value
	 */
	public int valueClassification()
	{
		switch( this.value )
		{
		case "two":
			return 2;
		case "three":
			return 3;
		case "four":
			return 4;
		case "five":
			return 5;
		case "six":
			return 6;
		case "seven":
			return 7;
		case "eight":
			return 8;
		case "nine":
			return 9;
		case "ten":
			return 10;
		case "jack":
			return 11;
		case "queen":
			return 12;
		case "king":
			return 13;
		case "ace":
			return 14;
		case "undefined":
			return 999;
		default:
			throw new IllegalStateException( "It was not possible to classify valueInt by value for the Card: " + this.toString() );
		}
	}
	
	public String valueIntClassification()
	{
		switch( this.valueInt )
		{
		case 2:
			return "two";
		case 3:
			return "three";
		case 4:
			return "four";
		case 5:
			return "five";
		case 6:
			return "six";
		case 7:
			return "seven";
		case 8:
			return "eight";
		case 9:
			return "nine";
		case 10:
			return "ten";
		case 11:
			return "jack";
		case 12:
			return "queen";
		case 13:
			return "king";
		case 14:
			return "ace";
		case 999:
			return "undefined";
		default:
			throw new IllegalStateException( "It was not possible to classify value by valueInt for the Card: " + this.toString() );
		}
	}
	
	public int colourClassification()
	{
		switch ( this.colour )
		{
		case "spades":							// "spades" is in German "Schippe"	
			return 1;
		case "hearts":							// "hearts" is in German "Herz"
			return 2;
		case "diamonds":						// "diamonds" is in German "Karo"
			return 3;
		case "clubs":							// "clubs" is in German "Kreuz"
			return 4;
		case "undefined":
			return 999;
		default:
			throw new IllegalStateException( "It was not possible to classify colourToInt by colour for the Card: " + this.toString() );
		}
	}
	
	public String colourToIntClassification()
	{
		switch( this.colourToInt )
		{
		case 1:
			return "spades";
		case 2:
			return "hearts";
		case 3:
			return "diamonds";
		case 4:
			return "clubs";
		case 999:
			return "undefined";
		default:
			throw new IllegalStateException( "It was not possible to classify colour by colourToInt for the Card: " + this.toString() );
		}
	}
	
	@Override
	public String toString()
	{
		return "(" + this.colour + ", " + this.value + ")";
	}

	public boolean equals( Card card )
	{
		if ( this.value == card.value )
		{
			if ( this.colour == card.colour )
				return true;
			else
				return false;
		}
		else
			return false;
	}
	
	public static CardList higherValue( Card card1, Card card2 )
	{
		CardList resultOfHigherValue = new CardList();
		
		Card higherCard = ( card1.valueClassification() > card2.valueClassification() ) ? card1 : card2;
		
		if( card1.equals( higherCard ) == true )
		{
			resultOfHigherValue.add( card1 );
			resultOfHigherValue.add( card2 );
		}
		else
		{
			resultOfHigherValue.add( card2 );
			resultOfHigherValue.add( card1 );
		}
		
		return resultOfHigherValue;
	}
	
	/**
	 * Returns from a string, for example the string "9c" represents the card "(clubs, nine)".
	 * 
	 * @param s a string from which you want get the card representation
	 * @return the card representation
	 */
	public static Card stringToCard( String s )
	{
		Card card = new Card();
		
		if ( s.length() != 2 )
			throw new IllegalArgumentException( "The length of the commited input is unequal two." );
		else
		{
			String value = "";
			switch ( s.charAt(0) )
			{
			case '2':
				value = "two";
				break;
			case '3':
				value = "three";
				break;
			case '4':
				value = "four";
				break;
			case '5':
				value = "five";
				break;
			case '6':
				value = "six";
				break;
			case '7':
				value = "seven";
				break;
			case '8':
				value = "eight";
				break;
			case '9':
				value = "nine";
				break;
			case 'T':
				value = "ten";
				break;
			case 'J':
				value = "jack";
				break;
			case 'Q':
				value = "queen";
				break;
			case 'K':
				value = "king";
				break;
			case 'A':
				value = "ace";
				break;
			}
			
			String colour = "";
			switch ( s.charAt(1) )
			{
			case 's':
				colour = "spades";
				break;
			case 'h':
				colour = "hearts";
				break;
			case 'd':
				colour = "diamonds";
				break;
			case 'c':
				colour = "clubs";
				break;
			}
			
			card.set( value, colour );
		}
		
		return card;
	}
	
}
