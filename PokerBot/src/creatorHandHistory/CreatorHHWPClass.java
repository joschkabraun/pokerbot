package creatorHandHistory;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
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
	 * The sessional file in which the history of the people who enter and leave the table should be written. The file is going to be empty after the session.
	 */
	private File s;
	
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
	
	private boolean used;
	
	/**
	 * @deprecated because the old version of the PokerBot are longer supported and there are new things!
	 */
	public CreatorHHWPClass( File f, Rectangle rectNP, Rectangle rectTC )
	{
		try {
			this.r = new Robot();
			this.f = f;
			this.s = null;
			this.rectNP = rectNP;
			this.rectTC = rectTC;
			this.lastS = new String[1];
			this.lastS[0] = "test";
			switch ( f.getAbsolutePath() ) {
			case "c:\\pokerBot\\bot_v1_3_x\\hhTableRightDown.txt":
				this.fLog = new File("c://pokerBot//bot_v1_3_x//debug//creatorLogTableRightDown.txt");
				break;
			case "c:\\pokerBot\\bot_v1_3_x\\hhTableRightUp.txt":
				this.fLog = new File("c://pokerBot/bot_v1_3_x//debug//creatorLogTableRightUp.txt");
				break;
			case "c:\\pokerBot\\bot_v1_3_x\\hhTableLeftDown.txt":
				this.fLog = new File("c://pokerBot/bot_v1_3_x//debug//creatorLogTableLeftDown.txt");
				break;
			case "c:\\pokerBot\\bot_v1_3_x\\hhTableLeftUp.txt":
				this.fLog = new File("c://pokerBot/bot_v1_3_x//debug//creatorLogTableLeftUp.txt");
				break;
			default:
				throw new IllegalArgumentException("Bla bla!");
			}
		} catch ( AWTException e ) {
			e.printStackTrace();
		}
	}
	
	public CreatorHHWPClass( File f, File s, Rectangle rectNP, Rectangle rectTC )
	{
		try {
			this.r = new Robot();
			this.f = f;
			this.s = s;
			FileOutputStream fos = new FileOutputStream(s);		// clear the s-file
			fos.write(new String().getBytes());
			fos.flush();
			fos.close();
			FileWriter fw = new FileWriter(s, false);
			this.rectNP = rectNP;
			this.rectTC = rectTC;
			this.lastS = new String[1];
			this.lastS[0] = "test";
			int numberPlayer;
			switch ( f.getAbsolutePath() ) {
			case "c:\\pokerBot\\bot_v1_3_x\\hhTableRightDown.txt":
				this.fLog = new File("c://pokerBot//bot_v1_3_x//debug//creatorLogTableRightDown.txt");
				System.out.println("Please enter the number of players for the table RIGHT-DOWN: ");
				numberPlayer = new java.util.Scanner(System.in).nextInt();
				fw.write(String.format(numberPlayer+"%n"));
				fw.flush();
				fw.close();
				break;
			case "c:\\pokerBot\\bot_v1_3_x\\hhTableRightUp.txt":
				this.fLog = new File("c://pokerBot/bot_v1_3_x//debug//creatorLogTableRightUp.txt");
				System.out.println("Please enter the number of players for the table RIGHT-UP: ");
				numberPlayer = new java.util.Scanner(System.in).nextInt();
				fw.write(String.format(numberPlayer+"%n"));
				fw.flush();
				fw.close();
				break;
			case "c:\\pokerBot\\bot_v1_3_x\\hhTableLeftDown.txt":
				this.fLog = new File("c://pokerBot/bot_v1_3_x//debug//creatorLogTableLeftDown.txt");
				System.out.println("Please enter the number of players for the table LEFT-DOWN: ");
				numberPlayer = new java.util.Scanner(System.in).nextInt();
				fw.write(String.format(numberPlayer+"%n"));
				fw.flush();
				fw.close();
				break;
			case "c:\\pokerBot\\bot_v1_3_x\\hhTableLeftUp.txt":
				this.fLog = new File("c://pokerBot/bot_v1_3_x//debug//creatorLogTableLeftUp.txt");
				System.out.println("Please enter the number of players for the table LEFT-UP: ");
				numberPlayer = new java.util.Scanner(System.in).nextInt();
				fw.write(String.format(numberPlayer+"%n"));
				fw.flush();
				fw.close();
				break;
			default:
				this.fLog = new File("c://pokerBot/bot_v1_2_0//debug//creatorLogTableLeftUp.txt");
				System.out.println("Please enter the number of players for the table LEFT-UP: ");
				numberPlayer = new java.util.Scanner(System.in).nextInt();
				fw.write(String.format(numberPlayer+"%n"));
				fw.flush();
				fw.close();
//				throw new IllegalArgumentException("Bla bla!");
			}
		} catch ( AWTException | IOException e ) {
			e.printStackTrace();
		}
	}
	
	public void createHH(Rectangle notepad) throws Exception {
		this.rectNP = notepad;
		createHH();
	}
	
	/**
	 * This method creates hand history and write it into the file f.
	 * @throws Exception this exception results about the whole exceptions in the method
	 */
	public void createHH() throws Exception
	{
		this.used = false;
		String[] lSplit = null;
		int counter;
		try {
			String line = getText();
			
			for ( counter = 0; counter < 52; counter++ ) {		// sometimes is the creator to fast and the new line (in the table chat) is not already there
				if ( line.equals("") || line.length() < 5 ) {
					line = getText();
					continue;
				} else if (CreatorHHWPThread.isHTML(line) && counter == 0) {
					lSplit = htmlToNormal(line.split("\n"));
					break;
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
				
				if ( (! line.startsWith("Geber: ")) || isPreviousText ) {
					line = getText();
				} else
					break;
			}
			
			FileWriter fwr = new FileWriter(new File("c://pokerBot/bot_v1_3_x//debug//counter.txt"), true);
			fwr.write(String.format(counter+"%n"));
			if (counter == 52 && ! line.equals("")) {
				fwr.write(String.format("Attention!%n"));
				fwr.write(line);
				fwr.write(String.format("%n%n%n"));
			}
			fwr.flush();
			fwr.close();
			
			if (bots.Bot.debug_normal)
				try { 										// for testing
					FileWriter fw = new FileWriter(this.fLog, true);
					fw.write(String.format("%nline: %n") + line + String.format("%n"));
					fw.flush();
					fw.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			
			if (lSplit == null)
				lSplit = line.split( "\n" );
			for ( int i = 0; i < lSplit.length; i++ )
				lSplit[i] =  lSplit[i] + "\n";
			
			File file = new File("c://pokerBot/bot_v1_2_0//debug//bug//bug_log.txt");
			FileWriter filewriter = new FileWriter(file, true);
			ArrayList<String> results = new ArrayList<String>();
			for (int i = 0; i < lSplit.length; i++) {
				String s = lSplit[i];
				if (s.length() > 2)
					if (s.charAt(s.length()-2) == ' ')
						if (s.charAt(s.length()-3) == ' ') {
							results.add(s);
							lSplit[i] = s.substring(0, s.length()-2) + "\n";
						}
			}
			if (results.size() > 0) {
				filewriter.write(String.format("Here is the bug!!!%n"));
				for (String s : lSplit)
					filewriter.write(String.format(s+"%n"));
				filewriter.write(String.format("In the following are the \"wrong\" strings:%n"));
				for (String s : results)
					filewriter.write(String.format(s+"%n"));
				filewriter.write(String.format("%n%n%n%n%n"));
				filewriter.flush();
				filewriter.close();
			}
			
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
			
			
			if ( lastS[0].length() == 0 || lastS[0].equals("test") ) {	// if the previous read text is empty, the creator just should write the last hand history
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
				FileWriter writer2 = new FileWriter(s, true);
				for ( String s : lSplit )											// the whole hand history have to be scanned
					if ( s.matches("Geber: Spieler .+ hat sich an den Tisch gesetzt.{0,2}\n") || s.matches("Geber: Spieler .+ hat sich an den Tisch gesetzt.*") ||
							s.matches("Geber: .+ hat den Tisch verlassen.{0,2}\n") || s.matches("Geber: .+ hat den Tisch verlassen.*") )
						writer2.write(s + String.format("%n"));
				writer2.flush();
				writer2.close();				
				
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
				
				FileWriter writer = new FileWriter( f, true );
				FileWriter writerS = new FileWriter(s, true);
				FileWriter fw = new FileWriter(this.fLog, true);					// for testing
				if ( bots.Bot.debug_normal )
					fw.write(String.format("%nrest: " + "%n"));
				for (String s : rest) {
					if ( bots.Bot.debug_normal )
						fw.write(String.format(s.substring(0) + "%n"));
					writer.write( String.format( s.substring(0) + "%n" ) );
					if ( s.matches("Geber: Spieler .+ hat sich an den Tisch gesetzt.{0,2}\n") || s.matches("Geber: Spieler .+ hat sich an den Tisch gesetzt.*") ||
							s.matches("Geber: .+ hat den Tisch verlassen.{0,2}\n") || s.matches("Geber: .+ hat den Tisch verlassen.*") )
						writerS.write(String.format(s.substring(0) + "%n"));
				}
				writer.flush();
				writer.close();
				writerS.flush();
				writerS.close();
				fw.flush();
				fw.close();
			}
			
			lastS = lSplit;
			for ( int i = 0; i < 10; i++ )
				Tools.clearClipboard();
		} catch ( Exception e ) {
			FileWriter fwr = new FileWriter(new File("c://pokerBot/bot_v1_3_x//debug//exception.txt"), true);
			try {
				fwr.write(String.format("Exception: %n"));
				fwr.write(String.format("e.toString(): " + e.toString()+"%n"));
				fwr.write(String.format("e.getCause(): " + e.getCause() + "%n%n%n%n"));
				fwr.flush();
				fwr.close();
			} catch (IOException ioe) {
				throw ioe;
			}
			throw new Exception( e );
		}
	}
	
	/**This method copies first the text of the table-chat into the clipboard, pastes than the text into a editor
	 * and puts concluding the text again into the clipboard. 
	 * 
	 * @return
	 * @throws Exception
	 */
	private String getText() throws Exception {
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
		
		return Tools.getClipboardByNotepadS(this.rectNP);
	}
	
	/**
	 * Assumption/initial situation: A is included in B.
	 * Returns the strings which are in B but are "after" (in the array) A.
	 * 
	 * @param A the array of strings you deduct from b
	 * @param B the array of strings from which you want know which strings are in B but are after A (which is also in B).
	 * @return the strings which are in B but are after A in B.
	 */
	public String[] notIncludedStrings( String[] A, String[] B ) {
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
					File file = new File("c://pokerBot//bot_v1_3_x//debug//specialExceptionInCreatorHHWPClassNotIncludedStrings.txt");
					try {
					FileWriter filewriter = new FileWriter(file, true);
					StringBuilder a = new StringBuilder();
					for (String s : A)
						a.append(s);
					StringBuilder b = new StringBuilder();
					for (String s : B)
						b.append(s);
					filewriter.write("A:\n" + a + "\n");
					filewriter.write("B:\n" + b);
					filewriter.write(String.format("%n%n%n%n%n%n"));
					filewriter.flush();
					filewriter.close();
					} catch (IOException r) {
						StringBuilder a = new StringBuilder();
						for (String s : A)
							a.append(String.format("%s%n", s));
						StringBuilder b = new StringBuilder();
						for (String s : B)
							b.append(String.format("%s%n", s));
						System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");
						System.out.println("A:\n" + a);
						System.out.println("B:\n" + b);
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
			System.out.println("a");
			
			int hallo = -1;
			for ( int i = sA.length()-1; i > 0; i--)
				if (sB.indexOf(sA.substring(0, i)) > -1) {
					hallo = i;
					break;
				}
			System.out.println("hallo: "+hallo);
			System.out.println(sA.substring(0, hallo));
			System.out.println("sA: 777" + sA + "888");
			System.out.println("sB: 777" + sB + "888");
			
			if (! used) {
				String[] A2 = new String[A.length];
				String[] B2 = new String[B.length];
				String s;
				
				for (int i = 0; i < A.length; i++) {				// deletes "  " at the end of lines
					s = A[i];
					if (s.length() > 1)
						if ( s.charAt(s.length()-2) == ' ')
							if ( s.charAt(s.length()-3) == ' ') {
								A2[i] = s.substring(0, s.length()-2) + "\n";
								continue;
							}
					A2[i] = s;
				}
				for (int i = 0; i < B.length; i++) {
					s = B[i];
					if (s.length() > 1)
						if ( s.charAt(s.length()-2) == ' ')
							if ( s.charAt(s.length()-3) == ' ') {
								B2[i] = s.substring(0, s.length()-2) + "\n";
								continue;
							}
					B2[i] = s;
				}
				
				this.used = true;
				return notIncludedStrings(A2, B2);
			}
			
			
			throw new IllegalArgumentException("A is not included in B!");
		}
		String restS = sB.substring(index + sA.length());
		String[] rest = restS.split("\n");
		return rest;
	}
	
	/**
	 * This is a method especially programmed for parsing the HTML-Code, which is sometimes produced by the algorithm of the CreatorHHWPClass/-Thread, into normal text. 
	 * 
	 * @return
	 */
	public static String[] htmlToNormal(String[] sa) {
		int start = 0;
		for (int i=0; i<sa.length; i++)
			if (sa[i].startsWith("<TR>") || sa[i].startsWith("<TD>")) {
				start = i;
				break;
			}
		
		ArrayList<String> sl = new ArrayList<String>();
		for (int i=start; i<sa.length; i++)
			if (sa[i].startsWith("<TD>"))
				sl.add(clearAngleBracket(sa[i]).toString());
			else if(! sa[i].startsWith("<TR>"))											// because sometimes one line is split off into two parts
				sl.set(sl.size()-1, sl.get(sl.size()-1).concat(clearAngleBracketMoreSafe(sa[i]).toString()));
		
		return other.Tools.toArrayS(sl);
	}
	
	/**
	 * @return the parameter s without "<", ">" and the the text which is enclosed by "< ... >". So this method is written for HTML-Code.
	 */
	private static StringBuilder clearAngleBracket(String s) {
		StringBuilder sb = new StringBuilder();
		boolean append = true;
		for ( int i=0; i<s.length(); i++ ) {
			char c = s.charAt(i);
			if (c == '<')
				append = false;
			else if (c == '>')
				append = true;
			else if (! append)
				continue;
			else
				sb.append(c);
		}
		if (sb.length() > 1)
			if (sb.charAt(sb.length()-1) != ' ')
				sb.append(' ');
			else
				if (sb.charAt(sb.length()-2) == ' ')
					sb.deleteCharAt(sb.charAt(sb.length()-2));
		return sb;
	}
	
	private static StringBuilder clearAngleBracketMoreSafe(String s) {
		for ( int i = 0; i < s.length(); i++ )
			if (s.charAt(i) == '>')
				return clearAngleBracket(s.substring(i+1));
			else if (s.charAt(i) == '<')
				return clearAngleBracket(s);
		return clearAngleBracket(s);
	}
	
}
