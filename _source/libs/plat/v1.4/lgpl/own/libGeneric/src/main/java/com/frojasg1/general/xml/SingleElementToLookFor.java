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
package com.frojasg1.general.xml;

import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.string.StringFunctions;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class SingleElementToLookFor
{
	protected String _tagName = null;
	protected int _index = 0;
	protected String _attValPairList = null;

	protected Map< String, String > _attValPairMap = new HashMap<>();

	// temporal variables for parsing
	protected int _startingPos = 0;
	protected int _nextPos = 0;

	public SingleElementToLookFor( String tagName, int index, String attValPairList )
	{
		_tagName = tagName;
		_index = index;

		_startingPos = 0;

		_attValPairList = parseOptional( attValPairList, '<', '>' );
		fillAttValPairMap( );
	}

	// format of composedTag:    tag1<att1=val1,...,attN=valN>[ind]. ... .tagN<att1=val1,...,attN=valN>[ind]
	public SingleElementToLookFor( String composedLocation )
	{
		parse( composedLocation );
	}

	protected String parseOptional( String composedLocation, char startingChar, char endingChar )
	{
		_nextPos = composedLocation.indexOf( startingChar );
		String result = null;

		if( ( _nextPos > -1 ) && ( _nextPos < composedLocation.length() ) )
		{
			if( _tagName == null )
				_tagName = composedLocation.substring( _startingPos, _nextPos );
			_startingPos = _nextPos;

			_nextPos = composedLocation.indexOf( endingChar, _startingPos );
			if( ( _nextPos > -1 ) && ( _nextPos< composedLocation.length() ) )
			{
				result = composedLocation.substring( _startingPos + 1, _nextPos );
				_startingPos = _nextPos;
			}
			else
			{
				throwException( composedLocation );
			}
		}

//		if( _tagName == null )
//			_tagName = composedLocation;

		return( result );
	}

	protected void throwException( String composedLocation )
	{
		throw( new RuntimeException( "Error parsing composedLocation: " + composedLocation ) );
	}

	protected void parse( String composedLocation )
	{
		_tagName = null;
		_startingPos = 0;

		_attValPairList = parseOptional( composedLocation, '<', '>' );
		fillAttValPairMap();

		String indexStr = parseOptional( composedLocation, '[', ']' );

		if( indexStr != null )
		{
			Integer indexInt = IntegerFunctions.parseInt( indexStr );
			if( indexInt == null )
				throwException( composedLocation );

			_index = indexInt;
		}
		else
		{
			_index = 1;
		}

		if( _tagName == null )
			_tagName = composedLocation;
	}

	protected void fillAttValPairMap( )
	{
		if( ! StringFunctions.instance().isEmpty(_attValPairList) )
		{
			String[] attValArr = _attValPairList.split( "," );
			for( int ii=0; ii<attValArr.length; ii++ )
			{
				String[] attVal = attValArr[ii].split( "=" );
				_attValPairMap.put( attVal[0], attVal[1] );
			}
		}
	}

	public String getTagName()
	{
		return( _tagName );
	}

	public int getIndex()
	{
		return( _index );
	}

	public void setTagName( String tagName )
	{
		_tagName = tagName;
	}

	public void setIndex( int index )
	{
		_index = index;
	}

	public String getAttValPairList()
	{
		return( _attValPairList );
	}

	public void setAttValPairList( String value )
	{
		_attValPairList = value;
	}

	public Map<String, String> getAttValPairMap()
	{
		return( _attValPairMap );
	}
}
