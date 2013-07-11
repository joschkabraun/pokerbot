package strategy.strategyPokerChallenge.ringclient;

import strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.RingDynamics;

/**
 *
 * Maintains a version of the server's state on the client side.
 * Keeps track of bankroll, seat, and the state of the match in a usable form.
 * @author Kami II
 */
public class ClientRingDynamics extends RingDynamics {
	
    /**
     * The seat taken by this player.
     */
    public int seatTaken;
    
    public ClientRingDynamics() {
    }
    
}
