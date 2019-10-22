package strategy.nashEquilibrium;

import java.io.Serializable;
import java.util.BitSet;


public class InformationSet implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3225607579214325393L;
	private BitSet bitset;
	
	public InformationSet(History h, BucketSequence bs) {
		this.bitset = (BitSet) h.getBitSet().clone();
		BitSet bsbs = (BitSet) bs.getBitSet().clone();
		for (int i = 48; i < bsbs.length()+48; i++)
			this.bitset.set(i, bsbs.get(i-48));
	}
	
	public InformationSet(BitSet bs) {
		this.bitset = (BitSet) bs.clone();
	}
	
	protected BitSet getBitSet() {
		return this.bitset;
	}
	
	protected byte getLastBucket() {
		int l = ((int) (Math.log10(PCSAlgorithm.buckets)/Math.log10(2))) + 1;			// number of bits per bucket
		byte b = 0, c;
		for (int i = 48; i < this.bitset.length(); i += l) {
			c = (byte) convert(this.bitset.get(i, i+l));
			if (c == 0)
				break;
			else if (c < 0)
				throw new IllegalStateException();
			else if (c < PCSAlgorithm.buckets+1)
				b = c;
			else
				throw new IllegalStateException();
		}
		if (b == 0)
			throw new IllegalStateException();
		return b;
	}
	
	public boolean equals(Object o) {
		if (o instanceof InformationSet)
			if (((InformationSet) o).bitset.equals(this.bitset))
				return true;
		return false;
	}
	
	public Object clone() {
		return new InformationSet((BitSet) this.bitset.clone());
	}
	
	/**This method returns the player whose turn it is at this history.
	 * 
	 * @return {@code false} states this is player 1; {@code true} states this is player 2
	 */
	protected boolean getPlayer() {
		return this.getHistory().getPlayerToAct();
	}
	
	protected History getHistory() {
		return new History(this.bitset.get(0, 48));
	}
	
	public String toBinary() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.bitset.length(); i++)
			if (this.bitset.get(i))
				sb.append('1');
			else
				sb.append('0');
		return sb.toString();
	}
	
	public static long convert(BitSet bits) {
	    long value = 0l;
	    for (int i = 0; i < bits.length(); ++i) {
	      value += bits.get(i) ? (1L << i) : 0L;
	    }
	    return value;
	}
	
	public static void main(String[] args) {
		InformationSet is = new InformationSet(new History(), new BucketSequence(new int[]{1, 1, 1, 4}));
		System.out.println(is.getLastBucket());
	}
	
}
