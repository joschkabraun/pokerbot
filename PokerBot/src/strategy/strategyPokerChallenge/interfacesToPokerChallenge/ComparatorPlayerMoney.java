package strategy.strategyPokerChallenge.interfacesToPokerChallenge;

import handHistory.PlayerMoney;

import java.util.Comparator;

public class ComparatorPlayerMoney implements Comparator<PlayerMoney> {

	/**
	 * This method compares two PlayerMoneys by the number of seats which the pm.player's sit.
	 */
	public int compare(PlayerMoney pm1, PlayerMoney pm2) {
		if ( pm1.player.seatBehindBU.behindBU < pm2.player.seatBehindBU.behindBU )
			return -1;
		else if ( pm1.player.seatBehindBU.behindBU == pm2.player.seatBehindBU.behindBU )
			return 0;
		else
			return 1;
	}

}
