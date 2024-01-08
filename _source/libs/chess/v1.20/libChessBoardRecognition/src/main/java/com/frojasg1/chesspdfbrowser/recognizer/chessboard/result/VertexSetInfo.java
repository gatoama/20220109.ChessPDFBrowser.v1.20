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
package com.frojasg1.chesspdfbrowser.recognizer.chessboard.result;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class VertexSetInfo
{
	protected int _minX = -1;
	protected int _maxX = -1;
	protected int _minY = -1;
	protected int _maxY = -1;
	protected int _edgeLength = -1;
	protected int _numVertex = -1;
	protected int _xElems = -1;
	protected int _yElems = -1;

	public VertexSetInfo( int minX, int maxX, int minY, int maxY, int edgeLength,
							int numVertex, int xElems, int yElems )
	{
		_minX = minX;
		_maxX = maxX;
		_minY = minY;
		_maxY = maxY;
		_edgeLength = edgeLength;
		_numVertex = numVertex;
		_xElems = xElems;
		_yElems = yElems;
	}

	public int getMinX() {
		return _minX;
	}

	public int getMaxX() {
		return _maxX;
	}

	public int getMinY() {
		return _minY;
	}

	public int getMaxY() {
		return _maxY;
	}

	public int getEdgeLength() {
		return _edgeLength;
	}

	public int getNumVertex() {
		return _numVertex;
	}

	public int getxElems() {
		return _xElems;
	}

	public int getyElems() {
		return _yElems;
	}

}
