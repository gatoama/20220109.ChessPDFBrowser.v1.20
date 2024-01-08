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
package com.frojasg1.chesspdfbrowser.analysis.game.listener;

import com.frojasg1.chesspdfbrowser.analysis.SubvariantAnalysisResult;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.SuccessResult;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface AnalyzeGameListener
{
	public void newPositionAnalyzed( MoveTreeNode analyzedNode, int current,
									int total, SubvariantAnalysisResult sar,
									ChessGame resultGame,
									SuccessResult successResult );
}
