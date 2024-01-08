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
package com.frojasg1.general.completion;

import com.frojasg1.general.containers.DoubleComparator;
import com.frojasg1.general.string.StringFunctions;
import java.util.Comparator;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class MapOfPrototypesBase
{
	protected PreTextStringOrderedArrayMap< PrototypeForCompletionBase > _map = null;

	public MapOfPrototypesBase()
	{
		_map = createMapOfPrototypes();
	}

	protected abstract PrototypeManagerInitBase createPrototypeManagerInit();

	protected PreTextStringOrderedArrayMap< PrototypeForCompletionBase > createMapOfPrototypes()
	{
		PreTextStringOrderedArrayMap< PrototypeForCompletionBase > result = new PreTextStringOrderedArrayMap<>(PrototypeForCompletionBase.class);

//		result.setExactValueComparator( new ExactPrototypeForCompletionComparator() );

		return( result );
	}

	public void clear()
	{
		_map.clear();
	}

	public PrototypeForCompletionBase get( String name )
	{
		return( _map.getValueFromKey(name) );
	}

	public PrototypeForCompletionBase getFirst( String name )
	{
		return( _map.getFirstValueFromKey(name) );
	}

	public void put( PrototypeForCompletionBase value )
	{
		if( value != null )
			_map.put( value.getName(), value );
	}

	public PrototypeForCompletionBase remove( String name )
	{
		return( _map.remove(name) );
	}

	public PrototypeForCompletionBase removeFirst( String name )
	{
		return( _map.removeFirst(name) );
	}

	public PrototypeForCompletionBase[] getPrototypeRange( String preText )
	{
		return( _map.getRangeFromSimplifiedKey(preText, preText) );
	}

	protected abstract PrototypeForCompletionBase createPrototypeForCompletion( String name, String type );
/*
	{
		return( PrototypeForCompletionFactory.instance().createObject(name, type) );
	}
*/
	public PrototypeForCompletionBase removeExact( String name, String type )
	{
		PrototypeForCompletionBase result = _map.removeExact(name, createPrototypeForCompletion( name, type ) );

		return( result );
	}

	protected static class ExactPrototypeForCompletionComparator
					implements DoubleComparator< PrototypeForCompletionBase, PrototypeForCompletionBase >
	{
		protected static final Comparator<String> _strComparator = StringFunctions.instance().getExactComparator();

		@Override
		public int compare(PrototypeForCompletionBase mm, PrototypeForCompletionBase cc)
		{
			int result = 0;
			
			if( ( mm == null ) && ( cc == null ) )
			{
				
			}
			else if( mm==null)
				result = -1;
			else if( cc == null )
				result = 1;
			else
			{
				result = _strComparator.compare( mm.getName(), cc.getName() );
				if( result == 0 )
					result = _strComparator.compare( mm.getType(), cc.getType() );
			}

			return( result );
		}
	}

	
}
