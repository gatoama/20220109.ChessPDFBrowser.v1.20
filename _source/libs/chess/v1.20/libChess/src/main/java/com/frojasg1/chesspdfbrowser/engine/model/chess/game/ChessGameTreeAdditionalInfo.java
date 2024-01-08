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
package com.frojasg1.chesspdfbrowser.engine.model.chess.game;

import com.frojasg1.chesspdfbrowser.engine.io.writers.WrittenNodeInfo;
import com.frojasg1.chesspdfbrowser.engine.io.writers.ChessGameViewWriterObserver;
import com.frojasg1.chesspdfbrowser.engine.io.writers.implementation.ChessGameViewWriter;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessBoard;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessGamePositionException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessModelException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessMoveException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessPieceCreationException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessWriterException;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import java.util.Iterator;

/**
 *
 * @author Usuario
 */
public class ChessGameTreeAdditionalInfo implements ChessGameViewWriterObserver
{
	protected ChessGame _parent = null;
	protected WrittenNodeInfo[] _arrayMoveAdditionalInfo = null;

	protected String _gameMovesString = null;

	protected int _currentCharPosition = 0;
	protected int _currentIndex = 0;

	public ChessGameTreeAdditionalInfo(ChessGame parent)
	{
		_parent = parent;
	}

	public String getMoveTreeString()
	{
		if( _gameMovesString == null )
		{
			try
			{
				updateAdditionalInfo();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
		
		return( _gameMovesString );
	}

	public WrittenNodeInfo getMoveAdditionalInfo( int charPosition )
	{
		WrittenNodeInfo result = null;

		if( _arrayMoveAdditionalInfo != null )
		{
			result = getMoveAdditionalInfo( charPosition, 0, _arrayMoveAdditionalInfo.length-1 );

			if( result != null )
			{
				if( !result.isInsideMove(charPosition) && !result.isInsideComment(charPosition) &&
					!result.isInsideCommentForVariant(charPosition) )
				{
					result = null;
				}
			}
		}

		return( result );
	}

	protected WrittenNodeInfo getMoveAdditionalInfo( int charPosition, int begin, int end )
	{
		WrittenNodeInfo result = null;

		int middle = ( begin + end ) / 2;

		if( checkIfIndexMatches( charPosition, begin ) )
		{
			result = _arrayMoveAdditionalInfo[begin];
		}
		else if( checkIfIndexMatches( charPosition, end ) )
		{
			result = _arrayMoveAdditionalInfo[end];
		}
		else if( checkIfIndexMatches( charPosition, middle ) )
		{
			result = _arrayMoveAdditionalInfo[middle];
		}
		else if( begin < end - 2 )
		{
			if( _arrayMoveAdditionalInfo[middle].getMinimumPosition() > charPosition )
			{
				result = getMoveAdditionalInfo( charPosition, begin, middle );
			}
			else
			{
				result = getMoveAdditionalInfo( charPosition, middle, end );
			}
		}

		return( result );
	}

	protected boolean checkIfIndexMatches( int charPosition, int index )
	{
		boolean result = ( ( _arrayMoveAdditionalInfo[index].getMinimumPosition() <= charPosition ) &&
							( ( index == (_arrayMoveAdditionalInfo.length - 1) ) ||
							( _arrayMoveAdditionalInfo[index+1].getMinimumPosition() > charPosition ) ) );
		return( result );
	}

	public int size()
	{
		return( ( _arrayMoveAdditionalInfo == null ) ? 0 : _arrayMoveAdditionalInfo.length );
	}

	public WrittenNodeInfo getElement( int index )
	{
		return( ( _arrayMoveAdditionalInfo == null ) ? null : _arrayMoveAdditionalInfo[index] );
	}

	public String updateAdditionalInfo()
		throws ChessGamePositionException, ChessPieceCreationException, ChessMoveException, ChessModelException, ChessWriterException
	{
		if( ( _parent != null ) && ( _parent.getChessViewConfiguration() != null ) )
		{
			_currentIndex = 0;
			_currentCharPosition = 0;

			_arrayMoveAdditionalInfo = null;
			int numberOfElements = _parent.getNumberOfMovesInTree();
//			int numberOfElements = _parent.getMoveTreeGame().getNumberOfTotalElements();
			if( numberOfElements > 0 )
			{
				_arrayMoveAdditionalInfo = new WrittenNodeInfo[ numberOfElements ];
			}

			boolean isToWriteInTextPane = true;
			ChessGameViewWriter writer = new ChessGameViewWriter( isToWriteInTextPane );

			_gameMovesString = writer.getGameMovesString(_parent, this );

			if( _gameMovesString != null )
			{
				_gameMovesString = _gameMovesString + " ";
			}
		}

		return( _gameMovesString );
	}

	@Override
	public void newWrittenNode( WrittenNodeInfo nodeInfo )
	{
		_arrayMoveAdditionalInfo[ _currentIndex ] = nodeInfo;
		_currentIndex++;
	}
}
