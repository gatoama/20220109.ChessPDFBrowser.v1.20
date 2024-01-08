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
import com.frojasg1.chesspdfbrowser.engine.io.writers.WrittenNodeInfo;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessMoveException;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.MoveToken;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessBoard;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGamePosition;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.general.NullFunctions;
import com.frojasg1.libpdf.api.ImageWrapper;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Usuario
 */
public class MoveTreeNode implements Serializable
{
	protected WrittenNodeInfo _additionalInfo = null;

	protected int _level = 0;		// it gives the number of the move. The first move of the game is 1 (first move of white), the first move of black is 2.

	protected MoveTreeNode _parent = null;
	protected LinkedList<MoveTreeNode> _children = null;

	protected ChessGameMove _move = null;
	protected String _comment = null;

	protected String _commentForVariant = null;

//	protected List<NAG> _nagList = null;

	protected ImageWrapper _image = null;

	protected MoveTreeNode( MoveTreeNode parent, ChessGameMove cgm, int level )
	{
		_parent = parent;

		if( cgm != null )
		{
			_move = new ChessGameMove( cgm );
			_move.setHasBeenChecked(false);

			if( _move.getMoveToken() != null )
				_comment = _move.getMoveToken().getComment();
		}

		_level = level;

		_children = new LinkedList<MoveTreeNode>();
//		_nagList = new ArrayList<NAG>();
	}

	public MoveTreeNode getFirstChild()
	{
		return( _children.isEmpty() ? null : _children.getFirst() );
	}

	protected MoveTreeNode getAncestor()
	{
		MoveTreeNode result = this;
		while( result.getParent() != null )
			result = result.getParent();

		return( result );
	}

	public ChessGame getChessGame()
	{
		ChessGame result = null;
		MoveTreeNode ancestor = getAncestor();

		if( ancestor instanceof MoveTreeGame )
		{
			result = ( (MoveTreeGame) ancestor ).getChessGame();
		}

		return( result );
	}

	public void setImage( ImageWrapper image )
	{
		_image = image;
	}

	public ImageWrapper getImage()
	{
		return( _image );
	}

	public boolean isFirstChild()
	{
		return( ( getParent() != null ) && ( getParent()._children.indexOf( this ) == 0 ) );
	}

	public void setAdditionalInfo( WrittenNodeInfo ai )
	{
		_additionalInfo = ai;
	}

	public WrittenNodeInfo getAdditionalInfo()
	{
		return( _additionalInfo );
	}

/*
	public void addNag( NAG nag )
	{
		_nagList.add( nag );
	}

	public Iterator<NAG> getNagIterator()
	{
		return( _nagList.iterator() );
	}
*/
	public MoveTreeNode getParentGameTreeAtLevel( int level ) throws ChessMoveException
	{
		if( (level < 0 ) || (level > _level+1) )
			throw( new ChessMoveException(	String.format( getChessStrConf().getProperty( ChessStringsConf.CONF_LEVEL_OUT_OF_SEQUENCE ), 
												_level,
												level )   ) );

		MoveTreeNode result = this;

		while( ( result != null ) && ( result._level != level ) )
		{
			result = result.getParent();
		}

		return( result );
	}

	public LinkedList<ChessGameMove> getGameMoveList()
	{
		LinkedList<ChessGameMove> result = new LinkedList<ChessGameMove>();

		MoveTreeNode node = this;
		while( (node != null) )
		{
			if( node.getMove() != null )
			{
				result.addFirst( node.getMove() );
			}
			node = node.getParent();
		}

		return( result );
	}

	public MoveTreeNode findChild( ChessGameMove cgm )
	{
		MoveTreeNode result = null;

		Iterator<MoveTreeNode> it = _children.iterator();

		while( ( result == null ) && it.hasNext() )
		{
			MoveTreeNode gt = it.next();
			if( gt.getMove().equals( cgm ) )
			{
				result = gt;
			}
		}

		return( result );
	}

	protected MoveTreeNode simpleInsertGen( ChessGameMove cgm, int level, Consumer<MoveTreeNode> inserter )
	{
//		MoveTreeNode result = findChild( cgm );
		MoveTreeNode result = null;	// multilple lines with the same move can be possible.

		if( result == null )
		{
			result = new MoveTreeNode( this, cgm, level );
			inserter.accept(result);
		}

		return( result );
	}

	public MoveTreeNode simpleInsert( MoveTreeNode nodeToInsert )
	{
		return( simpleInsertGen( nodeToInsert, (mtn) -> _children.add( mtn ) ) );
	}

	protected MoveToken getMoveToken( ChessGameMove cgm )
	{
		MoveToken result = null;
		if( cgm != null )
			result = cgm.getMoveToken();

		return( result );
	}

	protected int getLevelForChild( ChessGameMove cgm )
	{
		MoveToken mt = getMoveToken( cgm );
		int numberOfPly = 0;

		if( mt != null )
			numberOfPly = mt.getNumberOfPly();

		if( numberOfPly <= 0 )
			numberOfPly = getLevel() + 1;

		return( numberOfPly );
	}

	public MoveTreeNode simpleInsert( ChessGameMove cgm )
	{
		MoveTreeNode result = null;
		if( cgm != null )
		{
/*
			MoveToken mt = cgm.getMoveToken();
			int numberOfPly = 0;
			
			if( mt != null )
				numberOfPly = mt.getNumberOfPly();
			else
				numberOfPly = getLevel() + 1;
*/
			int numberOfPly = getLevelForChild( cgm );
			result = simpleInsert( cgm, numberOfPly );
		}

		return( result );
	}

	public MoveTreeNode simpleInsert( ChessGameMove cgm, int level )
	{
		return( simpleInsertGen( cgm, level, (mtn) -> _children.add( mtn ) ) );		
	}

	public MoveTreeNode insertFirst( MoveTreeNode nodeToInsert )
	{
		return( simpleInsertGen( nodeToInsert, (mtn) -> _children.addFirst( mtn ) ) );
	}

	public MoveTreeNode insertFirst( ChessGameMove cgm, int level )
	{
		return( simpleInsertGen( cgm, level, (mtn) -> _children.addFirst( mtn ) ) );		
	}

	public MoveTreeNode insertFirst( ChessGameMove cgm )
	{
		return( simpleInsertGen( cgm, getLevelForChild( cgm ), (mtn) -> _children.addFirst( mtn ) ) );		
	}

	protected int getLevelForInsertion( MoveTreeNode nodeToInsert )
	{
		return( getLevel() + 1 );
	}

	protected MoveTreeNode simpleInsertGen( MoveTreeNode nodeToInsert, Consumer<MoveTreeNode> inserter )
	{
		MoveTreeNode result = null;

		if( nodeToInsert != null )
		{
			result = simpleInsertGen( nodeToInsert.getMove(), getLevelForInsertion( nodeToInsert ), inserter );
//			result = simpleInsertGen( nodeToInsert.getMove(), nodeToInsert.getLevel(), inserter );
			result.setComment( nodeToInsert.getComment() );
			result.setCommentForVariant( nodeToInsert.getCommentForVariant() );
		}

		return( result );
	}

	public ChessGameMove getMove()
	{
		return( _move );
	}

	public MoveTreeNode getParent()
	{
		return( _parent );
	}

	public void setComment( String comment )
	{
		_comment = comment;
	}

	public String getComment()
	{
		return( _comment );
	}

	public void setCommentForVariant( String value )
	{
		_commentForVariant = value;
	}

	public String getCommentForVariant()
	{
		return( _commentForVariant );
	}

	public int getLevel()
	{
		return( _level );
	}

	public int getNumberOfTotalElements()
	{
		int result = 0;
		
		if( _move != null ) result++;
		
		Iterator<MoveTreeNode> it = _children.iterator();
		
		while( it.hasNext() )
		{
			MoveTreeNode mt = it.next();
			result = result + mt.getNumberOfTotalElements();
		}
		
		return( result );
	}

	public int getNumberOfChildren()
	{
		return( _children.size() );
	}
	
	public int getTotalNumberOfMoves()
	{
		int result = 1;

		for( MoveTreeNode child: getChildrenList() )
			result += child.getNumberOfTotalElements();

		return( result );
	}

	public MoveTreeNode getChild( int index )
	{
		MoveTreeNode result = null;
		
		if( (index >= 0) && (index < _children.size() ) )
		{
			result = _children.get(index);
		}
		return( result );
	}

	public Iterator<MoveTreeNode> getChildrenIterator()
	{
		return( _children.iterator() );
	}

	public MoveTreeNode getEndOfMainLine()
	{
		MoveTreeNode result = this;

		while( result.getNumberOfChildren() > 0 )
		{
			result = result.getChild(0);
		}

		return( result );
	}

	protected MoveTreeNode getNewVariationMoveNode( int increment )
	{
		MoveTreeNode result = null;
		MoveTreeNode current = this;

		MoveTreeNode parent = current.getParent();
		if( ( parent != null ) && ( parent.getNumberOfChildren() > 1 ) )
		{
			int index = parent._children.indexOf( current ) + increment;		// indexOf should always return an index different from -1
			if( ( index >= 0 ) && ( index < parent.getNumberOfChildren()) )
			{
				result = parent.getChild( index );
			}
			else if( increment < 0 )			// decrement variation (up arrow)
				result = this;					// do not change
			else if( increment > 0 )			// increment variation (down arrow)
				result = this;					// do not change
		}
		else if( ( increment < 0 ) && ( parent == null ) )
			result = current;					// do not change

		if( ( result == null ) && ( increment != 0 ) )
		{
			while( result == null )
			{
				if( increment > 0 )		// (down arrow). in this case, we must move to the next tree move node which has more than one child.
				{
					if( current.getNumberOfChildren() == 0 )
						result = current;
					else if( current.getNumberOfChildren() > 1 )
						result = current.getChild(0);
					else
						current = current.getChild(0);
				}
				else		// (up arrow). in this case, we must move to the previous tree move whose parent has more than one child.
				{
					parent = current.getParent();
					if( parent == null )
						result = current;
					else if( parent.getNumberOfChildren() > 1 )
						result = current;
					else
						current = parent;
				}
			}
		}

		return( result );
	}

	public MoveTreeNode getNextVariationMoveNode()
	{
		return( getNewVariationMoveNode( 1 ) );
	}

	public MoveTreeNode getPreviousVariationMoveNode()
	{
		return( getNewVariationMoveNode( -1 ) );
	}
	
	public MoveTreeNode getRootNode()
	{
		MoveTreeNode result = this;
		
		while( result.getParent() != null )
		{
			result = result.getParent();
		}
		
		return( result );
	}
	
	public MoveTreeGame getMoveTreeGame()
	{
		MoveTreeGame result = null;
		MoveTreeNode mtn = getRootNode();
		if( mtn instanceof MoveTreeGame )
			result = (MoveTreeGame) mtn;
		
		return( result );
	}
	
	public int getFirstChildLevel()
	{
		int result = -1;
		
		if( getNumberOfChildren() > 0 )
		{
			result = getChild(0).getLevel();
		}

		return( result );
	}
	
	public boolean isMainLine()
	{
		boolean result = true;
		
		MoveTreeNode node = this;
		while( result && (node.getParent()!=null) )
		{
			result = ( node.getParent().getChild(0) == node );
			node = node.getParent();
		}

		return( result );
	}

	public void removeChild( MoveTreeNode mtn )
	{
		_children.remove( mtn );
	}
	
	public void remove()
	{
		if( _parent != null )
		{
			_parent.removeChild( this );
			_parent = null;
		}
		else
		{
			_children.clear();
		}
/*
		for( MoveTreeNode node: _children )
			node.remove();

		_children.clear();
*/
	}

	public boolean isParent( MoveTreeNode mtn )
	{
		MoveTreeNode current = getParent();
		boolean result = false;
		while( !result && ( current != null ) && ( mtn != null ) )
		{
			result = ( current == mtn );
			current = current.getParent();
		}

		return( result );
	}

	public int getMoveNumber()
	{
		int result = ( _level + 1 ) / 2;
		return( result );
	}

	protected static ChessStringsConf getChessStrConf()
	{
		return( ChessStringsConf.instance() );
	}

	protected void setLevel( int level )
	{
		_level = level;
	}

	protected ChessGamePosition getPosition( ChessGamePosition initialPosition, List<ChessGameMove> listOfMoves )
	{
		ChessGamePosition result = null;

		ChessBoard cb = new ChessBoard();
			
		try
		{
			cb.setPosition( initialPosition );

			cb.doMoves( listOfMoves);

			result = cb.getCurrentPosition();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		return( result );
	}
	
	public ChessGamePosition getPreviousPosition()
	{
		ChessGamePosition result = null;

		if( getParent() != null )
		{
			MoveTreeGame mtg = getMoveTreeGame();
			if( mtg != null )
			{
				result = getPosition( mtg.getInitialPosition(), getParent().getGameMoveList() );
			}
		}

		return( result );
	}

	public List<MoveTreeNode> getChildrenList()
	{
		return( _children );
	}

	protected <CC, RR> RR getIfNotNull( CC obj, Function<CC,RR> getter )
	{
		return( NullFunctions.instance().getIfNotNull(obj, getter) );
	}

	public boolean wasWhiteToMove()
	{
		Boolean result = getIfNotNull(
							getIfNotNull( getMove(), ChessGameMove::getMoveToken ),
							MoveToken::isWhiteToMove );
		if( result == null )
			result = ( getLevel() % 2 == 1 );

		return( result );
	}

	public boolean willBeWhiteToMove()
	{
		return( ! wasWhiteToMove() );
	}

	protected void updateLevel( int level )
	{
		setLevel( level );
		for( MoveTreeNode child: getChildrenList() )
			child.updateLevel();
	}

	protected void updateLevel()
	{
		if( ( _parent != null ) && ( ( _parent.getLevel() + 1 ) != getLevel() ) )
			updateLevel( _parent.getLevel() + 1 );
	}
}
