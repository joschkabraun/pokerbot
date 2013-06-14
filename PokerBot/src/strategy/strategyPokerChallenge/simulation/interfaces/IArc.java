package strategy.strategyPokerChallenge.simulation.interfaces;

import strategy.strategyPokerChallenge.data.Action;

public interface IArc {
	public double getValue();
	public void addValue(double value);
	public INode getParent();
	public void setParent(INode parent);
	public INode getChild();
	public Action getDecision();
	public boolean isDeterministic();
	public int[] getRandomBucket();
	public int getSimulationCount();
	public void increaseSimulationCount();
	public boolean equals(Object obj);
	public int hashCode();
	public void setChild(INode child);
	public int getWin();
	public double getWinRatio();
}
