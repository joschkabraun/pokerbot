package handHistory;

import gameBasics.Player;

/**
 * This class implements the object PlayerMoney.
 * The object PlayerMoney is an object turning data types Player and Double to one data type.
 * The data type consists of a player and the money he/she collected in the round.
 * 
 * @author Joschka J. Braun
 * @version 1.0
 */
public class PlayerMoney

{
	
	/**
	 * The player who collected the money.
	 */
	public Player player;
	
	/**
	 * The money the player collected.
	 */
	public double money = 0;
	
	public PlayerMoney() {
		this.player = new Player();
	}
	
	public PlayerMoney( Player player, double money ) {
		this.player = new Player( player );
		this.money = money;
	}
	
	public PlayerMoney( PlayerMoney plMoney ) {
		this.player = new Player( plMoney.player );
		this.money = plMoney.money;
	}
	
	@Override
	public String toString() {
		return "(name player: " + this.player.name + ", money: " + this.money + ")";
	}
	
}
