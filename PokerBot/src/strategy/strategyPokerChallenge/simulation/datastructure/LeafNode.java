package strategy.strategyPokerChallenge.simulation.datastructure;

import strategy.strategyPokerChallenge.simulation.interfaces.IArc;
import strategy.strategyPokerChallenge.simulation.interfaces.INode;

/**
 * The LeafNode is the end of the tree.
 */
public class LeafNode extends AbstractNode implements INode {

	private double value;

	public LeafNode(IArc parentArc, long id) {
		super(parentArc, id);
	}

	public LeafNode(long id) {
		super(id);
	}

	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
}
