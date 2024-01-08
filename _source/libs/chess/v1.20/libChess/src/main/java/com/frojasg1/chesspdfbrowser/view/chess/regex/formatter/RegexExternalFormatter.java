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
package com.frojasg1.chesspdfbrowser.view.chess.regex.formatter;

import com.frojasg1.chesspdfbrowser.engine.tags.regex.BlockRegexBuilder;
import com.frojasg1.chesspdfbrowser.model.regex.parser.BlockToReplaceWith;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexToken;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexTokenId;
import com.frojasg1.general.document.formatted.FormatForText;
import com.frojasg1.general.document.formatter.impl.ExternalTextFormatterBase;
import com.frojasg1.general.string.StringFunctions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.swing.JTextPane;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RegexExternalFormatter extends ExternalTextFormatterBase
{
	public static final String DEFAULT_STYLE = "DEFAULT_STYLE";
	public static final String STYLE_FOR_BLOCK_TO_REPLACE_WITH = "STYLE_FOR_BLOCK_TO_REPLACE_WITH";
	public static final String STYLE_FOR_ERROR = "STYLE_FOR_ERROR";

	protected String _blockToReplaceWithStr = null;
	protected BlockToReplaceWith _blockToReplaceWith = null;

	protected List<RegexToken> _listOfRegexTokens = null;

	protected BlockRegexBuilder _regexBuilder = null;

	protected JTextPane _textPane;

	protected boolean _isNew = false;

	protected String _lastText = null;
	protected Integer _lastCaretPosition = null;

	public RegexExternalFormatter( BlockRegexBuilder regexBuilder,
									JTextPane textPane )
	{
		_regexBuilder = regexBuilder;

		_textPane = textPane;
	}

	public void setTextPane( JTextPane textPane )
	{
		_textPane = textPane;
	}


	protected void setBlockToReplaceWith( String blockToReplaceWithStr )
	{
		if( !Objects.equals( _blockToReplaceWithStr, blockToReplaceWithStr ) )
		{
			_blockToReplaceWithStr = blockToReplaceWithStr;
			_blockToReplaceWith = getBlockToReplaceWith( _blockToReplaceWithStr );
		}
	}

	protected boolean validateRegex( String expression, String blockToReplaceWith )
	{
		Pattern pattern = null;
		try
		{
			pattern = _regexBuilder.getRegexPattern( expression,
												Arrays.asList( blockToReplaceWith ) ).getPattern();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			pattern = null;
		}

		return( pattern != null );
	}

	protected BlockToReplaceWith getBlockToReplaceWith( String blockToReplaceWithStr )
	{
		BlockToReplaceWith result = null;
		
		if( blockToReplaceWithStr != null )
		{
			result = _blockToReplaceWith;
			if( result == null )
			{
				result = new BlockToReplaceWith();
			}

			result.init( blockToReplaceWithStr );
		}

		return( result );
	}

	protected String getBlockName( BlockToReplaceWith blockToReplaceWith )
	{
		String result = null;
		if( blockToReplaceWith != null )
			result = blockToReplaceWith.getBlockName();

		return( result );
	}

	protected int getBlockIndex( BlockToReplaceWith blockToReplaceWith )
	{
		int result = -1;
		if( blockToReplaceWith != null )
			result = blockToReplaceWith.getIndex();

		return( result );
	}

	@Override
	public Collection< FormatForText > formatText( String text )
	{
		Collection< FormatForText > result = null;
		int caretPos = getCaretPosition();
		if( isDifferentFromLast( text, caretPos ) )
		{
			result = new ArrayList<>();

			boolean hasToCheckRegex = true;
	/*
			if( _blockToReplaceWith == null )
			{
				result.add( createFormatForText( text, 0, DEFAULT_STYLE ) );
			}
			else
	*/
			{
				String blockNameToReplaceWith = getBlockName( _blockToReplaceWith );
				int remainingIndex = getBlockIndex( _blockToReplaceWith );
				List<RegexToken> list = getRegexTokenList( text );
				boolean added = false;
				String styleName = null;
				for( RegexToken token: list )
				{
					added = false;
					styleName = DEFAULT_STYLE;
					if( token.getTokenId().equals( RegexTokenId.BLOCK_NAME ) )
					{
						String blockNamePercent = StringFunctions.instance().removeAtEnd(token.getString(), "?" );
						String blockName = token.getTransformedString();
						if( ! _regexBuilder.getBlockContainer().contains(blockName) )
						{
							styleName = STYLE_FOR_ERROR;
							added = true;
							hasToCheckRegex = false;

							if( _isNew )
							{
								if( caretIsInside( token, blockNamePercent, caretPos ) )
								{
									addErrorAndStopFormatting( token, caretPos, text, result );
									break;
								}
							}
						}
						else if( Objects.equals( blockNameToReplaceWith, blockNamePercent ) )
						{
							remainingIndex--;
							if( remainingIndex == 0 )
							{
								styleName = STYLE_FOR_BLOCK_TO_REPLACE_WITH;
								added = true;
							}
						}

						if( added )
						{
							result.add( createFormatForText( blockNamePercent,
													token.getStartPosition(),
													styleName ) );

							if( token.isOptional() )
							{
								styleName = DEFAULT_STYLE;
								result.add( createFormatForText( "?",
														token.getStartPosition() + blockNamePercent.length(),
														styleName ) );
							}
						}
					}

					if( !added )
						result.add( createFormatForText( token.getString(),
												token.getStartPosition(),
												styleName ) );
				}
			}

			if( hasToCheckRegex && !validateRegex( text, _blockToReplaceWithStr ) )
			{
				result.clear();
				result.add( createFormatForText( text, 0, STYLE_FOR_ERROR ) );
			}
		}

		return( result );
	}

	protected boolean isDifferentFromLast( String text, int caretPos )
	{
		boolean isNew = isNew();
		boolean result = !Objects.equals( text, _lastText );// ||
//						!Objects.equals( caretPos, _lastCaretPosition );

		result = ( result || isNew );

		_lastText = text;
		_lastCaretPosition = caretPos;

		return( result );
	}

	protected boolean caretIsInside( RegexToken token, String blockNamePercent,
									int caretPos )
	{
		boolean result = false;

		int relativePos = caretPos - token.getStartPosition();
		result = ( relativePos > 0 ) && ( relativePos < blockNamePercent.length() );

		return( result );
	}

	protected void addErrorAndStopFormatting( RegexToken token, int caretPos,
												String text,
												Collection< FormatForText > result )
	{
		result.add( createFormatForText( token.getString().substring( 0, caretPos - token.getStartPosition() ),
										token.getStartPosition(),
										STYLE_FOR_ERROR ) );

		result.add( createFormatForText( text.substring( caretPos ),
										caretPos,
										DEFAULT_STYLE ) );
	}

	protected int getCaretPosition()
	{
		return( _textPane.getCaretPosition() );
	}

	protected List<RegexToken> getRegexTokenList( String text )
	{
		return( _listOfRegexTokens );
	}

	public boolean isNew()
	{
		return( _isNew );
	}

	public void setIsNew( boolean value )
	{
		_isNew = value;
/*
		if( !_isNew )
		{
			_lastText = null;
			_lastCaretPosition = null;
		}
*/
	}

	public void setRegexTokenList( List<RegexToken> listOfRegexTokens )
	{
		_listOfRegexTokens = listOfRegexTokens;
	}
}
