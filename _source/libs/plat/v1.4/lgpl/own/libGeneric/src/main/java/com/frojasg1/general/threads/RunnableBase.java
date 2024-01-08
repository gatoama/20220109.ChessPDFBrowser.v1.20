/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.threads;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.NullFunctions;
import com.frojasg1.general.executor.ExecutorInterface;
import static java.lang.Thread.currentThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class RunnableBase implements ExecutorInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(RunnableBase.class);

	protected static final int PAUSE_CONDITION_INDEX = 0;

	protected boolean _hasToStop = false;
	protected boolean _hasToPause = false;

	private LockAndConditionBase _lockAndConditions;

//	protected ReentrantLock _pauseLock = new ReentrantLock(true);
//	protected Condition _waitingToContinue = _pauseLock.newCondition();

	protected Consumer<Exception> _onExceptionFunction;

	protected RunnableBase()
	{
		this(0);
	}

	protected RunnableBase( RunnableBase that )
	{
		_lockAndConditions = new LockAndConditionBase(that._lockAndConditions);
		_lockAndConditions.init();
	}

	protected RunnableBase( int numExtraConditions )
	{
		_lockAndConditions = new LockAndConditionBase(numExtraConditions + 1);
		_lockAndConditions.init();
	}

	protected LockAndConditionBase getLockAndConditions() {
		return _lockAndConditions;
	}

	@Override
	public void run()
	{
		try
		{
			if( doInit() )
			{

				while( !getHasToStop() && pauseIfNecessary() )
					doRoutine();
			}
		}
		catch( Exception ex )
		{
			LOGGER.error( "Error processing RunnableBase", ex );
			invokeOnExceptionFunction( ex );
		}
		finally
		{
			doEnd();
		}
	}

	public void setOnExceptionFunction(Consumer<Exception> _onExceptionFunction) {
		this._onExceptionFunction = _onExceptionFunction;
	}

	protected void invokeOnExceptionFunction( Exception ex )
	{
		if( _onExceptionFunction != null )
			_onExceptionFunction.accept(ex);
	}

	protected abstract boolean doInit();
	protected abstract void doRoutine();
	public abstract void doEnd();

	protected boolean getHasToStop()
	{
		return( _hasToStop );
	}

	protected void taskToSignalGen( Runnable runnable,
									int conditionIndex)
	{
		getLockAndConditions().taskToSignalGen(runnable, conditionIndex);
	}

	protected void ifHasWaitersSignal(int conditionIndex)
	{
		getLockAndConditions().ifHasWaitersSignal(conditionIndex);
	}

	protected void taskToSignalWaitingToContinue( Runnable runnable )
	{
		taskToSignalGen( runnable, PAUSE_CONDITION_INDEX );
	}

	public void setHasToStop( boolean value )
	{
		taskToSignalWaitingToContinue( () -> setHasToStopInternal(value) );
	}

	@Override
	public void hasToStop()
	{
		setHasToStop(true);
	}

	protected void setHasToStopInternal( boolean value )
	{
		simpleSetHasToStop( value );
		hasToStopAssociatedTasks();
	}

	protected abstract void hasToStopAssociatedTasks();

	protected void simpleSetHasToStop( boolean value )
	{
		_hasToStop = value;
	}

	public boolean getHasToPause()
	{
		return( _hasToPause );
	}

	public void setHasToPause( boolean value )
	{
		taskToSignalWaitingToContinue( () -> simpleSetHasToPause(value) );
	}

	protected void simpleSetHasToPause( boolean value )
	{
		_hasToPause = value;
	}


	protected boolean pauseIfNecessary()
	{
		return( lockFunction( this::pauseIfNecessaryInternal ) );
	}

	

	protected boolean pauseIfNecessaryInternal()
	{
		boolean hasPaused = false;
		boolean hasToStop = false;
		while( getHasToPause() && !( hasToStop = getHasToStop() ) )
		{
			doBeforePause();
			hasPaused = true;
			try
			{
				getCondition(PAUSE_CONDITION_INDEX).await( 1000, TimeUnit.MILLISECONDS );
			}
			catch( InterruptedException ex )
			{
				Thread.currentThread().interrupt();
				return( false );
			}
		}

		if( hasPaused )
			doAfterPause();

		return( !hasToStop );
	}

	protected Condition getCondition( int conditionIndex )
	{
		return( getLockAndConditions().getCondition( conditionIndex ) );
	}

	protected boolean sleep( int timeToSleep )
	{
		return( awaitInterrupted( PAUSE_CONDITION_INDEX, timeToSleep ) );
	}

	protected abstract void doBeforePause();

	protected abstract void doAfterPause();

	protected <CC> CC lockFunction( Supplier<CC> function )
	{
		return( getLockAndConditions().lockFunction(function) );
	}

	protected void lockProcedure( Runnable procedure )
	{
		getLockAndConditions().lockProcedure(procedure);
	}

	protected boolean awaitInterrupted( int conditionIndex, Integer timeoutMs )
	{
		return( getLockAndConditions().awaitInterrupted(conditionIndex, timeoutMs) );
	}

	protected boolean isInterrupted()
	{
		return( currentThread().isInterrupted() );
	}

	protected <CC, RR> RR getIfNotNull( CC obj, Function<CC,RR> getter )
	{
		return( NullFunctions.instance().getIfNotNull(obj, getter) );
	}

	protected <CC, RR> RR getIfNotNull( CC obj, Function<CC,RR> getter, RR defaultValue )
	{
		return( NullFunctions.instance().getIfNotNull(obj, getter, defaultValue) );
	}

	public Exception safeMethodExecution( ExecutionFunctions.UnsafeMethod run )
	{
		return( ExecutionFunctions.instance().safeMethodExecution(run) );
	}

	public <CC> CC safeFunctionExecution( ExecutionFunctions.UnsafeFunction<CC> run )
	{
		return( ExecutionFunctions.instance().safeFunctionExecution(run) );
	}
}
