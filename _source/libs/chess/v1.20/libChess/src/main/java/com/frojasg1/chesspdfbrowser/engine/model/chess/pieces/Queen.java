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
import java.util.List;

/**
 *
 * @author Usuario
 */
public class Queen extends ChessPiece
{
	protected static final double VALUE = 9d;
	public static final String PIECE_TYPE_CODE="Q";

	public Queen( ChessBoard chessBoard, int color )
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

		if( ( IntegerFunctions.abs( cgm._deltaColumn ) == IntegerFunctions.abs( cgm._deltaRow ) ) ||
			( (cgm._deltaColumn == 0) && (cgm._deltaRow != 0 ) ) ||
			( ( cgm._deltaRow == 0 ) && (cgm._deltaColumn != 0 ) )
			)
		{
			result = isDiagonalOrStraightPathClear(cgm);
		}

		if( result )
		{
			result = destinationSquareMatchesWithMove( cgm, weKnowIfIsACaptureMoveOrNot );
		}

		return( result );
	}

}
