package handHistory;

import java.util.ArrayList;

public class ShowDown

{
	
	/**
	 * The list of all players, them hand cards and card combination.
	 */
	public ArrayList<PlayerHandCombination> playerHandList;
	
	/**
	 * The list of all players who collected money and the money they collected.
	 */
	public ArrayList<PlayerMoney> playerMoneyList;
	
	public ShowDown() {
		this.playerHandList = new ArrayList<PlayerHandCombination>();
		this.playerMoneyList = new ArrayList<PlayerMoney>();
	}
	
	public ShowDown( ArrayList<PlayerHandCombination> playerHandList )
	{
		this.playerHandList = playerHandList;
	}
	
	public ShowDown( ShowDown showDown )
	{
		this.playerHandList = showDown.playerHandList;
	}
	
	public boolean isEmpty() {
		if ( this.playerHandList.size() == 0 && this.playerMoneyList.size() == 0 )
			return true;
		return false;
	}
	
	@Override
	public String toString() {		
		String ret = "show down:";
		if ( this.playerHandList.size() != 0 ) {
			ret += String.format("player hand list:%n");
			for ( PlayerHandCombination phc : this.playerHandList )
				ret += String.format(phc + "%n");
		}
		if ( this.playerMoneyList.size() != 0 ) {
			ret += String.format("player money list:%n");
			for ( PlayerMoney pm : this.playerMoneyList )
				ret += String.format(pm + "%n");
		}
		return ret;
	}
	
}
