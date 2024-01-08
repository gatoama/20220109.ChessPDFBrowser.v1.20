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
package com.frojasg1.chesspdfbrowser.recognizer.trainer.impl;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class BoardBoxResult
{
	protected int _col;
	protected int _row;
	protected Character _piece;
	
	public BoardBoxResult( int col, int row, Character piece )
	{
		_col = col;
		_row = row;
		_piece = piece;
	}

	public int getCol() {
		return _col;
	}

	public int getRow() {
		return _row;
	}

	public Character getPiece() {
		return _piece;
	}

	public boolean isWhiteBoardBox()
	{
		return( ( _col + _row ) % 2 == 1 );
	}
}
