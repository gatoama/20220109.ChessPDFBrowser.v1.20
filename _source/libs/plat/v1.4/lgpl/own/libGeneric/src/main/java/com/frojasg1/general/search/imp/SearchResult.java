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
public class SearchResult implements SearchReplaceTextInterface.SearchResultInterface
{
	protected int _start = -1;
	protected String _matchedStr = null;
	protected String _completeText = null;

	public SearchResult( int start, String matchedStr, String completeText )
	{
		_start = start;
		_matchedStr = matchedStr;
		_completeText = completeText;
	}
	
	@Override
	public int getStart()			{ return( _start );	}

	@Override
	public String getMatchedString() { return( _matchedStr );	}

	@Override
	public String getCompleteText() {return( _completeText );	}

	@Override
	public boolean resultsDifferentFromNoResults()
	{
		return( true );
	}

}
