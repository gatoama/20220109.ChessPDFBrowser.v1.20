/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.analysers.impl;

import com.frojasg1.general.analysers.GenListSplitterBase;
import com.frojasg1.general.analysers.GenListSplitterBase;
import java.util.Objects;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DefaultListQuotedDataSplitter extends GenListSplitterBase
{
	protected static final String QUOTE_CHARS = "\"'";
	
	protected String _notableChars;

	public DefaultListQuotedDataSplitter( String string, String separator )
	{
		super( string, separator );

		_notableChars = QUOTE_CHARS + separator;
	}

	protected void passWholeAttribute()
	{
		String ch = null;
		loop: while( ! hasEnded() )
		{
			ch = getNextNotableChar();
			if( ch == null )
				break;

			_pos++;

			switch( ch )
			{
				case "'":
				case "\"":
//					String ch2 = incPosAndGetMatchingChar(ch);
					String ch2 = getMatchingChar(ch);
					if( !Objects.equals( ch2, ch) )
						throw( new RuntimeException( "Matching quote not found at pos: " + _pos ) );
					_pos++;

				break;

				default:
					if( isSeparator( ch ) )
					{
						break loop;
					}
			}
		}
	}

	protected String getNextNotableChar()
	{
		return( getNextNotableChar(_notableChars) );
	}
}
