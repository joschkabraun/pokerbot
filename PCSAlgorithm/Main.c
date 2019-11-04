/*
 * Main.c
 *
 * In this source file are all previous created source files merged into one source file.
 *
 *  Created on: 21.02.2014
 *      Author: Joschka
 */

#include <math.h>
#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include <string.h>
#include <stdint.h>
#include <unistd.h>
#include <fcntl.h>
#ifdef __unix__
	#include <sys/types.h>
	#include <sys/stat.h>
#elif __MSDOS__ || __WIN32__ || _MSC_VER
	#include <io.h>
	#include <sys\stat.h>
#endif

// global variables of PCSAlgorithm.c

#define buckets 5

//#if ___WIN32__												// on a windows operating system
//	typedef long long int64;
//#   define int64_fmt "%lld"	
//#else														// on a linux operating system
	typedef long long int64;					// it is 64bit on windows and on ubuntu 14.04 vm ware
#   define int64_fmt "%lld"
//#endif

int64 *I;
int Il;
//int64 *Histories;					// debug

typedef float vector_t[3];

vector_t *strategy;

vector_t *r;
vector_t *s;
vector_t *m;

/*
 * PCSAlgorithm.c
 *
 *  Created on: 20.02.2014
 *      Author: Joschka
 */

/*
 * This function returns the index of the information set information_set in the array of all information sets I;
 * implemented as a binary search
 */
int get_index(int64 information_set) {
	int first = 0, last = Il-1, middle = (first+last)/2;
	while (first <= last) {
		if (I[middle] < information_set)
			first = middle+1;
		else if (I[middle] == information_set)
			return middle;
		else
			last = middle-1;
		middle = (first+last)/2;
	}
	printf("Not found! "int64_fmt" is not present in the list (function: get_index).\n", information_set);
	return -1;
}

/*int get_index_his(int64 history) {				// debug
	for (int i = 0; i < 23691; i++)
		if (Histories[i]==history)
			return i;
	printf("Not found! "int64_fmt" is not present in the list (function: get_index_his).\n", history);
	return -1;
}

int indexof(int64 x, int64 *array, int length) {		// debug
	for (int i=0; i<length; i++)
		if (x==array[i])
			return i;
	return -1;
}*/







typedef struct array_long_long {
	int64 *pointer;
	int size;
} array_long_long;

typedef struct array_actions {
	enum actions *pointer;
	int size;
} array_actions;

/*
 * bitoperation.c
 *
 *  Created on: 07.02.2014
 *      Author: Joschka
 */

/*
 * val: the value which should be tested
 * bit: the number of bit which should be tested whether it is set or not
 * return: 1 = bit is set; 0 = bit is not set
 */

int test_bit(int64 val, short bit) {
	if ((val & (0x01LL << bit)) == 0)
		return 0;
	else
		return 1;
}

/*
 * val: the value whose bit should be set
 * bit: the number of bit which should be set
 */
#define SET_BIT(val, bit) (val) = (val) | (0x01LL << (bit))

/*
 * val: the value whose bit should be cleared
 * bit: the number of the bit which should be cleared
 */
#define CLEAR_BIT(val, bit) (val) = (val) & (~(0x01LL << (bit)))

/*
 * return: the bit after the highest bit which is set
 */
char next_bit_to_set(int64 val) {
	char bit = 63;
	while (bit > -1)
		if (test_bit(val, bit))
			return ++bit;
		else
			--bit;
	return 0;
}







/*
 * bucketsequence.c
 *
 *  Created on: 20.02.2014
 *      Author: Joschka
 */

int64 createBucketSequence(int *I, int size) {
	if (size > 4 || size == 0)
		assert(0);								// illegal argument
	int64 bs = 0;
	int l = (log10(buckets) / log10(2)) + 1;					// number of bits per bucket
	for (int i=0; i < size; i++) {
		int k = I[i];
		for (int j = i*l; j < (i+1)*l; j++) {
			if ((k % 2) == 1)
				SET_BIT(bs, j);
			else
				CLEAR_BIT(bs, j);
			k /= 2;
		}
	}
	return bs;
}







/*
 * InformationSet.c
 *
 *  Created on: 20.02.2014
 *      Author: Joschka
 */
 
 
int64 createInformationSet(int64 history, int64 bucketsequence) {
	return (history | (bucketsequence << 48));
}

int get_last_bucket(int64 informationset) {
	if (informationset==-6076574518398440533ll)
		printf("index1: %d, value: "int64_fmt"\n", get_index(informationset), informationset);
	int l = (log10(buckets)/log10(2)) + 1;											// number of bits per bucket
	int b = 0;
	for (int i = 48; i < next_bit_to_set(informationset); i+=l) {
		int64 h = 0;
		for (int j = i; j < i+l; j++)
			SET_BIT(h, j);
		int64 c = informationset & h;
		c = c >> i;								// sets the first bit, which is may set, on the first position
		if (c == 0)
			break;
		else if (c < 0)
			assert(0);				// illegal state
		else if (c <= buckets)
			b = c;
		else
			assert(0);				// illegal state
	}
	if (b == 0)
		printf("index2: %d, value: "int64_fmt"\n", get_index(informationset), informationset);
	assert(b != 0);
	return b;
}

int64 getHistory(int64 informationset) {
	int64 h = 0;
	for (int i = 0; i < 48; i++)
		SET_BIT(h, i);
	return (informationset & h);
}






/*
 * history.c
 *
 * This is a re-implementation of the class History.java in the PokerBot
 *
 *  Created on: 18.02.2014
 *      Author: Joschka
 */

enum actions
	{
		FOLD = 0,
		CHECK_CALL,
		BET_RAISE
	};
enum game_states
	{
		PRE_FLOP = 0,
		FLOP,
		TURN,
		RIVER,
		SHOW_DOWN
	};

char getNextBitToSet(int64 history) {
	char bit = next_bit_to_set(history);
	if (bit % 2 == 1)
		return ++bit;
	return bit;
}


/**
 * adds an action to the history (= int64)
 */
void add(int64 *history, enum actions action) {
	char bit = getNextBitToSet(*history);
	if (action == FOLD)
		SET_BIT(*history, bit);
	else if (action == CHECK_CALL)
		SET_BIT(*history, bit+1);
	else if (action == BET_RAISE) {
		SET_BIT(*history, bit);
		SET_BIT(*history, bit+1);
	}
}


/* This method returns the player whose turn it is.
 *
 * return: 0 for player 1; 1 for player 2
 */
int get_player_to_act(int64 history) {
	char bit = getNextBitToSet(history);
	bit /= 2;
	if (bit % 2 == 0)
		return 0;
	return 1;
}

/*
 * return: 0: is not-terminal; 1: is terminal
 */
int isTerminal(int64 history) {
	char bit = getNextBitToSet(history);
	if (bit == 0) {
		if (test_bit(history, 0)==1 && test_bit(history, 1)==0)				// fold
			return 1;
		else
			return 0;
	}
	if (test_bit(history, bit-2) == 1 && test_bit(history, bit-1) == 0)		// fold
		return 1;
	if (bit < 15)
		return 0;

	int counter = 0;
	int64 h2 = history;
	while (h2 > 7) {
		if (test_bit(h2, 2) == 0 && test_bit(h2, 3)) {		// call
			++counter;
			h2 = h2 >> 4;
			continue;
		}
		h2 = h2 >> 2;
	}
	if (next_bit_to_set(h2) > 0)
		assert(!(test_bit(h2, 1) == 0 && test_bit(h2, 0) == 1));	// call 		: This history is not correct!!
	if (counter < 4)
		return 0;
	if (counter == 4)
		return 1;
	assert(0);												// This history is not correct!!
	return -1;
}

enum game_states getGameState(int64 history) {
	char bit = next_bit_to_set(history);
	if (bit < 3)
		return PRE_FLOP;
	else if (bit < 5) {
		if (test_bit(history, 2) == 1 && test_bit(history, 3) == 1)		// raise
			return PRE_FLOP;
		else															// call
			return FLOP;
	}

	int counter = 0;
	int64 h2 = history;
	while (h2 > 7) {
		if (test_bit(h2, 2) == 0 && test_bit(h2, 3)) {		// call
			++counter;
			h2 = (h2 >> 4);
			continue;
		}
		h2 = (h2 >> 2);
	}

	switch (counter) {
	case 0:
		return PRE_FLOP;
	case 1:
		return FLOP;
	case 2:
		return TURN;
	case 3:
		return RIVER;
	case 4:
		return SHOW_DOWN;
	default:
		assert(0);								// This history is not correct!!!
		return SHOW_DOWN;
	}
}

enum actions getLastAction(int64 history) {
	if (history == 0)
		assert(0);								// illegal argument!!
	char bit = getNextBitToSet(history);
	if (test_bit(history, bit-2) == 1)
		if (test_bit(history, bit-1) == 1)
			return BET_RAISE;
		else
			return FOLD;
	else
		if (test_bit(history, bit-1) == 1)
			return CHECK_CALL;
		else {
			assert(0);
			return FOLD;
		}
}

/*
 * Before invoking this method should be checked by isTerminal() whether this the history is over
 * because the method does not consider the case if the history is over.
 *
 * return: an arranged array of the possible actions at this history; arranged means like {f, c, r}
 */
array_actions A(int64 history) {
	array_actions fst = {.pointer = malloc(3*sizeof(enum actions)), .size = 3};
	fst.pointer[0] = FOLD;
	fst.pointer[1] = CHECK_CALL;
	fst.pointer[2] = BET_RAISE;
	array_actions snd = {.pointer = malloc(2 * sizeof(enum actions)), .size = 2};
	snd.pointer[0] = CHECK_CALL;
	snd.pointer[1] = BET_RAISE;
	array_actions trd = {.pointer = malloc(2 * sizeof(enum actions)), .size = 2};
	trd.pointer[0] = FOLD;
	trd.pointer[1] = CHECK_CALL;
	if (history == 0) {
		free(snd.pointer);
		free(trd.pointer);
		return fst;
	}
	char bit = getNextBitToSet(history)-1;
	int numR = 0;
	if (test_bit(history, bit) == 1) {
		if (test_bit(history, bit-1) == 0) {											// call
			free(fst.pointer);
			free(trd.pointer);
			return snd;
		}
		else																			// raise
			for (int i = 2; i < bit+1; i+=2)
				if (test_bit(history, bit-i) == 1 && test_bit(history, bit-i+1) == 1)	// raise
					++numR;
				else
					break;
	}
	if (numR > 4)
		assert(0);													// this history is not correct
	else if (numR == 4) {
		free(fst.pointer);
		free(snd.pointer);
		return trd;
	}
	free(snd.pointer);
	free(trd.pointer);
	return fst;
}

/* The small blind is 1, the big blind is 2.
 * So the small bet is 2 and the big bet is 4.
 */
int getPayoff(int64 history) {
	int payoff = 1 + 2;					// = small blind + big blind
	int64 h = 0;
	enum game_states gs = getGameState(h);
	for (int i = 1; i < getNextBitToSet(history); i+=2)
		if (test_bit(history, i-1) == 1)
			if (test_bit(history, i) == 0)			// fold
				break;
			else {									// raise
				add(&h, BET_RAISE);
				switch (gs) {
				case PRE_FLOP:
				case FLOP:
					payoff += 4;
					break;
				case TURN:
				case RIVER:
					payoff += 8;
					break;
				default:
					assert(0);
					break;
				}
			}
		else
			if (test_bit(history, i) == 1) {		// call
				add(&h, CHECK_CALL);
				switch (gs) {
				case PRE_FLOP:
				case FLOP:
					payoff += 2;
					break;
				case TURN:
				case RIVER:
					payoff += 4;
					break;
				default:
					assert(0);
					break;
				}
			} else
				assert(0);			// this history is not correct
	return payoff;
}

int64 history_without_last_action(int64 history) {
	if (history == 0)
		assert(0);
	char bit = getNextBitToSet(history);
	int64 h = history;
	CLEAR_BIT(h, bit-1);
	CLEAR_BIT(h, bit-2);
	return h;
}

int64 history_with(int64 history, enum actions action) {
	int64 h = history;
	char bit = getNextBitToSet(history);
	if (action == FOLD)
		SET_BIT(h, bit);
	else if (action == CHECK_CALL)
		SET_BIT(h, bit+1);
	else {									// case: BET_RAISE
		SET_BIT(h, bit);
		SET_BIT(h, bit+1);
	}
	return h;
}



#define SQR(val) ((val) * (val))
#define CUBE(val) (SQR(val) * (val))
#define QUAD(val) (SQR(SQR(val)))

/* lookupInfosets(h) retrieves all of the information
 * sets consistent with history and the current player P(h)’s range of
 * possible private outcomes, whether sampled (|~I| = 1) or not.
 *
 * @return all of the information sets consistent with history (parameter).
 *
 *
 * The size of info_sets (return value) is equal to pow(buckets,getGameState(h))*sizeof(int64).
 */
array_long_long look_up_infosets(int64 history) {
	switch (getGameState(history)) {
	case PRE_FLOP: {
		int64 *info_sets = malloc(buckets * sizeof(int64));
		for (int a = 1; a <= buckets; a++)
			info_sets[a-1] = createInformationSet(history, createBucketSequence(&a, 1));
		array_long_long all = {.pointer=info_sets, .size=buckets};
		return all;
		break;
	}
	case FLOP: {
		int ali[SQR(buckets)][2];
		int i = 0;
		for (int a = 1; a <= buckets; a++)
			for (int b = 1; b <= buckets; b++) {
				ali[i][0] = a;
				ali[i][1] = b;
				++i;
			}
		int64 *info_sets = malloc(i * sizeof(int64));				// note, after the loop is i = pow(buckets, 2)
		for (int j = 0; j < i; j++)
			info_sets[j] = createInformationSet(history, createBucketSequence(ali[j], 2));
		array_long_long all = {.pointer=info_sets, .size=SQR(buckets)};
		return all;
		break;
	}
	case TURN: {
		int ali[CUBE(buckets)][3];
		int i = 0;
		for (int a = 1; a <= buckets; a++)
			for (int b = 1; b <= buckets; b++)
				for (int c = 1; c <= buckets; c++) {
					ali[i][0] = a;
					ali[i][1] = b;
					ali[i][2] = c;
					++i;
				}
		int64 *info_sets = malloc(i * sizeof(int64));				// note, after the loop is i = pow(buckets, 3)
		for (int j = 0; j < i; j++)
			info_sets[j] = createInformationSet(history, createBucketSequence(ali[j], 3));
		array_long_long all = {.pointer=info_sets, .size=CUBE(buckets)};
		return all;
		break;
	}
	case RIVER: {
		int ali[QUAD(buckets)][4];
		int i = 0;
		for (int a = 1; a <= buckets; a++)
			for (int b = 1; b <= buckets; b++)
				for (int c = 1; c <= buckets; c++)
					for (int d = 1; d <= buckets; d++) {
						ali[i][0] = a;
						ali[i][1] = b;
						ali[i][2] = c;
						ali[i][3] = d;
						++i;
					}
		int64 *info_sets = malloc(i * sizeof(int64));				// note, after the loop is i = pow(buckets, 4)
		for (int j = 0; j < i; j++)
			info_sets[j] = createInformationSet(history, createBucketSequence(ali[j], 4));
		array_long_long all = {.pointer=info_sets, .size=QUAD(buckets)};
		return all;
		break;
	}
	default:
		assert(0);
		break;
	}
	assert(0);
	return (array_long_long) {.pointer = NULL, .size = -1};
}








/*
 * strategy.c
 *
 *  Created on: 20.02.2014
 *      Author: Joschka
 */

/*Regret Matching according some popular thesis of http://poker.cs.ualberta.ca/
* in the period from 2007-2011.
*
* @param indexIS the index of the information set
* @param r_I_f for fold
* @param r_I_c for check/call
* @param r_I_r for bet/raise
 */
void regret_matching(vector_t *strategy, int indexIS, float r_I_f, float r_I_c, float r_I_r) {
	array_actions as = A(getHistory(I[indexIS]));
	float r_p = fmax(0, r_I_f + r_I_c + r_I_r);
	float r_I_f_p = fmax(0, r_I_f), r_I_c_p = fmax(0, r_I_c), r_I_r_p = fmax(0, r_I_r);
	float r_I_p[] = {r_I_f_p, r_I_c_p, r_I_r_p};
	int le = as.size;

	if (r_p > 0) {
		if (le == 3) {
			strategy[indexIS][0] = r_I_f_p / r_p;
			strategy[indexIS][1] = r_I_c_p / r_p;
			strategy[indexIS][2] = r_I_r_p / r_p;
		} else {
			for (int i = 0; i < 3; i++)
				strategy[indexIS][i] = 0.0;
			strategy[indexIS][as.pointer[0]] = r_I_p[as.pointer[0]] / r_p;
			strategy[indexIS][as.pointer[1]] = r_I_p[as.pointer[1]] / r_p;
		}
	} else
		if (le == 3)
			for (int i = 0; i < 3; i++)
				strategy[indexIS][i] = 1./3.;
		else {
			for (int i = 0; i < 3; i++)
				strategy[indexIS][i] = 0.0;
			strategy[indexIS][as.pointer[0]] = 2./3.;
			strategy[indexIS][as.pointer[1]] = 2./3.;
		}
	free(as.pointer);
}








/* This information set comparator compares by the BitSet of the information sets.
 * Comparing by the BitSet means comparing by the number which the BitSets represents.
 *
 * @author Joschka
 */
int cmp_long_long(const void *wert1, const void *wert2) {
//	int64 sum = (*(int64*)wert1 - *(int64*) wert2);
	if (*(int64*)wert1 > *(int64*)wert2)
		return 1;
	else if (*(int64*)wert1 < *(int64*)wert2)
		return -1;
	else
		return 0;
}

/* This information set comparator compares by the strength of the information sets.
 * Comparing by the strength means comparing by the last bucket of the information set
 * because the last bucket is the important bucket for determining the strength.
 *
 * @author Joschka
 */
int cmp_strength(const void *wert1, const void *wert2) {
	return (get_last_bucket(*(int64*) wert1) - get_last_bucket(*(int64*) wert2));
}

/*
 * The length of ev is (IL*sizeof(float*)).
 */
float* terminal_node_evaluation(float *ev, int value, int64 *pIS, int64 *opIS, int num_IS, float probOpIS[]) {
	float winsum = 0.0;
	int j = 0;
	for (int i = 0; i < num_IS; i++) {
		while (get_last_bucket(opIS[j]) < get_last_bucket(pIS[i])) {
			winsum += probOpIS[get_index(opIS[j])] * value;
			j++;														// correct
		}
		ev[get_index(pIS[i])] = winsum * value;
	}

	float losesum = 0.0;
	j = num_IS - 1;
	for (int i = num_IS - 1; i > -1; i--) {
		while (get_last_bucket(opIS[j]) > get_last_bucket(pIS[i])) {
			losesum += probOpIS[get_index(opIS[j])];
			j--;														// correct
		}
		ev[get_index(pIS[i])] -= losesum * value;
	}

	return ev;
}

#define COPY_ARRAY(src, dst, limit) {\
	for (int i = 0; i < limit; i++)\
		dst[i] = src[i];}

/**
 * The return value has the size Il*sizeof(float).
 * pi is the probability for the player, in which view the tree is traversed, to reach the information sets
 * pOp is the probability for the opponent to reach the information sets
 */
float* WalkTree(float *ev, int64 h, int player, float pi[], float pOp[]) {
	if (isTerminal(h)) {
		int payoff = getPayoff(h);

		int64 h2 = history_without_last_action(h);
		array_long_long info_sets = look_up_infosets(h2);
		int num = pow(buckets, getGameState(h2));
		int *indizesIS = malloc(num * sizeof(int));
		for (int i = 0; i < num; i++)
			indizesIS[i] = get_index(info_sets.pointer[i]);

		if (getLastAction(h) == FOLD)
			if (get_player_to_act(h) == player)
				for (int i = 0; i < num; i++)
					ev[indizesIS[i]] = payoff;
			else
				for (int i = 0; i < num; i++)
					ev[indizesIS[i]] = -payoff;
		else {
			qsort(info_sets.pointer, num, sizeof(int64), cmp_strength);		// does this work???
//			if (get_player_to_act(h2))			// bug!! of 14.06.2014
//				terminal_node_evaluation(ev, payoff, info_sets.pointer, info_sets.pointer, num, p1);
//			else
//				terminal_node_evaluation(ev, payoff, info_sets.pointer, info_sets.pointer, num, p2);
			terminal_node_evaluation(ev,  payoff, info_sets.pointer, info_sets.pointer, num, pOp);
		}
		free(info_sets.pointer);
		free(indizesIS);
		return ev;
	}

	array_long_long info_sets = look_up_infosets(h);
	int num = pow(buckets, getGameState(h));
	int *indizes_IS = malloc(num * sizeof(int));
	for (int i = 0; i < num; i++)
		indizes_IS[i] = get_index(info_sets.pointer[i]);
	free(info_sets.pointer);
	for (int i = 0; i < num; i++)
		regret_matching(strategy, indizes_IS[i] ,r[indizes_IS[i]][0], r[indizes_IS[i]][1], r[indizes_IS[i]][2]);
	int player_to_act = get_player_to_act(h);

	array_actions aa = A(h);
	enum actions *a = aa.pointer;
	for (int j = 0; j < aa.size; j++)
		if (player_to_act == player) {
			float *P = malloc(Il*sizeof(float));			// P = pi'
			memcpy(P, pi, Il*sizeof(float));
			for (int i = 0; i < num; i++)
				P[indizes_IS[i]] *= strategy[indizes_IS[i]][a[j]];
			float *U = malloc(Il*sizeof(float));
			memset(U, 0, Il*sizeof(float));
			WalkTree(U, history_with(h, a[j]), player, P, pOp);								// U = u'
			free(P);
			for (int i = 0; i < num; i++)
				m[indizes_IS[i]][a[j]] = U[indizes_IS[i]];
			for (int i = 0; i < num; i++)
				ev[indizes_IS[i]] += strategy[indizes_IS[i]][a[j]] * U[indizes_IS[i]];
			free(U);
		} else {
			float *P = malloc(Il*sizeof(float));			// P = pOp'
			memcpy(P, pOp, Il*sizeof(float));
			for (int i = 0; i < num; i++)
				P[indizes_IS[i]] *= strategy[indizes_IS[i]][a[j]];
			float *U = malloc(Il*sizeof(float));
			memset(U, 0, Il*sizeof(float));
			WalkTree(U, history_with(h, a[j]), player, pi, P);								// U = u'
			free(P);
			for (int i = 0; i < num; i++)
				ev[indizes_IS[i]] += U[indizes_IS[i]];
			free(U);
		}
	free(a);

	if (player_to_act == player)
		for (int i = 0; i < sizeof(indizes_IS)/sizeof(int); i++) {
			array_actions aa = A(h);
			enum actions *a = aa.pointer;
			for (int j=0; j<aa.size; j++) {
				r[indizes_IS[i]][a[j]] += m[indizes_IS[i]][a[j]] - ev[indizes_IS[i]];
				s[indizes_IS[i]][a[j]] += pi[indizes_IS[i]] * strategy[indizes_IS[i]][a[j]];
			}
		}
//			if (player_to_act == 0)	{																// the first player		// bug!! of 14.06.2014
//				array_actions aa = A(h);
//				enum actions *a = aa.pointer;
//				for (int j = 0; j < aa.size; j++) {
//					r[indizes_IS[i]][a[j]] += m[indizes_IS[i]][a[j]] - ev[indizes_IS[i]];
//					s[indizes_IS[i]][a[j]] += p1[indizes_IS[i]] * strategy[indizes_IS[i]][a[j]];
//				}
//				free(aa.pointer);
//			}
//			else {																					// the second player
//				array_actions aa = A(h);
//				enum actions *a = aa.pointer;
//				for (int j = 0; j < aa.size; j++) {
//					r[indizes_IS[i]][a[j]] += m[indizes_IS[i]][a[j]] - ev[indizes_IS[i]];
//					s[indizes_IS[i]][a[j]] += p2[indizes_IS[i]] * strategy[indizes_IS[i]][a[j]];
//				}
//				free(aa.pointer);
//			}

	free(indizes_IS);
	return ev;
}

int INDEX = 0;

void walk_history(int64 *history) {
	if (isTerminal(history[INDEX]))
		return;
	array_actions aa = A(history[INDEX]);
	int64 copy = history[INDEX];
	for (int j = 0; j < aa.size; j++) {
		int64 h = history_with(copy, aa.pointer[j]);
		++INDEX;
		history[INDEX] = h;
		walk_history(history);
	}
	free(aa.pointer);
}


int64* get_all_histories() {
	int64 *hs = malloc(23691 * sizeof(int64));
	memset(hs, 0, 23691*sizeof(int64));
	walk_history(hs);
	return hs;
}

array_long_long get_all_infosets_to(int64* history, int size) {
	int num = 0;
	for (int i = 0; i < size; i++)
		if (isTerminal(history[i]))
			continue;
		else {
			array_long_long all = look_up_infosets(history[i]);
			num += all.size;
			free(all.pointer);
		}
	int64 *info_sets = malloc(num * sizeof(int64));
	int k = 0;
	for (int i = 0; i < size; i++)
		if (isTerminal(history[i]))
			continue;
		else {
			array_long_long all = look_up_infosets(history[i]);
			for (int j = 0; j < all.size; j++) {
				info_sets[k] = all.pointer[j];
				++k;
			}
			free(all.pointer);
		}
	return (array_long_long) {.pointer = info_sets, .size = num};
}

void create_information_sets() {
	int64 *histories = get_all_histories();
	array_long_long all = get_all_infosets_to(histories, 23691);			// 23691 is the number of different possible histories
/*	Histories = malloc(23691 * sizeof(int64));			// debug
//	for (int i=0; i < 23691; i++)						// debug
		Histories[i] = histories[i];					// debug
*/	free(histories);
	I = malloc(all.size * sizeof(int64));
	for (int i = 0; i < all.size; i++)
		I[i] = all.pointer[i];
	free(all.pointer);
	qsort(I, all.size, sizeof(int64), cmp_long_long);
	Il = all.size;
	printf("The number of information sets is %d\n", Il);
}

void solve(int t) {
	float *P = malloc(Il*sizeof(float));
	for (int i = 0; i < t; i++)
		for (int player = 0; player < 2; player++) {
			for (int i = 0; i < Il; i++)
				P[i] = 1;
			float *ev = malloc(Il*sizeof(float));
			memset(ev, 0, Il*sizeof(float));
			WalkTree(ev, 0, player, P, P);
			free(ev);
		}
	free(P);
}

void save_strategy(vector_t *avg_strategy) {
	char name_src[100] = "C:\\pokerBot\\bot_v1_3_x\\datastructure\\strategy-";
	char file_name[100];
	sprintf(file_name, "%s%d%s", name_src, buckets, "-buckets.strategy");
	FILE *save = fopen(file_name, "w");					// Windows!!
	if (NULL == save) {
		fprintf(stderr, "Error in opening file with path %s save_strategy()\n", file_name);
		assert(0);
	}
	for (int i=0; i<Il; i++)
		fprintf(save, "%g %g %g\n", avg_strategy[i][0],avg_strategy[i][1],avg_strategy[i][2]);
	fclose(save);
}

vector_t* get_average_strategy(vector_t *avg) {
	memset(avg, 0, Il*sizeof(vector_t));
	for (int i = 0; i < Il; i++)
		regret_matching(avg, i, s[i][0], s[i][1], s[i][2]);
	return avg;
}

int main() {
	setvbuf(stdout, NULL, _IONBF, 0);
	setvbuf(stderr, NULL, _IONBF, 0);

	create_information_sets();

	strategy = (vector_t*) malloc(Il * sizeof(vector_t));
	r = (vector_t*) malloc(Il * sizeof(vector_t));
	s = (vector_t*) malloc(Il * sizeof(vector_t));
	m = (vector_t*) malloc(Il * sizeof(vector_t));
	memset(strategy, 0, Il*sizeof(vector_t));
	memset(r, 0, Il*sizeof(vector_t));
	memset(s, 0, Il*sizeof(vector_t));
	memset(m, 0, Il*sizeof(vector_t));

	int t = 1;
	printf("Please enter the number of iterations: ");
	scanf("%d", &t);
	
	while(t>0) {
		solve(t);
		printf("Please enter the number of iterations: ");
		scanf("%d", &t);
	}
	
	
	vector_t *avg = (vector_t*) malloc(Il * sizeof(vector_t));
	save_strategy(get_average_strategy(avg));
	free(avg);

	free(r);
	free(s);
	free(m);
	free(strategy);
	free(I);
}
