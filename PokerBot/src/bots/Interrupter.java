package bots;

import java.io.IOException;
import java.io.PipedInputStream;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jhook.Keyboard;
import jhook.KeyboardListener;

/**
 * This class was implemented for interrupting the whole bot. The class interrupts the bot when the user moves the mouse.
 * 
 * @author Joschka J. Braun
 */
public class Interrupter extends Thread

{
	
	/**
	 * These are the stream for the communication of the threads to the Interrupter.
	 */
	private ArrayList<PipedInputStream> ins;
	
	/**
	 * These are the threads which will killed when the bot has an exception.
	 */	
	private ArrayList<Thread> threads;
	
	/**
	 * The whole bot with the main-method.
	 */
	private Bot bot;
	
	public static final Lock lock = new ReentrantLock();
	
	public Interrupter( ThreadGroup main, String string, ArrayList<PipedInputStream> ins, ArrayList<Thread> threads, Bot bot ) {
		super( main, string );
		this.ins = new ArrayList<PipedInputStream>();
		this.threads = new ArrayList<Thread>();
		if ( ins.size() != threads.size() )
			throw new IllegalArgumentException("There have to be the same number of PipedInputStreams as Threads!");
		for ( PipedInputStream pis : ins )
			this.ins.add(pis);
		for  ( Thread t : threads )
			this.threads.add(t);
		this.bot = bot;
	}
	
	@Override
	public void run()
	{
		
		Keyboard keyboard = new Keyboard();
		keyboard.addListener( new KeyboardListener() {
			public void keyPressed( boolean keydown, int vk ) {
				if ( vk == 27 )
					exit_whole();
				else if ( vk == 113 )
					exit_bot();
			}
		} );
		
		
		try {
			while ( true ) {
				lock.lock();
				for ( int i = 0; i < ins.size(); i++ )
					if ( this.ins.get(i).available() == 0 )
						continue;
					else if ( this.ins.get(i).read() == 1 )
						exit_whole();
				lock.unlock();
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			exit_whole();
		}
	}
	
	/**
	 * This method kills WinnerPoker and the whole bot.
	 */
	public void exit_whole()
	{
		bot.exit();
		try {
			Runtime.getRuntime().exec( "taskkill /F /IM casino.exe" );
		} catch (IOException e) {
			e.printStackTrace();
		}
		for ( Thread t : threads )
			t.interrupt();
		this.interrupt();
		System.exit( 0 );
	}
	
	/**
	 * This method kills just the (whole) bot.
	 */
	public void exit_bot()
	{
		bot.exit();
		for ( Thread t : threads )
			t.interrupt();
		this.interrupt();
		System.exit( 0 );
	}
	
}
