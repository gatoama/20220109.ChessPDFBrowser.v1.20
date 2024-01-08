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
package com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree;

import com.frojasg1.chesspdfbrowser.engine.configuration.ChessStringsConf;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessMoveException;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.MoveToken;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessBoard;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class ChessGameMove implements Serializable
{
	public int _column1 = -1;
	public int _row1 = -1;
	public int _column2 = -1;
	public int _row2 = -1;

	public String _promotionPiece = null;
	public boolean _hasCapturedPiece = false;

	public int _deltaColumn = -1000;
	public int _deltaRow = -1000;

	public String _resultOfGame = null;

	public List<NAG> _nagList = null;

	protected MoveToken _moveToken = null;

	protected boolean _hasBeenChecked = false;

	public ChessGameMove( int column1, int row1, int column2, int row2 )
	{
		setMoveCoordinates( column1, row1, column2, row2 );

		_nagList = new ArrayList<NAG>();
	}

	public ChessGameMove( ChessGameMove cgm )
	{
		copy( cgm );

		_nagList = new ArrayList<NAG>();
	}

	public ChessGameMove( MoveToken mt )
	{
		_moveToken = mt;

		_nagList = new ArrayList<NAG>();
	}

	public final void copy( ChessGameMove cgm )
	{
		setMoveCoordinates( cgm._column1, cgm._row1, cgm._column2, cgm._row2 );

		_promotionPiece = cgm._promotionPiece;
		_hasCapturedPiece = cgm._hasCapturedPiece;
		_resultOfGame = cgm._resultOfGame;
		_moveToken = cgm._moveToken;
	}

	public void setMoveCoordinates( int column1, int row1, int column2, int row2 )
	{
		_row1 = row1;
		_column1 = column1;
		_row2 = row2;
		_column2 = column2;

		calculateDeltas();

		_hasBeenChecked = ( ( _row1 > 0 ) && ( _column1 > 0 ) && ( _row2 > 0 ) && ( _column2 > 0 ) );
	}

	public void setHasBeenChecked( boolean value )
	{
		_hasBeenChecked = value;
	}
	
	public boolean hasBeenChecked()
	{
		return( _hasBeenChecked );
	}

	public MoveToken getMoveToken()
	{
		return( _moveToken );
	}

	public void setMoveToken( MoveToken mt )
	{
		_moveToken = mt;
	}
	
	public void setPromotionPiece( String pieceType )
	{
		_promotionPiece = pieceType;
	}

	public void setHasCapturedPiece()
	{
		_hasCapturedPiece = true;
	}

	public void setResultOfGame( String resultOfGame )
	{
		_resultOfGame = resultOfGame;
	}

	public String getResultOfGame()
	{
		return( _resultOfGame );
	}

	public Iterator<NAG> getNagIterator()
	{
		return( _nagList.iterator() );
	}

	public void addNAG( NAG nag )
	{
		_nagList.add(nag);
	}
	
	public void addAllNAGs( Iterator<NAG> it )
	{
		while( it.hasNext() )
			_nagList.add( it.next() );
	}

	public static String getColumnLetter( int columnIndex )
	{
		StringBuilder sb = new StringBuilder();
		int asc = 'a' + columnIndex - 1;
		char ascChar = (char) asc;
		
		sb.append(ascChar);
		
		return( sb.toString() );
	}
	
	public String toString()
	{
		String promotion = "";
		if( _promotionPiece != null )	promotion = "=" + _promotionPiece;
		
		String result = String.format( "%s-%s%s", getSquareString( _column1, _row1 ), getSquareString( _column2, _row2), promotion );
		
		if( _moveToken != null )
			result = result + "(" + _moveToken.getString() + ")";
		
		return( result );
	}

	protected void checkBounds( int value, int upperBound ) throws ChessMoveException
	{
		if( ( value <0 ) || !( value <= upperBound ) )
			throw( new ChessMoveException( getChessStrConf().getProperty( ChessStringsConf.CONF_COORDINATE_OF_MOVE_OUT_OF_BOUNDS ) +
										": " + this ) );
	}

	public boolean checkIsMoveInsideBoard() throws ChessMoveException
	{
		checkBounds( _column1, ChessBoard.NUM_OF_COLUMNS );
		checkBounds( _column2, ChessBoard.NUM_OF_COLUMNS );
		checkBounds( _row1, ChessBoard.NUM_OF_ROWS );
		checkBounds( _row2, ChessBoard.NUM_OF_ROWS );
		
		return( true );
	}
	
	protected void calculateDeltas()
	{
		_deltaColumn = _column2 - _column1;
		_deltaRow = _row2 - _row1;
	}

	@Override
	public boolean equals( Object obj )
	{
		boolean result = false;

		if( obj instanceof ChessGameMove )
		{
			ChessGameMove other = (ChessGameMove) obj;

			if( !hasBeenChecked() && ( getMoveToken() != null ) && ( other.getMoveToken() != null ) )
			{
				result = ( getMoveToken().getString().equals( other.getMoveToken().getString() ) );
			}
			else
			{
				result =	(_column1 == other._column1 ) &&
							(_column2 == other._column2 ) &&
							(_row1 == other._row1 ) &&
							(_row2 == other._row2 ) &&
							( ( _promotionPiece == null ) && ( other._promotionPiece == null ) ||
							  ( _promotionPiece != null ) && _promotionPiece.equals( other._promotionPiece )
							);
			}
		}

		return( result );
	}

	public static String getSquareString( int col, int row )
	{
		String result = getColumnLetter( col ) + String.valueOf( row );

		return( result );
	}
	
	public static int getColumnIndex( String column )
	{
		int result = -1;
		
		if( ( column != null ) && ( column.length() == 1 ) )
		{
			char ch = column.toLowerCase().charAt(0);
			result = ch - 'a' + 1;
		}

		return( result );
	}

	public static int getRowIndex( String column )
	{
		int result = -1;
		
		if( ( column != null ) && ( column.length() == 1 ) )
		{
			char ch = column.charAt(0);
			result = ch - '0';
		}

		return( result );
	}

	protected static ChessStringsConf getChessStrConf()
	{
		return( ChessStringsConf.instance() );
	}
}
