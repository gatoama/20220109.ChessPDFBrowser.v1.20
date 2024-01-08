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
package com.frojasg1.chesspdfbrowser.recognizer.recognizer.impl;

import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputImage;
import com.frojasg1.chesspdfbrowser.engine.position.impl.ChessGamePositionImpl;
import com.frojasg1.chesspdfbrowser.recognizer.configuration.ChessRecognizerApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.recognizer.configuration.FlipBoardMode;
import java.util.Objects;
import java.util.function.Predicate;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessBoardPositionRecognizerWithStoreImplFlippingBoard
	extends ChessBoardPositionRecognizerWithStoreImpl
{
	protected ChessRecognizerApplicationConfiguration _appliConf;

	public ChessBoardPositionRecognizerWithStoreImplFlippingBoard(ChessRecognizerApplicationConfiguration appliConf)
	{
		super();
		_appliConf = appliConf;
	}

	protected ChessRecognizerApplicationConfiguration getAppliConf()
	{
		return( _appliConf );
	}


	@Override
	public String recognizeBoardFen(InputImage image)
	{
		String fenStr = super.recognizeBoardFen(image);

		if( _positionDetected != null )
		{
			_positionDetected = flipBoardIfNecessary( _positionDetected );
			_result.setDetectedPosition(_positionDetected);
			fenStr = _positionDetected.getFenBoardStringBase();
		}

		return( fenStr );
	}

	protected ChessGamePositionImpl flipBoardIfNecessary( ChessGamePositionImpl positionDetected )
	{
		ChessGamePositionImpl result = positionDetected;
		if( positionDetected != null )
		{
			if( hasToFlipBoard( positionDetected ) )
				result = flipBoard( positionDetected );
		}

		return( result );
	}

	protected ChessGamePositionImpl flipBoard( ChessGamePositionImpl positionDetected )
	{
		ChessGamePositionImpl result = positionDetected;
		result.flipBoard();

		return( result );
	}

	protected boolean hasToFlipBoard( ChessGamePositionImpl positionDetected )
	{
		boolean result = false;

		FlipBoardMode fbm = getAppliConf().getChessBoardRecognizedFlipBoardMode();
		if( Objects.equals( fbm, FlipBoardMode.BLACK_ON_THE_BOTTOM ) )
			result = true;
		else if( Objects.equals( fbm, FlipBoardMode.WHITE_ON_THE_BOTTOM ) )
			result = false;
		else if( Objects.equals( fbm, FlipBoardMode.AUTO ) )
			result = ! probablyWhitePlaysOnBottom( positionDetected );

		return( result );
	}

	protected double calculateRank( ChessGamePositionImpl positionDetected,
									Predicate<Character> conditionToMatchPieceColor )
	{
		double result = 0;
		int count = 0;
		int rankCount = 0;
		Character[][] position = positionDetected.getPosition();
		for( int jj=1; jj<ChessGamePositionImpl.NUM_OF_COLUMNS; jj++ )
			for( int ii=1; ii<ChessGamePositionImpl.NUM_OF_ROWS; ii++ )
			{
				Character pieceChar = position[jj][ii];
				if( ( pieceChar != null ) && ( conditionToMatchPieceColor.test( pieceChar ) ) )
				{
					count++;
					rankCount += ii;
				}
			}

		if( count > 3 )
			result = ( (double) rankCount ) / count;

		return( result );
	}

	protected boolean probablyWhitePlaysOnBottom( ChessGamePositionImpl positionDetected )
	{
		boolean result = true;

		double averageRankForWhite = calculateRank( positionDetected, (ch) -> ( ch.charValue() < 'a' ) );
		double averageRankForBlack = calculateRank( positionDetected, (ch) -> ( ch.charValue() > 'Z' ) );

		if( ( averageRankForWhite == 0d ) || ( averageRankForBlack == 0d ) )
			result = true;
		else
			result = averageRankForWhite <= averageRankForBlack;

		return( result );
	}
}
