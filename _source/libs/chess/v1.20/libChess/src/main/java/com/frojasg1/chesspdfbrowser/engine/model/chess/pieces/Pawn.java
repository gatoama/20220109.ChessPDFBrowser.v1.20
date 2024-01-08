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
import com.frojasg1.chesspdfbrowser.engine.exception.ChessMoveException;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessBoard;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGamePosition;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import static com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.ChessPiece.getChessStrConf;
import com.frojasg1.general.number.IntegerFunctions;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class Pawn extends ChessPiece
{
	protected static final double VALUE = 1d;
	public static final String PIECE_TYPE_CODE="P";

	public Pawn( ChessBoard chessBoard, int color )
	{
		super(chessBoard, VALUE, color);
	}
	
	@Override
	public String getPieceTypeCode()
	{
		return( PIECE_TYPE_CODE );
	}

	@Override
	public boolean isProbablyLegalThisMove_child( ChessGameMove cgm, boolean weKnowIfIsACaptureMoveOrNot ) throws ChessMoveException
	{
		boolean result = false;

		if( ! isPieceAtInitialPositionOfMove( cgm ) )
			throw( new ChessMoveException( String.format( getChessStrConf().getProperty( ChessStringsConf.CONF_PIECE_DOES_NOT_MATCH_INITIAL_POSITION ),
														this.toString(),
														cgm.toString() )    ) );

		if( _color == WHITE )	result = isProbablyLegalThisMove_child_forWhite( cgm, weKnowIfIsACaptureMoveOrNot );
		else if( _color == BLACK ) result = isProbablyLegalThisMove_child_forBlack( cgm, weKnowIfIsACaptureMoveOrNot );

		return( result );
	}

	public boolean isEnPassantCapture( ChessGameMove cgm )
	{
		boolean result = false;
/*
		ChessPiece rivalPawnToTakeEnPassant = null;
		int initialRowForPreviousMove = -1;
		
		if( _color == WHITE )
		{
			if( ( cgm._row2 == 6 ) && IntegerFunctions.abs( cgm._deltaColumn ) == 1 && ( cgm._deltaRow == 1 ) )
			{
				rivalPawnToTakeEnPassant = _chessBoard.getPiece_fast( cgm._column2, cgm._row1 );
				initialRowForPreviousMove = 7;
			}
		}
		else if( _color == BLACK )
		{
			if( ( cgm._row2 == 3 ) && IntegerFunctions.abs( cgm._deltaColumn ) == 1 && ( cgm._deltaRow == -1 ) )
			{
				rivalPawnToTakeEnPassant = _chessBoard.getPiece_fast( cgm._column2, cgm._row1 );
				initialRowForPreviousMove = 2;
			}
		}

		if( rivalPawnToTakeEnPassant != null )
		{
			if( rivalPawnToTakeEnPassant instanceof Pawn )
			{
				ChessGameMove previousCgm = _chessBoard.peekLastMove();
				
				result =	(previousCgm != null) &&
							( previousCgm._column1 == cgm._column2 ) &&
							( previousCgm._column2 == cgm._column2 ) &&
							( previousCgm._row2 == cgm._row1 ) &&
							( previousCgm._row1 == initialRowForPreviousMove );
			}
		}
*/
		ChessGamePosition currentPosition = _chessBoard.getCurrentPosition();
		if( ( currentPosition.isEnPassantMove() != null ) &&
			currentPosition.isEnPassantMove()
		  )
		{
			result = ( cgm._column2 == currentPosition.getEnPassantColumn() ) &&
					( cgm._row2 == currentPosition.getEnPassantRow() );
		}

		return( result );
	}

	public boolean isProbablyLegalThisMove_child_forWhite( ChessGameMove cgm, boolean weKnowIfIsACaptureMoveOrNot ) throws ChessMoveException
	{
		boolean result = true;

		ChessPiece destPiece = _chessBoard.getPiece_fast( cgm._column2, cgm._row2 );

		if( ( !weKnowIfIsACaptureMoveOrNot || !cgm._hasCapturedPiece ) && ( cgm._deltaColumn == 0 ) )
		{
			result = ( destPiece == null );
			if( result )
			{
				if( cgm._deltaRow == 1 );
				else if( (cgm._row1 == 2) && ( cgm._deltaRow == 2 ) )	result = ( _chessBoard.getPiece_fast( cgm._column2, cgm._row2 - 1 ) == null );
				else result = false;
			}

			if( result && !weKnowIfIsACaptureMoveOrNot )
				cgm._hasCapturedPiece = false;
		}
		else if( ( !weKnowIfIsACaptureMoveOrNot || cgm._hasCapturedPiece ) && ( IntegerFunctions.abs( cgm._deltaColumn ) == 1 ) && ( cgm._deltaRow == 1 ) )
		{
			result = ( destPiece != null ) && ( destPiece.getColor() != getColor() );

			if( ! result )
			{
				result = isEnPassantCapture( cgm );
			}

			if( result && !weKnowIfIsACaptureMoveOrNot )
				cgm._hasCapturedPiece = true;
		}
		else
		{
			result = false;
		}

		return( result );
	}

	public boolean isProbablyLegalThisMove_child_forBlack( ChessGameMove cgm, boolean weKnowIfIsACaptureMoveOrNot ) throws ChessMoveException
	{
		boolean result = true;

		ChessPiece destPiece = _chessBoard.getPiece_fast( cgm._column2, cgm._row2 );

		if( ( !weKnowIfIsACaptureMoveOrNot || !cgm._hasCapturedPiece ) && cgm._deltaColumn == 0 )
		{
			result = ( destPiece == null );
			if( result )
			{
				if( cgm._deltaRow == -1 );
				else if( (cgm._row1 == 7) && ( cgm._deltaRow == -2 ) )	result = ( _chessBoard.getPiece_fast( cgm._column2, cgm._row2 + 1 ) == null );
				else result = false;
			}

			if( result && !weKnowIfIsACaptureMoveOrNot )
				cgm._hasCapturedPiece = false;
		}
		else if( ( !weKnowIfIsACaptureMoveOrNot || cgm._hasCapturedPiece ) && ( IntegerFunctions.abs( cgm._deltaColumn ) == 1 ) && ( cgm._deltaRow == -1 ) )
		{
			result = ( destPiece != null ) && ( destPiece.getColor() != getColor() );

			if( ! result )
			{
				result = isEnPassantCapture( cgm );
			}

			if( result && !weKnowIfIsACaptureMoveOrNot )
				cgm._hasCapturedPiece = true;
		}
		else
		{
			result = false;
		}

		return( result );
	}

	@Override
	public List<ChessGameMove> getListOfLegalMoves()
	{
		List<ChessGameMove> result = super.getListOfLegalMoves();

		LinkedList<ChessGameMove> promotionMoves = new LinkedList<ChessGameMove>();

		Iterator<ChessGameMove> it = result.iterator();

		while( it.hasNext() )
		{
			ChessGameMove cgm = it.next();
			if( legalMoveIsPromotion( cgm ) )
			{
				promotionMoves.add( cgm );
			}
		}

		it = promotionMoves.iterator();
		while( it.hasNext() )
		{
			ChessGameMove cgm = it.next();

			ChessGameMove cgm2 = new ChessGameMove( cgm );
			cgm2.setPromotionPiece( "Q" );
			result.add( cgm2 );

			cgm2 = new ChessGameMove( cgm );
			cgm2.setPromotionPiece( "R" );
			result.add( cgm2 );

			cgm2 = new ChessGameMove( cgm );
			cgm2.setPromotionPiece( "B" );
			result.add( cgm2 );

			cgm2 = new ChessGameMove( cgm );
			cgm2.setPromotionPiece( "N" );
			result.add( cgm2 );

			result.remove( cgm );
		}

		return( result );
	}

	public boolean legalMoveIsPromotion( ChessGameMove cgm )
	{
		return( ( cgm._row2 == 1 ) || ( cgm._row2 == 8 ) );
	}
	

}
