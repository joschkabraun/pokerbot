package creatorHandHistory;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.regex.Pattern;

import other.Tools;

/**
 * This class implements the program which creates the hand history (HH) of winner poker (WP) and safes it in a file
 * because WP does not create a hand history while the game. The created hand history will be a bit different to the
 * normal hand history of winner poker so in the package parser is one class else for parsing this hand history.
 * 
 * This class just creates HH if it called. CreatorHHWPThread creates HH the whole time.
 * 
 * @author Joschka J. Braun
 */
public class CreatorHHWPClass

{
	
	private Robot r;
	
	/**
	 * The file in which the hand history should be written. 
	 */
	private File f;
	
	/**
	 * The rectangle in which the method Other.getClipboardByNotepadS(Rectangle) should be used.
	 */
	private Rectangle rectNP;
	
	/**
	 * The rectangle of the table chat (TC).
	 */
	private Rectangle rectTC;
	
	/**
	 * lastS has the same role like in CreatorHHWPThread.
	 */
	private String[] lastS;
	
	public CreatorHHWPClass( File f, Rectangle rectNP, Rectangle rectTC )
	{
		try {
			this.r = new Robot();
			this.f = f;
			this.rectNP = rectNP;
			this.rectTC = rectTC;
			this.lastS = new String[1];
			this.lastS[0] = "test";
		} catch ( AWTException e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method creates hand history and write it into the file f.
	 * @throws Exception this exception results about the whole exceptions in the method
	 */
	public void createHH() throws Exception
	{
		try {
			Point p = Tools.createPointIn( rectTC) ;
			r.mouseMove (p.x, p.y ); 										// start getting the whole text in the clipboard
			r.mousePress( InputEvent.BUTTON1_DOWN_MASK );
			r.mouseRelease( InputEvent.BUTTON1_DOWN_MASK );
			r.keyPress( KeyEvent.VK_CONTROL );
			r.keyPress( KeyEvent.VK_A );
			r.keyRelease( KeyEvent.VK_A );
			r.keyPress( KeyEvent.VK_C );
			r.keyRelease( KeyEvent.VK_C );
			r.keyRelease( KeyEvent.VK_CONTROL );							// end getting the new text in the clipboard
			
			synchronized (r) { r.wait(30); }								// It takes some time until the system has had time to update the clipboard.
			
			String line = Tools.getClipboardByNotepadS(rectNP);
			
			for ( int i = 0; i < 10; i++ ) 									// sometimes is the creator to fast and the new line (in the table chat) is not already there
				if ( line.equals( "" ) ) {
					synchronized ( r ) { r.wait(30); }
					
					Point p2 = Tools.createPointIn(rectTC);
					r.mouseMove(p2.x, p2.y); 								// start getting the whole text in the clipboard
					r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
					r.keyPress(KeyEvent.VK_CONTROL);
					r.keyPress(KeyEvent.VK_A);
					r.keyRelease(KeyEvent.VK_A);
					r.keyPress(KeyEvent.VK_C);
					r.keyRelease(KeyEvent.VK_CONTROL);
					r.keyRelease(KeyEvent.VK_C); 							// end getting the new text in the clipboard
					
					synchronized (r) { r.wait(30); }						// It takes some time until the system has had time to update the clipboard.
					
					line = Tools.getClipboardByNotepadS(rectNP);
				} else
					break;
			
			for ( int i = 0; i < 4; i++ )									// sometimes is the line which was read html-code
				if (CreatorHHWPThread.isHTML(line)) {
					synchronized (r) { r.wait(30); }
					
					Point p2 = Tools.createPointIn(rectTC);
					r.mouseMove(p2.x, p2.y); 								// start getting the whole text in the clipboard
					r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
					r.keyPress(KeyEvent.VK_CONTROL);
					r.keyPress(KeyEvent.VK_A);
					r.keyRelease(KeyEvent.VK_A);
					r.keyPress(KeyEvent.VK_C);
					r.keyRelease(KeyEvent.VK_CONTROL);
					r.keyRelease(KeyEvent.VK_C); 							// end getting the new text in the clipboard
					
					synchronized (r) { r.wait(30); } 						// It takes some time until the system has had time to update the clipboard.
					
					line = Tools.getClipboardByNotepadS(rectNP);
				}
			
			String[] lSplit = line.split( "\n" );
			
			if ( lastS[0].length() == 0 || lastS[0].equals("test") ) {			// if the previous read text is empty the creator just should write the last hand history
				ArrayList<String> strings = new ArrayList<String>();			// into the file of the hand histories
				int index = 0;
				for ( int i = lSplit.length-1; i > -1; i-- )
					if ( Pattern.matches("Geber: .+ ist der Geber", lSplit[i].trim()) ) {
						index = i;
						break;
					}
				for ( int i = index; i < lSplit.length; i++ )
					strings.add(lSplit[i]);
				for ( String s : strings ) {
					FileWriter writer = new FileWriter( f, true );
					writer.write( String.format( s.substring(0) + "%n" ) );
					writer.flush();
					writer.close();
				}
				String[] stringsA = new String[strings.size()];
				for ( int i = 0; i < stringsA.length; i++ )
					stringsA[i] = strings.get(i);
			} else {
				String[] rest = CreatorHHPS.notIncludedStrings( lastS, lSplit );
				if ( line.startsWith( "Geber: " ) ) {
					for (String s : rest) {
						FileWriter writer = new FileWriter( f, true );
						writer.write( String.format( s.substring(0) + "%n" ) );
						writer.flush();
						writer.close();
					}
				}
				lastS = lSplit;
			}
			
			lastS = lSplit;
			Tools.clearClipboard();
		} catch ( Exception e ) {
			throw new Exception( e );
		}
	}
	
}
