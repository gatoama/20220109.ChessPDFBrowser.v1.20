/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 */
package com.frojasg1.chesspdfbrowser.engine.position;

import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.recognizer.constants.LibConstants;
import java.io.Serializable;
import java.util.function.BiPredicate;

/**
 *
 * @author Usuario
 */
public class ChessGamePositionBase implements Serializable
{
	public static final String GLOBAL_CONF_FILE_NAME = "ChessGamePositionBase.properties";

	protected static final String CONF_UNEXPECTED_NUMBER_OF_ROWS_PARSING_FEN = "UNEXPECTED_NUMBER_OF_ROWS_PARSING_FEN";
	protected static final String CONF_UNRECOGNIZED_FEN_CHAR_PIECE = "UNRECOGNIZED_FEN_CHAR_PIECE";
	protected static final String CONF_UNEXPECTED_NUMBER_OF_COLUMNS_PARSING_FEN = "UNEXPECTED_NUMBER_OF_COLUMNS_PARSING_FEN";
	protected static final String CONF_BAD_COLUMN_NUMBER = "BAD_COLUMN_NUMBER";
	protected static final String CONF_BAD_ROW_NUMBER = "BAD_ROW_NUMBER";
	protected static final String CONF_BAD_PIECE_TYPE = "BAD_PIECE_TYPE";

	public static final int NUM_OF_ROWS = 8;
	public static final int NUM_OF_COLUMNS = 8;

	protected static InternationalizedStringConfImp _internationalizedStringConf = null;

	protected Character[][] _position = null;

	static
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								LibConstants.sa_PROPERTIES_PATH_IN_JAR );

		registerInternationalizedStrings();
	}

	public ChessGamePositionBase()
	{
		setEmptyPosition();
	}

	public boolean isComplete()
	{
		return( true );
	}

	public String getFenPositionBase( String fen )
	{
		return( fen.split( "\\s" )[0] );
	}

	protected boolean isPieceAtPosition( int col, int row, char pieceCode )
	{
		return( ( _position[col][row] != null ) && ( _position[col][row] == pieceCode ) );
	}

	public Character[][] getPosition()								{	return( _position ); }

	public Character getCharacterAtPosition( int col, int row )
	{
		return( _position[col][row] );
	}

	public void setFenPositionBase( String fenPosition )
	{
		setEmptyPosition();

		parseFenPositionOnlyPosition( fenPosition );
	}

	public void setEmptyPosition()
	{
		_position = new Character[NUM_OF_COLUMNS+1][];

		for( int ii=0; ii<=NUM_OF_COLUMNS; ii++ )
		{
			_position[ii] = new Character[NUM_OF_ROWS+1];
		}
	}

	protected void parseFenPositionOnlyPosition( String fenPosition )
	{
		String trimmedString = fenPosition.trim();
		
		String[] fields = trimmedString.split( "\\s" );

		// board
		parseBoardFenPositionBase( fields[0] );
	}

	public void parseBoardFenPositionBase( String boardFenPosition )
	{
		String[] rows = boardFenPosition.split( "/" );

		if( rows.length != NUM_OF_ROWS )
			throw( new RuntimeException( createCustomInternationalString( CONF_UNEXPECTED_NUMBER_OF_ROWS_PARSING_FEN, boardFenPosition ) ) );

		for( int ii=0; ii<rows.length; ii++ )
		{
			int row = 8 - ii;

			int col = 1;
			int jj = 0;
			while( jj < rows[ii].length() )
			{
				char currentChar = rows[ii].charAt(jj);
				if( ( currentChar <= '9') && ( currentChar >= '0' ) )
				{
					int numberOfBlanks = currentChar - '0';
					for( int zz=0; zz<numberOfBlanks; zz++ )
						putPieceAtPositionBase( null, col+zz, row );
					col += numberOfBlanks;
				}
				else
				{
					try
					{
						putPieceAtPositionBase( currentChar, col, row );
					}
					catch( Throwable th )
					{
						throw( new RuntimeException( createCustomInternationalString( CONF_UNRECOGNIZED_FEN_CHAR_PIECE,
																	currentChar,
																	boardFenPosition ) ) );
					}
					col++;
				}
				if( col > NUM_OF_COLUMNS+1 )
					throw( new RuntimeException( createCustomInternationalString( CONF_UNEXPECTED_NUMBER_OF_COLUMNS_PARSING_FEN,
													boardFenPosition ) ) );
				jj++;
			}
		}
	}

	public Character getPieceAtPosition( int col, int row )
	{
		Character result = null;
		
		if( ( col>0 ) && ( col<=NUM_OF_COLUMNS )
			&& ( row>0 ) && ( row<=NUM_OF_ROWS ) )
		{
			result = _position[col][row];
		}

		return( result );
	}

	public void putPieceAtPositionBase( Character piece, int col, int row )
	{
		if( ( col<1 ) && ( col>NUM_OF_COLUMNS ) )
			throw( new RuntimeException( createCustomInternationalString( CONF_BAD_COLUMN_NUMBER,  col ) ) );

		if( ( row<1 ) && ( row>NUM_OF_ROWS ) )
			throw( new RuntimeException( createCustomInternationalString( CONF_BAD_ROW_NUMBER,  row ) ) );

/*
		if( _position[col][row] != null )
			throw( new ChessGamePositionException( "There was already a piece at position (" + col + "," + row + "). Piece: " + piece ) );
*/
		if( piece != null )
		{
			checkPieceCodeBase( piece );
		}

		_position[col][row] = piece;
	}

	public int getWhiteColor()
	{
		return( 0 );
	}

	public int getBlackColor()
	{
		return( 1 );
	}

	public int getColorOfPieceCode( char pieceCode )
	{
		int result = -1;

		if( pieceCode == getPieceType( pieceCode ) )
			result = getWhiteColor();
		else
			result = getBlackColor();

		return( result );
	}

	// PieceType is pieceCode to upperCase (as is encoded for White)
	public String getPieceType( String pieceCode )
	{
		StringBuilder pieceSb = new StringBuilder();
		pieceSb.append( pieceCode );

		String result = pieceSb.toString().toUpperCase();

		return( result );
	}

	// PieceType is pieceCode to upperCase (as is encoded for White)
	public static char getPieceType( char pieceCode )
	{
		StringBuilder pieceSb = new StringBuilder();
		pieceSb.append( pieceCode );

		char result = pieceSb.toString().toUpperCase().charAt(0);

		return( result );
	}

	public String getFlippedFenBoardStringBase()
	{
		StringBuilder sb = new StringBuilder();
		boolean flipBoard = true;
		buildFenBoardStringBase( sb, flipBoard );

		return( sb.toString() );
	}

	public String getFenBoardStringBase()
	{
		StringBuilder sb = new StringBuilder();
		boolean flipBoard = false;
		buildFenBoardStringBase( sb, flipBoard );

		return( sb.toString() );
	}

	public void buildFenBoardStringBase( StringBuilder sb, boolean flipBoard )
	{
		for( int row=NUM_OF_ROWS; row>0; row-- )
		{
			int effectiveRow = ( flipBoard ? NUM_OF_ROWS + 1 - row: row );
			int blanks = 0;
			if( row<NUM_OF_ROWS )	sb.append( "/" );
			for( int col=1; col<=NUM_OF_COLUMNS; col++ )
			{
				int effectiveCol = ( flipBoard ? NUM_OF_COLUMNS + 1 - col: col );
				Character piece = _position[effectiveCol][effectiveRow];

				if( piece == null )	blanks++;
				else
				{
					if( blanks > 0 )
					{
						sb.append( String.valueOf( blanks ) );
						blanks = 0;
					}
					checkPieceCodeBase( piece );
					sb.append( piece );
				}

				if( ( col == NUM_OF_COLUMNS ) && ( blanks > 0 ) )
				{
					sb.append( String.valueOf( blanks ) );
					blanks = 0;
				}
			}
		}
	}

	public boolean isPieceCode( String pieceCode )
	{
		boolean result = false;
		String pieceType = getPieceType( pieceCode );
		switch( pieceType )
		{
			case "K":
			case "Q":
			case "R":
			case "B":
			case "N":
			case "P":
				result = true;
		}

		return( result );
	}

	public void checkPieceCodeBase( char pieceCode )
	{
		char pieceType = getPieceType( pieceCode );
		if( ( pieceType != 'K' ) &&
			( pieceType != 'Q') &&
			( pieceType != 'R') &&
			( pieceType != 'B') &&
			( pieceType != 'N') &&
			( pieceType != 'P')	)
				throw( new RuntimeException( createCustomInternationalString( CONF_BAD_PIECE_TYPE, pieceType ) ) );
	}

	public <CC> void flipBoardArray( CC[][] array )
	{
		for( int jj=1; jj<=NUM_OF_COLUMNS; jj++ )
			for( int ii=1; ii<=4; ii++ )
			{
				CC piece = array[jj][ii];
				array[jj][ii] = array[9-jj][9-ii];
				array[9-jj][9-ii] = piece;
			}
	}

	public void flipBoard()
	{
		flipBoardArray( _position );
	}

	protected <CC> boolean arraysEqual( CC[][] arr1, CC[][] arr2, BiPredicate<CC,CC> equalsFunc )
	{
		boolean result = ( arr1 == arr2 );
		if( ! result && ( arr1 != null ) && ( arr2 != null ) )
		{
			result = true;
			for( int jj=0; result && (jj<NUM_OF_COLUMNS); jj++ )
				for( int ii=0; result && (ii<NUM_OF_ROWS); ii++ )
					result = equalsFunc.test( arr1[jj][ii], arr2[jj][ii] );
		}

		return( result );
	}

	@Override
	public boolean equals( Object thatObj )
	{
		boolean result = ( thatObj instanceof ChessGamePositionBase );

		if( result )
		{
			ChessGamePositionBase that = (ChessGamePositionBase) thatObj;
			result = ( this == that );

			if( ! result )
				result = arraysEqual( _position, that._position, (o1, o2) -> o1==o2 );
		}

		return( result );
	}

	protected static void registerInternationalizedStrings()
	{
		registerInternationalString(CONF_UNEXPECTED_NUMBER_OF_ROWS_PARSING_FEN, "Number of rows different from 8 parsing position of Fen String. Fen: '$1'" );
		registerInternationalString(CONF_UNRECOGNIZED_FEN_CHAR_PIECE, "Unrecognized fen char piece: $1 ['$2']" );
		registerInternationalString(CONF_UNEXPECTED_NUMBER_OF_COLUMNS_PARSING_FEN, "Error parsing Fen string. Row has more than 8 columns: ['$1']" );
		registerInternationalString(CONF_BAD_COLUMN_NUMBER, "Bad column number: $1" );
		registerInternationalString(CONF_BAD_ROW_NUMBER, "Bad row number: $1" );
		registerInternationalString(CONF_BAD_PIECE_TYPE, "Bad piece type: $1" );
	}

	public static void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	public String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}

	public String createCustomInternationalString(String label, Object... args) {
		return( _internationalizedStringConf.createCustomInternationalString( label, args ) );
	}
}
