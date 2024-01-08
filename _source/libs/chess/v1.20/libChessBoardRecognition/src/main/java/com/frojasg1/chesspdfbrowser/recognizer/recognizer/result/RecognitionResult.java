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
package com.frojasg1.chesspdfbrowser.recognizer.recognizer.result;

import com.frojasg1.chesspdfbrowser.engine.position.ChessGamePositionBase;
import com.frojasg1.chesspdfbrowser.recognizer.chessboard.result.ChessBoardGridResult;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RecognitionResult
{
	protected ChessGamePositionBase _detectedPosition = null;
	protected ChessBoardGridResult _grid = null;

	public RecognitionResult( ChessGamePositionBase detectedPosition,
								ChessBoardGridResult grid )
	{
		_detectedPosition = detectedPosition;
		_grid = grid;
	}

	public void setDetectedPosition(ChessGamePositionBase cgp) {
		_detectedPosition = cgp;
	}

	public ChessGamePositionBase getDetectedPosition() {
		return _detectedPosition;
	}

	public ChessBoardGridResult getGrid() {
		return _grid;
	}
}
