package cardBasics;

import java.util.Comparator;
import cardBasics.CardCombination;

public class CardCombinationComparator implements Comparator<CardCombination>

{
	
	@Override
	public int compare( CardCombination cardCombination1, CardCombination cardCombination2 )
	{
		if ( cardCombination1.combinationValue < cardCombination2.combinationValue )
			return -1;
		else if( cardCombination1.combinationValue == cardCombination2.combinationValue )
		{
			if ( cardCombination1.highCard.getRank().toInt() < cardCombination2.highCard.getRank().toInt() )
				return -1;
			else if ( cardCombination1.highCard.getRank().toInt() == cardCombination2.highCard.getRank().toInt() )
				return 0;
			else
				return 1;
		}
		else
			return 1;
	}
	
}
