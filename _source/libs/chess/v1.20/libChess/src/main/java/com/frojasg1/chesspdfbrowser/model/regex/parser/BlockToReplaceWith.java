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

import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.general.number.IntegerFunctions;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class BlockToReplaceWith
{
	public static final String GLOBAL_CONF_FILE_NAME = "BlockToReplaceWith.properties";

	protected static final String CONF_ERROR_OBTAINING_BLOCK_NAME_AND_INDEX = "ERROR_OBTAINING_BLOCK_NAME_AND_INDEX";

	protected static final Pattern REGEX_TO_OBTAIN_BLOCK_AND_INDEX = Pattern.compile( "^(%[^\\[%]+%)\\[([\\d])\\]$" );

	protected static InternationalizedStringConfImp _internationalizedStringConf = null;

	protected String _blockName = null;
	protected int _index = 0;

	static
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								ApplicationConfiguration.instance().getInternationalPropertiesPathInJar() );

		registerInternationalizedStrings();
	}

	public BlockToReplaceWith()//( BaseApplicationConfigurationInterface appConf )
	{
	}

	public void init( String text )
	{
		if( text != null )
		{
			Matcher matcher = REGEX_TO_OBTAIN_BLOCK_AND_INDEX.matcher( text );

			if( ! matcher.matches() )
				throw( new RuntimeException( getInternationalString( CONF_ERROR_OBTAINING_BLOCK_NAME_AND_INDEX ) ) );

			matcher.find(0);
			_blockName = matcher.group(1);
			_index = IntegerFunctions.parseInt( matcher.group(2) );
		}
	}

	public String getBlockName()
	{
		return( _blockName );
	}

	public int getIndex()
	{
		return( _index );
	}

	protected static void registerInternationalizedStrings()
	{
		registerInternationalString(CONF_ERROR_OBTAINING_BLOCK_NAME_AND_INDEX, "Error obtaining blockName and index" );
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
