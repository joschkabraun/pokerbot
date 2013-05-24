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
	public ArrayList<PlayerMoney> playerMoney;
	
	public ShowDown() {
		this.playerHandList = new ArrayList<PlayerHandCombination>();
		this.playerMoney = new ArrayList<PlayerMoney>();
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
		if ( this.playerMoney.isEmpty() )
			return true;
		return false;
	}
	
}
