package gameBasics;

public enum GameState {
	
	STARTING {
		public String toString() {
			return "Starting";
		}
	},
	
	PRE_FLOP {
		public String toString() {
			return "Pre Flop";
		}
	},
	
	FLOP {
		public String toString() {
			return "Flop";
		}
	},
	
	TURN {
		public String toString() {
			return "Turn";
		}
	},
	
	RIVER {
		public String toString() {
			return "River";
		}
	},
	
	SHOW_DOWN {
		public String toString() {
			return "Show Down";
		}
	},
	
	SUMMARY {
		public String toString() {
			return "Summary";
		}
	};
	
}
