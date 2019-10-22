package parser;

import cardBasics.Card;
import cardBasics.CardList;
import handHistory.HandHistory;
import handHistory.HandHistory.Limit;
import handHistory.SeatNumberPlayer;
import handHistory.PlayerAction;
import handHistory.PlayerActionList;
import handHistory.HandHistory.GameType;
import gameBasics.GameState;
import gameBasics.Player;
import gameBasics.SeatPosition;
import gameBasics.Action;
import gameBasics.Pot;
import other.Tools;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * @deprecated Because handHistory.HandHistory.playerStatesOut is not going to be determined!
 */
public class ParserPokerStars

{
	
	/**
	 * @deprecated Because handHistory.HandHistory.playerStatesOut will not determined!
	 * 
	 * Returns the hand history of a .txt-file which is the hand history written from PokerStars.
	 * The differential to the method parserPS(file) is that here you can passing the .txt-file in which PokerStars
	 * the whole time writes in and the parser will just parse the last hand history of the .txt-file in the object hand history.
	 * parserPS(file) just works correctly when you passing only the necessary part of the hand history, so only the
	 * actual hand history.
	 * 
	 * @param f .txt-file with the hand history
	 * @return a .txt-file parsed into the object hand history
	 */
	public static  HandHistory parserMainPS( File f )
	{
		File heapParserPokerStars = new File( "c://pokerBot//bot_v1_0_0//heapParserPS.txt" );		// This file does not exist in  bot version 1.0.0!! Because the bot does not
		String[] allLines = Tools.allLines( f );													// play at PokerStars!
		
		try {
		FileWriter heapW = new FileWriter( heapParserPokerStars, false );
		
		int hhStart = 0;
		int hhEnd = 0;
		for ( int i = 0; i < allLines.length; i++ )
		{
			String s = allLines[i];
			if( s.startsWith( "PokerStars" ) )
				hhStart = i;
			else if ( s.isEmpty() )
				hhEnd = i;
		}
		
		for ( int i = hhStart; i < hhEnd; i++ )
			heapW.write( String.format(allLines[i] + "%n") );
		
		heapW.flush();
		heapW.close();
		
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		
		return parserPS( heapParserPokerStars );
	}
	
	/**
	 * @deprecated Because handHistory.HandHistory.playerStatesOut will not determined!
	 * 
	 * Returns the hand history of a .txt-file which is the from PokerStars written hand history.
	 * The method just works correctly when you passing only the necessary part of the hand history,
	 * so the actual hand history.
	 * 
	 * @param file .txt-file with just the to parsing hand history
	 * @return a .txt-file parsed into the object hand history
	 */
	public static HandHistory parserPS( File file )
	{
		HandHistory handHistory = new HandHistory();
		
		if ( ( ! file.exists() ) ) {
			System.err.println( "This file does not exist. File: " + file.toString() + " (class ParserPokerStars, method parserPS" );
			return handHistory;
		}
		
		
		
		
		// in which line the individual phases starts 
		
		String[] allLines = Tools.allLines( file );
		
		int linePreFlop = 0;											// In this line starts the pre-flop-phase.
		for ( int i = 3; i < allLines.length; i++ )
			if ( allLines[i].equalsIgnoreCase( "*** HOLE CARDS ***" ) )
			{
				linePreFlop = i;
				break;
			}
		
		int lineFlop = 0; 												// In this line starts the flop-phase.
		for ( int i = linePreFlop; i < allLines.length; i++ )
			if ( allLines[i].matches( "\\*\\*\\* FLOP \\*\\*\\* \\[.*\\]" )   )
			{
				lineFlop = i;
				break;
			}
		
		int lineTurn = 0;											// In this line starts the turn-phase.
		int lineRiver = 0;											// In this line starts the river-phase.
		int lineShowDown = 0;										// In this line starts the show-down-phase.
		if ( lineFlop > 0 )
		{
			for ( int i = lineFlop; i < allLines.length; i++ )
				if ( allLines[i].matches( "\\*\\*\\* TURN \\*\\*\\* \\[.*\\] \\[.*\\]" ) )
				{
					lineTurn = i;
					break;
				}
			
			if ( lineTurn > 0 )
			{
				for ( int i = lineTurn; i < allLines.length; i++ )
					if ( allLines[i].matches( "\\*\\*\\* RIVER \\*\\*\\* \\[.*\\] \\[.*\\]" ) )
					{
						lineRiver = i;
						break;
					}
				
				if ( lineRiver > 0 )
					for ( int i = lineRiver; i < allLines.length; i++ )
						if ( allLines[i].equalsIgnoreCase( "*** SHOW DOWN ***" )  )
						{
							lineShowDown = i;
							break;
						}
			}
		}
		
		int lineSummary = 0;										// In this line starts the summary-phase.
		for ( int i = linePreFlop; i < allLines.length; i++ )
			if ( allLines[i].equalsIgnoreCase( "*** SUMMARY ***" ) )
			{
				lineSummary = i;
				break;
			}
		
		
		
		
		// in which stage the game is / handHistory.stage
		
		handHistory.state = GameState.PRE_FLOP;
		
		if ( lineSummary > 0 )
			handHistory.state = GameState.SUMMARY;
		else if ( lineFlop > 0 ) {
			handHistory.state = GameState.FLOP;
			if ( lineTurn > 0 ) {
				handHistory.state = GameState.TURN;
				if ( lineRiver > 0 ) {
					handHistory.state = GameState.RIVER;
					if ( lineShowDown > 0 )
						handHistory.state = GameState.SHOW_DOWN;
		}	}	}
		
		
		
		
		// first line
		
		handHistory.pokerRoom = "PokerStars";																// handHistory.pokerRoom
		
		String firstLine = allLines[ 0 ];
		String[] firstLineSplitted = firstLine.split( "\\s+" );
		
		String handNumberString = "";
		GameType gameType = null;
		int stringBeforeLimit = 0;
		String SBAndBB = "";
		for ( int i = 0; i < firstLineSplitted.length; i++ )
		{
			String s = firstLineSplitted[ i ];
			if ( s.matches( "#\\d*\\p{Punct}" ) )				// handNumber
				handNumberString = s;
			else if ( s.equalsIgnoreCase( "Hold'em" ) )			// gameType Hold'em
			{
				gameType = GameType.HOLD_EM;
				stringBeforeLimit = i;
			}
			else if ( s.matches( "[(].*" ) )					// SB and BB
				SBAndBB = s;
		}
		
		if ( gameType == null )
			throw new RuntimeException("The game type was not found!");
		
		long handNumber = Long.parseLong( handNumberString.substring(1, handNumberString.length()-1) );
		handHistory.handNumber = handNumber;																// handHistory.handNumber
		handHistory.gameType = gameType;																	// handHistory.gameType
		
		String[] SBAndBBSplitted = SBAndBB.split( "[/]" );
		double sb = Double.parseDouble( SBAndBBSplitted[0].substring(1) );
		double bb = Double.parseDouble( SBAndBBSplitted[1].substring(0, SBAndBBSplitted[1].length()-1) );
		
		handHistory.pot = new Pot ( sb + bb );																// handHistory.pot
		handHistory.SB = sb;																				// handHistory.SB
		handHistory.BB = bb;																				// handHistory.BB
		
		Limit limit = null;
		if ( firstLineSplitted[ stringBeforeLimit+1 ].equalsIgnoreCase( "No" ) )
		{
			if ( firstLineSplitted[ stringBeforeLimit+2 ].equalsIgnoreCase( "Limit" ) )
				limit = Limit.NO_LIMIT;
		}
		else if ( firstLineSplitted[ stringBeforeLimit+1 ].equalsIgnoreCase( "Fixed" ) )					// I do not know how poker stars handles the case if this is a fixed limit game.
			if ( firstLineSplitted[ stringBeforeLimit+2 ].equalsIgnoreCase( "Limit" ) )
				limit = Limit.FIXED_LIMIT;
		if ( limit == null )
			throw new RuntimeException("The limit was not found!");
		handHistory.limit = limit;																			// handHisotry.limit
		
		String jT = firstLine.split( "-" )[ 1 ].substring( 1 );				// jT = just time
		int year = Integer.parseInt( jT.substring(0, 4) );
		int month = Integer.parseInt( jT.substring(5, 7) );
		int day = Integer.parseInt( jT.substring( 8, 10) );
		int hour = Integer.parseInt( jT.substring(11, 13) );
		int minute = Integer.parseInt( jT.substring(14, 16) );
		int second = Integer.parseInt( jT.substring(17, 19) );
		handHistory.time = new GregorianCalendar( year, month, day, hour, minute, second );
		TimeZone TZ = TimeZone.getTimeZone( "ET" );
		handHistory.time.setTimeZone( TZ );
		
		// second line
		String secondLine = allLines[ 1 ];
		String[] secondLineSplitted = secondLine.split( "\\s+" );
		
		String maxSeat2 = "";
		int seatNumberButton = 0;
		for ( int i = 0; i < secondLineSplitted.length; i++ )
		{
			String s = secondLineSplitted[ i ];
			if ( s.matches( "\\d-max" ) )
				maxSeat2 = s;
			else if ( s.matches( "#\\d" ) )
				seatNumberButton = Integer.parseInt( s.substring( 1, 2 ) );					// Variable seatNumberButton is important for getting the player who is the button.
			
		}
		int maxSeat = Integer.parseInt( maxSeat2.substring( 0, 1 ) );
		handHistory.maxSeatAtTable = maxSeat;																// handHistory.maxSeatOnTable		
		
		String[] secondLineSplitted2 = secondLine.split( "'" );
		handHistory.tableName = secondLineSplitted2[ 1 ];													// handHistory.tableName
		
		
		
		
		// third line until the posts of the blinds. This part is for the listSeatNumberToPlayer and allPlayers.
		
		int untilItIsAboutSeats = 2;
		
		for ( int i = 2;  i < handHistory.maxSeatAtTable+2; i++ )
		{
			String[] sA = allLines[ i ].split( "\\s+" );
			if ( sA[0].equalsIgnoreCase( "Seat" ) )
				++untilItIsAboutSeats;
		}
		
		int[] possibleSeatNumbers = new int[ untilItIsAboutSeats-2 ];						// determining of the possible seat numbers
		for ( int i = 2; i < untilItIsAboutSeats; i++ )
			possibleSeatNumbers[ i-2 ] = Character.getNumericValue( allLines[i].charAt(5) );
		
		ArrayList<SeatNumberPlayer> listSeatNumberToPlayer = new ArrayList<SeatNumberPlayer>();
		ArrayList<Player> allPlayers = new ArrayList<Player>();
		ArrayList<String> allPlayerNames = new ArrayList<String>();
		for ( int i = 2; i < untilItIsAboutSeats; i++ )
		{
			String s = allLines[ i ];
			int seatNumber = Character.getNumericValue( s.charAt(5) );
			String playerName = s.replaceFirst("Seat \\d: ", "").replaceFirst(" [(].*[)]", "");
			playerName = playerName.substring( 0, playerName.length()-1 );
			double playerMoney = 0;
			String[] splitted = s.split( "\\s+" );
			
			for ( String a : splitted )
				if ( a.matches( "[(]\\d+[.]?\\d*" ) )
					{ playerMoney = Double.parseDouble( a.substring( 1, a.length() ) ); break; }
			
			SeatPosition seatBU = new SeatPosition( seatNumber, possibleSeatNumbers, seatNumberButton );
			Player player = new Player( playerName, seatBU, seatNumber, playerMoney );
			listSeatNumberToPlayer.add( new SeatNumberPlayer( seatNumber, player ) );
			allPlayers.add( player );
			allPlayerNames.add( playerName );
		}
		
		handHistory.allPlayers = allPlayers;															// handHistory.allPlayers
		handHistory.listSeatNumberToPlayer = listSeatNumberToPlayer;									// handHistory.listSeatNumbersToPlayer
		handHistory.numberPlayersAtTable = allPlayers.size();											// handHistory.numberPlayersAtTable
		
		int maxLenPlyNam = 0;			// the longest length of all player names
		for ( int i = 0; i < allPlayerNames.size(); i++ )
			maxLenPlyNam = (maxLenPlyNam > allPlayerNames.get(i).split("\\s+").length) ? maxLenPlyNam : allPlayerNames.get(i).split("\\s+").length;
		
		
		
		
		// the button, small and big blind
		
		Player button = new Player();
		ArrayList<Player> sB = new ArrayList<Player>();
		ArrayList<Player> bB = new ArrayList<Player>();
		
		for ( int i = untilItIsAboutSeats; i < linePreFlop; i++ )			// determining the small and big blind
		{
			String b = allLines[ i ];
			if ( b.contains( "posts small blind" ) )
			{
				int untilName = b.indexOf( "posts" )-2;
				String pName = b.substring( 0, untilName );
				int iOfS = indexOf( allPlayers, getPlayer( allPlayers, pName ) );
				allPlayers.get( iOfS ).seatBehindBU.positionNamed = "smallBlind";
				allPlayers.get( iOfS ).seatBehindBU.positionRange = "smallBlind";
				sB.add( getPlayer(allPlayers, pName) );
				continue;
			}
			if ( b.contains( "posts big blind" ) )
			{
				int untilName = b.indexOf( "posts" )-2;
				String pName = b.substring( 0, untilName );
				int iOfB = indexOf( allPlayers, getPlayer( allPlayers, pName) );
				allPlayers.get( iOfB ).seatBehindBU.positionNamed = "bigBlind";
				allPlayers.get( iOfB ).seatBehindBU.positionRange = "bigBlind";
				bB.add( getPlayer(allPlayers, pName) );
				continue;
			}
		}
		for ( int i = 0; i < handHistory.allPlayers.size(); i++ )		// determining the button
			if ( handHistory.allPlayers.get(i).seatBehindBU.positionNamed.equals( "button" ) )
				button.set( handHistory.allPlayers.get(i) );
		
		handHistory.button = button;																	// handHistory.button
		handHistory.smallBlindP = sB;																	// handHistory.sB
		handHistory.bigBlindP = bB;																		// handHistory.bB
		
		
		
		
		// pre-flop-phase
		
		String gettingStartCards = allLines[ linePreFlop+1 ];
		String gSC2 = "";
		if ( gettingStartCards.matches( "Dealt to.*\\[\\w\\w \\w\\w\\]" ) )
			gSC2 = gettingStartCards.split( "Dealt to " )[ 1 ];
		
		String[] gSC2Array = gSC2.split( "\\s+" );
		
		Card startCard1 = new Card( Card.stringToCard( gSC2Array[ gSC2Array.length-2 ].substring(1) ) );
		Card startCard2 = new Card( Card.stringToCard( gSC2Array[ gSC2Array.length-1 ].trim().substring(0, 2) ) );
		
		CardList startHand = new CardList( startCard1, startCard2 );
		handHistory.preFlop.startHand = startHand;													// handHistory.preFlop.startHand
		
		int untilFlopPhase = 0;
		if ( lineFlop > 0 )
			untilFlopPhase = lineFlop;
		else if ( lineSummary > 0 )
			untilFlopPhase = lineSummary;
		else
			untilFlopPhase = allLines.length;
		
		Action actPreFlop = new Action();
		PlayerActionList playActListPreFlop = new PlayerActionList();
		Pot potPreFlop = new Pot( 0 );
		
		loop:
		for ( int i = linePreFlop+2; i < untilFlopPhase; i++ )					// first two lines after beginning of the flop are the actions of the player listed
		{
			if ( allLines[i].isEmpty() )
				continue;
			
			String[] s3 = allLines[ i ].split( "\\s+" );
			
			if ( allLines[i].contains( "has timed out" ) )			// if someone has timed out, the parser will go to the next line because this does not matter
				continue;
			else if ( allLines[i].contains( "leaves the table" ) )		// if someone leaves the table, the parser will go to the next line because this does not matter
				continue;
			else if ( allLines[i].contains( "joins the table at" ) )	// if some joins the table, the parser will go to the next line because this does not matter
				continue;
			else if ( allLines[i].contains( "is capped" ) )
				continue;
			else if ( allLines[i].contains( "collected" ) )
				continue;
			for ( int j = 1; j < Math.min(maxLenPlyNam+1, s3.length); j++ )	// if someone said something, the parser will go to the next line because this does not matter
				if ( s3[j].equals( "said," ) )
					continue loop;
			
			if ( allLines[i].contains( "Uncalled bet" ) )
			{
				double money = Double.parseDouble( s3[ 1 + 1 ].substring( 1, s3[ 1 + 1 ].length()-1 ) );
				potPreFlop.removeM( money );
				continue;
			}
			
			String fullName = allLines[ i ].split( "[:]" )[ 0 ];
			int nameSplitted = fullName.split( "\\s+" ).length;			// the number of single words in the full name of the player
			
			String s4 = s3[ nameSplitted ].trim().substring( 0, s3[ nameSplitted ].trim().length() );
			int nameIndex = allPlayerNames.indexOf( fullName );
			
			if ( s4.equalsIgnoreCase( "folds" ) )										// determining the action the player did
			{
				actPreFlop.set( "fold", 0 );
				playActListPreFlop.add( new PlayerAction(allPlayers.get(nameIndex), actPreFlop) );
			}
			else if ( s4.equalsIgnoreCase( "checks" ) )
			{
				actPreFlop.set( "check", 0 );
				playActListPreFlop.add( new PlayerAction(allPlayers.get(nameIndex), actPreFlop) );
			}
			else if ( s4.equalsIgnoreCase( "calls" ) )
			{
				double money = Double.parseDouble( s3[ 1 + nameSplitted ].substring( 0 ) );
				actPreFlop.set( "call", money );
				playActListPreFlop.add( new PlayerAction(allPlayers.get(nameIndex), actPreFlop) );
				potPreFlop.addM( money );
			}
			else if ( s4.equalsIgnoreCase( "bets" ) )					// Attention to case "bet". This case is if somebody bets.
			{
				double money = Double.parseDouble( s3[ 1 + nameSplitted ].substring( 0 ) );
				actPreFlop.set( "bet", money );
				playActListPreFlop.add( new PlayerAction(allPlayers.get(nameIndex), actPreFlop) );
				potPreFlop.addM( money );
			}
			else if ( s4.equalsIgnoreCase( "raises" ) )
			{
				double money = Double.parseDouble( s3[ 1 + 2 + nameSplitted ].trim().substring( 0 ) );
				actPreFlop.set( "raise", money );
				playActListPreFlop.add( new PlayerAction(allPlayers.get(nameIndex), actPreFlop) );
				potPreFlop.addM( money );
			}
		}
		
		handHistory.preFlop.playerActionList = playActListPreFlop;										// handHistory.preFlop.playerActionList
		handHistory.preFlop.pot = potPreFlop;															// handHistory.preFlop.pot
		handHistory.pot.money += potPreFlop.money;														// handHistory.pot
		
		handHistory.preFlop.howManyPlayersInGame = handHistory.preFlop.howManyPlayersInGame(bB.size());	// handHistory.preFlop.howManyPlayersInGame
		handHistory.howManyPlayerInGame = handHistory.preFlop.howManyPlayersInGame;						// handHistory.howManyPlayersInGame
		handHistory.bettingRounds.add( handHistory.preFlop );											// handHistory.bettingRounds
		
		
		
		
		
		// flop-phase
		
		if ( lineFlop > 0 )
		{
			CardList boardFlop = new CardList();
			
			String sFlopLine = allLines[ lineFlop ];
			sFlopLine = sFlopLine.substring( 14, 22 );
			String[] sFlopLineA = sFlopLine.split( "\\s+" );
			for ( String s : sFlopLineA )
				boardFlop.add( new Card( Card.stringToCard( s ) ) );
			handHistory.flop.board = boardFlop;															// handHistory.flop.board
			handHistory.flop.flop = boardFlop;															// handHistory.flop.flop
			
			int untilTurnPhase = 0;
			if ( lineTurn > 0 )
				untilTurnPhase = lineTurn;
			else if ( lineSummary > 0 )
				untilTurnPhase = lineSummary;
			else
				untilTurnPhase = allLines.length;
			
			Action actFlop = new Action();
			PlayerActionList playActListFlop = new PlayerActionList();
			Pot potFlop = new Pot( 0 );
			
			loop:
			for ( int i = lineFlop+1; i < untilTurnPhase; i++ )					// the actions of the players are first listed one line after beginning of the flop
			{
				if ( allLines[i].isEmpty() )
					continue;
				
				String[] s3 = allLines[ i ].split( "\\s+" );
				
				if ( allLines[i].contains( "has timed out" ) )			// if someone has timed out, the parser will go to the next line because this does not matter
					continue;
				else if ( allLines[i].contains( "leaves the table" ) )		// if someone leaves the table, the parser will go to the next line because this does not matter
					continue;
				else if ( allLines[i].contains( "joins the table at" ) )	// if some joins the table, the parser will go to the next line because this does not matter
					continue;
				else if ( allLines[i].contains( "is capped" ) )
					continue;
				else if ( allLines[i].contains( "collected" ) )
					continue;
				for ( int j = 1; j < Math.min(maxLenPlyNam+1, s3.length); j++ )	// if someone said something, the parser will go to the next line because this does not matter
					if ( s3[j].equals( "said," ) )
						continue loop;
				
				if ( allLines[i].contains( "Uncalled bet" ) )
				{
					double money = Double.parseDouble( s3[ 1 + 1 ].substring( 1, s3[ 1 + 1 ].length()-1 ) );
					potFlop.removeM( money );
					continue;
				}
				
				String fullName = allLines[ i ].split( "[:]" )[ 0 ];
				int nameSplitted = fullName.split( "\\s+" ).length;			// the number of single words in the full name of the player
				
				String s4 = s3[ nameSplitted ].trim().substring( 0, s3[ nameSplitted ].trim().length() );
				int nameIndex = allPlayerNames.indexOf( fullName );
				
				if ( s4.equalsIgnoreCase( "folds" ) )										// determining the action the player did
				{
					actFlop.set( "fold", 0 );
					playActListFlop.add( new PlayerAction(allPlayers.get(nameIndex), actFlop) );
				}
				else if ( s4.equalsIgnoreCase( "checks" ) )
				{
					actFlop.set( "check", 0 );
					playActListFlop.add( new PlayerAction(allPlayers.get(nameIndex), actFlop) );
				}
				else if ( s4.equalsIgnoreCase( "calls" ) )
				{
					double money = Double.parseDouble( s3[ 1 + nameSplitted ].substring( 0 ) );
					actFlop.set( "call", money );
					playActListFlop.add( new PlayerAction(allPlayers.get(nameIndex), actFlop) );
					potFlop.addM( money );
				}
				else if ( s4.equalsIgnoreCase( "bets" ) )					// Attention to case "bet". This case is if somebody bets.
				{
					double money = Double.parseDouble( s3[ 1 + nameSplitted ].substring( 0 ) );
					actFlop.set( "bet", money );
					playActListFlop.add( new PlayerAction(allPlayers.get(nameIndex), actFlop) );
					potFlop.addM( money );
				}
				else if ( s4.equalsIgnoreCase( "raises" ) )
				{
					double money = Double.parseDouble( s3[ 1 + 2 + nameSplitted ].trim().substring( 0 ) );
					actFlop.set( "raises", money );
					playActListFlop.add( new PlayerAction(allPlayers.get(nameIndex), actFlop) );
					potFlop.addM( money );
				}
			}
			
			handHistory.flop.playerActionList = playActListFlop;								// handHistory.flop.playerActionList
			handHistory.flop.pot = potFlop;														// handHistory.flop.pot
			handHistory.pot.money += potFlop.money;												// handHistory.pot
			
			handHistory.flop.howManyPlayersInGame = handHistory.flop.howManyPlayersInGame( handHistory.preFlop.howManyPlayersInGame );		// handHistory.flop.howManyPlayersInGame
			handHistory.howManyPlayerInGame = handHistory.flop.howManyPlayersInGame;		// handHistory.howManyPlayersInGame
			handHistory.bettingRounds.add( handHistory.flop );									// handHistory.bettingRounds
		}
		
		
		
		
		// turn-phase
		
		if ( lineTurn > 0 )
		{			
			Card turn = new Card( Card.stringToCard( allLines[ lineTurn ].substring( 25, 27 ) ) );
			CardList boardTurn = new CardList( handHistory.flop.board );
			boardTurn.add( turn );
			handHistory.turn.turn = turn;														// handHistory.turn.turn
			handHistory.turn.restOpenCards = handHistory.flop.board;							// handHistory.turn.restOpenCards
			handHistory.turn.board = boardTurn;													// handHistory.turn.board
			
			int untilRiverPhase = 0;
			if ( lineRiver > 0 )
				untilRiverPhase = lineRiver;
			else if ( lineSummary > 0 )
				untilRiverPhase = lineSummary;
			else
				untilRiverPhase = allLines.length;
			
			Action actTurn = new Action();
			PlayerActionList playActListTurn = new PlayerActionList();
			Pot potTurn = new Pot( 0 );
			
			loop:
			for ( int i = lineTurn+1; i < untilRiverPhase; i++ )					// the actions of the players are first listed one line after beginning of the turn
			{
				if ( allLines[i].isEmpty() )
					continue;
				
				String[] s3 = allLines[ i ].split( "\\s+" );
				
				if ( allLines[i].contains( "has timed out" ) )			// if someone has timed out, the parser will go to the next line because this does not matter
					continue;
				else if ( allLines[i].contains( "leaves the table" ) )		// if someone leaves the table, the parser will go to the next line because this does not matter
					continue;
				else if ( allLines[i].contains( "joins the table at" ) )	// if some joins the table, the parser will go to the next line because this does not matter
					continue;
				else if ( allLines[i].contains( "is capped" ) )
					continue;
				else if ( allLines[i].contains( "collected" ) )
					continue;
				for ( int j = 1; j < Math.min(maxLenPlyNam+1, s3.length); j++ )	// if someone said something, the parser will go to the next line because this does not matter
					if ( s3[j].equals( "said," ) )
						continue loop;
				
				if ( allLines[i].contains( "Uncalled bet" ) )
				{
					double money = Double.parseDouble( s3[ 1 + 1 ].substring( 1, s3[ 1 + 1 ].length()-1 ) );
					potTurn.removeM( money );
					continue;
				}
				
				String fullName = allLines[ i ].split( "[:]" )[ 0 ];
				int nameSplitted = fullName.split( "\\s+" ).length;			// the number of single words in the full name of the player
				
				String s4 = s3[ nameSplitted ].trim().substring( 0, s3[ nameSplitted ].trim().length() );
				int nameIndex = allPlayerNames.indexOf( fullName );
				
				if ( s4.equalsIgnoreCase( "folds" ) )										// determining the action the player did
				{
					actTurn.set( "fold", 0 );
					playActListTurn.add( new PlayerAction(allPlayers.get(nameIndex), actTurn) );
				}
				else if ( s4.equalsIgnoreCase( "checks" ) )
				{
					actTurn.set( "check", 0 );
					playActListTurn.add( new PlayerAction(allPlayers.get(nameIndex), actTurn) );
				}
				else if ( s4.equalsIgnoreCase( "calls" ) )
				{
					double money = Double.parseDouble( s3[ 1 + nameSplitted ].substring( 0 ) );
					actTurn.set( "call", money );
					playActListTurn.add( new PlayerAction(allPlayers.get(nameIndex), actTurn) );
					potTurn.addM( money );
				}
				else if ( s4.equalsIgnoreCase( "bets" ) )					// Attention to case "bet". This case is if somebody bets.
				{
					double money = Double.parseDouble( s3[ 1 + nameSplitted ].substring( 0 ) );
					actTurn.set( "bet", money );
					playActListTurn.add( new PlayerAction(allPlayers.get(nameIndex), actTurn) );
					potTurn.addM( money );
				}
				else if ( s4.equalsIgnoreCase( "raises" ) )
				{
					double money = Double.parseDouble( s3[ 1 + 2 + nameSplitted ].trim().substring( 0 ) );
					actTurn.set( "raises", money );
					playActListTurn.add( new PlayerAction(allPlayers.get(nameIndex), actTurn) );
					potTurn.addM( money );
				}
			}
			
			handHistory.turn.playerActionList = playActListTurn;						// handHistory.turn.playerActionList
			handHistory.turn.pot = potTurn;												// handHistory.turn.pot
			handHistory.pot.money += potTurn.money;										// handHistory.pot.money
			
			handHistory.turn.howManyPlayersInGame = handHistory.turn.howManyPlayersInGame( handHistory.flop.howManyPlayersInGame );		// handHistory.turn.howManyPlayersInGame
			handHistory.howManyPlayerInGame = handHistory.turn.howManyPlayersInGame;	// handHistory.howManyPlayersInGame
			handHistory.bettingRounds.add( handHistory.turn );						// handHistory.bettingRounds
		}
		
		
		
		
		// river-phase
		
		if ( lineRiver > 0 )
		{			
			Card river = new Card( Card.stringToCard( allLines[ lineRiver ].substring( 29, 31 ) ) );
			CardList boardRiver = new CardList( handHistory.turn.board );
			boardRiver.add( river );
			handHistory.river.river = river;
			handHistory.river.restOpenCards = handHistory.turn.board;
			handHistory.river.board = boardRiver;
			
			int untilShowDown = 0;
			if ( lineShowDown > 0 )
				untilShowDown = lineShowDown;
			else if ( lineSummary > 0 )
				untilShowDown = lineSummary;
			else
				untilShowDown = allLines.length;
			
			Action actRiver = new Action();
			PlayerActionList playActListRiver = new PlayerActionList();
			Pot potRiver = new Pot( 0 );
			
			loop:
			for ( int i = lineRiver+1; i < untilShowDown; i++ )					// the actions of the players are first listed one line after beginning of the river
			{
				if ( allLines[i].isEmpty() )
					continue;
				
				String[] s3 = allLines[ i ].split( "\\s+" );
				
				if ( allLines[i].contains( "has timed out" ) )			// if someone has timed out, the parser will go to the next line because this does not matter
					continue;
				else if ( allLines[i].contains( "leaves the table" ) )		// if someone leaves the table, the parser will go to the next line because this does not matter
					continue;
				else if ( allLines[i].contains( "joins the table at" ) )	// if some joins the table, the parser will go to the next line because this does not matter
					continue;
				else if ( allLines[i].contains( "is capped" ) )
					continue;
				else if ( allLines[i].contains( "collected" ) )
					continue;
				for ( int j = 1; j < Math.min(maxLenPlyNam+1, s3.length); j++ )	// if someone said something, the parser will go to the next line because this does not matter
					if ( s3[j].equals( "said," ) )
						continue loop;
				
				if ( allLines[i].contains( "Uncalled bet" ) )
				{
					double money = Double.parseDouble( s3[ 1 + 1 ].substring( 1, s3[ 1 + 1 ].length()-1 ) );
					potRiver.removeM( money );
					continue;
				}
				
				String fullName = allLines[ i ].split( "[:]" )[ 0 ];
				int nameSplitted = fullName.split( "\\s+" ).length;			// the number of single words in the full name of the player
				
				String s4 = s3[ nameSplitted ].trim().substring( 0, s3[ nameSplitted ].trim().length() );
				int nameIndex = allPlayerNames.indexOf( fullName );
				
				if ( s4.equalsIgnoreCase( "folds" ) )										// determining the action the player did
				{
					actRiver.set( "fold", 0 );
					playActListRiver.add( new PlayerAction(allPlayers.get(nameIndex), actRiver) );
				}
				else if ( s4.equalsIgnoreCase( "checks" ) )
				{
					actRiver.set( "check", 0 );
					playActListRiver.add( new PlayerAction(allPlayers.get(nameIndex), actRiver) );
				}
				else if ( s4.equalsIgnoreCase( "calls" ) )
				{
					double money = Double.parseDouble( s3[ 1 + nameSplitted ].substring( 0 ) );
					actRiver.set( "call", money );
					playActListRiver.add( new PlayerAction(allPlayers.get(nameIndex), actRiver) );
					potRiver.addM( money );
				}
				else if ( s4.equalsIgnoreCase( "bets" ) )					// Attention to case "bet". This case is if somebody bets.
				{
					double money = Double.parseDouble( s3[ 1 + nameSplitted ].substring( 0 ) );
					actRiver.set( "bet", money );
					playActListRiver.add( new PlayerAction(allPlayers.get(nameIndex), actRiver) );
					potRiver.addM( money );
				}
				else if ( s4.equalsIgnoreCase( "raises" ) )
				{
					double money = Double.parseDouble( s3[ 1 + 2 + nameSplitted ].trim().substring( 0 ) );
					actRiver.set( "raises", money );
					playActListRiver.add( new PlayerAction(allPlayers.get(nameIndex), actRiver) );
					potRiver.addM( money );
				}
			}
			
			handHistory.river.playerActionList = playActListRiver;				// handHistory.river.playerActionList
			handHistory.river.pot = potRiver;									// handHistory.river.pot
			handHistory.pot.money += potRiver.money;							// handHistory.pot.money
			
			handHistory.river.howManyPlayersInGame = handHistory.river.howManyPlayersBeforeInGame( handHistory.turn.howManyPlayersInGame );		// handHistory.river.howManyPlayersInGame
			handHistory.howManyPlayerInGame = handHistory.river.howManyPlayersInGame;		// handHistory.howManyPlayersInGame
			handHistory.bettingRounds.add( handHistory.river );					// handHistory.bettingRounds
		}
		
		
		
		
		// show-down-phase
		
		
		
		
		// summary-phase
		
		
		
		
		return handHistory;
	}
	
	/**
	 * Returns the player to the committed name of the player.
	 * 
	 * @param allPlayer an array with all players in the game
	 * @param playerName the name of the player
	 * @return the player to the name
	 */
	public static Player getPlayer( ArrayList<Player> allPlayer, String playerName )
	{
		Player p = new Player();
		for ( int i = 0; i < allPlayer.size(); i++ )
			if ( allPlayer.get(i).name.equals( playerName ) )
				p.set( allPlayer.get(i) );
		
		if( p.name.isEmpty() )
			System.err.println( "It was not possible to determine the player to the commited name of the player." );
		return p;
	}
	
	/**
	 * Returns the first index of the player p in the list allPlayers.
	 * If allPlayers does not contain Player p, the method will return -1.
	 * 
	 * @param allPlayers the array with all players of the game
	 * @param p the player
	 * @return the index of p in allPlayers
	 */
	public static int indexOf( ArrayList<Player> allPlayers, Player p )
	{
		int index = -1;
		for ( int i = 0; i < allPlayers.size(); i++ )
			if ( allPlayers.get(i).equals( p ) ) {
				index = i;
				break;
			}
		return index;
	}
	
}
