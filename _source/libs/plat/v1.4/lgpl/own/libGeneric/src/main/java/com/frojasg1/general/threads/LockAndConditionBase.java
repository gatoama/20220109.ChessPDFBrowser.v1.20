/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.threads;

import com.frojasg1.general.NullFunctions;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class LockAndConditionBase {

	protected ReentrantLock _lock;
	protected Condition[] _conditions;

	protected int _numConditions;

	public LockAndConditionBase( int numConditions )
	{
		this( numConditions, true );
	}

	public LockAndConditionBase( LockAndConditionBase that )
	{
		this( that.getNumConditions() );
	}

	protected LockAndConditionBase( LockAndConditionBase that, boolean init )
	{
		this( that.getNumConditions(), init );
	}

	protected LockAndConditionBase( int numConditions, boolean init )
	{
		_numConditions = numConditions;
		if( init )
			init();
	}

	protected void init()
	{
		_lock = createLock();
		_conditions = createConditions();
	}

	protected ReentrantLock createLock()
	{
		return( new ReentrantLock(true) );
	}

	protected Condition[] createConditions()
	{
		Condition[] result = new Condition[getNumConditions()];

		for( int ii=0; ii<result.length; ii++ )
			result[ii] = createCondition();

		return( result );
	}

	protected Condition createCondition()
	{
		return( getLock().newCondition() );
	}

	public ReentrantLock getLock() {
		return _lock;
	}

	protected boolean conditionHasWaiters( int conditionIndex ) {
		return getLock().hasWaiters( getCondition( conditionIndex ) );
	}

	public Condition[] getConditions() {
		return _conditions;
	}

	public int getNumConditions() {
		return _numConditions;
	}

	public Condition getCondition( int index )
	{
		return( getConditions()[index] );
	}

	public <CC> CC lockFunction( Supplier<CC> function )
	{
		return( LockFunctions.instance().lockFunction(getLock(), function) );
	}

	public void lockProcedure( Runnable procedure )
	{
		LockFunctions.instance().lockProcedure(getLock(), procedure);
	}

	public boolean awaitInterrupted( int conditionIndex, Integer timeoutMs )
	{
		return( LockFunctions.instance().awaitInterrupted(
							getCondition(conditionIndex), timeoutMs) );
	}

	public void ifHasWaitersSignal( int conditionIndex )
	{
		if( hasWaiters( conditionIndex ) )
			signalCondition( conditionIndex );
	}

	public boolean hasWaiters( int conditionIndex )
	{
		return( getLock().hasWaiters( getCondition(conditionIndex) ) );
	}

	public void signalCondition( int conditionIndex )
	{
		getCondition(conditionIndex).signal();
	}

	protected void runAndSignalInternal( Runnable runnable, int conditionIndex )
	{
		if( runnable != null )
			runnable.run();

		ifHasWaitersSignal( conditionIndex );
	}

	public void taskToSignalGen( Runnable runnable,
								int conditionIndex)
	{
		lockProcedure( () -> runAndSignalInternal( runnable, conditionIndex ) );
	}

	protected <CC, RR> RR getIfNotNull( CC obj, Function<CC, RR> getter )
	{
		return( NullFunctions.instance().getIfNotNull(obj, getter) );
	}
}
