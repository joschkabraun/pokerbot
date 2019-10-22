package strategy.nashEquilibrium;

import java.util.BitSet;
import java.util.Comparator;

/**This information set comparator compares by the BitSet of the information sets.
 * Comparing by the BitSet means comparing by the number which the BitSets represents.
 * 
 * @author Joschka
 */
public class InformationSetComparatorBitSet implements Comparator<InformationSet> {

	@Override
	public int compare(InformationSet o1, InformationSet o2) {
		long r1 = convert(o1.getBitSet()), r2 = convert(o2.getBitSet());
		if (r1 < r2)
			return -1;
		else if (r1 == r2)
			return 0;
		else
			return 1;
	}
	
	private static long convert(BitSet bits) {
	    long value = 0l;
	    for (int i = 0; i < bits.length(); ++i) {
	      value += bits.get(i) ? (1L << i) : 0L;
	    }
	    return value;
	}
	
}
