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
package com.frojasg1.general.string;

import com.frojasg1.general.number.IntegerFunctions;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class CreateCustomString
{
	protected static CreateCustomString _instance = null;

	public static void changeInstance( CreateCustomString instance )
	{
		_instance = instance;
	}

	public static CreateCustomString instance()
	{
		if( _instance == null )
			_instance = new CreateCustomString();

		return( _instance );
	}

	protected Splitter createSplitter( String stringToFormat )
	{
		return( new Splitter( stringToFormat ) );
	}

	protected String getParameterString( Integer index, Object ... args )
	{
		String result = "PARAMETER_NOT_PRESENT";

		if( index == null )
			result = null;
		else if( index <= args.length )
		{
			Object obj = args[ index -1 ];

			if( obj == null )
				result = "null";
			else
				result = obj.toString();
		}

		return( result );
	}

	public String createCustomString( String stringToFormat, Object ... args )
	{
		String result = null;
		
		if( stringToFormat != null )
		{
			StringBuilder sb = new StringBuilder();
			Splitter sp = createSplitter( stringToFormat );

			SplitterResult sr = null;
			while( ( sr = sp.next() ) != null )
			{
				sb.append( sr.getPreviousString() );

				String paramStr = getParameterString( sr.getParmameterIndex(), args );
				if( paramStr != null )
					sb.append( paramStr );
			}

			result = sb.toString();
		}

		return( result );
	}

	protected static class SplitterResult
	{
		protected String _previousString = null;
		protected Integer _parameterIndex = null;

		public SplitterResult( String previousString, Integer parameterIndex )
		{
			_previousString = previousString;
			_parameterIndex = parameterIndex;
		}

		public String getPreviousString()
		{
			return( _previousString );
		}

		public Integer getParmameterIndex()
		{
			return( _parameterIndex );
		}
	}

	protected static class Splitter
	{
		protected String _string = null;
		protected int _index = 0;
		protected Integer _parameterIndex = null;

		public Splitter( String str )
		{
			_string = str;
		}

		public Character getNextCharacterInternal()
		{
			Character result = null;

			if( _index < _string.length() )
			{
				result = _string.charAt( _index );
				_index++;
			}

			return( result );
		}

		protected boolean isDigit( Character ch )
		{
			return( ( ch != null ) && ( ch >= '0' ) && ( ch <= '9' ) );
		}

		protected Integer getParameterIndex()
		{
			Integer result = null;

			String indexStr = "";
			Character ch = getNextCharacterInternal();
			while( isDigit( ch ) )
			{
				indexStr = indexStr + ch;
				ch = getNextCharacterInternal();
			}

			if( indexStr.length() > 0 )
			{
				result = IntegerFunctions.parseInt( indexStr );
				if( result == null )
					throw( new RuntimeException( "Could not parse parameterIndex: " + indexStr ) );
			}

			if( ch != null )
				_index--;

			return( result );
		}

		public Character getNextCharacter()
		{
			Character result = getNextCharacterInternal();

			if( result != null )
			{
				if( result == '\\' )
					result = getNextCharacterInternal();
				else if( result == '$' )
					_parameterIndex = getParameterIndex();
			}

			return( result );
		}

		protected SplitterResult createSplitterResult( String previousString,
														Integer parameterIndex )
		{
			return( new SplitterResult( previousString, parameterIndex ) );
		}

		public SplitterResult next()
		{
			StringBuilder previousString_sb = new StringBuilder();

			_parameterIndex = null;

			Character ch = getNextCharacter();
			while( ( ch != null ) && ( _parameterIndex == null ) )
			{
				if( ch != null )
					previousString_sb.append( ch );

				ch = getNextCharacter();
			}

			SplitterResult result = null;

			if( ( previousString_sb.length() > 0 ) || ( _parameterIndex != null ) )
				result = createSplitterResult( previousString_sb.toString(), _parameterIndex );

			return( result );
		}
	}
}
