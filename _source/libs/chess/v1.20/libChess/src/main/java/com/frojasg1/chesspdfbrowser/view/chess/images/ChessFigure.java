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
package com.frojasg1.chesspdfbrowser.view.chess.images;

import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Bishop;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.ChessPiece;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.King;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Knight;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Pawn;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Queen;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Rook;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public enum ChessFigure
{
	WHITE_PAWN( "processed.white.pawn.png", Pawn.PIECE_TYPE_CODE, ChessPiece.WHITE ),
	WHITE_ROOK( "processed.white.rook.png", Rook.PIECE_TYPE_CODE, ChessPiece.WHITE ),
	WHITE_KNIGHT( "processed.white.knight.png", Knight.PIECE_TYPE_CODE, ChessPiece.WHITE ),
	WHITE_BISHOP( "processed.white.bishop.png", Bishop.PIECE_TYPE_CODE, ChessPiece.WHITE ),
	WHITE_QUEEN( "processed.white.queen.png", Queen.PIECE_TYPE_CODE, ChessPiece.WHITE ),
	WHITE_KING( "processed.white.king.png", King.PIECE_TYPE_CODE, ChessPiece.WHITE ),
	BLACK_PAWN( "processed.black.pawn.png", Pawn.PIECE_TYPE_CODE, ChessPiece.BLACK ),
	BLACK_ROOK( "processed.black.rook.png", Rook.PIECE_TYPE_CODE, ChessPiece.BLACK ),
	BLACK_KNIGHT( "processed.black.knight.png", Knight.PIECE_TYPE_CODE, ChessPiece.BLACK ),
	BLACK_BISHOP( "processed.black.bishop.png", Bishop.PIECE_TYPE_CODE, ChessPiece.BLACK ),
	BLACK_QUEEN( "processed.black.queen.png", Queen.PIECE_TYPE_CODE, ChessPiece.BLACK ),
	BLACK_KING( "processed.black.king.png", King.PIECE_TYPE_CODE, ChessPiece.BLACK );


	protected String _simpleResourceName;

	protected String _pieceTypeCode;
	protected int _color;


	ChessFigure( String simpleResourceName, String pieceTypeCode, int color )
	{
		_simpleResourceName = simpleResourceName;
		_pieceTypeCode = pieceTypeCode;
		_color = color;
	}

	public String getSimpleResourceName()
	{
		return( _simpleResourceName );
	}

	public String getPieceTypeCode()
	{
		return( _pieceTypeCode );
	}

	public String getPieceCode()
	{
		return( ChessPiece.getPieceCode( getPieceTypeCode(), getColor() ) );
	}

	public int getColor()
	{
		return( _color );
	}
}
