package other;

import java.lang.ProcessBuilder;
import java.lang.Process;
import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class contains all "waste products" of the other classes. This class is about the clearness implemented so you can find general methods faster.
 * 
 * @author Joschka J. Braun
 *
 */
public class Tools

{
	
	/**
	 * Returns a random number between a and b (both are inclusive). a is smaller than b.
	 * 
	 * @param a the smaller integer of a and b
	 * @param b the bigger integer of a and b
	 * @return a random between a and b (both inclusive)
	 */
	public static int randomBetween( int a, int b )
	{
		return (int) Math.round( a + (b-a) * Math.random() );
	}
	
	/**
	 * Returns the string (if there is a string in the clipboard) in the clipboard.
	 * 
	 * @return string in the clipboard
	 * @throws IOException 
	 * @throws UnsupportedFlavorException 
	 */
	public static String getClipboardS()
	{
		try {
		Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable transferData = systemClipboard.getContents( null );
		
		for ( DataFlavor dataFlavor : transferData.getTransferDataFlavors() ) {
			Object content = transferData.getTransferData( dataFlavor );
			if ( content instanceof String )
				return (String) content;
		}
		
		return "";
		} catch ( Exception e1 ) { try {
			System.out.println( 1111111111 );
			Object o = new Object();
			synchronized ( o ) { o.wait( 100 ); }
			
			Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable transferData = systemClipboard.getContents( null );
			
			for ( DataFlavor dataFlavor : transferData.getTransferDataFlavors() ) {
				Object content = transferData.getTransferData( dataFlavor );
				if ( content instanceof String )
					return (String) content;
			}
			
				return "";
			} catch ( Exception e2 ) {
				e2.printStackTrace();
	}	} return ""; }
	
	/**
	 * Returns the string (if there is a string in the clipboard) in the clipboard.
	 * The method opens notepad paste the clipboard in it, copies the pasted text into the clipboard and than returns the clipboard by using the method getClipboardS().
	 * This method is written for the case if the method getClipboardS() does not work (for example for texts on websites).
	 * 
	 * ATTENTION for using the method because the method opens notepad and the frame of notepad appears on the place of the last closed notepad. That is not a problem
	 * for using the method not in concurrent programming. For using the method in concurrent programming the method should be secure ahead of between access.
	 * 
	 * @return string in the clipboard
	 * @throws IOException 
	 * @throws AWTException 
	 * @throws InterruptedException 
	 * @throws UnsupportedFlavorException 
	 */
	public static String getClipboardByNotepadS() throws IOException, AWTException, InterruptedException, UnsupportedFlavorException
	{
		Process proc = new ProcessBuilder( "notepad" ).start();
		Robot r = new Robot();
		
		r.keyPress( KeyEvent.VK_CONTROL );			// pasting the clipboard into notepad
		r.keyPress( KeyEvent.VK_V );
		r.keyRelease( KeyEvent.VK_CONTROL );
		r.keyRelease( KeyEvent.VK_V );
		
		synchronized( r ) { r.wait( 1 ); }			// The breaks are in the code thereby the different parts of the method works correctly.
													// Without one of the breaks the method does not work correctly.
		
//		clearClipboard();							// Clearing the clipboard is not necessary.
		
		synchronized( r ) { r.wait( 1 ); }
		
		r.keyPress( KeyEvent.VK_CONTROL );			// selecting the whole pasted text
		r.keyPress( KeyEvent.VK_A );
		r.keyRelease( KeyEvent.VK_CONTROL );
		r.keyRelease( KeyEvent.VK_A );
		
		synchronized( r ) { r.wait( 1 ); }
		
		r.keyPress( KeyEvent.VK_CONTROL );			// copying the selected text into the clipboard
		r.keyPress( KeyEvent.VK_C );
		r.keyRelease( KeyEvent.VK_CONTROL );
		r.keyRelease( KeyEvent.VK_C );
		
		synchronized( r ) { r.wait( 1 ); }
		
		proc.destroy();
		
		synchronized( r ) { r.wait( 1 ); }
		
		return getClipboardS();
	}
	
	/**
	 * Returns the string (if there is a string in the clipboard) in the clipboard.
	 * The method clicks into an open notepad paste the clipboard in it, copies the pasted text into the clipboard
	 * and than returns the clipboard by using the method getClipboardS().
	 * This method is written for the case if the method getClipboardS() does not work (for example for texts on websites).
	 * The difference to getClipboardByNotepadS() is this method uses an already opened notepad. So for using this method it have to be sure that the parameters are right.
	 * For using this method you do not have to use notepad other programs are also okay.
	 * 
	 * ATTENTION for using the method because the method opens notepad and the frame of notepad appears on the place of the last closed notepad. That is not a problem
	 * for using the method not in concurrent programming. For using the method in concurrent programming the method should be secure ahead of between access.
	 * 
	 * @param rect a rectangle which is in the GUI of notepad. The method needs this rectangle for calculating a coordinate thereby the method has access to notepad.
	 * @return string in the clipboard
	 * @throws AWTException 
	 * @throws IOException 
	 * @throws UnsupportedFlavorException 
	 * @throws InterruptedException 
	 */
	public static String getClipboardByNotepadS( Rectangle rect ) throws AWTException, UnsupportedFlavorException, IOException, InterruptedException
	{
		Point p = createPointIn( rect );
		
		Robot r = new Robot();
		
		r.mouseMove(p.x, p.y);
		r.mousePress( InputEvent.BUTTON1_DOWN_MASK );
		r.mouseRelease( InputEvent.BUTTON1_DOWN_MASK );
		
		synchronized( r ) { r.wait( 10 ); }			// The breaks are in the code thereby the different parts of the method works correctly.
													// Without one of the breaks the method does not work correctly.
		
		r.keyPress( KeyEvent.VK_CONTROL );			// pasting the clipboard into notepad
		r.keyPress( KeyEvent.VK_V );
		r.keyRelease( KeyEvent.VK_V );
		r.keyRelease( KeyEvent.VK_CONTROL );
		
		synchronized( r ) { r.wait( 10 ); }
		
//		clearClipboard();							// Clearing the clipboard is not necessary.
		
		synchronized( r ) { r.wait( 10 ); }
		
		r.keyPress( KeyEvent.VK_CONTROL );			// selecting the whole pasted text
		r.keyPress( KeyEvent.VK_A );
		r.keyRelease( KeyEvent.VK_A );
		r.keyRelease( KeyEvent.VK_CONTROL );
		
		synchronized( r ) { r.wait( 10 ); }
		
		r.keyPress( KeyEvent.VK_CONTROL );			// copying the selected text into the clipboard
		r.keyPress( KeyEvent.VK_C );
		r.keyRelease( KeyEvent.VK_CONTROL );
		r.keyRelease( KeyEvent.VK_C );
		
		synchronized( r ) { r.wait( 10 ); }
		
		r.keyPress( KeyEvent.VK_BACK_SPACE );		// for clearing the GUI of notepad
		r.keyRelease( KeyEvent.VK_BACK_SPACE );
		
		synchronized( r ) { r.wait( 10 ); }
		
		return getClipboardS();
	}
	
	/**
	 * Clears the clipboard by setting it with an empty string.
	 */
	public static void clearClipboard()
	{
		try {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents( new StringSelection( "" ), null );
		} catch (Exception e ) { try {
				Object o = new Object();
				synchronized ( o ) { o.wait( 100 ); }
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents( new StringSelection( "" ), null );
			} catch ( Exception e2 ) {
				e2.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns a random point in the rectangle r.
	 * 
	 * @param r the chosen rectangle
	 * @return
	 */
	public static Point createPointIn( Rectangle r )
	{
		return new Point( (int) Math.round(r.x + Math.random() * r.width), (int) Math.round(r.y + Math.random() * r.height) );
	}
	
	/**
	 * Returns the last modified file of the files.
	 * 
	 * @param files an array of files
	 * @return last modified file of files
	 */
	public static File lastModifiedOf( File[] files )
	{
		long time = files[0].lastModified();
		File retF = files[0];
		for ( File f : files )
		{
			retF = (time >= f.lastModified()) ? retF : f;
			time = (time >= f.lastModified()) ? time : f.lastModified();
		}
		return retF;
	}
	
	/**
	 * Returns whether the two buffered images show the same. Whether the two BufferedImages are the same
	 * depends on how many percents (X) have to be the same. If the format of pictures are not the same,
	 * they are not the same!
	 * 
	 * @param b1 one buffered image
	 * @param b2 another buffered image
	 * @param X the amount of percent whereby the both BufferedImages are the same
	 * @return whether the two buffered images shows the same
	 */
	public static boolean compare( BufferedImage b1, BufferedImage b2, double X )
	{
		if ( X <= 0 )
			throw new IllegalArgumentException( "The domain of X is 0 < X <= 1." );
		if ( 1 < X )
			throw new IllegalArgumentException( "The domain of X is 0 < X <= 1." );
		
		if ( b1.getHeight() != b2.getHeight() || b1.getWidth() != b2.getWidth() )
			return false;
		else
		{
			int[][] array = new int[ b1.getHeight() ][ b1.getWidth() ];
			for ( int h = 0; h < b1.getHeight(); h++ )
				for ( int w = 0; w < b1.getWidth(); w++ )
					array[ h ][ w ] = b1.getRGB( w,  h ) - b2.getRGB( w, h );
			return isMoreThanXPercent0( array, X );
		}
	}
	
	/**
	 * Returns whether is more equal than X percent of the entries in the array are null.
	 * 
	 * @param array an two-dim array
	 * @param X is the amount of percent by the array is tested. The domain of X is 0<X<=1.
	 * @return true: more equal than X % of the entries of the array are null; false: otherwise
	 */
	public static boolean isMoreThanXPercent0( int[][] array, double X )
	{
		if ( X <= 0 )
			throw new IllegalArgumentException( "The domain of X is 0 < X <= 1." );
		if ( 1 < X )
			throw new IllegalArgumentException( "The domain of X is 0 < X <= 1." );
		
		int counter = 0;
		for ( int i = 0; i < array.length; i++ )
			for ( int j = 0; j < array[ i ].length; j++ )
				if ( array[ i ][ j ] == 0 )
					++counter;
		int number = array.length;
		for ( int i = 0; i < array.length; i++ )
			number += array[ i ].length;
		
		if ( ((double) counter / number) >= X )
			return true;
		else
			return false;
	}
	
	/**
	 * Returns the number of entries between valA and valB in the array a. valB is inclusive in the count of the algorithm.
	 * 
	 * @param a array of integer in which you want to search
	 * @param valB the first integer
	 * @param valB the second integer
	 * @return number of entries in a between valA and valB (inclusive)
	 */
	public static int entriesBetween( int[] a, int valA, int valB )
	{
		int index = Arrays.binarySearch( a, valA );
		int ret = 0;
		for ( int i = index+1; i < a.length; i++ )
			if ( a[i] <= valB )
				++ret;
		return ret;
	}
	
	/**
	 * Returns the number of lines in this file.
	 * 
	 * @param file the file from which know how many lines it contains
	 * @return number of lines in the file
	 */
	public static int howManyLines( File file )
	{
		int howManyLines = 0;
		try {
			BufferedReader reader = new BufferedReader( new FileReader( file ) );
			while ( reader.readLine() != null ) {
				++howManyLines;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if ( howManyLines == 0 )
			System.err.println( "There are not any lines! Maybe the file " + file.toString() + " is wrong. (class ParserPokerStars, method howManyLines)" );
		return howManyLines;
	}
	
	/**
	 * Returns an array of string with all lines separated.
	 * 
	 * @param file the file, returning all lines in an array
	 * @return an array with all lines separated
	 */
	public static String[] allLines( File file )
	{
		if ( howManyLines( file ) == 0 )
		{
			String[] line = new String[1];
			System.err.println( "The passed file is empty. (class ParserPokerStars, method allLines, file " + file.toString() );
			return line;
		}
		
		int numberLines = howManyLines( file );
		String[] allLines = new String[ numberLines ];
		
		try {
			BufferedReader reader = new BufferedReader( new FileReader( file ) );
			for ( int i = 0; i < numberLines; i++ )
				allLines[i] = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return allLines;
	}
	
	/**
	 * Returns the first index of o in O. If o is not in O, the method will return -1.
	 * 
	 * @param O an array of Objects
	 * @param o an Object
	 * @return first index of o in O
	 */
	public static int indexOf( Object[] O, Object o )
	{
		for ( int i = 0; i < O.length; i++ )
			if ( O[i].equals( o ) )
				return i;
		return -1;		
	}
	
	/**
	 * Returns the first index of k in I. If k is not in I, the method will return -1.
	 * 
	 * @param I an array of integer
	 * @param k an integer
	 * @return first index of k in I
	 */
	public static int indexOf( int[] I, int k )
	{
		for ( int i = 0; i < I.length; i++ )
			if ( I[i] == k )
				return i;
		return -1;
	}
	
	/**
	 * Returns whether the array O contains the object o.
	 * 
	 * @param O an array of the type object
	 * @param o an object
	 * @return whether O includes o
	 */
	public static boolean includes( Object[] O, Object o )
	{
		for ( Object o2 : O )
			if ( o2.equals(o) )
				return true;
		return false;
	}
	
	/**
	 * Returns how often o is in O.
	 * 
	 * @param O an array of the type object
	 * @param o an object
	 * @return how often o is in O
	 */
	public static int entriesIn( Object[] O, Object o )
	{
		int howOften = 0;
		for ( Object o2 : O )
			if ( o2.equals(o) )
				++howOften;
		return howOften;
	}
	
	/**
	 * Returns how often a is in A.
	 * 
	 * @param A an array of integers
	 * @param a an integer
	 * @return how often a is in A
	 */
	public static int entriesIn( int[] A, int a )
	{
		int howOften = 0;
		for ( int a2 : A )
			if ( a2 == a )
				++howOften;
		return howOften;
	}
	
	/**
	 * Returns all indexes of a in A.
	 * 
	 * @param A an array of integers
	 * @param a an integer
	 * @return indexes of a in A
	 */
	public static int[] whichIndexesIn( int[] A, int a )
	{
		ArrayList<Integer> ret = new ArrayList<Integer>();
		
		for ( int i = 0; i < A.length; i++ )
				if ( A[i] == a )
					ret.add( i );
		
		if ( ret.size() > 0 )
			return toArrayI( ret );
		else
		{
			int[] retI = { 0 };
			return retI;
		}
	}
	
	/**
	 * Returns all indexes of s in S.
	 * 
	 * @param S an array of strings
	 * @param S an string
	 * @return indexes of s in S
	 */
	public static int[] whichIndexesIn( String[] S, String s )
	{
		ArrayList<Integer> ret = new ArrayList<Integer>();
		
		for ( int i = 0; i < S.length; i++ )
			if ( S[i].equals( s ) )
				ret.add( i );
		
		if ( ret.size() > 0 )
			return toArrayI( ret );
		else
		{
			int[] retI = { 0 };
			return retI;
		}
	}
	
	/**
	 * Returns the maximal number of entries of one integer in the array A.
	 * 
	 * @param A an array of integers
	 * @return the maximal number of entries
	 */
	public static int maxNumberOfEntriesIn( int[] A )
	{
		int counter = 0;
		for ( int a : A )
			counter = (counter >= whichIndexesIn(A, a).length) ? counter : whichIndexesIn(A, a).length;
		return counter;
	}
	
	/**
	 * Returns the ArrayList S as an array of strings.
	 * 
	 * @param S an ArrayList of strings
	 * @return S as an array of strings
	 */
	public static String[] toArrayS( ArrayList<String> S )
	{
		String[] ret = new String[ S.size() ];
		for ( int i = 0; i < ret.length; i++ )
			ret[i] = S.get(i);
		return ret;
	}
	
	/**
	 * Returns the ArrayList I as an array of integers.
	 * 
	 * @param I an ArrayList of integers
	 * @return I as an array of integers
	 */
	public static int[] toArrayI( ArrayList<Integer> I )
	{
		int[] ret = new int[ I.size() ];
		for ( int i = 0; i < ret.length; i++ )
			ret[i] = I.get(i);
		return ret;
	}
	
	/**
	 * Returns the array I as an ArrayList of integers.
	 * 
	 * @param I an array of integers
	 * @return I as an ArrayList of integers
	 */
	public static ArrayList<Integer> toArrayListI( int[] I )
	{
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for ( int i : I )
			ret.add( i );
		return ret;
	}
	
	/**
	 * Returns an array of strings as a string.
	 * 
	 * @param S an array of strings
	 * @return S as one string
	 */
	public static String arraysToString( String[] S )
	{
		String ret = "";
		for ( String s : S )
			ret += s + ", ";
		if ( ret.length() < 3 )
			return ret;
		return ret.substring(0, ret.length()-2);
	}
	
	/**
	 * Returns the double to the string s. This method is a modified version of Double.parseDouble(String).
	 * For example you can use this method for 2,000 (two thousand).
	 * 
	 * @param s a double as a string
	 * @return the double to the string s
	 */
	public static double parseDouble( String s )
	{
		if ( s.matches( "\\d+[,]\\d+" ) )
			return Double.parseDouble( s.split("[,]")[0] + s.split("[,]")[1] );
		if ( s.matches( "\\d+[,]\\d+[,]\\d+" ) )
			return Double.parseDouble( s.split("[,]")[0] + s.split("[,]")[1] + s.split("[,]")[2] );
		return Double.parseDouble( s );
	}
	
}
