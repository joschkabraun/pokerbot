package strategy.strategyPokerChallenge.interfacesToPokerChallenge;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import cardBasics.Card;
import cardBasics.CardList;

import other.Tools;
import parser.ParserCreatorWinnerPoker1Table;
import parser.ParserCreatorWinnerPoker4Tables;

import bots.Bot_v1_1_0Tables;
import strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.MatchType;
import strategy.strategyPokerChallenge.data.Bucket;
import strategy.strategyPokerChallenge.data.CONSTANT;
import strategy.strategyPokerChallenge.data.GameState;
import strategy.strategyPokerChallenge.data.Action;
import strategy.strategyPokerChallenge.data.SimulationGuide;
import strategy.strategyPokerChallenge.history.*;
import strategy.strategyPokerChallenge.ringclient.ClientRingDynamics;
import gameBasics.Player;
import gameBasics.PlayerYou;
import handHistory.BettingRound;
import handHistory.HandHistory;
import handHistory.HandHistory.GameType;
import handHistory.HandHistory.Limit;
import handHistory.PlayerAction;
import handHistory.PlayerHandCombination;
import handHistory.PlayerMoney;

/**
 * This class implements the necessary tools for creating the datastructures for using the
 * TU Darmstadts AKI-RealBot Monte-Carlo-Search.
 * 
 * HHToTUDBotHistory = Hand History to TU Darmstadts Bot History
 * 
 * You can say that this class is the interface from the original bot to the TUDarmstadt's AKI-RealBot
 * 
 * ATTENTION this class implemented for the case if you know every player.
 * 
 * @author Joschka
 */
public class HHToTUDBotHistory {
	
	/**
	 * An array of all hand histories. The last entry is the last hand history.
	 */
	private static HandHistory[] hands;
	
	/**
	 * This method creates the private static attribute of this class "hands".
	 * 
	 * For parameters and exceptions please look look for parser.ParserCreatorWinnerPoker4Tables.parserCWP(...).
	 */
	public static void createPrivateHands( File source, File parserToTUDBotData, Bot_v1_1_0Tables table, GameType gameType, Limit limit, int maxSeatOnTable, String playYouName,
			BufferedImage[] pictureSeats, Rectangle[] spaceSeats ) throws IOException, AWTException {
		HHToTUDBotHistory.hands = null;
		HHToTUDBotHistory.hands = getHands(source, parserToTUDBotData, table, gameType, limit, maxSeatOnTable, playYouName, pictureSeats, spaceSeats);
	}
	
	/**
	 * This method creates the private static attribute of this class "hands" in which can be hand histories of several tables.
	 * For more informations look at the other "createPrivateHands"-method.
	 */
	public static void createPrivateHands( File[] source, File[] parserToTUDBotData, Bot_v1_1_0Tables table[], GameType gameType[], Limit limit[], int maxSeatOnTable[], String playYouName,
			BufferedImage[][] pictureSeats, Rectangle[][] spaceSeats ) throws IOException, AWTException {
		HHToTUDBotHistory.hands = null;
		
		if ( source.length != parserToTUDBotData.length || parserToTUDBotData.length != table.length || table.length != gameType.length || gameType.length != limit.length ||
				limit.length != maxSeatOnTable.length || maxSeatOnTable.length != pictureSeats.length || pictureSeats.length != spaceSeats.length )
			throw new IllegalArgumentException("The passed arguments can not be correct because they do not have the same dimension!");
		
		ArrayList<ArrayList<HandHistory>> hhs = new ArrayList<ArrayList<HandHistory>>();
		HandHistory[] handsA;
		ArrayList<HandHistory> handsAL = new ArrayList<HandHistory>();
		HandHistory[] test;
		int length = 0;
		ArrayList<Integer> lengths = new ArrayList<Integer>();
		for ( int i = 0; i < source.length; i++ ) {
			if ( other.Tools.allLines(source[i]).length == 0 )
				continue;
			test = getHands(source[i], parserToTUDBotData[i],table[i], gameType[i], limit[i], maxSeatOnTable[i], playYouName, pictureSeats[i], spaceSeats[i]);
			if ( test.length==0 )
				continue;
			handsA = test;
			for ( HandHistory hh : handsA )
				handsAL.add(hh);
			hhs.add(handsAL);
			handsAL = new ArrayList<HandHistory>();
			length += handsA.length;
			lengths.add(length);
			test = null;
		}
		
		HHToTUDBotHistory.hands = new HandHistory[length];
		for ( int i = 0; i < hhs.size(); i++ )
			for ( int j = 0; j < hhs.get(i).size(); j++ )
				if ( i == 0 )
					HHToTUDBotHistory.hands[j] = hhs.get(i).get(j);
				else
					HHToTUDBotHistory.hands[j+lengths.get(i-1)] = hhs.get(i).get(j);
	}
	
	/**
	 * Returns an array of all hand histories. The last entry is the last hand history.
	 * 
	 * For parameters and exceptions please look look for parser.ParserCreatorWinnerPoker4Tables.parserCWP(...).
	 */
	public static HandHistory[] getHands( File source, File parserToTUDBotData, Bot_v1_1_0Tables table, GameType gameType, Limit limit, int maxSeatOnTable, String playYouName,
			BufferedImage[] pictureSeats, Rectangle[] spaceSeats ) throws IOException, AWTException {
		HandHistory[] hands;
		
		String[] allLinesWithoutTrim = Tools.allLines( source );
		int length = allLinesWithoutTrim.length;
		if ( length==0 )
			return new HandHistory[0];
		String[] allLines = new String[ length ];
		for ( int i = 0; i < length; i++ )
			allLines[ i ] = allLinesWithoutTrim[ i ].trim();
		
		int counter = 0;
		ArrayList<Integer> indizes = new ArrayList<Integer>();
		for ( int i = 0; i < allLines.length; i++ )
			if ( allLines[i].matches("Geber: .+ ist der Geber") ) {
				++counter;
				indizes.add(i);
			}
		indizes.add(allLines.length);
		
		hands = new HandHistory[ counter ];
		
		int j = indizes.get(0);
		for ( int i = 0; i < counter; i++ ) {
			FileWriter heapW = new FileWriter( parserToTUDBotData );
			for ( int k = j; k < indizes.get(i+1); k++ )
				heapW.write(String.format(allLines[k] + "%n"));
			heapW.flush();
			heapW.close();
			
			hands[i] = ParserCreatorWinnerPoker4Tables.parserCWP(parserToTUDBotData, gameType, limit, maxSeatOnTable, playYouName, pictureSeats, spaceSeats);
			
			j = indizes.get(i+1);
		}
		return hands;
	}
	
	private HHToTUDBotHistory() {
	}
	
	/**
	 * This method creates the History for the actual game situation.
	 * 
	 * @param hhAct the actual hand history
	 * @param playYou the Player you are
	 */
	public static void createHistory( HandHistory hhAct, PlayerYou playYou ) {
		if ( HHToTUDBotHistory.hands == null || HHToTUDBotHistory.hands.length == 0 )
			throw new HHToTUDBotHistoryException("The history was not possible to determine the history because HHToTUDBot.hands is empty!");
		
		History.setHistory( null );
		History history = History.getHistory();
		GlobalRoundData glob = new GlobalRoundData();
		
		Player[] players = new Player[hhAct.allPlayers.size() ];
		for ( int i = 0; i < players.length; i++ )
			players[i] = hhAct.allPlayers.get(i);
		HandHistory[] nHH = theNecessaryHH(players, HHToTUDBotHistory.hands);
		Player[] playersAr = players.clone();			// this is the array of players, as CONSTANT.PLAYER_COUNT
		Arrays.sort(playersAr, new ComparatorPlayer());
		
														// it was assumed the seat diversification is 0 = small blind, 1 = big blind and so on ...
		
		double[][][] playerRatio = new double[CONSTANT.PLAYER_COUNT][GameState.values().length][Action.values().length];
		int[][][][] playerBuckets = new int[CONSTANT.PLAYER_COUNT][GameState.values().length][Bucket.BUCKET_COUNT][Action.values().length];
		double[][][][] playerEstimate = new double[CONSTANT.PLAYER_COUNT][GameState.values().length][Bucket.BUCKET_COUNT][Action.values().length];
		double[][] bettingRatio = new double[CONSTANT.PLAYER_COUNT][GameState.values().length];
		int[][] bettingAddCount = new int[CONSTANT.PLAYER_COUNT][GameState.values().length];
		int[][] playerRoundCount = new int[CONSTANT.PLAYER_COUNT][GameState.values().length];
		double[] playerGrab = new double[CONSTANT.PLAYER_COUNT];
		
		
		int[][][] playerRatio2 = new int[CONSTANT.PLAYER_COUNT][GameState.values().length][Action.values().length+1];		// the last dim is one bigger for the
																															// amount how often the player has been in this state
																															// this array counts how often "fold", "call", "raise"
		
		for ( HandHistory hh : nHH ) {
			
			// numbBetsPerState and playerOutBettingRounds
			int[] playerOutBettingRounds = new int[CONSTANT.PLAYER_COUNT];
			int[] numBetsPerState = new int[GameState.values().length];
			for ( BettingRound br : hh.bettingRounds )
				for ( PlayerAction pa : br.playerActionList )
					if ( getIndex(playersAr, pa.player) == -1 )
						continue;
					else if ( pa.action.actionName.equals("fold") )
						playerOutBettingRounds[getIndex(playersAr, pa.player)] =numBetsPerState[br.getPokerChallengeGameState().ordinal()];
					else if ( pa.action.actionName.equals("bet") || pa.action.actionName.equals("raise") )
						numBetsPerState[br.getPokerChallengeGameState().ordinal()]++;
			
			
			// playerGrab
			PlayerMoney[] pma = hh.getAmountWonByPlayersArray();
			Arrays.sort(pma, new ComparatorPlayerMoney());
			ArrayList<Player> winningPlayers = new ArrayList<Player>();
			boolean weAreWinning = false;
			double[] playerGrabRound = new double[players.length];
			
			// evaluate the players that has win in the current round
			for ( PlayerMoney pm : pma )
				if ( pm.money > 0 ) {
					if ( pm.player.name.equals(playYou.name) )
						weAreWinning = true;
					winningPlayers.add(pm.player);
				}
			
			// use this to compute the amount win (lost) from every player
			if ( weAreWinning ) {
				for ( int i = 0; i < pma.length; i++ ) {
					if ( getIndex(players, pma[i].player) == -1 )
						continue;
					if ( pma[i].money < 0 )
						playerGrabRound[getIndex(players, pma[i].player)] = (pma[i].money * -1.0 / (double) winningPlayers.size());
				}
			} else
				for ( int i = 0; i < pma.length; i++ ) {
					if ( getIndex(players, pma[i].player) == -1 )
						continue;
					playerGrabRound[getIndex(players, pma[i].player)] = pma[i].money / (double) winningPlayers.size();
				}
			
			// add the computed values to global list
			for ( int i = 0; i < pma.length; i++ ) {
				if ( getIndex(players, pma[i].player) == -1 )
					continue;
				playerGrab[getIndex(players, pma[i].player)] += playerGrabRound[getIndex(players, pma[i].player)];
			}
			
			
			
			
			for ( BettingRound br : hh.bettingRounds )					// creating the playerRatio
				for ( PlayerAction pa : br.playerActionList ) {
					int playerIndex = getIndex(playersAr, pa.player);
					if ( playerIndex == -1 )
						continue;
					if ( pa.action.actionName.equals("fold") ) {
						++playerRatio[playerIndex][br.getPokerChallengeGameState().ordinal()][Action.FOLD.ordinal()];
						++playerRatio2[playerIndex][br.getPokerChallengeGameState().ordinal()][Action.FOLD.ordinal()];
						++playerRatio2[playerIndex][br.getPokerChallengeGameState().ordinal()][Action.FOLD.ordinal()+1];
					} else if ( pa.action.actionName.equals("check") || pa.action.actionName.equals("call") ) {
						++playerRatio[playerIndex][br.getPokerChallengeGameState().ordinal()][Action.CALL.ordinal()];
						++playerRatio2[playerIndex][br.getPokerChallengeGameState().ordinal()][Action.CALL.ordinal()];
						++playerRatio2[playerIndex][br.getPokerChallengeGameState().ordinal()][Action.CALL.ordinal()+1];
					} else if ( pa.action.actionName.equals("bet") || pa.action.actionName.equals("raise") ) {
						++playerRatio[playerIndex][br.getPokerChallengeGameState().ordinal()][Action.RAISE.ordinal()];
						++playerRatio2[playerIndex][br.getPokerChallengeGameState().ordinal()][Action.RAISE.ordinal()];
						++playerRatio2[playerIndex][br.getPokerChallengeGameState().ordinal()][Action.RAISE.ordinal()+1];
					}
				}
			
			
			
			
			ArrayList<PlayerHandCombination> phcl = hh.showDown.playerHandList;
			int playerBucket = -1;
			
			if ( phcl.size() > 0 )
				for ( PlayerHandCombination phc : phcl ) {
					playerBucket = (phc.handCards != null) ? strategy.strategyPokerChallenge.data.Bucket.getBucket(cardBasicsCardListToCaUAlbertaCard(phc.handCards)) : -1;
					
					// if the opponent had has to shown his cards count these explicit per bucket
					if ( playerBucket >= 0 )
						for ( BettingRound br : hh.bettingRounds )
							for ( int player = 0; player < playersAr.length; player++ ) {
								playerBuckets[player][br.getPokerChallengeGameState().ordinal()][playerBucket][Action.FOLD.ordinal()] += 
										playerRatio[player][br.getPokerChallengeGameState().ordinal()][Action.FOLD.ordinal()];
								playerBuckets[player][br.getPokerChallengeGameState().ordinal()][playerBucket][Action.CALL.ordinal()] += 
										playerRatio[player][br.getPokerChallengeGameState().ordinal()][Action.CALL.ordinal()];
								playerBuckets[player][br.getPokerChallengeGameState().ordinal()][playerBucket][Action.RAISE.ordinal()] += 
										playerRatio[player][br.getPokerChallengeGameState().ordinal()][Action.RAISE.ordinal()];
							}
				}
			
			
			for ( int player = 0; player < playersAr.length; player++ ) {
				if ( ParserCreatorWinnerPoker1Table.indexOf(hh.allPlayers, playersAr[player]) == -1 )
					continue;
				
				for ( BettingRound br : hh.bettingRounds ) {
					// in every gamestate smaller then the one the player has folded he has played all betting rounds
					if ( br.getPokerChallengeGameState().ordinal() < hh.playerStatesOut.get(
							ParserCreatorWinnerPoker4Tables.indexOfPPCGS(hh.playerStatesOut, playersAr[player].name)).gameState.ordinal() )
						bettingRatio[player][br.getPokerChallengeGameState().ordinal()] =
							(bettingRatio[player][br.getPokerChallengeGameState().ordinal()] * (double) bettingAddCount[player][br.getPokerChallengeGameState().ordinal()] + 1.0) /
							((double) bettingAddCount[player][br.getPokerChallengeGameState().ordinal()] +1);
					if ( br.getPokerChallengeGameState().ordinal() > hh.playerStatesOut.get(
							ParserCreatorWinnerPoker4Tables.indexOfPPCGS(hh.playerStatesOut, playersAr[player].name)).gameState.ordinal() )
						bettingRatio[player][br.getPokerChallengeGameState().ordinal()] =
							(bettingRatio[player][br.getPokerChallengeGameState().ordinal()] * (double) bettingAddCount[player][br.getPokerChallengeGameState().ordinal()] + 0.0) /
							((double) bettingAddCount[player][br.getPokerChallengeGameState().ordinal()] +1);
					if ( br.getPokerChallengeGameState().ordinal() != hh.playerStatesOut.get(
							ParserCreatorWinnerPoker4Tables.indexOfPPCGS(hh.playerStatesOut, playersAr[player].name)).gameState.ordinal() )
						bettingAddCount[player][br.getPokerChallengeGameState().ordinal()]++;
					
					// counting the rounds a player has played per state
					if ( br.getPokerChallengeGameState().ordinal() <= hh.playerStatesOut.get(
							ParserCreatorWinnerPoker4Tables.indexOfPPCGS(hh.playerStatesOut, playersAr[player].name)).gameState.ordinal() )
						playerRoundCount[player][br.getPokerChallengeGameState().ordinal()]++;
				}
				
				int state = hh.playerStatesOut.get(ParserCreatorWinnerPoker4Tables.indexOfPPCGS(hh.playerStatesOut, playersAr[player].name)).gameState.ordinal();
				if ( state > GameState.STARTING.ordinal() && state < GameState.SHOW_DOWN.ordinal() ) {
					double addingValue = 1.0 / ((double) playerOutBettingRounds[player]+1);
					bettingRatio[player][state] = (bettingRatio[player][state] * (double) bettingAddCount[player][state] + addingValue) / (double) (bettingAddCount[player][state] + 1);
					bettingAddCount[player][state]++;
				}
			}
			
			
			// fillRatio()
			{
				double tmpSum, foldRatio, remaningProb;
				for ( int player = 0; player < playersAr.length; player++ ) {
					if ( getIndex(hh.allPlayers, playersAr[player].name) == -1 )
						continue;
					
					playerRatio[player][1][2] += playerRatio[player][0][2];
					playerRatio[player][0][2] = 0;
					
					for ( BettingRound br : hh.bettingRounds ) {
						GameState state = br.getPokerChallengeGameState();
						foldRatio = 0;
						if ( (double) playerRoundCount[player][state.ordinal()] > 0 )
							foldRatio = (double) playerRatio2[player][state.ordinal()][Action.FOLD.ordinal()] / (double) playerRoundCount[player][state.ordinal()];
						playerRatio[player][state.ordinal()][2] = foldRatio;
						
						tmpSum = playerRatio[player][state.ordinal()][0];
						tmpSum += playerRatio[player][state.ordinal()][1];
						
						if ( tmpSum == 0 && foldRatio == 0 ) {
							// if no value is set assume a probability of 100% for raise
							playerRatio[player][state.ordinal()][0] = 1.0;
							playerRatio[player][state.ordinal()][1] = 0.0;
							playerRatio[player][state.ordinal()][2] = 0.0;
							continue;
						}
						
						if ( tmpSum > 0 ) {
							remaningProb = 1.0 - foldRatio;
							playerRatio[player][state.ordinal()][2] = (playerRatio[player][state.ordinal()][0] / tmpSum) * remaningProb;
							playerRatio[player][state.ordinal()][1] = (playerRatio[player][state.ordinal()][1] / tmpSum) * remaningProb;
						}
					}
				}
			}
			
			
			// fillPlayerEstimate()
			{
				int actionCount;
				double tmpSum;
				
				for ( int player = 0; player < playersAr.length; player++ )
					if ( getIndex(hh.allPlayers, playersAr[player].name) == -1 )
						continue;
					else
						for ( BettingRound br : hh.bettingRounds )
							for ( int j = 0; j < Bucket.BUCKET_COUNT; j++ ) {
								GameState state = br.getPokerChallengeGameState();
								actionCount = 0;
								for ( Action action : Action.values() )
									actionCount += playerBuckets[player][state.ordinal()][j][action.ordinal()];
								if ( actionCount < CONSTANT.SIGNIFICANT_ROUND_COUNT || j == 0 )
									playerEstimate[player][state.ordinal()][j] = playerRatio[player][state.ordinal()].clone();
								else {
									tmpSum = 0.0;
									for ( Action action : Action.values() )
										tmpSum += playerBuckets[player][state.ordinal()][j][action.ordinal()];
									for ( Action action : Action.values() )
										playerEstimate[player][state.ordinal()][j][action.ordinal()] = (double) playerBuckets[player][state.ordinal()][j][action.ordinal()] / tmpSum;
								}
							}
			}
			
			history.roundCount++;
		}
		
		glob.bettingRatio = strategy.strategyPokerChallenge.data.Tools.cloneArray(bettingRatio);
		glob.playerBuckets = strategy.strategyPokerChallenge.data.Tools.cloneArray(playerBuckets);
		glob.playerEstimate = strategy.strategyPokerChallenge.data.Tools.cloneArray(playerEstimate);
		glob.playerGrab = playerGrab.clone();
		glob.playerRatio = strategy.strategyPokerChallenge.data.Tools.cloneArray(playerRatio);
		
		history.curHistory = new GlobalRoundData(glob);
		history.globalHistory = new GlobalRoundData(glob);
		
		History.setHistory(history);
		
		SimulationGuide.setEstimates();
	}
	
	public static void createCONSTANT(HandHistory hh) {
		CONSTANT.PLAYER_COUNT = hh.allPlayers.size();
		CONSTANT.AGGRESSIVE_PREFLOP = new double[hh.numberPlayersAtTable];
		CONSTANT.AGGRESSIVE_RAISE = new double[hh.numberPlayersAtTable];
	}
	
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
	
	/**
	 * Returns all hand histories of "hhs" in which at least one player of "players" is.
	 * 
	 * @param players an array of Players
	 * @param hhs an array of HandHistorys
	 * @return all HandHistorys of "hhs" in which at least one Player of "players" is
	 */
	private static HandHistory[] theNecessaryHH( Player[] players, HandHistory[] hhs ) {
		ArrayList<HandHistory> lHH = new ArrayList<HandHistory>();
		
		for ( HandHistory h : hhs )
			for ( Player p : players )
				if ( parser.ParserCreatorWinnerPoker1Table.indexOf(h.allPlayers, p) > -1 ) {
					lHH.add(h);
					continue;
				}
		
		HandHistory[] aHH = new HandHistory[ lHH.size() ];
		for ( int i = 0; i < aHH.length; i++ )
			aHH[i] = lHH.get(i);
		
		return aHH;
	}
	
	private static int getIndex(Player[] players, Player p) {
		for ( int i = 0; i < players.length; i++ )
			if ( players[i].name.equals(p.name) )
				return i;
		return -1;
	}
	
	/**
	 * Translates cardBasics.Card into strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.
	 * 
	 * @param c a cardBasics.Card
	 * @return cardBasics.Card as strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card
	 */
	private static strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card cardBasicsCardToCaUAlbertaCard( Card c ) {
		strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Rank r;
		strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Suit s;
		
		switch ( c.valueInt ) {
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
		
		switch ( c.colourToInt ) {
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
	
	private static int getIndex( ArrayList<Player> pal, String pName ) {
		for ( int i = 0; i < pal.size(); i++ )
			if ( pal.get(i).name.equals(pName) )
				return i;
		return -1;
	}
	
}
