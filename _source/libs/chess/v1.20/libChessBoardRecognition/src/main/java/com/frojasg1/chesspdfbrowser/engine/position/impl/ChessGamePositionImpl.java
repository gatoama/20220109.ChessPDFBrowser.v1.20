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
package com.frojasg1.chesspdfbrowser.engine.position.impl;

import com.frojasg1.chesspdfbrowser.engine.position.ChessGamePositionBase;
import com.frojasg1.chesspdfbrowser.recognizer.chessboard.result.ChessBoardGridResult;
import com.frojasg1.chesspdfbrowser.recognizer.correlator.CorrelationResult;
import com.frojasg1.chesspdfbrowser.recognizer.store.pattern.ChessFigurePattern;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessGamePositionImpl extends ChessGamePositionBase
{
	protected CorrelationResult[][] _recognizedPatterns = null;
	protected ChessBoardGridResult _detectedGrid = null;

	public ChessGamePositionImpl()
	{
		initPatterns();
	}

	public void initPatterns()
	{
		_recognizedPatterns = new CorrelationResult[NUM_OF_COLUMNS+1][NUM_OF_ROWS+1];
	}

	public void putCorrelationResultAtPositionBase( CorrelationResult correlationResult, int col, int row )
	{
		_recognizedPatterns[col][row] = correlationResult;
		if( correlationResult != null )
		{
			if( correlationResult.getPattern() != null ) 
			{
				if( isPieceCode( correlationResult.getPattern().getPieceCode() ) )
					putPieceAtPositionBase( correlationResult.getPattern().getType().charAt(0), col, row);
			}
			else
				putPieceAtPositionBase( null, col, row);
		}
	}

	public CorrelationResult getCorrelationResultAtPosition( int col, int row )
	{
		return( _recognizedPatterns[col][row] );
	}

	@Override
	public boolean isComplete()
	{
		boolean result = true;
		break1:
		for( int jj=1; jj<=NUM_OF_COLUMNS; jj++ )
			for( int ii=1; ii<=NUM_OF_ROWS; ii++ )
				if( !( result = _recognizedPatterns[jj][ii] != null ) )
					break break1;

		return( result );
	}

	public void setDetectedGrid( ChessBoardGridResult detectedGrid )
	{
		_detectedGrid = detectedGrid;
	}

	public ChessBoardGridResult getDetectedGrid( )
	{
		return( _detectedGrid );
	}

	@Override
	public void flipBoard()
	{
		super.flipBoard();

		flipBoardArray( _recognizedPatterns );
	}

	@Override
	public boolean equals( Object thatObj )
	{
		boolean result = result = ( this == thatObj );

		if( ! result )
		{
			result = ( thatObj instanceof ChessGamePositionImpl ) && super.equals( thatObj );

			if( result )
			{
				ChessGamePositionImpl that = (ChessGamePositionImpl) thatObj;

				if( ! result )
					result = arraysEqual( _recognizedPatterns, that._recognizedPatterns,
											(cr1, cr2) -> match( cr1, cr2 ) );
			}
		}

		return( result );
	}

	protected boolean match( CorrelationResult cr1, CorrelationResult cr2 )
	{
		boolean result = ( cr1 == cr2 );
		if( ! result && ( cr1 != null ) && ( cr2 != null ) )
		{
			ChessFigurePattern pt1 = cr1.getPattern();
			ChessFigurePattern pt2 = cr2.getPattern();
			result = pt1 == pt2;
		}

		return( result );
	}
}
