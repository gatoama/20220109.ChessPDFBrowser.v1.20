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
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import static com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.ChessPiece.getChessStrConf;
import com.frojasg1.general.number.IntegerFunctions;
import java.io.Serializable;

/**
 *
 * @author Usuario
 */
public class King extends ChessPiece implements Serializable
{
	protected static final double VALUE = 4.5d;
	public static final String PIECE_TYPE_CODE="K";

	public King( ChessBoard chessBoard, int color )
	{
		super(chessBoard, VALUE, color);
	}

	@Override
	public String getPieceTypeCode()
	{
		return( PIECE_TYPE_CODE );
	}

	public boolean isInCheck( )
	{
		return( isSquareThreatened( _column, _row ) );
	}

	public boolean legalMoveIsCastle( ChessGameMove cgm )
	{
		int colDiff = cgm._column1 - cgm._column2;
		boolean result = ( ( colDiff == 2 ) || (colDiff == -2 ) );

		return( result );
	}

	protected boolean isThisMoveACastleLegalMove( ChessGameMove cgm )
	{
		boolean result = false;

		if( _chessBoard.getCurrentPosition().canCastle(cgm) && !isInCheck() )
		{
			int colDirection = IntegerFunctions.sgn( cgm._column2 - cgm._column1 );

			if( ( ( getColor() == WHITE ) && ( _row == 1 ) ||
				  ( getColor() == BLACK ) && ( _row == 8 ) )
			   )
			{
				result = ( _chessBoard.getPiece_fast( _column + colDirection, _row ) == null ) && !isSquareThreatened( _column + colDirection, _row );

				if( result )
					result = ( _chessBoard.getPiece_fast( cgm._column2, cgm._row2 ) == null ) && !isSquareThreatened( cgm._column2, cgm._row2 );
			}
		}

		return( result );
	}

	@Override
	protected boolean isProbablyLegalThisMove_child( ChessGameMove cgm, boolean weKnowIfIsACaptureMoveOrNot ) throws ChessMoveException
	{
		boolean result = false;

		if( ! isPieceAtInitialPositionOfMove( cgm ) )
			throw( new ChessMoveException( String.format( getChessStrConf().getProperty( ChessStringsConf.CONF_PIECE_DOES_NOT_MATCH_INITIAL_POSITION ),
														this.toString(),
														cgm.toString() )    ) );

		result = ( IntegerFunctions.max( IntegerFunctions.abs( cgm._deltaColumn ), IntegerFunctions.abs( cgm._deltaRow ) ) == 1 );

		if( ! result )
		{
			result = isThisMoveACastleLegalMove( cgm );
		}
		else
		{
			result = destinationSquareMatchesWithMove( cgm, weKnowIfIsACaptureMoveOrNot );
		}

		return( result );
	}

}
