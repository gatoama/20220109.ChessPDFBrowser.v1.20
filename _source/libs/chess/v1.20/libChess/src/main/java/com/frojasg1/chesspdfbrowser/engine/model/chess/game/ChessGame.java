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

import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessBoard;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGamePosition;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessGamePositionException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessModelException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessMoveException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessPieceCreationException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessWriterException;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import com.frojasg1.chesspdfbrowser.engine.io.writers.WrittenNodeInfo;
import com.frojasg1.chesspdfbrowser.engine.io.writers.implementation.ChessGameViewWriter;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGameResult;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNodeUtils;
import com.frojasg1.general.number.IntegerFunctions;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class ChessGame 
{
	protected ChessBoard _chessBoard = null;

	protected ChessGameHeaderInfo _headerInfo = null;
	protected ChessGameTreeAdditionalInfo _chessGameTreeAdditionalInfo = null;
	protected MoveTreeGame _moveTreeGame = null;

	protected ChessViewConfiguration _chessViewConfiguration = null;

	protected MoveTreeNode _currentMove = null;

	protected List<ChessGameMove> _currentListOfMoves = null;

	protected int _currentPositionInListOfMoves = -1;
	protected MoveTreeNode _currentMoveAtEndOfCurrentListOfMoves = null;

	public ChessGame( ChessViewConfiguration cvc ) throws ChessGamePositionException, ChessPieceCreationException
	{
		this( null, null, cvc );
	}

	public ChessGame( ChessGameHeaderInfo headerInfo,
						MoveTreeGame moveTreeGame,
						ChessViewConfiguration chessViewConfiguration
						) throws ChessGamePositionException, ChessPieceCreationException
	{
		if( headerInfo != null )
			_headerInfo = headerInfo;
		else
		{
			_headerInfo = new ChessGameHeaderInfo();
		}
		
		_chessViewConfiguration = chessViewConfiguration;

		if( moveTreeGame != null )
		{
			_moveTreeGame = moveTreeGame;
		}
		else
		{
			_moveTreeGame = new MoveTreeGame( ChessGamePosition.getInitialPosition() );
		}

		_moveTreeGame.setChessGame( this );

		_currentMove = _moveTreeGame;
		_chessGameTreeAdditionalInfo = new ChessGameTreeAdditionalInfo( this );

		_chessBoard = new ChessBoard();
		
		if( _moveTreeGame.getInitialPosition() != null )
			_chessBoard.setPosition( _moveTreeGame.getInitialPosition() );

		_currentListOfMoves = new ArrayList<ChessGameMove>();
	}

	public ChessGameHeaderInfo getChessGameHeaderInfo()
	{
		return( _headerInfo );
	}

	public void setChessGameHeaderInfo( ChessGameHeaderInfo cghi )
	{
		_headerInfo = cghi;
	}

	public void setChessViewConfiguration( ChessViewConfiguration cvc )
	{
		_chessViewConfiguration = cvc;
	}

	public ChessViewConfiguration getChessViewConfiguration()
	{
		return( _chessViewConfiguration );
	}

	public ChessGamePosition getInitialPosition()
	{
		return( _moveTreeGame.getInitialPosition() );
	}

	public void setInitialPosition( ChessGamePosition cgp )
	{
		if( cgp != null )
		{
			ChessGamePosition previous = _moveTreeGame.getInitialPosition();
			try
			{
				ChessGamePosition copiedPosition = MoveTreeNodeUtils.instance().copy( cgp );
				_moveTreeGame.setInitialPosition( cgp );
				_chessBoard.setPosition( cgp );
				String fenString = cgp.getFenString();
				if( ! fenString.equals( ChessGamePosition.INITIAL_POSITION_FEN_STRING ) )
				{
					_headerInfo.put( ChessGameHeaderInfo.FEN_TAG, fenString );
					_headerInfo.put( ChessGameHeaderInfo.SETUP_TAG, "1" );
				}
				else
				{
					_headerInfo.remove( ChessGameHeaderInfo.FEN_TAG );
					_headerInfo.remove( ChessGameHeaderInfo.SETUP_TAG );
				}
			}
			catch( Throwable th )
			{
				th.printStackTrace();

				_moveTreeGame.setInitialPosition( previous );
				try
				{
					_chessBoard.setPosition( previous );
				}
				catch( Throwable th1 )
				{}
			}
		}
		else
			_moveTreeGame.setLevel(0);
	}

	public MoveTreeGame getMoveTreeGame()
	{
		return( _moveTreeGame );
	}

	public ChessBoard getChessBoard()
	{
		return( getInitialPosition() == null ? null : _chessBoard );
	}

	public ChessGameTreeAdditionalInfo getChessGameTreeAdditionalInfo()
	{
		return( _chessGameTreeAdditionalInfo );
	}

	public boolean isValid()
	{
		return( getChessBoard() != null );
	}

	public String updateAdditionalInfo( ) throws ChessGamePositionException, ChessPieceCreationException, ChessMoveException, ChessModelException, ChessWriterException
	{
		_chessGameTreeAdditionalInfo = new ChessGameTreeAdditionalInfo( this );

		_chessGameTreeAdditionalInfo.updateAdditionalInfo();

		return( _chessGameTreeAdditionalInfo.getMoveTreeString() );
	}

	public int getNumberOfMovesInTree()
	{
		int result = 0;

		if( _moveTreeGame != null ) result = _moveTreeGame.getNumberOfTotalElements();

		return( result );
	}

	public MoveTreeNode insertMoves( List<ChessGameMove> listOfMoves )
		throws ChessGamePositionException, ChessPieceCreationException, ChessMoveException, ChessModelException, ChessWriterException
	{
		MoveTreeNode result = _moveTreeGame.insertMoves(listOfMoves);
//		updateAdditionalInfo();

		return( result );
	}

	public boolean doMovesFromInitialPosition( List<ChessGameMove> listOfMoves ) throws ChessGamePositionException, ChessPieceCreationException, ChessMoveException
	{
		return( setCurrentMove( findNode( listOfMoves ) ) );
	}

	public MoveTreeNode getCurrentMove()
	{
		return( _currentMove );
	}
	
	public void start() throws ChessMoveException, ChessPieceCreationException, ChessGamePositionException
	{
		if( isValid() && ( _chessBoard != null ) )
		{
			if( setCurrentMove( _moveTreeGame ) )
				_currentPositionInListOfMoves = -1;
		}
	}

	protected MoveTreeNode getNextMoveFromCurrentMoveAtEndOfCurrentListOfMoves()
	{
		MoveTreeNode result = _currentMoveAtEndOfCurrentListOfMoves;
		for( int ii=_currentPositionInListOfMoves + 1;
			(result != null) && (ii<(_currentListOfMoves.size()-1)); ii++ )
		{
			result = result.getParent();
		}
		return( result );
	}

	protected MoveTreeNode getNextMoveTreeNodeUnCurrentListOfMoves()
	{
		MoveTreeNode result = getNextMoveFromCurrentMoveAtEndOfCurrentListOfMoves();

		if( result == null )
			result = _currentMove.findChild( _currentListOfMoves.get( _currentPositionInListOfMoves + 1 ) );

		if( result == null )
			result = _currentMove.getChild(0);

		return( result );
	}

	public void forward() throws ChessMoveException, ChessPieceCreationException, ChessGamePositionException
	{
		if( isValid() && ( _chessBoard != null ) )
		{
			// if we are still inside the list of moves, we choose the next move. If not, we choose the next main line move
			if( _currentPositionInListOfMoves < (_currentListOfMoves.size()-1) )
			{
//				MoveTreeNode mtn = _currentMove.findChild( _currentListOfMoves.get( _currentPositionInListOfMoves + 1 ) );
				MoveTreeNode mtn = getNextMoveTreeNodeUnCurrentListOfMoves();
				if( mtn != null )	// mnt should never be null, but we check it.
				{
					if( setCurrentMove( mtn ) )
						_currentPositionInListOfMoves++;
				}
			}
			else if( _currentMove.getNumberOfChildren() > 0 )
			{
				if( setCurrentMove( _currentMove.getChild(0) ) )
					_currentPositionInListOfMoves++;
			}
		}
	}
	
	public void back() throws ChessMoveException, ChessPieceCreationException, ChessGamePositionException
	{
		if( isValid() && ( _chessBoard != null ) )
		{
			if( ( _currentMove.getParent() != null ) &&
				( _currentPositionInListOfMoves >= 0 ) )
			{
				if( setCurrentMove( _currentMove.getParent() ) )
					_currentPositionInListOfMoves--;
			}
		}
	}

	public void end() throws ChessMoveException, ChessPieceCreationException, ChessGamePositionException
	{
		if( isValid() && ( _chessBoard != null ) )
		{
			MoveTreeNode mtn = findNode( _currentListOfMoves );

			if( mtn != null )
			{
				if( setCurrentMove( mtn.getEndOfMainLine() ) )
					_currentPositionInListOfMoves = _currentMove.getLevel() - _moveTreeGame.getChild(0).getLevel();
			}
		}
	}

	public boolean setCurrentMove( MoveTreeNode mtn ) throws ChessMoveException, ChessPieceCreationException, ChessGamePositionException
	{
		boolean result = false;
		MoveTreeNode previous = _currentMove;
		try
		{
			setCurrentMove_internal( mtn );
			result = true;

			WrittenNodeInfo wni = null;
			if( mtn != null )
				wni = mtn.getAdditionalInfo();

			if( ( wni != null ) && ( wni.getIsIllegalMove() || wni.getIsAnyParentIllegalMove() ) )
			{
				_chessBoard.setCurrentNodeIsIllegal( true );
			}
		}
		catch( Throwable th )
		{
			setCurrentMove_internal( previous );	// if is has been some error, we reverse the operation.
			// currently is always allowed, even though the move is illegal or there is not an initial position.
		}
		return( result );
	}
/*
	protected void setCurrentMove_internal( MoveTreeNode mtn ) throws ChessMoveException, ChessPieceCreationException, ChessGamePositionException
	{
		if( ( mtn != null ) && ( _chessBoard != null ) && isValid() )
		{
			_currentMove = mtn;
			_chessBoard.doMovesFromInitialPosition( _moveTreeGame.getInitialPosition(), _currentMove.getGameMoveList() );
		}
	}
*/
	protected void setCurrentMove_internal( MoveTreeNode mtn ) throws ChessMoveException, ChessPieceCreationException, ChessGamePositionException
	{
		if( ( mtn != null ) &&
			( (mtn instanceof MoveTreeGame) || ( mtn.isParent( getMoveTreeGame() ) ) ) )
		{
			_currentMove = mtn;
			if(  ( _chessBoard != null ) && isValid() &&
				!( ( mtn.getAdditionalInfo() != null ) &&
					( mtn.getAdditionalInfo().getIsAnyParentIllegalMove() || 
						mtn.getAdditionalInfo().getIsIllegalMove() ) )
				)
			{
				_chessBoard.doMovesFromInitialPosition( _moveTreeGame.getInitialPosition(), _currentMove.getGameMoveList() );
				updateGameResult();
			}
		}
	}

	protected void updateGameResult()
	{
		if( _currentMove != null )
		{
			ChessGameMove cgm = _currentMove.getMove();
			if( cgm != null )
				cgm.setResultOfGame( getGameResultString() );
		}
	}

	protected String getGameResultString()
	{
		String result = null;
		ChessGameResult gameResult = _chessBoard.getChessGameResult();
		if( gameResult != null )
		{
			switch( gameResult )
			{
				case GAME_CONTINUES:		result = null; break;
				case WHITE_WINS:		result = "1-0"; break;
				case BLACK_WINS:		result = "0-1"; break;
				case WHITE_WINS_CHECK_MATE:		result = "1-0"; break;
				case BLACK_WINS_CHECK_MATE:		result = "0-1"; break;
				case DRAW:		result = "1/2-1/2"; break;
				case DRAW_STALE_MATE:		result = "1/2-1/2"; break;
				case DRAW_THIRD_REPETITION:		result = "1/2-1/2"; break;
				case DRAW_FIFTY_MOVES_WITHOUT_PROGRESS:		result = "1/2-1/2"; break;
				case DRAW_MUTUAL_AGREEMENT:		result = "1/2-1/2"; break;
				case DRAW_INSUFFICIENT_MATERIAL:		result = "1/2-1/2"; break;
			}
		}

		return( result );
	}



//	public void setCurrentListOfMoves( MoveTreeNode node,
//										List<ChessGameMove> listOfMoves ) throws ChessMoveException, ChessPieceCreationException, ChessGamePositionException
	public void setCurrentListOfMoves( MoveTreeNode node ) throws ChessMoveException, ChessPieceCreationException, ChessGamePositionException
	{
//		if( isValid() && ( _chessBoard != null ) )
		if( _chessBoard != null )
		{
//			MoveTreeNode mtn = findNode( listOfMoves );

			if( node != null )
			{
				List<ChessGameMove> listOfMoves = node.getGameMoveList();

				_currentMoveAtEndOfCurrentListOfMoves = node;
				_currentListOfMoves = listOfMoves;
				if( setCurrentMove( node ) )
					_currentPositionInListOfMoves = listOfMoves.size()-1;
			}
		}
	}

	public void setCurrentListOfMoves( List<ChessGameMove> listOfMoves ) throws ChessMoveException, ChessPieceCreationException, ChessGamePositionException
	{
//		if( isValid() && ( _chessBoard != null ) )
		if( _chessBoard != null )
		{
			MoveTreeNode mtn = findNode( listOfMoves );
			setCurrentListOfMoves( mtn );
		}
	}

	public MoveTreeNode changeMoveDueToSelectionInView( int charPosition ) throws ChessMoveException, ChessPieceCreationException, ChessGamePositionException
	{
		MoveTreeNode result = null;
		WrittenNodeInfo nodeInfo = _chessGameTreeAdditionalInfo.getMoveAdditionalInfo(charPosition);
		if( nodeInfo != null )
		{
			result = nodeInfo.getMoveTreeNode();
			setCurrentMove( result );
			result = _currentMove;					// as if there was an error setting the new move.
		}
		return( result );
	}

	public MoveTreeNode findNode( List<ChessGameMove> listOfMoves )
	{
		MoveTreeNode result = null;

		if( ( _moveTreeGame != null ) && ( listOfMoves != null ) )
		{
			result = _moveTreeGame.findNode(listOfMoves);
		}

		return( result );
	}

	public String getMoveListString( List<ChessGameMove> listOfMoves ) throws ChessWriterException
	{
		ChessGameViewWriter pgnw = new ChessGameViewWriter( true );

		List<ChessGameMove> firstListOfMoves = ( listOfMoves != null ? listOfMoves : _chessBoard.getListOfMoves() );

		return( pgnw.getGameMovesStringForLine(this, firstListOfMoves ) + "\n" + 
				pgnw.getGameMovesStringForLine(this, _currentMove.getGameMoveList() ) + "\n" );
	}

	public String getMoveListString( MoveTreeNode mtn ) throws ChessWriterException
	{
		return( getMoveListString( mtn, this.getChessViewConfiguration() ) );
	}

	public String getMoveListString( MoveTreeNode mtn, ChessViewConfiguration cvc ) throws ChessWriterException
	{
		ChessGameViewWriter pgnw = new ChessGameViewWriter( true );

		String result = pgnw.getGameMovesStringForLine(this, mtn, cvc );
		return( result );
	}

	public int getNumberOfMovesOfMainLine()
	{
		int numberOfMoves = 0;

		if( ( _moveTreeGame != null ) && ( _moveTreeGame.getNumberOfChildren() > 0 ) )
		{
			MoveTreeNode mtn = _moveTreeGame.getEndOfMainLine();
			numberOfMoves = ( mtn.getLevel() - _moveTreeGame.getChild(0).getLevel() ) / 2 + 1;
		}

		return( numberOfMoves );
	}

	public int getTotalNumberOfMoves()
	{
		int numberOfMoves = 0;

		if( ( _moveTreeGame != null ) && ( _moveTreeGame.getNumberOfChildren() > 0 ) )
		{
			numberOfMoves = _moveTreeGame.getTotalNumberOfMoves();
		}

		return( numberOfMoves );
	}

	protected void changeCurrentsBeforeRemoving( MoveTreeNode node )
	{
		if( _currentMoveAtEndOfCurrentListOfMoves != null )
		{
			if( ( node == _currentMoveAtEndOfCurrentListOfMoves ) ||
				_currentMoveAtEndOfCurrentListOfMoves.isParent(node) )
			{
				if( _currentListOfMoves != null )
				{
					while( _currentListOfMoves.size() > node.getLevel() - 1 )
						_currentListOfMoves.remove( _currentListOfMoves.size() - 1 );

					_currentMoveAtEndOfCurrentListOfMoves = node.getParent();
					_currentPositionInListOfMoves = IntegerFunctions.min(_currentPositionInListOfMoves, _currentListOfMoves.size() - 1 );
				}
			}
		}
	}

	public void remove( MoveTreeNode node )
	{
		changeCurrentsBeforeRemoving( node );
		node.remove();
	}
}
