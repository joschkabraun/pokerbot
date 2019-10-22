package strategy.strategyPokerChallenge.interfacesToPokerChallenge;

import gameBasics.Player;
import gameBasics.PlayerYou;
import handHistory.HandHistory;
import handHistory.PlayerAction;
import handHistory.PlayerMoney;

import java.util.ArrayList;
import java.util.Arrays;

import cardBasics.Card;
import cardBasics.CardList;

import strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.MatchType;
import strategy.strategyPokerChallenge.data.GameState;
import strategy.strategyPokerChallenge.ringclient.ClientRingDynamics;

public class HHToTUDBotHistoryImproved {
	
	
	
	
	public static ClientRingDynamics createRingDynamics( HandHistory hh, PlayerYou py ) {
		ClientRingDynamics crd = new ClientRingDynamics();
		int numPlayers = hh.allPlayers.size();
		
		Player[] players = new Player[hh.allPlayers.size() ];
		for ( int i = 0; i < players.length; i++ )
			players[i] = hh.allPlayers.get(i);
		Player[] playersAr = players.clone();			// this is the array of players, like CONSTANT.PLAYER_COUNT
		Arrays.sort(playersAr, new ComparatorPlayer());
		
		ArrayList<Player> actPs = hh.getPlayerActive();
		boolean[] active = new boolean[numPlayers];
		for ( int i = 0; i < active.length; i++ )
			active[i] = false;
		for ( Player p : actPs )
			active[getIndex(playersAr, p.name)] = true;
		crd.active = active.clone();																					// rd.active
		
		if ( hh.bettingRounds.get(hh.bettingRounds.size()-1).getPokerChallengeGameState().equals(GameState.PRE_FLOP) )
			crd.board = new strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card[0];
		else
			crd.board = cardBasicsCardListToCaUAlbertaCardWith5(hh.getBoard());											// rd.board
		
		int numBetActRound = 0;
		for ( PlayerAction pa : hh.bettingRounds.get(hh.bettingRounds.size()-1).playerActionList )
			if ( pa.action.actionName.equals("bet") || pa.action.actionName.equals("raise") )
				++numBetActRound;
		boolean[] canRaiseNextRound = new boolean[numPlayers];
		if  ( numBetActRound == 4 )
			for ( int i = 0; i < canRaiseNextRound.length; i++ )
				canRaiseNextRound[i] = false;
		else
			canRaiseNextRound = active.clone();
		crd.canRaiseNextTurn = canRaiseNextRound.clone();															// rd.canRaiseNextTurn
		
		crd.hole = new strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card[numPlayers][2];
		for ( int i = 0; i < crd.hole.length; i++ )
			crd.hole[i] = new strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card[2];
		crd.hole[getIndex(playersAr, py.name)] = cardBasicsCardListToCaUAlbertaCard(hh.preFlop.startHand);			// rd.hole
		
		MatchType mt = new MatchType();
		mt.stackBoundGame = false;
		mt.bigBlindSize = hh.BB;
		crd.info = mt;																								// rd.info
		
		ArrayList<PlayerMoney> pmlInPot = hh.getInPot();
		double[] inPot = new double[numPlayers];
		for ( int i = 0; i < numPlayers; i++ )
			inPot[i] = pmlInPot.get( HandHistory.getIndex(pmlInPot, playersAr[i].name) ).money;
		crd.inPot = inPot.clone();																					// rd.inPot
		
		crd.numPlayers = numPlayers;																					// rd.numPlayers
		
		crd.player = new int[numPlayers];																			// rd.player
		for ( int i = 0; i < crd.player.length; i++ )
			crd.player[i] = hh.allPlayers.get(i).seatBehindBU.behindBU-1;
		
		crd.roundBets = numBetActRound;																				// rd.roundBets
		
		crd.roundIndex = hh.bettingRounds.get(hh.bettingRounds.size()-1).getPokerChallengeGameState().ordinal()-1;	// rd.roundIndex
		
		crd.seatTaken = getIndex(playersAr, py.name);																// rd.seatTaken
		
		crd.stack = new double[numPlayers];																			// rd.stack
		for ( int i = 0; i < crd.stack.length; i++ )
			crd.stack[i] = hh.allPlayers.get(i).money;
		
		return crd;
	}
	
//	private static int getIndex(Player[] players, Player p) {
//		for ( int i = 0; i < players.length; i++ )
//			if ( players[i].name.equals(p.name) )
//				return i;
//		return -1;
//	}
	
	/**
	 * Translates cardBasics.Card into strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.
	 * 
	 * @param c a cardBasics.Card
	 * @return cardBasics.Card as strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card
	 */
	private static strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card cardBasicsCardToCaUAlbertaCard( Card c ) {
		strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Rank r;
		strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Suit s;
		
		switch ( c.getRank().toInt() ) {
		case 2:
			r = strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Rank.TWO; break;
		case 3:
			r = strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Rank.THREE; break;
		case 4:
			r = strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Rank.FOUR; break;
		case 5:
			r = strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Rank.FIVE; break;
		case 6:
			r = strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Rank.SIX; break;
		case 7:
			r = strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Rank.SEVEN; break;
		case 8:
			r = strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Rank.EIGHT; break;
		case 9:
			r = strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Rank.NINE; break;
		case 10:
			r = strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Rank.TEN; break;
		case 11:
			r = strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Rank.JACK; break;
		case 12:
			r = strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Rank.QUEEN; break;
		case 13:
			r = strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Rank.KING; break;
		case 14:
			r = strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Rank.ACE; break;
		default:
			throw new IllegalStateException( "The commited cardBasics.Card is not correct initialized!" );
		}
		
		switch ( c.getSuit().toInt() ) {
		case 1:
			s = strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Suit.SPADES; break;
		case 2:
			s = strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Suit.HEARTS; break;
		case 3:
			s = strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Suit.DIAMONDS; break;
		case 4:
			s = strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Suit.CLUBS; break;
		default:
			throw new IllegalStateException( "The commited cardBasics.Card is not correct initialized!" );
		}
		
		return new strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card(r, s);
	}
	
	/**
	 * Translates cardBasics.CardList to an array of strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.
	 * 
	 * @param cl a cardBasics.CardList
	 * @return cardBasics.CardList as an array of strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card
	 */
	private static strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card[] cardBasicsCardListToCaUAlbertaCard( CardList cl ) {
		if ( cl.size() == 0 )
			return new strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card[0];
		strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card[] cards = new strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card[cl.size()];
		for ( int i = 0; i < cards.length; i++ )
			cards[i] = cardBasicsCardToCaUAlbertaCard(cl.get(i));
		return cards;
	}
	
	/**
	 * It is exactly as the method cardBasicsCardListToCaUAlbertaCard. Just the returned array has a size of five.
	 */
	private static strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card[] cardBasicsCardListToCaUAlbertaCardWith5( CardList cl ) {
		if ( cl.size() == 0 )
			return new strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card[5];
		strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card[] cards = new strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card[5];
		for ( int i = 0; i < cl.size(); i++ )
			cards[i] = cardBasicsCardToCaUAlbertaCard(cl.get(i));
		return cards;
	}
	
	private static int getIndex( Player[] ps, String pName ) {
		for ( int i = 0; i < ps.length; i++ )
			if ( ps[i].name.equals(pName) )
				return i;
		return -1;
	}
	
}
