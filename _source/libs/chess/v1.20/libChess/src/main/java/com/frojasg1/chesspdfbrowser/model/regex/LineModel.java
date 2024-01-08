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

import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.model.regex.utils.BlockRegexUtils;
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import com.frojasg1.general.combohistory.TextComboBoxContent;
import com.frojasg1.general.combohistory.impl.TextComboBoxHistory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class LineModel
{
	public static final String GLOBAL_CONF_FILE_NAME = "LineModel.properties";

	protected static final String CONF_BLOCK_TO_BE_REPLACED_TO_GET_TAG_IS_NOT_PRESENT = "BLOCK_TO_BE_REPLACED_TO_GET_TAG_IS_NOT_PRESENT";

	protected static final String SYNCHRONIZATION_REGEX_NAME = "SYNCHONIZATION_REGEX";

	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();
	protected static InternationalizedStringConfImp _internationalizedStringConf = null;

	protected ProfileModel _parent = null;

	protected int _index = -1;

	protected RegexOfBlockModel _synchronizationRegexModel = null;

	protected Map<String, TagReplacementModel> _map = null;

	protected TextComboBoxContent _cbContent = null;

	protected boolean _isOptional = false;

	static
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								ApplicationConfiguration.instance().getInternationalPropertiesPathInJar() );

		registerInternationalizedStrings();
	}

	// function for DefaultConstructorInitCopier
	public LineModel()
	{
	}

	public void init(ProfileModel parent)
	{
		_parent = parent;

		_map = new HashMap<>();

		_synchronizationRegexModel = createRegexOfBlockModel(SYNCHRONIZATION_REGEX_NAME );

		_cbContent = createComboEmptyBoxHistory();
	}

	// function for DefaultConstructorInitCopier
	public void init( LineModel that )
	{
		_map = _copier.copyMap( that._map );

		_parent = null;

		// do not change, as block regex configuration must be accessible at the same memory address
		// even after the invocation to this function
		// for its memory address not to change
		if( _cbContent == null )
			_cbContent = createComboEmptyBoxHistory();
		_copier.copy( _cbContent, that._cbContent );

		_synchronizationRegexModel = _copier.copy( that._synchronizationRegexModel );

		_isOptional = that._isOptional;
	}

	public void setSynchronizationExpression( String synchronizationExpression )
	{
		getSynchronizationRegexModel().setExpression(synchronizationExpression);
	}

	protected RegexOfBlockModel createRegexOfBlockModel( String blockName )
	{
		RegexOfBlockModel result = new RegexOfBlockModel( blockName, _parent.getParent().getBlockConfigurationContainer() );

		return( result );
	}

	public int getIndex()
	{
		return( getParent().getListOfLines().indexOf( this ) );
	}

	public void setParent( ProfileModel parent )
	{
		_parent = parent;
	}

	public ProfileModel getParent( )
	{
		return( _parent );
	}

	public TextComboBoxHistory createComboEmptyBoxHistory()
	{
		TextComboBoxHistory result = new TextComboBoxHistory();
		result.init( (List<String>) null );

		return( result );
	}

	protected TagReplacementModel createTagReplacementModel( String tagName, TagReplacementModel other )
	{
		RegexWholeFileModel rcwc = _parent.getParent();
		TagReplacementModel result = new TagReplacementModel();
		result.setTagName( tagName );

		if( other != null )
			result.init( other );
//		else
//			result.init( regexConf );

		return( result );
	}

	public TagReplacementModel addTagRegexReplacement( String tagName, TagReplacementModel other )
	{
		TagReplacementModel result = createTagReplacementModel( tagName, other );
		if( result != null )
		{
			_map.put( tagName, result );
			getComboBoxContent().addItem(tagName);
		}

		return( result );
	}

	public TextComboBoxContent getComboBoxContent()
	{
		return( _cbContent );
	}

	public TagReplacementModel get( String tagName )
	{
		return( _map.get( tagName ) );
	}

	public void put( String tagName, TagReplacementModel value )
	{
		_map.put( tagName, value );
	}

	public boolean remove( String tagName )
	{
		TagReplacementModel value = _map.remove( tagName );
		_cbContent.removeItem(tagName);

		return( value != null );
	}

	public boolean contains( String regexName )
	{
		return( _cbContent.contains( regexName ) );
	}

	public Map<String, TagReplacementModel> getMap()
	{
		return( _map );
	}

	public RegexOfBlockModel getSynchronizationRegexModel()
	{
		return( _synchronizationRegexModel );
	}

	public RegexWholeFileModel getRegexWholeContainer()
	{
		return( getParent().getParent() );
	}

	public boolean isOptional()
	{
		return( _isOptional );
	}

	public void setOptional( boolean value )
	{
		_isOptional = value;
	}

	protected Pattern getRegexPattern( RegexWholeFileModel rwfm, String expression )
	{
		return( rwfm.getBlockRegexBuilder().getRegexPattern( expression ) );
	}

	protected boolean blockIsPresent( String expression, String blockToFind )
	{
		return( BlockRegexUtils.instance().blockIsPresent(expression, blockToFind) );
	}

	public void validate()
	{
		String expression = getSynchronizationRegexModel().getExpression();
		getRegexPattern( getRegexWholeContainer(), expression );

		for( Map.Entry<String, TagReplacementModel> entry: getMap().entrySet() )
		{
			if( !blockIsPresent( expression, entry.getValue().getBlockToReplaceWith() ) )
				throw( new RuntimeException( createCustomInternationalString( CONF_BLOCK_TO_BE_REPLACED_TO_GET_TAG_IS_NOT_PRESENT, entry.getKey() ) ) );
		}
	}

	protected static void registerInternationalizedStrings()
	{
		registerInternationalString(CONF_BLOCK_TO_BE_REPLACED_TO_GET_TAG_IS_NOT_PRESENT, "Block to be replaced to get tag is not present. BlockName: $1" );
	}

	public static void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	public String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}

	public String createCustomInternationalString(String label, Object... args) {
		return( _internationalizedStringConf.createCustomInternationalString( label, args ) );
	}
}
