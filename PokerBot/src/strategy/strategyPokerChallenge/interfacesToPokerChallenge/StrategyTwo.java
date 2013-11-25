package strategy.strategyPokerChallenge.interfacesToPokerChallenge;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import parser.ParserCreatorWinnerPoker4Tables;
import strategy.strategyPokerChallenge.ringclient.ClientRingDynamics;
import strategy.strategyPokerChallenge.simulation.real.RealSimulator;

import gameBasics.Action;
import gameBasics.GameState;
import gameBasics.PlayerYou;
import handHistory.HandHistory;
import handHistory.IPostFlop;
import handHistory.PreFlop;

/**
 * This class implements an interface for using the strategy of the TU Darmstadt's AKI-RealBot.
 */
public class StrategyTwo {
	
	/**
	 * For using this method the following datastructures/classes have to be initialized:
	 * strategy.strategyPokerChallenge.CONSTANT
	 * strategy.strategyPokerChallenge.History and strategy.strategyPokerChallenge.GlobalRoundData
	 * strategy.strategyPokerChallenge.ClientRingDynamics
	 * 
	 * A sample code for doing that:
	 * 	strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createPrivateHands( new File("c://pokerBot//bot_v1_1_0//hhTableLeftDown.txt"),
	 *				bots.Bot_v1_1_0Tables.LEFT_DOWN, "Hold'Em", "Fixed Limit", 9, "walk10er", new BufferedImage[1], new Rectangle[1] );
	 *	HandHistory hh = parser.ParserCreatorWinnerPoker4Tables.parserMainCWP(new File("c://pokerBot//bot_v1_1_0//hhTableLeftDown.txt"),
	 *				new File("c://pokerBot//bot_v1_2_0//parserTableLeftDown.txt"), "Hold'Em", "Fixed Limit", 9,
	 *				"walk10er", new BufferedImage[1], new Rectangle[1]);
	 *	strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createCONSTANT(hh);
	 *	ClientRingDynamics crd = strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createRingDynamics(hh, hh.getPlayerYou("walk10er"));
	 *	strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createHistory(hh, hh.getPlayerYou("walk10er"));
	 * 
	 * @param hh the actual hand history
	 * @param py the player you are
	 * @param sesFile a file with the people who enter and leave the table
	 * @param pictureSeats pictures of the seats if there are not any players
	 * @param spaceSeats the rectangles where the seats are
	 * @param crd the ClientRingDynamics
	 * @param tableWasRemoved whether the table was removed or not. This information is important for other.Tools.compare(...)-methods.
	 * @return The action determined by the strategy of the TU Darmstadt poker agent AKI-RealBot
	 * @throws AWTException
	 */
	public static synchronized Action actionFor( HandHistory hh, PlayerYou py, File sesFile, BufferedImage[] pictureSeats, Rectangle[] spaceSeats, ClientRingDynamics crd,
			boolean tableWasRemoved) throws AWTException {
		if ( hh.bettingRounds.get(hh.bettingRounds.size()-1).getPokerChallengeGameState().equals(strategy.strategyPokerChallenge.data.GameState.PRE_FLOP) )
//			if ( hh.allPlayers.size() != ParserCreatorWinnerPoker4Tables.howManyPlayersAtTableByPicture(pictureSeats, spaceSeats, tableWasRemoved) )
			if ( hh.allPlayers.size() != ParserCreatorWinnerPoker4Tables.howManyPlayersAtTableByFile(sesFile))
				return strategy.strategyPokerStrategy.StrategyOne.actionFor(hh, py);
		
		long time;
		double random = Math.random();
		if (random <= 0.25)
			time = 3000L;
		else if (random <= 0.5)
			time = 3500L;
		else if (random <= 0.75)
			time = 4000L;
		else if (random <= 1.0)
			time = 4500L;
		else
			time = 6000L;
		
		RealSimulator rs = new RealSimulator();
		TimeManager tm = new TimeManager(rs);
		rs.startSimulation(crd);
		Action ret = tm.getDecision(time);
		
		if ( hh.state == GameState.PRE_FLOP ) {
			if ( (ret.actionName.equals("fold") || ret.actionName.equals("call")) && PreFlop.actionMin(hh, py).actionName.equals("check") )
				ret.set("check");
		} else {
			IPostFlop postflop = (IPostFlop) hh.bettingRounds.get(hh.bettingRounds.size()-1);
			if ( (ret.actionName.equals("fold") || ret.actionName.equals("call")) && postflop.actionMin().actionName.equals("check") )
				ret.set("check");
		}
		
		return ret;
	}
	
}
