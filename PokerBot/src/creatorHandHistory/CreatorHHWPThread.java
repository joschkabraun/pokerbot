package creatorHandHistory;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.PipedOutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import other.Other;

/**
 * This class implements the program which creates the hand history (HH) of winner poker (WP) and safes it in a file
 * because WP does not create a hand history while the game. The created hand history will be a bit different to the
 * normal hand history of winner poker so in the package parser is one class else for parsing this hand history.
 * 
 * The difference of this class to the class CreatorHHWPClass is, that this class creates the whole time HHs and
 * CreatorHHWPClass just creates when it is used.
 * 
 * @author Joschka J. Braun
 *
 */
public class CreatorHHWPThread extends Thread implements Runnable

{
	
	/**
	 * out is for the communication with the Thread Interrupter
	 */
	public PipedOutputStream out;
	
	final Lock lock = new ReentrantLock();
	
	private Rectangle rectNP;
	
	private File f;
	
	public CreatorHHWPThread(ThreadGroup main, String string, PipedOutputStream out, Rectangle rect, File f ) {
		super( main, string );
		this.out = out;
		this.rectNP = rect;
		this.f = f;
	}
	
	public void run()
	{		
		File f = this.f;
		
		Rectangle rectBI = new Rectangle(14, 597, 366, 5);		// BI = buffered image
		Rectangle rectTC = new Rectangle(15, 548, 265, 49);		// TC = table chat
		Rectangle rectNP = this.rectNP;							// NP = notepad
		
		String[] lastS = new String[ 4 ];
		for ( int i = 0; i < 4; i++ )
			lastS[ i ] = "test";
		
		try {			
			Robot r = new Robot();
			
			BufferedImage bfStart = r.createScreenCapture( rectBI );
			
			int d = 1;
			int d2 = 1;
			int g = 1;
			int e = 1;
			
			while ( true )
			{
				lock.lock();
				BufferedImage bfNext = r.createScreenCapture( rectBI );
				
				if ( ! Other.compare(bfStart, bfNext, 0.975) )
				{
					Point p  = Other.createPointIn( rectTC );
					r.mouseMove( p.x,  p.y );								// start getting the whole text in the clipboard
					r.mousePress( InputEvent.BUTTON1_DOWN_MASK );
					r.mouseRelease( InputEvent.BUTTON1_DOWN_MASK );
					r.keyPress( KeyEvent.VK_CONTROL );
					r.keyPress( KeyEvent.VK_A );
					r.keyRelease( KeyEvent.VK_A );
					r.keyPress( KeyEvent.VK_C );
					r.keyRelease( KeyEvent.VK_CONTROL );
					r.keyRelease( KeyEvent.VK_C );							// end getting the new text in the clipboard
					
					synchronized( r ) { r.wait( 30 ); }			// It takes some time until the system has had time to update the clipboard.
					
					String line = Other.getClipboardByNotepadS( rectNP );
					
					for ( int i = 0; i < 10; i++ )							// sometimes is the creator to fast and the new line is not already there
					{
						int h = 1;
						if ( line.equals( "" ) )
						{
							synchronized( r ) { r.wait( 30 ); }
							
							Point p2  = Other.createPointIn( rectTC );
							r.mouseMove( p2.x,  p2.y );								// start getting the whole text in the clipboard
							r.mousePress( InputEvent.BUTTON1_DOWN_MASK );
							r.mouseRelease( InputEvent.BUTTON1_DOWN_MASK );
							r.keyPress( KeyEvent.VK_CONTROL );
							r.keyPress( KeyEvent.VK_A );
							r.keyRelease( KeyEvent.VK_A );
							r.keyPress( KeyEvent.VK_C );
							r.keyRelease( KeyEvent.VK_CONTROL );
							r.keyRelease( KeyEvent.VK_C );							// end getting the new text in the clipboard
							
							synchronized( r ) { r.wait( 30 ); }						// It takes some time until the system has had time to update the clipboard.
							
							line = Other.getClipboardByNotepadS( rectNP );
							
							System.out.println( "e: " + e + "_" + h );
							if ( h == 1 )
								++e;
							++h;
						}
						else
							break;
					}
					if ( isHTML( line ) )									// sometimes is the line again html-code
					{
						System.out.println( "d: " + d );
						d++;
						synchronized( r ) { r.wait( 30 ); }
						line = Other.getClipboardByNotepadS( rectNP );
					}
					if ( isHTML( line ) )									// sometimes is the line again html-code
					{
						System.out.println( "d2: " + d2 );
						d2++;
						synchronized( r ) { r.wait( 30 ); }
						line = Other.getClipboardByNotepadS( rectNP );
					}
					
					String[] lSplit = line.split( "\n" );
					
					String[] rest = CreatorHHPS.notIncludedStrings(lastS, lSplit);
					
					if (line.startsWith("Geber: ")) {
						for (String s : rest) {
							FileWriter writer = new FileWriter(f, true);
							writer.write(String.format(s.substring(0) + "%n"));
							writer.flush();
							writer.close();
						}
					} else {
						System.out.println( "g: " + g );
						++g;
					}
					
					lastS = lSplit;
					Other.clearClipboard();
				}
				
				bfStart = bfNext;
				lock.unlock();
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
			
			try {
				out.write( 1 ); out.flush(); out.close();
			} catch ( Exception e2 ) {
				System.err.println( "There was problem with writing, flushing or closing out!" );
				e.printStackTrace();
				this.getThreadGroup().interrupt();
	}	}	}
	
	/**
	 * Returns whether a string is html-code.
	 * 
	 * @param s string
	 * @return true: s is html-code; false: s is not hmtl-code
	 */
	protected static boolean isHTML( String s )
	{
		if ( s.startsWith( "<!DOCTYPE HTML PUBLIC" ) )
			return true;
		else
			return false;
	}
	
}
