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
package com.frojasg1.chesspdfbrowser.analysis.game;

import com.frojasg1.chesspdfbrowser.analysis.AnalysisControllerBase;
import com.frojasg1.chesspdfbrowser.analysis.EngineAnalysisProcessData;
import com.frojasg1.chesspdfbrowser.analysis.SubvariantAnalysisResult;
import com.frojasg1.chesspdfbrowser.analysis.engine.EngineWrapperInstanceWrapper;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.engineresult.ChessEngineResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.SuccessResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.ChessEngineConfigurationPersistency;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.go.ChessEngineGoAttributes;
import com.frojasg1.chesspdfbrowser.enginewrapper.variant.EngineMoveVariant;
import com.frojasg1.general.CollectionFunctions;
import com.frojasg1.general.timers.TimerFunctions;
import java.util.Objects;
import java.util.TimerTask;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class GameAnalysisController extends AnalysisControllerBase
{
	protected EngineWrapperInstanceWrapper _engineInstance = null;

	protected EngineMoveVariant _bestVariant = null;

//	protected Timer _timer = null;
//	protected TimerTask _task = null;

	protected EngineAnalysisProcessData _engineData = null;

	protected Consumer<SubvariantAnalysisResult> _callback;

	protected Integer _lastGoCorrelationId = null;

	public GameAnalysisController( ChessEngineConfigurationPersistency chessEngineConfPersistency,
						ChessViewConfiguration chessViewConfiguration,
						Consumer<SubvariantAnalysisResult> callback )
	{
		super( chessEngineConfPersistency, chessViewConfiguration );

		_callback = callback;
//		_timer = new Timer();
	}

	@Override
	protected Consumer<SuccessResult> getProcessAfterInitResultFunction()
	{
		return( null );
	}

	@Override
	protected BiConsumer<Integer,
				FullEngineActionResult<ChessEngineGoAttributes,ChessEngineResult>> getProcessGoResultFunction()
	{
		return( this::processGoResult );
	}

	public void startAnalysis(EngineAnalysisProcessData engineData)
	{
		if( hasChanged( _engineInstance, engineData ) )
		{
			_engineData = engineData;

			closeAnalysisProcess(_engineInstance);

			_engineInstance = createEngineInstance( 0, engineData );

			_bestVariant = null;

//			if( startEngine( 0, _engineInstance, engineData ) )
//				startTimer( getTimerTimeMs() + 500 );
			startEngine( 0, _engineInstance, engineData );
		}
	}

	protected String getEngineName()
	{
		return( getEngineName( _engineInstance ) );
	}

	public void processGoResult( Integer id, FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult> result )
	{
		EngineWrapperInstanceWrapper engineWrapper = _engineInstance;

		if( ( engineWrapper != null ) && ( result != null ) &&
			result.getSuccessResult().isAsuccess() &&
			result.getSuccessResult().isLast() ) // we are going to process only best last calculated variant
		{
			ChessEngineResult cer = result.getActionResult();
//			Integer currentCorrelationId = getCorrelationId( result );
			if( ( cer != null )
//				&& !Objects.equals( currentCorrelationId, _lastGoCorrelationId )
				)
			{
//				_lastGoCorrelationId = currentCorrelationId;
				_bestVariant = CollectionFunctions.instance().getFirstOf(cer.getBestVariants());
//				System.out.println( "informOfBestLine --> (correlationId: " + currentCorrelationId + ")" );
				informOfBestLine();
			}
		}
	}

	protected Integer getCorrelationId( ChessEngineGoAttributes goAttribs )
	{
		return( ( goAttribs != null ) ? goAttribs.getCorrelationId() : null );
	}

	protected ChessEngineGoAttributes getChessEngineGoAttributes( FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult> result )
	{
		return( ( result != null ) ? result.getRequest().getArgs() : null );
	}

	protected Integer getCorrelationId( FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult> result )
	{
		return( getCorrelationId( getChessEngineGoAttributes(result) ) );
	}

	public void startThinking()
	{
		startThinking( _engineInstance );
	}

	public void stop() {
		stop( _engineInstance );

//		cancelTimerTask();
	}

	public void releaseResources()
	{
		closeEngine( _engineInstance );
		_engineInstance = null;
		_bestVariant = null;
	}

	@Override
	protected void updatePositionEverywhere(String fenString) {
		updatePosition( _engineInstance, fenString );
	}

	@Override
	public void setNewPosition( MoveTreeNode mtn )
	{
		super.setNewPosition( mtn );
/*
		if( _engineData != null )
		{
			cancelTimerTask();
			startTimer( getTimerTimeMs() );
		}
*/
	}
/*
	protected void cancelTimerTask()
	{
		if( _task != null )
		{
			_task.cancel();
			_task = null;
		}
	}

	protected TimerTask createNewTimerTaskToInformOfBestLine()
	{
		return( TimerFunctions.instance().createTimerTask( this::informOfBestLine ) );
	}
*/

	protected void informOfBestLine()
	{
		SubvariantAnalysisResult sar = createSubvariantAnalysisResult( _bestVariant );

		_callback.accept( sar );
	}

	protected long getTimerTimeMs()
	{
		long result = -1;
		if( _engineData != null )
			result = _engineData.getNumberOfMilliSecondsToSpendInAnalysis();

		return( result );
	}
/*
	protected void startTimer( long ms )
	{
		_task = createNewTimerTaskToInformOfBestLine();
		startTimer(_task, ms);
	}

	protected void startTimer( TimerTask task, long ms )
	{
		_timer.schedule(task, ms);
	}
*/
}
