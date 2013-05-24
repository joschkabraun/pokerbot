package handHistory;

import java.util.*;
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
	 * The phase/stage in which the game current is.
	 * The possible stages are pre-flop, flop, turn, river, showDown, summary (the game is over).
	 */
	public String stage;
	
	/**
	 * The number of players they are in game.
	 */
	public int howManyPlayerInGame;
	
	public HandHistory() {
		this.pot = new Pot();
		this.preFlop = new PreFlop();
		this.flop = new Flop();
		this.turn = new Turn();
		this.river = new River();
		this.showDown = new ShowDown();
		this.summary = new Summary();		
	}
	
	public HandHistory( String pokerRoom, long handNumber, GregorianCalendar time, String tableName, int maxSeatTable,  ArrayList<SeatNumberPlayer> listSeatNumberToPlayer,
			Player button, ArrayList<Player> smallBlind, ArrayList<Player> bigBlind, PreFlop preFlop )
	{
		this.pokerRoom = pokerRoom;
		this.handNumber = handNumber;
		this.time = new GregorianCalendar( time.YEAR, time.MONTH, time.DATE, time.HOUR, time.MINUTE, time.SECOND );
		this.tableName = tableName;
		this.maxSeatAtTable = maxSeatTable;
		this.listSeatNumberToPlayer = new ArrayList<SeatNumberPlayer>( listSeatNumberToPlayer );
		this.allPlayers = new ArrayList<Player>();
		for ( int i = 0; i < this.listSeatNumberToPlayer.size(); i++ )
			this.allPlayers.add( new Player( this.listSeatNumberToPlayer.get(i).player ) );
		this.button = new Player( button );
		this.smallBlindP = new ArrayList<Player>( smallBlind );
		this.bigBlindP = new ArrayList<Player>( bigBlind );
		this.pot = new Pot( SB + BB + preFlop.pot.money );
		this.preFlop = new PreFlop( preFlop );
		this.stage = "pre-flop";
		this.howManyPlayerInGame = this.preFlop.howManyPlayersInGame;
	}
	
	public HandHistory( String pokerRoom, long handNumber, GregorianCalendar time, String tableName, int maxSeatTable,  ArrayList<SeatNumberPlayer> listSeatNumberToPlayer,
			Player button, ArrayList<Player> smallBlind, ArrayList<Player> bigBlind, PreFlop preFlop, Summary summary )
	{
		this.pokerRoom = pokerRoom;
		this.handNumber = handNumber;
		this.time = new GregorianCalendar( time.YEAR, time.MONTH, time.DATE, time.HOUR, time.MINUTE, time.SECOND );
		this.tableName = tableName;
		this.maxSeatAtTable = maxSeatTable;
		this.listSeatNumberToPlayer = new ArrayList<SeatNumberPlayer>( listSeatNumberToPlayer );
		for ( int i = 0; i < this.listSeatNumberToPlayer.size(); i++ )
			this.allPlayers.add( new Player( this.listSeatNumberToPlayer.get(i).player ) );
		this.button = new Player( button );
		this.smallBlindP = new ArrayList<Player>( smallBlind );
		this.bigBlindP = new ArrayList<Player>( bigBlind );
		this.pot = new Pot( SB + BB + preFlop.pot.money );
		this.preFlop = new PreFlop( preFlop );
		this.summary = new Summary( summary );
		this.stage = "summary";
		this.howManyPlayerInGame = this.preFlop.howManyPlayersInGame;
	}
	
	public HandHistory( String pokerRoom, long handNumber, GregorianCalendar time, String tableName, int maxSeatTable,  ArrayList<SeatNumberPlayer> playersAfterSeatPos,
			Player button, ArrayList<Player> smallBlind, ArrayList<Player> bigBlind, PreFlop preFlop, Flop flop )
	{
		this.pokerRoom = pokerRoom;
		this.handNumber = handNumber;
		this.time = new GregorianCalendar( time.YEAR, time.MONTH, time.DATE, time.HOUR, time.MINUTE, time.SECOND );
		this.tableName = tableName;
		this.maxSeatAtTable = maxSeatTable;
		this.listSeatNumberToPlayer = new ArrayList<SeatNumberPlayer>( listSeatNumberToPlayer );
		for ( int i = 0; i < this.listSeatNumberToPlayer.size(); i++ )
			this.allPlayers.add( new Player( this.listSeatNumberToPlayer.get(i).player ) );
		this.button = new Player( button );
		this.smallBlindP = new ArrayList<Player>( smallBlind );
		this.bigBlindP = new ArrayList<Player>( bigBlind );
		this.pot = new Pot( SB + BB + preFlop.pot.money + flop.pot.money );
		this.preFlop = new PreFlop( preFlop );
		this.flop = new Flop( flop );
		this.stage = "flop";
		this.howManyPlayerInGame = this.flop.howManyPlayersInGame;
	}
	
	public HandHistory( String pokerRoom, long handNumber, GregorianCalendar time, String tableName, int maxSeatTable,  ArrayList<SeatNumberPlayer> playersAfterSeatPos, Player button,
			ArrayList<Player> smallBlind, ArrayList<Player> bigBlind, PreFlop preFlop, Flop flop, Summary summary )
	{
		this.pokerRoom = pokerRoom;
		this.handNumber = handNumber;
		this.time = new GregorianCalendar( time.YEAR, time.MONTH, time.DATE, time.HOUR, time.MINUTE, time.SECOND );
		this.tableName = tableName;
		this.maxSeatAtTable = maxSeatTable;
		this.listSeatNumberToPlayer = new ArrayList<SeatNumberPlayer>( listSeatNumberToPlayer );
		for ( int i = 0; i < this.listSeatNumberToPlayer.size(); i++ )
			this.allPlayers.add( new Player( this.listSeatNumberToPlayer.get(i).player ) );
		this.button = new Player( button );
		this.smallBlindP = new ArrayList<Player>( smallBlind );
		this.bigBlindP = new ArrayList<Player>( bigBlind );
		this.pot = new Pot( SB + BB + preFlop.pot.money + flop.pot.money );
		this.preFlop = new PreFlop( preFlop );
		this.flop = new Flop( flop );
		this.summary = new Summary( summary );
		this.stage = "summary";
		this.howManyPlayerInGame = this.flop.howManyPlayersInGame;
	}
	
	public HandHistory( String pokerRoom, long handNumber, GregorianCalendar time, String tableName, int maxSeatTable,  ArrayList<SeatNumberPlayer> listSeatNumberToPlayer,
			Player button, ArrayList<Player> smallBlind, ArrayList<Player> bigBlind, PreFlop preFlop, Flop flop, Turn turn )
	{
		this.pokerRoom = pokerRoom;
		this.handNumber = handNumber;
		this.time = new GregorianCalendar( time.YEAR, time.MONTH, time.DATE, time.HOUR, time.MINUTE, time.SECOND );
		this.tableName = tableName;
		this.maxSeatAtTable = maxSeatTable;
		this.listSeatNumberToPlayer = new ArrayList<SeatNumberPlayer>( listSeatNumberToPlayer );
		for ( int i = 0; i < this.listSeatNumberToPlayer.size(); i++ )
			this.allPlayers.add( new Player( this.listSeatNumberToPlayer.get(i).player ) );
		this.button = new Player( button );
		this.smallBlindP = new ArrayList<Player>( smallBlind );
		this.bigBlindP = new ArrayList<Player>( bigBlind );
		this.pot = new Pot( SB + BB + preFlop.pot.money + flop.pot.money + turn.pot.money );
		this.preFlop = new PreFlop( preFlop );
		this.flop = new Flop( flop );
		this.turn = new Turn( turn );
		this.stage = "turn";
		this.howManyPlayerInGame = this.turn.howManyPlayersInGame;
	}
	
	public HandHistory( String pokerRoom, long handNumber, GregorianCalendar time, String tableName, int maxSeatTable,  ArrayList<SeatNumberPlayer> listSeatNumberToPlayer,
			Player button, ArrayList<Player> smallBlind, ArrayList<Player> bigBlind, PreFlop preFlop, Flop flop, Turn turn, Summary summary )
	{
		this.pokerRoom = pokerRoom;
		this.handNumber = handNumber;
		this.time = new GregorianCalendar( time.YEAR, time.MONTH, time.DATE, time.HOUR, time.MINUTE, time.SECOND );
		this.tableName = tableName;
		this.maxSeatAtTable = maxSeatTable;
		this.listSeatNumberToPlayer = new ArrayList<SeatNumberPlayer>( listSeatNumberToPlayer );
		for ( int i = 0; i < this.listSeatNumberToPlayer.size(); i++ )
			this.allPlayers.add( new Player( this.listSeatNumberToPlayer.get(i).player ) );
		this.button = new Player( button );
		this.smallBlindP = new ArrayList<Player>( smallBlind );
		this.bigBlindP = new ArrayList<Player>( bigBlind );
		this.pot = new Pot( SB + BB + preFlop.pot.money + flop.pot.money + turn.pot.money );
		this.preFlop = new PreFlop( preFlop );
		this.flop = new Flop( flop );
		this.turn = new Turn( turn );
		this.summary = new Summary( summary );
		this.stage = "summary";
		this.howManyPlayerInGame = this.turn.howManyPlayersInGame;
	}
	
	public HandHistory( String pokerRoom, long handNumber, GregorianCalendar time, String tableName, int maxSeatTable, ArrayList<SeatNumberPlayer> listSeatNumberToPlayer,
			Player button, ArrayList<Player> smallBlind, ArrayList<Player> bigBlind, PreFlop preFlop, Flop flop, Turn turn, River river )
	{
		this.pokerRoom = pokerRoom;
		this.handNumber = handNumber;
		this.time = new GregorianCalendar( time.YEAR, time.MONTH, time.DATE, time.HOUR, time.MINUTE, time.SECOND );
		this.tableName = tableName;
		this.maxSeatAtTable = maxSeatTable;
		this.listSeatNumberToPlayer = new ArrayList<SeatNumberPlayer>( listSeatNumberToPlayer );
		for ( int i = 0; i < this.listSeatNumberToPlayer.size(); i++ )
			this.allPlayers.add( new Player( this.listSeatNumberToPlayer.get(i).player ) );
		this.button = new Player( button );
		this.smallBlindP = new ArrayList<Player>( smallBlind );
		this.bigBlindP = new ArrayList<Player>( bigBlind );
		this.pot = new Pot( SB + BB + preFlop.pot.money + flop.pot.money + turn.pot.money + river.pot.money );
		this.preFlop = new PreFlop( preFlop );
		this.flop = new Flop( flop );
		this.turn = new Turn( turn );
		this.river = new River( river );
		this.stage = "river";
		this.howManyPlayerInGame = this.river.howManyPlayersInGame;
	}
	
	public HandHistory( String pokerRoom, long handNumber, GregorianCalendar time, String tableName, int maxSeatTable, ArrayList<SeatNumberPlayer> listSeatNumberToPlayer,
			Player button, ArrayList<Player> smallBlind, ArrayList<Player> bigBlind, PreFlop preFlop, Flop flop, Turn turn, River river, Summary summary )
	{
		this.pokerRoom = pokerRoom;
		this.handNumber = handNumber;
		this.time = new GregorianCalendar( time.YEAR, time.MONTH, time.DATE, time.HOUR, time.MINUTE, time.SECOND );
		this.tableName = tableName;
		this.maxSeatAtTable = maxSeatTable;
		this.listSeatNumberToPlayer = new ArrayList<SeatNumberPlayer>( listSeatNumberToPlayer );
		for ( int i = 0; i < this.listSeatNumberToPlayer.size(); i++ )
			this.allPlayers.add( new Player( this.listSeatNumberToPlayer.get(i).player ) );
		this.button = new Player( button );
		this.smallBlindP = new ArrayList<Player>( smallBlind );
		this.bigBlindP = new ArrayList<Player>( bigBlind );
		this.pot = new Pot( SB + BB + preFlop.pot.money + flop.pot.money + turn.pot.money + river.pot.money );
		this.preFlop = new PreFlop( preFlop );
		this.flop = new Flop( flop );
		this.turn = new Turn( turn );
		this.river = new River( river );
		this.summary = new Summary( summary );
		this.stage = "summary";
		this.howManyPlayerInGame = this.river.howManyPlayersInGame;
	}
	
	public HandHistory( String pokerRoom, long handNumber, GregorianCalendar time, String tableName, int maxSeatTable, ArrayList<SeatNumberPlayer> listSeatNumberToPlayer,
			Player button, ArrayList<Player> smallBlind, ArrayList<Player> bigBlind, PreFlop preFlop, Flop flop, Turn turn, River river, ShowDown showDown, Summary summary )
	{
		this.pokerRoom = pokerRoom;
		this.handNumber = handNumber;
		this.time = new GregorianCalendar( time.YEAR, time.MONTH, time.DATE, time.HOUR, time.MINUTE, time.SECOND );
		this.tableName = tableName;
		this.maxSeatAtTable = maxSeatTable;
		this.listSeatNumberToPlayer = new ArrayList<SeatNumberPlayer>( listSeatNumberToPlayer );
		for ( int i = 0; i < this.listSeatNumberToPlayer.size(); i++ )
			this.allPlayers.add( new Player( this.listSeatNumberToPlayer.get(i).player ) );
		this.button = new Player( button );
		this.smallBlindP = new ArrayList<Player>( smallBlind );
		this.bigBlindP = new ArrayList<Player>( bigBlind );
		this.pot = new Pot( SB + BB + preFlop.pot.money + flop.pot.money + turn.pot.money + river.pot.money );
		this.preFlop = new PreFlop( preFlop );
		this.flop = new Flop( flop );
		this.turn = new Turn( turn );
		this.river = new River( river );
		this.showDown = new ShowDown( showDown );
		this.summary = new Summary( summary );
		this.stage = "summary";
		this.howManyPlayerInGame = this.river.howManyPlayersInGame;
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
					if ( ! this.river.isEmpty() ) {
						s += String.format( "%n%nriver-phase:%n" + this.river );
						if ( ! this.showDown.isEmpty() )
							s += String.format( "%n%nshow down:%n" + this.showDown );
					}
				}
			} if ( this.summary != null )
				s += String.format( "%nsummary:%n" + this.summary );
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
	
}
