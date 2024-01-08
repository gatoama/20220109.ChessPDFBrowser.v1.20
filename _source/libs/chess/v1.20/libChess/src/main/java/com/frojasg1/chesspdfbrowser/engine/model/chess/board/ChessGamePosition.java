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
package com.frojasg1.chesspdfbrowser.engine.model.chess.board;

import com.frojasg1.chesspdfbrowser.engine.configuration.ChessStringsConf;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessGamePositionException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessModelException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessParserException;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.ChessPiece;
import com.frojasg1.chesspdfbrowser.engine.position.ChessGamePositionBase;
import com.frojasg1.general.ExecutionFunctions;
import java.util.Objects;

/**
 *
 * @author Usuario
 */
public class ChessGamePosition extends ChessGamePositionBase
{
	public static final String INITIAL_POSITION_FEN_STRING = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	protected static final String ONLY_KINGS_POSITION_FEN_STRING = "4k3/8/8/8/8/8/8/4K3 w - - 0 1";
	
	protected static ChessGamePosition _initialPosition = null;
	protected static ChessGamePosition _onlyKingsPosition = null;

	public static final ChessGameMove _saWhiteCastleKingSideMove = new ChessGameMove( 5, 1, 7, 1 );
	public static final ChessGameMove _saWhiteCastleQueenSideMove = new ChessGameMove( 5, 1, 3, 1 );
	public static final ChessGameMove _saBlackCastleKingSideMove = new ChessGameMove( 5, 8, 7, 8 );
	public static final ChessGameMove _saBlackCastleQueenSideMove = new ChessGameMove( 5, 8, 3, 8 );

	protected Boolean _isWhitesTurn = null;

	protected Boolean _whiteCanCastleKingSide = null;
	protected Boolean _whiteCanCastleQueenSide = null;
	protected Boolean _blackCanCastleKingSide = null;
	protected Boolean _blackCanCastleQueenSide = null;

	protected Integer _numberOfPliesWithoutProgress = null;
	protected int _incrementedNumberOfPliesWithoutProgress = 0;

	protected Integer _moveNumber = null;
	protected int _incrementedNumberOfPlies = 0;

	protected Boolean _isEnPassantMove = null;
	protected int _enPassantColumn = -1;
	protected int _enPassantRow = -1;

	public ChessGamePosition()
	{
		super();
	}

	public ChessGamePosition( String fenPosition ) throws ChessParserException, ChessGamePositionException, ChessModelException
	{
		super();
		if( fenPosition != null )
			setFenPosition( fenPosition );
		else
			setInitialPosition();
	}

	public ChessGamePosition( ChessBoard cb )
	{
		super();
		initialize( cb );
	}

	public boolean isStandardInitialPosition()
	{
		String fenString = ExecutionFunctions.instance().safeFunctionExecution( () -> getFenString() );
		return( Objects.equals( INITIAL_POSITION_FEN_STRING, fenString ) );
	}

	public int getPlyNumber()
	{
		int moveNumber = 1;
		if( _moveNumber != null )
			moveNumber = _moveNumber;

		int turnPly = 0;
		if( ! getIsWhitesTurn() )
			turnPly = 1;

		int plyNumber = (moveNumber * 2) - 1 + turnPly;

		return( plyNumber );
	}

	protected void initialize( ChessBoard cb )
	{
		setEmptyPosition();

		for( int ii=1; ii<=ChessBoard.NUM_OF_COLUMNS; ii++ )
			for( int jj=1; jj<=ChessBoard.NUM_OF_ROWS; jj++ )
			{
				ChessPiece cp = null;

				try
				{
					cp = cb.getPiece(ii, jj);
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}

				if( cp != null )
					putPieceAtPositionBase( cp.getPieceCode().charAt(0), ii, jj );
			}
		
		updateCastleAttributesFromPosition();
	}
	
	protected void updateCastleAttributesFromPosition()
	{
		if( !isPieceAtPosition( 5, 1, 'K' ) )
		{
			this.setWhiteCanCastleKingSide( false );
			this.setWhiteCanCastleQueenSide( false );
		}
		else
		{
			if( !isPieceAtPosition( 1, 1, 'R' ) )
				this.setWhiteCanCastleQueenSide( false );

			if( !isPieceAtPosition( 8, 1, 'R' ) )
				this.setWhiteCanCastleKingSide( false );
		}

		if( !isPieceAtPosition( 5, 8, 'k' ) )
		{
			this.setBlackCanCastleKingSide( false );
			this.setBlackCanCastleQueenSide( false );
		}
		else
		{
			if( !isPieceAtPosition( 1, 8, 'r' ) )
				this.setBlackCanCastleQueenSide( false );

			if( !isPieceAtPosition( 8, 8, 'r' ) )
				this.setBlackCanCastleKingSide( false );
		}
	}

	public Boolean getWhiteCanCastleKingSide()						{	return( _whiteCanCastleKingSide );	}
	public Boolean getWhiteCanCastleQueenSide()						{	return( _whiteCanCastleQueenSide );	}
	public Boolean getBlackCanCastleKingSide()						{	return( _blackCanCastleKingSide );	}
	public Boolean getBlackCanCastleQueenSide()						{	return( _blackCanCastleQueenSide );	}

	public Integer getNumberOfPliesWithoutProgress()				{	return( _numberOfPliesWithoutProgress ); }
	public Integer getMoveNumber()									{	return( _moveNumber ); }

	public Boolean isEnPassantMove()								{	return( _isEnPassantMove );	}

	public int getEnPassantColumn()									{	return( _enPassantColumn );	}
	public int getEnPassantRow()									{	return( _enPassantRow );	}

	public int getIncrementedNumberOfPliesWithoutProgress()			{	return( _incrementedNumberOfPliesWithoutProgress );	}
	public int getIncrementedNumberOfPlies()						{	return( _incrementedNumberOfPlies );	}

	public void setNumberOfPliesWithoutProgress( int value )		{	_numberOfPliesWithoutProgress = value; }
	public void resetNumberOfPliesWithoutProgress( )				{	_numberOfPliesWithoutProgress = 0; }
	public void incrementNumberOfPliesWithoutProgress()
	{
		if( _numberOfPliesWithoutProgress != null )
			_numberOfPliesWithoutProgress++;
		else
			_incrementedNumberOfPliesWithoutProgress ++;
	}

	public void setWhiteCanCastleKingSide( boolean value )						{	_whiteCanCastleKingSide = value;	}
	public void setWhiteCanCastleQueenSide( boolean value )						{	_whiteCanCastleQueenSide = value;	}
	public void setBlackCanCastleKingSide( boolean value )						{	_blackCanCastleKingSide = value;	}
	public void setBlackCanCastleQueenSide( boolean value )						{	_blackCanCastleQueenSide = value;	}

	public void setMoveNumber( int value )							{	_moveNumber = value;	}
	
	public void setPliesWithoutProgress( int value )				{	_numberOfPliesWithoutProgress = value;	}
	
	public void incrementNumberOfPly()
	{
		if( ( _isWhitesTurn != null ) && ( _moveNumber != null ) )
		{
			if( !_isWhitesTurn )	_moveNumber++;
		}

		_incrementedNumberOfPlies++;
		
		if( _isWhitesTurn != null )
		{
			_isWhitesTurn = ! _isWhitesTurn;
		}
	}

	public boolean canCastle( ChessGameMove cgm )
	{
		Boolean result = false;

		if( cgm.equals( _saWhiteCastleKingSideMove ) )			result = _whiteCanCastleKingSide;
		else if( cgm.equals( _saWhiteCastleQueenSideMove ) )	result = _whiteCanCastleQueenSide;
		else if( cgm.equals( _saBlackCastleKingSideMove ) )		result = _blackCanCastleKingSide;
		else if( cgm.equals( _saBlackCastleQueenSideMove ) )	result = _blackCanCastleQueenSide;

		if( result == null )	result = true;

		return( result );
	}

	public void setFenPosition( String fenPosition ) throws ChessParserException, ChessGamePositionException, ChessModelException
	{
		setEmptyPosition();

		parseFenPosition( fenPosition );
	}

	@Override
	public void setEmptyPosition()
	{
		super.setEmptyPosition();

		_isWhitesTurn = null;

		_whiteCanCastleKingSide = null;
		_whiteCanCastleQueenSide = null;
		_blackCanCastleKingSide = null;
		_blackCanCastleQueenSide = null;

		_numberOfPliesWithoutProgress = null;
		_incrementedNumberOfPliesWithoutProgress = 0;

		_moveNumber = null;
		_incrementedNumberOfPlies = 0;

		_isEnPassantMove = null;
		_enPassantColumn = -1;
		_enPassantRow = -1;
	}

	protected void parseFenPosition( String fenPosition ) throws ChessParserException, ChessGamePositionException, ChessModelException
	{
		String trimmedString = fenPosition.trim();
		
		String[] fields = trimmedString.split( "\\s" );

		if( (fields.length < 1 ) || ( fields.length > 6 ) )
			throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_NUM_OF_FIELDS_INCORRECT_FOR_FEN ) +
											": " + fenPosition ) );

		// board
		parseBoardFenPosition( fields[0] );

		// turn
		if( fields.length > 1 )
		{
			if( "w".equals( fields[1].toLowerCase() ) )
				setIsWhitesTurn();
			else if( "b".equals( fields[1].toLowerCase() ) )
				setIsBlacksTurn();
			else
				throw( new ChessParserException( String.format( getChessStrConf().getProperty( ChessStringsConf.CONF_COLOR_TO_MOVE_NOT_RECOGNIZED_FEN ),
																fields[1],
																fenPosition )   ) );
		}

		// castling
		if( fields.length > 2 )
		{
			parseCastling( fields[2] );
		}

		// en passant
		if( fields.length > 3 )
		{
			String enPassant = fields[3].toLowerCase();
			if( enPassant.equals( "-" ) )
			{
				this.setIsNotEnPassant();
			}
			else
			{
				if( enPassant.length() == 2 )
				{
					int col = enPassant.charAt(0) - 'a' + 1;
					int row = enPassant.charAt(1) - '0';

					try
					{
						setEnPassant( col, row );
					}
					catch( Throwable th )
					{
						throw( new ChessParserException( String.format( getChessStrConf().getProperty( ChessStringsConf.CONF_CELL_FOR_ENPASSANT_NOT_VALID_FEN ),
																fields[3],
																fenPosition )   ) );
					}
				}
			}
		}

		// plies without progress
		if( fields.length > 4 )
		{
			try
			{
				int numberOfPliesWithoutProgress = Integer.parseInt( fields[4] );
				setNumberOfPliesWithoutProgress( numberOfPliesWithoutProgress );
			}
			catch( Throwable th )
			{
				throw( new ChessParserException( String.format( getChessStrConf().getProperty( ChessStringsConf.CONF_BAD_NUMBER_OF_PLIES_WITHOUT_PROGRESS_FEN ),
																fields[4],
																fenPosition )   ) );
			}
		}
		
		// number of move
		if( fields.length > 5 )
		{
			try
			{
				int moveNumber = Integer.parseInt( fields[5] );
				setMoveNumber( moveNumber );
			}
			catch( Throwable th )
			{
				throw( new ChessParserException( String.format( getChessStrConf().getProperty( ChessStringsConf.CONF_NUMBER_OF_PLY_NOT_A_NUMBER ),
																fields[5],
																fenPosition )   ) );
			}
		}
	}

	public void parseCastling( String castlingStr ) throws ChessParserException
	{
		setWhiteCanCastleKingSide( false );
		setWhiteCanCastleQueenSide( false );
		setBlackCanCastleKingSide( false );
		setBlackCanCastleQueenSide( false );

		if( ! castlingStr.equals( "-" ) )
		{
			for( int ii=0; ii<castlingStr.length(); ii++ )
			{
				char current = castlingStr.charAt(ii);
				if( ( current != '-' ) && ( castlingStr.indexOf( current, ii+1 ) > ii ) )
					throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_REPEATED_CHARACTER_IN_CASTLING_FIELD_FEN ) +
													": " + castlingStr ) );

				if( current == 'K' )	_whiteCanCastleKingSide = true;
				else if( current == 'Q' )	_whiteCanCastleQueenSide = true;
				else if( current == 'k' )	_blackCanCastleKingSide = true;
				else if( current == 'q' )	_blackCanCastleQueenSide = true;
				else if( current != '-' )
					throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_UNRECOGNIZED_CHAR_IN_CASTLING_FIELD ) +
													": " + castlingStr ) );
			}
		}
	}

	public void parseBoardFenPosition( String boardFenPosition ) throws ChessParserException, ChessGamePositionException, ChessModelException
	{
		try
		{
			parseBoardFenPositionBase(boardFenPosition);
		}
		catch( Exception ex )
		{
			throw( new ChessParserException( ex.getMessage() ) );
		}
	}

	public void putPieceAtPosition( Character piece, int col, int row ) throws ChessGamePositionException, ChessModelException
	{
		try
		{
			putPieceAtPositionBase(piece, col, row);
		}
		catch( Exception ex )
		{
			throw( new ChessGamePositionException( ex.getMessage() ) );
		}
	}

	public int getWhiteColor()
	{
		return( ChessPiece.WHITE );
	}

	public int getBlackColor()
	{
		return( ChessPiece.BLACK );
	}
	
	public void setInitialPosition()
	{
		try
		{
			setFenPosition( INITIAL_POSITION_FEN_STRING );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	public void setOnlyKingsPosition()
	{
		try
		{
			setFenPosition( ONLY_KINGS_POSITION_FEN_STRING );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	public void setIsWhitesTurn()
	{
		_isWhitesTurn = true;
	}

	public void setIsBlacksTurn()
	{
		_isWhitesTurn = false;
	}

	public Boolean getIsWhitesTurn()
	{
		return( ( _isWhitesTurn == null ) || _isWhitesTurn );
	}

	public static ChessGamePosition getInitialPosition()
	{
		if( _initialPosition == null )
		{
			_initialPosition = new ChessGamePosition();
			_initialPosition.setInitialPosition();
		}

		return( _initialPosition );
	}

	public static ChessGamePosition getOnlyKingsPosition()
	{
		if( _onlyKingsPosition == null )
		{
			_onlyKingsPosition = new ChessGamePosition();
			_onlyKingsPosition.setOnlyKingsPosition();
		}
		return( _onlyKingsPosition );
	}

	public void setEnPassant( int column, int row ) throws ChessGamePositionException
	{
		if( (column<1) || (column>ChessBoard.NUM_OF_COLUMNS) || (row<1) || (row>ChessBoard.NUM_OF_ROWS ) )
			throw( new ChessGamePositionException( String.format( getChessStrConf().getProperty( ChessStringsConf.CONF_ENPASSANT_CELL_OUT_OF_BOUNDS ),
														column,
														row )   ) );

		_isEnPassantMove = true;
		_enPassantColumn = column;
		_enPassantRow = row;
	}

	public void checkAndSetEnPassant( int column, int row ) throws ChessGamePositionException
	{
		Boolean previousIsEnPassantMove = _isEnPassantMove;
		int previousEnPassantColumn = _enPassantColumn;
		int previousEnPassantRow = _enPassantRow;

		try
		{
			setEnPassant( column, row );
			if( !enPassantMoveMatches() )
				throw( new RuntimeException( String.format( getChessStrConf().getProperty( ChessStringsConf.CONF_ENPASSANT_MOVE_DOES_NOT_MATCH ),
														getIsWhitesTurn(),
														ChessGameMove.getSquareString( _enPassantColumn, _enPassantRow ) )  ) );
		}
		catch( Throwable th )
		{
			_isEnPassantMove = previousIsEnPassantMove;
			_enPassantColumn = previousEnPassantColumn;
			_enPassantRow = previousEnPassantRow;
			
			throw( new ChessGamePositionException( th.getMessage() ) );
		}
	}
	
	public void setIsNotEnPassant()
	{
		_isEnPassantMove = false;
	}

	public String getEnPassantSquare_str()
	{
		String result = "";
		
		if( ( _isEnPassantMove != null ) && _isEnPassantMove )
			result = ChessGameMove.getSquareString(_enPassantColumn, _enPassantRow);

		return( result );
	}
	
	public ResultOfComparison compare( ChessGamePosition other )
	{
		ResultOfComparison result = new ResultOfComparison( this, other );

		return( result );
	}

	@Override
	public boolean equals( Object obj )
	{
		boolean result = false;
		if( obj instanceof ChessGamePosition )
		{
			ChessGamePosition other = (ChessGamePosition) obj;
			ResultOfComparison resultOfComparison = compare( other );
			
			Boolean resultBool = resultOfComparison.isEqualSummary();
			if( resultBool != null )
				result = resultBool;
		}

		return( result );
	}

	public String getFenString() throws ChessModelException
	{
		updateCastleAttributesFromPosition();

		return( getFenString_internal() );
	}

	protected String getFenString_internal() throws ChessModelException
	{
		StringBuilder sb = new StringBuilder();

		buildFenBoardString( sb );

		if( _isWhitesTurn != null )
		{
			sb.append(" ");
			if( _isWhitesTurn )		sb.append( "w" );
			else					sb.append( "b" );
			
			if( buildFenCastlingString( sb ) )
			{
				if( buildFenEnPassantString( sb ) )
				{
					int numOfPlieswp = _incrementedNumberOfPliesWithoutProgress;
					if( _numberOfPliesWithoutProgress != null )
						numOfPlieswp = _numberOfPliesWithoutProgress;

//					if( _numberOfPliesWithoutProgress != null )
					{
						sb.append( " " + numOfPlieswp );

						int numOfPly = 1 + _incrementedNumberOfPlies + ( _isWhitesTurn ? 0 : 1 );
						int moveNumber = ( numOfPly + 1 ) / 2;
						if( _moveNumber != null )
						{
							moveNumber = _moveNumber;
						}

						sb.append( " " + moveNumber );
					}
				}
			}
		}

		return( sb.toString() );
	}

	public void buildFenBoardString( StringBuilder sb ) throws ChessModelException
	{
		buildFenBoardString( sb, false );
	}

	public void buildFenBoardString( StringBuilder sb, boolean flipBoard ) throws ChessModelException
	{
		try
		{
			buildFenBoardStringBase(sb, flipBoard);
		}
		catch( Exception ex )
		{
			throw( new ChessModelException( ex.getMessage() ) );
		}
	}

	public boolean buildFenCastlingString( StringBuilder sb )
	{
		boolean result = true;
/*
		result = (_whiteCanCastleKingSide!=null) &&
				(_whiteCanCastleQueenSide!=null) &&
				(_blackCanCastleKingSide!=null) &&
				(_blackCanCastleQueenSide!=null);
*/
//		if( result )
		{
			sb.append( " " );
			if( (_whiteCanCastleKingSide!=null) &&
				(_whiteCanCastleQueenSide!=null) &&
				(_blackCanCastleKingSide!=null) &&
				(_blackCanCastleQueenSide!=null) &&
				!_whiteCanCastleKingSide &&
				!_whiteCanCastleQueenSide &&
				!_blackCanCastleKingSide &&
				!_blackCanCastleQueenSide )
			{
				sb.append( "-" );
			}
			else
			{
				if( (_whiteCanCastleKingSide == null ) || _whiteCanCastleKingSide )
					sb.append( "K" );
				if( (_whiteCanCastleQueenSide == null ) || _whiteCanCastleQueenSide )
					sb.append( "Q" );
				if( (_blackCanCastleKingSide == null ) || _blackCanCastleKingSide )
					sb.append( "k" );
				if( (_blackCanCastleQueenSide == null ) || _blackCanCastleQueenSide )
					sb.append( "q" );
			}
		}

		return( result );
	}

	public boolean buildFenEnPassantString( StringBuilder sb )
	{
		boolean result = false;
		
//		if( _isEnPassantMove != null )
		{
			result = true;
			sb.append( " " );
			if( ( _isEnPassantMove != null ) && _isEnPassantMove )
			{
				if( enPassantMoveMatches() )
				{
					sb.append( ChessGameMove.getSquareString( _enPassantColumn, _enPassantRow ) );
				}
				else
				{
					throw( new RuntimeException( String.format( getChessStrConf().getProperty( ChessStringsConf.CONF_ENPASSANT_MOVE_DOES_NOT_MATCH ),
														getIsWhitesTurn(),
														ChessGameMove.getSquareString( _enPassantColumn, _enPassantRow ) )     ) );
				}
			}
			else
			{
				sb.append( "-" );
			}
		}

		return( result );
	}

	protected boolean enPassantMoveMatches()
	{
		boolean result = false;
		if( (_enPassantColumn>0) && (_enPassantColumn<=ChessBoard.NUM_OF_COLUMNS) &&
			(_enPassantRow>0) && (_enPassantRow<=ChessBoard.NUM_OF_ROWS ) )
		{
			Boolean wt = getIsWhitesTurn();
			if( wt != null )
			{
				Character piece = null;
				if( wt )
				{
					piece = getCharacterAtPosition( _enPassantColumn, 5 );
					result = ( _enPassantRow == 6 ) && ( piece != null ) && ( piece == 'p' );
				}
				else
				{
					piece = getCharacterAtPosition( _enPassantColumn, 4 );
					result = ( _enPassantRow == 3 ) && ( piece != null ) && ( piece == 'P' );
				}
			}
		}
		return( result );
	}

	public void checkPieceCode( char pieceCode ) throws ChessModelException
	{
		try
		{
			checkPieceCodeBase(pieceCode);
		}
		catch( Exception ex )
		{
			throw( new ChessModelException( ex.getMessage() ) );
		}
	}

	public boolean canBeBaseFenString()
	{
/*
		ChessGamePosition that = new ChessGamePosition();
		that.setFenPositionBase( this.getFenBoardStringBase() );

		String thisFen = ExecutionFunctions.instance().safeFunctionExecution( () -> getFenString() );
		String thatFen = ExecutionFunctions.instance().safeFunctionExecution( () -> that.getFenString() );

		return( Objects.equals( thisFen, thatFen ) );
*/
		return( ( ( getMoveNumber() == null ) || ( getMoveNumber() == 1 ) ) &&
				( ( getIsWhitesTurn() == null ) || getIsWhitesTurn() ) &&
				! isStandardInitialPosition() );
	}

	protected boolean isInitialCastlingValue( Boolean value )
	{
		return( ( value == null ) || value );
	}

	/**
	 * Class result of the comparison of two objects of class ChessGamePosition
	 */
	
	public class ResultOfComparison
	{
		protected Boolean _isEqualPositionOfPieces = null;
		protected Boolean _isEqualTurn = null;
		protected Boolean _isEqualWhiteCanCastleKingSide = null;
		protected Boolean _isEqualWhiteCanCastleQueenSide = null;
		protected Boolean _isEqualBlackCanCastleKingSide = null;
		protected Boolean _isEqualBlackCanCastleQueenSide = null;
		protected Boolean _isEqualNumberOfPliesWithoutProgress = null;
		protected Boolean _isEqualMoveNumber = null;
		protected boolean _isEqualIncrementOfNumberOfPlies = false;
		protected Boolean _isEqualIsEnPassantMove = null;
		protected Boolean _isEqualEnPassantColumn = null;
		protected Boolean _isEqualEnPassantRow = null;

		protected Boolean _isEqualSummary = null;
		protected Boolean _isEqualEnPassantMoveSummary = null;
		protected Boolean _isEqualMoveNumberSummary = null;
		protected Boolean _isEqualCastlingSummary = null;

		public ResultOfComparison( ChessGamePosition one, ChessGamePosition other )
		{
			evaluateComparison( one, other );
		}

		public Boolean isEqualSummary()						{ return( _isEqualSummary );	}
		public Boolean isEqualEnPassantSummary()			{ return( _isEqualEnPassantMoveSummary );	}
		public Boolean isEqualMoveNumberSummary()			{ return( _isEqualMoveNumberSummary );	}
		public Boolean isEqualCastlingSummary()				{ return( _isEqualCastlingSummary );	}

		public Boolean isEqualPositionOfPieces()			{ return( _isEqualPositionOfPieces );	}
		public Boolean isEqualTurn()						{ return( _isEqualTurn );	}
		public Boolean isEqualWhiteCanCastleKingSide()		{ return( _isEqualWhiteCanCastleKingSide );	}
		public Boolean isEqualWhiteCanCastleQueenSide()		{ return( _isEqualWhiteCanCastleQueenSide );	}
		public Boolean isEqualBlackCanCastleKingSide()		{ return( _isEqualBlackCanCastleKingSide );	}
		public Boolean isEqualBlackCanCastleQueenSide()		{ return( _isEqualBlackCanCastleQueenSide );	}
		public Boolean isEqualNumberOfPliesWithoutProgress(){ return( _isEqualNumberOfPliesWithoutProgress );	}
		public Boolean isEqualMoveNumber()					{ return( _isEqualMoveNumber );	}
		public Boolean isEqualIncrementOfNumberPlies()		{ return( _isEqualIncrementOfNumberOfPlies );	}
		public Boolean isEqualIsEnPassantMove()				{ return( _isEqualIsEnPassantMove );	}
		public Boolean isEqualEnPassantColumn()				{ return( _isEqualEnPassantColumn );	}
		public Boolean isEqualEnPassantRow()				{ return( _isEqualEnPassantRow );	}
		
		protected void evaluateComparison( ChessGamePosition one, ChessGamePosition other )
		{
			if( ( one != null ) && ( other != null ) )
			{
				_isEqualTurn = isEqualThan( one.getIsWhitesTurn(), other.getIsWhitesTurn() );

				_isEqualWhiteCanCastleKingSide = isEqualThan( one.getWhiteCanCastleKingSide(), other.getWhiteCanCastleKingSide() );
				_isEqualWhiteCanCastleQueenSide = isEqualThan( one.getWhiteCanCastleQueenSide(), other.getWhiteCanCastleQueenSide() );
				_isEqualBlackCanCastleKingSide = isEqualThan( one.getBlackCanCastleKingSide(), other.getBlackCanCastleKingSide() );
				_isEqualBlackCanCastleQueenSide = isEqualThan( one.getBlackCanCastleQueenSide(), other.getBlackCanCastleQueenSide() );

				_isEqualNumberOfPliesWithoutProgress = isEqualThan( one.getNumberOfPliesWithoutProgress(), other.getNumberOfPliesWithoutProgress() );
				if( _isEqualNumberOfPliesWithoutProgress == null )
				{
					_isEqualNumberOfPliesWithoutProgress = ( one.getIncrementedNumberOfPliesWithoutProgress() ==
															other.getIncrementedNumberOfPliesWithoutProgress() );
				}

				_isEqualIncrementOfNumberOfPlies = one.getIncrementedNumberOfPlies() == other.getIncrementedNumberOfPlies();
				_isEqualMoveNumber = isEqualThan( one.getMoveNumber(), other.getMoveNumber() );
				setIsEqualEnPassant( one, other );

				_isEqualPositionOfPieces = false;
				if( one._position.length == other._position.length )
				{
					_isEqualPositionOfPieces = true;
					for( int col=0; _isEqualPositionOfPieces && (col<one._position.length); col++ )
					{
						_isEqualPositionOfPieces = ( one._position[col].length == other._position[col].length );
						for( int row = 0; _isEqualPositionOfPieces && (row<one._position[col].length); row++ )
						{
							_isEqualPositionOfPieces =	(one.getCharacterAtPosition( col, row )==null) &&
														(other.getCharacterAtPosition( col, row )==null) ||
														(one.getCharacterAtPosition( col, row )!=null) &&
														( one.getCharacterAtPosition( col, row ).equals( other.getCharacterAtPosition( col, row ) ) );
						}
					}
				}
				
				evaluateSummary();
			}
			else
			{
				_isEqualSummary = ( ( one == null ) && ( other == null ) );
			}
		}

		protected void evaluateSummary()
		{
			_isEqualMoveNumberSummary = ( _isEqualMoveNumber != null ) && _isEqualMoveNumber && _isEqualIncrementOfNumberOfPlies ||
										( _isEqualMoveNumber == null ) && _isEqualIncrementOfNumberOfPlies;

			_isEqualCastlingSummary = and( _isEqualWhiteCanCastleKingSide, _isEqualWhiteCanCastleQueenSide );
			_isEqualCastlingSummary = and( _isEqualCastlingSummary, _isEqualBlackCanCastleKingSide );
			_isEqualCastlingSummary = and( _isEqualCastlingSummary, _isEqualBlackCanCastleQueenSide );

			_isEqualSummary = and(_isEqualPositionOfPieces, _isEqualTurn );
			_isEqualSummary = and( _isEqualSummary, _isEqualCastlingSummary );
//			_isEqualSummary = and( _isEqualSummary, _isEqualNumberOfPliesWithoutProgress );
//			_isEqualSummary = and( _isEqualSummary, _isEqualIncrementOfNumberOfPlies );
			_isEqualSummary = and( _isEqualSummary, _isEqualEnPassantMoveSummary );
		}

		protected Boolean and( Boolean one, Boolean other )
		{
			Boolean result = null;

			if( ( one != null ) && ( other != null ) )
			{
				result = one && other;
			}

			return( result );
		}

		protected Boolean isEqualThan( Object one, Object other )
		{
			Boolean result = null;
			if( one == other ) result = true;
			else
			{
				if( ( one != null ) && ( other != null ) )
				{
					result = ( one.equals(other) );
				}
			}
			return( result );
		}

		protected void setIsEqualEnPassant( ChessGamePosition one, ChessGamePosition other )
		{
			_isEqualIsEnPassantMove = isEqualThan( one.isEnPassantMove(), other.isEnPassantMove() );
			
			if( _isEqualIsEnPassantMove != null )
			{
				if( _isEqualIsEnPassantMove )
				{
					if( ( one.isEnPassantMove() != null ) && one.isEnPassantMove() )
					{
						_isEqualEnPassantColumn = ( one.getEnPassantColumn() == other.getEnPassantColumn() );
						_isEqualEnPassantRow = ( one.getEnPassantRow() == other.getEnPassantRow() );

						_isEqualEnPassantMoveSummary = _isEqualEnPassantColumn && _isEqualEnPassantRow;
					}
					else
					{
						_isEqualEnPassantMoveSummary = true;
					}
				}
				else
				{
					_isEqualEnPassantMoveSummary = false;
				}
			}
		}
	}

	protected static ChessStringsConf getChessStrConf()
	{
		return( ChessStringsConf.instance() );
	}
}
