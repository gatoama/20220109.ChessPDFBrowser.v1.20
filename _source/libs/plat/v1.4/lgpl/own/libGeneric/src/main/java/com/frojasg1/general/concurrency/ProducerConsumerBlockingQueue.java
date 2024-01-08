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
package com.frojasg1.general.concurrency;

import com.frojasg1.general.ExecutionFunctions;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ProducerConsumerBlockingQueue<CC>
{
	protected ReentrantLock _lock = new ReentrantLock( true );
	protected Condition _waitingForElementToBeProduced = _lock.newCondition();

	protected LinkedList<CC> _linkedList;

	protected boolean _hasToStop = false;
	protected Supplier<Boolean> _hasToStopSupplier = null;

	public ProducerConsumerBlockingQueue( Supplier<Boolean> hasToStopSupplier )
	{
		_hasToStopSupplier = hasToStopSupplier;
		_linkedList = new LinkedList<>();
	}

	public CC get()
	{
		try
		{
			_lock.lock();

			while( _linkedList.isEmpty() && !hasToStop() )
				ExecutionFunctions.instance().safeMethodExecution( () -> _waitingForElementToBeProduced.await( 500, TimeUnit.MILLISECONDS ) );

			CC result = null;
			if( !_linkedList.isEmpty() )
				result = _linkedList.removeFirst();

			return( result );
		}
		finally
		{
			_lock.unlock();
		}
	}

	public void add( CC elem )
	{
		try
		{
			_lock.lock();

			if( ! hasToStop() )
			{
				_linkedList.add( elem );

				if( _lock.hasWaiters(_waitingForElementToBeProduced) )
					_waitingForElementToBeProduced.signal();
			}
		}
		finally
		{
			_lock.unlock();
		}
	}

	protected boolean hasToStop()
	{
		return( _hasToStop || ( _hasToStopSupplier != null ) && _hasToStopSupplier.get() );
	}

	public void stop()
	{
		try
		{
			_lock.lock();

			_hasToStop = true;

			if( _lock.hasWaiters(_waitingForElementToBeProduced) )
				_waitingForElementToBeProduced.signalAll();
		}
		finally
		{
			_lock.unlock();
		}
	}
}
