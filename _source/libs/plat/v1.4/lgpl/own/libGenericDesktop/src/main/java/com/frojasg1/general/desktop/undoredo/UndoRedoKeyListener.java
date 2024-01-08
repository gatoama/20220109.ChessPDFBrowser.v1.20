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
package com.frojasg1.general.desktop.undoredo;

import com.frojasg1.general.desktop.keyboard.listener.imp.GenericKeyListenerImp;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class UndoRedoKeyListener extends GenericKeyListenerImp
{
	public static int KEY_ID_FOR_UNDO = 1;
	public static int KEY_ID_FOR_REDO = 2;
	public static int KEY_ID_FOR_OPEN_SEARCH = 3;
	public static int KEY_ID_FOR_OPEN_REPLACE = 4;
	public static int KEY_ID_FOR_SEARCH_FORWARD_AGAIN = 5;
	public static int KEY_ID_FOR_SEARCH_BACKWARDS_AGAIN = 6;

	public UndoRedoKeyListener()
	{}
}
