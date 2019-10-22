package cardBasics;

import java.util.Comparator;
import cardBasics.Card;

/**
 * 
 * @author Joschka J. Braun
 * @version 1.0
 */
public class CardComparatorByColourToInt implements Comparator<Card>

{
	
	@Override
	public int compare( Card card1, Card card2 )
	{
		if ( card1.getSuit().toInt() < card2.getSuit().toInt() )
			return -1;
		else if ( card1.getSuit().toInt() == card2.getSuit().toInt() )
			return 0;
		else
			return 1;
	}
	
	
}
