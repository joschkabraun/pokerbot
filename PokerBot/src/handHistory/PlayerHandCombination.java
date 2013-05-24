package handHistory;

import cardBasics.CardList;
import cardBasics.CardCombination;
import gameBasics.Player;

/**
 * This class implements the object PlayerHand.
 * The object SeatPlayer is an object turning data types Player, CardList and CardCombination or the data types Player and String to one data type.
 * The data type consists of a player, his/her hole cards and the card combination he/she has or of a player and whether he show is hand (there was than returned).
 * 
 * @author Joschka J. Braun
 * @version 1.0
 */
public class PlayerHandCombination

{
	
	/**
	 * The player of the hand cards
	 */
	public Player player;
	
	/**
	 * The hand cards of the play
	 */
	public CardList handCards;
	
	/**
	 * The hand cards of the player.
	 */
	public CardCombination handCombination;
	
	/**
	 * If the player has returned his cards there is "returned".
	 */
	public String stringAlternativeCards;
	
	public PlayerHandCombination() {
		this.player = new Player();
		this.handCards = new CardList();
		this.handCombination = new CardCombination();
	}
	
	public PlayerHandCombination( Player player, CardList handCards, CardCombination handCombination )
	{
		this.player = new Player( player );
		this.handCards = handCards;
		this.handCombination = handCombination;
	}
	
	public PlayerHandCombination( Player player, String ret )
	{
		this.player = new Player( player );
		this.stringAlternativeCards = ret;
	}
	
	public PlayerHandCombination( PlayerHandCombination playerHand )
	{
		this.player = new Player( playerHand.player );
		if ( playerHand.stringAlternativeCards == null ) {
			this.handCards = playerHand.handCards;
			this.handCombination = playerHand.handCombination;
		} else
			this.stringAlternativeCards = playerHand.stringAlternativeCards;
	}
	
	@Override
	public String toString() {
		return "(name player: " + this.player.name + ", hand cards: " + this.handCards + ", combination: " + this.handCombination.combination + ")";
	}
	
	
}
