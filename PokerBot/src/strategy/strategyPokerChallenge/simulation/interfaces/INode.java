package strategy.strategyPokerChallenge.simulation.interfaces;

import java.util.Vector;

public interface INode {
	public IArc getParentArc();
	public Vector<IArc> getChildArcs();
	public int children();
	public void addArc(IArc arc);
	public void initParent(IArc arc);
	public boolean equals(Object obj);
	public int hashCode();
}
