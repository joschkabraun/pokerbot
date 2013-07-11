package gameBasics;

public class Pot

{
	
	public double money = 0;
	
	public Pot() {
		this.money = 0;
	}
	
	public Pot( double money ) {
		this.money = money;
	}
	
	public Pot( Pot pot ) {
		this.money = pot.money;
	}
	
	public void addM( Pot p ) {
		this.money += p.money;
	}
	
	public void addM( double money ) {
		this.money += money;
	}
	
	public void removeM( double money ) {
		this.money -= money;
	}
	
	@Override
	public String toString() {
		return "money: " + this.money;
	}
	
}
