
package com.frojasg1.general.threads.runnablewithlock;

import com.frojasg1.general.threads.RunnableBase;

/**
 *
 * @author fjavier.rojas
 */
public abstract class RunnableWithLockAndSimpleConditionBase extends RunnableBase
{
//	protected ReentrantLockWithOneCondition _lock;

	public RunnableWithLockAndSimpleConditionBase( int numExtraConditions )
	{
		super( numExtraConditions );
	}

	public RunnableWithLockAndSimpleConditionBase( RunnableWithLockAndSimpleConditionBase that )
	{
		super( that );
	}
/*
	protected RunnableWithLockAndSimpleConditionBase(boolean init)
	{
		if( init )
			init();
	}

	protected void init()
	{
		_lock = createLock();
	}

	protected ReentrantLockWithOneCondition createLock()
	{
		return( new ReentrantLockWithOneCondition() );
	}

	public void lockProcedure( Runnable procedure )
	{
		_lock.lockProcedure(procedure);
	}

	public <CC> CC lockFunction( Supplier<CC> function )
	{
		return( _lock.lockFunction( function ) );
	}

	public boolean awaitInterrupted( Integer timeoutMs )
	{
		return( _lock.awaitInterrupted( timeoutMs ) );
	}

	public boolean hasWaiters( )
	{
		return( _lock.hasWaiters() );
	}

	public void signalCondition()
	{
		_lock.signalCondition();
	}
*/
}
