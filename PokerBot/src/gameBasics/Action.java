package gameBasics;

public class Action

{
	
	/**
	 * The type of the action. For example call, bet, fold, collect(ed) ...
	 */
	public String actionName;
	public double money = -100;
	
	public Action()
	{
	}
	
	public Action( String action, double money )
	{
		this.actionName = action;
		this.money = money;
	}
	
	public Action( Action action )
	{
		this.actionName = action.actionName;
		this.money = action.money;
	}
	
	public Action( String action )
	{
		this.actionName = action;
	}
	
	@Override
	public String toString()
	{
		if ( this.money == -100 )
			return "(" + this.actionName + ")";
		return "(" + this.actionName + ", " + this.money + ")";
	}
	
	public void set( String action )
	{
		this.actionName = action;
	}
	
	public void set( String action, double money )
	{
		this.actionName = action;
		this.money = money;
	}
	
	public void set( Action action )
	{
		this.actionName = action.actionName;
		this.money = action.money;
	}
	
	public boolean isEmpty()
	{
		if ( ((this.actionName == null) || (this.actionName.length() == 0)) && (this.money == -100) )
			return true;
		return false;
	}
	
}
