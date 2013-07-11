package strategy.strategyPokerChallenge.simulation.datastructure;

import strategy.strategyPokerChallenge.simulation.interfaces.IArc;
import strategy.strategyPokerChallenge.simulation.interfaces.INode;
import strategy.strategyPokerChallenge.data.Action;

public abstract class AbstractArc implements IArc {

	protected double value;
	protected INode child;
	protected INode parent;
	protected int simulationCount;
	private int win;
	
	public AbstractArc(INode parent, INode child) {
		this.parent = parent;
		this.child = child;
		this.value = 0;
		this.simulationCount = 0;
	}

	public void addValue(double value) {
		increaseSimulationCount();
		if(value > 0) this.win++;
		this.value+=value;
	}
	
	
	public double getValue() {
		return value / ((double)simulationCount);
	}

	public INode getChild() {
		return child;
	}
	
	public void setChild(INode child) {
		this.child = child;
	}

	public abstract Action getDecision();

	public INode getParent() {
		return parent;
	}

	public void setParent(INode parent) {
		this.parent = parent;
	}
	public int getSimulationCount() {
		return this.simulationCount;
	}
	public void increaseSimulationCount() {
		simulationCount++;
	}
	
	/**
	 * @return the win
	 */
	public int getWin() {
		return win;
	}
	
	public double getWinRatio() {
		return (double) win / (double) simulationCount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AbstractArc))
			return false;
		final AbstractArc other = (AbstractArc) obj;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		return true;
	}

}
