package bots;

import java.awt.Color;
import java.awt.Dimension;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import other.Tools;

/**
 * This class implements the main class of the bot.
 * BLU = BotLeftUp
 * BLD = BotLeftDown
 * BRU = BotRightUp
 * BRD = BotRightDown
 * 
 * @author Joschka J. Braun
 *
 */
public class Bot

{
	
	/**
	 * With these streams the Interrupter can read "messages" from the threads;
	 */
	private ArrayList<PipedInputStream> ins;
	
	/**
	 * These are the streams for writing to the Interrupter.
	 */
	private ArrayList<PipedOutputStream> outs;
	
	/**
	 * These are the bots of the single tables.
	 */	
	public ArrayList<Bot_v1_2_0> bots;
	
	/**
	 * This is the Interrupter which interrupts the whole bot if there are problem in the bot.
	 */
	private Interrupter interrupter;
	
	public Bot() {
		PipedInputStream inBLU2I = new PipedInputStream();
		PipedOutputStream outBLU2I = new PipedOutputStream();
		PipedInputStream inBLD2I = new PipedInputStream();
		PipedOutputStream outBLD2I = new PipedOutputStream();
		PipedInputStream inBRU2I = new PipedInputStream();
		PipedOutputStream outBRU2I = new PipedOutputStream();
		PipedInputStream inBRD2I = new PipedInputStream();
		PipedOutputStream outBRD2I = new PipedOutputStream();
		
		try {
			inBLU2I.connect( outBLU2I );
			inBLD2I.connect( outBLD2I );
			inBRU2I.connect( outBRU2I );
			inBRD2I.connect( outBRD2I );
		} catch ( IOException e ) {
			e.printStackTrace();
			System.exit( 0 );
		}
		
		ThreadGroup main = new ThreadGroup( "main" );
		String namePlayerYou = "walk10er";
		
		Bot_v1_2_0 BLU = new Bot_v1_2_0( main, "BotLeftUp-v1.2.0", outBLU2I, Bot_v1_1_0Tables.LEFT_UP, namePlayerYou );
		Bot_v1_2_0 BLD = new Bot_v1_2_0( main, "BotLeftDown-v1.2.0", outBLD2I, Bot_v1_1_0Tables.LEFT_DOWN, namePlayerYou );
		Bot_v1_2_0 BRU = new Bot_v1_2_0( main, "BotRightUp-v1.2.0", outBRU2I, Bot_v1_1_0Tables.RIGHT_UP, namePlayerYou );
		Bot_v1_2_0 BRD = new Bot_v1_2_0( main, "BotRightDown-v1.2.0", outBRD2I, Bot_v1_1_0Tables.RIGHT_DOWN, namePlayerYou );
		
		ins = new ArrayList<PipedInputStream>();
		ins.add(inBRD2I);
		ins.add(inBRU2I);
		ins.add(inBLD2I);
		ins.add(inBLU2I);
		outs = new ArrayList<PipedOutputStream>();
		outs.add(outBRD2I);
		outs.add(outBRU2I);
		outs.add(outBLD2I);
		outs.add(outBLU2I);		
		bots = new ArrayList<Bot_v1_2_0>();
		bots.add(BRD);
		bots.add(BRU);
		bots.add(BLD);
		bots.add(BLU);
		ArrayList<Thread> threads = new ArrayList<Thread>();
		threads.add(BRD);
		threads.add(BRU);
		threads.add(BLD);
		threads.add(BLU);
		
		interrupter = new Interrupter( main, "Interrupter", ins, threads, this );
	}
	
	public static void main(String[] args)
	{
		Bot bot = new Bot();
		
		JFrame frame = new JFrame( "Bot v1.2.0" );											// GUI
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setBounds( 2000, 100, 500, 500 );
		
		JPanel panel = new JPanel();
		panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS) );
		JLabel label = new JLabel( "<html>This is the graphical representation of the PokerBot with the version number 1.0.0. The bot plays poker on WinnerPoker. " +
				"Please do not shut down the computer and do not do anything else on the computer. If there is a problem, please tell this " +
				"the developer or the owner of the computer. And also do not kill this window or the bot that could cause a financial damage!</html>" );
		label.setPreferredSize( new Dimension(500, 100) );
		label.setForeground( Color.RED );
		panel.add( label );
		JTextArea text = new JTextArea();
		text.setPreferredSize( new Dimension(500, 400) );
		panel.add( text );
		
		frame.add( panel );
		frame.setVisible( true );
		
//		bot.bots.get(0).start();			// BRD = bot right down
		bot.bots.get(1).start();			// BRU = bot right up
		bot.bots.get(2).start();			// BLD = bot left down
		bot.bots.get(3).start();			// BLU = bot left up
		bot.interrupter.start();
		
	}
	
	/**
	 * This method deletes the last started hand history.
	 * This method is part of a bug-fix.
	 */
	protected void exit() {
		try {
			FileWriter fw;
			for ( Bot_v1_2_0 b : this.bots ) {
				String[] allLines = Tools.allLines(b.hhFile);
				int untilIndex = 0;
				for ( int i = allLines.length-1; i > -1; i-- )
					if ( Pattern.matches("Geber: .+ ist der Geber", allLines[i].trim()) ) {
							untilIndex = i;
							break;
					}
				fw = new FileWriter(b.hhFile);
				for ( int i = 0; i < untilIndex; i++ )
					fw.write(String.format(allLines[i] + "%n"));
				fw.flush();
				fw.close();
			}
		} catch ( IOException e ) {
			e.printStackTrace();
			System.exit(1);
		}
			
	}
	
}
