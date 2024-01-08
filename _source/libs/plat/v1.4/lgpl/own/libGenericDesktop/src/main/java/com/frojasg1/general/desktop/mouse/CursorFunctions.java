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
package com.frojasg1.general.desktop.mouse;

import java.awt.Cursor;

/**
 *
 * @author Usuario
 */
public class CursorFunctions
{
	public static final Cursor _defaultCursor = new Cursor( Cursor.DEFAULT_CURSOR );
	public static final Cursor _handCursor = new Cursor( Cursor.HAND_CURSOR );
	public static final Cursor _waitCursor = new Cursor( Cursor.WAIT_CURSOR );
	public static final Cursor _textCursor = new Cursor( Cursor.TEXT_CURSOR );
	public static final Cursor _crossHairCursor = new Cursor( Cursor.CROSSHAIR_CURSOR );
	public static final Cursor _eResizeCursor = new Cursor( Cursor.E_RESIZE_CURSOR );
	public static final Cursor _seResizeCursor = new Cursor( Cursor.SE_RESIZE_CURSOR );
	public static final Cursor _sResizeCursor = new Cursor( Cursor.S_RESIZE_CURSOR );
	public static final Cursor _swResizeCursor = new Cursor( Cursor.SW_RESIZE_CURSOR );
	public static final Cursor _wResizeCursor = new Cursor( Cursor.W_RESIZE_CURSOR );
	public static final Cursor _nwResizeCursor = new Cursor( Cursor.NW_RESIZE_CURSOR );
	public static final Cursor _nResizeCursor = new Cursor( Cursor.N_RESIZE_CURSOR );
	public static final Cursor _neResizeCursor = new Cursor( Cursor.NE_RESIZE_CURSOR );

	protected static CursorFunctions _instance = null;

	protected Cursor _currentCursor = null;

	public static void changeInstance( CursorFunctions newInstance )
	{
		_instance = newInstance;
	}

	public static CursorFunctions instance()
	{
		if( _instance == null )
			_instance = new CursorFunctions();

		return( _instance );
	}

	public void storeCurrentCursor( Cursor currentCursor )
	{
		_currentCursor = currentCursor;
	}

	public boolean isResizeDragging()
	{
		return( isResizeDragging(_currentCursor) );
	}

	public boolean isResizeDragging( Cursor cursor )
	{
		boolean result = false;
		
		if( cursor != null )
		{
			int type = cursor.getType();

			result = ( type >= Cursor.SW_RESIZE_CURSOR ) &&
					( type >= Cursor.E_RESIZE_CURSOR );
		}

		return( result );
	}

}
