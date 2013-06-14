package strategy.strategyPokerChallenge.simulation.real.datastructure;

import java.util.Arrays;

import strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card;
import strategy.strategyPokerChallenge.data.Bucket;

/**
 * A datastructure which represents some important informations about the player.
 */
public class RealPlayerState {
	private String name;
	private Card[] hole;
	private boolean active;
	private boolean canAct;
	private int amountIn;
	private int amountBet;
	private int seat;
	private int playerSeat;
	private int bucket;
	
	public RealPlayerState(boolean active, boolean canRaiseNextRound, int inPot, int seat, int playerSeat) {
		this.hole = null;
		this.active = active;
		this.bucket = -1;
		this.amountIn = inPot;
		this.amountBet = this.amountIn;
		this.canAct = canRaiseNextRound;
		this.seat = seat;
		this.playerSeat = playerSeat;
	}
	
	public RealPlayerState(RealPlayerState player) {
		this.name = player.name;
		this.active = player.active;
		this.bucket = player.bucket;
		this.amountIn = player.amountIn;
		this.amountBet = this.amountIn;
		this.canAct = player.canAct;
		this.seat = player.seat;
		this.playerSeat = player.playerSeat;
		if(player.hole!=null) {
			this.hole = new Card[2];
			this.hole[0] = new Card(player.hole[0].rank, player.hole[0].suit);
			this.hole[1] = new Card(player.hole[1].rank, player.hole[1].suit);
		}
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the amountIn
	 */
	public int getAmountIn() {
		return amountIn;
	}

	/**
	 * @param amountIn the amountIn to set
	 */
	public void setAmountIn(int amountIn) {
		this.amountIn = amountIn;
	}


	/**
	 * @return the amountBet
	 */
	public int getAmountBet() {
		return amountBet;
	}

	/**
	 * @param amountBet the amountBet to set
	 */
	public void setAmountBet(int amountBet) {
		this.amountBet = amountBet;
	}

	/**
	 * @return the canRaiseNextRound
	 */
	public boolean isCanRaiseNextRound() {
		return canAct;
	}

	/**
	 * @param canRaiseNextRound the canRaiseNextRound to set
	 */
	public void setCanRaiseNextRound(boolean canRaiseNextRound) {
		this.canAct = canRaiseNextRound;
	}

	public boolean canAct(int maxAmount) {
		return isActive() && (canAct || amountIn<maxAmount);
	}

	public void addPlayerNode(RealPlayerNode node) {
		this.bucket = node.getBucket();
		this.hole = node.getHole();
	}

	/**
	 * @return the seat
	 */
	public int getSeat() {
		return seat;
	}


	public int getPlayerSeat() {
		return this.playerSeat;
	}

	public Card[] getHole() {
		return hole;
	}

	public int getBucket() {
		return bucket;
	}

	public void setHole(Card[] hole) {
		 this.hole = hole;
		 this.bucket = Bucket.getBucket(hole);
		
	}
	
	public String toString() {
		return "Seat: " + seat + " Hole: " + Arrays.deepToString(hole);
	}

}
