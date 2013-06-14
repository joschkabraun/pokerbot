package gameBasics;

import other.*;

public class SeatPosition

{
	
	/**
	 * The number of seats behind the button (BU).
	 * The button sits the number of players on the table after the button!
	 * If x is the number of players at the table, than is behindBU of the button equal x!
	 */
	public int behindBU;
	public String positionNamed;		// correct name is "namedPosition"
	public String positionRange;
	
	public SeatPosition()
	{
	}
	
	public SeatPosition( int behindBU, String positionNamed, String positionRange )
	{
		this.behindBU = behindBU;
		this.positionNamed = positionNamed;
		this.positionRange = positionRange;
	}
	
	public SeatPosition( int behindBU, int numberOfPlayersOnTable )
	{
		this.behindBU = behindBU;
		this.positionNamed = this.behindBUToPositionNamed( numberOfPlayersOnTable );
		this.positionRange = this.positionNamedToPositonRange();
	}
	
	public SeatPosition( SeatPosition seat )
	{
		this.behindBU = seat.behindBU;
		this.positionNamed = seat.positionNamed;
		this.positionRange = seat.positionRange;
	}
	
	public SeatPosition( int seatNumberAbsolute, int[] possibleSeatNumbers, int seatNumberOfButton )
	{
		this.behindBU = seatNumberAbsoluteToBehindBU( seatNumberAbsolute, possibleSeatNumbers, seatNumberOfButton );
		this.positionNamed = this.behindBUToPositionNamed( possibleSeatNumbers.length );
		this.positionRange = this.positionNamedToPositonRange();
	}
	
	public boolean equals( SeatPosition sP ) {
		if ( this.behindBU == sP.behindBU )
			if ( this.positionNamed.equals( sP.positionNamed ) )
				if ( this.positionRange.equals( sP.positionRange ) )
					return true;
		return false;
	}
	
	@Override
	public String toString() {
		return "(number seats behind BU: " + this.behindBU + ", position named: " + this.positionNamed + ", name position range: " + this.positionRange +")";
	}
	
	/**
	 * Returns the number of seats behind the button.
	 * 
	 * @param seatNumberAbsolute the seat number of the player in the game in the hand history
	 * @param possibleSeatNumbers an array of integers which are the seat numbers which exist in the game 
	 * @param seatNumberOfButton the seat number of the button in the game
	 * @return the adjunct behindBU for a SeatPosition
	 */
	public static int seatNumberAbsoluteToBehindBU( int seatNumberAbsolute, int[] possibleSeatNumbers, int seatNumberOfButton )
	{
		int behindBU = 0;
		int numberOfPlayersOnTable = possibleSeatNumbers.length;
		
		if ( seatNumberAbsolute > seatNumberOfButton )
			behindBU = Tools.entriesBetween( possibleSeatNumbers, seatNumberOfButton, seatNumberAbsolute );
		else if ( seatNumberAbsolute == seatNumberOfButton )
			behindBU = numberOfPlayersOnTable;
		else
		{
			int differentAbsoluteAndButton = 0;
			for ( int i = 0; i < possibleSeatNumbers.length; i++ )
				if ( possibleSeatNumbers[ i ] == seatNumberAbsolute )
				{
					for ( int j = i; j < possibleSeatNumbers.length; j++ )
					{
						if ( possibleSeatNumbers[ j ] == seatNumberOfButton )
							break;
						++differentAbsoluteAndButton;
					}
					break;
				}
			
			behindBU = numberOfPlayersOnTable - differentAbsoluteAndButton; 
		}
		
		return behindBU;
	}
	
	public String behindBUToPositionNamed( int numberOfPlayersOnTable )			// You assume a table with maximal nine and minimal two players.
	{
		String returnValue = "null";
		int numberPlayers = numberOfPlayersOnTable;
		
		if ( this.behindBU == 1 ) {
			returnValue = "smallBlind";		// small blind = SB. Attention for SB = small bet
			return returnValue;
		} else if ( this.behindBU == numberPlayers ) {
			returnValue = "button";			// button = BU
			return returnValue;
		}
		
		if ( numberPlayers >= 3 )
		{
			if ( this.behindBU == 2 )
				returnValue = "bigBlind";	// big blind = BB. Attention for BB = big bet
			
			if ( numberPlayers >= 4 )
			{
				if ( this.behindBU == (numberPlayers-1) )
					returnValue = "cutOff";	// cut off = CO
				
				else if ( numberPlayers >= 8 )
				{
					if ( this.behindBU == 3 )
						returnValue = "UTG";	// UTG = under the gun
					else if ( this.behindBU == (numberPlayers-4) )
						returnValue = "MP1";	// MP = middle position
					else if ( this.behindBU == (numberPlayers-3) )
						returnValue = "MP2";
					else if ( this.behindBU == (numberPlayers-2) )
						returnValue = "MP3";
					
					if ( numberPlayers == 9 )
						if ( this.behindBU == 4 )
							returnValue = "UTG+1";
				}
				
				else
					for ( int i = 1; i < 4; i++ )					// determining of the number (=i) of possible player in middle position (=mp)
						if ( numberPlayers == (4+i) )
							for ( int j = 1; j <= i; j++ )
							{
								if ( this.behindBU == (numberPlayers - 2 - i + j) )
									returnValue = "MP" + j;
							}
			}
		}
		
		if ( returnValue == "null" )
			throw new IllegalStateException( "It was not possible to classify positionNamed for the SeatPosition: " + this.toString() );
		
		return returnValue;
	}
	
	public String positionNamedToPositonRange()
	{
		String returnValue = "null";
		
		switch ( this.positionNamed )
		{
		case "smallBlind":
			returnValue = "smallBlind";
			break;
		case "bigBlind":
			returnValue = "bigBlind";
			break;
		case "UTG": case "UTG+1": case "UTG+2":
			returnValue = "earlyPosition";
			break;
		case "MP1": case "MP2": case "MP3":
			returnValue = "middlePosition";
			break;
		case "cutOff": case "button":
			returnValue = "latePosition";
			break;
		}
		
		if ( returnValue == "null" )
			throw new IllegalStateException( "It was not possible to classify positionRange by positionNamed for the SeatPosition: " + this.toString() );
		
		return returnValue;
	}
	
}
