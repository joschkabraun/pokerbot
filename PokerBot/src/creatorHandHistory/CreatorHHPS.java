package creatorHandHistory;

import other.Tools;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.Point;


/**
 * This class implements the program which creates the hand history (HH) of poker stars (PS) and safes it in a file
 * because PS does not create a hand history while the game. The created hand history will be a bit different to the
 * normal hand history of poker stars so in the package parser is one class else for parsing this hand history.
 * 
 * ATTENTION for using this class because if the table chat is not as full as it last to the ground of the
 * table chat window, the program does not work correct. If the PokerBot uses this class and you start the program,
 * check first whether the table chat is enough full otherwise you play so long it is enough full. 
 * 
 * @author Joschka J. Braun
 *
 */
public class CreatorHHPS implements Runnable

{
	
	final Lock lock = new ReentrantLock();
	
	public void run()
	{		
		File f = new File( "c://pokerBot//bot_v1_0_0//heapCreatorHHPS.txt" );			// This file does not exist in  bot version 1.0.0!! Because the bot does not play
		Robot r;																		// at PokerStars
		Rectangle rect = new Rectangle(826, 237, 894, 6);
		String lStart = "";
		String[] lastS = new String[ 10 ];
		for ( int i = 0; i < 10; i++ )
			lastS[ i ] = "test";
		
		try {
			r = new Robot();
			
			BufferedImage bfStart = r.createScreenCapture( rect );
			
			while ( true )
			{
				lock.lock();
				BufferedImage bfNext = r.createScreenCapture( rect );
				
				if ( ! Tools.compare(bfStart, bfNext, 1) )
				{
					Point p = Tools.createPointIn( rect );
					r.mouseMove( p.x, p.y );								// getting the new text in the clipboard
					r.mousePress( InputEvent.BUTTON1_DOWN_MASK );
					Point p2 = Tools.createPointIn( new Rectangle( 836, 121, 1048, 5 ) );
					r.mouseMove( p2.x, p2.y );
					r.mouseRelease( InputEvent.BUTTON1_DOWN_MASK );
					r.keyPress( KeyEvent.VK_CONTROL );
					r.keyPress( KeyEvent.VK_C );
					r.keyRelease( KeyEvent.VK_CONTROL );
					r.keyRelease( KeyEvent.VK_C );
					r.mousePress( InputEvent.BUTTON1_DOWN_MASK );
					r.mouseRelease( InputEvent.BUTTON1_DOWN_MASK );
					
					synchronized( r ) { r.wait( 30 ); }			// It takes some time until the system has had time to update the clipboard.
					
					String line = Tools.getClipboardS();
					Tools.clearClipboard();
					
					String[] lSplit = line.split( "\n" );
					
					String[] rest = notIncludedStrings(lastS, lSplit);
					
					if ( line.startsWith( "Dealer:" ) )
						if ( ! line.equals( lStart ) )
							for ( String s : rest )
							{
								FileWriter writer = new FileWriter( f, true );
								writer.write( String.format( s.substring(8) + "%n" ) );
								writer.flush();
								writer.close();
							}
					
					lastS = lSplit;
					lStart = line;
				}
				
				bfStart = bfNext;
				lock.unlock();
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Returns the strings which are in b but not in a. Because this method has an exception, this method is not general (respectively this method is in the class Other).
	 * The exception is that if there is a string twice (or more) in a and one (or more but lower than in a) time(s) in b, the algorithm will pack the difference of the times
	 * of entries in b and the times in a in the result.
	 * 
	 * @param a the array of strings you deduct from b
	 * @param b the array of strings from which you want know which strings are in b but not in a
	 * @return the strings in b and not in a or twice (or more) in a
	 */
	public static String[] notIncludedStrings( String[] a, String[] b )
	{
		int index = -1;
		int arg = 0;
		
		String[] aC = Arrays.copyOf(a, a.length);
		String[] ret = Arrays.copyOf(b, b.length);
		
		for ( int i = 0; i < b.length; i++ )
			if ( i == b.length -1 )
				return ret;
			else if ( Tools.indexOf(a, b[i]) > -1 )
			{
				index = Tools.indexOf(a,  b[i]);
				arg = i;
				ret = Arrays.copyOfRange( ret, i, ret.length );
				aC = Arrays.copyOfRange( aC, index, aC.length );
				break;
			}
		
		if ( index > -1 )
			for ( int i = arg; i < b.length; i++ )
				if ( Tools.indexOf(aC, b[i]) + index + i == index + i - arg )			// the addition on the left side is for having the index in a not in aC
				{
					aC = Arrays.copyOfRange( aC, 1, aC.length );
					ret = Arrays.copyOfRange( ret,  1, ret.length );
				}
				else
					break;
		
		return ret;
	}
	
}
