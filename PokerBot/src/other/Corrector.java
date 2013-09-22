package other;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.PipedOutputStream;

import bots.Bot_v1_1_0Tables;
import bots.Bot_v1_2_0;

/**
 * This class implements methods for handling the problem if a window of WinnerPoker is not at the correct place.
 * The main purchase is to put the window again at the correct place.
 * 
 * @author Joschka
 */
public class Corrector {
	
	public static void main( String[] args ) throws Throwable {
		PipedOutputStream outBRU2I = new PipedOutputStream();
		ThreadGroup main = new ThreadGroup( "main" );
		String namePlayerYou = "walk10er";
		Bot_v1_2_0 bot = new Bot_v1_2_0( main, "BotRightUp-v1.2.0", outBRU2I, Bot_v1_1_0Tables.RIGHT_UP, namePlayerYou );
		
		boolean b1 = other.Tools.compare(bot.PICTURE_BUTTON_FOLD, (new Robot()).createScreenCapture(bot.SPACE_BUTTON_FOLD), 0.65);
		boolean b2 = other.Tools.compareSimilar(bot.PICTURE_BUTTON_FOLD, (new Robot()).createScreenCapture(bot.SPACE_BUTTON_FOLD), 0.055);
		System.out.println("b1: " + b1);
		System.out.println("b2: " + b2);
	}
	
	
	public static void correct( Rectangle corLoc, BufferedImage correctBI, Point pointOnLine, int amplitude ) throws Exception {
		Point actCurser = pointOnLine;
		Robot r = new Robot();
		BufferedImage bi;
		for ( int i = -amplitude; i <= amplitude; i++ ) {
			bi = r.createScreenCapture(corLoc);
			if ( Tools.compare(bi, correctBI, 0.65) )
				return;
			r.mouseRelease(InputEvent.BUTTON1_MASK);
			r.mouseMove(actCurser.x, actCurser.y);
			r.mousePress(InputEvent.BUTTON1_MASK);
			r.mouseMove(pointOnLine.x+i, pointOnLine.y);
			r.mouseRelease(InputEvent.BUTTON1_MASK);
			actCurser = MouseInfo.getPointerInfo().getLocation();
		}
		
		System.err.println("Maybe the method was not successful!");
	}
	
	
}
