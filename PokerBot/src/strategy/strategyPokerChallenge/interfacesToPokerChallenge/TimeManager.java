package strategy.strategyPokerChallenge.interfacesToPokerChallenge;

import strategy.strategyPokerChallenge.simulation.real.RealSimulator;

public class TimeManager extends Thread {
	
	private long time;
	private strategy.strategyPokerChallenge.data.Action act;
	private RealSimulator rs;
	
	public TimeManager(RealSimulator rs) {
		this.rs = rs;
	}
	
	protected synchronized gameBasics.Action getDecision( long time ) {
		this.time = time;
		this.run();
		
		switch ( this.act ) {
		case FOLD:
			return new gameBasics.Action("fold");
		case CALL:
			return new gameBasics.Action("call");
		case RAISE:
			return new gameBasics.Action("raise");
		default:
			throw new IllegalStateException("It was not possible to find an action!");
		}
	}
	
	@Override
	public void run() {
		@SuppressWarnings("unused")
		long start = System.currentTimeMillis(), now;
		while ( (now = System.currentTimeMillis()) < (long) (start + this.time) ) {
		}
		this.act = rs.getDecision();
	}
	
}
