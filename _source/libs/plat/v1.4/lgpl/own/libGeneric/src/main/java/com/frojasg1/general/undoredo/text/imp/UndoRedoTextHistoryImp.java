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

import com.frojasg1.general.undoredo.imp.UndoRedoHistoryImp;
import com.frojasg1.general.undoredo.text.TextUndoRedoElementInterface;
import com.frojasg1.general.undoredo.text.UndoRedoTextHistoryInterface;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class UndoRedoTextHistoryImp< URE extends TextUndoRedoElementInterface >
		extends UndoRedoHistoryImp< URE >
	implements	UndoRedoTextHistoryInterface<URE>
{
	public UndoRedoTextHistoryImp( )
	{
		super( );
	}

	@Override
	protected boolean hasToAddToUndoList( URE element )
	{
		boolean result = !hasElementsToRedo();

		if( ! result )
		{
			result = ! ( element == _listToRedo.getLast() );
		}

		return( result );
	}
/*
	@Override
	public void undo( URE elem )
	{
		VI vi = getUndoRedoInt().getView();
		vi.replaceText(elem.getStartOfElement(), elem.getNewStringElement(), elem.getPreviousStringElement());
	}

	@Override
	public void redo( URE elem )
	{
		VI vi = getUndoRedoInt().getView();
		vi.replaceText(elem.getStartOfElement(), elem.getPreviousStringElement(), elem.getNewStringElement());
	}
*/
}
