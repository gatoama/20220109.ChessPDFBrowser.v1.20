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
package com.frojasg1.general.document.formatted.text.imp;

import com.frojasg1.general.document.formatted.text.FormattedText;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class FormattedTextList extends FormattedText
{
	protected Type _type = null;
	protected final Type[] _automaticType = { Type.ASTERISK, Type.MINUS, Type.GREATER_THAN };

	public FormattedTextList( Type type )
	{
		_type = type;
	}

	public static enum Type
	{
		ASTERISK,
		MINUS,
		GREATER_THAN,
		NUMBERED,
		ALPHABETIC,
		GENERIC_LIST
	}

	public Type getType()
	{
		return( _type );
	}

	public String getStringPrefixOfElement( int index )
	{
		return( getStringPrefixOfElement( _type, index ) );
	}

	protected String getStringPrefixOfElement( Type type, int index )
	{
		String result = null;

		switch( type )
		{
			case NUMBERED:
				result = String.format( "%d-", index );
			break;

			case ALPHABETIC:
			{
				char letter = (char) ( 'a' + index );
				if( letter > 'z' )
					throw( new RuntimeException( "Double letter not implemented." ) );

				result = String.format( "%c)", letter );
			}
			break;

			case ASTERISK:
				result = "*";
			break;

			case MINUS:
				result = "-";
			break;

			case GREATER_THAN:
				result = ">";
			break;

			case GENERIC_LIST:
				result = calculateAutomaticPrefix();
			break;

			default:
				result = null;
			break;
		}

		return( result );
	}

	protected int getNestingIndex()
	{
		int result = 0;

		FormattedText current = this;
		while( ( current = current.getParent() ) != null )
		{
			if( current instanceof FormattedTextList )
			{
				FormattedTextList ftl = (FormattedTextList) current;

				if( ftl.getType().equals( Type.GENERIC_LIST ) )
					result++;
			}
		}

		return( result );
	}

	protected Type calculateAutomaticType( int index )
	{
		index = index % _automaticType.length;

		Type result = _automaticType[index];

		return( result );
	}

	protected String calculateAutomaticPrefix()
	{
		int index = getNestingIndex();

		Type type = calculateAutomaticType( index );

		return( getStringPrefixOfElement( type, 0 ) );
	}
}
