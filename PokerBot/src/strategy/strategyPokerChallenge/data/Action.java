package strategy.strategyPokerChallenge.data;

/**
 * The action to be taken  
 *
 * Created on 16.04.2008
 * @author Kami II
 */
public enum Action {
	
	/**
	 * Raise or Bet
	 */
	RAISE {
		public char toChar() {
			return 'r';
		}
	},
	
	/**
	 * Call or Check
	 */
	CALL {
		public char toChar() {
			return 'c';
		}
	},
	
	/**
	 * Fold
	 */
	FOLD {
		public char toChar() {
			return 'f';
		}
	};
	
	/**
	 * method to transform the Action into a char
	 * @return the char representation of the action
	 */
	public abstract char toChar();
	
	/**
	 * parses a char into an action enumeration
	 * @param c the char to be transformed
	 * @return the action the is represented by the given char
	 */
    public static Action parseAction(char c) {
        switch(c) {
        	case 'r':	return RAISE;
        	case 'c':	return CALL;
        	case 'f':	return FOLD;
        }
        return null;
    }
}
