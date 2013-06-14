package strategy.strategyPokerChallenge.simulation.interfaces;

import strategy.strategyPokerChallenge.data.Action;
import strategy.strategyPokerChallenge.ringclient.ClientRingDynamics;

public interface ISimulator {
	public Action getDecision();
	public void startSimulation(ClientRingDynamics crd);
	public void killTree();
}
