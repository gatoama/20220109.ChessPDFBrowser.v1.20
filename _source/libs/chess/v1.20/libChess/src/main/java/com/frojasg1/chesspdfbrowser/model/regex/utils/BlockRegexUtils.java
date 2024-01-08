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
package com.frojasg1.chesspdfbrowser.model.regex.utils;

import com.frojasg1.chesspdfbrowser.model.regex.parser.BlockToReplaceWith;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexLexicalAnalyser;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexToken;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexTokenId;
import com.frojasg1.general.string.StringFunctions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class BlockRegexUtils
{
	protected static BlockRegexUtils _instance = null;

//	protected RegexLexicalAnalyser _lex = null;

	protected Map<String, List<RegexToken>> _tokenListCache = null;

	public static BlockRegexUtils instance()
	{
		if( _instance == null )
			_instance = new BlockRegexUtils();

		return( _instance );
	}

	protected BlockRegexUtils()
	{
//		_lex = new RegexLexicalAnalyser();
		_tokenListCache = new HashMap<>();
	}

	protected List<RegexToken> calculateRegexTokenList( String text, boolean permisive )
	{
		List<RegexToken> result = null;

		if( text != null )
		{
			RegexLexicalAnalyser lex = new RegexLexicalAnalyser();
			lex.setOnErrorThrowException( ! permisive );
			result = lex.getListOfTokens( text );
		}

		return( result );
	}

	public List<RegexToken> getRegexTokenList( String expression, boolean permisive )
	{
		List<RegexToken> result = _tokenListCache.get( expression );

		if( result == null )
		{
			result = calculateRegexTokenList( expression, permisive );
			_tokenListCache.put(expression, result);
		}

		return( result );
	}

	public boolean blockIsPresent( String expression, String blockToFind )
	{
		boolean result = false;

		try
		{
			if( blockToFind != null )
			{
				BlockToReplaceWith btrw = new BlockToReplaceWith();
				btrw.init( blockToFind );

				int targetIndex = btrw.getIndex();
				String targetBlockName = btrw.getBlockName();

				int currentIndex = 0;
				boolean permisive = true;
				List<RegexToken> tokenList = getRegexTokenList( expression, permisive );
				for( RegexToken token: tokenList )
				{
					if( token.getTokenId().equals( RegexTokenId.BLOCK_NAME ) )
					{
						String blockName = StringFunctions.instance().removeAtEnd( token.getString(), "?" );
						if( blockName.equals( targetBlockName ) )
						{
							currentIndex++;
							if( currentIndex == targetIndex )
							{
								result = true;
								break;
							}
						}
					}
				}
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}
}
