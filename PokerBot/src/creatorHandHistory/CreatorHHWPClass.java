package creatorHandHistory;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	
	private File fLog;
	
	public CreatorHHWPClass( File f, Rectangle rectNP, Rectangle rectTC )
	{
		try {
			this.r = new Robot();
			this.f = f;
			this.rectNP = rectNP;
			this.rectTC = rectTC;
			this.lastS = new String[1];
			this.lastS[0] = "test";
			switch ( f.getAbsolutePath() ) {
			case "c:\\pokerBot\\bot_v1_2_0\\hhTableRightDown.txt":
				this.fLog = new File("c://pokerBot//bot_v1_2_0//debug//creatorLogTableRightDown.txt");
				break;
			case "c:\\pokerBot\\bot_v1_2_0\\hhTableRightUp.txt":
				this.fLog = new File("c://pokerBot/bot_v1_2_0//debug//creatorLogTableRightUp.txt");
				break;
			case "c:\\pokerBot\\bot_v1_2_0\\hhTableLeftDown.txt":
				this.fLog = new File("c://pokerBot/bot_v1_2_0//debug//creatorLogTableLeftDown.txt");
				break;
			case "c:\\pokerBot\\bot_v1_2_0\\hhTableLeftUp.txt":
				this.fLog = new File("c://pokerBot/bot_v1_2_0//debug//creatorLogTableLeftUp.txt");
				break;
			default:
				throw new IllegalArgumentException("Bla bla!");
			}
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
			
			for ( int i = 0; i < 26; i++ ) {							// sometimes is the creator to fast and the new line (in the table chat) is not already there
				if (bots.Bot.debug_normal) {					// for testing
					if (line.equals("") || line.equals(" "))
						continue;
					try {
						System.out.println("line.substring(line.length()-lastS[lastS.length-1].length()): " + line.substring(line.length()-lastS[lastS.length - 1].length()));
						System.out.print("lastS[lastS.length-1]: " + lastS[lastS.length - 1]);
						System.out.println("line.substring(line.length()-lastS[lastS.length-1].length()).equals(lastS[lastS.length-1]): " + line.substring(line.length()- lastS[lastS.length - 1].length()).equals(lastS[lastS.length - 1]));
						System.out.println();
					} catch (Throwable e) {
						System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
						System.err.println("line: " + line);
						System.err.println("lastS[lastS.length-1]: " + lastS[lastS.length - 1]);
						throw e;
					}
				}
				
				
				boolean isPreviousText;
				
				if ( lastS.length > 10 ) {
					String s = "";
					for ( int j = lastS.length-10; j < lastS.length; j++ )
						s += lastS[j];
					isPreviousText = line.substring(line.length()-s.length()).equals(s);
				} else if ( lastS.length > 5 ) {
					String s = "";
					for ( int j = lastS.length-5; j < lastS.length; j++ )
						s += lastS[j];
					isPreviousText = line.substring(line.length()-s.length()).equals(s);
				} else 
					isPreviousText = line.substring(line.length()-lastS[lastS.length-1].length()).equals(lastS[lastS.length-1]);
				
				
				if ( line.equals( "" ) || CreatorHHWPThread.isHTML(line) || (! line.startsWith("Geber: "))		// sometimes is the line which was read html-code
						|| isPreviousText || (line.length()<5) ) {												// sometimes the line is full of things
					synchronized ( r ) { r.wait(30); }															// which were previous read into the clipboard
					
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
			}
			
			if (bots.Bot.debug_normal)
				try { 										// for testing
					FileWriter fw = new FileWriter(this.fLog, true);
					fw.write(String.format("%nline: %n") + line + String.format("%n"));
					fw.flush();
					fw.close();
				} catch (Exception e2) {
					throw new Exception(e2);
				}
			
			
			String[] lSplit = line.split( "\n" );
			for ( int i = 0; i < lSplit.length; i++ )
				lSplit[i] =  lSplit[i] + "\n";
//				lSplit[i] =  lSplit[i] + "\r\n";
			
			if (bots.Bot.debug_normal)
				try { 										// for testing
					String str = "";
					for (String s : lSplit)
						str += s;
					FileWriter fw = new FileWriter(this.fLog, true);
					fw.write(String.format("%nlSplit: %n") + str + String.format("%n"));
					fw.flush();
					fw.close();
				} catch (Exception e2) {
					throw new Exception(e2);
				}
			
			
			if ( lastS[0].length() == 0 || lastS[0].equals("test") ) {	// if the previous read text is empty the creator just should write the last hand history
				ArrayList<String> strings = new ArrayList<String>();	// into the file of the hand histories
				int index = 0;
				for ( int i = lSplit.length-1; i > -1; i-- )
					if ( Pattern.matches("Geber: .+ ist der Geber", lSplit[i].trim()) ) {
						index = i;
						break;
					}
				for ( int i = index; i < lSplit.length; i++ )
					strings.add(lSplit[i]);
				
				if ( bots.Bot.debug_normal )
					try { 											// for testing
						String str = "";
						for (String s : strings)
							str += s + String.format("%n");
						FileWriter fw = new FileWriter(this.fLog, true);
						fw.write(String.format("%nthis is the first line:%n" + str + "%n"));
						fw.flush();
						fw.close();
					} catch (Exception e) {
						throw e;
					}
				
				FileWriter writer = new FileWriter( f, true );
				for ( String s : strings )
					writer.write(s + String.format("%n"));
				writer.flush();
				writer.close();
				
				String[] stringsA = new String[strings.size()];
				for ( int i = 0; i < stringsA.length; i++ )
					stringsA[i] = strings.get(i);
				lSplit = stringsA;
			} else {
				
				if (bots.Bot.debug_normal)
					try { 																	// for testing
						FileWriter fw = new FileWriter(this.fLog, true);
						fw.write(String.format("%nlastS (not in lines):%n"));
						for (String s : lastS)
							fw.write(s);
						fw.flush();
						fw.close();
					} catch (Exception e) {
						throw e;
					}
				
				String[] rest = notIncludedStrings( lastS, lSplit );
				
				if (bots.Bot.debug_normal) {									// for testing
					System.out.println("lastS:");
					for ( int i = (lastS.length > 5) ? lastS.length-3 : 0; i < lastS.length; i++ )
						System.out.print(lastS[i]);
					System.out.println("lSplit:");
					for ( int i = (lSplit.length > 5) ? lSplit.length-3 : 0; i < lSplit.length; i++ )
						System.out.print(lSplit[i]);
					System.out.println("rest:");
					for ( String s : rest )
						System.out.println(s);
				}
				
				if ( line.startsWith( "Geber: " ) ) {
					FileWriter writer = new FileWriter( f, true );
					FileWriter fw = new FileWriter(this.fLog, true);					// for testing
					if ( bots.Bot.debug_normal )
						fw.write(String.format("%nrest: " + "%n"));
					for (String s : rest) {
						if ( bots.Bot.debug_normal )
							fw.write(String.format(s.substring(0) + "%n"));
						writer.write( String.format( s.substring(0) + "%n" ) );
					}
					writer.flush();
					writer.close();
					fw.flush();
					fw.close();
				}
			}
			
			lastS = lSplit;
			for ( int i = 0; i < 10; i++ )
				Tools.clearClipboard();
		} catch ( Exception e ) {
			throw new Exception( e );
		}
	}
	
	/**
	 * Assumption/initial situation: A is included in B.
	 * Returns the strings which are in B but are "after" (in the array) A.
	 * 
	 * @param A the array of strings you deduct from b
	 * @param B the array of strings from which you want know which strings are in B but are after A (which is also in B).
	 * @return the strings which are in B but are after A in B.
	 */
	public static String[] notIncludedStrings( String[] A, String[] B ) {
		StringBuilder sbA = new StringBuilder();
		StringBuilder sbB = new StringBuilder();
		for ( String s : A )
			sbA.append(s);
		for ( String s : B )
			sbB.append(s);
		String sA = sbA.toString();
		String sB = sbB.toString();
		if ( B.length == 500 || A.length == 500 ) {		// The table chat of Winner Poker contains maximal 500 lines.
			if (bots.Bot.debug_normal)
				try {
					int helpIndex = sA.indexOf(B[0]+B[1]+B[2]+B[3]+B[4]+B[5]+B[6]+B[7]+B[8]+B[9]);
					sA = sA.substring(helpIndex);
				} catch ( ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e ) {
					File file = new File("c://pokerBot//bot_v1_2_0//debug//specialExceptionInCreatorHHWPClassNotIncludedStrings.txt");
					try {
					FileWriter filewriter = new FileWriter(file);
					filewriter.write("A:\n" + other.Tools.arraysToString(A) + "\n");
					filewriter.write("B:\n" + other.Tools.arraysToString(B));
					} catch (IOException r) {
						System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");
						System.out.println("A:\n" + other.Tools.arraysToString(A));
						System.out.println("B:\n" + other.Tools.arraysToString(B));
						System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");
					}
					throw e;
				}
			else {
				int helpIndex = sA.indexOf(B[0]+B[1]+B[2]+B[3]+B[4]+B[5]+B[6]+B[7]+B[8]+B[9]);
				sA = sA.substring(helpIndex);				
			}
				
		}
		int index = sB.indexOf(sA);
		if ( index == -1 ) {
			if ( sA == "" || sA == " " || sB == "" || sB == " " ) {
				System.out.println("OOOOOOMMMMMGGGGGGGG");
				System.out.println("sA: 777" + sA + "888");
				System.out.println("sB: 777" + sB + "888");
				System.out.println("OOOOOOMMMMMGGGGGGGG");
			}
			throw new IllegalArgumentException("A is not included in B!");
		}
		String restS = sB.substring(index + sA.length());
		String[] rest = restS.split("\n");
		return rest;
	}
	
}
