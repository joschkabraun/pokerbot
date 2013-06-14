package strategy.strategyPokerChallenge.ringclient;

import strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card;
import strategy.strategyPokerChallenge.data.Action;

/**
 *
 * This Class parses the Match State String coming from the server:
 * MATCHSTATE:<seat>:<handNumber>:<bettingSequence>:<cards>
 *
 * Created on 13.04.2008
 * @author Kami II
 */
public class ClientRingStateParser {

	/**
	 * numPlayers is derived from the cards sequence
	 */
	public int numPlayers;
	
	/**
	 * the seat is derived from the cards sequence. when this is not possible it will be
	 * set from the seat number out of the Match State String
	 */
	public int seat;
	
	/**
	 * seatToAct is derived from the seat statement in the Match State String
	 */
	public int seatToAct;
	
	/**
	 * handNumber is derived from the handNumber statement in the Match State String
	 */
	public int handNumber;

	/**
	 * bettingString is derived from the bettingSequence statement in the Match State String
	 */
	public String bettingString;
	
	/**
	 * lastAction is the last character in the bettingSequence statement in the Match State String
	 */
	public Action lastAction;
	
	/**
	 * the holes are derived from the cards statement in the Match State String
	 * most of the time only our own hole will be visible
	 * but at the end of a round all holes in the showdown will be visible
	 */
	public Card[][] hole;
	
	/**
	 * the flop is derived from the cards statement in the Match State String
	 */
	public Card[] flop;
	
	/**
	 * the turn is derived from the cards statement in the Match State String
	 */
	public Card turn;
	
	/**
	 * the river is derived from the cards statement in the Match State String
	 */
	public Card river;
	
	/**
	 * creates a new instance of the parser
	 */
	public ClientRingStateParser() {
		clear();
	}
	
	/**
	 * clears all attributes of the parser. this is called when parsing a new Match State String
	 */
	private void clear() {
		numPlayers = 0;
		seat = -1;
		seatToAct = -1;
		handNumber = -1;
		bettingString = "";
		lastAction = null;
		hole = null;
		flop = new Card[3];
		turn = null;
		river = null;		
	}
	
	/**
	 * parses a Match State String
	 */
	public void parseMatchStateMessage(String msg) {
		clear();
        int messageTypeColon = msg.indexOf(':');
        int seatColon = msg.indexOf(':', messageTypeColon+1);
        int handNumberColon = msg.indexOf(':', seatColon+1);
        int bettingSequenceColon = msg.indexOf(':', handNumberColon+1);
        seatToAct = Integer.parseInt(msg.substring(messageTypeColon+1, seatColon));
        handNumber = Integer.parseInt(msg.substring(seatColon+1, handNumberColon));
        bettingString = msg.substring(handNumberColon+1, bettingSequenceColon);
        if (bettingString.length() > 0) {
        	if (bettingString.charAt(bettingString.length() -1) != '/')
            	lastAction = Action.parseAction(bettingString.charAt(bettingString.length() -1));
        	else if (bettingString.length() > 1)
            	lastAction = Action.parseAction(bettingString.charAt(bettingString.length() -2));
        }
        setCards(msg.substring(bettingSequenceColon+1));
	}
	
	/**
	 * parses the cards statement and sets the cards (hole, flop, turn, river)
	 * but the number of players and the own seat are calculated, too
	 */
	private void setCards(String cardString) {
		String[] cards = cardString.split("/");
        // flop, turn and river are set here
		if (cards.length > 1) {
			flop = Card.toCardArray(cards[1]);
			if (cards.length > 2) {
				turn = new Card(cards[2]);
				if (cards.length > 3)
					river = new Card(cards[3]);
			}
		}

		// set all hole cards from the hole string
		// unknown cards will remain null pointers
		String[] holes = cards[0].split("\\|", -1);
		numPlayers = holes.length;
		hole = new Card[numPlayers][2];
		for (int i = 0; i < holes.length; i++) {
			if ((hole[i] = Card.toCardArray(holes[i])) != null) {
				if (seat == -1)
					seat = i;
				else if (seat > -1)
					seat = -2;
			}
		}

		// after all check if all but one hole are unknown
		// the only known hole is our own seat
		// if there were more than one hole known then the seatToAct must be our seat
		if (seat == -2)
			seat = seatToAct;
	}
	
	/**
	 * writes the parsed informations into a string to write it on the console
	 */
	public String toString() {
		String holeString = "";
		if (hole != null && hole.length > 0)
			for (int i = 0; i < hole.length; i++)
				holeString = holeString + hole[i][0] + hole[i][1] + " ";
		
		String flopString = "";
		if (flop != null)
			for (int i = 0; i < flop.length; i++)
				flopString = flopString + flop[i].toString();
		
		return "players: " + numPlayers +
				"\nhand#: " + handNumber +
				"\nseat: " + seat +
				"\nseat to act: " + seatToAct +
				"\nbettingString: " + bettingString +
				"\nlastAction: " + lastAction +
				"\nholes: " + holeString + 
				"\nflop: " + flopString + 
				"\nturn: " + turn + 
				"\nriver: " + river;
	}
}
