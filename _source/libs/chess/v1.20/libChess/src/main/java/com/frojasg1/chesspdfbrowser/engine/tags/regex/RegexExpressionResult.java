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
package com.frojasg1.chesspdfbrowser.engine.tags.regex;

import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexToken;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RegexExpressionResult
{
	protected String _particularTagRegexExpression = null;
	protected List<String> _replacementList = null;
	protected List<RegexToken> _listSimpleBlockRegexTokens = null;
	protected Pattern _pattern = null;

	protected int _numberOfOptionalBlocks = 0;

	public void setParticularTagRegexExpression( String value )
	{
		_particularTagRegexExpression = value;
	}

	public String getParticularTagRegexExpression()
	{
		return( _particularTagRegexExpression );
	}

	public void setReplacementList( List<String> list )
	{
		_replacementList = list;
	}

	public List<String> getReplacementList()
	{
		return( _replacementList );
	}

	public List<RegexToken> getListSimpleBlockRegexTokens()
	{
		return( _listSimpleBlockRegexTokens );
	}

	public void setListSimpleBlockRegexTokens( List<RegexToken> list )
	{
		_listSimpleBlockRegexTokens = list;
	}

	public Pattern getPattern()
	{
		return( _pattern );
	}

	public void setPattern( Pattern pattern )
	{
		_pattern = pattern;
	}

	public void setNumberOfOptionalBlocks( int value )
	{
		_numberOfOptionalBlocks = value;
	}

	public int getNumberOfOptionalBlocks()
	{
		return( _numberOfOptionalBlocks );
	}
}
