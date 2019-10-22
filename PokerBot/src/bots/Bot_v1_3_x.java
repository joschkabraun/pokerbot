package bots;

import gameBasics.Action;
import gameBasics.PlayerYou;
import handHistory.HandHistory;
import handHistory.HandHistory.GameType;
import handHistory.HandHistory.Limit;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import other.Tools;
import parser.ParserCreatorWinnerPoker4Tables;
import strategy.strategyPokerChallenge.interfacesToPokerChallenge.StrategyTwo;
import strategy.strategyPokerChallenge.ringclient.ClientRingDynamics;

import creatorHandHistory.CreatorHHWPClass;

public class Bot_v1_3_x extends Thread {
	
	/**
	 * The name of the player you are/represent.
	 */
	private String namePlayerYou;
	
	/**
	 * Whether the table was removed of the origin. If the table was removed of its origin, that could cause problems with methods as other.Tools.compare(...).
	 * 
	 */
	private boolean tableWasRemoved;
	
	/**
	 * out is for the communication with the Thread Interrupter
	 */
	private PipedOutputStream out;
	
	/**
	 * That is the table on which this bot-thread is ran.
	 */
	private Bot_v1_1_0Tables table;
	
	public static final Logger log = Logger.getLogger( Bot_v1_1_0.class.getName() );
	
	public static final Lock lock = new ReentrantLock();
	
	/**
	 * The file in which the logger writes.
	 */
	private File logFile;
	
	/**
	 * The file in which the hand history is.
	 */
	public File hhFile;
	
	/**
	 * The file in which the parser writes for parsing the hand history.
	 */
	private File parserFileHH;
	
	/**
	 * The file which in which the hand history of the people who enter and leave the table.
	 */
	private File sesFile;
	
	/**
	 * The creator of the class, which creates the hand history.
	 */
	private CreatorHHWPClass creator;
	
	/**
	 * The Rectangle in which the creator can copy-paste.
	 */
	private Rectangle SPACE_NOTEPAD;
	
	/**
	 * That is the space where the fold-button is. The localization depends on which table is played.
	 */
	public Rectangle SPACE_BUTTON_FOLD;
	
	/**
	 * That is the space where the check-/call-button is. The localization depends on which table is played.
	 */
	public Rectangle SPACE_BUTTON_CHECK_CALL;
	
	/**
	 * That is the space where the bet-/raise-button is. The locailzation depends on which table is played.
	 */
	public Rectangle SPACE_BUTTON_BET_RAISE;
	
	/**
	 * It is the picture of the fold-button.
	 */
	public BufferedImage PICTURE_BUTTON_FOLD;
	
	/**
	 * It is the picture of the check-/call-button.
	 */
	public BufferedImage PICTURE_BUTTON_CKECK_CALL;
	
	/**
	 * It is the picture of the bet-/raise-button.
	 */
	public BufferedImage PICTURE_BUTTON_BET_RAISE;
	
	/**
	 * That is the localization of the table chat on the screen. It depends on which table is played.
	 */
	private Rectangle SPACE_TABLECHAT;
	
	/**
	 * The gui of the bot.
	 */
	private JFrame gui;
	
	public Bot_v1_3_x(ThreadGroup main, String string, PipedOutputStream out, Bot_v1_1_0Tables table, String namePlayerYou, boolean tableWasRemoved, JFrame frame)
	{
		super( main, string );
		this.out = out;
		this.table = table;
		this.tableWasRemoved = tableWasRemoved;
		this.gui = frame;
		allocateTableToRest( table );
		creator = new CreatorHHWPClass( hhFile, sesFile, SPACE_NOTEPAD, SPACE_TABLECHAT );
		this.namePlayerYou = namePlayerYou;
	}
	
	@Override
	public void run() {
		try {
			Handler handler = new FileHandler( logFile.getAbsolutePath() );
			log.addHandler( handler );
			
			Robot r = new Robot();																				// robot
			
			Object o = new Object();
			
			while (true) {
				lock.lock();
				synchronized (o) {
					o.wait(750);
				}
				lock.unlock();
				
				BufferedImage f1 = r.createScreenCapture( SPACE_BUTTON_FOLD );
				BufferedImage f2 = r.createScreenCapture( SPACE_BUTTON_CHECK_CALL );
				BufferedImage f3 = r.createScreenCapture( SPACE_BUTTON_BET_RAISE );
				
				if ( (!this.tableWasRemoved && (Tools.compare(f1,PICTURE_BUTTON_FOLD,0.65) || Tools.compare(f2, PICTURE_BUTTON_CKECK_CALL, 0.65) || Tools.compare(f3, PICTURE_BUTTON_BET_RAISE, 0.65))) ||
				 (this.tableWasRemoved && (Tools.compareSimilar(f1,PICTURE_BUTTON_FOLD,0.05) || Tools.compareSimilar(f2, PICTURE_BUTTON_CKECK_CALL, 0.05) || Tools.compareSimilar(f3, PICTURE_BUTTON_BET_RAISE, 0.05))) ) {
					lock.lock();
					log.info( "Beginning creating the hand-history. Table: " + table.toString() );
					try {
						creator.createHH(new Rectangle(gui.getLocation().x+10, gui.getLocation().y+110, gui.getWidth()-20, gui.getHeight()-120));																			// the creator creates the necessary hand history
						log.info( "Creating of the hand-history was successful. Table: " + table.toString() );
					} catch ( Exception e ) {
						log.log( Level.SEVERE, "Creating of the hand-history was not successful. Table: " + table.toString(), e );
						log.info( "Next attempt to create the hand-history." );
						creator.createHH(new Rectangle(gui.getLocation().x+10, gui.getLocation().y+110, gui.getWidth()-20, gui.getHeight()-120));
					}
					lock.unlock();
					
					
					HandHistory hh = new HandHistory();															// hand history
					log.info( "Beginning parsing the hand-history-text-file to a object HandHistory. Table: " + table.toString() );
					try {
						hh = ParserCreatorWinnerPoker4Tables.parserMainCWP( hhFile, parserFileHH, sesFile, GameType.HOLD_EM, Limit.FIXED_LIMIT, 9, namePlayerYou);
						log.info( "Parsing of the hand-history-text-file was successful. Table: " + table.toString() );
					} catch ( Exception e1 ) {
						log.log(Level.SEVERE, "Parsing of the hand-history-text-file was not successfull. The path of the text-file is: "
								+ hhFile.toString() + ". Table: " + table.toString(), e1 );
						sleep(100);
						try {
							hh = ParserCreatorWinnerPoker4Tables.parserMainCWP( hhFile, parserFileHH, sesFile, GameType.HOLD_EM, Limit.FIXED_LIMIT, 9, namePlayerYou);
							log.info( "Parsing of the hand-history-text-file was successful. Table: " + table.toString() );
						} catch ( Exception e2 ) {
							log.log(Level.SEVERE, "Parsing of the hand-history-text-file was not successfull. The path of the text-file is: "
									+ hhFile.toString() + ". Table: " + table.toString(), e2 );
							sleep(100);
						} try {
							hh = ParserCreatorWinnerPoker4Tables.parserMainCWP( hhFile, parserFileHH, sesFile, GameType.HOLD_EM, Limit.FIXED_LIMIT, 9, namePlayerYou);
							log.info( "Parsing of the hand-history-text-file was successful. Table: " + table.toString() );
						} catch ( Exception e3 ) {
							log.log(Level.SEVERE, "Parsing of the hand-history-text-file was not successfull. The path of the text-file is: "
									+ hhFile.toString() + ". Table: " + table.toString(), e3 );
							sleep(100);
						} try {
							hh = ParserCreatorWinnerPoker4Tables.parserMainCWP( hhFile, parserFileHH, sesFile, GameType.HOLD_EM, Limit.FIXED_LIMIT, 9, namePlayerYou);
							log.info( "Parsing of the hand-history-text-file was successful. Table: " + table.toString() );
						} catch ( Exception e4 ) {
							log.log(Level.SEVERE, "Parsing of the hand-history-text-file was not successfull. The path of the text-file is: "
									+ hhFile.toString() + ". Table: " + table.toString(), e4 );
							exit();
						}
					}
					
					log.info( "End parsing the hand-history-text-file to a object HandHistory. Table: " + table.toString() );
					
					
					PlayerYou you = new PlayerYou();															// player you
					log.info( "Beginning getting PlayerYou. Table: " + table.toString() );
					try {
						you.set( hh.getPlayerYou( namePlayerYou ) );
					} catch ( Exception e ) {
						log.log( Level.SEVERE, "Getting the PlayerYou was not successful. Table: " + table.toString(), e );
						exit();
					}
					
					ClientRingDynamics crd = new ClientRingDynamics();
					log.info("Beginning creating the TU-Darmstadt's AKI-RealBot datastructure. Table: " + table.toString());						// AKI-RealBot datastructure
					try {
						File[] source = {new File("c://pokerBot//bot_v1_2_0//hhTableLeftDown.txt"), new File("c://pokerBot//bot_v1_2_0//hhTableLeftUp.txt"),
								new File("c://pokerBot//bot_v1_2_0//hhTableRightUp.txt"), new File("c://pokerBot//bot_v1_2_0//hhTableRightDown.txt")};
						File[] parsers = {new File( "c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableLeftDown.txt"),new File( "c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableLeftUp.txt"),
								new File("c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableRightUp.txt"), new File("c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableRightDown.txt")};
						File[] sesFiles = {new File("c://pokerBot//bot_v1_2_0//sessionalHHTableLeftDown.txt"),new File("c://pokerBot//bot_v1_2_0//sessionalHHTableLeftUp.txt"),
								new File("c://pokerBot//bot_v1_2_0//sessionalHHTableRightUp.txt"), new File("c://pokerBot//bot_v1_2_0//sessionalHHTableRightDown.txt")};
						bots.Bot_v1_1_0Tables[] tables = {bots.Bot_v1_1_0Tables.LEFT_DOWN, bots.Bot_v1_1_0Tables.LEFT_UP, bots.Bot_v1_1_0Tables.RIGHT_UP, bots.Bot_v1_1_0Tables.RIGHT_DOWN};
						GameType[] gameType = {GameType.HOLD_EM, GameType.HOLD_EM, GameType.HOLD_EM, GameType.HOLD_EM};
						Limit[] limit = {Limit.FIXED_LIMIT, Limit.FIXED_LIMIT, Limit.FIXED_LIMIT, Limit.FIXED_LIMIT};
						int[] maxSeatOnTable = {9, 9, 9, 9};
						
						long time = System.currentTimeMillis();
						
						strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createPrivateHands(source, parsers, sesFiles, tables, gameType, limit, maxSeatOnTable, namePlayerYou, false);
						long time1 = System.currentTimeMillis();
						strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createCONSTANT(hh);
						long time2 = System.currentTimeMillis();
						crd = strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createRingDynamics(hh, hh.getPlayerYou("walk10er"));
						long time3 = System.currentTimeMillis();
						strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createHistory(hh, you);
						long time4 = System.currentTimeMillis();
						
						if ( time4 - time > 9000L ) {
							System.out.println("time-diff-1: " + (time1 - time) );
							System.out.println("time-diff-2: " + (time2 - time1) );
							System.out.println("time-diff-3: " + (time3 - time2) );
							System.out.println("time-diff-4: " + (time4- time3) );
						}
						
						log.info("End creating the TU-Darmstadt's AKI-RealBot datastructure. Table: " + table.toString());
					} catch (Exception e) {
						log.log(Level.SEVERE, "Creating the TU-Darmstadt's AKI-RealBot datastructure was not successful! Table: " + table.toString(), e);
						log.info("The second attempt to creating the TU-Darmstadt's AKI-RealBot datastructure. Table: " + table.toString());
						try {
							File[] source = {new File("c://pokerBot//bot_v1_2_0//hhTableLeftDown.txt"), new File("c://pokerBot//bot_v1_2_0//hhTableLeftUp.txt"),
									new File("c://pokerBot//bot_v1_2_0//hhTableRightUp.txt"), new File("c://pokerBot//bot_v1_2_0//hhTableRightDown.txt")};
							File[] parsers = {new File( "c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableLeftDown.txt"),new File( "c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableLeftUp.txt"),
									new File("c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableRightUp.txt"), new File("c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableRightDown.txt")};
							File[] sesFiles = {new File("c://pokerBot//bot_v1_2_0//sessionalHHTableLeftDown.txt"),new File("c://pokerBot//bot_v1_2_0//sessionalHHTableLeftUp.txt"),
									new File("c://pokerBot//bot_v1_2_0//sessionalHHTableRightUp.txt"), new File("c://pokerBot//bot_v1_2_0//sessionalHHTableRightDown.txt")};
							bots.Bot_v1_1_0Tables[] tables = {bots.Bot_v1_1_0Tables.LEFT_DOWN, bots.Bot_v1_1_0Tables.LEFT_UP, bots.Bot_v1_1_0Tables.RIGHT_UP, bots.Bot_v1_1_0Tables.RIGHT_DOWN};
							GameType[] gameType = {GameType.HOLD_EM, GameType.HOLD_EM, GameType.HOLD_EM, GameType.HOLD_EM};
							Limit[] limit = {Limit.FIXED_LIMIT, Limit.FIXED_LIMIT, Limit.FIXED_LIMIT, Limit.FIXED_LIMIT};
							int[] maxSeatOnTable = {9, 9, 9, 9};
							
							strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createPrivateHands(source, parsers, sesFiles, tables, gameType, limit, maxSeatOnTable, namePlayerYou, false);
							strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createCONSTANT(hh);
							crd = strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createRingDynamics(hh, hh.getPlayerYou("walk10er"));
							strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createHistory(hh, you);
							
							log.info("End creating the TU-Darmstadt's AKI-RealBot datastructure was succesful. Table: " + table.toString());
						} catch (Exception e2) {
							log.log(Level.SEVERE, "Creating the TU-Darmstadt's AKI-RealBot datastructure was not successful! Table: " + table.toString(), e2);
							log.info("The third attempt to creating the TU-Darmstadt's AKI-RealBot datastructure. Table: " + table.toString());
							try {
								File[] source = {new File("c://pokerBot//bot_v1_2_0//hhTableLeftDown.txt"), new File("c://pokerBot//bot_v1_2_0//hhTableLeftUp.txt"),
										new File("c://pokerBot//bot_v1_2_0//hhTableRightUp.txt"), new File("c://pokerBot//bot_v1_2_0//hhTableRightDown.txt")};
								File[] parsers = {new File( "c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableLeftDown.txt"),new File( "c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableLeftUp.txt"),
										new File("c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableRightUp.txt"), new File("c://pokerBot//bot_v1_2_0//parserHHToTUDBotHistoryTableRightDown.txt")};
								File[] sesFiles = {new File("c://pokerBot//bot_v1_2_0//sessionalHHTableLeftDown.txt"),new File("c://pokerBot//bot_v1_2_0//sessionalHHTableLeftUp.txt"),
										new File("c://pokerBot//bot_v1_2_0//sessionalHHTableRightUp.txt"), new File("c://pokerBot//bot_v1_2_0//sessionalHHTableRightDown.txt")};
								bots.Bot_v1_1_0Tables[] tables = {bots.Bot_v1_1_0Tables.LEFT_DOWN, bots.Bot_v1_1_0Tables.LEFT_UP, bots.Bot_v1_1_0Tables.RIGHT_UP, bots.Bot_v1_1_0Tables.RIGHT_DOWN};
								GameType[] gameType = {GameType.HOLD_EM, GameType.HOLD_EM, GameType.HOLD_EM, GameType.HOLD_EM};
								Limit[] limit = {Limit.FIXED_LIMIT, Limit.FIXED_LIMIT, Limit.FIXED_LIMIT, Limit.FIXED_LIMIT};
								int[] maxSeatOnTable = {9, 9, 9, 9};
								
								strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createPrivateHands(source, parsers, sesFiles, tables, gameType, limit, maxSeatOnTable, namePlayerYou, false);
								strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createCONSTANT(hh);
								crd = strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createRingDynamics(hh, hh.getPlayerYou("walk10er"));
								strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createHistory(hh, you);
								
								log.info("End creating the TU-Darmstadt's AKI-RealBot datastructure was succesful. Table: " + table.toString());
							} catch (Exception e33) {
								log.log(Level.SEVERE, "Creating the TU-Darmstadt's AKI-RealBot datastructure was not successful! Table: " + table.toString(), e);
								exit();
							}
						}
					}
					
					gameBasics.Action action = new gameBasics.Action();											// action
					log.info( "Start getting right Action for the actual situation. Table: " + table.toString() );
					try {
						action.set(StrategyTwo.actionFor(hh, you, sesFile, crd));
					} catch ( Exception e ) {
						log.log( Level.SEVERE, "Getting the Action for the actual situation was not possible. Table: " + table.toString(), e );
						log.info("The second attempt to get the Action. Table: " + table.toString());
						try {
							action.set(StrategyTwo.actionFor(hh, you, sesFile, crd));
						} catch ( Exception e2 ) {
							log.log( Level.SEVERE, "Getting the Action for the actual situation was not possible. Table: " + table.toString(), e );
							log.info("The third attempt to get the Action. Table: " + table.toString());
							try {
								action.set(StrategyTwo.actionFor(hh, you, sesFile, crd));
							} catch ( Exception e3 ) {
								log.log( Level.SEVERE, "Getting the Action for the actual situation was not possible. Table: " + table.toString(), e );
								exit();
							}
						}
					}
					
					if ( action.isEmpty() || action == null )
						log.log( Level.SEVERE, "There was not any Action for the actual situation. Table: " + table.toString() );
					else
						log.info( String.format("Getting Action for the actual situation was sucessful.%nThe action was: " + action.toString() + ". Table: " + table.toString()) );
					log.info( "End getting Action for the actual game. Table: " + table.toString() );
					
					lock.lock();
					click( action );
					lock.unlock();
					lock.lock();
					synchronized (o) {
						o.wait(7500);
					}

				}
			}
		} catch ( Exception e ) {
			System.err.println( "There was a problem which was not in the logged part! The table was: " + this.table );
			e.printStackTrace();
			
			try {
				out.write( 1 ); out.flush(); out.close();
			} catch ( Exception e2 ) {
				System.err.println( "There was a problem with writing, flushing or closing this.out! The table was: " + this.table );
				e.printStackTrace();
				this.getThreadGroup().interrupt();
			}
		}
	}
	
	/**
	 * This method determines the pictures and locations of the action-buttons, the table chat, the nine seats and the file
	 * by given table.
	 */
	private void allocateTableToRest( Bot_v1_1_0Tables table )
	{
		SPACE_NOTEPAD = new Rectangle(gui.getLocation().x+10, gui.getLocation().y+110, gui.getWidth()-20, gui.getHeight()-120);
		
		if ( table == Bot_v1_1_0Tables.LEFT_UP )
		{
			logFile = new File( "c://pokerBot//bot_v1_3_x//loggingBotTableLeftUp.txt" );
			hhFile = new File( "c://pokerBot//bot_v1_3_x//hhTableLeftUp.txt" );
			parserFileHH = new File( "c://pokerBot//bot_v1_3_x//parserTableLeftUp.txt" );
			sesFile = new File("c://pokerBot//bot_v1_3_x//sessionalHHTableLeftUp.txt");
			
			SPACE_TABLECHAT = new Rectangle(15, 548, 265, 49);
			
			if ( ! this.tableWasRemoved ) {
				SPACE_BUTTON_FOLD = new Rectangle( 436, 537, 104, 35 );
				SPACE_BUTTON_CHECK_CALL = new Rectangle( 553, 537, 105, 38 );
				SPACE_BUTTON_BET_RAISE = new Rectangle( 674, 536, 100, 38 );
			} else {													// they are smaller than the origins because if the tables shifts there could be problems
				SPACE_BUTTON_FOLD = new Rectangle( 456, 547, 54, 15 );
				SPACE_BUTTON_CHECK_CALL = new Rectangle( 573, 547, 55, 18 );
				SPACE_BUTTON_BET_RAISE = new Rectangle( 694, 546, 50, 18 );
			}
			
			try {
				PICTURE_BUTTON_FOLD = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureButtonFold.PNG") );
				PICTURE_BUTTON_CKECK_CALL = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureButtonCheckCall.PNG") );
				PICTURE_BUTTON_BET_RAISE = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureButtonBetRaise.PNG") );

			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		else if ( table == Bot_v1_1_0Tables.LEFT_DOWN )
		{
			logFile = new File( "c://pokerBot//bot_v1_3_x//loggingBotTableLeftDown.txt" );
			hhFile = new File( "c://pokerBot//bot_v1_3_x//hhTableLeftDown.txt" );
			parserFileHH = new File( "c://pokerBot//bot_v1_3_x//parserTableLeftDown.txt" );
			sesFile = new File("c://pokerBot//bot_v1_3_x//sessionalHHTableLeftDown.txt");
			
			SPACE_TABLECHAT = new Rectangle( 14, 1105, 328, 50 );
			
			if ( ! this.tableWasRemoved ) {
				SPACE_BUTTON_FOLD = new Rectangle( 400, 1105, 95, 30 );				// the action-buttons and the table chat
				SPACE_BUTTON_CHECK_CALL = new Rectangle( 500, 1105, 100, 30 );
				SPACE_BUTTON_BET_RAISE = new Rectangle( 610, 1105, 100, 30 );
			} else {									// they are smaller than the origins because if the tables shifts there could be problems
				SPACE_BUTTON_FOLD = new Rectangle( 420, 1115, 55, 10 );
				SPACE_BUTTON_CHECK_CALL = new Rectangle( 520, 1115, 50, 10 );
				SPACE_BUTTON_BET_RAISE = new Rectangle( 630, 1115, 50, 10 );
			}
			
			try {
				PICTURE_BUTTON_FOLD = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureButtonFold.PNG") );
				PICTURE_BUTTON_CKECK_CALL = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown/pictureButtonCheckCall.PNG") );
				PICTURE_BUTTON_BET_RAISE = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureButtonBetRaise.PNG") );
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		else if ( table == Bot_v1_1_0Tables.RIGHT_DOWN )
		{
			logFile = new File( "c://pokerBot//bot_v1_3_x//loggingBotTableRightDown.txt" );
			hhFile = new File( "c://pokerBot//bot_v1_3_x//hhTableRightDown.txt" );
			parserFileHH = new File( "c://pokerBot//bot_v1_3_x//parserTableRightDown.txt" );
			sesFile = new File("c://pokerBot//bot_v1_3_x//sessionalHHTableRightDown.txt");
			
			SPACE_TABLECHAT = new Rectangle( 833, 1142, 322, 51 );
			
			if ( ! this.tableWasRemoved ) {
				SPACE_BUTTON_FOLD = new Rectangle( 1200, 1100, 100, 35 );
				SPACE_BUTTON_CHECK_CALL = new Rectangle( 1310, 1100, 100, 35 );
				SPACE_BUTTON_BET_RAISE = new Rectangle( 1420, 1100, 100, 35 );
			} else {										// they are smaller than the origins because if the tables shifts there could be problems
				SPACE_BUTTON_FOLD = new Rectangle( 1220, 1110, 50, 15 );
				SPACE_BUTTON_CHECK_CALL = new Rectangle( 1330, 1110, 50, 15 );
				SPACE_BUTTON_BET_RAISE = new Rectangle( 1440, 1110, 50, 15 );
			}
			
			try {
				PICTURE_BUTTON_FOLD = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureButtonFold.PNG") );
				PICTURE_BUTTON_CKECK_CALL = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureButtonCheckCall.PNG") );
				PICTURE_BUTTON_BET_RAISE = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureButtonBetRaise.PNG") );
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		else if ( table == Bot_v1_1_0Tables.RIGHT_UP )
		{
			logFile = new File( "c://pokerBot//bot_v1_3_x//loggingBotTableRightUp.txt" );
			hhFile = new File( "c://pokerBot//bot_v1_3_x//hhTableRightUp.txt" );
			parserFileHH = new File( "c://pokerBot//bot_v1_3_x//parserTableRightUp.txt" );
			sesFile = new File("c://pokerBot//bot_v1_3_x//sessionalHHTableRightUp.txt");
			
			SPACE_TABLECHAT = new Rectangle( 834, 581, 361, 61 );
			
			if ( ! this.tableWasRemoved ) {
				SPACE_BUTTON_FOLD = new Rectangle( 1255, 540, 100, 35 );
				SPACE_BUTTON_CHECK_CALL = new Rectangle( 1375, 540, 100, 32 );
				SPACE_BUTTON_BET_RAISE = new Rectangle( 1490, 540, 100, 45 );
			} else {											// they are smaller than the origins because if the tables shifts there could be problems
				SPACE_BUTTON_FOLD = new Rectangle( 1275, 550, 50, 15 );
				SPACE_BUTTON_CHECK_CALL = new Rectangle( 1395, 550, 50, 15 );
				SPACE_BUTTON_BET_RAISE = new Rectangle( 1510, 550, 50, 15 );
			}
			
			try {
				PICTURE_BUTTON_FOLD = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureButtonFold.PNG") );
				PICTURE_BUTTON_CKECK_CALL = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureButtonCheckCall.PNG") );
				PICTURE_BUTTON_BET_RAISE = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureButtonBetRaise.PNG") );
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		else
			throw new IllegalArgumentException( "The commited table does not mean anything to the bot. The table: " + table.toString() );
	}
	
	/**
	 * Returns for each action the right button.
	 * 
	 * @param a the action for which the button is searched
	 * @return the button for the chosen action
	 */
	private Rectangle actionToButton( gameBasics.Action a )
	{
		Rectangle r = new Rectangle();
		
		if ( a.actionName.equals( "fold" ) )
			r = SPACE_BUTTON_FOLD;
		else if ( a.actionName.equals( "check" ) || a.actionName.equals( "call" ) )
			r = SPACE_BUTTON_CHECK_CALL;
		else if ( a.actionName.equals( "bet" ) || a.actionName.equals( "raise" ) )
			r = SPACE_BUTTON_BET_RAISE;
		else
			throw new IllegalArgumentException( "It was not possible to assign a button to the chosen action. The action was: " + a.toString() );
		
		return r;
	}
	
	/**
	 * This method clicks on the correct button to the action act.
	 * 
	 * @param action the action
	 */
	public void click( gameBasics.Action action )
	{
		try {
			Rectangle re = actionToButton( action );													// mouse clicking
			Point p = Tools.createPointIn( re );
			Robot r = new Robot();
			r.mouseMove(p.x, p.y);
			r.mousePress( InputEvent.BUTTON1_DOWN_MASK );
			r.mouseRelease( InputEvent.BUTTON1_DOWN_MASK );
			
			synchronized ( r ) { r.wait( 75 ); }								// this part e.g. the situation, when the strategy return folding but also checking is possible
			BufferedImage f1 = r.createScreenCapture( SPACE_BUTTON_FOLD );
			BufferedImage f2 = r.createScreenCapture( SPACE_BUTTON_CHECK_CALL );
			BufferedImage f3 = r.createScreenCapture( SPACE_BUTTON_BET_RAISE );
			
//			if ( Tools.compare(f1, PICTURE_BUTTON_FOLD, 0.8) || Tools.compare(f2, PICTURE_BUTTON_CKECK_CALL, 0.8) || Tools.compare(f3, PICTURE_BUTTON_BET_RAISE, 0.8) ) {
			if ( Tools.compareSimilar(f1,PICTURE_BUTTON_FOLD,0.05) || Tools.compareSimilar(f2, PICTURE_BUTTON_CKECK_CALL, 0.05) || Tools.compareSimilar(f3, PICTURE_BUTTON_BET_RAISE, 0.05) ) {
				gameBasics.Action act = new Action( "check" );
				re = actionToButton( act );
				p = Tools.createPointIn( re );
				r.mouseMove(p.x, p.y);
				r.mousePress( InputEvent.BUTTON1_DOWN_MASK );
				r.mouseRelease( InputEvent.BUTTON1_DOWN_MASK );
				log.info( "The action was changed to check. Table: " + table.toString() );
			}
		} catch ( Exception  e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * This method is used when the bot has got an exception and therefore the whole bot has to be termined.
	 * @throws IOException an IOException about the PipedOutputStream
	 */
	private void exit() throws IOException
	{
		out.write(1);
		out.flush();
		out.close();
	}
	
}
