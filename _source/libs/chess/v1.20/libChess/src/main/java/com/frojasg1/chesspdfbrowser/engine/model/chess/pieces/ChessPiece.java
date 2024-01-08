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
package com.frojasg1.chesspdfbrowser.engine.model.chess.pieces;

import com.frojasg1.chesspdfbrowser.engine.configuration.ChessStringsConf;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessBoard;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessGamePositionException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessMoveException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessPieceCreationException;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import com.frojasg1.general.number.IntegerFunctions;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Usuario
 */
public abstract class ChessPiece implements Serializable
{
	public static final int NOT_INITIALIZED = -1;
	public static final int WHITE = 1;
	public static final int BLACK = 0;
	
	public static final String COLOR_CODE_FOR_WHITE = "W";
	public static final String COLOR_CODE_FOR_BLACK = "B";

	protected int _row;
	protected int _column;

	protected int _color = NOT_INITIALIZED;
	protected double _value = 0.0d;

	protected ChessBoard _chessBoard = null;

	protected int _numberOfMovesDone = 0;
	
	protected ChessPiece( ChessBoard chessBoard, double value, int color )
	{
		_chessBoard = chessBoard;
		
		_value = value;
		_row = 0;
		_column = 0;
		_color = color;
		
		_numberOfMovesDone = 0;
	}

	public void uncheckedSetColumnAndRow( int col, int row )
	{
		_row = row;
		_column = col;
	}
	
	public void setColumnAndRow( int col, int row ) throws ChessGamePositionException
	{
		if( (row<0) || (row>ChessBoard.NUM_OF_ROWS) )
			throw( new ChessGamePositionException( getChessStrConf().getProperty( ChessStringsConf.CONF_BAD_ROW ) + ": " + row ) );
		
		if( (col<0) || (col>ChessBoard.NUM_OF_COLUMNS) )
			throw( new ChessGamePositionException( getChessStrConf().getProperty( ChessStringsConf.CONF_BAD_COLUMN ) + ": " + col ) );
		
		uncheckedSetColumnAndRow( col, row );
	}

	public int getRow()
	{
		return( _row );
	}
	
	public int getColumn()
	{
		return( _column );
	}
	
	public int getColor()
	{
		return( _color );
	}
	
	public int getOpositeColor()
	{
		int result = -1;
		if( _color == WHITE )	result = BLACK;
		else if( _color == BLACK )	result = WHITE;
		return( result );
	}
	
	public String getColorString()
	{
		String result = "";
		if( _color == WHITE ) result = "W";
		else if( _color == BLACK )	result = "B";

		return( result );
	}

	public abstract String getPieceTypeCode();
	protected abstract boolean isProbablyLegalThisMove_child( ChessGameMove cgm, boolean weKnowIfIsACaptureMoveOrNot ) throws ChessMoveException;

	public boolean legalMoveIsPromotion( ChessGameMove cgm )
	{
		return( false );
	}
	
	public List<ChessGameMove> getListOfLegalMoves()
	{
		List<ChessGameMove> result = new LinkedList<ChessGameMove>();

		for( int col=1; col<=ChessBoard.NUM_OF_COLUMNS; col++ )
			for( int row=1; row<=ChessBoard.NUM_OF_ROWS; row++ )
			{
				ChessGameMove cgm = new ChessGameMove( getColumn(), getRow(), col, row );
				try
				{
					if( isLegalThisMove( cgm, false ) )
					{
						result.add(cgm);
					}
				}
				catch( Throwable th )
				{
//					th.printStackTrace();
				}
			}

		return( result );
	}

	public String getPieceCode()
	{
		return( getPieceCode( getPieceTypeCode(), getColor() ) );
	}

	public static String getPieceCode( String pieceTypeCode, int color )
	{
		String result = null;

		if( pieceTypeCode != null )
		{
			if( color == WHITE )	result = pieceTypeCode.toUpperCase();
			else if( color == BLACK ) result = pieceTypeCode.toLowerCase();
		}

		return( result );
	}

	protected boolean isPieceAtInitialPositionOfMove( ChessGameMove cgm )
	{
		boolean result = false;

		result = ( _column == cgm._column1 ) && ( _row == cgm._row1 );

		return( result );
	}
/*
	public boolean canMoveTo( int col, int row )
	{
		boolean result = false;
		
		List<ChessGameMove> listOfLegalMoves = getListOfLegalMoves();
		Iterator<ChessGameMove> it = listOfLegalMoves.iterator();
		
		while( !result && it.hasNext() )
		{
			ChessGameMove cgm = it.next();
			result = ( ( cgm._column2 == col ) && ( cgm._row2 == row ) );
		}

		return( result );
	}
*/
	public boolean isLegalThisMove( ChessGameMove cgm ) throws ChessMoveException, ChessPieceCreationException
	{
		return( isLegalThisMove( cgm, true ) );
	}

	public boolean isLegalThisMove( ChessGameMove cgm, boolean weKnowIfIsACaptureMoveOrNot ) throws ChessMoveException, ChessPieceCreationException
	{
		boolean result = false;

		cgm.checkIsMoveInsideBoard();

		if( isProbablyLegalThisMove_child( cgm, weKnowIfIsACaptureMoveOrNot ) )
		{
			try
			{
				_chessBoard.makeProbablyLegalMove( cgm );
				result = true;
			}
			catch( ChessMoveException cme )
			{
				cme.printStackTrace();
				throw( cme );
			}

			if( result )
			{
				King ownKing = _chessBoard.getOwnKing( this );
				if( ownKing != null )
				{
					result = !ownKing.isInCheck();
				}

				try
				{
					_chessBoard.undoMove( cgm );
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}
			}
		}
		
		return( result );
	}

	protected boolean destinationSquareMatchesWithMove( ChessGameMove cgm, boolean weKnowIfIsACaptureMoveOrNot )
	{
		boolean result = false;

		ChessPiece piece = _chessBoard.getPiece_fast( cgm._column2, cgm._row2 );

		if( weKnowIfIsACaptureMoveOrNot )
		{
			/* 
				The destination square matches if the move is not a capture, and the destination square is emtpy or
				if the move is a capture and the destination square holds a rival piece.
			*/
			if( piece != null )
			{
				result = (cgm._hasCapturedPiece) && ( piece.getColor() != getColor() );
			}
			else
			{
				result = !cgm._hasCapturedPiece;
			}
		}
		else
		{
			result = ( piece == null ) || ( piece.getColor() != getColor() );
		}

		return( result );
	}

	public void setValue( double value )	{	_value = value;	}
	public double getValue()				{	return( _value );	}

	public void incrementNumberOfMovesDone()	{ _numberOfMovesDone++;	}
	public void decrementNumberOfMovesDone()	{ _numberOfMovesDone--;	}

	public int getNumberOfMovesDone()			{ return( _numberOfMovesDone );	}

	public boolean isTheSameTypeOfPiece( ChessPiece other )
	{
		boolean result = false;
		if( other != null )
			result = ( getClass().equals( other.getClass() ) );

		return( result );
	}

	public void setChessBoard( ChessBoard cb )
	{
		_chessBoard = cb;
	}

	public boolean isDiagonalOrStraightPathClear( ChessGameMove cgm )
	{
		boolean result = true;

		if( IntegerFunctions.max(	IntegerFunctions.abs( cgm._deltaColumn ),
									IntegerFunctions.abs( cgm._deltaRow )) > 1 )
		{
			int columnStep = IntegerFunctions.sgn( cgm._deltaColumn );
			int rowStep = IntegerFunctions.sgn( cgm._deltaRow );

			int row = cgm._row1;
			int column = cgm._column1;

			int finalColumnToCheck = cgm._column2 - columnStep;
			int finalRowToCheck = cgm._row2 - rowStep;

			do
			{
				column += columnStep;
				row += rowStep;

				result = ( _chessBoard.getPiece_fast( column, row ) == null );
			}
			while( result && ( ( column != finalColumnToCheck ) || ( row != finalRowToCheck ) ) );
		}

		return( result );
	}

	protected ChessPiece findFirstPieceInPath( int col, int row, int deltaCol, int deltaRow )
	{
		ChessPiece result = null;

		int coltmp = col + deltaCol;
		int rowtmp = row + deltaRow;

		while( (result == null) &&
				(coltmp>=0) && (coltmp<=ChessBoard.NUM_OF_COLUMNS) &&
				(rowtmp>=0) && ( rowtmp<=ChessBoard.NUM_OF_ROWS ) )
		{
			result = _chessBoard.getPiece_fast(coltmp, rowtmp);

			// the own king does not count.
			// this is useful because we do not have to make the king's move before evaluate if the new position is in check.
			// it is useful also to evaluate if the squares of king to pass are threatened in the casttle move.
			if( ( result != null ) && ( result instanceof King ) && ( result.getColor() == getColor() ) )
				result = null;
			
			coltmp += deltaCol;
			rowtmp += deltaRow;
		}

		return( result );
	}

	protected boolean lookForThreateningRookOrQueenInPath( int col, int row, int deltaCol, int deltaRow )
	{
		boolean result = false;

		ChessPiece piece = findFirstPieceInPath( col, row, deltaCol, deltaRow );

		if( piece != null )
		{
			if( piece.getColor() != getColor() )
			{
				result = ( piece instanceof Queen ) || ( piece instanceof Rook );
			}
		}

		return( result );
	}

	protected boolean lookForThreateningRookOrQueen( int col, int row )
	{	
		boolean result = lookForThreateningRookOrQueenInPath( col, row, 0, 1 );
		
		if( ! result )
		{
			result = lookForThreateningRookOrQueenInPath( col, row, 1, 0 );
			
			if( ! result )
			{
				result = lookForThreateningRookOrQueenInPath( col, row, 0, -1 );

				if( ! result )
				{
					result = lookForThreateningRookOrQueenInPath( col, row, -1, 0 );
				}
			}
		}

		return( result );
	}
	
	protected boolean lookForThreateningBishopQueenOrPawn( int col, int row, int deltaCol, int deltaRow )
	{
		boolean result = false;

		ChessPiece piece = findFirstPieceInPath( col, row, deltaCol, deltaRow );

		if( piece != null )
		{
			if( piece.getColor() != getColor() )
			{
				result = ( piece instanceof Queen ) || ( piece instanceof Bishop );
				
				if( !result && ( piece instanceof Pawn ) )
				{
					int rowDifference = piece.getRow() - row;
					result = ( ( rowDifference == -1 ) && ( piece.getColor() == WHITE ) ||
								( rowDifference == 1 ) && ( piece.getColor() == BLACK ) 
							) &&
							( (piece.getColumn()-col) == deltaCol );
				}
			}
		}

		return( result );
	}

	protected boolean lookForThreateningBishopQueenOrPawn( int col, int row )
	{	
		boolean result = lookForThreateningBishopQueenOrPawn( col, row, -1, 1 );
		
		if( ! result )
		{
			result = lookForThreateningBishopQueenOrPawn( col, row, 1, 1 );
			
			if( ! result )
			{
				result = lookForThreateningBishopQueenOrPawn( col, row, 1, -1 );

				if( ! result )
				{
					result = lookForThreateningBishopQueenOrPawn( col, row, -1, -1 );
				}
			}
		}
		
		return( result );
	}

	protected boolean lookForThreateningKnightInSquare( int col, int row, int deltaCol, int deltaRow )
	{
		boolean result = false;

		ChessPiece piece = null;

		int coltmp = col + deltaCol;
		int rowtmp = row + deltaRow;

		if( (coltmp>=0) && (coltmp<=ChessBoard.NUM_OF_COLUMNS) &&
				(rowtmp>=0) && ( rowtmp<ChessBoard.NUM_OF_ROWS ) )
		{
			piece = _chessBoard.getPiece_fast(coltmp, rowtmp);

			result = ( piece != null ) && ( piece instanceof Knight) && ( piece.getColor() != getColor() );
		}

		return( result );
	}

	protected boolean lookForThreateningKnight( int col, int row )
	{
		boolean result = lookForThreateningKnightInSquare( col, row, 2, 1 );

		if( ! result )
		{
			result = lookForThreateningKnightInSquare( col, row, 2, -1 );
			
			if( ! result )
			{
				result = lookForThreateningKnightInSquare( col, row, 1, 2 );

				if( ! result )
				{
					result = lookForThreateningKnightInSquare( col, row, -1, 2 );

					if( ! result )
					{
						result = lookForThreateningKnightInSquare( col, row, -2, 1 );

						if( ! result )
						{
							result = lookForThreateningKnightInSquare( col, row, -2, -1 );

							if( ! result )
							{
								result = lookForThreateningKnightInSquare( col, row, 1, -2 );

								if( ! result )
								{
									result = lookForThreateningKnightInSquare( col, row, -1, -2 );
								}
							}
						}
					}
				}
			}
		}
		
		return( result );
	}
	
	protected boolean lookForThreateningKing( int col, int row )
	{
		boolean result = false;

		ChessPiece rivalKing = _chessBoard.getRivalKing( this );
		
		if( rivalKing != null )
		{
			int absDeltaCol = IntegerFunctions.abs( col - rivalKing.getColumn() );
			int absDeltaRow = IntegerFunctions.abs( row - rivalKing.getRow() );
			
			int maxDelta = IntegerFunctions.max( absDeltaCol, absDeltaRow );
			result = (maxDelta == 1);
		}

		return( result );
	}

	protected boolean isSquareThreatened( int col, int row )
	{
		boolean result = false;
		
		result = lookForThreateningRookOrQueen( col, row );
		
		if( !result )
		{
			result = lookForThreateningBishopQueenOrPawn( col, row );
			
			if( ! result )
			{
				result = lookForThreateningKnight( col, row );
				
				if( ! result )
				{
					result = lookForThreateningKing( col, row );
				}
			}
		}

		return( result );
	}

	public static ChessPiece createPiece( ChessBoard chessBoard, String pieceCode ) throws ChessPieceCreationException
	{
		ChessPiece result = null;

		if( pieceCode != null )
		{
			int color = -1;
			String pieceTypeCode = pieceCode.toUpperCase();

			if( pieceCode.equals( pieceTypeCode ) )	color = WHITE;
			else									color = BLACK;

			result = createPiece( chessBoard, pieceTypeCode, color );
		}

		return( result );
	}

	public static ChessPiece createPiece( ChessBoard chessBoard, String pieceCode, int color ) throws ChessPieceCreationException
	{
		ChessPiece result = null;

		if( pieceCode != null )
		{
			String pieceTypeCode = pieceCode.toUpperCase();

			if( pieceTypeCode.equals( Pawn.PIECE_TYPE_CODE ) )
				result = new Pawn( chessBoard, color );
			else  if( pieceTypeCode.equals( Knight.PIECE_TYPE_CODE ) )
				result = new Knight( chessBoard, color );
			else  if( pieceTypeCode.equals( Bishop.PIECE_TYPE_CODE ) )
				result = new Bishop( chessBoard, color );
			else  if( pieceTypeCode.equals( Rook.PIECE_TYPE_CODE ) )
				result = new Rook( chessBoard, color );
			else  if( pieceTypeCode.equals( Queen.PIECE_TYPE_CODE ) )
				result = new Queen( chessBoard, color );
			else if( pieceTypeCode.equals( King.PIECE_TYPE_CODE ) )
				result = new King( chessBoard, color );
			else
				throw( new ChessPieceCreationException( getChessStrConf().getProperty( ChessStringsConf.CONF_ERROR_UNRECOGNIZED_PIECE_CODE ) +
														": " + pieceTypeCode ) );
		}

		return( result );
	}

	public static void main( String[] args )
	{
		System.out.println( "WHITE: " + WHITE );
		System.out.println( "BLACK: " + BLACK );
	}

	protected static ChessStringsConf getChessStrConf()
	{
		return( ChessStringsConf.instance() );
	}
}
