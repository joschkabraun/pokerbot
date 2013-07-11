package strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics;

/**
 * 
 * <P>
 * This class provides the mechanics for three games. A fourth (no-limit
 * ring) is also given, but in a more untested form.</P>
 * <UL>
 * <LI>RING LIMIT:2/4 limit texas hold'em with 3-10 players</LI>
 * <LI>HEADS-UP LIMIT:2/4 limit reverse blinds texas hold'em with 2 players</LI>
 * <LI>HEADS-UP NO-LIMIT:No-limit reverse blinds texas hold'em with 2 players (NOTE: This will not be used for the competition: Doyle's game will be used instead)</LI>
 * <LI>HEADS-UP DOYLES:1000 chip limit reverse blinds texas hold'em with 2 players</LI>
 * </UL>
 * <P>DISCLAIMER: Although there are notes throughout this implementation, THE
 * IMPLEMENTATION IS THE FORMAL STATEMENT OF THE RULES, NOT THE COMMENTS.</P>
 * 
 * 
 * 
 * 
 * <H2>Ring Limit (More than 2 players)</H2>
 * 
 * 
 * <P>If there are k players, seats are numbered 0 through k-1.</P>
 * <P>The <I>i</I>th player on the <I>j</I>th round is in seat <I>i-j</I> MOD <I>k</I>.</P>
 * <P> The highest number seat is the <B>button</B>.  Seat 0
 * is the <b>small blind seat</b>. Seat 1 is the <b>big blind seat</b>. The first to act (voluntarily) in the
 * first round is Seat 2, and thereafter the lowest index seat is the first
 * to act.</P>
 *
 * <P>In a hand of poker, a player can put money in the <B>pot</B> from her <B>stack</B>. Whenever it is said that
 * a player puts money in the pot, it is assumed it is from her stack (and is therefore removed from her stack). 
 * The money in
 * the pot goes to the winner or winners of the pot. We assume that the stack of each
 * player is quite large (at least 48 chip for each hand played) and therefore no player
 * goes bankrupt.</P>
 * 
 * <P>At all times, it is maintained how much each player has put in the pot. The maximum amount in the
 * pot is the maximum of all these amounts.
 * At the beginning of a hand, all players are <B>active</B>, meaning they are
 * still eligible for the money in the pot.</P>
 * 
 * <P>
 * The small blind amount is 1 chip, the big blind amount is 2 chips.
 * The <B>bet amount</B> on the preflop and flop is 2 chips, and on the turn and river is 4 chips.
 * </P>
 * 
 * <P>The hand begins by the player in the small blind seat putting 1 chip into the pot. The player
 * in the big blind seat then puts 2 chips in the pot. </P>
 * 
 * <P>Two private cards are then dealt to each player. These are only revealed to that player.</P>
 * 
 * <H3>Preflop Betting</H3>
 * <P>Seat 2 is the first to make a voluntary
 * act. She has three choices:
 * </P>
 * <UL>
 * <LI><B>fold</B>: Place no more money in the pot, and become <B>inactive</B> (ineligible for the pot).</LI>
 * <LI><B>call</B>: Bring the amount she has in the pot to the maximum amount in the pot.</LI>
 * <LI><B>raise</B>: Bring the amount she has in the pot to the maximum amount in the pot, and then put in the bet amount (2 chips) </LI>.
 * </UL>
 * <P>
 * For any Seat k (except the button), the <B>next seat</B> is Seat k+1. The next
 * seat after the button is Seat 0. The <B>next active player</B> is determined by
 * going from one seat to the next until an active player is found.
 * </P>
 * 
 * <P>A round ends after both everyone has made a voluntary action (not a blind) and if there was a raise,
 * all (but the last to raise) active players have made a voluntary action thereafter.</P>
 * 
 * 
 * <P>After a player has made a voluntary act, if the round has not ended, the next active player 
 * makes an voluntary action. She can always be fold or call.
 * She can raise if no more than two raises have already been made.</P>
 * 
 * <H3>Flop</H3>
 * <P>After the preflop betting has ended, three flop cards are revealed to all the players.</P>
 * 
 * A round of betting similar to the preflop betting, except:
 * <UL>
 * <LI>The first player to act is the active player in the seat with lowest index.</LI>
 * <LI>A player may raise if there have been less than four raises made previously on the round.</LI>
 * </UL>
 * 
 * <h3>Turn</h3>
 * <P>After the flop betting has ended, the turn card is revealed to all the players.</P>
 * <P>A betting round follows similar to the flop betting, except the betting amount is now 4 chips.
 * 
 * <h3>River</h3>
 * <P>After the river betting has ended, the river card is revealed to all the players.</P>
 * <P>A betting round follows identical to the turn betting.</P>
 * 
 * <h3>Showdown</h3>
 * <P>After the river betting has ended, any players who are active (yet to fold this hand) are 
 * eligible for the pot. All active players reveal their cards: by considering each players' two private
 * cards and the five public cards (three flop, the turn, the river), one can determine, given two
 * players, which has better cards or if their cards are of equal quality. See <A href="HandAnalysis.html">HandAnalysis</A>.
 * Thus, we consider all players who have the best cards, who we refer to as <B>winners</B> of the hand.
 * If the number of winners divides the number of chips in the pot, it is divided evenly. If the number
 * of winners does not divide the number of chips in the pot, then extra chips are given to winners in seat number
 * ordering.
 * </P>
 *
 * <H2>Heads-Up Limit</H2>
 * If there are TWO PLAYERS, then it is REVERSE BLINDS: the button (Seat 1) 
 * plays the small blind, the other player (Seat 0) the big blind. Seat 1 goes first
 * on the first round, and Seat 2 thereafter. Otherwise, the rules are the same
 * as ring limit.
 * 
 * <H2>Heads-Up No Limit</H2>
 * <P>The game of heads-up no limit is similar to heads-up limit, but there are more actions available to the players.</P>
 * <P>It is also the case that the players have a stack size limited to (1000?) chips. If a player has all of her chips in
 * the pot, and loses the hand, the match ends.
 * <P>If a player is in Seat 0 (the big blind seat) and only has 1 chip, she can still put in that 1 chip as the big blind.
 * Seat 1 must still put a second chip in the pot to call the big blind.</P>
 * <P>In particular, a player can still fold or call, and any time it is a player's turn to act and her stack is not zero, 
 * she can raise (no limit on the number of raises per round).</P>
 * <P>The bet amount is, to some degree, the choice of the player raising. If it is the first bet on a round, it
 * must be at least the size of the big blind (2 chips). If it is not, then it must at least be the size of the previous bet.
 * The exception to this rule is that a player can always go all in, putting all her remaining stack in the pot. Of course,
 * the player cannot have a negative stack after a raise, which places an upper limit upon how much she can raise.</P>
 * 
 * <P>
 * If one player puts more money in the pot than the other is capable due to her stack, then the other is only required to go all in
 *  to stay active. However, the player with more in the pot gets back the difference of the amounts in the pot before the rest of
 *  the pot is given to the winner(s).
 * </P>
 * 
 * 
 * 
 * <H2>Implementation of the Protocol</H3>
 * <P>
 * Before every action of a player, and at the end of the hand, all players receive the state of the hand (see
 *   {@link #getMatchState(int)}).
 * It is of the form:<BR>
 * {@code MATCHSTATE:<seat>:<handNumber>:<bettingSequence>:<cards>}<BR>
 * 
 * <P>In a simple betting sequence (for limit games), the betting sequence is the list of raises &quot;r&quot;, calls &quot;c&quot;, and folds &quot;f&quot;.
 * with the ends of rounds specified by slashes. In an advanced betting sequence, the total amount the player has in the
 * pot that round follows a call or raise.
 * <P>Actions are preformed by echoing the match state as it was sent and appending a colon and an action string.</P>
 * A fold is indicated by &quot;f&quot;, a call by &quot;c&quot;, and a raise by &quot;r&quot; and possibly an amount. The
 * amount is not not required in the limit game.
 * </P>
 * <H3>Illegal actions</H3>
 * <P>If an illegal action is submitted, there is a mapping from all actions (strings) to legal ones. In particular, if a player
 * makes an action that does not begin with 'r', 'f', or 'c', the player calls. If a player
 * attempts to raise and cannot raise, the player calls. If a player gives a raise that is too high, it is changed to the maximum
 * raise. If a player gives a raise that is too low or does not specify an amount, it is the minimum raise.</P>
 * 
 * <P>Blinds have been added to the betting sequence in the more generic language.
 * <P>Current stack sizes has not been added to the match state.
 * <H3>Acknowledgements</H3>
 * 
 * The rules for heads-up no-limit and ring limit are based largely upon the rules of the US Poker 
 * Association.<BR>
 * The rules for limit are based upon the rules found in Poker Academy.<BR>
 * 
 * Darse Billings clarified some of the rules in the no-limit ring implementation, which
 * will not be in the competition this year.<BR>
 * 
 * @author Martin Zinkevich
 */
public class RingDynamics {
	public MatchType info;
    /**
     * The number of players
     */
    public int numPlayers;

    /**
     * player[i] is the player in seat i
     */
    public int[] player;
    
    /**
     * The stack size of a particular player.
     * Note that this is NOT indexed by seat, but by player
     */
    public double[] stack;
    
    /**
     * inPot[i] is the contribution to the pot of the 
     * player in seat i (in CHIPS).
     */
    public double[] inPot;
    
    /**
     * active[i] is true if the player in seat i is 
     * still active (has not folded).
     */
    public boolean[] active;

    /**
     * canRaiseNextTurn[i] is true if the player in seat i can
     * still raise her next turn this round (unless four bets 
     * are made before play reaches him in a limit game, she has no
     * money, or she has already folded).
     */
    public boolean[] canRaiseNextTurn;
    
    /**
     * Round bets is the number of bets made in this round
     * so far. This is to keep betting capped at 4.
     */
    public int roundBets;
    
    /** 
     * Round index incremented when the cards for that round are dealt. 
     * Preflop is 0, Flop is 1, Turn is 2, River is 3, Showdown is 4. */
    public int roundIndex;
    
    /** Cards in the hole */
    public Card[][] hole;
    
    /** Full board (may not have been revealed) */
    public Card[] board;

    /**
     * Map a player index to a seat index
     * @param player a player index
     * @return the respective seat index
     */
    public int playerToSeat(int playerIndex){
    	for(int i=0;i<player.length;i++){
    		if (player[i]==playerIndex){
    			return i;
    		}
    	}
    	return -1;
    }
    
    /**
     * Map a seat index to a player index
     * @param seat a seat index
     * @return the respective player index
     */
    public int seatToPlayer(int seat){
    	return player[seat];
    }
    
    /**
     * Gets the next seat, given the current one.
     * Loops around the "end" of the table.
     * @param seat
     * @return the next seat
     */
    public int getNextSeat(int seat){
        return (seat+1<numPlayers) ? (seat+1) : 0;
    }

    /**
     * Gets the next seat of an active player.
     * Note an active player may NOT be able to act this round.
     * @param seat
     * @return
     */
    public int getNextActiveSeat(int seat) {
		if (getNumActivePlayers()==0){
			throw new RuntimeException("No active players!!!");
		}
    	do {
			seat = getNextSeat(seat);
		} while (!active[seat]);
		return seat;
	}
    
	/**
	 * Returns the maximum in pot, or bigBlindSize
	 * if the maximum is below the big blind size.
	 * 
	 * This is because the "big blind", even if not
	 * completed, must be matched by other players, and
	 * that the next raise must start from the big blind (i.e.,
	 * a bet must leave the max in pot to 4 small blinds).
	 * @return the maximum amount in the pot in CHIPS
	 */
	public double getMaxInPot() {
		double maxSoFar = info.bigBlindSize;
		for (int i = 0; i < numPlayers; i++) {
			if (inPot[i] > maxSoFar) {
				maxSoFar = inPot[i];
			}
		}
		return maxSoFar;
	}
    
    /**
	 * Get the stack size of the player in a particular seat
	 * 
	 * @param seat the seat of the player
	 * @return the size of the stack in CHIPS
	 */
    public double getSeatStack(int seat){
    	return stack[seatToPlayer(seat)];
    }
    
    /**
     * Gets the amount required for a certain seat to
     * call. If it is a stack bound game, it is the minimum 
     * of the stack size of the player
     * and the difference between the maximum in the pot
     * versus the pot of that player. Otherwise, it is
     * just the difference.
     * @param seat
     * @return the amount in CHIPS required to call
     */
    public double getAmountToCall(int seat){
    	// CHIPS
    	double maxSoFar=getMaxInPot();
    	double normalAmountToCall = maxSoFar-inPot[seat];
    	if (info.stackBoundGame){
    		double stackBound = getSeatStack(seat);
    		if (stackBound<normalAmountToCall){
    			return stackBound;
    		}
    	}
    	return normalAmountToCall;
    }
    
    public boolean canActThisRound(int seat){
    	return active[seat] && (canRaiseNextTurn[seat] || (inPot[seat]<getMaxInPot()));
    }
    
    public int getNumPlayersLeftToAct(){
    	int initialSeat = getNextActiveSeat(0);
    	int seat=initialSeat;
    	int numCanAct = 0;
    	do{
    		if (canActThisRound(seat)){
    			numCanAct++;
    		}
    		seat = getNextActiveSeat(seat);
    	} while(seat!=initialSeat);
    	return numCanAct;
    }
    
    /**
     * Return the number of players that have not folded.
     * A player is active if she has not folded.
     * @return the number of players that has not folded
     */
    public int getNumActivePlayers(){
    	int result = 0;
    	for(boolean activeSeat:active){
    		if (activeSeat){
    			result++;
    		}
    	}
    	return result;
    }
    
}
