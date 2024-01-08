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
package com.frojasg1.general.undoredo.text.imp;

import com.frojasg1.general.undoredo.imp.ListOfUndoRedoListeners;
import com.frojasg1.general.undoredo.text.TextUndoRedoListener;
import java.util.Iterator;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ListOfTextUndoRedoListeners extends ListOfUndoRedoListeners<TextUndoRedoListener> implements TextUndoRedoListener
{
	public ListOfTextUndoRedoListeners()
	{
		super();
	}

	@Override
	public void caretHasChanged()
	{
		Iterator<TextUndoRedoListener> it = _list.iterator();
		while( it.hasNext() )
		{
			it.next().caretHasChanged();
		}
	}
}
