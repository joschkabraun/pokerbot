package handHistory;

import gameBasics.Player;

/**
 * This class implements the object SeatNumberPlayer.
 * The object SeatNumberPlayer is an object turning data types Integer and Player to one data type.
 * The data type consists of a number of the seat and the player who sit there.
 * 
 * @author Joschka J. Braun
 * @version 1.0
 */
public class SeatNumberPlayer

{
	
	/**
	 * The absolute seat number on the table. Absolute means the seat number in the hand history.
	 */
	public int seatNumber;
	
	/**
	 * The player who is sitting on the seat number.
	 */
	public Player player;
	
	public SeatNumberPlayer( int seatNumber, Player player )
	{
		this.seatNumber = seatNumber;
		this.player = new Player( player );
	}
	
	@Override
	public String toString() {
		return "(player name:" + this.player.name + ", seat number: " + this.seatNumber + ")";
	}
	
}
