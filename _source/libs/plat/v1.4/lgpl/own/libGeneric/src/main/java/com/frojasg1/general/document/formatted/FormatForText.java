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
package com.frojasg1.general.document.formatted;

import com.frojasg1.general.number.IntegerFunctions;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class FormatForText implements Comparable<FormatForText>
{
	protected String _text;
	protected String _styleName;
	protected int _start;
	protected int _length = -1;

	public FormatForText( String text, int start, String styleName )
	{
		_text = text;
		_start = start;
		_styleName = styleName;

		if( _text != null )
			_length = _text.length();
	}
/*
	public FormatForText( int start, int length, String styleName )
	{
		_text = null;
		_start = start;
		_styleName = styleName;

		_length = length;
	}
*/
	public String getText()
	{
		return( _text );
	}

	public String getStyleName()
	{
		return( _styleName );
	}

	public int getStart()
	{
		return( _start );
	}

	public int getLength()
	{
		return( _length );
	}

	public void addOffset( int offset )
	{
		_start += offset;
	}

	@Override
	public int compareTo(FormatForText other)
	{
		int result = 1;
		if( other != null )
		{
			int diff = _start - other._start;
			result = IntegerFunctions.sgn( diff );
		}

		return( result );
	}
}
