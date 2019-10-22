package strategy.nashEquilibrium;

import gameBasics.GameState;

import java.util.ArrayList;
import java.util.BitSet;

public class BucketSequence {
	
	private BitSet bitset;
	
	public BucketSequence(int[] I) {
		if (I.length > 4 || I.length == 0)
			throw new IllegalArgumentException();
		this.bitset = new BitSet();
		int l = ((int) (Math.log10(PCSAlgorithm.buckets)/Math.log10(2))) + 1;			// number of bits per bucket
		int k;
		for (int i = 0; i < I.length; i++) {
			k = I[i];
			for (int j = i*l; j < (i+1)*l; j++) {
				this.bitset.set(j, (k % 2)==1);
				k /= 2;
			}
		}
	}
	
	protected BitSet getBitSet() {
		return this.bitset;
	}
	
//	private static BucketSequence[] getBucketSequencesWithLastBucket(byte bucket, GameState gs) {
//		ArrayList<BucketSequence> alis = new ArrayList<BucketSequence>();
//		ArrayList<int[]> ali = new ArrayList<int[]>();
//		switch (gs) {
//		case PRE_FLOP:
//			ali.add(new int[]{bucket});
//			break;
//		case FLOP:
//			for (int a = 1; a < PCSAlgorithm.buckets+1; a++)
//				ali.add(new int[]{a, bucket});
//			break;
//		case TURN:
//			for (int a = 1; a < PCSAlgorithm.buckets+1; a++)
//				for (int b = 1; b < PCSAlgorithm.buckets+1; b++)
//					ali.add(new int[]{a, b, bucket});
//			break;
//		case RIVER:
//			for (int a = 1; a < PCSAlgorithm.buckets+1; a++)
//				for (int b = 1; b < PCSAlgorithm.buckets+1; b++)
//					for (int c = 1; c < PCSAlgorithm.buckets+1; c++)
//						ali.add(new int[]{a, b, c, bucket});
//			break;
//		case SHOW_DOWN:
//			throw new IllegalStateException("This method should not be avoked.");
//		}
//		for (int[] I : ali)
//			alis.add(new BucketSequence(I));
//		BucketSequence[] b = new BucketSequence[alis.size()];
//		for (int j = 0; j < b.length; j++)
//			b[j] = alis.get(j);
//		return b;
//	}
	
}
