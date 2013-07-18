package other;

import gameBasics.Action;
import handHistory.HandHistory;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import creatorHandHistory.CreatorHHWPClass;

import bots.Bot;
import bots.Bot_v1_2_0;

import strategy.strategyPokerChallenge.data.CONSTANT;
import strategy.strategyPokerChallenge.ringclient.ClientRingDynamics;

public class Test

{
	
	public static void main( String[] args ) throws Exception
	{
		
		HandHistory hh = parser.ParserCreatorWinnerPoker4Tables.parserMainCWP(new File("c://pokerBot//bot_v1_2_0//hhTableLeftUp.txt"),
				new File("c://pokerBot//bot_v1_2_0//parserTableLeftUp.txt"), "Hold'Em", "Fixed Limit", 9,
				"walk10er", new BufferedImage[1], new Rectangle[1]);
		
		File[] source = {new File("c://pokerBot//bot_v1_2_0//hhTableLeftDown.txt"), new File("c://pokerBot//bot_v1_2_0//hhTableLeftUp.txt"),
				new File("c://pokerBot//bot_v1_2_0//hhTableRightUp.txt"), new File("c://pokerBot//bot_v1_2_0//hhTableRightDown.txt")};
		File[] parsers = {new File( "c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableLeftDown.txt"),new File( "c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableLeftUp.txt"),
				new File("c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableRightUp.txt"), new File("c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableRightDown.txt")};
		bots.Bot_v1_1_0Tables[] table = {bots.Bot_v1_1_0Tables.LEFT_DOWN, bots.Bot_v1_1_0Tables.LEFT_UP, bots.Bot_v1_1_0Tables.RIGHT_UP, bots.Bot_v1_1_0Tables.RIGHT_DOWN};
		String[] gameType = {"Hold_Em", "Hold_Em", "Hold_Em", "Hold_Em"};
		String[] limit = {"Fixed Limit", "Fixed Limit", "Fixed Limit", "Fixed Limit"};
		int[] maxSeatOnTable = {9, 9, 9, 9};
		String playYouName = "walk10er";
		BufferedImage[][] picutreSeats = new BufferedImage[4][0];
		Rectangle[][] spaceSeats = new Rectangle[4][0];
		
		strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createPrivateHands(source, parsers, table, gameType,
				limit, maxSeatOnTable, playYouName, picutreSeats, spaceSeats);
		
		strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createCONSTANT(hh);
		ClientRingDynamics crd = strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createRingDynamics(hh, hh.getPlayerYou("walk10er"));
		strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createHistory(hh, hh.getPlayerYou("walk10er"));		
		Action action = strategy.strategyPokerChallenge.interfacesToPokerChallenge.StrategyTwo.actionFor(hh, hh.getPlayerYou("walk10er"), new BufferedImage[1], new Rectangle[1], crd);
		
		System.out.println(action);
//		
//		Bot bot = new Bot();
//		Bot_v1_2_0 botV = bot.bots.get(3);
//		
//		botV.click(new Action("fold"));
//		botV.click(new Action("call"));
//		botV.click(new Action("raise"));
//		
//		synchronized( bot ) {bot.wait(1000);}
//		for ( int i = 0; i < 10; i++ )
//			botV.click(new Action("fold"));
//		synchronized( bot ) {bot.wait(1000);}
//		for ( int i = 0; i < 10; i++ )
//			botV.click(new Action("call"));
//		synchronized( bot ) {bot.wait(1000);}
//		for ( int i = 0; i < 10; i++ )
//			botV.click(new Action("raise"));
//		
//		File f = new File( "c://pokerBot//bot_v1_2_0//hhTableLeftUp.txt" );
//		Rectangle rectNP = new Rectangle( 2010, 210, 480, 380 );
//		Rectangle rectTC = new Rectangle(15, 548, 265, 49);
//		CreatorHHWPClass creator = new CreatorHHWPClass(f, rectNP, rectTC);
//		creator.createHH();
		
	}
	
}
