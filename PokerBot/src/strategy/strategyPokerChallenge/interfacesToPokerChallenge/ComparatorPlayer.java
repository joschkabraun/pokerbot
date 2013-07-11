package strategy.strategyPokerChallenge.interfacesToPokerChallenge;

import gameBasics.Player;

import java.util.Comparator;

public class ComparatorPlayer implements Comparator<Player> {

	/**
	 * This method is for using the method sort for arrays. With using this method
	 * an array of players should be arranged after their seats behind the button
	 * at which the first entry the small blind, the second the big blind is and so on ...
	 */
	public int compare(Player p1, Player p2) {
		if ( p1.seatBehindBU.behindBU < p2.seatBehindBU.behindBU )
			return -1;
		else if ( p1.seatBehindBU.behindBU == p2.seatBehindBU.behindBU )
			return 0;
		else
			return 1;
	}

}
