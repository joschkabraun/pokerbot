package strategy.strategyPokerChallenge.simulation.datastructure;

import strategy.strategyPokerChallenge.data.Action;
import strategy.strategyPokerChallenge.simulation.interfaces.IArc;
import strategy.strategyPokerChallenge.simulation.interfaces.INode;

public class DecisionArc extends AbstractArc implements IArc{
	private Action decision;

	public DecisionArc(INode parent, INode child, Action decision) {
		super(parent, child);
		this.decision = decision;
	}

	public DecisionArc(INode parent, Action a) {
		super(parent,null);
		this.decision = a;
	}

	@Override
	public Action getDecision() {
		return this.decision;
	}
	
	public boolean isDeterministic() {
		return true;
	}

	public int[] getRandomBucket() {
		return new int[]{};
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((decision == null) ? 0 : decision.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof DecisionArc))
			return false;
		final DecisionArc other = (DecisionArc) obj;
		if (decision == null) {
			if (other.decision != null)
				return false;
		} else if (!decision.equals(other.decision))
			return false;
		return true;
	}
}
