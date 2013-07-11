package bots;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

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
	
	public static void main(String[] args)
	{
		
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
		
		Bot_v1_2_0 BLU = new Bot_v1_2_0( main, "BotLeftDown-v1.1.0", outBLU2I, Bot_v1_1_0Tables.LEFT_UP, namePlayerYou );
		Bot_v1_2_0 BLD = new Bot_v1_2_0( main, "BotLeftDown-v1.1.0", outBLD2I, Bot_v1_1_0Tables.LEFT_DOWN, namePlayerYou );
		Bot_v1_2_0 BRU = new Bot_v1_2_0( main, "BotLeftDown-v1.1.0", outBRU2I, Bot_v1_1_0Tables.RIGHT_UP, namePlayerYou );
		Bot_v1_2_0 BRD = new Bot_v1_2_0( main, "BotLeftDown-v1.1.0", outBRD2I, Bot_v1_1_0Tables.RIGHT_DOWN, namePlayerYou );
		Interrupter interrupter = new Interrupter( main, "Interrupter", inBLU2I, inBLD2I, inBRU2I, inBRD2I, BLU, BLD, BRU, BRD );
		
		BLU.start();
		BLD.start();
		BRU.start();
		BRD.start();
		interrupter.start();
		
	}
	
}
