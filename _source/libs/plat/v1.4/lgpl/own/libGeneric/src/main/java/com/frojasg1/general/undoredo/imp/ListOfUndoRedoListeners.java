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
package com.frojasg1.general.undoredo.imp;

import com.frojasg1.general.listeners.ListOfListenersImp;
import com.frojasg1.general.undoredo.UndoRedoListener;
import java.util.Iterator;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ListOfUndoRedoListeners<URL extends UndoRedoListener> extends ListOfListenersImp<URL> implements UndoRedoListener
{
	public ListOfUndoRedoListeners()
	{
		super();
	}

	@Override
	public synchronized void undoListHasChanged()
	{
		Iterator<URL> it = _list.iterator();
		while( it.hasNext() )
		{
			it.next().undoListHasChanged();
		}
	}

	@Override
	public synchronized void redoListHasChanged()
	{
		Iterator<URL> it = _list.iterator();
		while( it.hasNext() )
		{
			it.next().redoListHasChanged();
		}
	}

	@Override
	public synchronized void originalElementHasChanged()
	{
		Iterator<URL> it = _list.iterator();
		while( it.hasNext() )
		{
			it.next().originalElementHasChanged();
		}
	}
}
