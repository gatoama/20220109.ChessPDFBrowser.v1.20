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

import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGamePosition;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author Usuario
 */
public class MoveTreeGame extends MoveTreeNode
{
	protected ChessGamePosition _initialPosition = null;
	protected int _numberOfElements = 0;
	protected int _levelOfFirstMove = -1;	// if the movetree begins with an initialposition different from begin of game, the level of the first move in the tree
											// must be : MOVE * 2 - 1 if white to play, and MOVE * 2 if black to play.

	protected ChessGame _game = null;

	public MoveTreeGame( ChessGamePosition initialPosition )
	{
		super( null, null, 0 ); // we create the root of the tree. It has no parent, no moves and is at level 0.
		_initialPosition = initialPosition;
	}

	public ChessGamePosition getInitialPosition()
	{
		return( _initialPosition );
	}

	public void setChessGame( ChessGame game )
	{
		_game = game;
	}

	@Override
	public ChessGame getChessGame()
	{
		return( _game );
	}

	protected boolean levelCanMatchTurn( MoveTreeNode mtn, int newLevel )
	{
		return( ( mtn == null ) || ( ( mtn.getLevel() % 2 ) != ( newLevel % 2 ) ) ); // to match they must be different
	}

	protected boolean levelsMatch( MoveTreeNode mtn, ChessGamePosition cgp )
	{
		return( ( mtn.getLevel() == cgp.getPlyNumber() ) );
	}

	// only if necessary, in case the just inserted node or position does not match with the other
	protected void updateInitialPosition()
	{
		MoveTreeNode mtn = getFirstChild();
		if( ( mtn != null ) && ( _initialPosition != null ) &&
			_initialPosition.canBeBaseFenString() && // if move number or any parameter is set, we do not execute the following
				!levelsMatch( mtn, _initialPosition )
			)
		{
			onlyUpdateInitialPosition();
		}
		updateLevel();
	}

	protected void onlyUpdateInitialPosition()
	{
		MoveTreeNode mtn = getFirstChild();
		if( ( mtn != null ) && ( _initialPosition != null )	)
		{
//			if( getNumberOfChildren() == 1 )
			{
				if( ( mtn.getLevel() != 1 ) && _initialPosition.isStandardInitialPosition() )
					_initialPosition = null;
				else
				{
					_initialPosition.setMoveNumber( mtn.getMoveNumber() );
					if( mtn.wasWhiteToMove() )
						_initialPosition.setIsWhitesTurn();
					else
						_initialPosition.setIsBlacksTurn();
				}
			}
		}
	}

	// only if necessary
	protected void updateLevel()
	{
		int newLevel = 0;
		if( _initialPosition != null )
			newLevel = _initialPosition.getPlyNumber() - 1;
		else
		{
			MoveTreeNode mtn = getFirstChild();
			if( mtn != null )
				newLevel = mtn.getLevel() - 1;
		}

		MoveTreeNode fc = getFirstChild();
		if( ( fc != null ) && ( newLevel != getLevel() ) )
		{
			if( !levelCanMatchTurn( fc, newLevel ) )
				updateLevel( newLevel );
			else
			{
				onlyUpdateInitialPosition();
				updateLevel();
			}
		}
	}

	public void setInitialPosition( ChessGamePosition cgp )
	{
		if( cgp != null )
		{
			_initialPosition = cgp;

			if( getNumberOfChildren() == 0 )
				updateLevel();
			else
				updateInitialPosition();
		}
		else
			setLevel(0);
	}

	public void setLevelOfFirstMove( int levelOfFirstMove )
	{
		_levelOfFirstMove = levelOfFirstMove;
	}

	public boolean isValid()
	{
		return( getInitialPosition() != null );
	}

//	public int getNumberOfTotalElements()		{	return( _numberOfElements );	}

/*
	public MoveTreeNode insertMoves( List<ChessGameMove> listOfMoves )
	{
		MoveTreeNode result = null;

		if( ( listOfMoves != null ) && ( listOfMoves.size() > 0 ) )
		{
			MoveTreeNode node = this;
			MoveTreeNode parent = null;
			ChessGameMove move = null;
			int depth = 0;

			Iterator<ChessGameMove> it = listOfMoves.iterator();
			while( ( node != null ) && ( it.hasNext() ) )
			{
				move = it.next();
				parent = node;
				node = node.findChild( move );
				depth++;
			}

			if( ( node == null ) && ( parent != null ) )
			{
				do
				{
					if( node != null )	move = it.next();
					int level = parent.getLevel() + 1;
					if( ( depth == 1 ) && (_levelOfFirstMove > 0 ) ) level = _levelOfFirstMove;
					node = parent.simpleInsert(move, level);
					parent = node;
					_numberOfElements++;
				}
				while( it.hasNext() );
			}
			result = node;
		}

		return( result );
	}
*/

	public MoveTreeNode insertMoves( List<ChessGameMove> listOfMoves )
	{
		MoveTreeNode result = this;

		if( ( listOfMoves != null ) && ( listOfMoves.size() > 0 ) )
		{
			int level = ( (_levelOfFirstMove > 0 ) ? _levelOfFirstMove : result.getLevel() + 1 );

			Iterator<ChessGameMove> it = listOfMoves.iterator();
			while( it.hasNext() )
			{
				ChessGameMove move = it.next();
				MoveTreeNode old = result;
				result = result.findChild(move);
				if( result == null )
				{
					result = old.simpleInsert(move, level);
					_numberOfElements++;
				}
				level = result.getLevel() + 1;
			}
		}

		return( result );
	}
/*
	public MoveTreeNode findNode( List<ChessGameMove> listOfMoves, int numberOfMoves )
	{
		MoveTreeNode result = this;

		if( ( listOfMoves != null ) && ( listOfMoves.size() > 0 ) )
		{
			ChessGameMove move = null;

			Iterator<ChessGameMove> it = listOfMoves.iterator();
			int ii=0;
			for( ii=0; ( result != null ) && ( it.hasNext() ) && ii<numberOfMoves; ii++ )
			{
				move = it.next();
				result = result.findChild( move );
			}
		}

		return( result );
	}
*/
	public MoveTreeNode findNode( List<ChessGameMove> listOfMoves, int numberOfMoves )
	{
		return( findNode( listOfMoves, numberOfMoves, 0, this ) );
	}

	public MoveTreeNode findNode( List<ChessGameMove> listOfMoves, int numberOfMoves,
									int indexOfMove, MoveTreeNode node )
	{
		MoveTreeNode result = null;

		if( (node != null) &&
			( listOfMoves != null ) && ( listOfMoves.size() > indexOfMove ) )
		{
			ChessGameMove move = listOfMoves.get(indexOfMove);

			for( MoveTreeNode child: node.getChildrenList() )
			{
				if( child.getMove().equals( move ) )
				{
					if( numberOfMoves == ( indexOfMove + 1 ) )
						result = child;
					else
						result = findNode(listOfMoves, numberOfMoves, indexOfMove + 1, child);

					if( result != null )
						break;
				}
			}
		}

		return( result );
	}

	public MoveTreeNode findNode( List<ChessGameMove> listOfMoves )
	{
		MoveTreeNode result = this;

		if( listOfMoves != null )
		{
			result = findNode( listOfMoves, listOfMoves.size() );
		}

		return( result );
	}

	@Override
	public MoveTreeNode simpleInsert( ChessGameMove cgm, int level )
	{
		if( getNumberOfChildren() == 0 )
			_level = level - 1;
/*
		if( ( _level != 0 ) &&
			( ( _initialPosition == null ) ||
				( _initialPosition.equals( ChessGamePosition.getInitialPosition() ) )
			)
		  )
		{
			_initialPosition = null;
		}
		else
			setInitialPosition( ChessGamePosition.getInitialPosition() );
*/
		MoveTreeNode result = super.simpleInsert(cgm, level);

		return( result );
	}

	protected void checkIfNumberOfChildrenIsZero()
	{
		if( ( getNumberOfChildren() == 0 ) &&
			( _initialPosition == null )
			)
		{
			setInitialPosition( ChessGamePosition.getInitialPosition() );
			_level = 0;
		}
	}

	@Override
	public void remove()
	{
		super.remove();

		checkIfNumberOfChildrenIsZero();
	}

	@Override
	public void removeChild( MoveTreeNode mtn )
	{
		super.removeChild( mtn );

		checkIfNumberOfChildrenIsZero();
	}
	
	@Override
	public void setLevel( int level )
	{
		super.setLevel( level );
	}

	@Override
	public ChessGamePosition getPreviousPosition()
	{
		return( getInitialPosition() );
	}

	public MoveTreeNode getLastNodeOfMainVariant()
	{
		MoveTreeNode result = this;
		while(true)
		{
			MoveTreeNode next = result.getChild(0);
			if( next != null )
				result = next;
			else
				break;
		}

		return( result );
	}

	public boolean wasWhiteToMove()
	{
		// negated as next turn is White to move. Previous move was for the other player.
		return( ! getInitialPosition().getIsWhitesTurn() );
	}

	@Override
	protected int getLevelForInsertion( MoveTreeNode nodeToInsert )
	{
		int result = super.getLevelForInsertion(nodeToInsert);

//		if( ( ( getInitialPosition() == null ) || getInitialPosition().isStandardInitialPosition() )
//			&& ( getNumberOfChildren() == 0 ) )
		if( getNumberOfChildren() == 0 )
		{
			result = nodeToInsert.getLevel();
		}

		return( result );
	}

	@Override
	protected MoveTreeNode simpleInsertGen( ChessGameMove cgm, int level, Consumer<MoveTreeNode> inserter )
	{
		boolean hasToUpdatePosition = ( getNumberOfChildren() == 0 );

		MoveTreeNode result = super.simpleInsertGen(cgm, level, inserter);

		if( hasToUpdatePosition )
			updateInitialPosition();

		return( result );
	}
}
