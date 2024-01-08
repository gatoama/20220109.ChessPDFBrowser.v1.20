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
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessMoveException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessPieceCreationException;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Bishop;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.ChessPiece;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.King;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Knight;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Pawn;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Queen;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Rook;
import com.frojasg1.general.number.IntegerFunctions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Usuario
 */
public class ChessBoard implements Serializable
{
	public static final int NUM_OF_ROWS = 8;
	public static final int NUM_OF_COLUMNS = 8;

	public static final int NUM_OF_PIECES_PER_PLAYER = 16;
	public static final int INITIAL_INDEX_FOR_PAWNS = 8;

	protected LinkedList<ChessGameMove> _moves = null;
	protected LinkedList<ChessGamePosition> _positions = null;
	protected ChessGamePosition _newPosition = null;

	protected ChessPiece[] _whitePieces = null;
	protected ChessPiece[] _blackPieces = null;

	protected LinkedList<ChessPiece> _capturedWhitePieces = null;
	protected LinkedList<ChessPiece> _capturedBlackPieces = null;

	protected ChessPiece[][] _board = null;

//	protected boolean _isWhitesTurn = true;

	protected ChessGameResult _chessGameResult = ChessGameResult.GAME_CONTINUES;

	protected boolean _currentNodeIsIllegal = false;

	public ChessBoard()
	{
		init();
	}

	protected void init()
	{
		_moves = new LinkedList<ChessGameMove>();
		_positions = new LinkedList<ChessGamePosition>();

		_whitePieces = new ChessPiece[NUM_OF_PIECES_PER_PLAYER];
		_blackPieces = new ChessPiece[NUM_OF_PIECES_PER_PLAYER];

		_capturedWhitePieces = new LinkedList<ChessPiece>();
		_capturedBlackPieces = new LinkedList<ChessPiece>();

		_board = new ChessPiece[NUM_OF_COLUMNS+1][];

		for( int ii=0; ii<=NUM_OF_COLUMNS; ii++ )
		{
			_board[ii] = new ChessPiece[NUM_OF_ROWS+1];
		}

		_currentNodeIsIllegal = false;
	}

	public void setCurrentNodeIsIllegal( boolean value )
	{
		if( value )
		{
			init();
		}
		
		_currentNodeIsIllegal = value;
	}

	public boolean isCurrentNodeIllegal()
	{
		return( _currentNodeIsIllegal );
	}

	public void setInitialPosition() throws ChessGamePositionException, ChessPieceCreationException
	{
		ChessGamePosition cgp = new ChessGamePosition();
		cgp.setInitialPosition();
		
		setPosition( cgp );
	}

	public int getColorToPlay()
	{
		return( getColorToPlay( getIsWhitesTurn() ) );
	}

	public int getColorNotToPlay()
	{
		return( getColorToPlay( ! getIsWhitesTurn() ) );
	}

	protected int getColorToPlay( boolean playsWhite )
	{
		int result = ChessPiece.BLACK;
		if( getIsWhitesTurn() ) result = ChessPiece.WHITE;

		return( result );
	}
	
	protected void putPieceInPawnsPlace( ChessPiece[] arrayOfPieces, ChessPiece piece ) throws ChessPieceCreationException
	{
		boolean putCorrectly = false;
		
		for( int ii=INITIAL_INDEX_FOR_PAWNS; (ii<NUM_OF_PIECES_PER_PLAYER) && !putCorrectly; ii++ )
		{
			if( arrayOfPieces[ii] == null )
				putCorrectly = putPieceInArray( arrayOfPieces, piece, ii, true );
		}
		
		if( ! putCorrectly )
			throw( new ChessPieceCreationException( String.format( getChessStrConf().getProperty( ChessStringsConf.CONF_ALL_PAWN_POSITIONS_FULL ),
																	piece.getPieceCode() )  ) );
	}

	
	protected boolean putPieceInArray( ChessPiece[] arrayOfPieces, ChessPiece piece, int index, boolean throwExceptionIfItIsOccupied ) throws ChessPieceCreationException
	{
		boolean result = false;

		if( arrayOfPieces[index] == null )
		{
			arrayOfPieces[index] = piece;
			result = true;
		}
		else if( throwExceptionIfItIsOccupied )
			throw( new ChessPieceCreationException( String.format( getChessStrConf().getProperty( ChessStringsConf.CONF_PIECE_ALREADY_PUT ),
																	piece.getPieceCode() ) ) );

		return( result );
	}
	
	public void putPieceInPiecesArray( ChessPiece piece ) throws ChessPieceCreationException
	{
		boolean correctlyPut = false;
		ChessPiece[] arrayOfPieces = getOwnPieces( piece );
		
		if( piece instanceof King )
		{
			correctlyPut = putPieceInArray( arrayOfPieces, piece, 0, true );
		}
		else if( piece instanceof Queen )
		{
			correctlyPut = putPieceInArray( arrayOfPieces, piece, 1, false );
		}
		else if( piece instanceof Rook )
		{
			correctlyPut = putPieceInArray( arrayOfPieces, piece, 2, false );
			if( ! correctlyPut ) correctlyPut = putPieceInArray( arrayOfPieces, piece, 3, false );
		}
		else if( piece instanceof Bishop )
		{
			correctlyPut = putPieceInArray( arrayOfPieces, piece, 4, false );
			if( ! correctlyPut ) correctlyPut = putPieceInArray( arrayOfPieces, piece, 5, false );
		}
		else if( piece instanceof Knight )
		{
			correctlyPut = putPieceInArray( arrayOfPieces, piece, 6, false );
			if( ! correctlyPut ) correctlyPut = putPieceInArray( arrayOfPieces, piece, 7, false );
		}
		
		if( ! correctlyPut )
		{
			putPieceInPawnsPlace( arrayOfPieces, piece );
		}
	}

	public void setPosition( ChessGamePosition cgp ) throws ChessGamePositionException, ChessPieceCreationException
	{
		init();

		for( int jj=1; jj<=NUM_OF_COLUMNS; jj++ )
			for( int ii=1; ii<=NUM_OF_ROWS; ii++ )
			{
				Character pieceCodeChar = cgp.getPieceAtPosition(jj, ii);
				ChessPiece piece = null;
				if( pieceCodeChar != null )
				{
					String pieceCode = pieceCodeChar.toString();
					piece = ChessPiece.createPiece(this, pieceCode);
					putPieceInPiecesArray( piece );
				}
				setPieceInSquare( piece, jj, ii );
			}

		_newPosition = cgp;
		forwardPosition();
	}

	public void doMovesFromInitialPosition( ChessGamePosition initialPosition, List<ChessGameMove> sMoves )
		throws ChessMoveException, ChessPieceCreationException, ChessGamePositionException
	{
		setPosition( initialPosition );
		_chessGameResult = getChessGameResult_internal();
		doMoves(sMoves);
	}

	public void doMoves( List<ChessGameMove> sMoves ) throws ChessMoveException, ChessPieceCreationException
	{
		if( sMoves != null )
		{
			Iterator<ChessGameMove> it = sMoves.iterator();

			while( it.hasNext() )
			{
				ChessGameMove cgm = it.next();
				move( cgm );
			}
		}
	}

	public ChessPiece[] getPieces( int color )
	{
		ChessPiece[] result = null;

		if( color == ChessPiece.WHITE )
			result = _whitePieces;
		else if( color == ChessPiece.BLACK )
			result = _blackPieces;

		return( result );
	}
	
	public ChessPiece[] getOwnPieces( ChessPiece cp )
	{
		return( getPieces( cp.getColor() ) );
	}

	public ChessPiece[] getRivalPieces( ChessPiece cp )
	{
		return( getPieces( cp.getOpositeColor() ) );
	}

	public LinkedList<ChessPiece> getCapturedPieces( int color )
	{
		LinkedList<ChessPiece> result = null;

		if( color == ChessPiece.WHITE )
			result = _capturedWhitePieces;
		else if( color == ChessPiece.BLACK )
			result = _capturedBlackPieces;

		return( result );
	}

	public LinkedList<ChessPiece> getOwnCapturedPieces( ChessPiece cp )
	{
		return( getCapturedPieces( cp.getColor() ) );
	}

	public LinkedList<ChessPiece> getRivalCapturedPieces( ChessPiece cp )
	{
		return( getCapturedPieces( cp.getOpositeColor() ) );
	}

	public King getKing( int color )
	{
		King result = null;

		ChessPiece[] pieces = getPieces(color);

		if( ( pieces[0] instanceof King ) &&
			( color == pieces[0].getColor() )
			 )
		{
			result = (King) pieces[0];
		}

		return( result );
	}
	
	public King getOwnKing( ChessPiece cp )
	{
		return( getKing( cp.getColor() ) );
	}

	public King getRivalKing( ChessPiece cp )
	{
		return( getKing( cp.getOpositeColor() ) );
	}

	public ChessPiece getPiece_fast( int col, int row )
	{
		return( _board[col][row] );
	}
	
	public ChessPiece getPiece( int col, int row ) throws ChessMoveException
	{
		if( (row<0) || (row>ChessBoard.NUM_OF_ROWS) )
			throw( new ChessMoveException( getChessStrConf().getProperty( ChessStringsConf.CONF_BAD_ROW ) + ": " + row ) );
		
		if( (col<0) || (col>ChessBoard.NUM_OF_COLUMNS) )
			throw( new ChessMoveException( getChessStrConf().getProperty( ChessStringsConf.CONF_BAD_COLUMN ) + ": " + col ) );
		
		ChessPiece result = getPiece_fast( col, row );

		return( result );
	}

	public void setPieceInSquare( ChessPiece cp, int col, int row )
	{
		_board[col][row] = cp;
		if( cp != null ) cp.uncheckedSetColumnAndRow( col, row );
	}

	protected void makeSimpleMove( ChessPiece cp, int col, int row )
	{
		setPieceInSquare( null, cp.getColumn(), cp.getRow() );
		setPieceInSquare( cp, col, row );
	}

	protected int findIndexOfPiece( ChessPiece[] ownPieces, ChessPiece piece )
	{
		int index = -1;

		int initialIndex = 0;
//		if( piece instanceof Pawn )	initialIndex = INITIAL_INDEX_FOR_PAWNS;
		
		for( int ii=initialIndex;
			(index==-1) && (ii<NUM_OF_PIECES_PER_PLAYER);
			ii++ )
		{
			if( ownPieces[ii] == piece ) index = ii;
		}
		
		return( index );
	}
	
	public void removeCapturedPiece( ChessPiece piece )
	{
		ChessPiece[] ownPieces = getOwnPieces( piece );
		int index = findIndexOfPiece( ownPieces, piece );

		ownPieces[index] = null;
		setPieceInSquare( null, piece.getColumn(), piece.getRow() );
	}

	protected ChessPiece makePromotion( ChessPiece pawn, ChessGameMove cgm ) throws ChessMoveException, ChessPieceCreationException
	{
		String newPieceCode = ChessPiece.getPieceCode( cgm._promotionPiece, pawn.getColor() );
		ChessPiece newPiece = ChessPiece.createPiece(this, newPieceCode);

		ChessPiece[] ownPieces = getOwnPieces( pawn );
		int index = findIndexOfPiece( ownPieces, pawn );
		
		ownPieces[index] = newPiece;

		// we put the original position, because after the part of taken, it will be moved to the final position
		newPiece.uncheckedSetColumnAndRow( cgm._column1, cgm._row1 );
		return( newPiece );
	}

	protected DataToCasttle getDataToCasttle( ChessGameMove cgm )
	{
		int destinationColumnForRook = -1;
		
		int initialColumnForRook = -1;
		
		if( cgm._column2 == 7 )
		{
			initialColumnForRook = 8;
			destinationColumnForRook = 6;
		}
		else if( cgm._column2 == 3 )
		{
			initialColumnForRook = 1;
			destinationColumnForRook = 4;
		}

		DataToCasttle result = new DataToCasttle( initialColumnForRook, destinationColumnForRook );
		return( result );
	}
	
	protected void doLegalCasttle( King king, ChessGameMove cgm ) throws ChessMoveException
	{
		DataToCasttle data = getDataToCasttle( cgm );

		ChessPiece rook = getPiece_fast( data.getInitialColumnForRook(), cgm._row2 );

		makeSimpleMove( king, cgm._column2, cgm._row2 );
		makeSimpleMove( rook, data.getDestinationColumnForRook(), cgm._row1 );
	}

	public void makeLegalMove( ChessGameMove cgm ) throws ChessMoveException, ChessPieceCreationException
	{
		ChessPiece piece = getPiece_fast( cgm._column1, cgm._row1 );
		ChessPiece destPiece = getPiece_fast( cgm._column2, cgm._row2 );

		if( ( piece instanceof King ) && ( (King) piece ).legalMoveIsCastle( cgm ) )
		{
			doLegalCasttle( (King) piece, cgm );
		}
		else
		{
			if( cgm._promotionPiece != null )
			{
				piece = makePromotion( piece, cgm );
			}

			if( destPiece != null )
			{
				cgm.setHasCapturedPiece();
				removeCapturedPiece( destPiece );
				getRivalCapturedPieces( piece ).push( destPiece );
			}
			else if( piece instanceof Pawn )
			{
				Pawn pawn = (Pawn) piece;

				if( pawn.isEnPassantCapture(cgm) )
				{
					destPiece = getPiece_fast( cgm._column2, cgm._row1 );

					if( destPiece != null )
					{
						cgm.setHasCapturedPiece();
						removeCapturedPiece( destPiece );
						getRivalCapturedPieces( piece ).push( destPiece );
					}
					else
					{
						throw( new ChessMoveException( getChessStrConf().getProperty( ChessStringsConf.CONF_EXPECTED_ENPASSANT_PAWN ) ) );
					}
				}
			}

			makeSimpleMove( piece, cgm._column2, cgm._row2 );
		}
		piece.incrementNumberOfMovesDone();
		_moves.add( cgm );

		updatePosition( _newPosition );
		forwardPosition();
	}

	public ChessPiece getCapturedPiece( ChessGameMove cgm ) throws ChessMoveException, ChessPieceCreationException
	{
		ChessPiece piece = getPiece_fast( cgm._column1, cgm._row1 );
		ChessPiece result = getPiece_fast( cgm._column2, cgm._row2 );
/*
		if( cgm._promotionPiece != null )
		{
			piece = makePromotion( piece, cgm );
		}
*/
		if( ( result == null ) && ( piece instanceof Pawn ) )
		{
			Pawn pawn = (Pawn) piece;

			if( pawn.isEnPassantCapture(cgm) )
			{
				result = getPiece_fast( cgm._column2, cgm._row1 );
			}
		}

		return( result );
	}

	protected void forwardPosition()
	{
		_positions.push( _newPosition );
		try
		{
			_newPosition = new ChessGamePosition( _positions.peek().getFenString() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	protected void backwardPosition()
	{
		_positions.pop( );
		try
		{
			_newPosition = new ChessGamePosition( _positions.peek().getFenString() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	public void makeProbablyLegalMove( ChessGameMove cgm ) throws ChessMoveException, ChessPieceCreationException
	{
		makeLegalMove( cgm );
	}

	public void move( ChessGameMove cgm ) throws ChessMoveException, ChessPieceCreationException
	{
		ChessPiece piece = getPiece( cgm._column1, cgm._row1 );

		if( ( piece != null ) && ( piece.isLegalThisMove(cgm) ) )
		{
			makeLegalMove( cgm );
		}
		else
		{
			throw( new ChessMoveException( getChessStrConf().getProperty( ChessStringsConf.CONF_ILLEGAL_MOVE ) +
											": " + cgm ) );
		}

		_chessGameResult = getChessGameResult_internal();
		setAppropriateGameResult( cgm );
	}

	protected void setAppropriateGameResult( ChessGameMove cgm )
	{
		if( ( _chessGameResult == null ) ||
			( _chessGameResult.equals( ChessGameResult.GAME_CONTINUES ) ) )
		{
			_chessGameResult = getChessGameResult( cgm );
		}
	}

	protected ChessGameResult getChessGameResult( ChessGameMove cgm )
	{
		ChessGameResult result = null;
		if( cgm != null )
		{
			String gameResultStr = cgm.getResultOfGame();
			if( gameResultStr != null )
			{
				switch( gameResultStr )
				{
					case "1-0" : result = ChessGameResult.WHITE_WINS;
					break;
					case "0-1" : result = ChessGameResult.BLACK_WINS;
					break;
					case "1/2-1/2" : result = ChessGameResult.DRAW;
					break;
				}
			}
		}

		return( result );
	}

	protected void undoLegalCasttle( King king, ChessGameMove cgm ) throws ChessMoveException
	{
		DataToCasttle data = getDataToCasttle( cgm );

		ChessPiece rook = getPiece_fast( data.getDestinationColumnForRook(), cgm._row2 );

		makeSimpleMove( king, cgm._column1, cgm._row1 );
		makeSimpleMove( rook, data.getInitialColumnForRook(), cgm._row2 );
	}

	protected ChessPiece undoPromotion( ChessPiece piece, ChessGameMove cgm ) throws ChessMoveException, ChessPieceCreationException
	{
		ChessPiece oldPawn = ChessPiece.createPiece(this, Pawn.PIECE_TYPE_CODE, piece.getColor() );

		ChessPiece[] ownPieces = getOwnPieces( piece );
		int index = findIndexOfPiece( ownPieces, piece );

		ownPieces[index] = oldPawn;

		// we put the final position, because after the part of undoTaken, the pawn will be moved to the initial position
		oldPawn.uncheckedSetColumnAndRow( cgm._column2, cgm._row2 );
		return( oldPawn );
	}

	protected int findFirstEmptyIndexOfPawns( ChessPiece[] arrayOfPieces )
	{
		int result = -1;

		for( int ii=INITIAL_INDEX_FOR_PAWNS; (result == -1) && (ii<NUM_OF_PIECES_PER_PLAYER); ii++ )
		{
			if( arrayOfPieces[ii]==null ) result = ii;
		}

		return( result );
	}

	public void restoreCapturedPiece( ChessPiece piece ) throws ChessMoveException
	{
		ChessPiece[] ownPieces = getOwnPieces( piece );
		int index = -1;

		if( !( piece instanceof Pawn ) )
		{
			if( piece instanceof King )
			{
				if( ownPieces[0] == null )	index = 0;
			}
			if( piece instanceof Queen )
			{
				if( ownPieces[1] == null )	index = 1;
			}
			else if( piece instanceof Rook )
			{
				if( ownPieces[2] == null )	index = 2;
				else if( ownPieces[3] == null ) index = 3;
			}
			else if( piece instanceof Bishop )
			{
				if( ownPieces[4] == null )	index = 4;
				else if( ownPieces[5] == null ) index = 5;
			}
			else if( piece instanceof Knight )
			{
				if( ownPieces[6] == null )	index = 6;
				else if( ownPieces[7] == null ) index = 7;
			}
		}

		if( index == -1 )
		{
			index = findFirstEmptyIndexOfPawns( ownPieces );

			if( index == -1 )
			{
				throw( new ChessMoveException( getChessStrConf().getProperty( ChessStringsConf.CONF_EMPTY_PIECE_NOT_FOUND ) ) );
			}
		}

		ownPieces[index] = piece;
		setPieceInSquare( piece, piece.getColumn(), piece.getRow() );
	}

	public void undoLegalMove( ChessGameMove cgm ) throws ChessMoveException, ChessPieceCreationException
	{
		ChessPiece piece = getPiece_fast( cgm._column2, cgm._row2 );

		if( ( piece instanceof King ) && ( (King) piece ).legalMoveIsCastle( cgm ) )
		{
			undoLegalCasttle( (King) piece, cgm );
		}
		else
		{
			if( cgm._promotionPiece != null )
			{
				piece = undoPromotion( piece, cgm );
			}

			makeSimpleMove( piece, cgm._column1, cgm._row1 );

			if( cgm._hasCapturedPiece )
			{
				LinkedList<ChessPiece> capturedPieces = getRivalCapturedPieces( piece );
				ChessPiece lastCapturedPiece = null;

				if( (capturedPieces.size() > 0 ) && ( ( lastCapturedPiece = capturedPieces.pop() ) != null ) )
				{
					restoreCapturedPiece( lastCapturedPiece );
				}
				else
				{
					throw( new ChessMoveException( getChessStrConf().getProperty( ChessStringsConf.CONF_NO_PIECE_TO_UNTAKE ) ) );
				}
			}
		}
	}

	public void undoMove( ChessGameMove cgm ) throws ChessModelException
	{
		ChessPiece piece = getPiece( cgm._column2, cgm._row2 );

		if( ( piece != null ) )
		{
			undoLegalMove( cgm );
		}
		else
		{
			throw( new ChessMoveException( getChessStrConf().getProperty( ChessStringsConf.CONF_ILLEGAL_MOVE_TO_UNDO ) + ": " + cgm ) );
		}

		piece.decrementNumberOfMovesDone();
		_moves.removeLast();
		backwardPosition();
		_chessGameResult = ChessGameResult.GAME_CONTINUES;
	}
/*
	public void undoMove_deeper( ChessGameMove cgm ) throws ChessModelException
	{
		undoMove( cgm );

		_chessGameResult = ChessGameResult.GAME_CONTINUES;
	}
*/
	public ChessGameMove peekLastMove()
	{
		return( (_moves.size() > 0) ?_moves.getLast() : null );
	}

	public void updatePosition( ChessGamePosition cgp ) throws ChessMoveException
	{
		try
		{
			for( int col=1; col<=NUM_OF_COLUMNS; col++ )
				for( int row=1; row<=NUM_OF_ROWS; row++ )
				{
					ChessPiece piece = getPiece_fast( col, row );

					Character pieceCode = null;
					if( piece != null )
					{
						pieceCode = piece.getPieceCode().charAt(0);
					}
					cgp.putPieceAtPosition(pieceCode, col, row);
				}

			if( _moves.size() > 0 )
			{
				ChessGameMove lastMove = _moves.getLast();
				ChessPiece lastMovedPiece = getPiece_fast( lastMove._column2, lastMove._row2 );

				if( lastMovedPiece == null )
					throw( new ChessMoveException( getChessStrConf().getProperty( ChessStringsConf.CONF_LAST_MOVED_PIECE_NOT_AT_DESTINATION_SQUARE ) +
													": " + lastMove ) );

				// we set the default case, not en passant. If there is an en passant move, it this flag will be updated afterwards (setEnPassant).
				cgp.setIsNotEnPassant();

				if( lastMovedPiece instanceof Pawn )
				{
					cgp.resetNumberOfPliesWithoutProgress();
					if( IntegerFunctions.abs( lastMove._deltaRow ) == 2 )
					{
						cgp.setEnPassant( lastMove._column1, lastMove._row1 + lastMove._deltaRow/2 );
					}
				}
				else
				{
					if( lastMove._hasCapturedPiece )
					{
						cgp.resetNumberOfPliesWithoutProgress();

						// if the captured piece is a rook and the rook is in its original position
						// we forbid castling.
						if( getRivalCapturedPieces( lastMovedPiece ).getFirst() instanceof Rook )
						{
							if( ( lastMovedPiece.getColor() == ChessPiece.BLACK ) && ( lastMove._row2 == 1 ) )
							{
								if( lastMove._column2 == 1 )
									cgp.setWhiteCanCastleQueenSide( false );
								else if( lastMove._column2 == 8 )
									cgp.setWhiteCanCastleKingSide( false );
							}
							else if( ( lastMovedPiece.getColor() == ChessPiece.WHITE ) && ( lastMove._row2 == 8 ) )
							{
								if( lastMove._column2 == 1 )		
									cgp.setBlackCanCastleQueenSide( false );
								else if( lastMove._column2 == 8 )
									cgp.setBlackCanCastleKingSide( false );
							}
						}
					}
					else
					{
						cgp.incrementNumberOfPliesWithoutProgress();
					}

					if( lastMovedPiece instanceof King )
					{
						if( cgp.getIsWhitesTurn() )
						{
							cgp.setWhiteCanCastleKingSide( false );
							cgp.setWhiteCanCastleQueenSide( false );
						}
						else
						{
							cgp.setBlackCanCastleKingSide( false );
							cgp.setBlackCanCastleQueenSide( false );
						}
					}
					else if( lastMovedPiece instanceof Rook )
					{
						if( cgp.getIsWhitesTurn() )
						{
							if( lastMove._row1 == 1 )
							{
								if( lastMove._column1 == 8 )		cgp.setWhiteCanCastleKingSide( false );
								else if( lastMove._column1 == 1 )	cgp.setWhiteCanCastleQueenSide( false );
							}
						}
						else
						{
							if( lastMove._row1 == 8 )
							{
								if( lastMove._column1 == 8 )		cgp.setBlackCanCastleKingSide( false );
								else if( lastMove._column1 == 1 )	cgp.setBlackCanCastleQueenSide( false );
							}
						}
					}
				}

				// if there are no moves in the _moves list, we do not increment,
				// because is the first move, and this function can be called from
				// the setting of the initial position
				cgp.incrementNumberOfPly();
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	public ChessGamePosition getLastPosition()
	{
		return( _positions.peekLast() );
	}

	public boolean isLegalThisMove( ChessGameMove cgm )
	{
		return( isLegalThisMove( cgm, true ) );
	}
	
	public boolean isLegalThisMove( ChessGameMove cgm, boolean weKnowIfIsACaptureMoveOrNot  )
	{
		boolean result = false;

		try
		{
			ChessPiece cp = getPiece( cgm._column1, cgm._row1 );
			if( ( cp != null ) && ( cp.getColor() == getColorToPlay() ) )
			{
				result = cp.isLegalThisMove(cgm, weKnowIfIsACaptureMoveOrNot);
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
		
		return( result );
	}
	
	public boolean isCheckMate( boolean thereIsAnyLegalMove )
	{
		boolean result = false;

		King king = getKing(getColorToPlay() );

		result = (king != null) && king.isInCheck() && !thereIsAnyLegalMove;

		return( result );
	}

	public boolean isCheck( )
	{
		boolean result = false;

		King king = getKing(getColorToPlay() );

		result = (king != null) && king.isInCheck();

		return( result );
	}

	protected boolean isStaleMate( boolean thereIsAnyLegalMove )
	{
		boolean result = false;

		King king = getKing(getColorToPlay() );

		result = (king != null) && !king.isInCheck() && !thereIsAnyLegalMove;

		return( result );
	}

	public List<ChessGameMove> getListOfTotalPossibleMoves()
	{
		List<ChessGameMove> result = new LinkedList<ChessGameMove>();
		
		ChessPiece[] pieces = getPieces(getColorToPlay() );
		
		for( int ii=0; ii<pieces.length; ii++ )
		{
			if( pieces[ii] != null )
			{
				List<ChessGameMove> lcgm = pieces[ii].getListOfLegalMoves();
				result.addAll( lcgm );
			}
		}
		return( result );
	}

	public boolean isThereAnyLegalMove()
	{
		boolean result = false;
		ChessPiece[] pieces = getPieces(getColorToPlay() );
		
		for( int ii=0; !result && (ii<pieces.length); ii++ )
		{
			if( pieces[ii] != null )
			{
				List<ChessGameMove> lcgm = pieces[ii].getListOfLegalMoves();
				result = ( lcgm.size() > 0 );
			}
		}
		return( result );
	}
	
	protected boolean isTripleRepetition()
	{
		boolean result = false;
		int numberOfRepetitions = 1;

		if( _positions.size() > 0 )
		{
			ListIterator<ChessGamePosition> it = _positions.listIterator( _positions.size() );

			ChessGamePosition positionToCompare = null;
			if( it.hasPrevious() )	positionToCompare = it.previous();

			if( positionToCompare != null )
			{
				boolean thereAreMore = true;
				while( !result && thereAreMore )
				{
					thereAreMore = it.hasPrevious();
					if( thereAreMore )
					{
						it.previous();
						thereAreMore = it.hasPrevious();
						if( thereAreMore )
						{
							ChessGamePosition other = it.previous();

							if( positionToCompare.equals( other ) )
							{
								numberOfRepetitions++;
								result = ( numberOfRepetitions == 3 );
							}
						}
					}
				}
			}
		}
		
		return( result );
	}

	protected boolean isFiftyMovementsWithoutProgress()
	{
		int numberOfMovesWithoutProgress = ( (getCurrentPosition().getNumberOfPliesWithoutProgress() != null ) ?
												getCurrentPosition().getNumberOfPliesWithoutProgress() :
												getCurrentPosition().getIncrementedNumberOfPliesWithoutProgress() );
		
		boolean result = ( numberOfMovesWithoutProgress >= 100 );

		return( result );
	}

	protected ChessGameResult getChessGameResult_internal()
	{
		ChessGameResult result = ChessGameResult.GAME_CONTINUES;
		boolean bIsThereAnyLegalMove = isThereAnyLegalMove();

		if( isCheckMate( bIsThereAnyLegalMove ) )
		{
			if( getIsWhitesTurn() )			result = ChessGameResult.BLACK_WINS_CHECK_MATE;
			else							result = ChessGameResult.WHITE_WINS_CHECK_MATE;
		}
		else if( isStaleMate( bIsThereAnyLegalMove ) )
		{
			result = ChessGameResult.DRAW_STALE_MATE;
		}
		else if( isTripleRepetition() )
		{
			result = ChessGameResult.DRAW_THIRD_REPETITION;
		}
		else if( isFiftyMovementsWithoutProgress() )
		{
			result = ChessGameResult.DRAW_FIFTY_MOVES_WITHOUT_PROGRESS;
		}
		return( result );
	}

	public ChessGameResult getChessGameResult()
	{
		return( _chessGameResult );
	}

	public List<ChessPiece> getPiecesOfTypeColor( String pieceTypeCode, int color )
	{
		List<ChessPiece> result = new ArrayList<ChessPiece>();

		ChessPiece[] pieceArray = getPieces( color );

		if( pieceTypeCode != null )
		{
			int initialIndex = 0;

			if( pieceTypeCode.equals( "P" ) ) initialIndex = INITIAL_INDEX_FOR_PAWNS;

			for( int ii=initialIndex; ii<pieceArray.length; ii++ )
			{
				if( (pieceArray[ii]!=null) &&
					pieceTypeCode.equals( pieceArray[ii].getPieceTypeCode() ) &&
					(color == pieceArray[ii].getColor() )
				   )
				{
					result.add( pieceArray[ii] );
				}
			}
		}
		return( result );
	}

	public List<ChessPiece> getPiecesOfTypeColor( ChessPiece piece, int color )
	{
		List<ChessPiece> result = null;

		if( piece != null )
		{
			result = getPiecesOfTypeColor( piece.getPieceTypeCode(), color );
		}
		else
		{
			result = new ArrayList<ChessPiece>();
		}

		return( result );
	}

	public List<ChessGameMove> getListOfMoves()
	{
		List<ChessGameMove> result = new ArrayList<ChessGameMove>( _moves );

		return( result );
	}

	public ChessGamePosition getCurrentPosition()
	{
		return( _newPosition );
	}

	protected class DataToCasttle
	{
		protected int _initialColumnForRook = -1;
		protected int _destinationColumnForRook = -1;

		public DataToCasttle( int initialColumnForRook, int destinationColumnForRook )
		{
			_initialColumnForRook = initialColumnForRook;
			_destinationColumnForRook = destinationColumnForRook;
		}

		public int getInitialColumnForRook()		{ return( _initialColumnForRook );	}
		public int getDestinationColumnForRook()	{ return( _destinationColumnForRook );	}
	}
	
	public boolean getIsWhitesTurn()
	{
		return( getCurrentPosition().getIsWhitesTurn() );
	}

	protected static ChessStringsConf getChessStrConf()
	{
		return( ChessStringsConf.instance() );
	}

	public void flipBoard()
	{
		try
		{
			ChessGamePosition cgp = new ChessGamePosition( getCurrentPosition().getFenString() );
			cgp.flipBoard();
			setPosition( cgp );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}
}
