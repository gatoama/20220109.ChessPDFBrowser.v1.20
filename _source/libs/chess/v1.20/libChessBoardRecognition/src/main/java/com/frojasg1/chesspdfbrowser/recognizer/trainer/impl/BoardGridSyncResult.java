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

import com.frojasg1.chesspdfbrowser.recognizer.chessboard.result.ChessBoardGridResult;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.ComponentsStats;
import com.frojasg1.general.structures.Pair;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class BoardGridSyncResult
{
	protected ChessBoardGridResult _grid = null;
	protected Pair<ComponentsStats, ComponentsStats> _emptySquareComponentStats = null;

	public BoardGridSyncResult()
	{
		
	}

	public ChessBoardGridResult getGrid() {
		return _grid;
	}

	public void setGrid(ChessBoardGridResult _grid) {
		this._grid = _grid;
	}

	public Pair<ComponentsStats, ComponentsStats> getEmptySquareComponentStats() {
		return _emptySquareComponentStats;
	}

	public void setEmptySquareComponentStats(Pair<ComponentsStats, ComponentsStats> _emptySquareComponentStats) {
		this._emptySquareComponentStats = _emptySquareComponentStats;
	}

}
