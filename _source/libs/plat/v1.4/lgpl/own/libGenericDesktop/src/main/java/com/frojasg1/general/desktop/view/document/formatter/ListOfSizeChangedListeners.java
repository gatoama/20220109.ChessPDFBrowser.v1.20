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
package com.frojasg1.general.desktop.view.document.formatter;

import com.frojasg1.general.listeners.GenericObserved;
import com.frojasg1.general.listeners.ListOfListenersImp;
import java.awt.Dimension;
import java.util.Iterator;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ListOfSizeChangedListeners extends ListOfListenersImp< SizeChangedListener >
														implements SizeChangedListener
{
	public ListOfSizeChangedListeners()
	{
		super();
	}

	@Override
	public void sizeChanged( GenericObserved observed, Dimension newSize )
	{
		Iterator<SizeChangedListener> it = _list.iterator();
		while( it.hasNext() )
		{
			it.next().sizeChanged(observed, newSize);
		}
	}
}
