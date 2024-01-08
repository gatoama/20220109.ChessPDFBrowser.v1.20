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
package com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens;

import com.frojasg1.chesspdfbrowser.engine.configuration.ChessStringsConf;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessParserException;
import com.frojasg1.chesspdfbrowser.engine.io.notation.comment.CommentString;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.NAG;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class MoveToken extends Token
{
	protected int _moveTurnNumber;
	protected boolean _isWhiteToMove;
	protected String _comment = null;
	protected List<NAG> _nagList = null;

	protected TypeOfNovelty _typeOfNovelty = TypeOfNovelty.NOTHING;

	protected boolean _isPossiblyMainLine = false;

	protected int _pageNumber = -1;

	// the moveString must be in English.

	public MoveToken()
	{
		super( TokenId.MOVE );

		_nagList = new ArrayList<NAG>();
	}

	public MoveToken( PreToken preToken ) throws ChessParserException
	{
		super( preToken );

		if( getTokenId() != TokenId.MOVE )
			throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_NOT_A_MOVE_PRETOKEN ) ) );

		updateNovelty();

		_nagList = new ArrayList<NAG>();
	}

	protected void updateNovelty()
	{
		String moveString = getString();
		if( moveString.endsWith( "TN" ) )
		{
			_typeOfNovelty = TypeOfNovelty.TN_NOVELTY;
			moveString = moveString.substring( 0, moveString.length() - 2 );
		}
		else if( moveString.endsWith( "N" ) )
		{
			_typeOfNovelty = TypeOfNovelty.N_NOVELTY;
			moveString = moveString.substring( 0, moveString.length() - 1 );
		}

		setString( moveString );
	}

	public void setPageNumber( int pageNumber )		{	_pageNumber = pageNumber; }
	public void setMoveNumber( int moveNumber )		{	_moveTurnNumber = moveNumber;	}
	public void setIsWhiteToMove( boolean value )	{	_isWhiteToMove = value;	}
	public void setComment( String comment )		{	_comment = comment;	}
	public void setIsPossiblyMainLine( boolean value ) {	_isPossiblyMainLine = value;	}

	public int getPageNumber()						{	return( _pageNumber ); }
	public int getMoveNumber()						{	return( _moveTurnNumber );	}
	public boolean isWhiteToMove()					{	return( _isWhiteToMove );	}
	public String getComment()
	{
		String result = null;

		if( ( _comment != null ) || !_typeOfNovelty.equals( TypeOfNovelty.NOTHING ) )
		{
			CommentString cs = new CommentString( ( _comment != null ? _comment : "" ) );
			switch( _typeOfNovelty )
			{
				case TN_NOVELTY:	cs.setIsNoveltyTN(); break;
				case N_NOVELTY:		cs.setIsNoveltyN(); break;
			}

			result = cs.getComposedComment();
		}

		return( result );
	}
	public boolean getIsPossiblyMainLine()			{ return( _isPossiblyMainLine ); }


	public int getNumberOfPly()
	{
		int result = 2 * ( _moveTurnNumber - 1 ) + 1;
		if( !_isWhiteToMove )	result++;

		return( result );
	}

	public void clearNAGlist()
	{
		_nagList.clear();
	}

	public void addNag( NAG nag )
	{
		_nagList.add(nag);
	}

	public void addAllNAGs( Iterator<NAG> it )
	{
		while( it.hasNext() )
			_nagList.add(it.next());
	}

	public Iterator<NAG> nagIterator()
	{
		return( _nagList.iterator() );
	}

	public String toString()
	{
		String result = String.format( "%d. %s, isWhiteToMove: %s",
										getMoveNumber(), getString(),
										String.valueOf( isWhiteToMove() ) );

		return( result );
	}

	protected static ChessStringsConf getChessStrConf()
	{
		return( ChessStringsConf.instance() );
	}

	protected static enum TypeOfNovelty
	{
		NOTHING,
		N_NOVELTY,
		TN_NOVELTY
	}
}
