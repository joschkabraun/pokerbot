package strategy.strategyPokerChallenge.ringclient;

import java.util.ArrayList;

import strategy.strategyPokerChallenge.ringclient.interfaces.StateChangeListener;
import strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card;
import strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.MatchType;
import strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.RingDynamics;
import strategy.strategyPokerChallenge.data.Action;
import strategy.strategyPokerChallenge.data.GameState;

/**
 *
 * Maintains a version of the server's state on the client side.
 * Keeps track of bankroll, seat, and the state of the match in a usable form.
 * @author Kami II
 */
public class ClientRingDynamics extends RingDynamics {

	/**
	 * the match state string parser
	 */
	private ClientRingStateParser parser;
	
	/**
	 * the list of listeners that are updated according to the changes in this class
	 */
	private ArrayList<StateChangeListener> listeners;
	
    /**
     * The seat taken by this player.
     */
    public int seatTaken;

    /**
     * the representation of the actual game state
     */
    public GameState gameState;
    
    /**
     * The bankroll at the beginning of the hand when (handOver==false): when (handOver==true),
     * it becomes the bankroll at the end of the hand.
     */
    public double bankroll;
    
    /**
     * the amount of players that are playing
     */
    public int playerCount;
    
    /**
     * should the state be changed?
     */
    private boolean changeState = false;
    
    /** 
     * Creates a new instance of ClientRingDynamics 
     */
	public ClientRingDynamics(int numPlayers, MatchType info, ClientRingStateParser parser) {
		super(numPlayers, info, new String[numPlayers]);
		this.playerCount = numPlayers;
		this.parser = parser;
		listeners = new ArrayList<StateChangeListener>();
		setGameState(GameState.STARTING);
	}
    
    /**
     * Returns true if it is our turn to act.
     */
    public synchronized boolean isOurTurn(){
        return (!handOver) && (seatTaken == seatToAct) && (active[seatTaken]);
    }
    
    /**
     * Initialize this from a match state message.
     */
    public synchronized void setFromMatchStateMessage(String message){
    	if (hole == null || gameState == GameState.SHOWDOWN || handOver)
    		setGameState(GameState.STARTING);
    	
    	changeState = false;
    	parser.parseMatchStateMessage(message);
        seatTaken = parser.seatToAct;
        handNumber = parser.handNumber;
        setCards();

        if (parser.lastAction != null)
        	actionPerformed(seatToAct, parser.lastAction);
        
        if (changeState)
        	setGameState(gameState.nextState());
        
        if (handOver){
            updateBankroll();
            setGameState(GameState.SHOWDOWN);
            roundFinished();
        }
    }
    
    /**
     * Initialize all the cards from the card sequence.
     * Unknown cards are null pointers.
     */
    public void setCards(){
        hole = parser.hole;
        board = new Card[5];
        for (int i = 0; i < 3; i++)
        	board[i] = parser.flop[i];
        board[3] = parser.turn;
        board[4] = parser.river;
        
        switch (gameState) {
        	case STARTING:	if (hole != null)		setGameState(gameState.nextState()); break;
        	case PREFLOP:	if (board[0] != null)	changeState = true;	break;
        	case FLOP:		if (board[3] != null)	changeState = true;	break;
        	case TURN:		if (board[4] != null)	changeState = true;	break;
        }
    }

    /**
     * Returns the betting sequence since the last cards observed.
     */
    public String getRoundBettingSequence(){
        int finalSlash = bettingSequence.lastIndexOf('/');
        if (finalSlash==-1){
            return bettingSequence;
        }
        return bettingSequence.substring(finalSlash+1);
    }
    
    /**
     * Update the bankroll: note that this is ONLY changed at the end of a hand.
     */
    public void updateBankroll(){
        bankroll += amountWon[seatTaken];
    }

    /**
     * called when a player performs an action
     * @param seat the seat the player sits in
     * @param action the action the player performs
     */
    private void actionPerformed(int seat, Action action) {
        if (action != null) {
            handleAction(action.toChar());
    		for (StateChangeListener lis: listeners)
    			lis.actionPerformed(seat, seatToPlayer(seat), action);
        }
    }

    /**
     * called when the game state changes
     * @param newState the new game state
     */
    private void setGameState(GameState newState) {
        gameState = newState;
        if (gameState == GameState.STARTING) {
        	if (hole!=null){
           		setHandNumber(handNumber +1);
           		nextSeats();
        	}
            initializeBets();
        }
		for (StateChangeListener lis: listeners)
			lis.stateChanged(gameState);
    }

    /**
     * called when a round is finished
     */
    private void roundFinished() {
		for (StateChangeListener lis: listeners)
			lis.roundFinished(seatToPlayer(seatTaken), seatToPlayer(0), amountWon, inPot, hole);
    }

	/**
	 * adds a listener to the listener list
	 * @param listener the listener to be added to the list
	 * @return true if the listener was added to the list, else false
	 */
	public boolean addStateChangeListener(StateChangeListener listener) {
		return listeners.add(listener);
	}
	
	/**
	 * removes a certain listener from the listener list
	 * @param listener the listener to be removed from the list
	 * @return true if the listener was removed from the list, else false
	 */
	public boolean removeStateChangeListener(StateChangeListener listener) {
		return listeners.remove(listener);
	}

	/**
	 * @return the parser
	 */
	public ClientRingStateParser getParser() {
		return parser;
	}

	/**
	 * sets a new parser
	 * @param parser the new parser
	 */
	public void setParser(ClientRingStateParser parser) {
		this.parser = parser;
	}
}
