/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.analysers;

import com.frojasg1.general.string.StringFunctions;
import java.util.Objects;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class GenListSplitterBase
{
	protected String _separator;

	protected String _string;

//	protected int _pos = -1;
	protected int _pos = 0;

	protected String _next;

	public GenListSplitterBase( String string, String separator )
	{
		_string = string;
		_separator = separator;
	}

	public String next()
	{
		_next = null;

		return( getToken() );
	}

	public String getToken()
	{
		if( _next == null )
			_next = calculateNext();

		return( _next );
	}

	public boolean hasEnded()
	{
		return( _pos >= getTotalLength() );
	}

	protected int calculateEnd()
	{
		return( currentIsSeparator() ? _pos - 1 : _pos );
	}

	protected boolean currentIsSeparator()
	{
		boolean result = false;
		String ch = getStringAtPos( _pos - 1 );
		if( ch != null )
			result = isSeparator( ch );

		return( result );
	}

	protected Character getCurrentChar()
	{
		Character result = null;
		if( ( _pos > -1 ) && ! hasEnded() )
			result = charAt( _pos - 1 );

		return( result );
	}

	protected char charAt( int pos )
	{
		return( _string.charAt( pos ) );
	}

	protected String calculateNext()
	{
		String result = null;

		if( !hasEnded() )
		{
//			if( ! isFirst() )
//				skipSeparator();

//			int start = _pos + 1;
			int start = _pos;

			passWholeAttribute();

			int end = Math.max( start, calculateEnd() );

			result = _string.substring( start, end ).trim();
		}

		return( result );
	}

	protected abstract void passWholeAttribute();

	protected boolean isFirst()
	{
		return( _pos == 0 );
	}

	protected void skipSeparator()
	{
		String ch = null;
	loop: while( ! hasEnded() )
		{
			ch = getStringAtPos();
			_pos++;
			switch( ch )
			{
				case " " :
				case "\t" :
				break;

				default:
					if( isSeparator(ch) )
						break loop;

					throwSeparatorNotFound( ch );
			}
		}

		if( ! hasEnded() && ! isSeparator( ch ) )
			throwSeparatorNotFound( ch );
	}

	protected String getStringAtPos()
	{
		return( getStringAtPos(_pos) );
	}

	protected String getStringAtPos( int pos )
	{
		String result = null;
		if( ( pos > -1 ) && ( pos < _string.length() ) )
			result = _string.substring(pos, pos + 1);

		return( result );
	}

	protected boolean isSeparator( String str )
	{
		return( Objects.equals( _separator, str) );
	}

	protected void throwSeparatorNotFound( String ch )
	{
		throw( new RuntimeException(
						String.format( "Separator not found at position: %d, found: '%s'",
							_pos, ch )
									)
			);
	}

	protected int getTotalLength()
	{
		return( _string.length() );
	}
/*
	protected String incPosAndGetMatchingChar( String ch )
	{
		_pos++;

		return( getMatchingChar(ch) );
	}
*/
	protected String getMatchingChar( String ch )
	{
		if( ! hasEnded() )
			_pos = _string.indexOf(ch, _pos);
		
		arrangePos();

		return( getStringAtPos() );
	}

	protected void arrangePos()
	{
		if( ( _pos == -1 ) || ( _pos > getTotalLength() ) )
			_pos = getTotalLength();
	}

	protected String getNextNotableChar( String notableChars )
	{
		String ch = null;
		_pos = StringFunctions.instance().indexOfAnyChar(_string, notableChars, _pos);
		if( _pos == -1 )
			_pos = getTotalLength();

		return( getStringAtPos() );
	}
}
