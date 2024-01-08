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
package com.frojasg1.chesspdfbrowser.analysis;

import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class SubvariantAnalysisResult
{
	protected double _score;
	protected ChessGame _subvariantChessGame;

	public SubvariantAnalysisResult( double score, ChessGame subvariantChessGame )
	{
		_score = score;
		_subvariantChessGame = subvariantChessGame;
	}

	public double getScore() {
		return _score;
	}

	public ChessGame getSubvariantChessGame() {
		return _subvariantChessGame;
	}

	public MoveTreeNode getBestLine()
	{
		MoveTreeNode result = null;

		ChessGame cg = getSubvariantChessGame();
		if( cg != null )
		{
			result = cg.getMoveTreeGame().getFirstChild();
		}

		return( result );
	}
}
