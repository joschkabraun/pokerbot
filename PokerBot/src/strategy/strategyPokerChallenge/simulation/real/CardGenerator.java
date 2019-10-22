package strategy.strategyPokerChallenge.simulation.real;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card;
import strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Rank;
import strategy.strategyPokerChallenge.ca.ualberta.cs.poker.free.dynamics.Card.Suit;
import strategy.strategyPokerChallenge.data.Bucket;

public class CardGenerator {
	private Random r;
	Card[] cards;

	public CardGenerator() {
		this.r = new Random();
		cards = Card.getAllCards();
	}

	public CardGenerator(CardGenerator cardGenerator) {
		this.r = new Random();
		this.cards = new Card[cardGenerator.cards.length];
		for (int i = 0; i < cardGenerator.cards.length; i++) {
			Card c = cardGenerator.cards[i];
			if (c != null)
				this.cards[i] = new Card(c.rank, c.suit);
			else
				this.cards[i] = null;
		}
	}

	public Card getNextAndRemoveCard() {
		int i = r.nextInt(cards.length);
		Card c = null;
		while (c == null) {
			c = cards[i];
			cards[i] = null;
			i = r.nextInt(cards.length);
		}
		return c;
	}

	public void removeCard(Card c) {
		cards[c.rank.index * 4 + c.suit.index] = null;
	}

	public void addCard(Card c) {
		cards[c.rank.index * 4 + c.suit.index] = c;
	}

	public Card[] getHole() {
		Card[] result = new Card[2];
		result[0] = getNextAndRemoveCard();
		result[1] = getNextAndRemoveCard();
		return result;
	}

	public Card[] getHole(int bucket, int maxBucket) {
		try {
			File file = new File("c://pokerBot/bot_v1_2_0//debug//bucket//bucketing.txt");
			FileWriter fw = new FileWriter(file, true);
			fw.write(String.format("bucket: %d, maxBucket: %d, %tT%n", bucket, maxBucket, new Date()));
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Card[] result = new Card[2];
		result[0] = getNextAndRemoveCard();
		result[1] = getNextAndRemoveCard();
		while (Bucket.getBucket(result) < bucket
				|| Bucket.getBucket(result) > maxBucket) {
			addCard(result[0]);
			addCard(result[1]);
			result[0] = getNextAndRemoveCard();
			result[1] = getNextAndRemoveCard();
		}
		return result;
	}

	public Card[] getHole(int bucket, int maxBucket, Card[] board) {
//		Card[] result = new Card[2];
//		result[0] = getNextAndRemoveCard();
//		result[1] = getNextAndRemoveCard();
//		int i = 0;
//		while ((Bucket.getBucket(result) < bucket
//				|| Bucket.getBucket(result) > maxBucket || (!hitBoard(result,
//				board)))
//				&& i < 150) {
//			addCard(result[0]);
//			addCard(result[1]);
//			result[0] = getNextAndRemoveCard();
//			result[1] = getNextAndRemoveCard();
//			i++;
//		}
//		// System.out.println("Result: " + Arrays.deepToString(result) + "
//		// Board: " + Arrays.deepToString(board) + " i: " + i);
//		return result;

		Card[] result = new Card[2];
		Rank highestRank = Rank.TWO;
		for (Card c : board) {
			if (c != null) {
				if (c.rank.compareTo(highestRank) > 0)
					highestRank = c.rank;
			}
		}
		result[0] = getNextAndRemoveCard(highestRank);
		if (result[0] == null) {
			result[0] = getNextAndRemoveCard();
		}
		result[1] = getNextAndRemoveCard();
		return result;
	}

	public Card[] getHole(Card[] board) {
		Card[] result = new Card[2];
		result[0] = getNextAndRemoveCard();
		result[1] = getNextAndRemoveCard();
		int i = 0;
		while ((!hitBoard(result, board) || i < 100)) {
			addCard(result[0]);
			addCard(result[1]);
			result[0] = getNextAndRemoveCard();
			result[1] = getNextAndRemoveCard();
			i++;
		}
		return result;
	}
	
	

	private boolean hitBoard(Card[] result, Card[] board) {
		Card c1 = result[0];
		Card c2 = result[1];
		int highestRank = 0;
		for (Card c : board) {
			if (c != null) {
				if (c.rank.index > highestRank)
					highestRank = c.rank.index;
			}
		}

		if (c1.rank.index == c2.rank.index && c1.rank.index >= highestRank) {
			return true;
		}
		for (Card c : board) {
			if (c != null) {
				if (c.rank.index == c2.rank.index
						|| c.rank.index == c1.rank.index) {
					// System.out.println("c:" + c + " c1: " + c1 + " c2: " +
					// c2);
					return true;
				}
			}
		}
		return false;
	}

	public Card[] getHoleHigh(int bucket, int maxBucket, Card[] board) {
		Card[] result = new Card[2];
		
		result[0] = killerCard(board);
		if (result[0] == null) {
			return getHole(bucket, maxBucket, board);
		}
		result[1] = getNextAndRemoveCard();
		return result;
	}

	public Card killerCard(Card[] board) {
		int[] suitCounts = new int[Card.Suit.values().length];
		int[] rankCounts = new int[Card.Rank.values().length];
		int mostFrequentRank = -1;
		int secondMostFrequentRank = -1;
		int highestRank = -1;
		for (Card c : board) {
			if (c != null) {
				suitCounts[c.suit.index]++;
				rankCounts[c.rank.index]++;
			}
		}
		for (int i = 0; i < suitCounts.length; i++) {
			if (suitCounts[i] >= 3) {
				return getNextAndRemoveCard(Suit.toSuit(i));
			}
		}
		int runningCount = 0;
		int hole = 0;
		int holeRank = -1;
		int runningCountHole = 0;
		for (int i = rankCounts.length - 1; i >= 0; i--) {
			if (rankCounts[i] > 0) {
				runningCount++;
				if (runningCount == 4) {
					if(i+4 >= rankCounts.length){
						if(i-1 >=0)
							return getNextAndRemoveCard(Rank.toRank(i-1));
						else return getNextAndRemoveCard(Rank.ACE);
					}
					else return getNextAndRemoveCard(Rank.toRank(i + 4));
					
				}
				if (runningCount + runningCountHole == 4) {
					if(holeRank!=-1)
						return getNextAndRemoveCard(Rank.toRank(holeRank));
					else break;
				}
			} else {
				if (hole == 0) {
					hole++;
					runningCountHole = runningCount;
					holeRank = i;
				} else {
					hole = 0;
					runningCountHole = 0;
					holeRank = -1;
				}
				runningCount = 0;
			}
		}
		if (runningCount == 3 && rankCounts[12] > 0) {
			return getNextAndRemoveCard(Rank.toRank(3));
		}
		for (int i = 0; i < rankCounts.length; i++) {
			if (rankCounts[i] > 0)
				highestRank = i;
			if ((mostFrequentRank == -1)
					|| rankCounts[i] >= rankCounts[mostFrequentRank]) {
				secondMostFrequentRank = mostFrequentRank;
				mostFrequentRank = i;
			} else if ((secondMostFrequentRank == -1)
					|| rankCounts[i] >= rankCounts[secondMostFrequentRank]) {
				secondMostFrequentRank = i;
			}
		}
		if (rankCounts[mostFrequentRank] >= 2)
			return getNextAndRemoveCard(Rank.toRank(mostFrequentRank));
		if (rankCounts[mostFrequentRank] == 1)
			return getNextAndRemoveCard(Rank.toRank(highestRank));
		return null;
	}

	private Card getNextAndRemoveCard(Suit suit) {
		Card c = null;
		int random = 0;
		do {
			random = r.nextInt(Card.Rank.values().length);
			c = cards[random * 4 + suit.index];
			cards[random * 4 + suit.index] = null;
		} while (c == null);
		return c;
	}
	
	private Card getNextAndRemoveCard(Rank rank) {
		Card c = null;
		int random = 0;
		int count = 0;
		do {
			random = r.nextInt(Card.Suit.values().length);
			c = cards[rank.index * 4 + random];
			cards[rank.index * 4 + random] = null;
			count++;
		} while (c == null && count < 20);
		return c;
	}
}
