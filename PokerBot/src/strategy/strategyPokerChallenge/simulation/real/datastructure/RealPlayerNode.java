package strategy.strategyPokerChallenge.simulation.real.datastructure;

import java.util.Arrays;

import strategy.strategyPokerChallenge.simulation.datastructure.AbstractNode;
import strategy.strategyPokerChallenge.simulation.interfaces.INode;
import strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card;
import strategy.strategyPokerChallenge.data.Bucket;

public class RealPlayerNode extends AbstractNode implements INode {
	private Card[] hole;
	private int bucket;
	private String name;

	public RealPlayerNode(RealPlayerState player, long nodeId) {
		super(nodeId);
		this.bucket = player.getBucket();
		this.hole = player.getHole();
		this.name = player.getName();
	}

	public RealPlayerNode(long node_id, Card[] hole, String name) {
		super(node_id);
		this.hole = hole;
		this.bucket = Bucket.getBucket(hole);
		this.name = name;
	}

	public Card[] getHole() {
		return hole;
	}

	public int getBucket() {
		return bucket;
	}
	
	public String toString() {
		return "Name: " + name + " Hole: " + Arrays.deepToString(hole);
	}

}
