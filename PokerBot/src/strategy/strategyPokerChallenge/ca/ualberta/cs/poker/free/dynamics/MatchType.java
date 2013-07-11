package strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics;

/**
 * This class represents the constants which define the match, both
 * at the level of the hand and the match as a whole.
 * @author maz
 *
 */
public class MatchType {
    
    /**
     * The size of the big blind
     */
    public double bigBlindSize = 2;

    /**
     * Get whether the stacks are effectively infinite.
     * For Doyle's Game, this is false, because the betting
     * is explicitly capped at doyleLimit instead of the underlying
     * stack size.
     */
    public boolean stackBoundGame;
    
    public MatchType() {
    }
    
}
