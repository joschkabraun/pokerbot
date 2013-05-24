package gameBasics;

import java.util.ArrayList;

public class Table

{
	
	public PlayerYou you;
	public ArrayList<PlayerOpponent> opponent;
	public int numberOfPlayers;
	public int maxNumberOfPlayers;
	
	
	public Table( PlayerYou you, ArrayList<PlayerOpponent> opponent, int maxNumberOfPlayers )
	{
		this.you = you;
		this.opponent = opponent;
		this.numberOfPlayers = opponent.size() + 1;
		this.maxNumberOfPlayers = maxNumberOfPlayers;
	}
	
	@Override
	public String toString() {
		return "player you: " + this.you.toString() + ", list opponents: " + this.opponent.toString() +
				", number players: " +this.numberOfPlayers + ", max number players: " + this.maxNumberOfPlayers;
	}
	
}
