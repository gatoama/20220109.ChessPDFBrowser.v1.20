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
package com.frojasg1.general.map;

import com.frojasg1.general.ObjectFunctions;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class MapFunctions
{
	protected static MapFunctions _instance;

	public static void changeInstance( MapFunctions inst )
	{
		_instance = inst;
	}

	public static MapFunctions instance()
	{
		if( _instance == null )
			_instance = new MapFunctions();
		return( _instance );
	}
	
	public <KK,VV> VV get( Map<KK,VV> map, KK key )
	{
		return( getIfNotNull( map, m -> m.get(key) ) );
	}

	protected <CC, RR> RR getIfNotNull( CC obj, Function<CC,RR> getter )
	{
		RR result = null;
		if( ( obj != null ) && ( getter != null ) )
			result = getter.apply(obj);

		return( result );
	}

	public <KK, VV> KK getKey( Map.Entry<KK, VV> entry )
	{
		return( getIfNotNull( entry, Map.Entry::getKey ) );
	}

	public <KK, VV> VV getValue( Map.Entry<KK, VV> entry )
	{
		return( getIfNotNull( entry, Map.Entry::getValue ) );
	}

	public <CC extends Comparable<CC>> int compare( CC obj, CC obj2, boolean nullIsMin )
	{
		return( ObjectFunctions.instance().compare(obj, obj2, nullIsMin) );
	}

	public <KK, VV extends Comparable<VV>> Map.Entry<KK, VV> maxValue( Map.Entry<KK, VV> e1, Map.Entry<KK, VV> e2 )
	{
		return( ( compare( getValue(e1), getValue(e2), true ) >= 0 ) ? e1 : e2 );
	}

	public <KK, VV extends Comparable<VV>> Map.Entry<KK, VV> minValue( Map.Entry<KK, VV> e1, Map.Entry<KK, VV> e2 )
	{
		return( ( compare( getValue(e1), getValue(e2), false ) <= 0 ) ? e1 : e2 );
	}

	public <KK, VV extends Comparable<VV>> KK getKeyForMaxValue( Map<KK, VV> map )
	{
		return( getKey( map.entrySet().stream().reduce( null, (e1, e2) -> maxValue(e1, e2) ) ) );
	}

	public <KK, VV extends Comparable<VV>> KK getKeyForMinValue( Map<KK, VV> map )
	{
		return( getKey( map.entrySet().stream().reduce( null, (e1, e2) -> minValue(e1, e2) ) ) );
	}
}
