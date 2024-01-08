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
package com.frojasg1.general.threads;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class LockFunctions
{
	protected static class LazyHolder
	{
		protected static final LockFunctions INSTANCE = new LockFunctions();
	}

	public static LockFunctions instance()
	{
		return( LazyHolder.INSTANCE );
	}


	protected <CC> CC lockFunction( Lock lock, Supplier<CC> function )
	{
		try
		{
			if( lock != null )
				lock.lock();

			return( function.get() );
		}
		finally
		{
			if( lock != null )
				lock.unlock();
		}
	}

	protected void lockProcedure( Lock lock, Runnable procedure )
	{
		try
		{
			if( lock != null )
				lock.lock();

			procedure.run();
		}
		finally
		{
			if( lock != null )
				lock.unlock();
		}
	}

	protected boolean awaitInterrupted( Condition condition, Integer timeoutMs )
	{
		boolean result = true;
		try
		{
			if( timeoutMs != null )
				result = condition.await( timeoutMs, TimeUnit.MILLISECONDS );
			else
				condition.await();
		}
		catch( InterruptedException ex )
		{
			Thread.currentThread().interrupt();
			result = false;
		}
		return( result );
	}
}
