package strategy.strategyPokerChallenge.simulation.real;

import strategy.strategyPokerChallenge.history.GlobalRoundData;
import strategy.strategyPokerChallenge.history.History;

import java.util.Random;

import strategy.strategyPokerChallenge.ringclient.ClientRingDynamics;
import strategy.strategyPokerChallenge.simulation.datastructure.DecisionArc;
import strategy.strategyPokerChallenge.simulation.datastructure.LeafNode;
import strategy.strategyPokerChallenge.simulation.interfaces.INode;
import strategy.strategyPokerChallenge.simulation.real.datastructure.RealPlayerNode;
import strategy.strategyPokerChallenge.simulation.real.datastructure.RealPlayerState;
import strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card;
import strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.HandAnalysis;
import strategy.strategyPokerChallenge.data.Action;
import strategy.strategyPokerChallenge.data.CONSTANT;
import strategy.strategyPokerChallenge.data.GameState;
import strategy.strategyPokerChallenge.data.SimulationGuide;

public class RealGame {
	private SimulationGuide simulationGuides;
	private Random random;
	private RealPlayerState[] playerStates;
	private int actualSeat;
	private int bettingRound;
	private int round;
	private int start_round;
	private int ownSeat;
	private double maxInPot;
	private CardGenerator cardGenerator;
	private Card[] board;
	private ClientRingDynamics crd;
	private boolean initialize = false;

	public RealGame(ClientRingDynamics crd, INode root) {
		this.crd = crd;
		this.random = new Random();
		this.cardGenerator = new CardGenerator();
		if (crd.board != null) {
			// copy board
			this.board = new Card[crd.board.length];
			for (int i = 0; i < crd.board.length; i++) {
				Card c = crd.board[i];
				if (c != null)
					this.board[i] = new Card(c.rank, c.suit);
				else
					this.board[i] = null;
			}
			// remove board cards
			for (Card c : board) {
				if (c != null) {
					cardGenerator.removeCard(c);
				}
			}
		}

		this.playerStates = new RealPlayerState[CONSTANT.PLAYER_COUNT];

		this.simulationGuides = new SimulationGuide(crd);
		for (int i = 0; i < CONSTANT.PLAYER_COUNT; i++) {
			// if (i != ownSeat)
			this.playerStates[i] = new RealPlayerState(crd.active[i], crd.canRaiseNextTurn[i], crd.inPot[i], i, crd.seatToPlayer(i));
		}

		this.actualSeat = crd.seatTaken;
		this.ownSeat = this.actualSeat;
		bettingRound = crd.roundBets;
		round = crd.roundIndex;
		start_round = round;
		maxInPot = crd.getMaxInPot();

		RealPlayerNode pNode = (RealPlayerNode) root;
		RealPlayerState player = playerStates[ownSeat];
		player.addPlayerNode(pNode);
		for (Card c : pNode.getHole()) {
			cardGenerator.removeCard(c);
		}
	}

	public RealGame(RealGame game) {
		this.initialize = game.initialize;
		this.cardGenerator = new CardGenerator(game.cardGenerator);
		this.random = new Random();
		this.playerStates = new RealPlayerState[CONSTANT.PLAYER_COUNT];
		this.crd = game.crd;
		this.simulationGuides = new SimulationGuide(crd);
		for (int i = 0; i < CONSTANT.PLAYER_COUNT; i++) {
			this.playerStates[i] = new RealPlayerState(game.playerStates[i]);
		}
		this.actualSeat = game.actualSeat;
		this.bettingRound = game.bettingRound;
		this.round = game.round;
		this.ownSeat = game.ownSeat;
		this.maxInPot = game.maxInPot;
		if (game.board != null) {
			// clone board
			this.board = new Card[game.board.length];
			for (int i = 0; i < game.board.length; i++) {
				Card c = game.board[i];
				if (c != null)
					this.board[i] = new Card(c.rank, c.suit);
				else
					this.board[i] = null;
			}
		}
		this.start_round = game.start_round;
	}

	public INode getNextNode() {
		if(initialize) {
			generateNextRound();
			initialize  = false;
		}
		RealPlayerState player;
		int numActive = getNumActivePlayer();
		// Game ends
		if (numActive <= 1)
			return actualLeafNode();
		// new Round starts
		if (getNumPlayerToAct() == 0) {
			startNewRound();
		}

		player = getNextPlayerCanAct();
		if (player.getHole() == null) {
			player.setHole(asignHole());
		}
		if (round < 4) {
			actualSeat = player.getSeat();
			return new RealPlayerNode(player, CONSTANT.getNodeId());

		} else
			return actualLeafNode();
	}

	private void generateNextRound() {
		switch (round) {
		case 1:
			generateFlop();
			break;
		case 2:
			generateTurnRiver();
			break;
		case 3:
			generateTurnRiver();
			break;
		}
	}
	private void startNewRound() {
		round++;
		switch (round) {
		case 1:
			generateFlop();
			break;
		case 2:
			generateTurnRiver();
			break;
		case 3:
			generateTurnRiver();
			break;
		}

		bettingRound = 0;
		for (RealPlayerState p : playerStates) {
			if (p.isActive())
				p.setCanRaiseNextRound(true);
		}
		actualSeat = getNextSeatCanAct();
	}

	private void generateTurnRiver() {
		for (int i = 0; i < this.board.length; i++) {
			if (board[i] == null) {
				board[i] = cardGenerator.getNextAndRemoveCard();
				break;
			}
		}

	}

	private void generateFlop() {
		this.board = new Card[5];
		for (int i = 0; i < 3; i++)
			this.board[i] = cardGenerator.getNextAndRemoveCard();
	}

	private int getNextSeatCanAct() {
		for (int i = 0; i < CONSTANT.PLAYER_COUNT; i++) {
			RealPlayerState p = playerStates[i];
			if (p.canAct(maxInPot))
				return i;
		}
		return 0;
	}

	private Card[] asignHole() {

		int bucket = 1;
		int maxBucket = 4;
		GlobalRoundData grd = History.getHistory().getCurrent();

		if (History.getHistory().isActive()) {
			double foldRatio = grd.getPlayerRatio(playerStates[actualSeat]
					.getPlayerSeat(), GameState.PRE_FLOP, Action.FOLD);
			double raiseRatio = grd.getPlayerRatio(playerStates[actualSeat]
					.getPlayerSeat(), GameState.PRE_FLOP, Action.CALL);
			if (crd.inPot[actualSeat] >= 4)
				foldRatio += raiseRatio;
			else {
				double callRatio = foldRatio + raiseRatio;
				if (callRatio <= 0.70) {
					maxBucket = 0;
				} else if (callRatio <= 0.77) {
					maxBucket = 1;
				} else if (callRatio <= 0.88) {
					maxBucket = 2;
				} else if (callRatio <= 0.96) {
					maxBucket = 3;
				} else
					maxBucket = 4;
			}
			if (foldRatio <= 0.70) {
				bucket = 0;
			} else if (foldRatio <= 0.77) {
				bucket = 1;
			} else if (foldRatio <= 0.88) {
				bucket = 2;
			} else if (foldRatio <= 0.96) {
				bucket = 3;
			} else
				bucket = 4;
		}
		// {0.65,0.79,0.9,0.97,1.0};
		Card[] hole = new Card[2];
		if (crd.roundIndex == 0 && !calledAlready(actualSeat)) {
			hole = cardGenerator.getHole();
		} else {
			if (crd.roundIndex == 0 || (crd.roundIndex == 1 && !calledAlready(actualSeat))) {
				hole = cardGenerator.getHole(bucket, maxBucket);
			}

			else {
				 if(History.getHistory().isActive()) {
				 double foldRatio = grd.getPlayerRatio(playerStates[actualSeat].getPlayerSeat(), GameState.FLOP, Action.FOLD);
				 double low = 1.0/3.0;
				 double high = 2.0/3.0;
				 double low_value = 0;
				 double high_value = 0;
				 if(foldRatio < low) {
					 low_value = low * Math.pow(foldRatio * (1.0/low), 2);
				 }
				 else if(foldRatio < high) {
					 high_value = low * Math.pow((foldRatio - low) * (1.0/low), 2);
					 low_value = low;
				 }
				 else {
					 high_value = foldRatio;
					 low_value = low;
				 }
				 
				 double r = random.nextDouble();
									
				 if(r<low_value)hole = cardGenerator.getHole(bucket,maxBucket,
				 board);
				 else if(r < low_value + high_value) hole = cardGenerator.getHoleHigh(bucket, maxBucket, board);
				 else hole = cardGenerator.getHole(bucket, maxBucket);
				 }
				 else {
					 hole = cardGenerator.getHole(bucket+1, maxBucket);
				 }

			}
		}
		return hole;
	}

	private boolean calledAlready(int player) {
		if (crd.roundBets == 0 && crd.inPot[player] < 2)
			return false;
		else if (crd.inPot[player] < 2)
			return false;
		else {
			return true;
		}
	}

	
	private int getBetSize() {
		if (round < 2)
			return 2;
		else
			return 4;
	}

	private int getNumPlayerToAct() {
		int actPlayers = 0;
		for (RealPlayerState player : playerStates) {
			if (player.canAct(maxInPot))
				actPlayers++;
		}
		return actPlayers;
	}

	private int getNumActivePlayer() {
		int activePlayers = 0;
		for (RealPlayerState player : playerStates) {
			if (player.isActive())
				activePlayers++;
		}
		return activePlayers;
	}

	private INode actualLeafNode() {
		LeafNode leaf = new LeafNode(CONSTANT.getNodeId());
		double[] value = new double[CONSTANT.PLAYER_COUNT];
		if (!playerStates[ownSeat].isActive()) {
			value[ownSeat] = -playerStates[ownSeat].getAmountIn();
			leaf.setValue(value[ownSeat]);
			return leaf;
		}
		int pot = 0;
		boolean[] active = new boolean[CONSTANT.PLAYER_COUNT];
		Card[][] hole = new Card[CONSTANT.PLAYER_COUNT][2];
		for (int i = 0; i < CONSTANT.PLAYER_COUNT; i++) {
			RealPlayerState player = playerStates[i];
			if (player.isActive()) {
				active[i] = true;
				if (player.getHole() == null) {
					player.setHole(asignHole());
				}
				hole[i] = player.getHole();
			}
			pot += player.getAmountIn();
			value[i] = -player.getAmountIn();
		}

		if (getNumActivePlayer() == 1) {
			value[ownSeat] = pot - playerStates[ownSeat].getAmountIn();
			leaf.setValue(value[ownSeat]);
			return leaf;
		}
		determineWinner(active, pot, value);
		if (value[ownSeat] > 0)
			value[ownSeat] -= playerStates[ownSeat].getAmountIn();

//		if (CONSTANT.DEBUG_IMMI) {
//			 System.out.println("Board: " + Arrays.deepToString(board)
//			 + " Holes: " + Arrays.deepToString(hole) + " Active: "
//			 + Arrays.toString(active) + " OwnSeat: " + ownSeat);
//			 System.out.println("Values: " + Arrays.toString(value));
//		}

		leaf.setValue(value[ownSeat]);
		return leaf;
	}

	public void determineWinner(boolean[] playersIn, int potSize, double[] value) {

		boolean[] potWinners = new boolean[CONSTANT.PLAYER_COUNT];
		for (int i = 0; i < CONSTANT.PLAYER_COUNT; i++) {
			potWinners[i] = false;
		}

		int firstIndex = 0;
		while (playersIn[firstIndex] == false) {
			firstIndex++;
		}
		Card[][] playersToCompare = new Card[2][];
		playersToCompare[0] = playerStates[firstIndex].getHole();
		potWinners[firstIndex] = true;

		for (int i = firstIndex + 1; i < CONSTANT.PLAYER_COUNT; i++) {
			if (playersIn[i]) {
				playersToCompare[1] = playerStates[i].getHole();

				int winnerIndex = HandAnalysis.determineWinner(
						playersToCompare, board);
				if (winnerIndex == -1) {
					// tie
					potWinners[i] = true;
				} else if (winnerIndex == 1) {
					// new winner
					potWinners[i] = true;
					for (int j = 0; j < i; j++) {
						potWinners[j] = false;
					}
					playersToCompare[0] = playerStates[i].getHole();
					;
				}
			}
		}

		int numWinners = 0;
		for (int i = 0; i < CONSTANT.PLAYER_COUNT; i++) {
			if (potWinners[i]) {
				numWinners++;
			}
		}

		// Amount given to every winner.
		int baseAmountWon = potSize / numWinners;
		int remainder = potSize - (baseAmountWon * numWinners);

		for (int i = 0; i < CONSTANT.PLAYER_COUNT; i++) {
			if (potWinners[i]) {
				value[i] = baseAmountWon;
				if (remainder > 0) {
					value[i]++;
					remainder--;
				}
			}
		}
	}

	private RealPlayerState getNextPlayerCanAct() {
		for (int i = 1; i < CONSTANT.PLAYER_COUNT; i++) {
			RealPlayerState player = playerStates[(i + actualSeat)
					% CONSTANT.PLAYER_COUNT];
			if (player.canAct(maxInPot))
				return player;
		}
		return null;
	}

	public DecisionArc getDecision(RealPlayerNode pNode) {
		double fold = simulationGuides.getValue(actualSeat, round, pNode.getBucket(), 2);
		double raise = simulationGuides.getValue(actualSeat, round, pNode.getBucket(), 0);
		double ran = random.nextDouble();

		Action a = Action.FOLD;
		if (ran < fold) {
			if (playerStates[actualSeat].getAmountIn() == maxInPot) {		// if you can check and thereby continue the game
				callAction();
				a = Action.CALL;
			}
			else {
				foldAction();
				a = Action.FOLD;
			}
		} else if (ran < raise + fold) {
			if (bettingRound == 3) {
				callAction();
				a = Action.CALL;
			} else {
				raiseAction();
				a = Action.RAISE;
			}
		} else {
			callAction();
			a = Action.CALL;
		}
		DecisionArc arc = new DecisionArc(pNode, a);
		INode nextNode = getNextNode();
		arc.setChild(nextNode);
		nextNode.initParent(arc);
		pNode.addArc(arc);
		return arc;
	}

	public DecisionArc getDecision(RealPlayerNode pNode, int i) {
		Action a = Action.FOLD;
		if (i == 0) {			// in these first if-statements is determined which path (fold, call, raise) of game-tree is chosen
			foldAction();
			a = Action.FOLD;
		} else if (i == 1) {
			if (bettingRound == 3) {
				callAction();
				a = Action.CALL;
			} else {
				raiseAction();
				a = Action.RAISE;
			}
		} else {
			callAction();
			a = Action.CALL;
		}
		DecisionArc arc = new DecisionArc(pNode, a);
		INode nextNode = getNextNodeStart();
		arc.setChild(nextNode);
		nextNode.initParent(arc);
		pNode.addArc(arc);
		return arc;
	}

	private INode getNextNodeStart() {
		RealPlayerState player;
		// Game ends
		if (getNumActivePlayer() <= 1)
			return actualLeafNode();
		// new Round starts
		if (getNumPlayerToAct() == 0) {
			startNewRoundStart();
		}

		player = getNextPlayerCanAct();
		if (round < 4) {
			actualSeat = player.getSeat();
			return new RealPlayerNode(player, CONSTANT.getNodeId());

		} else
			for (RealPlayerState p : playerStates) {
				if (p.isActive())
					p.setCanRaiseNextRound(false);
			}
			return new RealPlayerNode(player, CONSTANT.getNodeId());
	}

	private void startNewRoundStart() {
		initialize = true; 
		round++;
		bettingRound = 0;
		for (RealPlayerState p : playerStates) {
			if (p.isActive())
				p.setCanRaiseNextRound(true);
		}
		actualSeat = getNextSeatCanAct();
	}

	/**
	 * If someone raises
	 */
	private void raiseAction() {
		bettingRound++;
		maxInPot += getBetSize();
		RealPlayerState player = playerStates[actualSeat];
		player.setAmountIn(maxInPot);
		for (RealPlayerState p : playerStates) {
			if (p.isActive())
				p.setCanRaiseNextRound(true);
		}
		player.setCanRaiseNextRound(false);
	}

	private void callAction() {
		RealPlayerState player = playerStates[actualSeat];
		player.setAmountIn(maxInPot);
		player.setCanRaiseNextRound(false);
	}

	/**
	 * If we fold the game will be set inactive No need to process this path
	 * anymore
	 */
	private void foldAction() {
		if (actualSeat == ownSeat) {
			for (RealPlayerState p : playerStates) {
				p.setActive(false);
			}
		} else {
			playerStates[actualSeat].setActive(false);
		}
	}
}
