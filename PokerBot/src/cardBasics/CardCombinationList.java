package cardBasics;

import java.util.*;

/**
 * 
 * @author Joschka J. Braun
 * @version 1.0
 */
@SuppressWarnings("serial")
public class CardCombinationList extends ArrayList<CardCombination>

{
	
	public CardCombinationList( CardCombination... cardCombinations )
	{
		for ( CardCombination cardCombination : cardCombinations )
			this.add( cardCombination );
	}
	
	Comparator<CardCombination> comp = new CardCombination();
	
	public void sortByCombinationValue()
	{
		CardCombination[] thisAsArray = new CardCombination[ this.size() ];
		
		for ( int i = 0; i < this.size(); i++ )
			thisAsArray[ i ] = this.get( i );
		
		Arrays.sort( thisAsArray, comp );
		
		this.clear();
		
		for ( int i = 0; i < thisAsArray.length; i++ )
			this.add( thisAsArray[ i ] );
	}
	
}
