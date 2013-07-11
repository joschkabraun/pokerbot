package handHistory;

import java.util.*;

import cardBasics.CardList;
import gameBasics.GameState;
import gameBasics.Player;
import gameBasics.Pot;
import gameBasics.PlayerYou;

@SuppressWarnings("static-access")
public class HandHistory

{
	
	/**
	 * The room in which the bot plays. For example PokerStars or WinnerPoker.
	 */
	public String pokerRoom;
	
	/**
	 * The number of the played hand. Review whether there is "hand" or "game".
	 */
	public long handNumber;
	
	/**
	 * The type of the played game.
	 */
	public String gameType;
	
	/**
	 * The limit of the bets in the game. For example no limit, fixed limit, pot limit ...
	 */
	public String limit;
	
	/**
	 * SB is the small blind (for no limit) or rather the small bet (for limit).
	 */
	public double SB;
	
	/**
	 * BB is the big blind (for no limit) or rather the big bet (for limit).
	 */
	public double BB;
	
	/**
	 * The time when the game was played.
	 * Attention. It is the server time.
	 */
	public GregorianCalendar time;
	
	/**
	 * The name of the table.
	 */
	public String tableName;
	
	/**
	 * The maximal number of players who can play at this table.
	 */
	public int maxSeatAtTable;
	
	/**
	 * The number of players who play at this table in this round.
	 */
	public int numberPlayersAtTable;
	
	/**
	 * The allocation (diversification) of the seat position.
	 */
	public ArrayList<SeatNumberPlayer> listSeatNumberToPlayer;
	
	/**
	 * All players in the game.
	 */
	public ArrayList<Player> allPlayers;
	
	/**
	 * The player who is the button.
	 */
	public Player button;
	
	/**
	 * The list of players who are the small blinds.
	 */
	public ArrayList<Player> smallBlindP;
	
	/**
	 * The list of players who are the big blinds. Usually the list is out of one elements but if someone want to play at once, he/she will pay the big blind.
	 */
	public ArrayList<Player> bigBlindP;
	
	/**
	 * The pot of the game.
	 */
	public Pot pot;
	
	/**
	 * The documentation of the things happened in the pre-flop-phase.
	 */
	public PreFlop preFlop;
	
	/**
	 * The documentation of the things happened in the flop-phase.
	 */
	public Flop flop;
	
	/**
	 * The documentation of the things happened in the turn-phase.
	 */
	public Turn turn;
	
	/**
	 * The documentation of the things happened in the river-phase.
	 */
	public River river;
	
	/**
	 * The documentation of the things happened in the show down.
	 */
	public ShowDown showDown;
	
	/**
	 * The summary of the whole game. The summary contains the pot size and the size of possible side pots, the rake, the board, if there was a board,
	 * and a summary of the acts of the players and them profit in this round.
	 */
	public Summary summary;
	
	/**
	 * The game state in which the game current is.
	 * The possible stages are pre-flop, flop, turn, river, showDown, summary (the game is over).
	 */
	public GameState state;
	
	/**
	 * The number of players they are in game.
	 */
	public int howManyPlayerInGame;
	
	/**
	 * An ArrayList with all BettingRounds which are in this HandHistory.
	 */
	public ArrayList<BettingRound> bettingRounds;
	
	/**
	 * An ArrayList which assigns a Player a PokerChallengeGameState (that is the GameState in which the Player is the first time out).
	 * The ArrayList is sorted by the diversification of the Players (the first entry is the small blind, the second entry is the big blind etc.).
	 */
	public ArrayList<PlayerPokerChallengeGameState> playerStatesOut;
	
	public HandHistory() {
		this.allPlayers = new ArrayList<Player>();
		this.bettingRounds = new ArrayList<BettingRound>();
		this.bigBlindP = new ArrayList<Player>();
		this.button = new Player();
		this.listSeatNumberToPlayer = new ArrayList<SeatNumberPlayer>();
		this.pot = new Pot();
		this.preFlop = new PreFlop();
		this.flop = new Flop();
		this.turn = new Turn();
		this.river = new River();
		this.showDown = new ShowDown();
		this.smallBlindP = new ArrayList<Player>();
		this.summary = new Summary();
		this.time = new GregorianCalendar();
	}
	
	public HandHistory(HandHistory hh) {
		this.allPlayers = hh.allPlayers;
		this.BB = hh.BB;
		this.bettingRounds = hh.bettingRounds;
		this.bigBlindP = hh.bigBlindP;
		this.button = new Player(hh.button);
		this.flop = new Flop(hh.flop);
		this.gameType = hh.gameType;
		this.handNumber = hh.handNumber;
		this.howManyPlayerInGame = hh.howManyPlayerInGame;
		this.limit = hh.limit;
		this.listSeatNumberToPlayer = hh.listSeatNumberToPlayer;
		this.maxSeatAtTable = hh.maxSeatAtTable;
		this.numberPlayersAtTable = hh.numberPlayersAtTable;
		this.playerStatesOut = hh.playerStatesOut;
		this.pokerRoom = hh.pokerRoom;
		this.pot = new Pot(hh.pot);
		this.preFlop = new PreFlop(hh.preFlop);
		this.river = new River(hh.river);
		this.SB = hh.SB;
		this.showDown = new ShowDown(hh.showDown);
		this.smallBlindP = hh.smallBlindP;
		this.state = hh.state;
		this.summary = new Summary(hh.summary);
		this.tableName = hh.tableName;
		this.time = hh.time;
		this.turn = new Turn(hh.turn);
	}
		
	@Override
	public String toString() {
		String sbNames = "";
		for ( int i = 0; i < this.smallBlindP.size(); i++ )
			sbNames += this.smallBlindP.get(i).name + ", ";
		sbNames = sbNames.substring( 0, sbNames.length()-2 );
		String bbNames = "";
		for ( int i = 0; i < this.bigBlindP.size(); i++ )
			bbNames += this.bigBlindP.get(i).name + ", ";
		bbNames = bbNames.substring( 0, bbNames.length()-2 );
		String s = String.format( "poker room: " + this.pokerRoom + ", number played hand: " + this.handNumber +
				 ", time: " + this.time.YEAR + "/" + this.time.MONTH+1 + "/" + this.time.DAY_OF_YEAR+" "+this.time.HOUR_OF_DAY+":"+this.time.MINUTE + ":"+this.time.MILLISECOND +
				"%nname table: " + this.tableName + ", maximal seats table: " + this.maxSeatAtTable + ", players at the table: " + this.numberPlayersAtTable +
				"%nlimit: "+this.limit+", small blind: " + this.SB +", big blind: "+ this.BB +
				"%nnall players: " + this.allPlayers +
				"%nlist seat number to player: " + this.listSeatNumberToPlayer + ", many players in game: " + this.howManyPlayerInGame +
				"%nname player BU: " + this.button.name + ", name players SB: (" + sbNames + "), name players BB: (" + bbNames + "), pot: " + this.pot );
		if ( ! this.preFlop.isEmpty() ) {
			s += String.format( "%n%npre-flop-phase:%n" + this.preFlop );
			if ( ! this.flop.isEmpty() ) {
				s += String.format( "%n%nflop-phase:%n" + this.flop );
				if ( ! this.turn.isEmpty() ) {
					s += String.format( "%n%nturn-phase:%n" + this.turn );
					if ( ! this.river.isEmpty() )
						s += String.format( "%n%nriver-phase:%n" + this.river );
				}
			}
			if ( this.showDown != null )
				s += String.format( "%n" + this.showDown );
		}
		return s;
				
	}
	
	public PlayerYou getPlayerYou( String nameYou )
	{
		int index = parser.ParserCreatorWinnerPoker1Table.indexOf( this.allPlayers, nameYou );
		
		if ( index == -1 )
			throw new IllegalStateException( "Getting the PlayerYou of the game was not sucessfull because he is not contained in the hand history!" );
		
		return new PlayerYou(allPlayers.get( index ), this.preFlop.startHand);
	}
	
	/**
	 * @return the common cards / the open cards
	 */
	public CardList getCommonCards() {
		if ( this.river.board.size() > 0 )
			return this.river.board;
		else
			if ( this.turn.board.size() > 0 )
				return this.turn.board;
			else
				if ( this.flop.board.size() > 0 )
					return this.flop.board;
				else
					return new CardList();
	}
	
	/**
	 * Returns the amount won (inclusive negative values) by the players.
	 * 
	 * @return the amount won by the players
	 */
	public ArrayList<PlayerMoney> getAmountWonByPlayersList() {
		ArrayList<PlayerMoney> pml = new ArrayList<PlayerMoney>();
		for ( Player p : this.allPlayers )
			pml.add( new PlayerMoney(p, 0) );
		
		for ( Player p : this.smallBlindP )
			pml.get(getIndex(pml, p)).money -= this.SB;
		for ( Player p : this.bigBlindP )
			pml.get(getIndex(pml, p)).money -= this.BB;
		
		for ( BettingRound br : this.bettingRounds)
			for ( PlayerAction pa : br.playerActionList )
				pml.get(getIndex(pml, pa.player)).money -= pa.action.money;
		
		for ( PlayerMoney pm : this.showDown.playerMoneyList )
			pml.get(getIndex(pml, pm.player)).money += pm.money;
		
		return pml;
	}
	
	public PlayerMoney[] getAmountWonByPlayersArray() {
		ArrayList<PlayerMoney> pml = this.getAmountWonByPlayersList();
		PlayerMoney[] pma = new PlayerMoney[pml.size()];
		for ( int i = 0; i < pma.length; i++ )
			pma[i] = pml.get(i);
		return pma;
	}
	
	/**
	 * Returns the first index of the Player in the ArrayList of PlayerActions if the player is in the ArrayList else -1.
	 * 
	 * @return if "p" is in "pml", the first index of "p" in "pml"; else -1 
	 */
	public static int getIndex( ArrayList<PlayerMoney> pml, Player p ) {
		for ( int i = 0; i < pml.size(); i++ )
			if ( pml.get(i).player.name.equals(p.name) )
				return i;
		return -1;
	}
	
	/**
	 * Returns the first index of the Player in the ArrayList of PlayerActions if the player is in the ArrayList else -1.
	 * 
	 * @return if "p" is in "pml", the first index of "p" in "pml"; else -1 
	 */
	public static int getIndex( ArrayList<PlayerMoney> pml, String pName ) {
		for ( int i = 0; i < pml.size(); i++ )
			if ( pml.get(i).player.name.equals(pName) )
				return i;
		return -1;
	}
	
	/**
	 * Returns all players who did not fold yet.
	 * @return all players who are still in game
	 */
	public ArrayList<Player> getPlayerActive() {
		ArrayList<Player> actPs = new ArrayList<Player>();
		for ( Player p : this.allPlayers )
			actPs.add(p);
		for ( BettingRound br : this.bettingRounds )
			for ( PlayerAction pa : br.playerActionList )
				if ( indexOf(actPs, pa.player.name) == -1 )
					continue;
				else
					if ( pa.action.equals("fold") )
						actPs = remove(actPs, pa.player);
		return actPs;
	}
	
	public static int indexOf( ArrayList<Player> ps, Player p ) {
		for ( int i = 0; i < ps.size(); i++ )
			if ( ps.get(i).name.equals(p.name) )
				return i;
		return -1;
	}
	
	public static int indexOf( ArrayList<Player> ps, String pName ) {
		for ( int i = 0; i < ps.size(); i++ )
			if ( ps.get(i).name.equals(pName) )
				return i;
		return -1;
	}
	
	public ArrayList<Player> remove( ArrayList<Player> ps, Player p ) {
		ArrayList<Player> ret = new ArrayList<Player>();
		for ( Player p2 : ps )
			if ( ! p2.name.equals(p.name) )
				ret.add(p2);
		return ret;
	}
	
	public CardList getBoard() {
		strategy.strategyPokerChallenge.data.GameState gameState = this.bettingRounds.get(this.bettingRounds.size()-1).getPokerChallengeGameState();
		switch ( gameState ) {
		case FLOP:
			return this.flop.board.clone();
		case TURN:
			return this.turn.board.clone();
		case RIVER:
			return this.river.board.clone();
		default:
			return new CardList();
		}
	}
	
	/**
	 * Returns the contribution of every player in the pot.
	 * @return the contribution of every player in the pot
	 */
	public ArrayList<PlayerMoney> getInPot() {
		ArrayList<PlayerMoney> pml = new ArrayList<PlayerMoney>();
		for ( Player p : this.allPlayers )
			pml.add(new PlayerMoney(p, 0));
		for ( BettingRound br : this.bettingRounds )
			for ( PlayerAction pa : br.playerActionList )
				pml.get(getIndex(pml, pa.player.name)).money += pa.action.money;
		return pml;
	}
	
}
