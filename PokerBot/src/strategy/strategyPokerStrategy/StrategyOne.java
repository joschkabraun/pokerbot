package strategy.strategyPokerStrategy;

import cardBasics.CardList;
import cardBasics.CardsStack;
import gameBasics.Action;
import gameBasics.PlayerYou;
import handHistory.HandHistory;
import handHistory.PreFlop;

/**
 * This class implements the poker strategy of www.PokerStrategy.com for texas hold'em fixed limit.
 * 
 * @author Joschka
 * @version 1.0
 */
public class StrategyOne

{
	
	public static Action actionFor( HandHistory hh, PlayerYou you )
	{
		switch( hh.state )
		{
		case PRE_FLOP:
			return preFlopAction(hh, you);
		case FLOP:
			return flopAction(hh, you);
		case TURN:
			return turnAction(hh, you);
		case RIVER:
			return riverAction(hh, you);
		default: 
			throw new IllegalArgumentException( "The passed hand history is not correct because there is not any GameState!" );
		}
	}
	
	public static Action preFlopAction( HandHistory handHistory, PlayerYou you )	// The algorithm is documented in a article of www.pokerStrategy.com and
	{																				// the implementation in an handwritten paper to the PokerBot (on page (PB)25 ).
		Action retAct = new Action( "fold" );
		CardList cards = new CardList( you.startCards );
		cards.sortByCardValue();
		int value = 100 * cards.get(1).valueInt + cards.get(0).valueInt;
		
		boolean isOneColour = cards.isOneColour();
		
		if ( handHistory.preFlop.howOftenHaveYouPlayed( you ) == 0 )				// if you have just one time played
		{
			if ( (value == 1414) || (value == 1313) || (value == 1212) || (value == 1413) )		// very strong cards
				retAct.set( "raise" );
			else																	// The consequence if your cards are not very strong cards and there was more than on raise.
				if ( handHistory.preFlop.howOftenRaised() > 1 )
					retAct.set( "fold" );
			
			if ( (value == 1111) || (value == 1010) || (value == 909) || (value == 1412) || ((value == 1411) && isOneColour) )			// strong cards
			{
				if ( handHistory.preFlop.howOftenRaised() == 1 )
				{
					if ( handHistory.preFlop.howOftenCalledAfterLastRaise() == 0 )
					{
						if ( you.seatBehindBU.positionRange.equals( "earlyPosition" ) )
							retAct.set( "fold" );
						else
							retAct.set( "raise" );
					}
					else
						retAct.set( "call" );
				}
				else
					retAct.set( "raise" );
			}
			
			else if ( ((value == 1411) && (!isOneColour)) || (value == 1410) || (value == 1312) )					// middle strong cards
			{
				if ( handHistory.preFlop.howOftenRaised() == 1 )
				{
					if ( handHistory.preFlop.howOftenCalledAfterLastRaise() == 1 )
					{
						if ( you.seatBehindBU.positionRange.equals( "bigBlind" ) || ((value == 1411) && (!isOneColour)) )
							retAct.set( "call" );
						else
							retAct.set( "fold" );
					}
					else
					{
						if ( you.seatBehindBU.positionRange.equals( "bigBlind" ) )
							retAct.set( "call" );
						else
							retAct.set( "fold" );
					}
				}
				else
				{
					if ( you.seatBehindBU.positionRange.equals( "earlyPosition" ) )
						retAct.set( "fold" );
					else
						retAct.set( "raise" );
				}
			}
			
			else if ( (value == 808) || (value == 707) || (value == 606) || (value == 505) || (value == 404) || (value == 303) || (value == 202) ||	// strong speculative cards
					(((value == 1311) || (value == 1310) || (value == 1211) || (value == 1210) || (value == 1110) || (value == 1009)) && isOneColour) )
			{
				if ( handHistory.preFlop.howOftenRaised() == 1 )
				{
					if ( handHistory.preFlop.howOftenCalledAfterLastRaise() > 0 )
						retAct.set( "call" );
					else
					{
						if ( you.seatBehindBU.positionRange.equals( "bigBlind" ) )
							retAct.set( "call" );
						else
							retAct.set( "fold" );
					}
				}
				else
				{
					if ( handHistory.preFlop.howManyPlayersInGame(handHistory.bigBlindP.size()) > 1 )
					{
						if ( you.seatBehindBU.positionRange.equals("bigBlind") )
							retAct.set( "check" );
						else
							retAct.set( "call" );
					}
					else if ( you.seatBehindBU.positionRange.equals( "earlyPosition" ) || you.seatBehindBU.positionRange.equals( "middlePosition" ) )
						retAct.set( "fold" );
					else if ( handHistory.preFlop.howManyPlayersInGame(handHistory.bigBlindP.size()) == 0 )
					{
						if ( you.seatBehindBU.positionRange.equals( "bigBlind" ) )
							retAct.set( "check" );
						else
							retAct.set( "call" );
					}
					else
						retAct.set( "raise" );
				}
			}
			
			else if ( (((value == 1311) || (value == 1310) || (value == 1211) || (value == 1119)) && (! isOneColour)) ||				// mixed cards
					(((value == 1409) || (value == 1408) || (value == 1407) || (value == 1406) || (value == 1405) || (value == 1404) || (value == 1403) || (value == 1402) ||
							(value == 1309) || (value == 807) || (value == 908)) && isOneColour) )
			{
				if ( handHistory.preFlop.howOftenRaised() == 1 )
					retAct.set( "fold" );
				else if ( you.seatBehindBU.positionRange.equals( "earlyPosition" ) || you.seatBehindBU.positionRange.equals( "middlePosition" ) )
					retAct.set( "fold" );
				else if ( handHistory.preFlop.haveAllFoldedBefore() )
					retAct.set( "raise" );
				else if ( you.seatBehindBU.positionRange.equals( "bigBlind" ) )
					retAct.set( "check" );
				else if ( handHistory.preFlop.howManyPlayersInGame(handHistory.bigBlindP.size()) > 1 )
					retAct.set( "check" );
				else if ( handHistory.preFlop.howManyPlayersInGame(handHistory.bigBlindP.size()) == 1 )
				{
					if ( you.seatBehindBU.positionRange.equals( "latePosition" ) )
						retAct.set( "fold" );
					else
						retAct.set( "call" );
				}
			}
		}
		
		else if ( handHistory.preFlop.howOftenHaveYouPlayed(you) > 0 )								// if you have more than one time played in this game in the pre-flop-phase
		{
			if ( (value == 1414) || (value == 1313) || (value == 1212) || (value == 1413) )
				retAct.set( "raise" );
			else if ( handHistory.preFlop.howOftenRaisedAfterFirstPlay(you) == 1 )
					retAct.set( "call" );
			else
			{
				if ( (value == 1111) || (value == 1010) || (value == 909) || (value == 1412) || ((value == 1411) && isOneColour) )
					retAct.set( "call" );
				else
					retAct.set( "fold" );
			}
		}
		
		else													// if your cards were not in the starting-hands-chart
		{
			if ( you.seatBehindBU.positionRange.equals( "smallBlind" ) ) {
				if ( handHistory.preFlop.howOftenRaised() == 0 )
					retAct.set( "call" );
			} else if ( you.seatBehindBU.positionRange.equals( "bigBlind" ) ) {
				if ( handHistory.preFlop.howOftenRaised() == 1 )
					retAct.set( "call" );
			} else
				retAct.set( "fold" );
		}
		
		if ( retAct.actionName.equals( "fold" ) && PreFlop.actionMin(handHistory, you).actionName.equals( "check" ) )
			retAct.set( "check" );
		else if ( retAct.actionName.equals("call") && handHistory.preFlop.playerActionList.actionMin().actionName.equals("check") )
			retAct.set( "check" );
//		else if ( retAct.actionName.equals("call") && handHistory.preFlop.playerActionList.howOftenBettedRaised() == 4 )
//			retAct.set( "raise" );			// you prove the action with raise thereby the poker-bot clicks on the correct button (the button for calling is after the fourth
											// raise on the Bet-/Raise-Button
		
		return retAct;
	}
	
	public static Action flopAction( HandHistory handHistory, PlayerYou you )
	{
		Action retAct = new Action("fold");
		CardList yourCards = new CardList( you.startCards );
		CardList board = new CardList( handHistory.flop.board );
		CardList cards = new CardList( yourCards, board );
		
		if ( cards.hasRoyalFlush() || cards.hasStraightFlush() || cards.hasQuadruplet() || cards.hasFullHouse() || cards.hasFlush() || cards.hasStraight() ||
				cards.hasTriple() || cards.hasTwoPair() || CardList.isMonsterDraw(yourCards, board) )					// made hands and monsterdraw
		{
			if ( handHistory.flop.playerActionList.howOftenBettedRaised() == 0 )
				retAct.set( "bet" );
			else
				retAct.set( "raise" );
		}
		else if ( CardList.isFlushDraw(yourCards, board) || CardList.isOESD(yourCards, board) || CardList.isDoubleBellyBuster(yourCards, board) ||		// draws
				CardList.isOverCardsAndBellyBuster(yourCards, board) || CardList.isTopPair(yourCards, board) || CardList.isOverPair(yourCards, board) )
		{
				if ( handHistory.flop.playerActionList.howOftenHaveYouPlayed(you) == 0 ) {
					if ( handHistory.flop.playerActionList.howOftenBettedRaised() > 0 )
						retAct.set( "call" );
					else
						retAct.set( "bet" );
				}
				else
					retAct.set( "call" );
		}
		else if ( CardList.isBellyBuster(yourCards, board) || CardList.isOverCards(yourCards, board) )		// belly buster and overcards
		{
			if ( handHistory.flop.playerActionList.howOftenHaveYouPlayed(you) == 0 )
			{
				if ( handHistory.flop.playerActionList.howOftenBettedRaised() == 0 ) {
					if ( handHistory.howManyPlayerInGame < 4 )
						if ( handHistory.preFlop.playerActionList.haveRaised(you) )
							retAct.set( "bet" );
				} else if ( handHistory.flop.playerActionList.howOftenBettedRaised() == 1 ) {
					if ( handHistory.pot.money >= 10 * handHistory.BB )
						retAct.set( "call" );
				} else
					retAct.set( "fold" );
			}
			else if ( handHistory.flop.playerActionList.howOftenHaveYouPlayed(you) == 1 )
			{
				if ( handHistory.flop.playerActionList.howOftenBettedRaised() < 3 ) {
					if ( handHistory.pot.money >= 10 * handHistory.BB )
						retAct.set( "call" );
				} else
					retAct.set( "fold" );
			}
			else
				retAct.set( "fold" );
		}
		else			// trashy hands
		{
			if ( handHistory.flop.playerActionList.howOftenBettedRaised() == 0 ) {
				if ( handHistory.howManyPlayerInGame < 4 )
					if ( handHistory.preFlop.playerActionList.haveRaised(you) )
						retAct.set( "bet" );
			} else
				retAct.set( "fold" );
		}
		
		if ( retAct.actionName.equals( "fold" ) && handHistory.flop.playerActionList.actionMin().actionName.equals( "check" ) )
			retAct.set( "check" );
		else if ( retAct.actionName.equals("call") && handHistory.flop.playerActionList.actionMin().actionName.equals("check") )
			retAct.set( "check" );
//		else if ( retAct.actionName.equals("call") && handHistory.flop.playerActionList.howOftenBettedRaised() == 4 )
//			retAct.set( "raise" );			// you prove the action with raise thereby the poker-bot clicks on the correct button (the button for calling is after the fourth
											// raise on the Bet-/Raise-Button
		
		return retAct;
	}
	
	public static Action turnAction( HandHistory handHistory, PlayerYou you )
	{
		Action retAct = new Action( "fold" );
		CardList yourCards = new CardList( you.startCards );
		CardList board = new CardList( handHistory.turn.board );
		CardList cards = new CardList( yourCards, board );
		
		if ( handHistory.turn.playerActionList.howOftenHaveYouPlayed(you) == 0 )
		{
			if ( handHistory.turn.playerActionList.howOftenBettedRaised() == 0 )
			{
				if ( handHistory.flop.playerActionList.wasLastRaiser(you) )
				{
					if ( cards.hasRoyalFlush() || cards.hasStraightFlush() || cards.hasQuadruplet() || cards.hasFullHouse() || cards.hasFlush() || cards.hasStraight() ||
							cards.hasTriple() || cards.hasTwoPair() || CardList.isTopPair(yourCards, board) || CardList.isOverPair(yourCards, board) )		// made hands
						retAct.set( "bet" );
					else if ( CardList.isMonsterDraw(yourCards, board) || CardList.isFlushDraw(yourCards, board) || CardList.isOESD(yourCards, board) ||
							CardList.isDoubleBellyBuster(yourCards, board) || CardList.isBellyBuster(yourCards, board) )									// draws
						if ( handHistory.howManyPlayerInGame < 4 )
							retAct.set( "bet" );
						else
							retAct.set( "check" );
					else
						retAct.set( "check" );
				}
				else
				{
					if ( cards.hasRoyalFlush() || cards.hasStraightFlush() || cards.hasQuadruplet() || cards.hasFullHouse() || cards.hasFlush() || cards.hasStraight() ||
							cards.hasTriple() || cards.hasTwoPair() )					// made hands
						retAct.set( "bet" );
					else if ( CardList.isMonsterDraw(yourCards, board) || CardList.isFlushDraw(yourCards, board) || CardList.isOESD(yourCards, board) ||
							CardList.isDoubleBellyBuster(yourCards, board) || CardList.isTopPair(yourCards, board) || CardList.isOverPair(yourCards, board) )	// draws
						retAct.set( "call" );
					else if ( CardList.isBellyBuster(yourCards, board) )		// belly buster
						if ( handHistory.pot.money >= 10 * handHistory.BB )
							retAct.set( "call" );
						else
							retAct.set( "fold" );
					else
						retAct.set( "fold" );
				}
			}
			else																		// someone has already sat
				{
				if ( handHistory.turn.playerActionList.howOftenBettedRaised() == 1 )
				{
					if ( cards.hasRoyalFlush() || cards.hasStraightFlush() || cards.hasQuadruplet() || cards.hasFullHouse() || cards.hasFlush() || cards.hasStraight() ||
							cards.hasTriple() || cards.hasTwoPair() )					// made hands
						retAct.set( "raise" );
					else if ( CardList.isMonsterDraw(yourCards, board) || CardList.isFlushDraw(yourCards, board) || CardList.isOESD(yourCards, board) ||
							CardList.isDoubleBellyBuster(yourCards, board) || CardList.isTopPair(yourCards, board) || CardList.isOverPair(yourCards, board) )	// draws
						retAct.set( "call" );
					else if ( CardList.isBellyBuster(yourCards, board) )		// belly buster
						if ( handHistory.pot.money >= 10 * handHistory.BB )
							retAct.set( "call" );
						else
							retAct.set( "fold" );
					else
						retAct.set( "fold" );
				}
				else
				{
					CardsStack stack = new CardsStack();
					stack.remove( you.startCards );
					if ( cards.hasRoyalFlush() || cards.hasStraightFlush() || cards.hasQuadruplet() || cards.hasFullHouse() || cards.hasFlush() || cards.hasStraight() ||
							cards.hasTriple() || cards.hasTwoPair() )					// made hands
						if ( CardList.bestCardCombination(board, stack).equals( cards.whichCardCombinationIsThis() ) )
							retAct.set( "raise" );						// best hand
						else
							retAct.set( "call" );
					else
						retAct.set( "fold" );
				}
			}			
		}
		else										// implementation of the chapter "wenn es hinter dir Action gibt ..."
		{
			int howManyBets = handHistory.turn.playerActionList.howOftenRaisedAfterFirstPlay(you);
			
			if ( howManyBets == 1 )
			{
				if ( cards.hasRoyalFlush() || cards.hasStraightFlush() || cards.hasQuadruplet() || cards.hasFullHouse() || cards.hasFlush() || cards.hasStraight() ||
						cards.hasTriple() || cards.hasTwoPair() )					// made hands
					retAct.set( "raise" );
				else if ( CardList.isMonsterDraw(yourCards, board) || CardList.isFlushDraw(yourCards, board) || CardList.isOESD(yourCards, board) ||
						CardList.isDoubleBellyBuster(yourCards, board) || CardList.isTopPair(yourCards, board) || CardList.isOverPair(yourCards, board) )	// draws
					retAct.set( "call" );
				else if ( CardList.isBellyBuster(yourCards, board) )
					if ( handHistory.pot.money >= 10 * handHistory.BB )
						retAct.set( "call" );
					else
						retAct.set( "fold" );
				else
					retAct.set( "fold" );
			}
			else			// if there were more than one raise after you play
			{
				CardsStack stack = new CardsStack();
				stack.remove( you.startCards );
				if ( cards.hasRoyalFlush() || cards.hasStraightFlush() || cards.hasQuadruplet() || cards.hasFullHouse() || cards.hasFlush() || cards.hasStraight() ||
						cards.hasTriple() || cards.hasTwoPair() )					// made hands
					if ( CardList.bestCardCombination(board, stack).equals( cards.whichCardCombinationIsThis() ) )
						retAct.set( "raise" );
					else
						retAct.set( "call" );
				
				else if ( CardList.isMonsterDraw(yourCards, board) || CardList.isFlushDraw(yourCards, board) )
					retAct.set( "call" );
				else if ( (! board.isFlushPossible()) && (CardList.isOESD(yourCards, board) || CardList.isDoubleBellyBuster(yourCards, board)) )
					retAct.set( "call" );
			}
		}
		
		if ( retAct.actionName.equals("fold") && handHistory.turn.playerActionList.actionMin().actionName.equals("check") )
			retAct.set( "check" );
		else if ( retAct.actionName.equals("call") && handHistory.turn.playerActionList.actionMin().actionName.equals("check") )
			retAct.set( "check" );
//		else if ( retAct.actionName.equals("call") && handHistory.turn.playerActionList.howOftenBettedRaised() == 4 )
//			retAct.set( "raise" );			// you prove the action with raise thereby the poker-bot clicks on the correct button (the button for calling is after the fourth
											// raise on the Bet-/Raise-Button
		
		return retAct;
	}
	
	public static Action riverAction( HandHistory handHistory, PlayerYou you )
	{
		Action retAct = new Action( "fold" );
		CardList yourCards = new CardList( you.startCards );
		CardList board = new CardList( handHistory.turn.board );
		CardList cards = new CardList( yourCards, board );
		CardList boardTurn = new CardList( handHistory.turn.board );
		
		if ( handHistory.turn.playerActionList.wasLastRaiser(you) )
		{
			CardsStack stack = new CardsStack();
			stack.remove( you.startCards );
			
			if ( handHistory.river.playerActionList.howOftenHaveYouPlayed(you) == 0 )
			{				
				if ( handHistory.river.playerActionList.howOftenBettedRaised() == 0 )
					if ( cards.hasRoyalFlush() || cards.hasStraightFlush() || cards.hasQuadruplet() || cards.hasFullHouse() || cards.hasFlush() || cards.hasStraight() ||
							cards.hasTriple() || cards.hasTwoPair() || CardList.isTopPair(yourCards, board) || CardList.isOverPair(yourCards, board) )		// made hands
						retAct.set( "bet" );
					else
						retAct.set( "check" );
				
				else
					if ( cards.hasRoyalFlush() || cards.hasStraightFlush() || cards.hasQuadruplet() || cards.hasFullHouse() || cards.hasFlush() || cards.hasStraight() ||
							cards.hasTriple() || cards.hasTwoPair() )
						if ( CardList.bestCardCombination(board, stack).equals( cards.whichCardCombinationIsThis() ) )
							retAct.set( "raise" );
						else
							retAct.set( "call" );
					else
						retAct.set( "fold" );
			}
			else
				if ( cards.hasRoyalFlush() || cards.hasStraightFlush() || cards.hasQuadruplet() || cards.hasFullHouse() || cards.hasFlush() || cards.hasStraight() ||
						cards.hasTriple() || cards.hasTwoPair() )
					if ( CardList.bestCardCombination(board, stack).equals( cards.whichCardCombinationIsThis() ) )
						retAct.set( "raise" );
					else
						retAct.set( "call" );
				else
					retAct.set( "fold" );
		}
		
		else
		{
			if ( handHistory.river.playerActionList.howOftenHaveYouPlayed(you) == 0 )
			{
				if ( handHistory.river.playerActionList.howOftenBettedRaised() == 0 )
				{
					if ( cards.hasRoyalFlush() || cards.hasStraightFlush() || cards.hasQuadruplet() || cards.hasFullHouse() || cards.hasFlush() || cards.hasStraight() ||
							cards.hasTriple() || cards.hasTwoPair() )
						retAct.set( "bet" );
					else if ( CardList.isTopPair(yourCards, new CardList(handHistory.turn.turn)) )
						retAct.set( "bet" );
					else
						retAct.set( "check" );
				}
				else
				{
					CardsStack stack = new CardsStack();
					stack.remove( you.startCards );
					if ( cards.hasRoyalFlush() || cards.hasStraightFlush() || cards.hasQuadruplet() || cards.hasFullHouse() || cards.hasFlush() || cards.hasStraight() ||
							cards.hasTriple() || cards.hasTwoPair() )					// made hands
						if ( CardList.bestCardCombination(board, stack).equals( cards.whichCardCombinationIsThis() ) )
							retAct.set( "raise" );
						else
							retAct.set( "call" );
					
					else if ( handHistory.river.playerActionList.howOftenBettedRaised() == 1 )
					{
						if ( CardList.isTopPair(yourCards, new CardList(handHistory.turn.turn)) || CardList.isTopPair(yourCards, handHistory.flop.board) ||
								CardList.isOverPair(yourCards, boardTurn) || CardList.isOverPair(yourCards, handHistory.flop.board) )
							retAct.set( "call" );
						else
							retAct.set( "fold" );
					}
					else
						retAct.set( "fold" );
				}
			}
			else
			{
				CardsStack stack = new CardsStack();
				stack.remove( you.startCards );
				if ( cards.hasRoyalFlush() || cards.hasStraightFlush() || cards.hasQuadruplet() || cards.hasFullHouse() || cards.hasFlush() || cards.hasStraight() ||
						cards.hasTriple() || cards.hasTwoPair() )					// made hands
					if ( CardList.bestCardCombination(board, stack).equals( cards.whichCardCombinationIsThis() ) )
						retAct.set( "raise" );
					else
						retAct.set( "call" );
				
				if ( handHistory.river.playerActionList.howOftenRaisedAfterFirstPlay(you) == 1 )
					if ( CardList.isTopPair(yourCards, new CardList(handHistory.turn.turn)) )
						retAct.set( "call" );
					else
						retAct.set( "fold" );
				else
					retAct.set( "fold" );
			}
		}
		
		if ( retAct.actionName.equals("fold") && handHistory.river.playerActionList.actionMin().actionName.equals("check") )
			retAct.set( "check" );
		else if ( retAct.actionName.equals("call") && handHistory.river.playerActionList.actionMin().actionName.equals("check") )
			retAct.set( "check" );
//		else if ( retAct.actionName.equals("call") && handHistory.river.playerActionList.howOftenBettedRaised() == 4 )
//			retAct.set( "raise" );			// you prove the action with raise thereby the poker-bot clicks on the correct button (the button for calling is after the fourth
											// raise on the Bet-/Raise-Button
		
		return retAct;
	}
	
}
