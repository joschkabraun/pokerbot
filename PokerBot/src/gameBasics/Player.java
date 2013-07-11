package gameBasics;

public class Player

{
	
	public String name = "";
	/**
	 * The seatingPositon relative to the button (BU).
	 */
	public SeatPosition seatBehindBU;
	/**
	 * The seat number on the table the game.
	 * This number is taken from the hand history (HH).
	 */
	public int seatNumberAbsolute = 0;
	public double money = 0.0;
	
	
	public Player()
	{
		this.name = "";
		this.seatNumberAbsolute = 0;
		this.money = 0.0;
		this.seatBehindBU = new SeatPosition();
	}
	
	public Player( String name, SeatPosition seatBU, int seatAbsolute, double money )
	{
		this.name = name;
		this.seatBehindBU = new SeatPosition( seatBU );
		this.seatNumberAbsolute = seatAbsolute;
		this.money = money;
	}
	
	public Player( Player player )
	{
		this.name = player.name;
		this.seatBehindBU = new SeatPosition( player.seatBehindBU );
		this.seatNumberAbsolute = player.seatNumberAbsolute;
		this.money = player.money;
	}
	
	public void set( Player player )
	{
		this.name = player.name;
		this.seatBehindBU = new SeatPosition( player.seatBehindBU );
		this.seatNumberAbsolute = player.seatNumberAbsolute;
		this.money = player.money;
	}
	
	public void setSeatBehindBU( SeatPosition seatBehindBU ) {
		this.seatBehindBU = new SeatPosition( seatBehindBU );
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	
	public void setMoney( double money ) {
		this.money = money;
	}
	
	public void removeM( double money ) {
		this.money -= money;
	}
	
	public void addM( double money ) {
		this.money += money;
	}
	
	public boolean equals( Player p ){
		if ( this.name.equals( p.name ) )
			if ( this.seatBehindBU.equals( p.seatBehindBU ) )
				if ( this.seatNumberAbsolute == p.seatNumberAbsolute )
					if ( this.money == p.money )
						return true;
		return false;
	}
	
	@Override
	public String toString() {
		return "name player: " + this.name + ", seat relative to BU: " + this.seatBehindBU.toString() + ", seat number in HH: " + this.seatNumberAbsolute +", money: "+this.money;
	}
	
}
