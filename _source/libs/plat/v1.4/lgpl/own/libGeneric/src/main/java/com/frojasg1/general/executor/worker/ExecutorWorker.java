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

import com.frojasg1.general.executor.ExecutorInterface;
import com.frojasg1.general.executor.ExecutorPullInterface;

/**
 *
 * @author axpe
 */
public class ExecutorWorker extends Thread
{
	protected ExecutorPullInterface _pull = null;

	ExecutorInterface _executor = null;
	
	protected boolean _stop = false;

	public ExecutorWorker( ExecutorPullInterface pull, String name )
	{
		super(name);
		_pull = pull;
	}

	public ExecutorWorker( ExecutorPullInterface pull )
	{
		_pull = pull;
	}

	public void hasToStop()
	{
		_stop = true;

		stopExecutor();
	}

	public boolean isStopped()
	{
		return( !isAlive() );
	}

	@Override
	public void run()
	{
		_stop = false;

		while( !_stop )
		{
			setExecutor( _pull.getNextExecutor(this) );

			try
			{
				if( _executor != null )
					_executor.execute();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}

			_stop = stopAfterExecution( _executor );

			releaseExecutor();
		}
	}

	protected void releaseExecutor()
	{
		_pull.stoppingToExecute(this, _executor);
		setExecutor(null);
	}

	protected boolean stopAfterExecution( ExecutorInterface executor )
	{
		return( _executor == null );
	}

	protected synchronized void setExecutor( ExecutorInterface executor )
	{
		_executor = executor;
	}

	public synchronized void stopExecutor()
	{
		ExecutorInterface executor = _executor;
		if( executor != null )
			executor.hasToStop();
	}
}
