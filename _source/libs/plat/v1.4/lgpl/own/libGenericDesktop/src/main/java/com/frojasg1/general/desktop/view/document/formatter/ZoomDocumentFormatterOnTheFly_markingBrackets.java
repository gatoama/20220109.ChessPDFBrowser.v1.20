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
package com.frojasg1.general.desktop.view.document.formatter;

import com.frojasg1.general.document.formatted.FormatForText;
import com.frojasg1.applications.common.configuration.application.ChangeZoomFactorServerInterface;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomDocumentFormatterOnTheFly_markingBrackets extends ZoomDocumentFormatterOnTheFly implements ExternalTextFormatterDesktop
{
//	protected final String RED_BOLD = "DocumentListenerToMarkBrackets_RED_BOLD";
//	protected final String GREEN = "DocumentListenerToMarkBrackets_GREEN";
//	protected final String PLAIN = "DocumentListenerToMarkBrackets_PLAIN";

	protected final String DEFAULT_STYLE_NAME_TO_MARK_BRACKETS = "DEFAULT_STYLE_NAME_TO_MARK_BRACKETS";

	protected Color _originalMarkedBracketColor = Color.RED;
	protected Color _currentMarkedBracketColor = Color.RED;

	public ZoomDocumentFormatterOnTheFly_markingBrackets( JTextPane pane,
															ChangeZoomFactorServerInterface changeZoomFactorServer )
	{
		super( pane, changeZoomFactorServer );

		setExternalTextFormatter( this );
	}

	@Override
	protected void addParticularStyles( Integer defaultFontSize )
	{
		final Style defaultStyleToMarkBrackets = this.newFormattedStyleToBeModified(DEFAULT_STYLE_NAME_TO_MARK_BRACKETS);
		StyleConstants.setFontSize(defaultStyleToMarkBrackets, defaultFontSize + 2);
		StyleConstants.setForeground(defaultStyleToMarkBrackets, getCurrentMarkedBracketColor() );
		StyleConstants.setBold(defaultStyleToMarkBrackets, true);
	}

	protected Color getCurrentMarkedBracketColor()
	{
		return( _currentMarkedBracketColor );
	}

	protected List<CharacterAnalyzerNextResult> getBracketsAndQuotesLocation( Document doc )
	{
		ArrayList<CharacterAnalyzerNextResult> list = new ArrayList<CharacterAnalyzerNextResult>();

		try
		{
			CharacterAnalyzer lex = new CharacterAnalyzer( doc.getText( 0, doc.getLength() ) );
			
			CharacterAnalyzerNextResult character = null;
			while( (character = lex.next()) != null )
			{
				if( !character.a_isInsideQuotedString && (character.a_str.equals( "(" ) || character.a_str.equals(")" )) )
					list.add( character );
				else if( character.a_str.equals( "\"" ) )
					list.add( character );
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
			
		return( list );
	}
/*
	protected void setQuotedStringsStyle( List<CharacterAnalyzerNextResult> bl )
	{
		Iterator<CharacterAnalyzerNextResult> it = bl.iterator();
			
		boolean hasToContinue = true;
		while( hasToContinue && it.hasNext() )
		{
			CharacterAnalyzerNextResult _char = it.next();
			if( _char.a_str.equals( "\"" ) )
			{
				int pos1 = _char.a_position;
				int pos2 = _pane.getDocument().getLength();
				if( it.hasNext() )
				{
					CharacterAnalyzerNextResult _char2 = it.next();
					pos2 = _char2.a_position + 1;
				}
				_pane.getStyledDocument().setCharacterAttributes(pos1, pos2-pos1, _pane.getStyle(GREEN), true);
			}
		}
	}
*/	
	protected boolean isCharAtPosition( Document doc, int position, String _char )
	{
		boolean result = false;
		try
		{
			if( ( doc.getLength() > position ) && (position >= 0 ) )
			{
				result = doc.getText(position, 1).equals( _char );
			}
		}
		catch( Throwable th )
		{
			result = false;
		}
		return( result );
	}

	protected int findMatchingBracePosition( int position, List<CharacterAnalyzerNextResult> bl )
	{
		int result = -1;
			
		int step = 0;

		int index = getBraceAtPosition( bl, position );
		if( index >= 0 )
		{
			String _char = bl.get(index).a_str;

			if( _char.equals( ")" ) )
			{
				step = -1;
			}
			else if( _char.equals( "(" ) )
			{
				step = 1;
			}

			int indexMatched = getMatchingBrace( bl, index, step );
				
			if( indexMatched >= 0 )
				result = bl.get(indexMatched).a_position;
		}

		return( result );
	}

	protected int getBraceAtPosition( List<CharacterAnalyzerNextResult> bl, int position )
	{
		int result = -1;
			
		Iterator<CharacterAnalyzerNextResult> it = bl.iterator();
			
		boolean hasToContinue = true;
		int ii=0;
		while( hasToContinue && it.hasNext() )
		{
			CharacterAnalyzerNextResult bracket = it.next();
			if( bracket.a_position == position )
			{
				result = ii;
				hasToContinue = false;
			}
			else if( bracket.a_position > position )
				hasToContinue = false;
			ii++;
		}
			
		return( result );
	}

	protected int getMatchingBrace( List<CharacterAnalyzerNextResult> bl, int index, int step )
	{
		int result = -1;
			
		int numOpenBrackets = step;
		int ii = index + step;
		CharacterAnalyzerNextResult bracket = null;
		while( ( result < 0 ) && (ii < bl.size() ) && ( ii >= 0 ) )
		{
			bracket = bl.get( ii );

			if( bracket.a_str.equals( "(" ) )	numOpenBrackets++;
			else if( bracket.a_str.equals( ")" ) ) numOpenBrackets --;
				
			if( numOpenBrackets == 0 )	result = ii;
			ii = ii + step;
		}
			
		return( result );
	}

	@Override
	protected Color[] createOriginalInvertibleColors() {
		return( null );
	}

	@Override
	protected void invertColorsInternal( ColorInversor ci )
	{
		if( !isDarkMode() )
			_currentMarkedBracketColor = _originalMarkedBracketColor;
		else
			_currentMarkedBracketColor = ci.invertColor(_originalMarkedBracketColor);

		super.invertColorsInternal(ci);
	}

	public class CharacterAnalyserException extends Exception
	{
		public CharacterAnalyserException( String message )
		{
			super( message );
		}
	}
		
	protected class CharacterAnalyzerNextResult
	{
		public CharacterAnalyzerNextResult( boolean isInsideQuotedString, String str, int position )
		{
			a_isInsideQuotedString = isInsideQuotedString;
			a_str = str;
			a_position = position;
		}

		public boolean a_isInsideQuotedString;
		public String a_str;
		public int a_position;
	}
		
		
	protected class CharacterAnalyzer
	{
		protected String a_stringToParse;
		protected boolean a_isQuotedString;
		protected int a_currentPosition;
		
		public CharacterAnalyzer( String stringToParse )
		{
			a_stringToParse = stringToParse;
			a_isQuotedString = false;
			a_currentPosition = 0;
		}
			
		public CharacterAnalyzerNextResult next() throws CharacterAnalyserException
		{
			CharacterAnalyzerNextResult result = null;
			String resultStr = null;

			if( a_currentPosition < a_stringToParse.length() )
			{
				result = new CharacterAnalyzerNextResult( a_isQuotedString, null, a_currentPosition );
				resultStr = a_stringToParse.substring( a_currentPosition, a_currentPosition + 1 );

				boolean isDoubleDoubleQuote = false;

				if( a_isQuotedString )
				{
/*
						if( resultStr.equals( "\\" ) )
						{
							a_currentPosition++;
							if( a_currentPosition < a_stringToParse.length() )
							{
								resultStr = resultStr + a_stringToParse.substring( a_currentPosition, a_currentPosition + 1 );
							}
							else
							{
								throw( new LexicalException( "Not ended character in a quoted string" ) );
							}
						}
*/
					// two double quote characters in a row mean a double quote character inside the string but not the end of the string.
					if( resultStr.equals( "\"" ) )
					{
						if( (a_currentPosition+1) < a_stringToParse.length() )
						{
							if( a_stringToParse.substring( a_currentPosition + 1, a_currentPosition + 2 ).equals( "\"" ) )
							{
								isDoubleDoubleQuote = true;		// so that the string does not close.
								a_currentPosition++;			// if there are two double quotes in a row, we ignore the first one
								resultStr = resultStr + a_stringToParse.substring( a_currentPosition, a_currentPosition + 1 );
							}
						}
					}
				}

				if( !isDoubleDoubleQuote && resultStr.equals( "\"" ) )	// if we find a double quote, we enter the string mode
				{
					a_isQuotedString = ! a_isQuotedString;
					result.a_isInsideQuotedString = true;
				}
					
				result.a_str = resultStr;
				a_currentPosition++;
			}

			return( result );
		}
	}

	protected String getStyleNameToMarkBrackets()
	{
		return( DEFAULT_STYLE_NAME_TO_MARK_BRACKETS );
	}

	@Override
	public Collection< FormatForText > formatText( String text )
	{
		Collection< FormatForText > result = new ArrayList<>();

		if( _pane.hasFocus() )
		{
			Document doc = _pane.getDocument();
			int initialPosition = _pane.getCaretPosition();
			
			int position = initialPosition;
			String _char = "(";
			boolean found = true;
			if( !isCharAtPosition( doc, position, _char ) )
			{
				position = initialPosition - 1;
				if( !isCharAtPosition( doc, position, _char ) )
				{
					position = initialPosition;
					_char = ")";
					if( !isCharAtPosition( doc, position, _char ) )
					{
						position = initialPosition - 1;
						if( !isCharAtPosition( doc, position, _char ) ) found = false;
					}
				}
			}

			if( found )
			{
				List<CharacterAnalyzerNextResult> bl = getBracketsAndQuotesLocation( doc );

				int matchingPosition = findMatchingBracePosition( position, bl );

				try
				{
					if( matchingPosition >= 0 )
					{
						result.add( createFormatForText( doc.getText(position, 1), position, DEFAULT_STYLE_NAME_TO_MARK_BRACKETS ) );
						result.add( createFormatForText( doc.getText(matchingPosition, 1), matchingPosition, DEFAULT_STYLE_NAME_TO_MARK_BRACKETS ) );
					}
				}
				catch( Exception ex )
				{
					ex.printStackTrace();
				}
			}
		}

		return( result );
	}
}
