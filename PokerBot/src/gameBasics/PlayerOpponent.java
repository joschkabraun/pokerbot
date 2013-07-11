package gameBasics;

import cardBasics.CardList;

public class PlayerOpponent extends Player

{
	
	public CardList startCards;
	
	public PlayerOpponent() {
		this.seatBehindBU = new SeatPosition();
	}
	
	public PlayerOpponent( String name, SeatPosition seatBU, int seatAbsolute, CardList yourStartCards, double money )
	{
		this.name = name;
		this.seatBehindBU = new SeatPosition( seatBU );
		this.seatNumberAbsolute = seatAbsolute;
		this.money = money;
		this.startCards = new CardList( yourStartCards );
	}
	
	public PlayerOpponent( Player player, CardList hand )
	{
		this.name = player.name;
		this.seatBehindBU = new SeatPosition( player.seatBehindBU );
		this.seatNumberAbsolute = player.seatNumberAbsolute;
		this.money = player.money;
		this.startCards = new CardList( hand );
	}
	
	public void set( PlayerYou you )
	{
		this.name = you.name;
		this.seatBehindBU = new SeatPosition( you.seatBehindBU );
		this.seatNumberAbsolute = you.seatNumberAbsolute;
		this.money = you.money;
		this.startCards = new CardList( you.startCards );
	}
	
	public boolean isEmpty()
	{
		if ( (this.name == null) && (this.seatBehindBU == null) && (this.seatNumberAbsolute == 0) && (this.money == 0) && (this.startCards == null) )
			return true;
		return false;
	}
	
	@Override
	public String toString() {
		return "name player: " + this.name + ", seat relative to BU: " + this.seatBehindBU.toString() + ", seat number in HH: " + this.seatNumberAbsolute + ", money: " +this.money+
				", start cards: " + this.startCards;
	}	
}
