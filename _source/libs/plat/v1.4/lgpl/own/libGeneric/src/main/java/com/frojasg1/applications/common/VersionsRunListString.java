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
package com.frojasg1.applications.common;

import com.frojasg1.general.string.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class VersionsRunListString extends ListString
{
	protected static final Pattern PATTERN_FOR_DATE_STRING = Pattern.compile( "\\d+" );
	protected static final Pattern PATTERN_FOR_VERSION = Pattern.compile( "^.*\\.([vV]\\d+(\\.\\d+){0,10})\\..*$" );

	protected SimpleDateFormat _simpleDateFormat = new SimpleDateFormat( "yyyyMMdd" );

	protected boolean isAppropriateDateString( String candidate )
	{
		boolean result = false;
		try
		{
			result = ( candidate != null ) && ( candidate.length() == 8 ) &&
				( candidate.substring(0,2).equals( "20" ) );
			if( result )
			{
				Date date = _simpleDateFormat.parse(candidate);
			}
		}
		catch( Exception ex )
		{
			result = false;
		}
		return( result );
	}

	public String getDateStringFromVersionFileName( String versionFileName )
	{
		String result = "";
		Matcher matcher = PATTERN_FOR_DATE_STRING.matcher(versionFileName);
		while( matcher.find() )
		{
			String dateStringCandidate = matcher.group();
			if( isAppropriateDateString( dateStringCandidate ) )
			{
				result = dateStringCandidate;
				break;
			}
		}
		return( result );
	}

	public List<String> getReleaseDatesOfVersionsRun()
	{
		return( getList().stream().map(this::getDateStringFromVersionFileName )
					.sorted()
					.collect(Collectors.toList() ) );
	}

	public List<String> getFileNamesOfVersionsRunSortedByDateAsc()
	{
		List<String> result = new ArrayList<>( getList() );
		Collections.sort(result,
			(s1, s2) -> getDateStringFromVersionFileName(s1).compareTo(getDateStringFromVersionFileName(s2) ) );

		return( result );
	}

	public String getVersionFromFileName( String fileName )
	{
		String result = null;
		if( fileName != null )
		{
			Matcher matcher = PATTERN_FOR_VERSION.matcher(fileName);
			if(matcher.find())
			{
				result = matcher.group(1);
			}
		}

		return( result );
	}
}
