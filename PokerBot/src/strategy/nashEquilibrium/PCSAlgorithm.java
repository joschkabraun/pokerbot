package strategy.nashEquilibrium;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class PCSAlgorithm {
	
	public static final byte buckets = 5;
	
	private static InformationSet[] I;				// all information sets	new InformationSet[PCSAlgorithm.buckts][];
	private static int Il;							// the length of PCSAlgorithm.I
	private static Strategy S;
	
	private static float[][] r;
	private static float[][] s;
	private static float[][] m;
	
	private static int abc = 0;
	
	private static float[] WalkTree(History h, boolean player, float[] pi, float[] pOp) {
		if (h.isTerminal()) {
			int payoff = h.getPayoff();
			
			float[] ev = new float[Il];
			History h2 = h.getHistoryWithoutLastAction();
			InformationSet[] infoSets = h2.lookupInfosets();
			int[] indizesIS = new int[infoSets.length];
			for (int i = 0; i < indizesIS.length; i++)
				indizesIS[i] = getIndex(infoSets[i]);
			
			if (h.getLastAction().equals(new Action(-1)))
				if (h.getPlayerToAct() == player)
					for (int i : indizesIS)
						ev[i] = payoff;
				else
					for (int i : indizesIS)
						ev[i] = -payoff;
			else {
				Arrays.sort(infoSets, new InformationSetComparatorByStrength());			// ordering the information sets by them strength
				if (h2.getPlayerToAct())													// bug of 14.06.2014
					ev = terminalNodeEvaluation((float) payoff, infoSets, infoSets, pi);
				else
					ev = terminalNodeEvaluation((float) payoff, infoSets, infoSets, pOp);
//				terminalNodeEvaluation((float) payoff, infoSets, infoSets, pOp);			// solution of 14.06.2014. Look at C-code!
			}
			
			return ev;
		}
		
		InformationSet[] infoSets = h.lookupInfosets();
		int[] indizesIS = new int[infoSets.length];
		for (int i = 0; i < indizesIS.length; i++)
			indizesIS[i] = getIndex(infoSets[i]);
		float[] u = new float[Il];
		for (int i : indizesIS)
			S.regretMatching(i, r[i][0],  r[i][1],  r[i][2]);
		boolean actPlayToAct = h.getPlayerToAct();
		
		float[] P1 = pi.clone();						// P1 = p1'
		float[] P2 = pOp.clone();						// P2 = p2'
		for (Action a : h.A())
			if (actPlayToAct == player) {
				for (int i : indizesIS)
					P1[i] = S.get(i, a.toInt()+1) * pi[i];
				float[] U = WalkTree(new History(h, a), player, P1, pOp);// U = u'// this is maybe wrong because of the place of the arguments P1 and p2
				for (int i : indizesIS)
					m[i][a.toInt()+1] = U[i];
				for (int i : indizesIS)
					u[i] = u[i] + S.get(i, a.toInt()+1) * U[i];
			} else {
				for (int i : indizesIS)
					P2[i] = S.get(i, a.toInt()+1) * pOp[i];
				float[] U = WalkTree(new History(h, a), player, pi, P2);// U = u'// this is maybe wrong because of the place of the arguments p1 and P2
				for (int i : indizesIS)
					u[i] = u[i] + U[i];
			}
		
		if (actPlayToAct == player)
			for (int i : indizesIS)
				for (Action a : h.A()) {
					int j = a.toInt()+1;
					r[i][j] = r[i][j] + m[i][j] - u[i];
					if (actPlayToAct == false)
						s[i][j] = s[i][j] + pi[i] * S.get(i, j);
					else
						s[i][j] = s[i][j] + pOp[i] * S.get(i, j);
				}
		return u;
	}
	
	private static void Solve(long t) {
		boolean[] N = new boolean[]{false, true};
		float[] P = new float[Il];
		for (@SuppressWarnings("unused") float p : P)
			p = 1.0f;
		for (long j = 0; j < t; j++)
			for (boolean i : N)
				WalkTree(new History(), i, P, P);
	}
	
	public static InformationSet getInformationSet(int indexIS) {
		return PCSAlgorithm.I[indexIS];
	}
	
	public static void main(String[] args) {
//		System.out.print("Create the informations sets new? ");
//		String str = new java.util.Scanner(System.in).next();
//		if (str.equals("y"))
//			createAndSaveInformationSets();
//		else if (str.equals("n"))
			loadInformationSets();
//		else
//			throw new IllegalArgumentException();
//		
//		S = new Strategy(I.length);							// initializes the strategy
//		
//		r = new float[Il][3];
//		s = new float[Il][3];
//		m = new float[Il][3];
//		for (@SuppressWarnings("unused") float[] i : r)
//			i = new float[]{0.0f, 0.0f, 0.0f};
//		for (@SuppressWarnings("unused") float[] i : s)
//			i = new float[]{0.0f, 0.0f, 0.0f};
//		for (@SuppressWarnings("unused") float[] i : m)
//			i = new float[]{0.0f, 0.0f, 0.0f};
//		
////		System.out.print("Please enter the number of iterations: ");
////		long t = new java.util.Scanner(System.in).nextLong();
////		Solve(t);
//		Solve(1);
//		
//		saveStrategy(getAverageStrategy());
		
		long l = 46769307646296260l;
		for (InformationSet is : I)
			if (InformationSet.convert(is.getBitSet()) == l ) {
				System.out.println("heloooo");
				System.out.println(is.getLastBucket());
			}
	}
	
	private static void createAndSaveInformationSets() {
		I = getAllInformationSetsTo(getAllHistories());				// initializes the information sets
		Arrays.sort(I, new InformationSetComparatorBitSet());
		Il = I.length;
		System.out.printf("The number of information sets is %d%n", Il);
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(String.format("c://pokerBot//bot_v1_3_x//datastructure//informationSets-%s-buckets.ser", PCSAlgorithm.buckets));
			ObjectOutputStream o = new ObjectOutputStream(fos);
			o.writeObject(I);
			o.flush();
			o.close();
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void loadInformationSets() {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(String.format("c://pokerBot//bot_v1_3_x//datastructure//informationSets-%s-buckets.ser", PCSAlgorithm.buckets));
			ObjectInputStream o = new ObjectInputStream(fis);
			I = (InformationSet[]) o.readObject();
			Il = I.length;
			o.close();
		} catch (IOException | ClassNotFoundException e) {
			System.err.println(e);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void saveStrategy(Strategy S) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(String.format("c://pokerBot//bot_v1_3_x//datastructure//strategy-%s-buckets.ser", PCSAlgorithm.buckets));
			ObjectOutputStream o = new ObjectOutputStream(fos);
			o.writeObject(S);
			o.flush();
			o.close();
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static Strategy getAverageStrategy() {
		Strategy avg = new Strategy(Il);
		for (int i = 0; i < Il; i++)
			avg.regretMatching(i, s[i][0], s[i][1], s[i][2]);
		return avg;
	}
	
	private static History[] getAllHistories() {
		ArrayList<History> allH = walk(new History());
		History[] ret = new History[allH.size()];
		for (int i = 0; i < ret.length; i++)
			ret[i] = (History) allH.get(i).clone();
		allH = null;
		return ret;
	}
	
	private static ArrayList<History> walk(History h) {
		ArrayList<History> alh = new ArrayList<History>();
		alh.add(h);
		if (h.isTerminal())
			return alh;
		Action[] ah = h.A();
		ArrayList<History> w = new ArrayList<History>();
		History n;
		for (Action a : ah) {
			n = new History(h, a);
			w = walk(n);
			for (History h2 : w)
				alh.add(h2);
		}
		return alh;
	}
	
	private static InformationSet[] getAllInformationSetsTo(History[] hs) {
		ArrayList<InformationSet> allI = new ArrayList<InformationSet>();
		for (History h : hs) {
			if (h.isTerminal())
				continue;
			for (InformationSet I : h.lookupInfosets())
				allI.add(I);
		}
		InformationSet[] I = new InformationSet[allI.size()];
		for (int i = 0; i < I.length; i++)
			I[i] = (InformationSet) allI.get(i).clone();
		allI = null;
		return I;
	}
	
	private static int getIndex(InformationSet IS) {
		return Arrays.binarySearch(I, IS, new InformationSetComparatorBitSet());
	}
	
	/**
	 * @deprecated because it is not going to be used
	 */
	@SuppressWarnings("unused")
	private static InformationSet[] getAllInformationSetsOfPlayer(boolean player) {
		ArrayList<InformationSet> alp = new ArrayList<InformationSet>();
		for (InformationSet I : PCSAlgorithm.I)
			if (I.getPlayer() == player)
				alp.add(I);
		InformationSet[] I = new InformationSet[alp.size()];
		for (int i = 0; i < I.length; i++)
			I[i] = (InformationSet) alp.get(i).clone();
		alp = null;
		return I;
	}
	
	private static float[] terminalNodeEvaluation(float value, InformationSet[] pIS, InformationSet[] opIS, float[] probOpIS) {
		float[] ev = new float[Il];
		
		float winsum = 0.0f;
		int j = 0;
		for (int i = 0; i < pIS.length; i++) {
			byte b = pIS[i].getLastBucket();
			while (opIS[j].getLastBucket() < b) {
				winsum += probOpIS[getIndex(opIS[j])];
				j++;
			}
			ev[getIndex(pIS[i])] = winsum * value;
		}
		
		float losesum = 0.0f;
		j = opIS.length - 1;
		for (int i = pIS.length-1; i > -1; i--) {
			byte b = pIS[i].getLastBucket();
			while (opIS[j].getLastBucket() > b) {
				losesum += probOpIS[getIndex(opIS[j])];
				j--;
			}
			ev[getIndex(pIS[i])] -= losesum * value;
		}
		
		return ev;
	}
	
}
