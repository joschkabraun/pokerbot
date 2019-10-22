package other;

import gameBasics.Action;
import handHistory.HandHistory;
import handHistory.HandHistory.GameType;
import handHistory.HandHistory.Limit;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import strategy.strategyPokerChallenge.ringclient.ClientRingDynamics;

public class Test

{
	
	public static final boolean debug = false;
	
	public static void main(String[] args) throws Throwable {
		File sesFile = new File("c://pokerBot//bot_v1_2_0//sessionalHHTableLeftUp.txt");
		HandHistory hh = parser.ParserCreatorWinnerPoker4Tables.parserMainCWP(new File("c://pokerBot//bot_v1_2_0//hhTableLeftUp.txt"),
				new File("c://pokerBot//bot_v1_2_0//parserTableLeftUp.txt"), sesFile, GameType.HOLD_EM, Limit.FIXED_LIMIT, 9, "walk10er");
		System.out.println(hh);
		
		File[] source = {new File("c://pokerBot//bot_v1_2_0//hhTableLeftDown.txt"), new File("c://pokerBot//bot_v1_2_0//hhTableLeftUp.txt"),
				new File("c://pokerBot//bot_v1_2_0//hhTableRightUp.txt"), new File("c://pokerBot//bot_v1_2_0//hhTableRightDown.txt")};
		File[] parsers = {new File( "c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableLeftDown.txt"),new File( "c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableLeftUp.txt"),
				new File("c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableRightUp.txt"), new File("c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableRightDown.txt")};
		File[] sesFiles = {new File("c://pokerBot//bot_v1_2_0//sessionalHHTableLeftDown.txt"),new File("c://pokerBot//bot_v1_2_0//sessionalHHTableLeftUp.txt"),
				new File("c://pokerBot//bot_v1_2_0//sessionalHHTableRightUp.txt"), new File("c://pokerBot//bot_v1_2_0//sessionalHHTableRightDown.txt")};
		bots.Bot_v1_1_0Tables[] table = {bots.Bot_v1_1_0Tables.LEFT_DOWN, bots.Bot_v1_1_0Tables.LEFT_UP, bots.Bot_v1_1_0Tables.RIGHT_UP, bots.Bot_v1_1_0Tables.RIGHT_DOWN};
		GameType[] gameType = {GameType.HOLD_EM, GameType.HOLD_EM, GameType.HOLD_EM, GameType.HOLD_EM};
		Limit[] limit = {Limit.FIXED_LIMIT, Limit.FIXED_LIMIT, Limit.FIXED_LIMIT, Limit.FIXED_LIMIT};
		int[] maxSeatOnTable = {9, 9, 9, 9};
		String playYouName = "walk10er";
		
		strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createPrivateHands(source, parsers, sesFiles, table, gameType,
				limit, maxSeatOnTable, playYouName, true);
		
		strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createCONSTANT(hh);
		ClientRingDynamics crd = strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createRingDynamics(hh, hh.getPlayerYou("walk10er"));
		strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createHistory(hh, hh.getPlayerYou("walk10er"));		
		Action action = strategy.strategyPokerChallenge.interfacesToPokerChallenge.StrategyTwo.actionFor(hh, hh.getPlayerYou("walk10er"), sesFile, new BufferedImage[1],
				new Rectangle[1], crd, true);
		
		System.out.println(action);		
	}
	
}
