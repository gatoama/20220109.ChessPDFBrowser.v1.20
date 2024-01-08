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
package com.frojasg1.general.undoredo;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface UndoRedoHistoryInterface< URE extends UndoRedoElementInterface >
{
//	void setUndoRedoInt( UR uri );
//	UR getUndoRedoInt();

	void setNewUndoElement( URE urei );
	void setNewUndoElement( Collection<URE> urei );
	void setNewRedoElement( URE urei );
	void setNewRedoElement( Collection<URE> urei );

	Collection<URE> removeElementToUndo();
	Collection<URE> removeElementToRedo();

//	void replaceElementToUndo( URE urei );

	boolean hasElementsToUndo();
	boolean hasElementsToRedo();

	void clearRedoList();
	void clearUndoList();
}
