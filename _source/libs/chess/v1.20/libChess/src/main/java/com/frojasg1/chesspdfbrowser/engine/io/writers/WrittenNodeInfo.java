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
package com.frojasg1.chesspdfbrowser.engine.io.writers;

import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;

/**
 *
 * @author Usuario
 */
public class WrittenNodeInfo 
{
	protected MoveTreeNode _moveTree;
	
	protected int _initialPositionOfMove = -1;
	protected int _finalPositionOfMove = -1;
	protected boolean _isThereComment = false;
	protected int _initialPositionOfComment = -1;
	protected int _finalPositionOfComment = -1;
	protected boolean _isThereCommentForVariant = false;
	protected int _initialPositionOfCommentForVariant = -1;
	protected int _finalPositionOfCommentForVariant = -1;
	protected boolean _isMainLine = false;

	protected boolean _isIllegalMove = false;
	protected boolean _isAnyParentIllegalMove = false;

	public WrittenNodeInfo( MoveTreeNode moveTree, int initialPositionOfMove, int finalPositionOfMove, boolean isMainLine )
	{
		_moveTree = moveTree;
		if( _moveTree != null )
			_moveTree.setAdditionalInfo(this);

		_initialPositionOfMove = initialPositionOfMove;
		_finalPositionOfMove = finalPositionOfMove;
		_isMainLine = isMainLine;
		
		if( _moveTree != null )
		{
			MoveTreeNode parent = _moveTree.getParent();
			if( parent != null )
			{
				WrittenNodeInfo parentWni = parent.getAdditionalInfo();
				if( parentWni != null )
					_isAnyParentIllegalMove = parentWni.getIsIllegalMove() || parentWni.getIsAnyParentIllegalMove();
			}
		}
	}

	public void setLocationOfComment( int initialPositionOfComment, int finalPositionOfComment )
	{
		_isThereComment = true;
		_initialPositionOfComment = initialPositionOfComment;
		_finalPositionOfComment = finalPositionOfComment;
	}

	public void setLocationOfCommentForVariant( int initialPosition, int finalPosition )
	{
		_isThereCommentForVariant = true;
		_initialPositionOfCommentForVariant = initialPosition;
		_finalPositionOfCommentForVariant = finalPosition;
	}

	public int getMinimumPosition()
	{
		int result = getInitialPositionOfCommentForVariant();
		if( result == -1 )
			result = getInitialPositionOfMove();

		return( result );
	}

	public int getInitialPositionOfMove()		{	return( _initialPositionOfMove );	}
	public int getFinalPositionOfMove()			{	return( _finalPositionOfMove );	}
	public boolean isThereComment()				{	return( _isThereComment );	}
	public int getInitialPositionOfComment()	{	return( _initialPositionOfComment );	}
	public int getFinalPositionOfComment()		{	return( _finalPositionOfComment );	}
	public boolean isThereCommentForVariant()				{	return( _isThereCommentForVariant );	}
	public int getInitialPositionOfCommentForVariant()	{	return( _initialPositionOfCommentForVariant );	}
	public int getFinalPositionOfCommentForVariant()		{	return( _finalPositionOfCommentForVariant );	}

	public boolean isMainLine()					{	return( _isMainLine );	}

	public MoveTreeNode getMoveTreeNode()		{	return( _moveTree );	}
	public boolean getIsIllegalMove()			{	return( _isIllegalMove );	}
	public boolean getIsAnyParentIllegalMove()	{	return( _isAnyParentIllegalMove );	}

	public void setMoveTreeNode( MoveTreeNode value )
	{
		_moveTree = value;
	}

	public void setIsIllegalMove( boolean value )
	{
		_isIllegalMove = value;
	}

	protected boolean isInside( int start, int end, int positionToCheck )
	{
		return( (positionToCheck>=start) && (positionToCheck<end) );
	}

	public boolean isInsideMove( int charPosition )
	{
		return( isInside( _initialPositionOfMove, _finalPositionOfMove, charPosition ) );
	}

	public boolean isInsideComment( int charPosition )
	{
		return( _isThereComment && isInside( _initialPositionOfComment, _finalPositionOfComment, charPosition ) );
	}

	public boolean isInsideCommentForVariant( int charPosition )
	{
		return( _isThereCommentForVariant && isInside( _initialPositionOfCommentForVariant,
											_finalPositionOfCommentForVariant, charPosition ) );
	}
}
