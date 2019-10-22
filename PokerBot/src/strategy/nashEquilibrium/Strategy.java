package strategy.nashEquilibrium;

import java.io.Serializable;

public class Strategy implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3976609840967641997L;
	private float[][] actionprobs;
	
	public Strategy(int numIS) {
		this.actionprobs = new float[numIS][3];
	}
	
	/**Regret Matching according some popular thesis of http://poker.cs.ualberta.ca/
	 * in the period from 2007-2011.
	 * 
	 * @param indexIS the index of the information set
	 * @param r_I_f for fold
	 * @param r_I_c for check/call
	 * @param r_I_r for bet/raise
	 */
	public void regretMatching(int indexIS, float r_I_f, float r_I_c, float r_I_r) {
		Action[] as = PCSAlgorithm.getInformationSet(indexIS).getHistory().A();
		float r_p = Math.max(0, r_I_f + r_I_c + r_I_r);
		float r_I_f_p = Math.max(0, r_I_f), r_I_c_p = Math.max(0, r_I_c), r_I_r_p = Math.max(0, r_I_r);
		float[] r_I_p = new float[]{r_I_f_p, r_I_c_p, r_I_r_p};
		if (r_p > 0)
			if (as.length == 3) {
				actionprobs[indexIS][0] = r_I_f_p / r_p;
				actionprobs[indexIS][1] = r_I_c_p / r_p;
				actionprobs[indexIS][2] = r_I_r_p / r_p;
			} else {							// the array has two elements at least
				for (int i = 0; i < 3; i++)
					this.actionprobs[indexIS][i] = 0.0f;
				this.actionprobs[indexIS][as[0].toInt()+1] = r_I_p[as[0].toInt()+1] / r_p;
				this.actionprobs[indexIS][as[1].toInt()+1] = r_I_p[as[1].toInt()+1] / r_p;
			}
		else
			if (as.length == 3)
				for (int i = 0; i < 3; i++)
					this.actionprobs[indexIS][i] = 0.3333333333333333333333333333333333333333f;
			else {							// the array has two elements at least
				for (int i = 0; i < 3; i++)
					this.actionprobs[indexIS][i] = 0.0f;
				this.actionprobs[indexIS][as[0].toInt()+1] = 0.666666666666666666666666666666f;
				this.actionprobs[indexIS][as[1].toInt()+1] = 0.666666666666666666666666666666f;
			}
	}
	
	public float get(int indexInformationSet, int indexAction) {
		return this.actionprobs[indexInformationSet][indexAction];
	}
	
}
