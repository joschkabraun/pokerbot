package parser;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import cardBasics.Card;
import cardBasics.CardList;
import other.Tools;
import gameBasics.Action;
import gameBasics.GameState;
import gameBasics.Player;
import gameBasics.Pot;
import gameBasics.SeatPosition;
import handHistory.HandHistory;
import handHistory.PlayerAction;
import handHistory.PlayerActionList;
import handHistory.SeatNumberPlayer;

public class ParserCreatorWinnerPoker4Tables

{
	
	/**
	 * Returns the HandHistory (as object HandHistory) of a .txt-file which is made by the CreatorWinnerPoker4Tables (CWP4T).
	 * This method just works for maxSeatOnTable = 9 and if there are four tables.
	 * 
	 * @param f .txt-file with the hand history
	 * @param gameType the gameType of the hand history, e.g. "Hold'Em"
	 * @param limit the limit of the game, e.g. "Fixed Limit", "No Limit" or "Pot Limit"
	 * @param maxSeatOnTable the maximal number of seats on the table of the hand history
	 * @param playYouName the name of the PlayerYou
	 * @param pictureSeats the pictures of the empty seats
	 * @param spaceSeats the space of the seats
	 * @return a .txt-file parser into a hand history-object
	 */
	public static HandHistory parserMainCWP( File f, String gameType, String limit, int maxSeatOnTable, String playYouName, BufferedImage[] pictureSeats,
			Rectangle[] spaceSeats ) throws IOException, AWTException
	{
		if ( maxSeatOnTable != 9 )
			throw new IllegalArgumentException( "The ParserCreatorWinnerPoker does not work for maxSeatOnTable != 9!" );
		
		File parserFile;
		
		if ( f.getAbsolutePath().equals( "c:\\pokerBot\\bot_v1_1_0\\hhTableLeftDown.txt" ) )
			parserFile = new File( "c://pokerBot//bot_v1_1_0//parserTableLeftDown.txt" );
		else if ( f.getAbsolutePath().equals( "c:\\pokerBot\\bot_v1_1_0\\hhTableLeftUp.txt" ) )
			parserFile = new File( "c://pokerBot//bot_v1_1_0//parserTableLeftUp.txt" );
		else if ( f.getAbsolutePath().equals( "c:\\pokerBot\\bot_v1_1_0\\hhTableRightUp.txt" ) )
			parserFile = new File( "c://pokerBot//bot_v1_1_0//parserTableRightUp.txt" );
		else if ( f.getAbsolutePath().equals( "c:\\pokerBot\\bot_v1_1_0\\hhTableRightDown.txt" ) )
			parserFile = new File( "c://pokerBot//bot_v1_1_0//parserTableRightDown.txt" );
		else
			throw new IllegalArgumentException( "The commited file for the hand history is false!" );
		
		String[] allLinesWithoutTrim = Tools.allLines( f );
		int length = allLinesWithoutTrim.length;
		String[] allLines = new String[ length ];
		for ( int i = 0; i < length; i++ )
			allLines[ i ] = allLinesWithoutTrim[ i ].trim();
		
		FileWriter heapW = new FileWriter( parserFile );
		
		int hhStart = 0;
		int hhEnd = allLines.length - 1;
		
		for (int i = allLines.length - 1; -1 < i; i--)
			if (allLines[i].matches("Geber: .+ ist der Geber"))
				{ hhStart = i; break; }
		
		for (int i = hhStart; i < hhEnd + 1; i++)
			heapW.write(String.format(allLines[i] + "%n"));

		heapW.flush();
		heapW.close();
		
		return parserCWP( parserFile, gameType, limit, maxSeatOnTable, playYouName, pictureSeats, spaceSeats );
	}
	
	/**
	 * Returns the HandHistory (as Object HandHistory) of a .txt-file which is made from the method
	 * parserMainCWP.
	 * 
	 * @param f .txt-file with the hand history
	 * @param gameType the gameType of the hand history, e.g. "Hold'Em"
	 * @param limit the limit of the game, e.g. "Fixed Limit", "No Limit" or "Pot Limit"
	 * @param maxSeatOnTable the maximal number of seats on the table of the hand history
	 * @param playYouName the name of the PlayerYou
	 * @param pictureSeats the pictures of the empty seats
	 * @param spaceSeats the space of the seats
	 * @return a .txt-file parser into a hand history-object
	 */
	public static HandHistory parserCWP( File f, String gameType, String limit, int maxSeatOnTable, String playYouName, BufferedImage[] pictureSeats,
			Rectangle[] spaceSeats )  throws AWTException
	{
		HandHistory handHistory = new HandHistory();
		
		String[] allLines = ParserCreatorWinnerPoker1Table.clearLines( ParserCreatorWinnerPoker1Table.clearCurrency( Tools.allLines( f ) ) );
		
		if ( allLines[ 0 ].equals("null") )
			throw new IllegalArgumentException( "The passed file f (" + f.getAbsolutePath() + ") is empty" );
		
		
		
		
		// in which line the individual phases starts
		
		int linePreFlop = 0;										// In this line starts the pre-flop-phase
		for ( int i = 3; i < allLines.length; i++ )
			if ( allLines[i].equals( "Geber: Karten werden gegeben" ) )
			{
				linePreFlop = i;
				break;
			}
		
		int lineFlop = 0;											// In this line starts the flop-phase
		for ( int i = linePreFlop; i < allLines.length; i++ )
			if ( allLines[i].matches( "Geber: Flop wird gegeben .+" ) )
			{
				lineFlop = i;
				break;
			}
		
		int lineTurn = 0;											// In this line starts the turn-phase
		int lineRiver = 0;											// In this line starts the river-phase
		int lineShowDown = 0;										// In this line starts the show-down-phase
		
		if ( lineFlop > 0 )
		{
			for ( int i = lineFlop; i < allLines.length; i++ )
				if ( allLines[i].matches( "Geber: Turnkarte wird gegeben .+" ) )
				{
					lineTurn = i;
					break;
				}
			
			if ( lineTurn > 0 )
			{
				for ( int i = lineTurn; i < allLines.length; i++ )
					if ( allLines[i].matches( "Geber: Riverkarte wird gegeben .+" ) )
					{
						lineRiver = i;
						break;
					}
				
				if ( lineRiver > 0 )
					for ( int i = lineRiver; i < allLines.length; i++ )
						if ( allLines[i].matches( "Geber: .+ zeigt .+" ) )
						{
							lineShowDown = i;
							break;
						}
			}
		}
		
		
		
		
		// in which stage the game is / handHistory.stage
		
		handHistory.state = GameState.PRE_FLOP;
		
		if ( lineFlop > 0 ) {
			handHistory.state = GameState.FLOP;
			if ( lineTurn > 0 ) {
				handHistory.state = GameState.TURN;
				if ( lineRiver > 0 ) {
					handHistory.state = GameState.RIVER;
					if ( lineShowDown > 0 )
						handHistory.state = GameState.SHOW_DOWN;
		}	}	}
		
		
		
		
		// general
		
		handHistory.pokerRoom = "WinnerPoker";																	// handHistory.pokerRoom
		
		handHistory.handNumber = 0L;																			// handHistory.handNumber
		
		handHistory.gameType = gameType;																		// handHistory.gameType
		
		handHistory.limit = limit;																				// handHistory.limit
		
		handHistory.time = new GregorianCalendar();																// handHistory.time
		
		handHistory.maxSeatAtTable = maxSeatOnTable;															// handHistory.maxSeatOnTable
		
		handHistory.tableName = "";																				// handHistory.tableName
		
		
		
		
		// determining: listSeatNumberToPlayer and allPlayers
		
		ArrayList<SeatNumberPlayer> listSeatNumberToPlayer = new ArrayList<SeatNumberPlayer>();
		ArrayList<Player> allPlayers = new ArrayList<Player>();
		ArrayList<String> allPlayerNames = new ArrayList<String>();
		
		if ( lineFlop > 0 )
		{
			String[] linesUntilFlop = new String[ lineFlop ];
			for (int j = 0; j < lineFlop; j++)
				linesUntilFlop[ j ] = allLines[j];
			
			int untilItIsNecessaryForGettingAllPlayerIncl = untilItIsNecessaryForAllPlayers(linesUntilFlop);
			
			String[] necessaryLines = new String[ untilItIsNecessaryForGettingAllPlayerIncl + 1 ];
			for ( int j = 0; j < necessaryLines.length; j++ )
				necessaryLines[ j ] = allLines[ j ];
			
			int howOftenSB = 0;
			int howOftenBB = 0;
						
			for ( int i = 0; i < untilItIsNecessaryForGettingAllPlayerIncl + 1; i++ )
			{
				String s = allLines[ i ].split( "Geber: " )[ 1 ];
				
				double playerMoney = 0.0;
				int seatNumber = allPlayers.size() + 1;
				String playerName = "";
				
				if ( s.matches( ".+ setzt Small Blind .+" ) )
					if ( howOftenSB == 0 )
						{ playerName = s.split( " setzt Small Blind .+" )[ 0 ].trim(); ++howOftenSB; }
					else
						continue;
				else if ( s.matches( ".+ setzt Big Blind .+" ) )
					if ( howOftenBB == 0 )
						{ playerName = s.split( " setzt Big Blind .+" )[ 0 ].trim(); ++howOftenBB; }
					else
						continue;
				else if ( s.matches( ".+ schiebt" ) )
					playerName = s.split( " schiebt" )[ 0 ].trim();
				else if ( s.matches( ".+ erhöht auf .+" ) )
					playerName = s.split( " erhöht auf .+" )[ 0 ].trim();
				else if ( s.matches( ".+ geht mit bei .+" ) )
					playerName = s.split( " geht mit bei .+" )[ 0 ].trim();
				else if ( s.matches( ".+ setzt .+" ) )
					playerName = s.split( " setzt .+" )[ 0 ].trim();
				else if ( s.matches( ".+ passt" ) )
					playerName = s.split( " passt" )[ 0 ].trim();
				
				if ( ! playerName.equals( "" ) && indexOf(allPlayers, playerName) == -1 )
				{
					SeatPosition seatBU = new SeatPosition( seatNumber, howManyPlayersContained(linesUntilFlop) );
					Player player = new Player( playerName, seatBU, seatNumber, playerMoney );
					listSeatNumberToPlayer.add( new SeatNumberPlayer( seatNumber, player ) );
					allPlayers.add( player );
					allPlayerNames.add( playerName );
		}	} handHistory.numberPlayersAtTable = getPlayerNames(necessaryLines).size(); }						// handHistory.numberPlayersAtTable

		else
		{
			int howOftenSB = 0;
			int howOftenBB = 0;
			
			if ( playerTwiceContained( allLines ) )		// you know every player because all had their turn
			{
				int numberPlayersAtTable = howManyPlayersContained( allLines );
				for ( int i = 0; i < untilItIsNecessaryForAllPlayers(allLines) + 1; i++ )
				{
					String s = allLines[ i ].split( "Geber: " )[ 1 ];
					
					double playerMoney = 0.0;
					int seatNumber = allPlayers.size() + 1;
					String playerName = "";
					
					if ( s.matches( ".+ setzt Small Blind .+" ) )
						if ( howOftenSB == 0 )
							{ playerName = s.split( " setzt Small Blind .+" )[ 0 ].trim(); ++howOftenSB; }
						else
							continue;
					else if ( s.matches( ".+ setzt Big Blind .+" ) )
						if ( howOftenBB == 0 )
							{ playerName = s.split( " setzt Big Blind .+" )[ 0 ].trim(); ++howOftenBB; }
						else
							continue;
					else if ( s.matches( ".+ schiebt" ) )
						playerName = s.split( " schiebt" )[ 0 ].trim();
					else if ( s.matches( ".+ erhöht auf .+" ) )
						playerName = s.split( " erhöht auf .+" )[ 0 ].trim();
					else if ( s.matches( ".+ geht mit bei .+" ) )
						playerName = s.split( " geht mit bei .+" )[ 0 ].trim();
					else if ( s.matches( ".+ setzt .+" ) )
						playerName = s.split( " setzt .+" )[ 0 ].trim();
					else if ( s.matches( ".+ passt" ) )
						playerName = s.split( " passt" )[ 0 ].trim();
					
					
					if ( ! playerName.equals( "" ) )
					{
						SeatPosition seatBU = new SeatPosition( seatNumber, numberPlayersAtTable );
						Player player = new Player( playerName, seatBU, seatNumber, playerMoney );
						listSeatNumberToPlayer.add( new SeatNumberPlayer( seatNumber, player ) );
						allPlayers.add( player );
						allPlayerNames.add( playerName );
					}	} handHistory.numberPlayersAtTable = getPlayerNames(allLines).size(); }			// handHistory.numberPlayersAtTable
			
			else					// there is at least one player who did not do his/her action in this game
			{
				int numberPlayersAtTable = howManyPlayersAtTable( pictureSeats, spaceSeats );
				handHistory.numberPlayersAtTable = numberPlayersAtTable;								// handHistory.numberPlayersAtTable
				
				for ( int i = 0; i < allLines.length; i++ )
				{
					String s = allLines[ i ].split( "Geber: " )[ 1 ];
					
					double playerMoney = 0.0;
					int seatNumber = allPlayers.size() + 1;
					String playerName = "";
					
					if ( s.matches( ".+ setzt Small Blind .+" ) )
						if ( howOftenSB == 0 )
							{ playerName = s.split( " setzt Small Blind .+" )[ 0 ].trim(); ++howOftenSB; }
						else
							continue;
					else if ( s.matches( ".+ setzt Big Blind .+" ) )
						if ( howOftenBB == 0 )
							{ playerName = s.split( " setzt Big Blind .+" )[ 0 ].trim(); ++howOftenBB; }
						else
							continue;
					else if ( s.matches( ".+ setzt Big Blind .+ und Dead Small Blind .+" ) )
						playerName = s.split( " setzt Big Blind .+ und Dead Small Blind .+" )[ 0 ].trim();
					else if ( s.matches( ".+ schiebt" ) )
						playerName = s.split( " schiebt" )[ 0 ].trim();
					else if ( s.matches( ".+ erhöht auf .+" ) )
						playerName = s.split( " erhöht auf .+" )[ 0 ].trim();
					else if ( s.matches( ".+ geht mit bei .+" ) )
						playerName = s.split( " geht mit bei .+" )[ 0 ].trim();
					else if ( s.matches( ".+ setzt .+" )  )
						playerName = s.split( " setzt .+" )[ 0 ].trim();
					else if ( s.matches( ".+ passt" ) )
						playerName = s.split( " passt" )[ 0 ].trim();
					
					if ( ! playerName.equals( "" ) )
					{
						SeatPosition seatBU = new SeatPosition( seatNumber, numberPlayersAtTable );
						Player player = new Player( playerName, seatBU, seatNumber, playerMoney );
						listSeatNumberToPlayer.add( new SeatNumberPlayer( seatNumber, player ) );
						allPlayers.add( player );
						allPlayerNames.add( playerName );
				}	}
				
				if ( (indexOf(allPlayers, playYouName) == -1) && (playYouName != "") ) {	// this if-statement is for the case if PlayerYou was not his/her
					int seatNumber = allPlayers.size() + 1;									// turn. The HH should have a PlayerYou for the strategy
					SeatPosition seatBU = new SeatPosition( seatNumber, howManyPlayersAtTable( pictureSeats, spaceSeats ) );
					Player player = new Player( playYouName, seatBU, seatNumber, 0.0 );
					listSeatNumberToPlayer.add( new SeatNumberPlayer( seatNumber, player ) );
					allPlayers.add( player );
					allPlayerNames.add( playYouName );
		}	}	}
		
		handHistory.allPlayers = allPlayers;														// handHistory.allPlayers
		handHistory.listSeatNumberToPlayer = listSeatNumberToPlayer;								// handHistory.listSeatNumberToPlayer
		
		
		
		
		// the button, small, big blind (players and money) and playerYou
		
		double sb = 0.0;
		double bb = 0.0;
		Player button = new Player();
		ArrayList<Player> sB = new ArrayList<Player>();
		ArrayList<Player> bB = new ArrayList<Player>();
		Pot pot = new Pot();
		
		if ( button.name.equals( "" ) ) {
			button.name = allLines[ 0 ].split( "Geber: " )[ 1 ].split( " ist der Geber" )[ 0 ].trim();
			button.money = 0.0;
			button.seatNumberAbsolute = handHistory.numberPlayersAtTable;
			button.seatBehindBU = new SeatPosition( button.seatNumberAbsolute, button.seatNumberAbsolute );
			
			allPlayers.add( 0, button );
			allPlayerNames.add( 0, button.name );
			listSeatNumberToPlayer.add( 0, new SeatNumberPlayer(button.seatNumberAbsolute, button) );
			handHistory.allPlayers = allPlayers;													// handHistory.allPlayers
			handHistory.listSeatNumberToPlayer = listSeatNumberToPlayer;							// handHistory.listSeatNumberToPlayer
		}
		
		int howOftenSB = 0;
		int howOftenBB = 0;
		
		for ( int i = 0; i < ((linePreFlop > 0) ? linePreFlop : allLines.length); i++ )				// determining sb, bb, sB, bB
		{
			String s = allLines[ i ].split( "Geber: " )[ 1 ];
			
			double playerMoney = 0.0;
			String playerName = "";
			int seatNumber = 0;
			
			if ( s.matches( ".+ setzt Small Blind .+" ) )
			{
				playerName = s.split( " setzt Small Blind .+" )[ 0 ].trim();
				
				++howOftenSB;
				if ( howOftenSB == 1 )
					seatNumber = 1;
				else if ( indexOf(allPlayers, playerName) > -1 )
					seatNumber = allPlayers.get(indexOf(allPlayers, playerName)).seatNumberAbsolute;
				else
					seatNumber = allPlayers.size() + 1;				
				
				SeatPosition seatBU = new SeatPosition( seatNumber, "smallBlind", "smallBlind" );
				Player player = new Player( playerName, seatBU, seatNumber, playerMoney );
				sB.add( player );
				
				sb = Tools.parseDouble( s.split( " setzt Small Blind " )[ 1 ].trim() );
				pot.addM( sb );
				
				if ( indexOf(allPlayers, playerName) > -1 )
					allPlayers.get(indexOf(allPlayers, playerName)).seatBehindBU = new SeatPosition( allPlayers.get(indexOf(allPlayers, playerName)).seatNumberAbsolute, "smallBlind", "smallBlind" );
				else
				{
					allPlayers.add( player ); allPlayerNames.add( playerName ); listSeatNumberToPlayer.add( new SeatNumberPlayer(player.seatNumberAbsolute, player) );
					handHistory.allPlayers = allPlayers; handHistory.listSeatNumberToPlayer = listSeatNumberToPlayer;
			}	}
			else if ( s.matches( ".+ setzt Big Blind .+ und Dead Small Blind .+" ) )
			{
				playerName = s.split( " setzt Big Blind " )[ 0 ].trim();
				
				if ( indexOf(allPlayers, playerName) > -1 )
					seatNumber = allPlayers.get(indexOf(allPlayers, playerName)).seatNumberAbsolute;
				else
					seatNumber = allPlayers.size() + 1;
				
				SeatPosition seatBU = new SeatPosition( seatNumber, "bigBlind", "bigBlind" );
				Player player = new Player( playerName, seatBU, seatNumber, playerMoney );
				bB.add( player );
				pot.addM( sb + bb );
				
				if ( indexOf(allPlayers, playerName) > -1 )
					allPlayers.get(indexOf(allPlayers, playerName)).seatBehindBU = new SeatPosition( allPlayers.get(indexOf(allPlayers, playerName)).seatNumberAbsolute, "bigBlind", "bigBlind" );
				else
				{
					allPlayers.add( player ); allPlayerNames.add( playerName ); listSeatNumberToPlayer.add( new SeatNumberPlayer(player.seatNumberAbsolute, player) );
					handHistory.allPlayers = allPlayers; handHistory.listSeatNumberToPlayer = listSeatNumberToPlayer;
			}	}
			else if ( s.matches( ".+ setzt Big Blind .+" ) )
			{
				playerName = s.split( " setzt Big Blind .+" )[ 0 ].trim();
				
				++howOftenBB;
				if ( howOftenBB == 1 )
					seatNumber = 2;
				else if ( indexOf(allPlayers, playerName) > -1 )
					seatNumber = allPlayers.get(indexOf(allPlayers, playerName)).seatNumberAbsolute;
				else
					seatNumber = allPlayers.size() + 1;
				
				SeatPosition seatBU = new SeatPosition( seatNumber, "bigBlind", "bigBlind" );
				Player player = new Player( playerName, seatBU, seatNumber, playerMoney );
				bB.add( player );
				
				bb = Tools.parseDouble( s.split( " setzt Big Blind " )[ 1 ].trim() );
				pot.addM( bb );
				
				if ( indexOf(allPlayers, playerName) > -1 )
					allPlayers.get(indexOf(allPlayers, playerName)).seatBehindBU = new SeatPosition( allPlayers.get(indexOf(allPlayers, playerName)).seatNumberAbsolute, "bigBlind", "bigBlind" );
				else
				{
					allPlayers.add( player ); allPlayerNames.add( playerName ); listSeatNumberToPlayer.add( new SeatNumberPlayer(player.seatNumberAbsolute, player) );
					handHistory.allPlayers = allPlayers; handHistory.listSeatNumberToPlayer = listSeatNumberToPlayer;
			}	}	}
		
		
		handHistory.SB = sb;																		// handHistory.SB
		handHistory.BB = bb;																		// handHistory.BB
		handHistory.button = button;																// handHistory.button
		handHistory.smallBlindP = sB;																// handHistory.smallBlindP
		handHistory.bigBlindP = bB;																	// handHistory.bigBlindP
		handHistory.pot = pot;																		// handHistory.pot
		
		
		
		if ( indexOf( allPlayers, playYouName ) == -1 ) {			// if PlayerYou is not in the HandHistory contained
			int seatNumber = handHistory.allPlayers.size() + 1;
			double playerMoney = 0.0;
			SeatPosition seatBU = new SeatPosition( seatNumber, handHistory.numberPlayersAtTable );
			Player player = new Player( playYouName, seatBU, seatNumber, playerMoney );
			allPlayers.add( player );
			allPlayerNames.add( playYouName );
			listSeatNumberToPlayer.add( new SeatNumberPlayer(player.seatNumberAbsolute, player) );
			handHistory.allPlayers = allPlayers;
			handHistory.listSeatNumberToPlayer = listSeatNumberToPlayer;
		}
		
		
		
		
		// pre-flop-phase
		
		String gettingStartCards = allLines[ linePreFlop + 1 ];
		Card startCard1 = new Card();
		Card startCard2 = new Card();
		
		if ( gettingStartCards.matches( "Geber: Ihre Karten [23456789TJQKA][shdc] [23456789TJQKA][shdc]" ) ) {
			startCard1.set( Card.stringToCard( gettingStartCards.split( " Ihre Karten " )[ 1 ].trim().substring( 0, 2 ) ) );
			startCard2.set( Card.stringToCard( gettingStartCards.split( " Ihre Karten " )[ 1 ].trim().substring( 3 ).trim() ) );
		}
		
		CardList startHand = new CardList( startCard1, startCard2 );
		handHistory.preFlop.startHand = startHand;													// handHistory.preFlop.startHand
		
		
		Action actPreFlop = new Action();
		PlayerActionList playActListPreFlop = new PlayerActionList();
		Pot potPreFlop = new Pot( 0 );
		
		for ( int i = linePreFlop + 2; i < ((lineFlop > 0) ? lineFlop : allLines.length); i++ ) {
			if ( allLines[ i ].isEmpty() )
				continue;
			
			String s = allLines[ i ].split( "Geber: " )[ 1 ].trim();
			
			String playerName = "";
			double money = 0.0;
			
			if ( s.matches( ".+ passt" ) ) {
				playerName = s.split( " passt" )[ 0 ].trim();
				actPreFlop.set( "fold", 0 );
				playActListPreFlop.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actPreFlop ) );
			} else if ( s.matches( ".+ schiebt" ) ) {
				playerName = s.split( " schiebt" )[ 0 ].trim();
				actPreFlop.set( "check", 0 );
				playActListPreFlop.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actPreFlop ) );
			} else if ( s.matches( ".+ geht mit bei .+" ) ) {
				money = Tools.parseDouble( s.split( ".+ geht mit bei " )[ 1 ].trim() );
				playerName = s.split( " geht mit bei .+" )[ 0 ].trim();
				actPreFlop.set( "call", money );
				playActListPreFlop.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actPreFlop ) );
				potPreFlop.addM( money );
			} else if ( s.matches( ".+ setzt .+" ) ) {
				money = Tools.parseDouble( s.split( ".+ setzt ")[ 1 ].trim() );
				playerName = s.split( " setzt .+" )[ 0 ].trim();
				actPreFlop.set( "bet", money );
				playActListPreFlop.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actPreFlop ) );
				potPreFlop.addM( money );
			} else if ( s.matches( ".+ erhöht auf .+" ) ) {
				money = Tools.parseDouble( s.split( ".+ erhöht auf ")[ 1 ].trim() );
				playerName = s.split( " erhöht auf .+" )[ 0 ].trim();
				actPreFlop.set( "raise", money );
				playActListPreFlop.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actPreFlop ) );
				potPreFlop.addM( money );
		}	}
		
		handHistory.preFlop.playerActionList = playActListPreFlop;									// handHistory.preFlop.playerActionList
		handHistory.preFlop.pot = potPreFlop;														// handHistory.preFlop.pot
		handHistory.pot.addM( potPreFlop );															// handHistory.pot
		
		handHistory.preFlop.howManyPlayersInGame = handHistory.preFlop.howManyPlayersInGame(bB.size());	// handHistory.preFlop.howManyPlayersInGame
		handHistory.howManyPlayerInGame = handHistory.preFlop.howManyPlayersInGame;					// handHistory.howManyPlayersInGame
		
		
		
		
		// flop-phase
		
		if ( lineFlop > 0 )
		{
			CardList boardFlop = new CardList();
			
			for ( String s : allLines[lineFlop].split("Geber: Flop wird gegeben ")[1].split("\\s+")  )
				boardFlop.add( new Card(Card.stringToCard(s.trim())) );
			
			handHistory.flop.board = boardFlop;														// handHistory.flop.board
			handHistory.flop.flop = boardFlop;														// handHistory.flop.flop
			
			
			Action actFlop = new Action();
			PlayerActionList playActListFlop = new PlayerActionList();
			Pot potFlop = new Pot( 0 );
			
			for ( int i = lineFlop + 1; i < ((lineTurn > 0) ? lineTurn : allLines.length); i++ ) {
				if ( allLines[ i ].isEmpty() )
					continue;
				
				String s = allLines[ i ].split( "Geber: " )[ 1 ].trim();
				
				String playerName = "";
				double money = 0.0;
				
				if ( s.matches( ".+ passt" ) ) {
					playerName = s.split( " passt" )[ 0 ].trim();
					actFlop.set( "fold", 0 );
					playActListFlop.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actFlop ) );
				} else if ( s.matches( ".+ schiebt" ) ) {
					playerName = s.split( " schiebt" )[ 0 ].trim();
					actFlop.set( "check", 0 );
					playActListFlop.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actFlop ) );
				} else if ( s.matches( ".+ geht mit bei .+" ) ) {
					money = Tools.parseDouble( s.split( ".+ geht mit bei " )[ 1 ].trim() );
					playerName = s.split( " geht mit bei .+" )[ 0 ].trim();
					actFlop.set( "call", money );
					playActListFlop.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actFlop ) );
					potFlop.addM( money );
				} else if ( s.matches( ".+ setzt .+" ) ) {
					money = Tools.parseDouble( s.split( ".+ setzt ")[ 1 ].trim() );
					playerName = s.split( " setzt .+" )[ 0 ].trim();
					actFlop.set( "bet", money );
					playActListFlop.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actFlop ) );
					potFlop.addM( money );
				} else if ( s.matches( ".+ erhöht auf .+" ) ) {
					money = Tools.parseDouble( s.split( ".+ erhöht auf ")[ 1 ].trim() );
					playerName = s.split( " erhöht auf .+" )[ 0 ].trim();
					actFlop.set( "raise", money );
					playActListFlop.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actFlop ) );
					potFlop.addM( money );
			}	}
			
			handHistory.flop.playerActionList = playActListFlop;								// handHistory.flop.playerActionList
			handHistory.flop.pot = potFlop;														// handHistory.flop.pot
			handHistory.pot.addM( potFlop );													// handHistory.pot
			
			handHistory.flop.howManyPlayersInGame = handHistory.flop.howManyPlayersInGame( handHistory.preFlop.howManyPlayersInGame );	// handHistory.flop.howManyPlayersInGame
			handHistory.howManyPlayerInGame = handHistory.flop.howManyPlayersInGame;			// handHistory.howManyPlayersInGame
		}
		
		
		
		
		// turn-phase
		
		if ( lineTurn > 0 )
		{
			CardList boardTurn = new CardList();
			
			for ( String s : allLines[lineTurn].split("Geber: Turnkarte wird gegeben ")[1].split("\\s+") )
				boardTurn.add( Card.stringToCard(s.trim()) );
			
			handHistory.turn.turn = new Card( boardTurn.get(boardTurn.size()-1) );				// handHistory.turn.turn
			handHistory.turn.restOpenCards = handHistory.flop.board;							// handHistory.turn.restOpenCards
			handHistory.turn.board = boardTurn;													// handHistory.turn.board
			
			
			Action actTurn = new Action();
			PlayerActionList playActListTurn = new PlayerActionList();
			Pot potTurn = new Pot( 0 );
			
			for ( int i = lineFlop + 1; i < ((lineTurn > 0) ? lineTurn : allLines.length); i++ ) {
				if ( allLines[ i ].isEmpty() )
					continue;
				
				String s = allLines[ i ].split( "Geber: " )[ 1 ].trim();
				
				String playerName = "";
				double money = 0.0;
				
				if ( s.matches( ".+ passt" ) ) {
					playerName = s.split( " passt" )[ 0 ].trim();
					actTurn.set( "fold", 0 );
					playActListTurn.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actTurn ) );
				} else if ( s.matches( ".+ schiebt" ) ) {
					playerName = s.split( " schiebt" )[ 0 ].trim();
					actTurn.set( "check", 0 );
					playActListTurn.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actTurn ) );
				} else if ( s.matches( ".+ geht mit bei .+" ) ) {
					money = Tools.parseDouble( s.split( ".+ geht mit bei " )[ 1 ].trim() );
					playerName = s.split( " geht mit bei .+" )[ 0 ].trim();
					actTurn.set( "call", money );
					playActListTurn.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actTurn ) );
					potTurn.addM( money );
				} else if ( s.matches( ".+ setzt .+" ) ) {
					money = Tools.parseDouble( s.split( ".+ setzt ")[ 1 ].trim() );
					playerName = s.split( " setzt .+" )[ 0 ].trim();
					actTurn.set( "bet", money );
					playActListTurn.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actTurn ) );
					potTurn.addM( money );
				} else if ( s.matches( ".+ erhöht auf .+" ) ) {
					money = Tools.parseDouble( s.split( ".+ erhöht auf ")[ 1 ].trim() );
					playerName = s.split( " erhöht auf .+" )[ 0 ].trim();
					actTurn.set( "raise", money );
					playActListTurn.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actTurn ) );
					potTurn.addM( money );
			}	}
			
			handHistory.turn.playerActionList = playActListTurn;							// handHistory.turn.playerActionList
			handHistory.turn.pot = potTurn;													// handHistory.turn.pot
			handHistory.pot.addM( potTurn );												// handHistory.pot
			
			handHistory.turn.howManyPlayersInGame = handHistory.turn.howManyPlayersInGame( handHistory.flop.howManyPlayersInGame );	// handHistory.turn.howManyPlayersInGame
			handHistory.howManyPlayerInGame = handHistory.turn.howManyPlayersInGame;		// handHistory.howManyPlayersInGame
		}
		
		
		
		
		// river-phase
		
		if ( lineRiver > 0 )
		{
			CardList boardRiver = new CardList();
			
			for ( String s : allLines[lineRiver].split("Geber: Riverkarte wird gegeben ")[1].split("\\s+") )
				boardRiver.add( Card.stringToCard(s.trim()) );
			
			handHistory.river.river = new Card( boardRiver.get(boardRiver.size()-1) );	// handHistory.river.river
			handHistory.river.restOpenCards = handHistory.turn.board;					// handHistory.river.restOpenCards
			handHistory.river.board = boardRiver;										// handHistory.river.board
			
			
			Action actRiver = new Action();
			PlayerActionList playActListRiver = new PlayerActionList();
			Pot potRiver = new Pot( 0 );
			
			for ( int i = lineFlop + 1; i < ((lineTurn > 0) ? lineTurn : allLines.length); i++ ) {
				if ( allLines[ i ].isEmpty() )
					continue;
				
				String s = allLines[ i ].split( "Geber: " )[ 1 ].trim();
				
				String playerName = "";
				double money = 0.0;
				
				if ( s.matches( ".+ passt" ) ) {
					playerName = s.split( " passt" )[ 0 ].trim();
					actRiver.set( "fold", 0 );
					playActListRiver.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actRiver ) );
				} else if ( s.matches( ".+ schiebt" ) ) {
					playerName = s.split( " schiebt" )[ 0 ].trim();
					actRiver.set( "check", 0 );
					playActListRiver.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actRiver ) );
				} else if ( s.matches( ".+ geht mit bei .+" ) ) {
					money = Tools.parseDouble( s.split( ".+ geht mit bei " )[ 1 ].trim() );
					playerName = s.split( " geht mit bei .+" )[ 0 ].trim();
					actRiver.set( "call", money );
					playActListRiver.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actRiver ) );
					potRiver.addM( money );
				} else if ( s.matches( ".+ setzt .+" ) ) {
					money = Tools.parseDouble( s.split( ".+ setzt ")[ 1 ].trim() );
					playerName = s.split( " setzt .+" )[ 0 ].trim();
					actRiver.set( "bet", money );
					playActListRiver.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actRiver ) );
					potRiver.addM( money );
				} else if ( s.matches( ".+ erhöht auf .+" ) ) {
					money = Tools.parseDouble( s.split( ".+ erhöht auf ")[ 1 ].trim() );
					playerName = s.split( " erhöht auf .+" )[ 0 ].trim();
					actRiver.set( "raise", money );
					playActListRiver.add( new PlayerAction( allPlayers.get(indexOf(allPlayers, playerName)), actRiver ) );
					potRiver.addM( money );
			}	}
			
			handHistory.river.playerActionList = playActListRiver;					// handHistory.river.playerActionList
			handHistory.river.pot = potRiver;										// handHistory.river.pot
			handHistory.pot.addM( potRiver );										// handHistory.pot
			
			handHistory.river.howManyPlayersInGame = handHistory.river.howManyPlayersBeforeInGame( handHistory.turn.howManyPlayersInGame );	// handHistory.river.howManyPlayersInGame
			handHistory.howManyPlayerInGame = handHistory.river.howManyPlayersInGame;	// handHistory.howManyPlayersInGame
		}
		
		
		
		
		// show-down-phase
		
		
		
		// summary-phase
		
		
		
		
		return handHistory;
	}
	
	/**
	 * Returns how many players sits at the table.
	 * This method is just written for a table with 9 players!
	 * 
	 * @return the number of players at the table
	 */
	public static int howManyPlayersAtTable(BufferedImage[] pictureSeats, Rectangle[] spaceSeats) throws AWTException
	{
		Robot r = new Robot();
		
		BufferedImage bi1 = r.createScreenCapture( spaceSeats[ 0 ] );
		BufferedImage bi2 = r.createScreenCapture( spaceSeats[ 1 ] );
		BufferedImage bi3 = r.createScreenCapture( spaceSeats[ 2 ] );
		BufferedImage bi4 = r.createScreenCapture( spaceSeats[ 3 ] );
		BufferedImage bi5 = r.createScreenCapture( spaceSeats[ 4 ] );
		BufferedImage bi6 = r.createScreenCapture( spaceSeats[ 5 ] );
		BufferedImage bi7 = r.createScreenCapture( spaceSeats[ 6 ] );
		BufferedImage bi8 = r.createScreenCapture( spaceSeats[ 7 ] );
		BufferedImage bi9 = r.createScreenCapture( spaceSeats[ 8 ] );
		
		int counter = 0;
		
		if ( Tools.compare( bi1, pictureSeats[ 0 ],  0.75 ) )
			++counter;
		if ( Tools.compare( bi2, pictureSeats[ 1 ],  0.75 ) )
			++counter;
		if ( Tools.compare( bi3, pictureSeats[ 2 ],  0.75 ) )
			++counter;
		if ( Tools.compare( bi4, pictureSeats[ 3 ],  0.75 ) )
			++counter;
		if ( Tools.compare( bi5, pictureSeats[ 4 ],  0.75 ) )
			++counter;
		if ( Tools.compare( bi6, pictureSeats[ 5 ],  0.75 ) )
			++counter;
		if ( Tools.compare( bi7, pictureSeats[ 6 ],  0.75 ) )
			++counter;
		if ( Tools.compare( bi8, pictureSeats[ 7 ],  0.75 ) )
			++counter;
		if ( Tools.compare( bi9, pictureSeats[ 8 ],  0.75 ) )
			++counter;
		
		return 9 - counter;
	}

	public static int indexOf( ArrayList<Player> players, Player p ) {
		return ParserCreatorWinnerPoker1Table.indexOf( players, p );
	}
	
	public static int indexOf( ArrayList<Player> players, String namePlayer ) {
		return ParserCreatorWinnerPoker1Table.indexOf( players, namePlayer );
	}
	
	public static ArrayList<String> getPlayerNames( String[] lines ) {
		return ParserCreatorWinnerPoker1Table.getPlayerNames( lines );
	}
	
	public static int untilItIsNecessaryForAllPlayers( String[] lines ) {
		return ParserCreatorWinnerPoker1Table.untilItIsNecessaryForAllPlayers( lines );
	}
	
	public static boolean playerTwiceContained( String[] lines ) {
		return ParserCreatorWinnerPoker1Table.playerTwiceContained( lines );
	}
	
	public static int howManyPlayersContained( String[] lines ) {
		return ParserCreatorWinnerPoker1Table.howManyPlayersContained( lines );
	}
	
}
