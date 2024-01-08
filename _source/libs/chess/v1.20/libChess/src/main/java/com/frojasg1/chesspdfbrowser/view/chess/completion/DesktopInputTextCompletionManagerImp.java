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
package com.frojasg1.chesspdfbrowser.view.chess.completion;

import com.frojasg1.chesspdfbrowser.model.regex.BlockRegexConfigurationContainer;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexLexicalAnalyser;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexToken;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexTokenId;
import com.frojasg1.general.desktop.completion.base.DesktopInputTextCompletionManagerBase;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DesktopInputTextCompletionManagerImp extends DesktopInputTextCompletionManagerBase
{
	protected CompletionConfiguration _conf = null;

	protected BlockRegexConfigurationContainer _blockConfigurationContainer = null;

	public DesktopInputTextCompletionManagerImp( CompletionConfiguration conf,
				BlockRegexConfigurationContainer blockConfigurationContainer )//TextCompletionConfiguration conf )
	{
		super( );

		_conf = conf;
		_blockConfigurationContainer = blockConfigurationContainer;
	}

	@Override
	protected String getCaretWord( String text, int caretPos )
	{
		String result = "";

		if( ( text != null ) && ( caretPos <= text.length() ) )
		{
			String str = text.substring( 0, caretPos );

			RegexLexicalAnalyser lex = new RegexLexicalAnalyser();
			lex.setOnErrorThrowException( false );

			List<RegexToken> list = lex.getListOfTokens( str );

			if( ! list.isEmpty() )
			{
				RegexToken lastToken = list.get( list.size() - 1 );

				if( lastToken.getTokenId().equals( RegexTokenId.FLAT_STRING ) )
				{
					String tmp = lastToken.getString();
					if( ! tmp.isEmpty() && tmp.startsWith( "%" ) )
						result = tmp;
				}
			}

			if( ! result.isEmpty() )
			{
				if( existsBlockAt( text, caretPos, lex ) )
					result = "";
			}
		}

		return( result );
	}

	protected boolean existsBlockAt( String text, int caretPos, RegexLexicalAnalyser lex )
	{
		boolean result = false;

		List<RegexToken> list = lex.getListOfTokens( text );
		for( RegexToken token: list )
		{
			if( ( token.getStartPosition() + token.getString().length() ) > caretPos )
			{
				if( token.getTokenId().equals( RegexTokenId.BLOCK_NAME ) )
				{
					String blockName = token.getTransformedString();

					if( _blockConfigurationContainer.contains( blockName ) )
						result = true;
				}
				break;
			}
		}

		return( result );
	}

	@Override
	protected boolean hasToShowCompletionWindow()
	{
		boolean result = false;
		if( _conf != null )
			result = _conf.isAutocompletionForRegexActivated();

		return( result ); //_conf.hasToShowCompletionWindow() );
	}

	@Override
	protected CurrentParamResult getCurrentParam(String inputText, int caretPos)
	{
		return( null );
	}

	@Override
	protected CurrentParamResult createCurrentParamResult(String name, int paramCount)
	{
		return( null );
	}
}
