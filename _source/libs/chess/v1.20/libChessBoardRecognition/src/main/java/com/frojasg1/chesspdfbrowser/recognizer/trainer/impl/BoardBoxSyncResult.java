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

import com.frojasg1.chesspdfbrowser.recognizer.store.set.ComponentsStats;
import com.frojasg1.general.desktop.image.pixel.impl.PixelComponents;
import com.frojasg1.general.structures.Pair;
import java.awt.Dimension;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class BoardBoxSyncResult
{
	protected int _deltaX = 0;
	protected int _deltaY = 0;
	protected Dimension _effectiveBoardBoxDimension = null;

	protected PixelComponents[][] _imagePixels = null;

	protected Pair<ComponentsStats, ComponentsStats> _emptySquareComponentStats = null;

	public BoardBoxSyncResult( int deltaX, int deltaY, Dimension effectiveBoardBoxDimension,
								PixelComponents[][] imagePixels,
								Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats )
	{
		_deltaX = deltaX;
		_deltaY = deltaY;
		_effectiveBoardBoxDimension = effectiveBoardBoxDimension;
		_imagePixels = imagePixels;
		_emptySquareComponentStats = emptySquareComponentStats;
	}

	public int getDeltaX() {
		return _deltaX;
	}

	public int getDeltaY() {
		return _deltaY;
	}

	public Dimension getEffectiveBoxEdgeLength() {
		return _effectiveBoardBoxDimension;
	}

	public PixelComponents[][] getImagePixels()
	{
		return( _imagePixels );
	}

	public Pair<ComponentsStats, ComponentsStats> getComponentsStatsForEmptySquares()
	{
		return( _emptySquareComponentStats );
	}

	public void setComponentsStatsForEmptySquares( Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats )
	{
		_emptySquareComponentStats = emptySquareComponentStats;
	}
}
