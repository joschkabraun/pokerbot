package handHistory;

import gameBasics.Player;
import gameBasics.Action;

/**
 * This class implements the object PlayerAction.
 * The object PlayerAction is an object turning data types Player and Action to one data type.
 * The data type consists of a player and an action the player did.
 * 
 * @author Joschka J. Braun
 * @version 1.0
 */
public class PlayerAction

{
	
	public Player player;
	public Action action;
	
	public PlayerAction( Player player, Action action )
	{
		this.player = new Player( player );
		this.action = new Action( action );
	}
	
	@Override
	public String toString() {
		return "name player: " + this.player.name + ", action: " + this.action;
	}
	
}
