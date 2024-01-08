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
package com.frojasg1.chesspdfbrowser.model.regex;

import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexLexicalAnalyser;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexToken;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RegexOfBlockModel
{
	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	protected String _name = null;
	protected String _expression = null;

	// container of blocks
	protected BlockRegexConfigurationContainer _container = null;

	protected List<RegexToken> _cachedListOfTokens = null;

	protected RegexLexicalAnalyser _lex = null;

	// function for DefaultConstructorInitCopier
	public RegexOfBlockModel()
	{
	}

	public RegexOfBlockModel( String name,
								BlockRegexConfigurationContainer container )
	{
		_name = name;
		_container = container;
	}

	// function for DefaultConstructorInitCopier
	public void init( RegexOfBlockModel that )
	{
		_name = that._name;
		_expression = that._expression;

		// this is programed for not to change.
		_container = that._container;

		_cachedListOfTokens = null;

		_lex = that._lex;
	}

	protected void setName( String name )
	{
		_name = name;
	}

	protected void copy( RegexOfBlockModel other )
	{
		_expression = other._expression;
	}

	public void setBlockRegexConfigurationContainer( BlockRegexConfigurationContainer container )
	{
		_container = container;
	}

	public List<RegexToken> getCachedListOfTokens()
	{
		return( _cachedListOfTokens );
	}

	public void setCachedListOfTokens( List<RegexToken> list )
	{
		_cachedListOfTokens = list;
	}

	public String getName()
	{
		return( _name );
	}

	public String getExpression()
	{
		return( _expression );
	}

	public void setExpression( String expression )
	{
		_expression = expression;

		_container.invalidateCachesParent();
	}

	public List<RegexToken> getListOfTokens()
	{
		return( _container.getListOfTokens( _name ) );
	}

	protected RegexLexicalAnalyser createLexicalAnalyser()
	{
		return( new RegexLexicalAnalyser() );
	}

	protected RegexLexicalAnalyser getLexicalAnalyser()
	{
		if( _lex == null )
			_lex = createLexicalAnalyser();

		return( _lex );
	}

	public void invalidateCache()
	{
		_cachedListOfTokens = null;
	}
}
