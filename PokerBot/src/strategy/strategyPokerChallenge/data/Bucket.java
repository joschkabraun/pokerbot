package strategy.strategyPokerChallenge.data;

import strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card;
import strategy.strategyPokerChallenge.data.CONSTANT;

public class Bucket {
	public static final int BUCKET_COUNT = 5;
	
	private static final int[][] buckets = {
			{ 4, 4, 3, 3, 2, 2, 1, 1, 1, 1, 1, 1, 1 },
			{ 3, 4, 3, 3, 2, 2, 1, 1, 0, 0, 0, 0, 0 },
			{ 3, 3, 4, 3, 2, 2, 1, 0, 0, 0, 0, 0, 0 },
			{ 3, 3, 2, 4, 2, 1, 0, 0, 0, 0, 0, 0, 0 },
			{ 2, 2, 2, 2, 3, 2, 1, 0, 0, 0, 0, 0, 0 },
			{ 2, 2, 1, 1, 2, 3, 2, 1, 0, 0, 0, 0, 0 },
			{ 1, 1, 0, 0, 0, 1, 3, 1, 1, 0, 0, 0, 0 },
			{ 1, 0, 0, 0, 0, 0, 1, 2, 1, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 } };
	

	private static int[] suitCounts;
	private static int[] rankCounts;

	public static int getBucket(Card[] cards) {
		if (cards == null || cards.length != 2 || cards[0] == null || cards[1] == null)
			return -1;
		Card c1 = cards[0];
		Card c2 = cards[1];
		// If rank of c2 greater c1 switch
		if (c2.rank.index > c1.rank.index) {
			Card temp = c1;
			c1 = c2;
			c2 = temp;
		}
		// same suit
		int result = 0;
		if (c1.suit == c2.suit)
			result = buckets[12-c1.rank.index][12-c2.rank.index];
		else
			result = buckets[12-c2.rank.index][12-c1.rank.index];
		
		return result;
	}

	public static int[] getFlopTurnRiver(Card[] board, Card[] cards) {
		int len = board.length;
		for (int i = 0; i < board.length; i++) {
			if (board[i] == null)
				len--;
		}
		Card[] all_cards = new Card[len + cards.length];
		for (int i = 0; i < len; i++) {
			all_cards[i] = board[i];
		}
		for (int i = 0; i < cards.length; i++) {
			all_cards[i + len] = cards[i];
		}
		suitCounts = new int[Card.Suit.values().length];
		rankCounts = new int[Card.Rank.values().length];
		for (int i = 0; i < all_cards.length; i++) {
			suitCounts[all_cards[i].suit.index]++;
			rankCounts[all_cards[i].rank.index]++;
		}

		if (cards.length != 2)
			return new int[] { -1, -1, -1 };
		Card c1 = cards[0];
		Card c2 = cards[1];
		// If rank of c2 greater c1 switch
		if (c2.rank.index > c1.rank.index) {
			Card temp = c1;
			c1 = c2;
			c2 = temp;
		}
		int[] values = new int[] { -1, -1, -1 };
		int length = all_cards.length;
		// flop
		if (length == 5)
			values[0] = 0;
		// turn
		if (length == 6)
			values[1] = 0;
		// river
		if (length == 7)
			values[2] = 0;

		if (testContainsFlush())
			return new int[] { 0, 0, 13 };
		if (testContainsStraight())
			return new int[] { 0, 0, 12 };

		int mostFrequentRank = -1;
		int secondMostFrequentRank = -1;
		for (int i = 0; i < rankCounts.length; i++) {
			if ((mostFrequentRank == -1)
					|| rankCounts[i] >= rankCounts[mostFrequentRank]) {
				secondMostFrequentRank = mostFrequentRank;
				mostFrequentRank = i;
			} else if ((secondMostFrequentRank == -1)
					|| rankCounts[i] >= rankCounts[secondMostFrequentRank]) {
				secondMostFrequentRank = i;
			}
		}
		boolean bh = false;
		boolean sh = false;
		boolean kicker = false;
		if (c1.rank.index >= Card.Rank.KING.index)
			kicker = true;
		if (rankCounts[mostFrequentRank] > 1) {
			if (c1.rank.index == mostFrequentRank) {
				bh = true;
			}
			else if (c2.rank.index == mostFrequentRank) {
				sh = true;
			}
			if (rankCounts[secondMostFrequentRank] > 1) {
				if (c1.rank.index == secondMostFrequentRank) {
					bh = true;
				}
				else if (c2.rank.index == secondMostFrequentRank) {
					sh = true;
				}
			}
		}
		if(!sh && bh && c1.rank.index < Card.Rank.JACK.index) {
			sh = true;
			bh = false;
		}
		if (rankCounts[mostFrequentRank] == 4) {
			values = new int[] { 3, 3, 3 };
		} else if (rankCounts[mostFrequentRank] == 3
				&& rankCounts[secondMostFrequentRank] >= 2) {
			values = new int[] { 3, 3, 2 };
		} else if (rankCounts[mostFrequentRank] == 3) {
			if (bh) {
				values[0] = 3;
				values[1] = 3;
			} else if (sh) {
				values[0] = 2;
				values[1] = 2;
			} else {
				if (kicker) {
					values[0] = 2;
				}
			}
		} else if (rankCounts[mostFrequentRank] == 2) {
			if (rankCounts[secondMostFrequentRank] == 2) {
				if (bh && sh) {
					values[0] = 3;
					values[1] = 2;
				} else {

					if (sh || bh) {
						values[0] = 3;
						if (kicker) {
							values[1] = 2;
						}
					} else {
						if (kicker) {
							values[0] = 2;
						}
					}
				}
			} else {
				if (bh) {
					values[0] = 3;
				} else if (sh) {
					values[0] = 2;
				} else {
					if (kicker) {
						values[0] = 2;
					}
				}
			}
		}
		if (length < 7) {
			if (testContainsFlushDraw() || testContainsStraightDraw()) {
				values[0]++;
			}
		}
		if(CONSTANT.DEBUG_IMMI) {
			System.out.print("Cards: ");
			for(Card c: all_cards) {
				System.out.print(c + " ");
			}
			System.out.println("Values: Flop:" + values[0] + " Turn: " + values[1] + " River: " + values[2]);
		}
		return values;
	}

	private static boolean testContainsStraight() {
		int runningCount = 0;
		for (int i = rankCounts.length - 1; i >= 0; i--) {
			if (rankCounts[i] > 0) {
				runningCount++;
				if (runningCount == 5) {
					return true;
				}
			} else {
				runningCount = 0;
			}
		}
		if (runningCount == 4 && rankCounts[12] > 0) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean testContainsFlush() {
		for (int i = 0; i < suitCounts.length; i++) {
			if (suitCounts[i] >= 5) {
				return true;
			}
		}
		return false;
	}

	private static boolean testContainsFlushDraw() {
		for (int i = 0; i < suitCounts.length; i++) {
			if (suitCounts[i] >= 4) {
				return true;
			}
		}
		return false;
	}

	private static boolean testContainsStraightDraw() {
		int runningCount = 0;
		for (int i = rankCounts.length - 1; i >= 0; i--) {
			if (rankCounts[i] > 0) {
				runningCount++;
				if (runningCount == 4) {
					return true;
				}
			} else {
				runningCount = 0;
			}
		}
		return false;
	}

}
