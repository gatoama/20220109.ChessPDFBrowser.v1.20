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

import com.frojasg1.chesspdfbrowser.analysis.SubvariantAnalysisResult;
import com.frojasg1.chesspdfbrowser.analysis.game.contexts.AnalyzeGameTaskContext;
import com.frojasg1.chesspdfbrowser.analysis.game.contexts.AnalyzeGameTaskInputContext;
import com.frojasg1.chesspdfbrowser.analysis.game.listener.AnalyzeGameListener;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGameHeaderInfo;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.SuccessResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.ChessEngineConfigurationPersistency;
import com.frojasg1.chesspdfbrowser.enginewrapper.utils.EngineWrapperUtils;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.concurrency.ProducerConsumerBlockingQueue;
import com.frojasg1.general.progress.OperationCancellation;
import com.frojasg1.general.timers.TimerFunctions;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AnalyzeGameTask implements Runnable
{
	public static final String ERROR_PROCESSING_SUBVARIANT = "ERROR_PROCESSING_SUBVARIANT";
	public static final String TIMEOUT_WAITING_FOR_ENGINE_ANSWER = "TIMEOUT_WAITING_FOR_ENGINE_ANSWER";
	public static final String EMPTY_ANSWER_FROM_ENGINE = "EMPTY_ANSWER_FROM_ENGINE";

	protected ReentrantLock _lock = new ReentrantLock( true );
	protected Condition _waitingForTaskToStart = _lock.newCondition();

	protected EngineWrapperUtils _engineUtils = new EngineWrapperUtils();

	protected AnalyzeGameTaskContext _context = new AnalyzeGameTaskContext();

	protected GameAnalysisController _gameAnalysisController = null;

	protected boolean _isEnd = false;
	protected boolean _isError = false;

	protected ProducerConsumerBlockingQueue<SubvariantAnalysisResult> _blockingQueue;

	protected Timer _timer = new Timer();
	protected TimerTask _timerTask;

	public AnalyzeGameTask( OperationCancellation operationCancellation,
							ChessEngineConfigurationPersistency chessEngineConfPersistency,
							ChessViewConfiguration chessViewConfiguration )
	{
		_context.setInitialOperationCancellation( operationCancellation );

		_blockingQueue = createProducerConsumerBlockingQueue();

		_gameAnalysisController = createGameAnalysisController( chessEngineConfPersistency,
							chessViewConfiguration );
	}

	protected <CC> ProducerConsumerBlockingQueue<CC> createProducerConsumerBlockingQueue()
	{
		return( new ProducerConsumerBlockingQueue<>( () -> !this.canContinueLight() ) );
	}

	protected GameAnalysisController createGameAnalysisController(
							ChessEngineConfigurationPersistency chessEngineConfPersistency,
							ChessViewConfiguration chessViewConfiguration )
	{
		GameAnalysisController result = new GameAnalysisController(chessEngineConfPersistency,
							chessViewConfiguration, this::bestLineDetected);

		return( result );
	}

	@Override
	public void run()
	{
		waitForStart();

		try
		{
			if( canContinue() )
			{
				updatePositionInGameAnalyzer();
				_gameAnalysisController.startAnalysis( _context.getInputContext().getEngineAnalysisProcessData() );

				while( canContinue() )
				{
					startTimer();
					SubvariantAnalysisResult sar = _blockingQueue.get();
					stopTimer();

					processBestLineDetected( sar );

					evaluateBestLine();
				}
			}
		}
		finally
		{
			_timer.cancel();
			_gameAnalysisController.releaseResources();
		}
	}

	protected TimerTask createTimerTask()
	{
		return( TimerFunctions.instance().createTimerTask( () -> doError( TIMEOUT_WAITING_FOR_ENGINE_ANSWER, null ) ) );
	}

	protected long getMillisecondsForTimeout()
	{
		return( _context.getInputContext().getEngineAnalysisProcessData().getNumberOfMilliSecondsToSpendInAnalysis() * 2 + 2000 );
	}

	protected void startTimer()
	{
		_timerTask = createTimerTask();
		_timer.schedule( _timerTask, getMillisecondsForTimeout() );
	}

	protected void stopTimer()
	{
		_timerTask.cancel();
	}

	protected void waitForStart()
	{
		try
		{
			_lock.lock();

			while( canContinue() && ( _context.getGameToAnalyze() == null ) )
			{
				ExecutionFunctions.instance().safeMethodExecution(
					() -> _waitingForTaskToStart.await( 500, TimeUnit.MILLISECONDS ) );
			}
		}
		finally
		{
			_lock.unlock();
		}
	}

	protected boolean isEnd()
	{
		return( _isEnd );
	}

	protected void setIsEnd( boolean value )
	{
		_isEnd = value;
	}

	protected boolean isError()
	{
		return( _isError );
	}

	protected void setIsError( boolean value )
	{
		_isError = value;
	}

	protected boolean canContinueLight()
	{
		return( !isError() && !isEnd() && !Thread.currentThread().isInterrupted() );
	}

	protected boolean wasCancelled()
	{
		return( _context.getOperationCancellation().getHasToCancel() );
	}

	protected boolean canContinue()
	{
		return( canContinueLight() && !wasCancelled() );
	}

	public void start( AnalyzeGameTaskInputContext inputContext,
						String stringForTaggingAnalyzedGames )
	{
		try
		{
			_lock.lock();

			if( _context.getGameToAnalyze() != null )
				throw( new RuntimeException( "Task already started" ) );

			_context.setInputContext(inputContext);
			_context.tagAnalysisGame(stringForTaggingAnalyzedGames);

			if( _lock.hasWaiters( _waitingForTaskToStart ) )
				_waitingForTaskToStart.signal();
		}
		finally
		{
			_lock.unlock();
		}
	}

	protected void evaluateBestLine()
	{
		updatePositionInGameAnalyzer();
	}

	protected void updatePositionInGameAnalyzer()
	{
		if( canContinue() )
		{
			_gameAnalysisController.setNewPosition( _context.getCurrentMoveToEvaluate() );
		}
	}

	protected void bestLineDetected( SubvariantAnalysisResult sar )
	{
		_blockingQueue.add( sar );
	}

	protected void processBestLineDetected( SubvariantAnalysisResult sar )
	{
		try
		{
			if( ( sar != null ) &&
				( ( _context.getCurrentProgress() > 0 ) ||
					( sar.getSubvariantChessGame() != null ) &&
					( sar.getSubvariantChessGame().getMoveTreeGame().getNumberOfChildren() > 0 )
				)
			  )
			{
				int currentProgress = _context.incrementCurrentProgress( 1 );

				MoveTreeNode currentNode = _context.getCurrentMoveToEvaluate();
				MoveTreeNode nextNodeNoEvaluate = _context.getNextNodeToEvaluate();

				int total = _context.getTotal();
				if( ( nextNodeNoEvaluate != null ) && ( currentProgress >= total ) )
				{
					total = currentProgress + 1;
				}

				if( nextNodeNoEvaluate == null )
					setIsEnd( true );

				_gameAnalysisController.addSubvariantAnalysisToGame_internal(
//														_context.getCurrentMoveToEvaluate(),
														currentNode,
														_context.getEngineName(),
														sar.getScore(),
														sar.getBestLine() );

				boolean isLast = !canContinue() || ( nextNodeNoEvaluate == null );
				sendCallbackMessage(currentNode, currentProgress, total, sar,
									_context.getResult(),
									_engineUtils.createSuccessResult(true, isLast));

	//			evaluateBestLine();
			}
			else if( canContinue() && ( _context.getCurrentProgress() == 0 ) )
			{
				doError( EMPTY_ANSWER_FROM_ENGINE, null );
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			doError( ERROR_PROCESSING_SUBVARIANT, ex );
		}
	}

	protected void doError( String errorMessage, Exception ex )
	{
		setIsError( true );

		sendErrorCallback( errorMessage, ex );
	}

	protected void sendErrorCallback( String errorMessage, Exception ex )
	{
		sendGenCallback(_engineUtils.createErrorResult(ex, errorMessage));
	}

	protected void sendGenCallback( SuccessResult successResult )
	{
		sendCallbackMessage(_context.getCurrentMoveToEvaluate(),
							_context.getCurrentProgress(),
							_context.getTotal(), null,
							_context.getResult(),
							successResult);
		
	}

	protected AnalyzeGameListener getListener()
	{
		return( _context.getListener() );
	}

	protected void sendCallbackMessage( MoveTreeNode analyzedNode, int current,
									int total, SubvariantAnalysisResult sar,
									ChessGame resultGame,
									SuccessResult successResult )
	{
		getListener().newPositionAnalyzed(analyzedNode, current, total, sar,
											resultGame,
											successResult);
	
	}
}
