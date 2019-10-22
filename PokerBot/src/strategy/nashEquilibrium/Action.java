package strategy.nashEquilibrium;

public class Action {
	
	public boolean a;
	public boolean b;
	
	public Action(boolean a, boolean b) {
		this.a = a;
		if (!b)
			if (!a)
				throw new IllegalArgumentException();
		this.b = b;
	}
	
	public Action(int i) {
		switch (i) {
		case -1:					// fold
			this.a = true;
			this.b = false;
			break;
		case 0:						// check/call
			this.a = false;
			this.b = true;
			break;
		case 1:						// bet/raise
			this.a = true;
			this.b = true;
			break;
		default:
			throw new IllegalArgumentException(String.format("The passed integer (%d) could not be assigned.", i));
		}
	}
	
	public int toInt(){
		if (this.a)
			if (this.b)
				return 1;
			else
				return -1;
		else
			if (this.b)
				return 0;
			else
				throw new IllegalStateException("a and b cannot be both true");
	}
	
	public String toString() {
		if (this.a)
			if (this.b)
				return "Bet/Raise";
			else
				return "Fold";
		else
			if (this.b)
				return "Check/Call";
			else
				throw new IllegalStateException("a and b cannot be both true");
	}
	
	public char toChar() {
		if (this.a)
			if (this.b)
				return 'r';
			else
				return 'f';
		else
			if (this.b)
				return 'c';
			else
				throw new IllegalStateException("a and b cannot be both true");
	}
	
	public boolean equals(Object o) {
		if (! (o instanceof Action))
			return false;
		else
			if (((Action) o).a == this.a && ((Action) o).b == this.b)
				return true;
			else
				return false;
	}
	
}
