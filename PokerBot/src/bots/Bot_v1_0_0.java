package bots;

import gameBasics.*;
import gameBasics.Action;
import handHistory.*;
import parser.*;
import strategyForTexasHoldEmFixedLimit.*;
import other.*;
import java.io.File;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.*;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.Robot;
import java.awt.Point;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * This class implements a bot which plays poker on one table on winner poker (WP).
 * 
 * @author Joschka J. Braun
 *
 */
public class Bot_v1_0_0 extends Thread

{
	
	/**
	 * out is for the communication with the Thread Interrupter
	 */
	public PipedOutputStream out;
	
	/**
	 * That is the logger especially for this 
	 */
	public static final Logger log = Logger.getLogger( Bot_v1_0_0.class.getName() );
	
	/**
	 * That lock do some critical parts of the program safe respective parallel programming.
	 */
	public static final Lock lock = new ReentrantLock();
	
	/**
	 * That is the field where the button for folding in the WinnerPoker-Software is when the window is on the coordinates (0,0)
	 * and has a normal (normal means you did not change) size.
	 */
	public static final Rectangle SPACE_BUTTON_FOLD = new Rectangle( 436, 537, 104, 39 );
	
	/**
	 * That is the field where the button for checking and calling (it depends on the situation) in the PokerStars-Software is
	 * when the window is on the coordinates (0,0) and has a normal (normal means you did not change) size.
	 */
	public static final Rectangle SPACE_BUTTON_CHECK_CALL = new Rectangle( 553, 537, 105, 38 );
	
	/**
	 * That is the field where the button for betting and raising (it depends on the situation) in the PokerStars-Software is
	 * when the window is on the coordinates (0,0) and has a normal (normal means you did not change) size.
	 */
	public static final Rectangle SPACE_BUTTON_BET_RAISE = new Rectangle( 674, 536, 106, 42 );
	
	/**
	 * It is the picture of the fold-button.
	 */
	private static BufferedImage PICTURE_BUTTON_FOLD;
	
	/**
	 * It is the picture of the check-/call-button.
	 */
	private static BufferedImage PICTURE_BUTTON_CKECK_CALL;
	
	/**
	 * It is the picture of the bet-/raise-button.
	 */
	private static BufferedImage PICTURE_BUTTON_BET_RAISE;	
	
	public Bot_v1_0_0 (ThreadGroup main, String string, PipedOutputStream out ) {
		super( main, string );
		this.out = out;
	}
	
	@Override
	public void run()
	{
		try {
			Handler handler = new FileHandler( "c://pokerBot//bot_v1_0_0//loggingBot.txt" );
			log.addHandler( handler );
			
			
			String namePlayerYou = "walk10er";																// the name of the player you are
			
			File f = new File( "c://pokerBot//bot_v1_0_0//heapCreatorHHWP.txt" );													// file of the hand history
			
			PICTURE_BUTTON_FOLD = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_0_0//pictureButtonFold.PNG") );
			PICTURE_BUTTON_CKECK_CALL = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_0_0//pictureButtonCheckCall.PNG") );
			PICTURE_BUTTON_BET_RAISE = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_0_0//pictureButtonBetRaise.PNG") );
			
			Robot r = new Robot();																				// robot
			
			
			JFrame jF = new JFrame( " - Bot v1.0.0" );															// GUI
			jF.setBounds( 2000, 100, 500, 175 );
			jF.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			JLabel jL1 = new JLabel( "<html>This is the graphical representation of the PokerBot with the version number 1.0.0. The bot plays poker on WinnerPoker. " +
					"Please do not shut down the computer and do not do anything else on the computer. If there is a problem, please tell this " +
					"the developer or the owner of the computer. And also do not kill this window or the bot that could cause a financial damage!.</html>" );
			jL1.setForeground( Color.RED );
			jF.add( jL1 );
			jF.setVisible( true );
			
			while ( true )
			{
				synchronized( r ) { r.wait( 500 ); }
				
				BufferedImage f1 = r.createScreenCapture( SPACE_BUTTON_FOLD );
				BufferedImage f2 = r.createScreenCapture( SPACE_BUTTON_CHECK_CALL );
				BufferedImage f3 = r.createScreenCapture( SPACE_BUTTON_BET_RAISE );
				
				lock.lock();
				if ( Other.compare(f1, PICTURE_BUTTON_FOLD, 0.8) || Other.compare(f2, PICTURE_BUTTON_CKECK_CALL, 0.8) || Other.compare(f3, PICTURE_BUTTON_BET_RAISE, 0.8) )
				{
					HandHistory hh = new HandHistory();															// hand history
					log.info( "Beginning parsing the hand-history-text-file to a object HandHistory." );
					try {
						hh = ParserCreatorWinnerPoker1Table.parserMainCWP(f, "Hold'Em", "Fixed Limit", 9, namePlayerYou);
						log.info("Parsing of the hand-history-text-file was successful");
					} catch (Exception e1) {
						lock.unlock();
						log.log(Level.SEVERE, "Parsing of the hand-history-text-file was not successfull. The path of the text-file is: "
								+ new File( "c://pokerBot//bot_v1_0_0//heapParserCWP").toString(), e1);
						sleep(100);
						try {
							hh = ParserCreatorWinnerPoker1Table.parserMainCWP(f, "Hold'Em", "Fixed Limit", 9, namePlayerYou);
							lock.lock();
							log.info("Second try parsing of the hand-history-text-file was successful");
						} catch (Exception e2) {
							log.log( Level.SEVERE, "Parsing of the hand-history-text-file was not successfull. The path of the text-file is: "
									+ new File("c://pokerBot//bot_v1_0_0//heapParserCWP").toString(), e2 );
							sleep(100);
							try {
								hh = ParserCreatorWinnerPoker1Table.parserMainCWP(f, "Hold'Em", "Fixed Limit", 9, namePlayerYou);
								lock.lock();
								log.info("Third try parsing of the hand-history-text-file was successful");
							} catch (Exception e3) {
								log.log(Level.SEVERE, "Parsing of the hand-history-text-file was not successfull. The path of the text-file is: "
												+ new File("c://pokerBot//bot_v1_0_0//heapParserCWP").toString(), e3);
								sleep(100);
								try {
									hh = ParserCreatorWinnerPoker1Table.parserMainCWP(f, "Hold'Em", "Fixed Limit", 9, namePlayerYou);
									lock.lock();
									log.info("Fourth try parsing of the hand-history-text-file was successful");
								} catch (Exception e4) {
									log.log(Level.SEVERE, "Parsing of the hand-history-text-file was not successfull. The path of the text-file is: "
													+ new File("c://pokerBot//bot_v1_0_0//heapParserCWP").toString(), e3);
									exit();
								}
							}
						}
					}
					log.info( "End parsing the hand-history-text-file to a object HandHistory." );
					
					
					PlayerYou you = new PlayerYou();															// player you
					log.info( "Beginning getting PlayerYou." );
					try {
						you.set( hh.getPlayerYou( namePlayerYou ) );
					} catch ( Exception e ) {
						log.log( Level.SEVERE, "Getting the PlayerYou was not successful", e );
						exit(); }
					
					if ( you.isEmpty() )
						log.log( Level.SEVERE, "The hand history does not includes a PlayerYou" );
					else
						log.info( "Getting the PlayerYou was successful" );
					log.info( "End getting PlayerYou" );
					
					
					gameBasics.Action action = new gameBasics.Action();											// action
					log.info( "Start getting right Action for the actual situation" );
					try {
						action.set( StrategyOne.actionFor(hh, you) );
					} catch ( Exception e ) {
						log.log( Level.SEVERE, "Getting the Action for the actual situation was not possible.", e );
						exit(); }
					
					if ( action.isEmpty() )
						log.log( Level.SEVERE, "There was not any Action for the actual situation" );
					else
						log.info( String.format("Getting Action for the actual situation was sucessful.%nThe action was: " + action.toString()) );
					log.info( "End getting Action for the actual game" );
					
					click( action );
				}
				lock.unlock();
			}
		} catch ( Exception e ) {
			System.err.println( "There was a problem which was not in the logged part!" );
			e.printStackTrace();
			
			try {
			out.write( 1 ); out.flush(); out.close();
			} catch ( Exception e2 ) {
				System.err.println( "There was a problem with writing, flushing or closing this.out!" );
				e.printStackTrace();
				this.getThreadGroup().interrupt();
			}
	}	}
	
	/**
	 * Returns for each action the right button.
	 * 
	 * @param a the action for which the button is searched
	 * @return the button for the chosen action
	 */
	public static Rectangle actionToButton( gameBasics.Action a )
	{
		Rectangle r = new Rectangle();
		
		if ( a.actionName.equals( "fold" ) )
			r = Bot_v1_0_0.SPACE_BUTTON_FOLD;
		else if ( a.actionName.equals( "check" ) || a.actionName.equals( "call" ) )
			r = Bot_v1_0_0.SPACE_BUTTON_CHECK_CALL;
		else if ( a.actionName.equals( "bet" ) || a.actionName.equals( "raise" ) )
			r = Bot_v1_0_0.SPACE_BUTTON_BET_RAISE;
		else
			throw new IllegalArgumentException( "It was not possible to assign a button to the chosen action. The action was: " + a.toString() );
		
		return r;
	}
	
	/**
	 * This method clicks on the correct button to the action act.
	 * 
	 * @param action the action
	 */
	public static void click( gameBasics.Action action )
	{
		try {
			Rectangle re = actionToButton( action );													// mouse clicking
			Point p = Other.createPointIn( re );
			Robot r = new Robot();
//			synchronized ( r ) { r.wait( (long) Math.random() * 100 ); }				// thereby the bot does not react at once and the bot is not so much conspicuous
			sleep( (long) Math.random() * 10000  );
			r.mouseMove(p.x, p.y);
			r.mousePress( InputEvent.BUTTON1_DOWN_MASK );
			r.mouseRelease( InputEvent.BUTTON1_DOWN_MASK );
			
			synchronized ( r ) { r.wait( 75 ); }								// this part e.g. the situation, when the strategy return folding but also checking is possible
			BufferedImage f1 = r.createScreenCapture( SPACE_BUTTON_FOLD );
			BufferedImage f2 = r.createScreenCapture( SPACE_BUTTON_CHECK_CALL );
			BufferedImage f3 = r.createScreenCapture( SPACE_BUTTON_BET_RAISE );
			PICTURE_BUTTON_FOLD = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_0_0//pictureButtonFold.PNG") );
			PICTURE_BUTTON_CKECK_CALL = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_0_0//pictureButtonCheckCall.PNG") );
			PICTURE_BUTTON_BET_RAISE = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_0_0//pictureButtonBetRaise.PNG") );
			
			if ( Other.compare(f1, PICTURE_BUTTON_FOLD, 0.8) || Other.compare(f2, PICTURE_BUTTON_CKECK_CALL, 0.8) || Other.compare(f3, PICTURE_BUTTON_BET_RAISE, 0.8) )
			{
				gameBasics.Action act = new Action( "check" );
				re = actionToButton( act );
				p = Other.createPointIn( re );
				r.mouseMove(p.x, p.y);
				r.mousePress( InputEvent.BUTTON1_DOWN_MASK );
				r.mouseRelease( InputEvent.BUTTON1_DOWN_MASK );
				log.info( "The action was changed to check." );
			}
		} catch ( Exception  e) {
			e.printStackTrace();
	}	}
	
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
	
}
