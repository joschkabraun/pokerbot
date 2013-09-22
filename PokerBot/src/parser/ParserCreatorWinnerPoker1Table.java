package parser;

import cardBasics.*;
import gameBasics.*;
import handHistory.*;
import handHistory.HandHistory.GameType;
import handHistory.HandHistory.Limit;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import javax.imageio.ImageIO;

import other.Tools;

public class ParserCreatorWinnerPoker1Table

{
	
	/**
	 * Returns the HandHistory (as object HandHistory) of a .txt-file which is made from the CreatorWinnerPoker1Table (CWP1T).
	 * This method just works for maxSeatOnTable=9! This method just works if the table is in the upper-left corner
	 * and only with one table!
	 * 
	 * @param f .txt-file with the hand history
	 * @param gameType the gameType of the hand history
	 * @param limit the limit of the hand history
	 * @param maxSeatOnTable the maximal number of seats on the table of the hand history
	 * @param playYouName the name of the PlayerYou
	 * @return a .txt-file parsed into the object hand history
	 */
	public static HandHistory parserMainCWP( File f, GameType gameType, Limit limit, int maxSeatOnTable, String playYouName ) throws IOException, AWTException
	{
		if ( maxSeatOnTable != 9 )
			throw new IllegalArgumentException( "The ParserCreatorWinnerPoker does not work for maxSeatOnTable != 9!" );
		
		File heapParserCWP = new File( "c://pokerBot//bot_v1_0_0//heapParserCWP.txt" );
		String[] allLinesWithoutTrim = Tools.allLines( f );
		
		int length = allLinesWithoutTrim.length;
		String[] allLines = new String[ length ];
		for ( int i = 0; i < length; i++ )
			allLines[ i ] = allLinesWithoutTrim[ i ].trim();
		
		FileWriter heapW = new FileWriter(heapParserCWP);
		
		int hhStart = 0;
		int hhEnd = allLines.length - 1;
		
		for (int i = allLines.length - 1; -1 < i; i--)
			if (allLines[i].matches("Geber: .+ ist der Geber"))
				{ hhStart = i; break; }
		
		for (int i = hhStart; i < hhEnd + 1; i++)
			heapW.write(String.format(allLines[i] + "%n"));

		heapW.flush();
		heapW.close();
		return parserCWP( heapParserCWP, gameType, limit, maxSeatOnTable, playYouName );
	}
	
	/**
	 * Returns the HandHistory (as Object HandHistory) of a .txt-file which is made from the method
	 * parserMainCWP.
	 * 
	 * @param f .txt-file with only the necessary hand history
	 * @param gameType the gameType of the hand history
	 * @param limit the limit of the hand history
	 * @param maxSeatOnTable the maximal number of seat on the table of the hand history
	 * @param playYouName the name of the PlayerYou
	 * @return a .txt-file parsed into the object hand history
	 */
	public static HandHistory parserCWP( File f, GameType gameType, Limit limit, int maxSeatOnTable, String playYouName ) throws AWTException, IOException
	{
		HandHistory handHistory = new HandHistory();
		
		if ( ( ! f.exists() ) )
			throw new IllegalArgumentException( "The passed file f does not exist" );
		
		String[] allLines = clearLines( clearCurrency( arraysTrim( Tools.allLines( f ) ) ) );
		
		if ( allLines[0].equals( "null" ) )
			throw new IllegalArgumentException( "The passed file f is empty" );
		
		
		
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
				int numberPlayersAtTable = howManyPlayersAtTable();
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
					SeatPosition seatBU = new SeatPosition( seatNumber, howManyPlayersAtTable() );
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
		handHistory.bettingRounds.add( handHistory.preFlop );										// handHistory.bettingRounds
		
		
		
		
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
			handHistory.bettingRounds.add( handHistory.flop );									// handHistory.bettingRounds
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
			handHistory.bettingRounds.add( handHistory.turn );								// handHistory.bettingRounds
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
			handHistory.bettingRounds.add( handHistory.river );						// handHistory.bettingRounds
		}
		
		
		
		
		// show-down-phase
		
		if ( lineShowDown > 0 ) {
			for ( int i = lineShowDown; i < allLines.length; i++ )
				if ( allLines[i].matches("Geber: Spiel Nr.\\d+: Gewinner ist .+ [$][0-9.,]+") ) {
					String playerName = allLines[i].split("Geber: Spiel Nr.\\d+ Gewinner ist ")[0].split(" [$][0-9.,]+")[0];
					Player p = handHistory.allPlayers.get( indexOf(handHistory.allPlayers, playerName) );
					double m = Tools.parseDouble( allLines[i].split("Geber: Spiel Nr.\\d+: Gewinner ist .+ [$]")[0] );
					handHistory.showDown.playerMoneyList.add( new PlayerMoney(p, m) );
					break;
				} else if ( allLines[i].matches("Geber: Spiel Nr.\\d+: .+ gewinnt den Haupt-Pot [(][$][0-9.,]+[)] mit .+ - .+") ) {
					String playerName = allLines[i].split("Geber: Spiel Nr.\\d+: ")[0].split(" gewinnt den Haupt-Pot")[0];
					Player p = handHistory.allPlayers.get( indexOf(handHistory.allPlayers, playerName) );
					double m = Tools.parseDouble( allLines[i].split("Geber: Spiel Nr.\\d+: .+ gewinnt den Haupt-Pot [(][$]")[0].split("[)] mit .+")[0] );
					handHistory.showDown.playerMoneyList.add( new PlayerMoney(p, m) );
					
					String sc = allLines[i].split("Geber: Spiel Nr.\\d+: .+ gewinnt den Haupt-Pot [(][$][0-9.,]+[)] mit .+ - ")[0];
					CardList handCC = new CardList();
					for ( String s : sc.split("\\s") )
						handCC.add( Card.stringToCard(s) );
					CardList hand = CardList.theNewCards(handCC, handHistory.getCommonCards());
					String ccWPTC = allLines[i].split("Geber: Spiel Nr.\\d+: .+ gewinnt den Haupt-Pot [(][$][0-9.,]+[)] mit ")[0].split(" .+ - .+")[0];
					String cc = ParserCreatorWinnerPoker4Tables.toOwnConvention(ccWPTC);
					handHistory.showDown.playerHandList.add( new PlayerHandCombination(p, hand, new CardCombination(handCC, cc)) );
				} else if ( allLines[i].matches("Geber: Spiel Nr.\\d+: .+ gewinnt den Neben-Pot [(][$][0-9.,]+[)] mit .+ - .+") ) {
					String playerName = allLines[i].split("Geber: Spiel Nr.\\d+: ")[0].split(" gewinnt den Neben-Pot")[0];
					Player p = handHistory.allPlayers.get( indexOf(handHistory.allPlayers, playerName) );
					double m = Tools.parseDouble( allLines[i].split("Geber: Spiel Nr.\\d+: .+ gewinnt den Neben-Pot [(][$]")[0].split("[)] mit .+")[0] );
					handHistory.showDown.playerMoneyList.add( new PlayerMoney(p, m) );
					
					String sc = allLines[i].split("Geber: Spiel Nr.\\d+: .+ gewinnt den Neben-Pot [(][$][0-9.,]+[)] mit .+ - ")[0];
					CardList handCC = new CardList();
					for ( String s : sc.split("\\s") )
						handCC.add( Card.stringToCard(s) );
					CardList hand = CardList.theNewCards(handCC, handHistory.getCommonCards());
					String ccWPTC = allLines[i].split("Geber: Spiel Nr.\\d+: .+ gewinnt den Neben-Pot [(][$][0-9.,]+[)] mit ")[0].split(" .+ - .+")[0];
					String cc = ParserCreatorWinnerPoker4Tables.toOwnConvention(ccWPTC);
					handHistory.showDown.playerHandList.add( new PlayerHandCombination(p, hand, new CardCombination(handCC, cc)) );
				}
		}
		
		
		
		
		// some more attributes of the HandHistory are determined
		
		handHistory.playerStatesOut = (ArrayList<PlayerPokerChallengeGameState>) Arrays.asList( new PlayerPokerChallengeGameState[allPlayers.size()] );
		for ( int i = 0; i < allPlayers.size(); i++ )
			loop:
				for ( BettingRound br : handHistory.bettingRounds )
					for ( PlayerAction pa : br.playerActionList )
						if ( ParserCreatorWinnerPoker4Tables.indexOfPPCGS(handHistory.playerStatesOut, allPlayers.get(i).name) == -1 )
							continue loop;
						else if ( ! pa.player.name.equals(allPlayers.get(i)) )
							continue;
						else if ( pa.action.actionName.equals("fold") )
							handHistory.playerStatesOut.set( i, new PlayerPokerChallengeGameState(allPlayers.get(i), br.getPokerChallengeGameState()) );
		
		
		
		
		return handHistory;
	}
	
	/**
	 * Returns the first index of the player p in the list allPlayers.
	 * If allPlayers does not contain Player p, the method will return -1.
	 * 
	 * @param allPlayers the list with all players of the game
	 * @param p the player
	 * @return the index of p in allPlayers
	 */
	public static int indexOf( ArrayList<Player> allPlayers, Player p )
	{
		for ( int i = 0; i < allPlayers.size(); i++ )
			if ( allPlayers.get(i).name.equals( p.name ) )
				return i;
		return -1;
	}
	
	/**
	 * Returns the first index of the player with the name namePlayer.
	 * If allPlayers does not contain a player with the name namePlayer, the method will return -1.
	 * 
	 * @param allPlayers the list with all players of the game
	 * @param namePlayer the name of the searched player
	 * @return the index of the player with the name namePlayer in allPlayers
	 */
	public static int indexOf( ArrayList<Player> allPlayers, String namePlayer )
	{
		for ( int i = 0; i < allPlayers.size(); i++ )
			if ( allPlayers.get(i).name.equals( namePlayer ) )
				return i;
		return -1;
	}
	
	/**
	 * Returns whether the player is in allPlayers.
	 * 
	 * @param allPlayers the list with all players of the game
	 * @param p the player
	 * @return whether the player is in allPlayers
	 */
	public static boolean contains( ArrayList<Player> allPlayers, Player p )
	{
		if ( indexOf( allPlayers, p ) == -1 )
			return false;
		return true;
	}
	
	/**
	 * Returns how many players sits at the table.
	 * This method is just written for a table with 9 players!
	 * 
	 * @return the number of players at the table
	 */
	public static int howManyPlayersAtTable() throws AWTException, IOException
	{
		Rectangle seat1 = new Rectangle( 128, 111, 51, 55);
		Rectangle seat2 = new Rectangle( 267, 66, 65, 51 );
		Rectangle seat3 = new Rectangle( 490, 68, 50, 52 );
		Rectangle seat4 = new Rectangle( 637, 113, 33, 55 );
		Rectangle seat5 = new Rectangle( 700, 223, 50, 50 );
		Rectangle seat6 = new Rectangle( 639, 359, 52, 54 );
		Rectangle seat7 = new Rectangle( 480, 389, 51, 49 );
		Rectangle seat8 = new Rectangle( 283, 386, 50, 55 );
		Rectangle seat9 = new Rectangle( 128, 357, 52, 53 );
		
		Robot r = new Robot();
		
		BufferedImage bi1 = r.createScreenCapture( seat1 );
		BufferedImage bi2 = r.createScreenCapture( seat2 );
		BufferedImage bi3 = r.createScreenCapture( seat3 );
		BufferedImage bi4 = r.createScreenCapture( seat4 );
		BufferedImage bi5 = r.createScreenCapture( seat5 );
		BufferedImage bi6 = r.createScreenCapture( seat6 );
		BufferedImage bi7 = r.createScreenCapture( seat7 );
		BufferedImage bi8 = r.createScreenCapture( seat8 );
		BufferedImage bi9 = r.createScreenCapture( seat9 );
		
		BufferedImage pi1 = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_0_0//pictureSeat1.PNG") );
		BufferedImage pi2 = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_0_0//pictureSeat2.PNG") );
		BufferedImage pi3 = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_0_0//pictureSeat3.PNG") );
		BufferedImage pi4 = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_0_0//pictureSeat4.PNG") );
		BufferedImage pi5 = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_0_0//pictureSeat5.PNG") );
		BufferedImage pi6 = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_0_0//pictureSeat6.PNG") );
		BufferedImage pi7 = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_0_0//pictureSeat7.PNG") );
		BufferedImage pi8 = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_0_0//pictureSeat8.PNG") );
		BufferedImage pi9 = ImageIO.read( new File("c://pokerBot//picturesWinnerPoker//bot_v1_0_0//pictureSeat9.PNG") );
		
		int counter = 0;
		
		if ( Tools.compare( bi1, pi1, 0.5 ) )
			++counter;
		if ( Tools.compare( bi2, pi2, 0.5 ) )
			++counter;
		if ( Tools.compare( bi3, pi3, 0.5 ) )
			++counter;
		if ( Tools.compare( bi4, pi4, 0.5 ) )
			++counter;
		if ( Tools.compare( bi5, pi5, 0.5 ) )
			++counter;
		if ( Tools.compare( bi6, pi6, 0.5 ) )
			++counter;
		if ( Tools.compare( bi7, pi7, 0.5 ) )
			++counter;
		if ( Tools.compare( bi8, pi8, 0.5 ) )
			++counter;
		if ( Tools.compare( bi9, pi9, 0.5 ) )
			++counter;
		
		return 9 - counter; 
	}
	
	/**
	 * Returns whether one player is twice found in lines.
	 * This method counts how many small blinds and big blinds are in lines. And if there are two small or big blinds,
	 * the method will just count the first small/big blind and ignore the other.
	 *  
	 * @param lines an array of strings
	 * @return whether one player is twice found in lines
	 */
	public static boolean playerTwiceContained( String[] lines )
	{
		ArrayList<String> pNam = new ArrayList<String>();		// pNam = player names
		int SBs = 0;
		int BBs = 0;
		
		for ( int i = 0; i < lines.length; i++ ) {
			if ( lines[i].equals("") || lines[i] == null )
				continue;
			String s = lines[ i ].split( "Geber: " )[ 1 ];
			
			if ( s.matches( ".+ schiebt" ) ) {
				String name = s.split( " schiebt" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return true;
				pNam.add( name );
			} else if ( s.matches( ".+ setzt Small Blind .+" ) ) {
				String name = s.split( " setzt Small Blind .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					if ( pNam.size() == 1 )					// if there are just two players than is the button also the small blind
						continue;
					else
						return true;
				if ( SBs == 1 )
					continue;
				pNam.add( name );
				++SBs;
			} else if ( s.matches( ".+ setzt Big Blind .+" ) ) {
				String name = s.split( " setzt Big Blind .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return true;
				if ( BBs == 1 )
					continue;
				pNam.add( name );
				++BBs;
			} else if ( s.matches( ".+ erhöht auf .+" ) ) {
				String name = s.split( " erhöht auf .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return true;
				pNam.add( name );
			} else if ( s.matches( ".+ geht mit bei .+" ) ) {
				String name = s.split( " geht mit bei .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return true;
				pNam.add( name );
			} else if ( s.matches( ".+ setzt .+" ) ) {
				String name = s.split( " setzt .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return true;
				pNam.add( name );
			} else if ( s.matches( ".+ passt" ) ) {
				String name = s.split( " passt" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return true;
				pNam.add( name );
			} else if ( s.matches( ".+ ist der Geber" ) ) {
				String name = s.split( " ist der Geber" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return true;
				pNam.add( name );
		}	}
		
		return false;
	}
	
	/**
	 * Returns how many different players are contained in lines.
	 * This method counts how many small blinds and big blinds are in lines. And if there are two small or big blinds,
	 * the method will just count the first small/big blind and ignore the other.
	 * 
	 * @param lines an array of strings
	 * @return how many players are contained in lines
	 */
	public static int howManyPlayersContained( String[] lines )
	{
		ArrayList<String> pNam = new ArrayList<String>();		// pNam = player names
		int SBs = 0;
		int BBs = 0;
		
		for ( int i = 0; i < lines.length; i++ ) {
			if ( lines[i].equals("") || lines[i] == null )
				continue;
			
			String s = lines[ i ].split( "Geber: " )[ 1 ];
			
			if ( s.matches( ".+ schiebt" ) ) {
				String name = s.split( " schiebt" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return pNam.size();
				pNam.add( name );
			} else if ( s.matches( ".+ erhöht auf .+" ) ) {
				String name = s.split( " erhöht auf .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return pNam.size();
				pNam.add( name );
			} else if ( s.matches( ".+ geht mit bei .+" ) ) {
				String name = s.split( " geht mit bei .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return pNam.size();
				pNam.add( name );
			} else if ( s.matches( ".+ setzt Small Blind .+" ) ) {
				String name = s.split( " setzt Small Blind .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					if ( pNam.size() == 1 )					// if there are just two players than is the button also the small blind
						continue;
					else
						return pNam.size();
				if ( SBs == 1 )
					continue;
				pNam.add( name );
				++SBs;
			} else if ( s.matches( ".+ setzt Big Blind .+" ) ) {
				String name = s.split( " setzt Big Blind .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return pNam.size();
				if ( BBs == 1 )
					continue;
				pNam.add( name );
				++BBs;
			} else if ( s.matches( ".+ setzt .+" ) ) {
				String name = s.split( " setzt .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return pNam.size();
				pNam.add( name );
			} else if ( s.matches( ".+ passt" ) ) {
				String name = s.split( " passt" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return pNam.size();
				pNam.add( name );
			} else if ( s.matches( ".+ ist der Geber" ) ) {
				String name = s.split( " ist der Geber" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return pNam.size();
				pNam.add( name );
		}	}
		
		return pNam.size();
	}
	
	/**
	 * Returns the number of the line which is the last necessary for getting all players.
	 * This numbers is similar to the variable "untilItIsAboutSeats" in the class "ParserPokerStars".
	 * This method counts how many small blinds and big blinds are in lines. And if there are two small or big blinds,
	 * the method will just count the first small/big blind and ignore the other.
	 * 
	 * @param lines an array of strings which contains the pre-flop-lines and the head (dealer, big blind and small blind)
	 * @return the number of the line which is the last necessary for getting all players
	 */
	public static int untilItIsNecessaryForAllPlayers( String[] lines )
	{
		ArrayList<String> pNam = new ArrayList<String>();		// pNam = player names
		int SBs = 0;
		int BBs = 0;
		
		for ( int i = 0; i < lines.length; i++ ) {
			if  ( lines[i].equals("") || lines[i] == null )
				continue;
			String s = lines[ i ].split( "Geber: " )[ 1 ];
			
			if ( s.matches( ".+ schiebt" ) ) {
				String name = s.split( " schiebt" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return i - 1;
				pNam.add( name );
			} else if ( s.matches( ".+ setzt Small Blind .+" ) ) {
				String name = s.split( " setzt Small Blind .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					if ( pNam.size() == 1 )						// if there are just two players than is the button also the small blind
						continue;
					else
						return i - 1;
				if ( SBs == 1 )
					continue;
				pNam.add( name );
				++SBs;
			} else if ( s.matches( ".+ setzt Big Blind .+" ) ) {
				String name = s.split( " setzt Big Blind .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return i - 1;
				if ( BBs == 1 )
					continue;
				pNam.add( name );
				++BBs;
			} else if ( s.matches( ".+ erhöht auf .+" ) ) {
				String name = s.split( " erhöht auf .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return i - 1;
				pNam.add( name );
			} else if ( s.matches( ".+ geht mit bei .+" ) ) {
				String name = s.split( " geht mit bei .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return i - 1;
				pNam.add( name );
			} else if ( s.matches( ".+ setzt .+" ) ) {
				String name = s.split( " setzt .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return i - 1;
				pNam.add( name );
			} else if ( s.matches( ".+ passt" ) ) {
				String name = s.split( " passt" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return i - 1;
				pNam.add( name );
			} else if ( s.matches( ".+ ist der Geber" ) ) {
				String name = s.split( " ist der Geber" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return i-1;
				pNam.add( name );
		}	}
		
		return lines.length;
	}
	
	/**
	 * Returns all player names, which are in lines.
	 * This method counts how many small blinds and big blinds are in lines. And if there are two small or big blinds,
	 * the method will just count the first small/big blind and ignore the other.
	 * 
	 * @param lines an array of Strings
	 * @return all player names of lines
	 */
	public static ArrayList<String> getPlayerNames( String[] lines )
	{
		ArrayList<String> pNam = new ArrayList<String>();		// pNam = player names
		int SBs = 0;
		int BBs = 0;
		
		for ( int i = 0; i < lines.length; i++ ) {
			String s = lines[ i ].split( "Geber: " )[ 1 ];
			
			if ( s.matches( ".+ schiebt" ) ) {
				String name = s.split( " schiebt" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return pNam;
				pNam.add( name );
			} else if ( s.matches( ".+ setzt Small Blind .+" ) ) {
				String name = s.split( " setzt Small Blind .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					if ( pNam.size() == 1 )					// if there are just two players than is the button also the small blind
						continue;
					else
						return pNam;
				if ( SBs == 1 )
					continue;
				pNam.add( name );
				++SBs;
			} else if ( s.matches( ".+ setzt Big Blind .+" ) ) {
				String name = s.split( " setzt Big Blind .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return pNam;
				if ( BBs == 1 )
					continue;
				pNam.add( name );
				++BBs;
			} else if ( s.matches( ".+ erhöht auf .+" ) ) {
				String name = s.split( " erhöht auf .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return pNam;
				pNam.add( name );
			} else if ( s.matches( ".+ geht mit bei .+" ) ) {
				String name = s.split( " geht mit bei .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return pNam;
				pNam.add( name );
			} else if ( s.matches( ".+ setzt .+" ) ) {
				String name = s.split( " setzt .+" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return pNam;
				pNam.add( name );
			} else if ( s.matches( ".+ passt" ) ) {
				String name = s.split( " passt" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return pNam;
				pNam.add( name );
			}  else if ( s.matches( ".+ ist der Geber" ) ) {
				String name = s.split( " ist der Geber" )[ 0 ].trim();
				if ( pNam.indexOf( name ) > -1 )
					return pNam;
				pNam.add( name );
				}
			}
		
		return pNam;
	}
	
	/**
	 * Returns the array of strings cleared by 2,000 (two thousand) = 2000
	 * 
	 * @param lines an array of strings
	 * @return lines cleared by 2,000 = 2000
	 */
	public static String[] clearLines( String[] lines )
	{
		String[] ret = new String[ lines.length ];
		
		for ( int i = 0; i < ret.length; i++ ) {
			ret[ i ] = "";
			String string = lines[ i ];
			String[] sA = string.split( "\\s+" );
			for ( int j = 0; j < sA.length; j++ ) {
				if ( sA[ j ].matches( "\\d+[,]\\d+" ) ) {
					int index = sA[ j ].indexOf( "," );
					ret[ i ] += sA[ j ].substring( 0, index ) + sA[ j ].substring( index+1 ) + " ";
				} else if ( sA[ j ].matches( "\\d+[,]\\d+[.]\\d+" ) ) {
					int index = sA[ j ].indexOf( "," );
					ret[ i ] += sA[ j ].substring( 0, index ) + sA[ j ].substring( index+1 ) + " ";
				} else if ( sA[ j ].matches( "\\d+[,]\\d+[,]\\d+" ) ) {
					int index1 = sA[ j ].indexOf( "," );
					int index2 = (sA[j].substring(0, index1) + sA[j].substring(index1+1)).indexOf( "," );
					ret[ i ] += sA[ j ].substring( 0, index1 ) + sA[ j ].substring( index1+1, index2 ) + sA[ j ].substring( index2+1 ) + " ";
				} else if ( sA[ j ].matches( "\\d+[,]\\d+[,]\\d+[.]\\d+" ) ) {
					int index1 = sA[ j ].indexOf( "," );
					int index2 = (sA[j].substring(0, index1) + sA[j].substring(index1+1)).indexOf( "," );
					ret[ i ] += sA[ j ].substring( 0, index1 ) + sA[ j ].substring( index1+1, index2 ) + sA[ j ].substring( index2+1 ) + " ";
				} else
					ret[ i ] += sA[ j ] + " ";
			}
			ret[ i ] = ret[ i ].trim();
		}
		
		return ret;
	}
	
	/**
	 * Returns the assigned array of strings without any currency.
	 * For example $ 2000 = 2000.
	 * 
	 * @param lines an array of strings
	 * @return lines cleared by $2000=2000
	 */
	public static String[] clearCurrency( String[] lines )
	{
		String[] ret = new String[ lines.length ];
		
		for ( int i = 0; i < ret.length; i++ ) {
			ret[ i ] = "";
			String string = lines[ i ];
			String[] sA = string.split( "(\\s+)|[(]|[)]" );
			for ( int j = 0; j < sA.length; j++ )
				if ( sA[j].equals(" ") || sA[j].equals("") )
					continue;
				else if ( sA[ j ].matches( "[$][0123456789,.]+" ) )
					ret[ i ] += sA[ j ].substring( 1 ) + " ";
				else
					ret[ i ] += sA[ j ] + " ";
			ret[ i ] = ret[ i ].trim();
		}
		
		return ret;
	}
	
	/**
	 * Returns the assigned array of strings without empty entries.
	 * 
	 * @param lines an array of strings
	 * @return the assigned array of strings without empty entries
	 */
	public static String[] arraysTrim( String[] lines )
	{
		ArrayList<String> ret = new ArrayList<String>();
		
		for ( String s : lines )
			if ( s.equals( "" ) )
				continue;
			else
				ret.add( s );
		
		return Tools.toArrayS( ret );
	}
	
}
