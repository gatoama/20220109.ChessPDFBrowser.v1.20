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

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RegexLexicalAnalyser
{
	public static final String GLOBAL_CONF_FILE_NAME = "RegexLexicalAnalyser.properties";

	protected static final String CONF_INCOMPLETE_BLOCK_NAME = "INCOMPLETE_BLOCK_NAME";
	protected static final String CONF_ERROR_WHEN_PARSING_REGEX = "ERROR_WHEN_PARSING_REGEX";

	protected static InternationalizedStringConfImp _internationalizedStringConf = null;

	protected boolean _onErrorThrowException = true;

	protected int _position = 0;
	protected String _text = null;

	static
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								ApplicationConfiguration.instance().getInternationalPropertiesPathInJar() );

		registerInternationalizedStrings();
	}

	public void setOnErrorThrowException( boolean onErrorThrowException )
	{
		_onErrorThrowException = onErrorThrowException;
	}


	public void start( String text )
	{
		_position = 0;
		_text = text;
	}

	public List<RegexToken> getListOfTokens( String text )
	{
		List<RegexToken> result = new ArrayList<>();

		start(text);
		RegexToken token = next();
		while( token != null )
		{
			result.add(token);
			token = next();
		}

		return( result );
	}

	protected void giveBackCharGroup( String charGroup )
	{
		_position -= charGroup.length();
	}

	protected String nextCharGroup()
	{
		String result = null;
		if( _position < _text.length() )
		{
			result = _text.substring( _position, _position + 1 );
			_position++;

			if( result.equals( "\\" ) )
			{
				if( _position < _text.length() )
				{
					result += _text.substring( _position, _position + 1 );
					_position++;
				}
				else if( _onErrorThrowException )
					throw( new RuntimeException( createCustomInternationalString( CONF_ERROR_WHEN_PARSING_REGEX,
																				_text , _position ) ) );
			}
		}

		return( result );
	}

	protected String getTransformedStringFromCharGroup( String charGroup )
	{
		String result = charGroup;
		if( result != null )
		{
			if( result.equals( "\\%" ) )
			{
				result = result.substring(1);
			}
		}

		return( result );
	}

	protected RegexToken createToken( int startPosition, String string, String transformedString,
			RegexTokenId tokenId, boolean isOptional )
	{
		return( new RegexToken( startPosition, string, transformedString, tokenId, isOptional ) );
	}

	public RegexToken next()
	{
		int startPosition = _position;
		RegexToken result = null;

		String string = "";
		String transformedString = "";

		RegexTokenId tokenId = RegexTokenId.FLAT_STRING;
		boolean isOptional = false;
		String charGroup = null;
		while( ( charGroup = nextCharGroup() ) != null )
		{
			if( string.isEmpty() || !charGroup.equals("%") ||
				charGroup.equals( string.substring(0,1) ) )
			{
				string += charGroup;
				transformedString += getTransformedStringFromCharGroup( charGroup );

				if( charGroup.equals("%") && (string.length() > 1 ) &&
					charGroup.equals( string.substring(0,1) ) )
				{
					tokenId = RegexTokenId.BLOCK_NAME;
					transformedString = string.substring( 1, string.length() - 1 );

					charGroup = nextCharGroup();
					if( charGroup != null )
					{
						if( charGroup.equals( "?" ) )
						{
							string += charGroup;
							isOptional = true;
						}
						else
						{
							giveBackCharGroup( charGroup );
						}
					}
					break;
				}
			}
			else
			{
				giveBackCharGroup( charGroup );
				break;
			}
		}

		if( ! string.isEmpty() )
		{
			if( _onErrorThrowException &&
				tokenId.equals( RegexTokenId.FLAT_STRING ) && string.substring(0,1).equals( "%" ) )
				throw( new RuntimeException( createCustomInternationalString( CONF_INCOMPLETE_BLOCK_NAME, _text ) ) );

			result = createToken( startPosition, string, transformedString, tokenId, isOptional );
		}

		return( result );
	}

	protected static void registerInternationalizedStrings()
	{
		registerInternationalString(CONF_INCOMPLETE_BLOCK_NAME, "Incomplete blockName: $1" );
		registerInternationalString(CONF_ERROR_WHEN_PARSING_REGEX, "Error when parsing regex of blocks expression: $1 at position: $2" );
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
