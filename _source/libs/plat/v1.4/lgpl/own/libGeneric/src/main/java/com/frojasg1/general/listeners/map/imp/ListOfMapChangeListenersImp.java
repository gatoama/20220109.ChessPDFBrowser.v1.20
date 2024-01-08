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
package com.frojasg1.general.listeners.map.imp;

import com.frojasg1.general.listeners.GenericObserved;
import com.frojasg1.general.listeners.ListOfListenersImp;
import com.frojasg1.general.listeners.map.MapChangeListener;
import com.frojasg1.general.listeners.map.ListOfMapChangeListeners;
import com.frojasg1.general.listeners.map.MapChangeObserved;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ListOfMapChangeListenersImp<KK, VV> extends ListOfListenersImp< MapChangeListener<KK, VV> >
									implements ListOfMapChangeListeners<KK, VV>
{

	@Override
	public void elementPut(MapChangeObserved<KK, VV> observed, KK key, VV value)
	{
		for( MapChangeListener ml: _list )
			ml.elementPut(observed, key, value);
	}

	@Override
	public void elementRemoved(MapChangeObserved<KK, VV> observed, KK key)
	{
		for( MapChangeListener ml: _list )
			ml.elementRemoved(observed, key);
	}

	@Override
	public void cleared(MapChangeObserved<KK, VV> observed)
	{
		for( MapChangeListener ml: _list )
			ml.cleared(observed);
	}

}
