package bots;

import java.io.IOException;
import java.io.PipedInputStream;
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
	 * These are the stream for the communication of the thread to the Interrupter.
	 */
	public PipedInputStream in1;
	public PipedInputStream in2;
	public PipedInputStream in3;
	public PipedInputStream in4;
	
	/**
	 * These are the threads which will killed when the bot has an exception.
	 */
	public Thread thread1;
	public Thread thread2;
	public Thread thread3;
	public Thread thread4;
	
	public static final Lock lock = new ReentrantLock();
	
	public Interrupter( ThreadGroup main, String string, PipedInputStream in1, PipedInputStream in2, PipedInputStream in3, PipedInputStream in4,
			Thread thread1, Thread thread2, Thread thread3, Thread thread4 ) {
		super( main, string );
		this.in1 = in1;
		this.in2 = in2;
		this.in3 = in3;
		this.in4 = in4;
		this.thread1 = thread1;
		this.thread2 = thread2;
		this.thread3 = thread3;
		this.thread4 = thread4;
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
				if ( this.in1.available() == 0 )
					continue;
				else if ( this.in1.read() == 1 )
					exit_whole();
				if ( this.in2.available() == 0 )
					continue;
				else if ( this.in2.read() == 1 )
					exit_whole();
				if ( this.in3.available() == 0 )
					continue;
				else if ( this.in3.read() == 1 )
					exit_whole();
				if ( this.in4.available() == 0 )
					continue;
				else if ( this.in4.read() == 1 )
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
		try {
			Runtime.getRuntime().exec( "taskkill /F /IM casino.exe" );
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.thread1.interrupt();
		this.thread2.interrupt();
		this.thread3.interrupt();
		this.thread4.interrupt();
		this.interrupt();
		System.exit( 0 );
	}
	
	/**
	 * This method kills just the (whole) bot.
	 */
	public void exit_bot()
	{
		this.thread1.interrupt();
		this.thread2.interrupt();
		this.thread3.interrupt();
		this.thread4.interrupt();
		this.interrupt();
		System.exit( 0 );
	}
	
}
