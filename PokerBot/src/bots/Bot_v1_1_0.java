package bots;

import gameBasics.Action;
import gameBasics.PlayerYou;
import handHistory.HandHistory;
import handHistory.HandHistory.GameType;
import handHistory.HandHistory.Limit;

import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import creatorHandHistory.CreatorHHWPClass;

import other.Tools;
import parser.ParserCreatorWinnerPoker4Tables;
import strategy.strategyPokerStrategy.StrategyOne;

public class Bot_v1_1_0 extends Thread

{
	
	/**
	 * out is for the communication with the Thread Interrupter
	 */
	public PipedOutputStream out;
	
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
	private File hhFile;
	
	/**
	 * The file in which the parser writes for parsing the hand history.
	 */
	private File parserFile;
	
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
	private BufferedImage PICTURE_BUTTON_CKECK_CALL;
	
	/**
	 * It is the picture of the bet-/raise-button.
	 */
	private BufferedImage PICTURE_BUTTON_BET_RAISE;
	
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
	 * @deprecated because it is to old and there is a much better strategy
	 */
	public Bot_v1_1_0( ThreadGroup main, String string, PipedOutputStream out, Bot_v1_1_0Tables table )
	{
		super( main, string );
		this.out = out;
		this.table = table;
		allocateTableToRest( table );
		creator = new CreatorHHWPClass( hhFile, SPACE_NOTEPAD, SPACE_TABLECHAT );
	}
	
	
	@Override
	public void run()
	{
		try {
			Handler handler = new FileHandler( logFile.getAbsolutePath() );
			log.addHandler( handler );
			
			String namePlayerYou = "walk10er";																	// name player you
			
			Robot r = new Robot();																				// robot
			
			
			JFrame frame = new JFrame( this.table + " - Bot v1.1.0" );											// GUI
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			frame.setBounds( 2000, 100, 500, 500 );
			
			JPanel panel = new JPanel();
			panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS) );
			JLabel label = new JLabel( "<html>This is the graphical representation of the PokerBot with the version number 1.0.0. The bot plays poker on WinnerPoker. " +
					"Please do not shut down the computer and do not do anything else on the computer. If there is a problem, please tell this " +
					"the developer or the owner of the computer. And also do not kill this window or the bot that could cause a financial damage!.</html>" );
			label.setPreferredSize( new Dimension(500, 100) );
			label.setForeground( Color.RED );
			panel.add( label );
			JTextArea text = new JTextArea();
			text.setPreferredSize( new Dimension(500, 400) );
			panel.add( text );
			
			frame.add( panel );
			frame.setVisible( true );
			
			
			while ( true )
			{
				synchronized( r ) { r.wait( 500 ); }
				
				BufferedImage f1 = r.createScreenCapture( SPACE_BUTTON_FOLD );
				BufferedImage f2 = r.createScreenCapture( SPACE_BUTTON_CHECK_CALL );
				BufferedImage f3 = r.createScreenCapture( SPACE_BUTTON_BET_RAISE );
				
				lock.lock();
				if ( Tools.compare(f1, PICTURE_BUTTON_FOLD, 0.65) || Tools.compare(f2, PICTURE_BUTTON_CKECK_CALL, 0.65) || Tools.compare(f3, PICTURE_BUTTON_BET_RAISE, 0.65) )
				{
					log.info( "Beginning creating the hand-history. Table: " + table.toString() );
					try {
						creator.createHH();																			// the creator creates the necessary hand history
						log.info( "Creating of the hand-history was successful. Table: " + table.toString() );
					} catch ( Exception e ) {
						log.log( Level.SEVERE, "Creating of the hand-history was not successful. Table: " + table.toString(), e );
						log.info( "Next attempt to create the hand-history." );
						creator.createHH();
					}
					
					HandHistory hh = new HandHistory();															// hand history
					log.info( "Beginning parsing the hand-history-text-file to a object HandHistory. Table: " + table.toString() );
					try {
						hh = ParserCreatorWinnerPoker4Tables.parserMainCWP( hhFile, parserFile, null, GameType.HOLD_EM, Limit.FIXED_LIMIT, 9, namePlayerYou, PICTURE_SEATS, SPACE_SEATS, true);
						log.info( "Parsing of the hand-history-text-file was successful. Table: " + table.toString() );
					} catch ( Exception e1 ) {
						log.log(Level.SEVERE, "Parsing of the hand-history-text-file was not successfull. The path of the text-file is: "
								+ hhFile.toString() + ". Table: " + table.toString(), e1 );
						sleep(100);
						creator.createHH();
						try {
							hh = ParserCreatorWinnerPoker4Tables.parserMainCWP( hhFile, parserFile, null, GameType.HOLD_EM, Limit.FIXED_LIMIT, 9, namePlayerYou, PICTURE_SEATS, SPACE_SEATS, true);
							log.info( "Parsing of the hand-history-text-file was successful. Table: " + table.toString() );
						} catch ( Exception e2 ) {
							log.log(Level.SEVERE, "Parsing of the hand-history-text-file was not successfull. The path of the text-file is: "
									+ hhFile.toString() + ". Table: " + table.toString(), e2 );
							sleep(100);
							creator.createHH();
						} try {
							hh = ParserCreatorWinnerPoker4Tables.parserMainCWP( hhFile, parserFile, null, GameType.HOLD_EM, Limit.FIXED_LIMIT, 9, namePlayerYou, PICTURE_SEATS, SPACE_SEATS, true);
							log.info( "Parsing of the hand-history-text-file was successful. Table: " + table.toString() );
						} catch ( Exception e3 ) {
							log.log(Level.SEVERE, "Parsing of the hand-history-text-file was not successfull. The path of the text-file is: "
									+ hhFile.toString() + ". Table: " + table.toString(), e3 );
							sleep(100);
							creator.createHH();
						} try {
							hh = ParserCreatorWinnerPoker4Tables.parserMainCWP( hhFile, parserFile, null, GameType.HOLD_EM, Limit.FIXED_LIMIT, 9, namePlayerYou, PICTURE_SEATS, SPACE_SEATS, true);
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
						exit(); }
					
					
					gameBasics.Action action = new gameBasics.Action();											// action
					log.info( "Start getting right Action for the actual situation. Table: " + table.toString() );
					try {
						action.set( StrategyOne.actionFor(hh, you) );
					} catch ( Exception e ) {
						log.log( Level.SEVERE, "Getting the Action for the actual situation was not possible. Table: " + table.toString(), e );
						exit(); }
					
					if ( action.isEmpty() )
						log.log( Level.SEVERE, "There was not any Action for the actual situation. Table: " + table.toString() );
					else
						log.info( String.format("Getting Action for the actual situation was sucessful.%nThe action was: " + action.toString() + ". Table: " + table.toString()) );
					log.info( "End getting Action for the actual game. Table: " + table.toString() );
					
					click( action );
				}
				lock.unlock();
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
		
		if ( table == Bot_v1_1_0Tables.LEFT_UP )
		{
			logFile = new File( "c://pokerBot//bot_v1_1_0//loggingBotTableLeftUp.txt" );
			hhFile = new File( "c://pokerBot//bot_v1_1_0//hhTableLeftUp.txt" );
			parserFile = new File( "c://pokerBot//bot_v1_1_0//parserTableLeftUp.txt" );
			
			SPACE_BUTTON_FOLD = new Rectangle( 436, 537, 104, 35 );
			SPACE_BUTTON_CHECK_CALL = new Rectangle( 553, 537, 105, 38 );
			SPACE_BUTTON_BET_RAISE = new Rectangle( 674, 536, 100, 38 );
			SPACE_TABLECHAT = new Rectangle(15, 548, 265, 49);
			
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
			logFile = new File( "c://pokerBot//bot_v1_1_0//loggingBotTableLeftDown.txt" );
			hhFile = new File( "c://pokerBot//bot_v1_1_0//hhTableLeftDown.txt" );
			parserFile = new File( "c://pokerBot//bot_v1_1_0//parserTableLeftDown.txt" );
			
			SPACE_BUTTON_FOLD = new Rectangle( 400, 1105, 95, 30 );				// the action-buttons and the table chat
			SPACE_BUTTON_CHECK_CALL = new Rectangle( 500, 1105, 100, 30 );
			SPACE_BUTTON_BET_RAISE = new Rectangle( 610, 1105, 100, 30 );
			SPACE_TABLECHAT = new Rectangle( 14, 1105, 328, 50 );
			
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
			logFile = new File( "c://pokerBot//bot_v1_1_0//loggingBotTableRightDown.txt" );
			hhFile = new File( "c://pokerBot//bot_v1_1_0//hhTableRightDown.txt" );
			parserFile = new File( "c://pokerBot//bot_v1_1_0//parserTableRightDown.txt" );
			
			SPACE_BUTTON_FOLD = new Rectangle( 1200, 1100, 100, 35 );
			SPACE_BUTTON_CHECK_CALL = new Rectangle( 1310, 1100, 100, 35 );
			SPACE_BUTTON_BET_RAISE = new Rectangle( 1420, 1100, 100, 35 );
			SPACE_TABLECHAT = new Rectangle( 833, 1142, 322, 51 );
			
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
			logFile = new File( "c://pokerBot//bot_v1_1_0//loggingBotTableRightUp.txt" );
			hhFile = new File( "c://pokerBot//bot_v1_1_0//hhTableRightUp.txt" );
			parserFile = new File( "c://pokerBot//bot_v1_1_0//parserTableRightUp.txt" );
			
			SPACE_BUTTON_FOLD = new Rectangle( 1255, 540, 100, 35 );
			SPACE_BUTTON_CHECK_CALL = new Rectangle( 1375, 540, 100, 32 );
			SPACE_BUTTON_BET_RAISE = new Rectangle( 1490, 540, 100, 45 );
			SPACE_TABLECHAT = new Rectangle( 834, 581, 361, 61 );
			
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
	 * This method is used when the bot has got an exception and therefore the whole bot has to be termined.
	 * @throws IOException an IOException about the PipedOutputStream
	 */
	private void exit() throws IOException
	{
		out.write(1);
		out.flush();
		out.close();
		lock.unlock();
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
	private void click( gameBasics.Action action )
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
			
			if ( Tools.compare(f1, PICTURE_BUTTON_FOLD, 0.8) || Tools.compare(f2, PICTURE_BUTTON_CKECK_CALL, 0.8) || Tools.compare(f3, PICTURE_BUTTON_BET_RAISE, 0.8) )
			{
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

	
}
