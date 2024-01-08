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
package com.frojasg1.chesspdfbrowser.engine.io.parsers.implementation.rawparser3;

import com.frojasg1.chesspdfbrowser.engine.exception.ChessParserException;
import static com.frojasg1.chesspdfbrowser.engine.io.parsers.implementation.rawparser3.RawImprovedChessGameParser3.MAX_NUMBER_OF_PLY_DIFF_TO_BE_THE_SAME_GAME;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.MoveToken;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.Token;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessBoard;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGamePosition;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.general.progress.CancellationException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class StartedSubvariantsList
{
	protected LinkedList<Element> _listOfOpenSubvariants = null;
	protected boolean _thereAreMovesSinceLastSubvariant = false;
	protected MoveTreeNode _currentNode = null;

	boolean _thereAreBrackets = false;

	protected MoveTreeNode _lastNodePreviousToPop = null;

	protected Token _nextProperToken = null;

	protected ChessBoard _chessBoard = new ChessBoard();

	protected ChessViewConfiguration _chessViewConfiguration = null;

	protected RawImprovedChessGameParser3 _parent = null;

	public void init(ChessViewConfiguration chessViewConfiguration,
						RawImprovedChessGameParser3 parent )
	{
		_parent = parent;
		_listOfOpenSubvariants = new LinkedList<>();
		_chessViewConfiguration = chessViewConfiguration;
	}

	public boolean isEmpty()
	{
		return( _listOfOpenSubvariants.isEmpty() );
	}
/*
	public MoveTreeNode getLast()
	{
		return( _listOfOpenSubvariants.getLast() );
	}
*/
	public void newSubvariant( )
	{
		_thereAreBrackets = true;
		if( !_thereAreMovesSinceLastSubvariant && !_listOfOpenSubvariants.isEmpty() )
		{
			_currentNode = _listOfOpenSubvariants.getLast().getMoveTreeNode();
		}

		Element elem = createElement(_currentNode, _thereAreMovesSinceLastSubvariant );
		_listOfOpenSubvariants.addLast(elem);

		_currentNode = null;
		_thereAreMovesSinceLastSubvariant = false;
	}

	public void setNextProperToken( Token nextProperToken )
	{
		_nextProperToken = nextProperToken;
	}

	protected boolean isSameMovePly( MoveToken mt, ChessGameMove cgm )
	{
		boolean result = false;

		if( ( mt != null ) && ( cgm != null ) )
			result = mt.getNumberOfPly() == cgm.getMoveToken().getNumberOfPly();

		return( result );
	}

	protected boolean isProbablyEndOfGame( MoveToken lastMoveToken, MoveToken mt )
	{
		boolean result = true;

		if( ( lastMoveToken != null ) && ( mt != null ) )
		{
			result = ( lastMoveToken.getNumberOfPly() + 1 > mt.getNumberOfPly() ) ||
					( ( lastMoveToken.getNumberOfPly() - mt.getNumberOfPly() ) < MAX_NUMBER_OF_PLY_DIFF_TO_BE_THE_SAME_GAME );
		}

		return( result );
	}

	protected MoveTreeNode getLastMoveIfMainLine( ChessGameMove cgm )
	{
		MoveTreeNode result = getLastMoveOfMainLine(_currentNode );

		int moveNumberOfPly = cgm.getMoveToken().getNumberOfPly();

		if( ( result == null ) || ( ( result.getLevel() + 1 ) != moveNumberOfPly ) ||
				!isProbablyMainLine( cgm ) )
		{
			result = null;
		}

		return( result );
	}

	protected boolean hasImage(ChessGameMove cgm)
	{
		return( _parent.isImageGap( cgm ) );
	}

	protected MoveTreeNode getCurrentNode( ChessGameMove cgm ) throws ChessParserException, CancellationException
	{
		MoveTreeNode result = _currentNode;

		int moveNumberOfPly = cgm.getMoveToken().getNumberOfPly();

		if( ! currentMoveHasJustBeenPop() )
		{
			MoveTreeNode lastIfMainLine = getLastMoveIfMainLine( cgm );
			if( lastIfMainLine != null )
			{
				return( lastIfMainLine );
			}
		}

		// just in case a closing bracket is missing
		if( ( result != null ) && currentMoveHasJustBeenPop() &&
			( ( result.getLevel() + 1 ) < moveNumberOfPly ) )
			result = _lastNodePreviousToPop;

		if( ( result == null ) && !isEmpty() )
			result = getLast(cgm);

		if( result != null )
		{
			if( result.getLevel() >= moveNumberOfPly )
			{
				if(  _thereAreBrackets && currentMoveHasJustBeenPop() || // just in case of missing bracket
					( ( result.getLevel() - moveNumberOfPly ) < MAX_NUMBER_OF_PLY_DIFF_TO_BE_THE_SAME_GAME ) &&
						!( ( ( result.getLevel() - moveNumberOfPly ) > 1 ) && hasImage(cgm) ) // if the move is not exactly the following, and it was an image, we consider it as a new game
					)
				{
					try
					{
						result = result.getParentGameTreeAtLevel( moveNumberOfPly - 1 );
					}
					catch( Exception ex )
					{
						result = null;
						ex.printStackTrace();
					}
				}
				else
					result = null;
			}
//			else if( ( result.getLevel() + 1 ) != moveNumberOfPly )
//				result = null;
		}

		return( result );
	}

	protected boolean currentMoveHasJustBeenPop()
	{
		return( _lastNodePreviousToPop != null );
	}

	protected MoveTreeNode getLastMoveOfMainLine( MoveTreeNode current )
	{
		MoveTreeNode result = null;

		if( current != null )
		{
			MoveTreeNode root = current.getRootNode();
			if( root != null )
				result = root.getEndOfMainLine();
		}

		return( result );
	}

	protected boolean isProbablyMainLine( ChessGameMove cgm )
	{
		boolean result = false;

//		LinkedList<Token> listOfTokensInAdvance = new LinkedList<>();
//		listOfTokensInAdvance.add( _nextProperToken );

		MoveToken lastMoveToken = (MoveToken) _nextProperToken;
/*
		int numberOfMovesAhead = 0;
		while( !result && ( numberOfMovesAhead < 100 ) && hasNextProperToken() )
		{
			listOfTokensInAdvance.add( _nextProperToken );
			if( _nextProperToken instanceof MoveToken )
			{
				MoveToken mt = (MoveToken) _nextProperToken;
				numberOfMovesAhead++;

				if( isSameMovePly( mt, cgm ) )
					break;

				if( isProbablyEndOfGame( lastMoveToken, mt ) )
					result = true;

				lastMoveToken = mt;
			}

			nextProperToken();
		}

		for( Token token: listOfTokensInAdvance )
			giveBack( token );

		if( hasNextProperToken() )
			nextProperToken();
*/
		result = lastMoveToken.getIsPossiblyMainLine();

		return( result );
	}

	public void resetLastNodePreviousToPop()
	{
		_lastNodePreviousToPop = null;
	}

	protected boolean closeSubvariant()
	{
		return( removeLast() );
	}

	protected boolean removeLast()
	{
		_lastNodePreviousToPop = _currentNode;
		MoveTreeNode result = null;

//		_thereAreMovesSinceLastSubvariant = false;
//		while( (result == null ) && mayBeTheFollowingMatches( cgm ) )
		while( ( result == null ) && !_listOfOpenSubvariants.isEmpty() )
		{
			Element elem = _listOfOpenSubvariants.removeLast();
//			if( matches( elem, cgm ) )
			{
				result = elem.getMoveTreeNode();
				_thereAreMovesSinceLastSubvariant = elem.getThereWhereMovesSinceLastSubvariant();
			}
		}

		_currentNode = result;

		return( _currentNode != null );
	}

	public void setCurrentNode( MoveTreeNode currentNode )
	{
		_currentNode = currentNode;
	}

	public MoveTreeNode getCurrentNode()
	{
		return( _currentNode );
	}

	protected MoveTreeNode getLast( ChessGameMove cgm )
	{
		MoveTreeNode result = null;

		if( !_listOfOpenSubvariants.isEmpty() )
		{
			Element elem = _listOfOpenSubvariants.getLast();
			result = elem.getMoveTreeNode();
			_thereAreMovesSinceLastSubvariant = elem.getThereWhereMovesSinceLastSubvariant();
		}

		return( result );
	}

	public void newMoveForLastSubvariant( MoveTreeNode currentMoveTreeNode )
	{
		_thereAreMovesSinceLastSubvariant = true;
		_currentNode = currentMoveTreeNode;
	}

	protected Element createElement( MoveTreeNode moveTreeNode,
										boolean thereAreMovesSinceLastSubvariant )
	{
		return( new Element( moveTreeNode, thereAreMovesSinceLastSubvariant ) );
	}

	protected boolean mayBeMainLine( ChessGameMove cgm )
	{
		return( getLastMoveIfMainLine( cgm ) != null );
	}

	protected boolean processMove(ChessGameMove cgm) throws ChessParserException, CancellationException
	{
		boolean success = true;

		_currentNode = processMove_internal( cgm );

		if( ( _currentNode != null ) )
		{
			success =  ( _currentNode instanceof MoveTreeGame ) ||
						( ( _currentNode.getLevel() + 1 ) == cgm.getMoveToken().getNumberOfPly() );

			if( !success )
			{
				int diff = cgm.getMoveToken().getNumberOfPly() - _currentNode.getLevel();
				if( ( diff > 1 ) && ( diff < 4 ) ) // discarding the move and letting continue, as it is only from 1 to 3 plies ahead and it must be a comment.
				{
					success = true;
				}
				else
					_currentNode = null;
			}
			else
			{
				if( currentMoveHasJustBeenPop() && _listOfOpenSubvariants.isEmpty() )
					_currentNode = _parent.insertFirst( _currentNode, cgm, cgm.getMoveToken().getNumberOfPly() );
				else
					_currentNode = _parent.simpleInsert( _currentNode, cgm, cgm.getMoveToken().getNumberOfPly() );

				if( startsFromInitialPosition( _currentNode ) )
					success = isLegal( _currentNode );

				if( ! success )
				{
					MoveTreeNode parent = _currentNode.getParent();
					_currentNode.remove();
					_currentNode = parent;
				}
			}

//			_listOfStartedSubvariants.setCurrentNode( _currentNode );
		}
		else
		{
			success = false;
		}

		return( success );
	}

	protected boolean startsFromInitialPosition( MoveTreeNode node )
	{
		boolean result = false;
		if( node != null )
		{
			result = ( node.getRootNode().getLevel() == 0 );
		}

		return( result );
	}

	protected ChessGame getChessGame( List<ChessGameMove> listOfMoves )
	{
		MoveTreeGame mtg = new MoveTreeGame( ChessGamePosition.getInitialPosition() );
		ChessGame result = null;
		try
		{
			result = new ChessGame( null, mtg, _chessViewConfiguration );
			mtg.insertMoves(listOfMoves);
			result.start();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	protected boolean isLegal( MoveTreeNode mtn )
	{
		boolean result = false;
		
		List<ChessGameMove> list = mtn.getGameMoveList();
		
		try
		{
			ChessGame cg = getChessGame( list );
			cg.getChessGameTreeAdditionalInfo().updateAdditionalInfo();

			_chessBoard.doMovesFromInitialPosition( ChessGamePosition.getInitialPosition(),
													cg.getMoveTreeGame().getEndOfMainLine().getGameMoveList() );
			result = true;
		}
		catch( Exception ex )
		{
			result = false;
		}

		return( result );
	}

	protected MoveTreeNode processMove_internal(ChessGameMove cgm) throws ChessParserException, CancellationException
	{
		boolean success = true;

		if( ( _currentNode == null ) ||
			( ( currentMoveHasJustBeenPop() &&
				( _currentNode.getLevel() + 1 ) < cgm.getMoveToken().getNumberOfPly() ) ||
				( ( _currentNode.getLevel() + 1 ) > cgm.getMoveToken().getNumberOfPly() ) ||
				mayBeMainLine( cgm )
			) &&
			( cgm.getMoveToken().getNumberOfPly() > 1 ) )
		{
			_currentNode = getCurrentNode( cgm );
		}

		newMoveForLastSubvariant( _currentNode );

		return( _currentNode );
	}

	protected class Element
	{
		protected MoveTreeNode _moveTreeNode = null;
		protected boolean _thereWhereMovesSinceLastSubvariant = false; 

		public Element( MoveTreeNode moveTreeNode,
						boolean thereWhereMovesSinceLastSubvariant )
		{
			_thereWhereMovesSinceLastSubvariant = thereWhereMovesSinceLastSubvariant;
			_moveTreeNode = moveTreeNode;
		}

		public MoveTreeNode getMoveTreeNode()
		{
			return( _moveTreeNode );
		}

		public boolean getThereWhereMovesSinceLastSubvariant()
		{
			return( _thereWhereMovesSinceLastSubvariant );
		}
	}
}
