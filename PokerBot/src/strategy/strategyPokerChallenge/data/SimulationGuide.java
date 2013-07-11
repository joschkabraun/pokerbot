package strategy.strategyPokerChallenge.data;

import strategy.strategyPokerChallenge.ringclient.ClientRingDynamics;

public class SimulationGuide {
	
	private double[][][][] values;
	
	@SuppressWarnings("unused")
	static private double[][][][] estimates;
	
	private static final double[][][] own_guides = new double[][][] { {{ 0.0, 1.0, 0.0 },
		{ 0.5, 0.5, 0.0 },
		{ 0.5, 0.5, 0.0 },
		{ 0.5, 0.5, 0.0 },
		{ 0.5, 0.5, 0.0 },
		{ 0.5, 0.5, 0.0 }},
			{{ 0.0, 1.0, 0.0 },
			{ 0.5, 0.5, 0.0 },
			{ 0.5, 0.5, 0.0 },
			{ 0.5, 0.5, 0.0 },
			{ 0.5, 0.5, 0.0 },
			{ 0.5, 0.5, 0.0 }},
				{{ 0.0, 1.0, 0.0 },
				{ 0.5, 0.5, 0.0 },
				{ 0.5, 0.5, 0.0 },
				{ 0.5, 0.5, 0.0 },
				{ 0.5, 0.5, 0.0 },
				{ 0.5, 0.5, 0.0 }},
					{{ 0.0, 1.0, 0.0 },
					{ 0.5, 0.5, 0.0 },
					{ 0.5, 0.5, 0.0 },
					{ 0.5, 0.5, 0.0 },
					{ 0.5, 0.5, 0.0 },
					{ 0.5, 0.5, 0.0 }},
						{{ 0.0, 1.0, 0.0 },
						{ 0.5, 0.5, 0.0 },
						{ 0.5, 0.5, 0.0 },
						{ 0.5, 0.5, 0.0 },
						{ 0.5, 0.5, 0.0 },
						{ 0.5, 0.5, 0.0 }},
		};
	
	private static final double[][][] guides = new double[][][] { {{ 0.0, 1.0, 0.0 },
		{ 0.15, 0.15, 0.7 },
		{ 0.5, 0.5, 0.0 },
		{ 0.5, 0.5, 0.0 },
		{ 0.5, 0.5, 0.0 },
		{ 0.5, 0.5, 0.0 }},
			{{ 0.0, 1.0, 0.0 },
			{ 0.2, 0.8, 0.0 },
			{ 0.5, 0.5, 0.0 },
			{ 0.5, 0.5, 0.0 },
			{ 0.5, 0.5, 0.0 },
			{ 0.5, 0.5, 0.0 }},
				{{ 0.0, 1.0, 0.0 },
				{ 0.65, 0.35, 0.0 },
				{ 0.5, 0.5, 0.0 },
				{ 0.5, 0.5, 0.0 },
				{ 0.5, 0.5, 0.0 },
				{ 0.5, 0.5, 0.0 }},
					{{ 0.0, 1.0, 0.0 },
					{ 0.6, 0.4, 0.0 },
					{ 0.5, 0.5, 0.0 },
					{ 0.5, 0.5, 0.0 },
					{ 0.5, 0.5, 0.0 },
					{ 0.5, 0.5, 0.0 }},
						{{ 0.0, 1.0, 0.0 },
						{ 0.8, 0.2, 0.0 },
						{ 0.5, 0.5, 0.0 },
						{ 0.5, 0.5, 0.0 },
						{ 0.5, 0.5, 0.0 },
						{ 0.5, 0.5, 0.0 }},
		};
	
	static public synchronized void setEstimates() {
		SimulationGuide.estimates = strategy.strategyPokerChallenge.history.History.getHistory().getCurrent().getPlayerEstimate();
	}

	public SimulationGuide(ClientRingDynamics crd) {
		values = new double[CONSTANT.PLAYER_COUNT][6][Bucket.BUCKET_COUNT][3];
		fillGuides(crd);
	}

	private synchronized void fillGuides(ClientRingDynamics crd) {
		for (int i = 0; i < CONSTANT.PLAYER_COUNT; i++) {
			if (crd.seatTaken == i) {
				values[i] = own_guides;
			} else {
				values[i] = guides;
			}
		}
	}
	
	public double getValue(int seat, int round, int bucket, int decision) {
		if (bucket == -1)
			bucket = 0;
		return values[seat][bucket][round + 1][decision];
	}
}
