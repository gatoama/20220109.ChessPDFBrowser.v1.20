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
package com.frojasg1.chesspdfbrowser.analysis.game.contexts;

import com.frojasg1.chesspdfbrowser.analysis.EngineAnalysisProcessData;
import com.frojasg1.chesspdfbrowser.analysis.game.listener.AnalyzeGameListener;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.general.progress.OperationCancellation;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AnalyzeGameTaskInputContext
{
	protected ChessGame _gameToAnalyze;
	protected AnalyzeGameListener _listener;
	protected OperationCancellation _operationCancellation = null;

	protected EngineAnalysisProcessData _engineAnalysisProcessData = null;

	public String getEngineName() {
		return( getEngineAnalysisProcessData().getEngineName() );
	}

	public EngineAnalysisProcessData getEngineAnalysisProcessData() {
		return _engineAnalysisProcessData;
	}

	public void setEngineAnalysisProcessData(EngineAnalysisProcessData _engineAnalysisProcessData) {
		this._engineAnalysisProcessData = _engineAnalysisProcessData;
	}

	public ChessGame getGameToAnalyze() {
		return _gameToAnalyze;
	}

	public void setGameToAnalyze(ChessGame _gameToAnalyze) {
		this._gameToAnalyze = _gameToAnalyze;
	}

	public AnalyzeGameListener getListener() {
		return _listener;
	}

	public void setListener(AnalyzeGameListener _listener) {
		this._listener = _listener;
	}

	public OperationCancellation getOperationCancellation() {
		return _operationCancellation;
	}

	public void setOperationCancellation(OperationCancellation _operationCancellation) {
		this._operationCancellation = _operationCancellation;
	}
}
