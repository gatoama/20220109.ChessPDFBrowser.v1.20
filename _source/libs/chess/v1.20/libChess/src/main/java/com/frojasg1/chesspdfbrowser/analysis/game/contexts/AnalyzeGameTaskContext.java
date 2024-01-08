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

import com.frojasg1.chesspdfbrowser.analysis.game.listener.AnalyzeGameListener;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGameHeaderInfo;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNodeUtils;
import com.frojasg1.general.progress.OperationCancellation;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AnalyzeGameTaskContext
{
	protected ChessGame _result;

	protected MoveTreeNode _currentMoveToEvaluate = null;

	protected int _total = 0;
	protected int _currentProgress = 0;

	protected AnalyzeGameTaskInputContext _inputContext = null;

	protected OperationCancellation _initialOperationCancellation = null;

	public int incrementCurrentProgress(int units)
	{
		_currentProgress += units;
		return( _currentProgress );
	}

	public AnalyzeGameTaskInputContext getInputContext() {
		return _inputContext;
	}

	public void setInputContext(AnalyzeGameTaskInputContext _inputContext) {
		this._inputContext = _inputContext;

		_result = copyMainLineGame( getGameToAnalyze() );

		_total = MoveTreeNodeUtils.instance().getNumberOfPliesOfMainLine(_result);

		_currentProgress = 0;

		_currentMoveToEvaluate = _result.getMoveTreeGame().getFirstChild();
	}

	protected ChessGame copyMainLineGame( ChessGame inputGame )
	{
		return( MoveTreeNodeUtils.instance().copyMainLineGame( inputGame ) );
	}

	public ChessGame getGameToAnalyze() {
		return( ( getInputContext() == null ) ?
				null :
				getInputContext().getGameToAnalyze()
			);
	}

	public ChessGame getResult() {
		return _result;
	}

	public void setResult(ChessGame _result) {
		this._result = _result;
	}

	public AnalyzeGameListener getListener() {
		return getInputContext().getListener();
	}

	public String getEngineName() {
		return getInputContext().getEngineName();
	}

	public void setInitialOperationCancellation(OperationCancellation _initialOperationCancellation) {
		this._initialOperationCancellation = _initialOperationCancellation;
	}

	public OperationCancellation getOperationCancellation() {
		return( ( getInputContext() == null ) ?
				_initialOperationCancellation :
				getInputContext().getOperationCancellation()
			);
	}

	public MoveTreeNode getCurrentMoveToEvaluate() {
		return _currentMoveToEvaluate;
	}

	public void setCurrentMoveToEvaluate(MoveTreeNode _currentMoveToEvaluate) {
		this._currentMoveToEvaluate = _currentMoveToEvaluate;
	}

	public int getTotal() {
		return _total;
	}

	public void setTotal(int _total) {
		this._total = _total;
	}

	public int getCurrentProgress() {
		return _currentProgress;
	}

	public void setCurrentProgress(int _current) {
		this._currentProgress = _current;
	}

	public MoveTreeNode getNextNodeToEvaluate()
	{
		_currentMoveToEvaluate = _currentMoveToEvaluate.getFirstChild();

		return( _currentMoveToEvaluate );
	}

	public void tagAnalysisGame( String stringForTaggingAnalyzedGames )
	{
		tagAnalysisGame( _result, stringForTaggingAnalyzedGames );
	}

	protected void tagAnalysisGame( ChessGame analyzedGame,
									String stringToTagAnalysisGame )
	{
		if( ( analyzedGame != null ) && ( stringToTagAnalysisGame != null ) )
		{
			ChessGameHeaderInfo header = analyzedGame.getChessGameHeaderInfo();
			String tagValue = stringToTagAnalysisGame;
			header.put( ChessGameHeaderInfo.ANNOTATOR_TAG, tagValue);
			String event = header.get( ChessGameHeaderInfo.EVENT_TAG );
			if( event == null )
				event = "";
			if( ! event.contains( tagValue ) )
				header.put( ChessGameHeaderInfo.EVENT_TAG, event + " - " + tagValue );
		}
	}
}
