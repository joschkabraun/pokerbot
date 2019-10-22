package strategy.strategyPokerChallenge.simulation.real;

import strategy.strategyPokerChallenge.history.GlobalRoundData;
import strategy.strategyPokerChallenge.history.History;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import strategy.strategyPokerChallenge.ringclient.ClientRingDynamics;
import strategy.strategyPokerChallenge.simulation.datastructure.DecisionArc;
import strategy.strategyPokerChallenge.simulation.datastructure.LeafNode;
import strategy.strategyPokerChallenge.simulation.interfaces.IArc;
import strategy.strategyPokerChallenge.simulation.interfaces.INode;
import strategy.strategyPokerChallenge.simulation.interfaces.ISimulator;
import strategy.strategyPokerChallenge.simulation.real.datastructure.RealPlayerNode;
import strategy.strategyPokerChallenge.data.Action;
import strategy.strategyPokerChallenge.data.CONSTANT;
import strategy.strategyPokerChallenge.data.GameState;

public class RealSimulator implements ISimulator {
	private INode root;
	private int ownSeat;
	private ClientRingDynamics crd;
	private Vector<RealSimulationWorker> workerThreads = new Vector<RealSimulationWorker>();
	private boolean started;

	public synchronized Action getDecision() {
		if (started) {

			// Stop simulationThreads
			synchronized (workerThreads) {
				while (workerThreads.size() > 0) {
					RealSimulationWorker simW = workerThreads.remove(0);
					simW.stopWork();
				}
			}
			
			if(History.getHistory().isActive()) updateAggressivity();
			double aggro = getAggressivePreFlop();
			double aggro_raise = getAggressiveRaise();
			// getBestChild
			Vector<IArc> childArcs = root.getChildArcs();		// all monte-carlo-searches make that the paths are children of the start-root
			root = null;
			Action actualDecision = Action.FOLD;
			
			double actualValue = Double.NEGATIVE_INFINITY;
			ConcurrentHashMap<Action, Double> values = new ConcurrentHashMap<Action, Double>();
			for (IArc child : childArcs) {
				Action decision = child.getDecision();
				double tempValue = child.getValue();
				
				values.put(decision, new Double(tempValue));
				if (tempValue > actualValue) {
					actualValue = tempValue;
					actualDecision = decision;
				}
			}
			started = false;
			/**
			 *  Knowledge Base:
			 *  
			 *  If our decision is fold check:
			 *  - if amount to call is equal 0 -> Call
			 *  	- if 2 player situation and opponent checked -> Raise
			 *  - if value of call is greater Aggressive preflop -> Call
			 *  	- if value of raise is greater Aggressive preflop / 2.0 -> Raise
			 *  - if amount to call is only 15 % of what we have inPot already -> Call
			 *  
			 *  If our decision is call check: 
			 * 	- if the value is greater Aggressive Raise (not in preflop) -> Raise
			 * 	- if two player situation and opponent checked -> Raise
			 */
			if (actualDecision.equals(Action.FOLD)) {
				if (crd.roundIndex == 0) {
					
					if (values.get(Action.CALL) > aggro) {
						return Action.CALL;
					}
					Double d = values.get(Action.RAISE);
					if (d!=null && d > (aggro/2.0)) {
						return Action.RAISE;
					}
				}
				if (crd.getAmountToCall(crd.seatTaken) == 0) {
					if (crd.getNumActivePlayers() == 2
							&& crd.getNumPlayersLeftToAct() == 1 && isTight())
						return Action.RAISE;
					return Action.CALL;
				}
				if (((double)crd.getAmountToCall(crd.seatTaken) / (double) crd.inPot[crd.seatTaken]) < 0.15) {
					return Action.CALL;
				}
			}
			if (actualDecision.equals(Action.CALL)) {
				if(values.get(Action.CALL) > aggro_raise && crd.roundIndex > 0)
					return Action.RAISE;
				
				if (crd.getAmountToCall(crd.seatTaken) == 0) {
					if (crd.getNumActivePlayers() == 2
							&& crd.getNumPlayersLeftToAct() == 1)
						return Action.RAISE;
				}
			}
			return actualDecision;
		}
		return Action.FOLD;
	}

	private boolean isTight() {
		if(History.getHistory().isActive()) {
		int seat = -1;
		for(int i = 0; i < crd.active.length; i++) {
			if(crd.active[i] && i!=ownSeat) {
				seat = i;
				break;
			}
		}
		GlobalRoundData grd = History.getHistory().getGlobal();
		double preFlop_fold = grd.getPlayerRatio(crd.seatToPlayer(seat), GameState.PRE_FLOP, Action.FOLD);
		double flop_fold = grd.getPlayerRatio(crd.seatToPlayer(seat), GameState.FLOP, Action.FOLD);
		if(preFlop_fold >= 0.85 || flop_fold >= 0.4) return true;
		return false;
		}
		else {
			return false;
		}
		
	}

	private void updateAggressivity() {
		GlobalRoundData grd = History.getHistory().getGlobal();
		for(int i = 0; i < CONSTANT.PLAYER_COUNT; i++) {
			double d = grd.getAmountWonFromPlayer(i);
			double aggroPreFlop = (CONSTANT.A_PREFLOP) * Math.pow(1.2,d/10.0);
			CONSTANT.AGGRESSIVE_PREFLOP[i] = Math.max(aggroPreFlop, CONSTANT.A_PREFLOP*3.0);
			double aggroRaise = CONSTANT.A_RAISE * Math.pow(0.95, d/10.0);
			CONSTANT.AGGRESSIVE_RAISE[i] = Math.min(aggroRaise, CONSTANT.A_RAISE);
		}
		
	}

	private Double getAggressiveRaise() {
		double result = 0;
		int sum = 0;
		for(int i = 0; i < CONSTANT.AGGRESSIVE_RAISE.length; i++) {
			if(crd.playerToSeat(i)!=ownSeat && crd.active[crd.playerToSeat(i)]) {
				result += CONSTANT.AGGRESSIVE_RAISE[i];
				sum++;
			}
		}
		return result/sum;
	}

	private double getAggressivePreFlop() {
		double result = 0;
		int sum = 0;
		for(int i = 0; i < CONSTANT.AGGRESSIVE_PREFLOP.length; i++) {
			if(crd.playerToSeat(i)!=ownSeat && crd.active[crd.playerToSeat(i)]) {
				result += CONSTANT.AGGRESSIVE_PREFLOP[i];
				sum++;
			}
		}
		return result/sum;
	}

	public synchronized void killTree() {
		Runtime.getRuntime().gc();
	}

	/**
	 * This method starts the monte-carlo simulation. It starts the threads of the tree.
	 */
	public synchronized void startSimulation(ClientRingDynamics crd) {
		synchronized (workerThreads) {
			while (workerThreads.size() > 0) {
				RealSimulationWorker simW = workerThreads.remove(0);
				simW.stopWork();
			}

			CONSTANT.NODE_ID = 0;
			this.ownSeat = crd.seatTaken;
			this.crd = crd;
			this.root = getRootNode();
			this.started = true;
			// Expand one step
			// make fold decision
			RealGame fold = new RealGame(crd, root);
			DecisionArc foldArc = fold.getDecision((RealPlayerNode) root, 0);
			// make no simulation, value doesn't change here
			foldArc.addValue(((LeafNode) foldArc.getChild()).getValue());
			// make raise decision
			RealGame raise = new RealGame(crd, root);
			DecisionArc raiseArc = raise.getDecision((RealPlayerNode) root, 1);
			RealSimulationWorker raiseW = new RealSimulationWorker(root, raise, raiseArc.getChild());
			workerThreads.add(raiseW);
			if (raiseArc.getDecision() == Action.RAISE) {
				// make call decision
				RealGame call = new RealGame(crd, root);
				DecisionArc callArc = call.getDecision((RealPlayerNode) root, 2);
				RealSimulationWorker callW = new RealSimulationWorker(root, call, callArc.getChild());
				workerThreads.add(callW);
			}
		}
	}

	/**
	 * @return the actual PlayerNode.
	 */
	private INode getRootNode() {
		return new RealPlayerNode(CONSTANT.NODE_ID, crd.hole[ownSeat],
				"Own Bot");
	}

}
