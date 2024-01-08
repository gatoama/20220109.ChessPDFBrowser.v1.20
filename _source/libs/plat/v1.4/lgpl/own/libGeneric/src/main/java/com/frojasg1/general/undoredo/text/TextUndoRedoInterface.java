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
package com.frojasg1.general.undoredo.text;

import com.frojasg1.general.undoredo.UndoRedoInterface;
import com.frojasg1.general.undoredo.UndoRedoReplacementInterface;
import com.frojasg1.general.view.ViewTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface TextUndoRedoInterface< URE extends TextUndoRedoElementInterface,
										URR extends UndoRedoReplacementInterface,
										VI extends ViewTextComponent > extends UndoRedoInterface< URE, URR >
{
	void registerView( VI view );
	VI getView();

	void setUndoRedoTextHistoryObject( UndoRedoTextHistoryInterface< URE > urthi );

	void registerListener( TextUndoRedoListener rel );
	void unregisterListener( TextUndoRedoListener rel );
}
