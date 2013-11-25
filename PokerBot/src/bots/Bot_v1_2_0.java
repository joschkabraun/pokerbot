package bots;

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

import gameBasics.Action;
import gameBasics.PlayerYou;
import handHistory.HandHistory;
import handHistory.HandHistory.GameType;
import handHistory.HandHistory.Limit;
import other.Tools;
import parser.ParserCreatorWinnerPoker4Tables;
import strategy.strategyPokerChallenge.interfacesToPokerChallenge.StrategyTwo;
import strategy.strategyPokerChallenge.ringclient.ClientRingDynamics;

import creatorHandHistory.CreatorHHWPClass;

public class Bot_v1_2_0  extends Thread {
	
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
	 * The 	arrangement of the seats on the table;
	 */
	private Rectangle[] SPACE_SEATS;
	
	/**
	 * The pictures of the seats.
	 */
	private BufferedImage[] PICTURE_SEATS;
	
	/**
	 * The spaces of all seats. The array is arranged by Bot_v1_1_0Tables.LEFT_DOWN, Bot_v1_1_0Tables.LEFT_UP, Bot_v1_1_0Tables.RIGHT_UP, Bot_v1_1_0Tables.RIGHT_DOWN.
	 */
	private Rectangle[][] spaceSeats;
	
	/**
	 * The pictures of all seats. The array is arranged by Bot_v1_1_0Tables.LEFT_DOWN, Bot_v1_1_0Tables.LEFT_UP, Bot_v1_1_0Tables.RIGHT_UP, Bot_v1_1_0Tables.RIGHT_DOWN.
	 */
	private BufferedImage[][] pictureSeats;
	
	public Bot_v1_2_0( ThreadGroup main, String string, PipedOutputStream out, Bot_v1_1_0Tables table, String namePlayerYou, boolean tableWasRemoved )
	{
		super( main, string );
		this.out = out;
		this.table = table;
		this.tableWasRemoved = tableWasRemoved;
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
				BufferedImage f1 = r.createScreenCapture( SPACE_BUTTON_FOLD );
				BufferedImage f2 = r.createScreenCapture( SPACE_BUTTON_CHECK_CALL );
				BufferedImage f3 = r.createScreenCapture( SPACE_BUTTON_BET_RAISE );
				
				if ( (!this.tableWasRemoved && (Tools.compare(f1,PICTURE_BUTTON_FOLD,0.65) || Tools.compare(f2, PICTURE_BUTTON_CKECK_CALL, 0.65) || Tools.compare(f3, PICTURE_BUTTON_BET_RAISE, 0.65))) ||
				 (this.tableWasRemoved && (Tools.compareSimilar(f1,PICTURE_BUTTON_FOLD,0.05) || Tools.compareSimilar(f2, PICTURE_BUTTON_CKECK_CALL, 0.05) || Tools.compareSimilar(f3, PICTURE_BUTTON_BET_RAISE, 0.05))) ) {
					lock.lock();
					log.info( "Beginning creating the hand-history. Table: " + table.toString() );
					try {
						creator.createHH();																			// the creator creates the necessary hand history
						log.info( "Creating of the hand-history was successful. Table: " + table.toString() );
					} catch ( Exception e ) {
						log.log( Level.SEVERE, "Creating of the hand-history was not successful. Table: " + table.toString(), e );
						log.info( "Next attempt to create the hand-history." );
						creator.createHH();
					}
					lock.unlock();
					
					
					HandHistory hh = new HandHistory();															// hand history
					log.info( "Beginning parsing the hand-history-text-file to a object HandHistory. Table: " + table.toString() );
					try {
						hh = ParserCreatorWinnerPoker4Tables.parserMainCWP( hhFile, parserFileHH, sesFile, GameType.HOLD_EM, Limit.FIXED_LIMIT, 9, namePlayerYou, PICTURE_SEATS, SPACE_SEATS, this.tableWasRemoved);
						log.info( "Parsing of the hand-history-text-file was successful. Table: " + table.toString() );
					} catch ( Exception e1 ) {
						log.log(Level.SEVERE, "Parsing of the hand-history-text-file was not successfull. The path of the text-file is: "
								+ hhFile.toString() + ". Table: " + table.toString(), e1 );
						sleep(100);
						try {
							hh = ParserCreatorWinnerPoker4Tables.parserMainCWP( hhFile, parserFileHH, sesFile, GameType.HOLD_EM, Limit.FIXED_LIMIT, 9, namePlayerYou, PICTURE_SEATS, SPACE_SEATS, this.tableWasRemoved);
							log.info( "Parsing of the hand-history-text-file was successful. Table: " + table.toString() );
						} catch ( Exception e2 ) {
							log.log(Level.SEVERE, "Parsing of the hand-history-text-file was not successfull. The path of the text-file is: "
									+ hhFile.toString() + ". Table: " + table.toString(), e2 );
							sleep(100);
						} try {
							hh = ParserCreatorWinnerPoker4Tables.parserMainCWP( hhFile, parserFileHH, sesFile, GameType.HOLD_EM, Limit.FIXED_LIMIT, 9, namePlayerYou, PICTURE_SEATS, SPACE_SEATS, this.tableWasRemoved);
							log.info( "Parsing of the hand-history-text-file was successful. Table: " + table.toString() );
						} catch ( Exception e3 ) {
							log.log(Level.SEVERE, "Parsing of the hand-history-text-file was not successfull. The path of the text-file is: "
									+ hhFile.toString() + ". Table: " + table.toString(), e3 );
							sleep(100);
						} try {
							hh = ParserCreatorWinnerPoker4Tables.parserMainCWP( hhFile, parserFileHH, sesFile, GameType.HOLD_EM, Limit.FIXED_LIMIT, 9, namePlayerYou, PICTURE_SEATS, SPACE_SEATS, this.tableWasRemoved);
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
						
						strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createPrivateHands(source, parsers, sesFiles, tables, gameType, limit, maxSeatOnTable, namePlayerYou, pictureSeats, spaceSeats, this.tableWasRemoved);
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
						
						log.info("Creating the TU-Darmstadt's AKI-RealBot datastructure was succesful. Table: " + table.toString());
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
							
							strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createPrivateHands(source, parsers, sesFiles, tables, gameType, limit, maxSeatOnTable, namePlayerYou, pictureSeats, spaceSeats, this.tableWasRemoved);
							strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createCONSTANT(hh);
							crd = strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createRingDynamics(hh, hh.getPlayerYou("walk10er"));
							strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createHistory(hh, you);
							
							log.info("Creating the TU-Darmstadt's AKI-RealBot datastructure was succesful. Table: " + table.toString());
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
								
								strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createPrivateHands(source, parsers, sesFiles, tables, gameType, limit, maxSeatOnTable, namePlayerYou, pictureSeats, spaceSeats, this.tableWasRemoved);
								strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createCONSTANT(hh);
								crd = strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createRingDynamics(hh, hh.getPlayerYou("walk10er"));
								strategy.strategyPokerChallenge.interfacesToPokerChallenge.HHToTUDBotHistory.createHistory(hh, you);
								
								log.info("Creating the TU-Darmstadt's AKI-RealBot datastructure was succesful. Table: " + table.toString());
							} catch (Exception e33) {
								log.log(Level.SEVERE, "Creating the TU-Darmstadt's AKI-RealBot datastructure was not successful! Table: " + table.toString(), e);
								exit();
							}
						}
					}
					
					gameBasics.Action action = new gameBasics.Action();											// action
					log.info( "Start getting right Action for the actual situation. Table: " + table.toString() );
					try {
						action.set(StrategyTwo.actionFor(hh, you, sesFile, PICTURE_SEATS, SPACE_SEATS, crd, this.tableWasRemoved));
					} catch ( Exception e ) {
						log.log( Level.SEVERE, "Getting the Action for the actual situation was not possible. Table: " + table.toString(), e );
						log.info("The second attempt to get the Action. Table: " + table.toString());
						try {
							action.set(StrategyTwo.actionFor(hh, you, sesFile, PICTURE_SEATS, SPACE_SEATS, crd, this.tableWasRemoved));
						} catch ( Exception e2 ) {
							log.log( Level.SEVERE, "Getting the Action for the actual situation was not possible. Table: " + table.toString(), e );
							log.info("The third attempt to get the Action. Table: " + table.toString());
							try {
								action.set(StrategyTwo.actionFor(hh, you, sesFile, PICTURE_SEATS, SPACE_SEATS, crd, this.tableWasRemoved));
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
						o.wait(1000);
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
		SPACE_NOTEPAD = new Rectangle( 2010, 210, 480, 380 );
		spaceSeats = new Rectangle[4][9];
		pictureSeats = new BufferedImage[4][9];
		
		try {
			spaceSeats[0][0] = new Rectangle( 113, 729, 50, 50 );			// left down
			spaceSeats[0][1] = new Rectangle( 241, 684, 52, 52 );
			spaceSeats[0][2] = new Rectangle( 442, 688, 50, 50 );
			spaceSeats[0][3] = new Rectangle( 575, 727, 50, 50 );
			spaceSeats[0][4] = new Rectangle( 630, 825, 51, 50 );
			spaceSeats[0][5] = new Rectangle( 575, 950, 55, 50 );
			spaceSeats[0][6] = new Rectangle( 432, 973, 50, 50 );
			spaceSeats[0][7] = new Rectangle( 254, 974, 50, 50 );
			spaceSeats[0][8] = new Rectangle( 110, 950, 50, 50 );
			pictureSeats[0][0] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureSeat1.PNG") );
			pictureSeats[0][1] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureSeat2.PNG") );
			pictureSeats[0][2] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureSeat3.PNG") );
			pictureSeats[0][3] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureSeat4.PNG") );
			pictureSeats[0][4] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureSeat5.PNG") );
			pictureSeats[0][5] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureSeat6.PNG") );
			pictureSeats[0][6] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureSeat7.PNG") );
			pictureSeats[0][7] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureSeat8.PNG") );
			pictureSeats[0][8] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureSeat9.PNG") );
			
			spaceSeats[1][0] = new Rectangle( 128, 111, 51, 55);			// left up
			spaceSeats[1][1] = new Rectangle( 267, 66, 65, 51 );
			spaceSeats[1][2] = new Rectangle( 490, 68, 50, 52 );
			spaceSeats[1][3] = new Rectangle( 637, 113, 33, 55 );
			spaceSeats[1][4] = new Rectangle( 700, 223, 50, 50 );
			spaceSeats[1][5] = new Rectangle( 639, 359, 52, 54 );
			spaceSeats[1][6] = new Rectangle( 480, 389, 51, 49 );
			spaceSeats[1][7] = new Rectangle( 283, 386, 50, 55 );
			spaceSeats[1][8] = new Rectangle( 128, 357, 52, 53 );
			pictureSeats[1][0] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureSeat1.PNG") );
			pictureSeats[1][1] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureSeat2.PNG") );
			pictureSeats[1][2] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureSeat3.PNG") );
			pictureSeats[1][3] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureSeat4.PNG") );
			pictureSeats[1][4] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureSeat5.PNG") );
			pictureSeats[1][5] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureSeat6.PNG") );
			pictureSeats[1][6] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureSeat7.PNG") );
			pictureSeats[1][7] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureSeat8.PNG") );
			pictureSeats[1][8] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureSeat9.PNG") );
			
			spaceSeats[2][0] = new Rectangle( 942, 120, 58, 57 );			// right up
			spaceSeats[2][1] = new Rectangle( 1083, 76, 54, 52 );
			spaceSeats[2][2] = new Rectangle( 1305, 75, 50, 54 );
			spaceSeats[2][3] = new Rectangle( 1453, 124, 54, 54 );
			spaceSeats[2][4] = new Rectangle( 1515, 232, 53, 53 );
			spaceSeats[2][5] = new Rectangle( 1456, 368, 51, 53 );
			spaceSeats[2][6] = new Rectangle( 1295, 394, 50, 56 );
			spaceSeats[2][7] = new Rectangle( 1097, 395, 51, 57 );
			spaceSeats[2][8] = new Rectangle( 943, 367, 57, 53 );
			pictureSeats[2][0] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureSeat1.PNG") );
			pictureSeats[2][1] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureSeat2.PNG") );
			pictureSeats[2][2] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureSeat3.PNG") );
			pictureSeats[2][3] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureSeat4.PNG") );
			pictureSeats[2][4] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureSeat5.PNG") );
			pictureSeats[2][5] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureSeat6.PNG") );
			pictureSeats[2][6] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureSeat7.PNG") );
			pictureSeats[2][7] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureSeat8.PNG") );
			pictureSeats[2][8] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureSeat9.PNG") );
			
			spaceSeats[3][0] = new Rectangle( 933, 727, 47, 50 );			// right down
			spaceSeats[3][1] = new Rectangle( 1055, 690, 55, 45 );
			spaceSeats[3][2] = new Rectangle( 1253, 690, 46, 48 );
			spaceSeats[3][3] = new Rectangle( 1385, 735, 55, 42 );
			spaceSeats[3][4] = new Rectangle( 1441, 826, 46, 49 );
			spaceSeats[3][5] = new Rectangle( 1385, 952, 48, 44 );
			spaceSeats[3][6] = new Rectangle( 1241, 974, 50, 47 );
			spaceSeats[3][7] = new Rectangle( 1068, 973, 50, 50 );
			spaceSeats[3][8] = new Rectangle( 929, 950, 51, 50 );
			pictureSeats[3][0] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureSeat1.PNG") );
			pictureSeats[3][1] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureSeat2.PNG") );
			pictureSeats[3][2] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureSeat3.PNG") );
			pictureSeats[3][3] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureSeat4.PNG") );
			pictureSeats[3][4] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureSeat5.PNG") );
			pictureSeats[3][5] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureSeat6.PNG") );
			pictureSeats[3][6] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureSeat7.PNG") );
			pictureSeats[3][7] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureSeat8.PNG") );
			pictureSeats[3][8] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureSeat9.PNG") );						
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		if ( table == Bot_v1_1_0Tables.LEFT_UP )
		{
			logFile = new File( "c://pokerBot//bot_v1_2_0//loggingBotTableLeftUp.txt" );
			hhFile = new File( "c://pokerBot//bot_v1_2_0//hhTableLeftUp.txt" );
			parserFileHH = new File( "c://pokerBot//bot_v1_2_0//parserTableLeftUp.txt" );
			sesFile = new File("c://pokerBot//bot_v1_2_0//sessionalHHTableLeftUp.txt");
			
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
			
			SPACE_SEATS = new Rectangle[ 9 ];
			SPACE_SEATS[ 0 ] = new Rectangle( 128, 111, 51, 55);
			SPACE_SEATS[ 1 ] = new Rectangle( 267, 66, 65, 51 );
			SPACE_SEATS[ 2 ] = new Rectangle( 490, 68, 50, 52 );
			SPACE_SEATS[ 3 ] = new Rectangle( 637, 113, 33, 55 );
			SPACE_SEATS[ 4 ] = new Rectangle( 700, 223, 50, 50 );
			SPACE_SEATS[ 5 ] = new Rectangle( 639, 359, 52, 54 );
			SPACE_SEATS[ 6 ] = new Rectangle( 480, 389, 51, 49 );
			SPACE_SEATS[ 7 ] = new Rectangle( 283, 386, 50, 55 );
			SPACE_SEATS[ 8 ] = new Rectangle( 128, 357, 52, 53 );
			
			try {
				PICTURE_BUTTON_FOLD = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureButtonFold.PNG") );
				PICTURE_BUTTON_CKECK_CALL = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureButtonCheckCall.PNG") );
				PICTURE_BUTTON_BET_RAISE = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureButtonBetRaise.PNG") );
				
				PICTURE_SEATS = new BufferedImage[ 9 ];
				PICTURE_SEATS[ 0 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureSeat1.PNG") );
				PICTURE_SEATS[ 1 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureSeat2.PNG") );
				PICTURE_SEATS[ 2 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureSeat3.PNG") );
				PICTURE_SEATS[ 3 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureSeat4.PNG") );
				PICTURE_SEATS[ 4 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureSeat5.PNG") );
				PICTURE_SEATS[ 5 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureSeat6.PNG") );
				PICTURE_SEATS[ 6 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureSeat7.PNG") );
				PICTURE_SEATS[ 7 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureSeat8.PNG") );
				PICTURE_SEATS[ 8 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftUp//pictureSeat9.PNG") );

			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		else if ( table == Bot_v1_1_0Tables.LEFT_DOWN )
		{
			logFile = new File( "c://pokerBot//bot_v1_2_0//loggingBotTableLeftDown.txt" );
			hhFile = new File( "c://pokerBot//bot_v1_2_0//hhTableLeftDown.txt" );
			parserFileHH = new File( "c://pokerBot//bot_v1_2_0//parserTableLeftDown.txt" );
			sesFile = new File("c://pokerBot//bot_v1_2_0//sessionalHHTableLeftDown.txt");
			
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
			
			SPACE_SEATS = new Rectangle[ 9 ];
			SPACE_SEATS[ 0 ] = new Rectangle( 113, 729, 50, 50 );
			SPACE_SEATS[ 1 ] = new Rectangle( 241, 684, 52, 52 );
			SPACE_SEATS[ 2 ] = new Rectangle( 442, 688, 50, 50 );
			SPACE_SEATS[ 3 ] = new Rectangle( 575, 727, 50, 50 );
			SPACE_SEATS[ 4 ] = new Rectangle( 630, 825, 51, 50 );
			SPACE_SEATS[ 5 ] = new Rectangle( 575, 950, 55, 50 );
			SPACE_SEATS[ 6 ] = new Rectangle( 432, 973, 50, 50 );
			SPACE_SEATS[ 7 ] = new Rectangle( 254, 974, 50, 50 );
			SPACE_SEATS[ 8 ] = new Rectangle( 110, 950, 50, 50 );
			
			try {
				PICTURE_BUTTON_FOLD = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureButtonFold.PNG") );
				PICTURE_BUTTON_CKECK_CALL = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown/pictureButtonCheckCall.PNG") );
				PICTURE_BUTTON_BET_RAISE = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureButtonBetRaise.PNG") );
				
				PICTURE_SEATS = new BufferedImage[ 9 ];
				PICTURE_SEATS[ 0 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureSeat1.PNG") );
				PICTURE_SEATS[ 1 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureSeat2.PNG") );
				PICTURE_SEATS[ 2 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureSeat3.PNG") );
				PICTURE_SEATS[ 3 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureSeat4.PNG") );
				PICTURE_SEATS[ 4 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureSeat5.PNG") );
				PICTURE_SEATS[ 5 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureSeat6.PNG") );
				PICTURE_SEATS[ 6 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureSeat7.PNG") );
				PICTURE_SEATS[ 7 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureSeat8.PNG") );
				PICTURE_SEATS[ 8 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableLeftDown//pictureSeat9.PNG") );
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		else if ( table == Bot_v1_1_0Tables.RIGHT_DOWN )
		{
			logFile = new File( "c://pokerBot//bot_v1_2_0//loggingBotTableRightDown.txt" );
			hhFile = new File( "c://pokerBot//bot_v1_2_0//hhTableRightDown.txt" );
			parserFileHH = new File( "c://pokerBot//bot_v1_2_0//parserTableRightDown.txt" );
			sesFile = new File("c://pokerBot//bot_v1_2_0//sessionalHHTableRightDown.txt");
			
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
			
			SPACE_SEATS = new Rectangle[ 9 ];
			SPACE_SEATS[ 0 ] = new Rectangle( 933, 727, 47, 50 );
			SPACE_SEATS[ 1 ] = new Rectangle( 1055, 690, 55, 45 );
			SPACE_SEATS[ 2 ] = new Rectangle( 1253, 690, 46, 48 );
			SPACE_SEATS[ 3 ] = new Rectangle( 1385, 735, 55, 42 );
			SPACE_SEATS[ 4 ] = new Rectangle( 1441, 826, 46, 49 );
			SPACE_SEATS[ 5 ] = new Rectangle( 1385, 952, 48, 44 );
			SPACE_SEATS[ 6 ] = new Rectangle( 1241, 974, 50, 47 );
			SPACE_SEATS[ 7 ] = new Rectangle( 1068, 973, 50, 50 );
			SPACE_SEATS[ 8 ] = new Rectangle( 929, 950, 51, 50 );
			
			try {
				PICTURE_BUTTON_FOLD = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureButtonFold.PNG") );
				PICTURE_BUTTON_CKECK_CALL = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureButtonCheckCall.PNG") );
				PICTURE_BUTTON_BET_RAISE = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureButtonBetRaise.PNG") );
				
				PICTURE_SEATS = new BufferedImage[ 9 ];
				PICTURE_SEATS[ 0 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureSeat1.PNG") );
				PICTURE_SEATS[ 1 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureSeat2.PNG") );
				PICTURE_SEATS[ 2 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureSeat3.PNG") );
				PICTURE_SEATS[ 3 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureSeat4.PNG") );
				PICTURE_SEATS[ 4 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureSeat5.PNG") );
				PICTURE_SEATS[ 5 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureSeat6.PNG") );
				PICTURE_SEATS[ 6 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureSeat7.PNG") );
				PICTURE_SEATS[ 7 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureSeat8.PNG") );
				PICTURE_SEATS[ 8 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightDown//pictureSeat9.PNG") );
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		else if ( table == Bot_v1_1_0Tables.RIGHT_UP )
		{
			logFile = new File( "c://pokerBot//bot_v1_2_0//loggingBotTableRightUp.txt" );
			hhFile = new File( "c://pokerBot//bot_v1_2_0//hhTableRightUp.txt" );
			parserFileHH = new File( "c://pokerBot//bot_v1_2_0//parserTableRightUp.txt" );
			sesFile = new File("c://pokerBot//bot_v1_2_0//sessionalHHTableRightUp.txt");
			
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
			
			SPACE_SEATS = new Rectangle[ 9 ];
			SPACE_SEATS[ 0 ] = new Rectangle( 942, 120, 58, 57 );
			SPACE_SEATS[ 1 ] = new Rectangle( 1083, 76, 54, 52 );
			SPACE_SEATS[ 2 ] = new Rectangle( 1305, 75, 50, 54 );
			SPACE_SEATS[ 3 ] = new Rectangle( 1453, 124, 54, 54 );
			SPACE_SEATS[ 4 ] = new Rectangle( 1515, 232, 53, 53 );
			SPACE_SEATS[ 5 ] = new Rectangle( 1456, 368, 51, 53 );
			SPACE_SEATS[ 6 ] = new Rectangle( 1295, 394, 50, 56 );
			SPACE_SEATS[ 7 ] = new Rectangle( 1097, 395, 51, 57 );
			SPACE_SEATS[ 8 ] = new Rectangle( 943, 367, 57, 53 );
			
			try {
				PICTURE_BUTTON_FOLD = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureButtonFold.PNG") );
				PICTURE_BUTTON_CKECK_CALL = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureButtonCheckCall.PNG") );
				PICTURE_BUTTON_BET_RAISE = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureButtonBetRaise.PNG") );
				
				PICTURE_SEATS = new BufferedImage[ 9 ];
				PICTURE_SEATS[ 0 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureSeat1.PNG") );
				PICTURE_SEATS[ 1 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureSeat2.PNG") );
				PICTURE_SEATS[ 2 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureSeat3.PNG") );
				PICTURE_SEATS[ 3 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureSeat4.PNG") );
				PICTURE_SEATS[ 4 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureSeat5.PNG") );
				PICTURE_SEATS[ 5 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureSeat6.PNG") );
				PICTURE_SEATS[ 6 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureSeat7.PNG") );
				PICTURE_SEATS[ 7 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureSeat8.PNG") );
				PICTURE_SEATS[ 8 ] = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_1_0//tableRightUp//pictureSeat9.PNG") );
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
