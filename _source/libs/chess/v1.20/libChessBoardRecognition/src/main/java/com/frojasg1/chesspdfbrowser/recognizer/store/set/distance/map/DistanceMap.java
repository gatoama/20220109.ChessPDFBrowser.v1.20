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
package com.frojasg1.chesspdfbrowser.recognizer.store.set.distance.map;

import com.frojasg1.chesspdfbrowser.recognizer.store.set.distance.ObjsMeanError;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DistanceMap<CC, OO extends ObjsMeanError<CC>> {
	protected Map<String, OO> _map;

	public void init()
	{
		_map = createMap();
	}

	protected <KK, VV> Map<KK, VV> createMap()
	{
		return( new HashMap<>() );
	}

	protected String toString_internal( CC elem )
	{
		return( elem.toString() );
	}

	protected String toString( CC elem )
	{
		String result = "null";
		if( elem != null )
			result = toString_internal( elem );

		return( result );
	}

	public String getKey( CC elem1, CC elem2 )
	{
		String str1 = toString( elem1 );
		String str2 = toString( elem2 );

		if( str1.compareTo( str2 ) > 0 )
		{
			String tmp = str2;
			str2 = str1;
			str1 = tmp;
		}

		return( String.format( "%s-%s", str1, str2 ) );
	}

	public Map<String, OO> getMap()
	{
		return( _map );
	}

	@Override
	public String toString()
	{
		List<OO> list = getMap().values().stream()
			.sorted( (o1, o2) -> o1.toString().compareTo( o2.toString() ) )
			.collect( Collectors.toList() );

		StringBuilder sb = new StringBuilder();

		String retCarr = String.format( "%n" );
		for( OO oo: list )
			sb.append( oo.toString() ).append( retCarr );

		return( sb.toString() );
	}
}
