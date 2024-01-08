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
package com.frojasg1.chesspdfbrowser.engine.io.notation.comment;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class CommentString
{
	protected static final String NOVELTY_N = "Novelty[N].";
	protected static final String NOVELTY_TN = "Novelty[TN].";

	protected String _comment = null;

	public CommentString( String comment )
	{
		_comment = comment;
	}

	public String getSimpleComment()
	{
		return( getSimpleComment( _comment ) );
	}

	public String getComposedComment()
	{
		return( _comment );
	}

	public boolean isNoveltyN()
	{
		boolean result = false;
		if( _comment != null )
			result = _comment.startsWith(NOVELTY_N);

		return( result );
	}

	public boolean isNoveltyTN()
	{
		boolean result = false;
		if( _comment != null )
			result = _comment.startsWith(NOVELTY_TN);

		return( result );
	}

	protected String getSimpleComment( String composedComment )
	{
		return( getSimpleComment( _comment, NOVELTY_N, NOVELTY_TN ) );
	}

	protected String getSimpleComment( String composedComment, String ... prefixesToRemove )
	{
		String result = composedComment;

		boolean found = true;
		while( ( result != null ) && found )
		{
			found = false;
			for( int ii=0; !found && ( ii< prefixesToRemove.length ); ii++ )
			{
				found = result.startsWith( prefixesToRemove[ii] );
				if( found )
					result = result.substring( prefixesToRemove[ii].length() );
			}
		}

		return( result );
	}

	protected void setIsNovelty( String prefixToBeSet )
	{
		_comment = getSimpleComment( _comment );
		_comment = prefixToBeSet + ( _comment != null ? _comment : "" );
	}

	public void setIsNoveltyN()
	{
		setIsNovelty( NOVELTY_N );
	}

	public void setIsNoveltyTN()
	{
		setIsNovelty( NOVELTY_TN );
	}
}
