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

import com.frojasg1.general.search.SearchReplaceTextInterface;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ReplaceSettings extends SearchSettings implements SearchReplaceTextInterface.ReplaceSettingsInterface
{
	protected String _strToReplaceTo = null;
	protected String _regExToReplaceTo = null;

	public ReplaceSettings( boolean useRegEx, boolean matchWholeWords, boolean matchCase,
							String textToLookFor, String regExToLookFor,
							String strToReplaceTo,
							String regExToReplaceTo )
	{
		super( useRegEx, matchWholeWords, matchCase, textToLookFor, regExToLookFor );

		_strToReplaceTo = strToReplaceTo;
		_regExToReplaceTo = regExToReplaceTo;
	}

	public ReplaceSettings( ReplaceSettings other )
	{
		this( other._useRegEx, other._matchWholeWords, other._matchCase,
				other._textToLookFor, other._regExToLookFor,
				other._strToReplaceTo, other._regExToReplaceTo );
	}

	@Override
	public void setStringToReplaceTo(String value) { _strToReplaceTo = value;	}

	@Override
	public String getStringToReplaceTo() { return( _strToReplaceTo );	}

	@Override
	public void setRegExToReplaceTo(String regEx) { _regExToReplaceTo = regEx;	}

	@Override
	public String getRegExToReplaceTo() { return( _regExToReplaceTo );	}

	@Override
	public boolean equals( Object other )
	{
		boolean result = super.equals( other );
/*
		if( result && ( other instanceof ReplaceSettings ) )
		{
			ReplaceSettings otherRS = (ReplaceSettings) other;
			result = GeneralFunctions.instance().equals( _strToReplaceTo, otherRS._strToReplaceTo ) &&
						GeneralFunctions.instance().equals( _regExToReplaceTo, otherRS._regExToReplaceTo );
		}
*/
		return( result );
	}

	public ReplaceSettings createCopy()
	{
		return( new ReplaceSettings( this ) );
	}
}

