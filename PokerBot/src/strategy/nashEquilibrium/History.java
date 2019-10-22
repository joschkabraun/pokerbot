package strategy.nashEquilibrium;

import gameBasics.GameState;

import java.util.ArrayList;
import java.util.BitSet;


public class History {
	
	private BitSet bitset;
	
	public History() {
		this.bitset = new BitSet();
	}
	
	public History(BitSet bs) {
		this.bitset = bs;
	}
	
	public History(History h, Action a) {
		this.bitset = (BitSet) h.bitset.clone();
		this.add(a);
	}
	
	protected History getHistoryWithoutLastAction() {
		if (this.bitset.length() == 0)
			throw new IllegalStateException();
		int l = this.getNextBitToSet();
		BitSet bs = (BitSet) this.bitset.clone();
		bs.set(l-1, false);
		bs.set(l-2, false);
		return new History(bs);
	}
	
	protected BitSet getBitSet() {
		return this.bitset;
	}
	
	public void add(Action a) {
		int s = this.getNextBitToSet();
		this.bitset.set(s, a.a);
		this.bitset.set(s+1, a.b);
	}
	
	protected Action getLastAction() {
		if (this.bitset.length() == 0)
			throw new IllegalStateException();
		int l = this.getNextBitToSet();
		return new Action(this.bitset.get(l-2), this.bitset.get(l-1));
	}
	
	private int getNextBitToSet() {
		int s = this.bitset.length();
		if (s % 2 == 1)
			return ++s;
		else
			return s;
	}
	
	/**This method returns the player whose turn it is.
	 * 
	 * @return {@code false} for player 1; {@code true} for player 2
	 */
	protected boolean getPlayerToAct() {
		int i = this.getNextBitToSet();
		int j = i / 2;
		if (j % 2 == 0)
			return false;
		else
			return true;
	}
	
	/**lookupInfosets(h) retrieves all of the information
	 * sets consistent with h and the current player P(h)’s range of
	 * possible private outcomes, whether sampled (|~I| = 1) or not.
	 * 
	 * @return all of the information sets consistent with this
	 */
	public InformationSet[] lookupInfosets() {
		ArrayList<InformationSet> alis = new ArrayList<InformationSet>();
		ArrayList<int[]> ali = new ArrayList<int[]>();
		switch (this.getGameState()) {
		case PRE_FLOP:
			for (int a = 1; a < PCSAlgorithm.buckets+1; a++)
				ali.add(new int[]{a});
			break;
		case FLOP:
			for (int a = 1; a < PCSAlgorithm.buckets+1; a++)
				for (int b = 1; b < PCSAlgorithm.buckets+1; b++)
					ali.add(new int[]{a, b});
			break;
		case TURN:
			for (int a = 1; a < PCSAlgorithm.buckets+1; a++)
				for (int b = 1; b < PCSAlgorithm.buckets+1; b++)
					for (int c = 1; c < PCSAlgorithm.buckets+1; c++)
						ali.add(new int[]{a, b, c});
			break;
		case RIVER:
			for (int a = 1; a < PCSAlgorithm.buckets+1; a++)
				for (int b = 1; b < PCSAlgorithm.buckets+1; b++)
					for (int c = 1; c < PCSAlgorithm.buckets+1; c++)
						for (int d = 1; d < PCSAlgorithm.buckets+1; d++)
							ali.add(new int[]{a, b, c, d});
			break;
		case SHOW_DOWN:
			throw new IllegalStateException("This method should not be avoked.");
		}
		for (int[] I : ali)
			alis.add(new InformationSet(this, new BucketSequence(I)));
		InformationSet[] i = new InformationSet[alis.size()];
		for (int j = 0; j < i.length; j++)
			i[j] = alis.get(j);
		return i;
	}
	
	public boolean isTerminal() {
		if (this.bitset.length() == 0)
			return false;
		int l = this.getNextBitToSet();
		if (this.bitset.get(l-2) == true && this.bitset.get(l-1) == false)		// last action was to fold
			return true;
		if (this.bitset.length() < 15)
			return false;
		int counter = 0;
		BitSet bs = this.bitset;
		while (bs.length() > 2) {
			if (bs.get(2) == false && bs.get(3)) {		// call
				++counter;
				bs = bs.get(4, Math.max(4, bs.length()));
				continue;
			}
			bs = bs.get(2, Math.max(2, bs.length()));
		}
		if (bs.length() > 0)
			if (bs.get(1) == false && bs.get(0))		// call
				throw new IllegalStateException("This history is not correct.");
		if (counter < 4)
			return false;
		if (counter == 4)
			return true;
		else
			throw new IllegalStateException("This history is not correct.");
	}
	
	/**
	 * 
	 * @return in which round the game is
	 */
	public GameState getGameState() {
		if (this.bitset.length() < 3)
			return GameState.PRE_FLOP;
		else if (this.bitset.length() < 5)
			if (this.bitset.get(2) && this.bitset.get(3))	// raise
				return GameState.PRE_FLOP;
			else											// call
				return GameState.FLOP;
		int counter = 0;
		BitSet bs = this.bitset;
		while (bs.length() > 2) {
			if (bs.get(2) == false && bs.get(3)) {		// call
				++counter;
				bs = bs.get(4, Math.max(4, bs.length()));
				continue;
			}
			bs = bs.get(2, Math.max(2, bs.length()));
		}
		switch (counter) {
		case 0:
			return GameState.PRE_FLOP;
		case 1:
			return GameState.FLOP;
		case 2:
			return GameState.TURN;
		case 3:
			return GameState.RIVER;
		case 4:
			return GameState.SHOW_DOWN;
		default:
			throw new IllegalStateException("This history is not correct.");
		}
	}
	
	/**Before invoking this method should be checked by isTerminal() whether this the history is over
	 * because the method does not consider the case if the history is over.
	 * 
	 * @return an arranged array of the possible actions at this history; arranged means like {f, c, r}
	 */
	public Action[] A() {
		Action f = new Action(-1), c = new Action(0), r = new Action(1);
		if (this.bitset.length() == 0)
			return new Action[]{f, c, r};
		int l = this.getNextBitToSet()-1;
		Action lA = new Action(this.bitset.get(l-1), this.bitset.get(l));
		int numR = 0;
		if (lA.equals(c))
			return new Action[]{c, r};
		else if (lA.equals(r))
			for (int i = 2; i < l+1; i+=2)
				if (this.bitset.get(l-i) == true && this.bitset.get(l-i+1) == true)
					++numR;
				else
					break;
		if (numR > 4)
			throw new IllegalStateException("This history is not correct.");
		else if (numR == 4)
			return new Action[]{f, c};
		else
			return new Action[]{f, c, r};
	}
	
	/**The small blind is 1, the big blind is 2.
	 * So the small bet is 2 and the big bet is 4.
	 * 
	 * @return the payoff
	 */
	protected int getPayoff() {
		int payoff = 1 + 2;			// small and big blind
		History h = new History();
		Action f = new Action(-1), c = new Action(0);
		for (int i = 1; i < this.getNextBitToSet(); i += 2) {
			Action a = new Action(this.bitset.get(i-1), this.bitset.get(i));
			h.add(a);
			if (a.equals(f))
				break;
			else if (a.equals(c))
				switch (h.getGameState()) {
				case PRE_FLOP:
				case FLOP:
					payoff += 2;
					break;
				case TURN:
				case RIVER:
					payoff += 4;
					break;
				}
			else
				switch (h.getGameState()) {
				case PRE_FLOP:
				case FLOP:
					payoff += 4;
					break;
				case TURN:
				case RIVER:
					payoff += 8;
					break;
				}
		}
		return payoff;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int l = this.getNextBitToSet();
		for (int i = 0; i < l; i+=2)
			sb.append(new Action(this.bitset.get(i), this.bitset.get(i+1)).toChar());
		return sb.toString();
	}
	
	public boolean equals(Object o) {
		if (o instanceof History)
			if (((History) o).bitset.equals(this.bitset))
				return true;
		return false;
	}
	
	public Object clone() {
		return new History((BitSet) this.bitset.clone());
	}
	
	public static void main(String[] args) {
		History h = new History();
		Action  f = new Action(-1), c = new Action(0), r = new Action(1);
		h.add(c);
		h.add(r);
		h.add(c);
		h.add(f);
		System.out.printf("h.isTerminal(): %b%n", h.isTerminal());
		System.out.printf("h.getGameState(): %s%n", h.getGameState().toString());
		Action[] A = h.A();
		System.out.println("results of h.A():");
		for (Action a : A)
			System.out.println(a);
		System.out.println(h);
		if (h.getPlayerToAct())
			System.out.println("Player 2.");
		else
			System.out.println("Player 1.");
		System.out.println("h.getPayoff(): " + h.getPayoff());
	}
	
}
