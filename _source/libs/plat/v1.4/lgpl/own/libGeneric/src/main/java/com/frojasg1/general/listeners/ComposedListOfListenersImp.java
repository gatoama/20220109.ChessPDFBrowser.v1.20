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
package com.frojasg1.general.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComposedListOfListenersImp<KK, LT> implements ComposedListOfListeners<KK,LT>
{
	protected Map< KK, List<LT> > _map;

	public ComposedListOfListenersImp()
	{
		_map = new HashMap<>();
	}

	protected List<LT> getParticularListOfListeners( KK key )
	{
		List<LT> result = _map.get(key);
		if( result == null )
		{
			result = new ArrayList<>();
			_map.put( key, result );
		}

		return( result );
	}


	@Override
	public void add( KK key, LT listener )
	{
		List<LT> list = this.getParticularListOfListeners(key);
		
		if( !list.contains( key ) )
			list.add( listener );
	}

	protected void remove( List<LT> list, LT listener )
	{
		if( list != null )
		{
			ListIterator<LT> it = list.listIterator();
			while( it.hasNext() )
			{
				LT elem = it.next();
				if( elem.equals( listener ) )
				{
					it.remove();
				}
			}
		}
	}

	@Override
	public void remove( KK key, LT listener )
	{
		List<LT> list = _map.get(key);
		
		if( list != null )
		{
			remove( list, listener );
			if( list.size() == 0 )
				_map.remove( key );
		}
	}

	@Override
	public void clear()
	{
		_map.clear();
	}
}
