package strategy.nashEquilibrium;

import java.util.Comparator;

/**This information set comparator compares by the strength of the information sets.
 * Comparing by the strength means comparing by the last bucket of the information set
 * because the last bucket is the important bucket for determining the strength.
 * 
 * @author Joschka
 */
public class InformationSetComparatorByStrength implements Comparator<InformationSet> {
	
	@Override
	public int compare(InformationSet o1, InformationSet o2) {
		byte b1 = o1.getLastBucket(), b2 = o2.getLastBucket();
		if (b1 < b2)
			return -1;
		else if (b1 == b2)
			return 0;
		else
			return 1;
	}
	
}
