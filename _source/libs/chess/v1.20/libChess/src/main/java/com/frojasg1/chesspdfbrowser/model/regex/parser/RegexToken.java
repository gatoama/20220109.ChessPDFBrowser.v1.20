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
package com.frojasg1.chesspdfbrowser.model.regex.parser;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RegexToken {
	
	protected int _startPosition = -1;
	protected String _string = null;
	protected String _transformedString = null;
	protected RegexTokenId _tokenId = null;
	protected boolean _isOptional = false;

	public RegexToken( int startPosition, String string, String transformedString, RegexTokenId tokenId,
		boolean isOptional )
	{
		_startPosition = startPosition;
		_string = string;
		_transformedString = transformedString;
		_tokenId = tokenId;
		_isOptional = isOptional;
	}

	public int getStartPosition()
	{
		return( _startPosition );
	}

	public String getString()
	{
		return( _string );
	}

	public String getTransformedString()
	{
		return( _transformedString );
	}

	public RegexTokenId getTokenId()
	{
		return( _tokenId );
	}

	public boolean isOptional()
	{
		return( _isOptional );
	}
}
