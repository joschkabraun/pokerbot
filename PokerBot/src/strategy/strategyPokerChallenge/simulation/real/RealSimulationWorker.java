package strategy.strategyPokerChallenge.simulation.real;

import strategy.strategyPokerChallenge.simulation.interfaces.INode;
import strategy.strategyPokerChallenge.simulation.real.RealGame;
import strategy.strategyPokerChallenge.simulation.real.RealPathSimulator;

public class RealSimulationWorker implements Runnable {
	

	private INode root;
	private RealGame game;
	private INode startNode;
	private Thread realSimulationWorker;

	public RealSimulationWorker(INode root, RealGame game, INode child) {
		this.game = game;
		this.root = root;
		this.startNode = child;
		this.realSimulationWorker = new Thread(this);
		this.realSimulationWorker.start();
	}

	public void run() {
		while (realSimulationWorker != null) {
			RealPathSimulator rps = new RealPathSimulator(root, new RealGame(game));
			rps.simulatePath(startNode);
		}
	}
	
	public synchronized void stopWork() {
		this.realSimulationWorker = null;
	}
}
