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
package com.frojasg1.general.executor.worker;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.executor.ExecutorInterface;
import com.frojasg1.general.executor.ExecutorPullInterface;
import com.frojasg1.general.executor.imp.FunctionWithCallbackExecutor;
import com.frojasg1.general.threads.ReentrantLockWithOneCondition;
import com.frojasg1.general.threads.RunnableBase;
import com.frojasg1.general.threads.ThreadFunctions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PullOfExecutorWorkers implements ExecutorPullInterface
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PullOfExecutorWorkers.class);

	protected ArrayList<ExecutorWorker> _listOfWorkers = new ArrayList<ExecutorWorker>();

	protected volatile int _capacity = -1;

	protected ReentrantLockWithOneCondition _lockForExecutorToBeProcessed = new ReentrantLockWithOneCondition();
//	protected Condition _waitForNewExecutorsBeingAvailable = _lockForExecutorToBeProcessed.newCondition();

	protected LinkedList<ExecutorInterface> _listOfExecutorsPendingToBeProcessed = new LinkedList<ExecutorInterface>();

	protected volatile boolean _hasEnded = false;

	protected volatile boolean _isPaused = false;

	protected volatile boolean _isActivated = true;

	protected String _name = null;

	protected Map<ExecutorInterface, ExecutorWorker> _map = new ConcurrentHashMap<>();

	public PullOfExecutorWorkers( String name )
	{
		_name = name;
	}

	public void init( int capacity )
	{
		setNumberOfThreads( capacity );
	}

	@Override
	public int getCapacity() {
		return _capacity;
	}

	public boolean isPaused()
	{
		return( _isPaused );
	}

	public void setIsPaused( boolean value )
	{
		_lockForExecutorToBeProcessed.lockProcedure( () -> setIsPausedInternal(value) );
	}

	public void setIsPausedInternal( boolean value )
	{
		_isPaused = value;
		if( !_isPaused && _lockForExecutorToBeProcessed.hasWaiters() )
			_lockForExecutorToBeProcessed.signalCondition();
	}

	public boolean isActivated()
	{
		return( _isActivated );
	}

	public void setIsActivated( boolean value )
	{
		_isActivated = value;

		if( ! _isActivated )
			clearListOfPendingTasks();
	}

	public void clearListOfPendingTasks()
	{
		_lockForExecutorToBeProcessed.lockProcedure( this::clearListOfPendingTasksInternal );
	}

	public void clearListOfPendingTasksInternal()
	{
		_listOfExecutorsPendingToBeProcessed.clear();
	}

	protected ExecutorWorker createExecutor()
	{
		ExecutorWorker result = null;
		
		if( _name != null )
			result = new ExecutorWorker( this, _name );
		else
			result = new ExecutorWorker( this );
			
		return( result );
	}

	public void setNumberOfThreads( int capacity )
	{
		lockProcedure( () -> setNumberOfThreadsInternal( capacity ) );
	}

	protected synchronized void setNumberOfThreadsInternal( int capacity )
	{
		_capacity = capacity;

		if( _capacity < _listOfWorkers.size() )
		{
			List<ExecutorWorker> list = _listOfWorkers.stream().limit( _listOfWorkers.size() - _capacity )
				.collect( Collectors.toList() );

			for( ExecutorWorker worker: list )
			{
				worker.hasToStop();
				_listOfWorkers.remove(worker);
			}
		}
		else
		{
			while( _listOfWorkers.size() < _capacity )
				_listOfWorkers.add( createExecutor() );
		}
	}

	@Override
	public ExecutorInterface getNextExecutor(ExecutorWorker executor)
	{
		return( _lockForExecutorToBeProcessed.lockFunction( () -> getNextExecutorInternal( executor ) ) );
	}

	protected ExecutorInterface getNextExecutorInternal(ExecutorWorker executor)
	{
		ExecutorInterface result = null;

		while( isPaused() || ( ! getHasEnded() && _listOfExecutorsPendingToBeProcessed.isEmpty() ) )
		{
			_lockForExecutorToBeProcessed.awaitInterrupted( 1000 );

			if( getHasEnded() )
				break;
		}
		if( ! getHasEnded() )
			result = _listOfExecutorsPendingToBeProcessed.removeLast();

		if( result != null )
			addExecutingRunnable(result, executor);

		return( result );
	}

	protected synchronized void removeExistingExecution( ExecutorWorker executor )
	{
		Iterator<Map.Entry<ExecutorInterface, ExecutorWorker>> it = _map.entrySet().iterator();
		while( it.hasNext() )
		{
			Map.Entry<ExecutorInterface, ExecutorWorker> pair = it.next();
			if( pair.getValue() == executor )
			{
				it.remove();
				break;
			}
		}
	}

	protected synchronized void addExecutingRunnable( ExecutorInterface result, ExecutorWorker executor )
	{
		removeExistingExecution(executor);
		if( ( result != null ) && ( executor != null ) )
			_map.put(result, executor);
	}

	@Override
	public void addPendingNonStopableExecutor( Runnable executor )
	{
		addPendingExecutor( new ExecutorInterface() {
			@Override
			public void hasToStop() {}

			@Override
			public void run()
			{
				executor.run();
			}
		});
	}

	@Override
	public void addPendingExecutor( ExecutorInterface executor )
	{
		_lockForExecutorToBeProcessed.lockProcedure( () -> addPendingExecutorInternal( executor ) );
	}

	protected void addPendingExecutorInternal( ExecutorInterface executor )
	{
		if( !isActivated() )
			return;

		_listOfExecutorsPendingToBeProcessed.addFirst(executor);

		if( _lockForExecutorToBeProcessed.hasWaiters() )
			_lockForExecutorToBeProcessed.signalCondition();
	}

	public boolean getHasEnded()
	{
		return( _hasEnded );
	}

	@Override
	public boolean isFinished()
	{
		return( lockFunction( this::isFinishedInternal ) );
	}

	protected synchronized boolean isFinishedInternal()
	{
		boolean result = true;

		Iterator<ExecutorWorker> it = _listOfWorkers.iterator();

		while( result && it.hasNext() )
			result = !it.next().isAlive();

		return( result );
	}

	public void setHasToEnd()
	{
		_lockForExecutorToBeProcessed.lockProcedure( this::setHasToEndInternal );
	}

	protected void setHasToEndInternal()
	{
		while( _lockForExecutorToBeProcessed.hasWaiters() )
			_lockForExecutorToBeProcessed.signalCondition();
	}

	@Override
	public void stopExecutors()
	{
		lockProcedure( this::stopExecutorsInternal );
	}

	public synchronized void stopExecutorsInternal()
	{
		for( ExecutorWorker worker: _listOfWorkers )
			worker.stopExecutor();
	}

	@Override
	public void hasToStop()
	{
		lockProcedure( this::hasToStopInternal );
	}

	protected synchronized void hasToStopInternal()
	{
		Iterator<ExecutorWorker> it = _listOfWorkers.iterator();

		_hasEnded = true;

		while( it.hasNext() )
			it.next().hasToStop();

		setHasToEnd();
	}

	@Override
	public void start()
	{
		lockProcedure( this::startInternal );
	}

	protected void inconditionalStop( ExecutorInterface runnable )
	{
		lockProcedure( () -> inconditionalStopInternal( runnable ) );
	}

	protected synchronized void removeWorker( ExecutorWorker worker )
	{
		
	}

	protected boolean inconditionalStopInternal( ExecutorInterface runnable )
	{
		boolean hasStopped = false;
		ExecutorWorker worker;
		if( ( worker = getExecutorWorker( runnable ) ) != null )
		{
			runnable.hasToStop();
			worker.interrupt();
			hasStopped = hasStoppedOrWait( runnable );
			if( !hasStopped )
			{
				worker.stop();
				removeWorker( worker );
				updateExecutors();
				hasStopped = true;

				if( runnable instanceof RunnableBase )
					((RunnableBase) runnable).doEnd();
			}
		}
		else
		{
			LOGGER.warn( "Asked to stop non running task" );
		}

		return( hasStopped );
	}

	protected boolean hasStoppedOrWait( ExecutorInterface runnable )
	{
		boolean result = false;
		long start = System.currentTimeMillis();
		final long MAX_TIME_TO_WAIT = 1000;
		while( !(result=!isBeingExecuted(runnable)) && ( ( System.currentTimeMillis() - start ) < MAX_TIME_TO_WAIT ) )
			ThreadFunctions.instance().sleep(40);

		return( result );
	}

	protected void updateExecutors()
	{
		setNumberOfThreadsInternal( getCapacity() );
	}

	protected void startInternal()
	{
		Iterator<ExecutorWorker> it = _listOfWorkers.iterator();

		while( it.hasNext() )
		{
			ExecutorWorker worker = it.next();
			if( !worker.isAlive() )
				worker.start();
		}
	}

	public synchronized void cancelThreadsNotStopped()
	{
		Iterator<ExecutorWorker> it = _listOfWorkers.iterator();
		
		while( it.hasNext() )
		{
			ExecutorWorker worker = it.next();
			if( worker.isAlive() )
				worker.stop();
			it.remove();
		}
	}

	public <RR> void addPendingExecutor( ExecutionFunctions.UnsafeFunction<RR> function,
										Consumer<RR> callback )
	{
		ExecutorInterface executor = new FunctionWithCallbackExecutor( function, callback );
		addPendingExecutor( executor );
	}

	@Override
	public void stoppingToExecute( ExecutorWorker executor, ExecutorInterface runnable )
	{
		lockProcedure( () -> stoppingToExecuteInternal( executor, runnable ) );
	}

	protected synchronized void stoppingToExecuteInternal( ExecutorWorker executor, ExecutorInterface runnable )
	{
		if( runnable != null )
			_map.remove(runnable);
	}

	public void lockProcedure( Runnable procedure )
	{
		_lockForExecutorToBeProcessed.lockProcedure(procedure);
	}

	public <CC> CC lockFunction( Supplier<CC> function )
	{
		return( _lockForExecutorToBeProcessed.lockFunction( function ) );
	}

	@Override
	public boolean isBeingExecuted( ExecutorInterface runnable )
	{
		return( getExecutorWorker( runnable ) != null );
	}

	public ExecutorWorker getExecutorWorker( ExecutorInterface runnable )
	{
		return( lockFunction( () -> getExecutorWorkerInternal( runnable ) ) );
	}

	protected synchronized ExecutorWorker getExecutorWorkerInternal( ExecutorInterface runnable )
	{
		ExecutorWorker result = null;
		if( runnable != null )
			result = _map.get( runnable );

		return( result );
	}

	@Override
	public boolean isPending( ExecutorInterface runnable )
	{
		return( lockFunction( () -> isPendingInternal( runnable ) ) );
	}

	protected boolean isPendingInternal( ExecutorInterface runnable )
	{
		return( _listOfExecutorsPendingToBeProcessed.contains( runnable ) );
	}

	@Override
	public ExecutorInterface replace(ExecutorInterface runnableToBeReplaced, Supplier<ExecutorInterface> replacementRunnableBuilder)
	{
		ExecutorInterface result = runnableToBeReplaced;
		if( inconditionalStopInternal(runnableToBeReplaced) )
		{
			result = replacementRunnableBuilder.get();
			addPendingExecutor(result);
		}

		return( result );
	}
}
