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

import com.frojasg1.chesspdfbrowser.engine.tags.regex.BlockRegexBuilder;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexToken;
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class BlockRegexConfigurationContainer
{
	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	protected Map<String, RegexOfBlockModel> _map = null;

	protected RegexWholeFileModel _parent = null;

	protected List<String> _initialItems = null;

	protected TextComboBoxHistoryCompletionServer _cbContent = null;

	public void init( RegexWholeFileModel parent )
	{
		_cbContent = null;

		_parent = parent;

		_map = new HashMap<>();

		_cbContent = getComboBoxContent();
	}

	// function for DefaultConstructorInitCopier
	public void init( BlockRegexConfigurationContainer other )
	{
		_map = _copier.copyMap( other._map );

		_parent = other.getParent();

		// do not change, as block regex configuration must be accessible at the same memory address
		// even after the invocation to this function
		// for its memory address not to change
		if( _cbContent == null )
			_cbContent = createComboEmptyBoxHistory();
		_copier.copy( _cbContent, other._cbContent );
	}

	protected Map<String, RegexOfBlockModel> getRegexModelMap()
	{
		return( _map );
	}

	public RegexWholeFileModel getParent()
	{
		return( _parent );
	}

	protected BlockRegexBuilder getRegexBuilder()
	{
		return( _parent.getBlockRegexBuilder() );
	}

	protected void copy( BlockRegexConfigurationContainer other )
	{
		// deliverately left empty
	}

	public TextComboBoxHistoryCompletionServer createComboEmptyBoxHistory()
	{
		TextComboBoxHistoryCompletionServer result = new TextComboBoxHistoryCompletionServer();
		result.init( (List<String>) null);

		return( result );
	}

	public TextComboBoxHistoryCompletionServer getComboBoxContent()
	{
		if( _cbContent == null )
			_cbContent = createComboEmptyBoxHistory();

		return( _cbContent );
	}

	protected RegexOfBlockModel createRegexOfBlockModel( String blockName, RegexOfBlockModel other )
	{
		RegexOfBlockModel result = new RegexOfBlockModel( blockName, this );

		if( other != null )
			result.init( other );
//		else
//			result.init( regexConf );

		return( result );
	}

	public RegexOfBlockModel addBlockRegex( String blockOrTagName, RegexOfBlockModel other )
	{
		RegexOfBlockModel result = createRegexOfBlockModel( blockOrTagName, other );
		if( result != null )
		{
			_map.put( blockOrTagName, result );
			getComboBoxContent().addItem(blockOrTagName);
		}

		return( result );
	}

	public void setExpression( String regexConfigurationName, String expression )
	{
		_map.get(regexConfigurationName).setExpression(expression);

		invalidateCaches();
	}

	protected boolean hasToInvalidateCaches()
	{
		return( true );
	}

	public void invalidateCachesParent()
	{
		if( ( _parent != null ) && hasToInvalidateCaches() )
			_parent.invalidateCaches();
	}

	public void invalidateCaches()
	{
		for( RegexOfBlockModel conf: _map.values() )
			conf.invalidateCache();
	}

	public RegexOfBlockModel get( String regexConfigurationName )
	{
		return( _map.get( regexConfigurationName ) );
	}

	public void put( String regexConfigurationName, RegexOfBlockModel value )
	{
		_map.put( regexConfigurationName, value );
	}

	public boolean remove( String regexConfigurationName )
	{
		RegexOfBlockModel value = _map.remove( regexConfigurationName );
		_cbContent.removeItem(regexConfigurationName);

		return( value != null );
	}

	public List<RegexToken> getListOfTokens( String regexConfigurationName )
	{
		List<RegexToken> result = null;

		RegexOfBlockModel robc = get( regexConfigurationName );
		if( robc != null )
		{
			result = robc.getCachedListOfTokens();

			if( result == null )
			{
				result = getRegexBuilder().getListOfTokens( robc.getExpression() );
				robc.setCachedListOfTokens(result);
			}
		}

		return( result );
	}

	public boolean contains( String regexName )
	{
		return( _cbContent.contains( regexName ) );
	}

	public void updateBlockConfCont( BlockRegexConfigurationContainer blockConfCont )
	{
		for( RegexOfBlockModel regexModel: _map.values() )
		{
			regexModel.setBlockRegexConfigurationContainer( blockConfCont );
		}
	}
}
