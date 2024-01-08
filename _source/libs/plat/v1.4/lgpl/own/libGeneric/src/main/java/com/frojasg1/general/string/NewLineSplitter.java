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
package com.frojasg1.general.string;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class NewLineSplitter
{
	protected static Pattern _newLinePattern;

	protected static NewLineSplitter _instance = null;

	static
	{
		try
		{
			_newLinePattern = Pattern.compile( "([^\n\r]*)(\r\n|\r|\n|$)" );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	public static NewLineSplitter instance()
	{
		if( _instance == null )
			_instance = new NewLineSplitter();

		return( _instance );
	}
	
	public List<String> split( String text )
	{
		List<String> result = new ArrayList<>();

		Matcher matcher = _newLinePattern.matcher( text );
		while( matcher.find() )
		{
			if( matcher.groupCount() > 0 )
				result.add( matcher.group(1) );
		}
		return( result );
	}
}
