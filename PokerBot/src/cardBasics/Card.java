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
		int valueClassification = 0;
		
		switch( this.value )
		{
		case "two":
			valueClassification = 2;
			break;
		case "three":
			valueClassification = 3;
			break;
		case "four":
			valueClassification = 4;
			break;
		case "five":
			valueClassification = 5;
			break;
		case "six":
			valueClassification = 6;
			break;
		case "seven":
			valueClassification = 7;
			break;
		case "eight":
			valueClassification = 8;
			break;
		case "nine":
			valueClassification = 9;
			break;
		case "ten":
			valueClassification = 10;
			break;
		case "jack":
			valueClassification = 11;
			break;
		case "queen":
			valueClassification = 12;
			break;
		case "king":
			valueClassification = 13;
			break;
		case "ace":
			valueClassification = 14;
			break;
		case "undefined":
			valueClassification = 999;
			break;
		}
		
		if ( valueClassification == 0 )
			throw new IllegalStateException( "It was not possible to classify valueInt by value for the Card: " + this.toString() );
		
		return valueClassification;
	}
	
	public String valueIntClassification()
	{
		String valueIntClassification = "null";
		
		switch( this.valueInt )
		{
		case 2:
			valueIntClassification = "two";
			break;
		case 3:
			valueIntClassification = "three";
			break;
		case 4:
			valueIntClassification = "four";
			break;
		case 5:
			valueIntClassification = "five";
			break;
		case 6:
			valueIntClassification = "six";
			break;
		case 7:
			valueIntClassification = "seven";
			break;
		case 8:
			valueIntClassification = "eight";
			break;
		case 9:
			valueIntClassification = "nine";
			break;
		case 10:
			valueIntClassification = "ten";
			break;
		case 11:
			valueIntClassification = "jack";
			break;
		case 12:
			valueIntClassification = "queen";
			break;
		case 13:
			valueIntClassification = "king";
			break;
		case 14:
			valueIntClassification = "ace";
			break;
		case 999:
			valueIntClassification = "undefined";
			break;
		}
		
		if ( valueIntClassification == "null" )
			throw new IllegalStateException( "It was not possible to classify value by valueInt for the Card: " + this.toString() );
			
		return valueIntClassification;
		
	}
	
	public int colourClassification()
	{
		int colourClassification = 0;
		
		switch ( this.colour )
		{
		case "spades":							// "spades" is in German "Schippe"	
			colourClassification = 1;
			break;
		case "hearts":							// "hearts" is in German "Herz"
			colourClassification = 2;
			break;
		case "diamonds":						// "diamonds" is in German "Karo"
			colourClassification = 3;
			break;
		case "clubs":							// "clubs" is in German "Kreuz"
			colourClassification = 4;
			break;
		case "undefined":
			colourClassification = 999;
			break;
		}
		
		if ( colourClassification == 0 )
			throw new IllegalStateException( "It was not possible to classify colourToInt by colour for the Card: " + this.toString() );
		
		return colourClassification;
	}
	
	public String colourToIntClassification()
	{
		String colour = "null";
		
		switch( this.colourToInt )
		{
		case 1:
			colour = "spades";
			break;
		case 2:
			colour = "hearts";
			break;
		case 3:
			colour = "diamonds";
			break;
		case 4:
			colour = "clubs";
			break;
		case 999:
			colour = "undefined";
			break;
		}
		
		if ( colour == "null" )
			throw new IllegalStateException( "It was not possible to classify colour by colourToInt for the Card: " + this.toString() );
		
		return colour;
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
