package strategy.strategyPokerChallenge.simulation.real;

import strategy.strategyPokerChallenge.ringclient.ClientRingDynamics;
import strategy.strategyPokerChallenge.simulation.datastructure.DecisionArc;
import strategy.strategyPokerChallenge.simulation.datastructure.LeafNode;
import strategy.strategyPokerChallenge.simulation.interfaces.IArc;
import strategy.strategyPokerChallenge.simulation.interfaces.INode;
import strategy.strategyPokerChallenge.simulation.real.datastructure.RealPlayerNode;

public class RealPathSimulator {
	private INode root;

	private RealGame game;

	public RealPathSimulator(INode root, ClientRingDynamics crd) {
		this.root = root;
		this.game = new RealGame(crd, root);
	}

	public RealPathSimulator(INode root, RealGame game) {
		this.root = root;
		this.game = game;
	}

	public void simulatePath(INode node) {
		int count = 0;
		while (!(node instanceof LeafNode) && count < 150) {
			if (node instanceof RealPlayerNode) {
				DecisionArc arc = game.getDecision((RealPlayerNode) node);
				node = arc.getChild();
			} 
			count++;
		}
		if (node instanceof LeafNode) {
			LeafNode leaf = (LeafNode) node;
			backTrace(node.getParentArc(), leaf.getValue());
		}
	}

	private void backTrace(IArc arc, double value) {
		if (root != null && arc!=null) {
			INode parent = arc.getParent();
			
			while (parent!=null && !parent.equals(root)) {
				arc.addValue(value);
				arc = parent.getParentArc();
				parent = arc.getParent();
			}
			arc.addValue(value);
		}
	}

	public void startPath(INode startNode) {
		this.simulatePath(startNode);
	}
}
