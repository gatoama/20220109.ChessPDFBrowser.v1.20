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
package com.frojasg1.general.search.imp;

import com.frojasg1.general.GeneralFunctions;
import com.frojasg1.general.search.SearchReplaceTextInterface;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class SearchSettings implements SearchReplaceTextInterface.SearchSettingsInterface
{
	protected boolean _useRegEx = false;
	protected boolean _matchWholeWords = false;
	protected boolean _matchCase = false;
	protected String _textToLookFor = null;
	protected String _regExToLookFor = null;

	public SearchSettings( boolean useRegEx, boolean matchWholeWords, boolean matchCase,
							String textToLookFor, String regExToLookFor )
	{
		_useRegEx = useRegEx;
		_matchWholeWords = matchWholeWords;
		_matchCase = matchCase;
		_textToLookFor = textToLookFor;
		_regExToLookFor = regExToLookFor;
	}

	public SearchSettings( SearchSettings other )
	{
		this( other._useRegEx, other._matchWholeWords, other._matchCase,
				other._textToLookFor, other._regExToLookFor );
	}

	@Override
	public void setUseRegEx(boolean value) {	_useRegEx = value; }

	@Override
	public void setMatchWholeWords(boolean value) {	_matchWholeWords = value;	}

	@Override
	public void setMatchCase(boolean value) { _matchCase = value;	}

	@Override
	public void setTextToLookFor(String textToMatch) { _textToLookFor = textToMatch;	}

	@Override
	public void setRegEx(String regex) { _regExToLookFor = regex;	}

	@Override
	public boolean getUseRegEx() { return( _useRegEx );	}

	@Override
	public boolean getMatchWholeWords() { return( _matchWholeWords );	}

	@Override
	public boolean getMatchCase() { return( _matchCase );	}

	@Override
	public String getTextToLookFor() { return( _textToLookFor );	}

	@Override
	public String getRegEx() { return( _regExToLookFor );	}

	@Override
	public boolean equals( Object other )
	{
		
		boolean result = false;
		
		if( other instanceof SearchSettings )
		{
			SearchSettings otherSS = ( SearchSettings ) other;
			result = ( _useRegEx == otherSS._useRegEx ) &&
						( _matchWholeWords == otherSS._matchWholeWords ) &&
						( _matchCase == otherSS._matchCase ) &&
						GeneralFunctions.instance().equals( _textToLookFor, otherSS._textToLookFor ) &&
						GeneralFunctions.instance().equals( _regExToLookFor, otherSS._regExToLookFor );
		}
		
		return( result );
	}

	public SearchSettings createCopy()
	{
		return( new SearchSettings( this ) );
	}
}
