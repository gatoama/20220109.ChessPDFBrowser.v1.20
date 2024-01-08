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

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.view.ReleaseResourcesable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */

/*
	LT - Listener Type
*/

public class ListOfListenersBase< LT > implements ListOfListeners< LT >, ReleaseResourcesable
{
	protected List<LT> _list;

	public ListOfListenersBase()
	{
		_list = new ArrayList<LT>();
	}

	public ListOfListenersBase( ListOfListenersBase<LT> that )
	{
		_list = new ArrayList<LT>(that._list);
	}

	@Override
	public synchronized void add( LT listener )
	{
		_list.add( listener );
	}

	@Override
	public synchronized void remove( LT listener )
	{
		ListIterator<LT> it = _list.listIterator();
		while( it.hasNext() )
		{
			LT elem = it.next();
			if( elem.equals( listener ) )
				it.remove();
		}
	}

	@Override
	public synchronized void clear()
	{
		_list.clear();
	}

	protected List<LT> getList()
	{
		return( _list );
	}

	public Exception safeMethodExecution( ExecutionFunctions.UnsafeMethod run )
	{
		return( ExecutionFunctions.instance().safeMethodExecution(run) );
	}

	@Override
	public void releaseResources()
	{
		clear();
	}
}
